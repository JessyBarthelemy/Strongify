package com.jessy_barthelemy.strongify.fragment;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.adapter.exerciseSet.ExerciseSetListAdapter;
import com.jessy_barthelemy.strongify.adapter.workout.SimpleItemTouchHelperCallback;
import com.jessy_barthelemy.strongify.customLayout.TutorialLayout;
import com.jessy_barthelemy.strongify.database.asyncTask.UpdateExerciseExecutionsTask;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.enumeration.ExerciseType;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.interfaces.OnExerciseChanged;

public class ExerciseSetFragment extends BaseFragment implements OnExerciseChanged{

    private String titleTransitionName;
    private ExerciseWithSet exerciseWithSet;
    private OnExerciseChanged listener;
    private int position;
    private TextView noData;

    private boolean isDeleteOnlyNew;
    private boolean isFromExecution;

    public ExerciseSetFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_exercise_set, container, false);
        TextView title = root.findViewById(R.id.exercise_set_title);

        if(exerciseWithSet != null)
            title.setText(ApplicationHelper.getExerciseTitle(getActivity(), exerciseWithSet.getExercise().title, exerciseWithSet.getExercise().base));
        if (titleTransitionName != null)
            title.setTransitionName(titleTransitionName);

        final RecyclerView exerciseSetView = root.findViewById(R.id.exercise_set_list);

        int exerciseType = exerciseWithSet.getSuperset() != null ? exerciseWithSet.getSuperset().exerciseType : ExerciseType.NORMAL;

        final ExerciseSetListAdapter adapter = new ExerciseSetListAdapter(getActivity() , exerciseWithSet.sets, exerciseType, this);
        adapter.setWeightUnitId(ApplicationHelper.getWeightIcon(getActivity()));
        adapter.setDeleteOnlyNew(isDeleteOnlyNew);
        exerciseSetView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        exerciseSetView.setLayoutManager(layoutManager);
        exerciseSetView.setHasFixedSize(true);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, exerciseSetView);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(exerciseSetView);

        final FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            adapter.cloneLastItem(exerciseWithSet.workoutExecution.executionId);
            int itemCount = adapter.getItemCount();
            if(itemCount > 0)
                exerciseSetView.smoothScrollToPosition(itemCount - 1);
        });

        if(exerciseType != ExerciseType.NORMAL)
            fab.setVisibility(View.GONE);

        noData = root.findViewById(R.id.exercise_set_no_data);
        if(exerciseWithSet.sets.isEmpty())
            noData.setVisibility(View.VISIBLE);

        final TutorialLayout tutorial = root.findViewById(R.id.tutorial);

        if(ApplicationHelper.hasToShowTutorial(getActivity(), ApplicationHelper.TUTORIAL_SET)){
            tutorial.setVisibility(View.VISIBLE);

            root.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            int[] location = new int[2];
                            location[0] = fab.getLeft() + (fab.getWidth() / 2);
                            location[1] = fab.getTop() + (fab.getWidth() / 2);

                            tutorial.setRadius(fab.getWidth());
                            tutorial.setPoint(location);
                            root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });

            tutorial.setOnClickListener(v -> {
                tutorial.setVisibility(View.GONE);
                ApplicationHelper.setTutorial(getActivity(), ApplicationHelper.TUTORIAL_SET);
            });
        }

        ImageView backAction = root.findViewById(R.id.back_action);
        backAction.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        Button close = root.findViewById(R.id.ok);

        if(isFromExecution){
            close.setVisibility(View.VISIBLE);
            close.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }

        return root;
    }

    public void setTitleTransitionName(String titleTransitionName){
        this.titleTransitionName = titleTransitionName;
    }

    public void setExerciseWithSet(ExerciseWithSet exerciseWithSet){
        this.exerciseWithSet = exerciseWithSet;
    }

    public void setListener(OnExerciseChanged listener){
        this.listener = listener;
    }

    public void setPosition(int position){
        this.position = position;
    }


    @Override
    public void onStop() {
        super.onStop();
        if(isFromExecution){
            listener.onExerciseAdded(exerciseWithSet, 0);
        }
        else{
            new UpdateExerciseExecutionsTask(getActivity(), exerciseWithSet, listener, position).execute();
        }
    }

    @Override
    public void onExerciseAdded(ExerciseWithSet exercise, int position) {
        if(exerciseWithSet.sets.size() > 0)
            noData.setVisibility(View.GONE);
    }

    @Override
    public void onExerciseRemoved() {
        if(exerciseWithSet.sets.size() == 0)
            noData.setVisibility(View.VISIBLE);
    }

    public void setIsFromExecution(boolean isFromExecution){
        this.isFromExecution = isFromExecution;
    }

    public void setDeleteOnlyNew(boolean isDeleteOnlyNew) {
        this.isDeleteOnlyNew = isDeleteOnlyNew;
    }
}

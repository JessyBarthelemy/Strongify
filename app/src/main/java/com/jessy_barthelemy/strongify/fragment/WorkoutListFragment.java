package com.jessy_barthelemy.strongify.fragment;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.adapter.workout.SimpleItemTouchHelperCallback;
import com.jessy_barthelemy.strongify.adapter.workout.WorkoutListAdapter;
import com.jessy_barthelemy.strongify.adapter.workout.WorkoutViewHolder;
import com.jessy_barthelemy.strongify.customLayout.TutorialLayout;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.asyncTask.ImportWorkoutTask;
import com.jessy_barthelemy.strongify.database.asyncTask.UpdateWorkoutsTask;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;
import com.jessy_barthelemy.strongify.helper.AnimationHelper;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.RevealAnimationSettings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkoutListFragment extends BaseFragment {

    public static final int IMPORT_WORKOUT = 1;

    private List<WorkoutWithExercise> workouts;
    //used to detect remove event and avoid updating the list due to the snackbar delay
    private int workoutCount;
    private TextView noData;

    private FloatingActionButton fab;
    private View view;
    private TutorialLayout tutorialPlay;
    private RecyclerView workoutView;
    private WeakReference<ViewGroup> container;

    public WorkoutListFragment() {}

    public static WorkoutListFragment newInstance() {
       return new WorkoutListFragment();
   }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.container = new WeakReference<>(container);
        view = inflater.inflate(R.layout.fragment_workout_list, container, false);

        if(getActivity() != null){
            workoutView = view.findViewById(R.id.workout_list);
            workouts = new ArrayList<>();
            final WorkoutListAdapter adapter = new WorkoutListAdapter(getActivity() ,workouts);
            workoutView.setAdapter(adapter);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
            workoutView.setLayoutManager(layoutManager);
//            workoutView.setHasFixedSize(true);

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, workoutView);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(workoutView);

            fab = view.findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RevealAnimationSettings settings = new RevealAnimationSettings();

                    settings.centerX = (int)(fab.getX() + fab.getWidth() / 2);
                    settings.centerY = (int)(fab.getY() + fab.getHeight() / 2);
                    settings.width = view.getWidth();
                    settings.height = view.getHeight();

                    WorkoutManagementFragment fragment = WorkoutManagementFragment.newInstance();
                    fragment.setOrder(workouts.size() -1);
                    fragment.setSettings(settings);
                    ((HomeActivity)getActivity()).openFragment(fragment);
                }
            });

            noData = view.findViewById(R.id.workout_no_data);

            final ObjectAnimator scaleDown = AnimationHelper.getScaleDownAnimation(fab);

            AppDatabase.getAppDatabase(getActivity()).workoutDao().getAllAsync().observe(getActivity(), new Observer<List<WorkoutWithExercise>>() {
                @Override
                public void onChanged(@Nullable List<WorkoutWithExercise> workoutWithExercises) {
                    //used to detect remove event and avoid updating the list due to the snackbar delay
                    if(workoutCount == 0 || (workoutWithExercises != null && workoutCount <=  workoutWithExercises.size()))
                    {
                        workouts = workoutWithExercises;
                        if(workouts != null){
                            for(WorkoutWithExercise workout : workouts){
                                Collections.sort(workout.workoutExecution);
                            }
                            adapter.setDataSet(workoutWithExercises);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    if(workoutWithExercises != null)
                        workoutCount = workoutWithExercises.size();

                    if(workoutWithExercises != null && workoutWithExercises.size() > 0) {
                        noData.setVisibility(View.GONE);
                        scaleDown.cancel();
                    }
                    else{
                        noData.setVisibility(View.VISIBLE);
                        scaleDown.start();
                    }

                    if(workouts != null && workouts.size() > 0) {
                        tutorialPlay = view.findViewById(R.id.tutorial_play);

                        if (ApplicationHelper.hasToShowTutorial(getActivity(), ApplicationHelper.TUTORIAL_PLAY))
                            showTutorial();
                    }
                }
            });

            final ImageView menu = view.findViewById(R.id.menu);
            menu.setVisibility(View.VISIBLE);
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(getActivity(), menu);
                    popup.getMenuInflater().inflate(R.menu.import_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.import_workout) {
                                if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
                                    intent.setType("text/plain");
                                    startActivityForResult(intent, IMPORT_WORKOUT);
                                }else{
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent, getString(R.string.workout_import_file)), 1);
                                }
                            } else if(id == R.id.tutorial_replay) {
                                ApplicationHelper.resetTutorial(getActivity());
                                showTutorial();
                            }

                            return true;
                        }
                    });

                    popup.show();
                }
                });
        }

        return view;
    }

    private void showTutorial(){
        if(tutorialPlay == null)
            return;

        tutorialPlay.setVisibility(View.VISIBLE);

        tutorialPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorialPlay.setVisibility(View.GONE);
                ApplicationHelper.setTutorial(getActivity(), ApplicationHelper.TUTORIAL_PLAY);
            }
        });


        workoutView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RecyclerView.ViewHolder holder = workoutView.findViewHolderForLayoutPosition(0);

                if(holder != null){
                    int[] location = new int[2];
                    int width = ((WorkoutViewHolder) holder).playWorkout.getWidth();
                    ((WorkoutViewHolder) holder).playWorkout.getLocationOnScreen(location);
                    location[0] += width / 2;
                    if(container.get() != null)
                        location[1] -= container.get().getTop();

                    tutorialPlay.setRadius(fab.getWidth());
                    tutorialPlay.setPoint(location);
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case IMPORT_WORKOUT:
                if (getContext() != null && data != null)
                    new ImportWorkoutTask(getActivity(), data.getData()).execute();
                else
                    Toast.makeText(getActivity(), R.string.workout_import_error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        new UpdateWorkoutsTask(getActivity(), workouts).execute();
    }
}
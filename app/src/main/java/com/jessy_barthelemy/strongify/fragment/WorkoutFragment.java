package com.jessy_barthelemy.strongify.fragment;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.adapter.exercise.ExerciseListAdapter;
import com.jessy_barthelemy.strongify.adapter.workout.SimpleItemTouchHelperCallback;
import com.jessy_barthelemy.strongify.customLayout.TutorialLayout;
import com.jessy_barthelemy.strongify.database.asyncTask.AddExercisesExecutions;
import com.jessy_barthelemy.strongify.database.asyncTask.UpdateCustomExerciseSetTask;
import com.jessy_barthelemy.strongify.database.asyncTask.UpdateExercisesExecutionsTask;
import com.jessy_barthelemy.strongify.database.entity.Superset;
import com.jessy_barthelemy.strongify.database.entity.Workout;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithGroup;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;
import com.jessy_barthelemy.strongify.enumeration.ExerciseType;
import com.jessy_barthelemy.strongify.helper.AnimationHelper;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.RevealAnimationSettings;
import com.jessy_barthelemy.strongify.interfaces.CustomExerciseManager;
import com.jessy_barthelemy.strongify.interfaces.CustomExerciseSetManager;
import com.jessy_barthelemy.strongify.interfaces.OnExerciseChanged;
import com.jessy_barthelemy.strongify.interfaces.OnExerciseExecutionAdded;
import com.jessy_barthelemy.strongify.interfaces.OnFragmentInteractionListener;
import com.jessy_barthelemy.strongify.interfaces.WorkoutManager;

import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WorkoutFragment extends BaseFragment implements OnFragmentInteractionListener,
                                                         OnExerciseExecutionAdded,
                                                         OnExerciseChanged,
                                                         CustomExerciseManager,
                                                         CustomExerciseSetManager,
                                                         WorkoutManager{
    private WorkoutWithExercise workout;
    private String titleTransitionName;
    private String periodicityTransitionName;
    private LinearLayout fabOptions;
    private ExerciseListAdapter adapter;
    private TextView title;
    private TextView periodicity;
    private TextView noData;
    private FloatingActionButton fab;
    private ObjectAnimator scaleDown;

    public WorkoutFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root =  inflater.inflate(R.layout.fragment_workout, container, false);
        title = root.findViewById(R.id.workout_title);
        periodicity = root.findViewById(R.id.workout_periodicity);

        if(getActivity() != null){
            title.setText(workout.workout.name);
            if(workout.workout.periodicity != null && workout.workout.periodicity.size() > 0){
                periodicity.setVisibility(View.VISIBLE);
                periodicity.setText(ApplicationHelper.getPeriodicityText(getActivity(), workout.workout.periodicity));
            }

            final RecyclerView exerciseView = root.findViewById(R.id.exercise_list);
            adapter = new ExerciseListAdapter(getActivity() ,workout.workoutExecution, this, this);
            adapter.setWeightUnityId(ApplicationHelper.getWeightIcon(getActivity()));
            exerciseView.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
            exerciseView.setLayoutManager(layoutManager);
//            exerciseView.setHasFixedSize(true);

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, exerciseView);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(exerciseView);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                title.setTransitionName(titleTransitionName);
                periodicity.setTransitionName(periodicityTransitionName);

                setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move));
                setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
            }

            final Animation slideDown = AnimationUtils.loadAnimation(this.getActivity(), R.anim.slide_down);
            final Animation slideUp = AnimationUtils.loadAnimation(this.getActivity(), R.anim.slide_up);
            final Animation fadeIn = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fade_in);
            final Animation fadeOut = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fade_out);

            fab = root.findViewById(R.id.fab);
            final FloatingActionButton fabExercise = root.findViewById(R.id.add_exercise);
            final FloatingActionButton fabSuperset = root.findViewById(R.id.add_superset);
            final FloatingActionButton fabCircuit = root.findViewById(R.id.add_circuit);

            final LinearLayout clickDetector = root.findViewById(R.id.click_detector);
            fabOptions = root.findViewById(R.id.fab_options);

            slideDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    fabOptions.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            clickDetector.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.performClick();
                    if(fabOptions.getVisibility() == View.VISIBLE)
                        closeClickDetector(clickDetector, slideDown, fadeOut);
                    return false;
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fabOptions.setVisibility(View.VISIBLE);
                    fabOptions.startAnimation(slideUp);
                    fab.setVisibility(View.GONE);

                    clickDetector.setVisibility(View.VISIBLE);
                    clickDetector.startAnimation(fadeIn);
                }
            });

            fabExercise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RevealAnimationSettings settings = new RevealAnimationSettings();

                    int[] locations = new int[2];
                    fabExercise.getLocationOnScreen(locations);
                    settings.centerX = (locations[0] + fabExercise.getWidth() / 2);
                    settings.centerY = (locations[1] + fabExercise.getHeight() / 2);
                    settings.width = root.getWidth();
                    settings.height = root.getHeight();

                    openExerciseFragment(settings, ExerciseType.NORMAL);
                }
            });

            TextView fabExerciseText = root.findViewById(R.id.add_exercise_text);
            fabExerciseText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openExerciseFragment(null, ExerciseType.NORMAL);
                }
            });

            //SUPERSET
            fabSuperset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RevealAnimationSettings settings = new RevealAnimationSettings();

                    int[] locations = new int[2];
                    fabSuperset.getLocationOnScreen(locations);
                    settings.centerX = (locations[0] + fabSuperset.getWidth() / 2);
                    settings.centerY = (locations[1] + fabSuperset.getHeight() / 2);
                    settings.width = root.getWidth();
                    settings.height = root.getHeight();

                    openExerciseFragment(settings, ExerciseType.SUPERSET);
                }
            });

            TextView fabSupersetText = root.findViewById(R.id.add_superset_text);
            fabSupersetText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openExerciseFragment(null, ExerciseType.SUPERSET);
                }
            });

            //CIRCUIT
            fabCircuit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RevealAnimationSettings settings = new RevealAnimationSettings();

                    int[] locations = new int[2];
                    fabCircuit.getLocationOnScreen(locations);
                    settings.centerX = (locations[0] + fabCircuit.getWidth() / 2);
                    settings.centerY = (locations[1] + fabCircuit.getHeight() / 2);
                    settings.width = root.getWidth();
                    settings.height = root.getHeight();

                    openExerciseFragment(settings, ExerciseType.CIRCUIT);
                }
            });

            TextView fabCircuitText = root.findViewById(R.id.add_circuit_text);
            fabCircuitText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openExerciseFragment(null, ExerciseType.CIRCUIT);
                }
            });

            final ImageView menu = root.findViewById(R.id.menu);
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(getActivity(), menu);
                    popup.getMenuInflater().inflate(R.menu.workout_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.update) {
                                WorkoutManagementFragment fragment = new WorkoutManagementFragment();
                                fragment.setWorkout(workout.workout);
                                fragment.setWorkoutManager(WorkoutFragment.this);
                                ((HomeActivity) getActivity()).openFragment(fragment);
                            } else if(id == R.id.export) {
                                FileWriter writer = null;
                                try {
                                    File export =  new File(getActivity().getExternalCacheDir(), "export.txt");
                                    writer = new FileWriter(export);
                                    writer.write(workout.toJSON().toString());

                                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                    intentShareFile.setType("text/plain");

                                    Uri exportUri = FileProvider.getUriForFile(getActivity(), "com.jessy_barthelemy.strongify.fileprovider", export);
                                    intentShareFile.putExtra(Intent.EXTRA_STREAM, exportUri);

                                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.workout_export_subject));
                                    startActivity(Intent.createChooser(intentShareFile, getString(R.string.workout_export)));
                                } catch (IOException | JSONException e) {
                                    Toast.makeText(getActivity(), R.string.error_set_count, Toast.LENGTH_LONG).show();
                                }finally {
                                    if(writer != null) {
                                        try {
                                            writer.close();
                                        } catch (IOException ignore) {}
                                    }
                                }
                            }

                            return true;
                        }
                    });

                    popup.show();
                }
            });

            ImageView backAction = root.findViewById(R.id.back_action);
            backAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }

        noData = root.findViewById(R.id.exercise_no_data);
        scaleDown = AnimationHelper.getScaleDownAnimation(fab);

        if(workout.workoutExecution.size() == 0){
            scaleDown.start();
            noData.setVisibility(View.VISIBLE);
        }

        final TutorialLayout tutorial = root.findViewById(R.id.tutorial);

        if(ApplicationHelper.hasToShowTutorial(getActivity(), ApplicationHelper.TUTORIAL_WORKOUT)){
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

            tutorial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tutorial.setVisibility(View.GONE);
                    ApplicationHelper.setTutorial(getActivity(), ApplicationHelper.TUTORIAL_WORKOUT);
                }
            });
        }

        return root;
    }

    public WorkoutWithExercise getWorkout() {
        return workout;
    }

    public void setWorkout(WorkoutWithExercise workout) {
        this.workout = workout;
    }

    public void setTransitionName(String title, String periodicity){
        this.titleTransitionName = title;
        this.periodicityTransitionName = periodicity;
    }

    private void closeClickDetector(final View clickDetector, Animation slideDown, Animation fadeOut){
        clickDetector.setVisibility(View.INVISIBLE);
        clickDetector.startAnimation(fadeOut);

        this.fabOptions.startAnimation(slideDown);
    }

    @Override
    public void onFragmentInteraction(List<ExerciseWithGroup> exercises, int exerciseType, Superset superset) {
        int supersetOrder = -1;
        int lastOrder = -1;

        //If we are actually adding exercise to existing superset we search for the order and supersetOrder
        if (superset != null) {
            for(int i = adapter.getDataSet().size() -1; i >= 0; i--){
                if(adapter.getDataSet().get(i).getSuperset() != null
                        && adapter.getDataSet().get(i).getSuperset().supersetId == superset.supersetId
                        && supersetOrder == -1){
                    supersetOrder = adapter.getDataSet().get(i).workoutExecution.supersetOrder;
                    lastOrder = adapter.getDataSet().get(i).workoutExecution.order;
                }
            }
        }

        supersetOrder++;

        if(lastOrder == -1)
            lastOrder = workout.workoutExecution.size();
        new AddExercisesExecutions(getActivity(), exercises, workout.workout.id, exerciseType, lastOrder, this, superset, supersetOrder).execute();
    }

    private void openExerciseFragment(RevealAnimationSettings settings, int exerciseType){
        ExerciseListFragment fragment = new ExerciseListFragment();
        fragment.setListener(WorkoutFragment.this);
        fragment.setSettings(settings);
        fragment.setExerciseType(exerciseType);
        fragment.setPickerMode(true);
        if(getActivity() != null)
            ((HomeActivity)getActivity()).replaceFragment(fragment, null);
    }

    //Superset represents the superset on which we add the exercises
    @Override
    public void onExercisesAdded(List<ExerciseWithSet> exerciseWithSets, Superset superset) {
        if(exerciseWithSets != null && exerciseWithSets.size() > 0){
            if(superset != null){
                int insertionPosition = -1;

                for(int i = adapter.getItemCount() -1; i >= 0; i--){
                    if(adapter.getDataSet().get(i).getSuperset() != null
                            && adapter.getDataSet().get(i).getSuperset().supersetId == superset.supersetId
                            && insertionPosition == -1){
                        insertionPosition = i;
                    }
                }

                insertionPosition++;

                workout.workoutExecution.addAll(insertionPosition, exerciseWithSets);
            }else{
                workout.workoutExecution.addAll(exerciseWithSets);
            }

            adapter.notifyDataSetChanged();

            if(workout.workoutExecution.size() > 0){
                noData.setVisibility(View.GONE);
                scaleDown.cancel();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        new UpdateExercisesExecutionsTask(getActivity(), workout.workoutExecution).execute();
    }

    @Override
    public void onExerciseAdded(ExerciseWithSet exercise, int position) {
        adapter.setDataSetItem(exercise, position);
    }

    @Override
    public void onExerciseRemoved() {
        if(workout.workoutExecution.size() == 0){
            noData.setVisibility(View.VISIBLE);
            scaleDown.start();
        }
    }

    @Override
    public void onWorkoutChanged(Context context, Workout workout) {
        title.setText(workout.name);
        if(workout.periodicity != null && workout.periodicity.size() > 0){
            periodicity.setVisibility(View.VISIBLE);
            periodicity.setText(ApplicationHelper.getPeriodicityText(context, workout.periodicity));
        }
    }

    @Override
    public void onCustomExerciseChanged(Superset superset) {
        if(superset != null)
            new UpdateCustomExerciseSetTask(getActivity(), adapter.getDataSet(), superset, this).execute();
    }

    @Override
    public void onCustomExerciseSetChanged(List<ExerciseWithSet> exercise) {
        adapter.notifyDataSetChanged();
    }
}

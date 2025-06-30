package com.jessy_barthelemy.strongify.fragment.execution;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.adapter.customExecution.CustomExecutionListAdapter;
import com.jessy_barthelemy.strongify.adapter.setHistory.SetHistoryListAdapter;
import com.jessy_barthelemy.strongify.customLayout.Timer;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.entity.Superset;
import com.jessy_barthelemy.strongify.database.entity.WorkoutExecution;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithGroup;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;
import com.jessy_barthelemy.strongify.dialog.ExerciseSetManagerDialog;
import com.jessy_barthelemy.strongify.enumeration.ExerciseType;
import com.jessy_barthelemy.strongify.enumeration.FragmentType;
import com.jessy_barthelemy.strongify.fragment.ExerciseListFragment;
import com.jessy_barthelemy.strongify.fragment.ExerciseSetFragment;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.interfaces.OnExerciseChanged;
import com.jessy_barthelemy.strongify.interfaces.OnFragmentInteractionListener;
import com.jessy_barthelemy.strongify.interfaces.OnTimerFinishedListener;
import com.jessy_barthelemy.strongify.service.ExecutionService;

import java.util.ArrayList;
import java.util.List;

public class WorkoutExecutionRestFragment extends BaseExecutionFragment implements OnFragmentInteractionListener, OnExerciseChanged {

    private List<ExerciseWithSet> exercises;
    private Timer timer;
    private boolean openingNextExecution;
    private Handler handler;
    private Runnable runnable;
    private boolean isForeground;
    private boolean isTimerFinished;
    private ExerciseSetManagerDialog exerciseDialog;

    public static String ADD_EXERCISE = "AE";
    public static String ADD_SET = "AS";
    public static String CHANGE_EXERCISE = "CE";

    public WorkoutExecutionRestFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workout_execution_rest, container, false);

        RecyclerView setView = root.findViewById(R.id.set_list);
        int weightIconId = ApplicationHelper.getWeightIcon(getActivity());
        final SetHistoryListAdapter adapter = new SetHistoryListAdapter(getActivity(), exercises, weightIconId);
        setView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        setView.setLayoutManager(layoutManager);
        //setView.setHasFixedSize(true);

        timer = root.findViewById(R.id.timer);
        final ExerciseWithSet exerciseWithSet = workout.workoutExecution.get(workout.currentExercise);
        int rest = exerciseWithSet.sets.get(exerciseWithSet.currentSet).rest;

        if(workout.endTimer == 0){
            if(exerciseWithSet.getSuperset() != null && exerciseWithSet.getSuperset().exerciseType == ExerciseType.SUPERSET)
                rest = exerciseWithSet.getSuperset().rest;
            workout.endTimer = System.currentTimeMillis() + rest * 1000;
        }

        timer.setRest(workout.endTimer, rest);
        timer.setOnTimerFinishedListener(new OnTimerFinishedListener() {
            @Override
            public void onTimerFinished() {
                isTimerFinished = true;
                if(!isForeground){
                    if(exerciseDialog != null)
                        exerciseDialog.dismiss();
                    openNextExecution();
                }

            }
        });

        timer.start();

        int nextExercisePosition = -1;
        if(exerciseWithSet.getSuperset() != null){
            for(int i = workout.currentExercise, len = workout.workoutExecution.size(); i < len && nextExercisePosition == -1; i++){
                if((workout.workoutExecution.get(i).getSuperset() != null
                        && workout.workoutExecution.get(i).getSuperset().supersetId != exerciseWithSet.getSuperset().supersetId)
                        || workout.workoutExecution.get(i).getSuperset() == null)
                    nextExercisePosition = i;
            }
        }else if((workout.currentExercise + 1) < workout.workoutExecution.size())
            nextExercisePosition = this.workout.currentExercise +1;

        if(nextExercisePosition != -1){
            List<ExerciseWithSet> nextExercises = getExercises(nextExercisePosition);
            RecyclerView nextExercisesView = root.findViewById(R.id.next_exercises);
            CustomExecutionListAdapter newtExerciseAdapter = new CustomExecutionListAdapter(getActivity(), nextExercises, weightIconId);
            nextExercisesView.setAdapter(newtExerciseAdapter);

            RecyclerView.LayoutManager layoutManagerNextEx = new LinearLayoutManager(getActivity());
            nextExercisesView.setLayoutManager(layoutManagerNextEx);
            //nextExercisesView.setHasFixedSize(true);

            TextView nextExerciseSetCount = root.findViewById(R.id.next_exercise_set_count);
            int count = workout.workoutExecution.get(nextExercisePosition).sets.size();
            nextExerciseSetCount.setText(getResources().getQuantityString(R.plurals.next_exercise_set_count, count, count));
        }else{
            TextView nextExercisesTitle = root.findViewById(R.id.next_exercises_title);
            nextExercisesTitle.setVisibility(View.GONE);
        }

        TextView currentSet = root.findViewById(R.id.current_set);
        currentSet.setText(getString(R.string.current_set, exerciseWithSet.currentSet+1, exerciseWithSet.sets.size()));

        if(exerciseWithSet.currentSet == (exerciseWithSet.sets.size() -1))
            timer.setLastExecution(true);

        final FloatingActionButton manageExercises = root.findViewById(R.id.fab_exercises);

        manageExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exerciseDialog =  new ExerciseSetManagerDialog();
                exerciseDialog.setExercises(workout.workoutExecution, workout.currentExercise);
                exerciseDialog.setTargetFragment(WorkoutExecutionRestFragment.this, 1);
                if(getFragmentManager() != null)
                    exerciseDialog.show(getFragmentManager(), WorkoutExecutionRestFragment.class.getSimpleName());
            }
        });

        final FloatingActionButton noteButton = root.findViewById(R.id.fab);
        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.workout_note);

                LayoutInflater li = LayoutInflater.from(getActivity());
                View noteView = li.inflate(R.layout.dialog_note, null);
                final EditText noteText = noteView.findViewById(R.id.note);
                noteText.setText(workout.note);
                noteText.setSelection(noteText.getText().length());
                builder.setView(noteView);

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        workout.note = noteText.getText().toString();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


        final TextView globalTimer = root.findViewById(R.id.global_timer);

        handler = new Handler();
        runnable = new Runnable() {

            @Override
            public void run() {
                int seconds = (int) ((System.currentTimeMillis() - workout.startTime) / 1000);
                int minutes = seconds / 60;
                seconds     = seconds % 60;

                globalTimer.setText(getString(R.string.time, minutes, seconds));

                handler.postDelayed(this, 500);
            }
        };

        handler.post(runnable);

        ImageView skipExecution = root.findViewById(R.id.skip_execution);
        skipExecution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNextExecution();
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getBooleanExtra(ADD_EXERCISE, false)){
            isForeground = true;
            timer.onDestroy();

            ExerciseListFragment fragment = new ExerciseListFragment();
            fragment.setListener(this);
            fragment.setSettings(null);
            fragment.setExerciseType(ExerciseType.NORMAL);
            fragment.setPickerMode(true);
            fragment.setMultipleMode(false);
            fragment.setAutoClose(false);
            ((HomeActivity)getActivity()).replaceFragment(fragment, null);
        }else if(data.getIntExtra(CHANGE_EXERCISE, -1) != -1){
            isForeground = true;
            timer.onDestroy();

            //move the selected exercise
            ExerciseWithSet targetExercise = workout.workoutExecution.get(data.getIntExtra(CHANGE_EXERCISE, -1));

            if(targetExercise.currentSet < targetExercise.sets.size() -1)
                targetExercise.currentSet++;

            workout.workoutExecution.remove(data.getIntExtra(CHANGE_EXERCISE, -1));
            workout.workoutExecution.add(workout.currentExercise, targetExercise);

            openNextFragmentStep();
        }else if(data.getBooleanExtra(ADD_SET, false)){
            isForeground = true;
            timer.onDestroy();
            ExerciseSetFragment fragment = new ExerciseSetFragment();
            fragment.setListener(this);

            fragment.setExerciseWithSet(workout.workoutExecution.get(workout.currentExercise));
            fragment.setIsFromExecution(true);
            fragment.setDeleteOnlyNew(true);
            ((HomeActivity)getActivity()).replaceFragment(fragment, null);
        }
    }

    private void openNextExecution(){
        timer.onDestroy();

        if(getActivity() != null){
            openingNextExecution = true;
            ExerciseWithSet exerciseWithSet = workout.workoutExecution.get(workout.currentExercise);

            //circuit
            if(exerciseWithSet.getSuperset() != null && exerciseWithSet.getSuperset().exerciseType == ExerciseType.CIRCUIT){
                //skip to next exercise when all set are done
                if(exerciseWithSet.currentSet >= (exerciseWithSet.sets.size() - 1)){
                    workout.currentExercise++;
                    //if the next exercise is part of the circuit
                }else if((workout.currentExercise + 1) <= (workout.workoutExecution.size() - 1)
                        && workout.workoutExecution.get(workout.currentExercise + 1).getSuperset() != null
                        && workout.workoutExecution.get(workout.currentExercise + 1).getSuperset().supersetId == exerciseWithSet.getSuperset().supersetId){
                    exerciseWithSet.currentSet++;
                    workout.currentExercise++;
                    //back to the first exercise of the circuit
                }else{
                    exerciseWithSet.currentSet++;
                    boolean found = false;
                    for(int i = 0; i < workout.currentExercise && !found; i++){
                        if(workout.workoutExecution.get(i).getSuperset() != null
                                && workout.workoutExecution.get(i).getSuperset().supersetId == exerciseWithSet.getSuperset().supersetId){
                            found = true;
                            workout.currentExercise = i;
                        }
                    }
                }
                //Superset
            }else if(exerciseWithSet.getSuperset() != null
                    && exerciseWithSet.getSuperset().exerciseType == ExerciseType.SUPERSET){
                if(exerciseWithSet.currentSet >= (exerciseWithSet.sets.size() -1) ){
                    for(int i  = workout.currentExercise, len = workout.workoutExecution.size(); i < len; i++){
                        if(workout.workoutExecution.get(i).getSuperset() != null
                                && workout.workoutExecution.get(i).getSuperset().exerciseType == ExerciseType.SUPERSET
                                && workout.workoutExecution.get(i).getSuperset().supersetId == exerciseWithSet.getSuperset().supersetId){
                            workout.currentExercise++;
                        }
                    }
                }else{
                    for(int i  = 0, len = workout.workoutExecution.size(); i < len; i++){
                        if(workout.workoutExecution.get(i).getSuperset() != null
                                && workout.workoutExecution.get(i).getSuperset().supersetId == exerciseWithSet.getSuperset().supersetId)
                            workout.workoutExecution.get(i).currentSet++;
                    }
                }
            }else if(exerciseWithSet.currentSet >= (exerciseWithSet.sets.size() -1)){
                workout.currentExercise++;
            }else{
                exerciseWithSet.currentSet++;
            }

            if(workout.currentExercise > (workout.workoutExecution.size() -1) &&  exerciseWithSet.currentSet >= (exerciseWithSet.sets.size() -1)){
                getActivity().stopService(new Intent(getActivity(), ExecutionService.class));
                WorkoutExecutionEndFragment fragment = new WorkoutExecutionEndFragment();
                fragment.setWorkout(workout);
                fragment.setStartTime(workout.startTime);
                ((HomeActivity)getActivity()).replaceFragment(fragment, null);
            }else{
                openNextFragmentStep();
            }
        }
    }

    private void openNextFragmentStep(){
        if(workout.workoutExecution.get(workout.currentExercise).getSuperset() != null
                && workout.workoutExecution.get(workout.currentExercise).getSuperset().exerciseType == ExerciseType.SUPERSET){
            WorkoutSupersetExecutionFragment fragment = new WorkoutSupersetExecutionFragment();
            fragment.setWorkout(workout);
            ((HomeActivity)getActivity()).replaceFragment(fragment, null);
        }else{
            WorkoutExecutionFragment fragment = new WorkoutExecutionFragment();
            fragment.setWorkout(workout);
            ((HomeActivity)getActivity()).replaceFragment(fragment, null);
        }
    }

    public void setWorkout(WorkoutWithExercise workout) {
        this.workout = workout;
        this.exercises = getExercises(this.workout.currentExercise);
    }

    public List<ExerciseWithSet> getExercises(int position){
        List<ExerciseWithSet> exercises = new ArrayList<>();
        ExerciseWithSet exerciseWithSet = this.workout.workoutExecution.get(position);

        if(exerciseWithSet.getSuperset() != null && exerciseWithSet.getSuperset().exerciseType == ExerciseType.SUPERSET){
            for(int i  = position, len = this.workout.workoutExecution.size(); i < len; i++){
                if(this.workout.workoutExecution.get(i).getSuperset() != null &&
                        this.workout.workoutExecution.get(i).getSuperset().supersetId == exerciseWithSet.getSuperset().supersetId){
                    exercises.add(this.workout.workoutExecution.get(i));
                }
            }
        }else{
            exercises.add(this.workout.workoutExecution.get(position));
        }

        return exercises;
    }

    @Override
    public void onServiceBind() {
        super.onServiceBind();
        if(timer != null)
            timer.setService(executionService);
    }

    @Override
    protected int getFragmentType() {
        return FragmentType.WORKOUT_REST;
    }

    @Override
    public void onDestroy() {
        if(timer != null)
            timer.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        if(!isForeground){
            handler.removeCallbacks(runnable);

            if(executionService != null && executionService.isStarted && !openingNextExecution && timer != null)
                executionService.openTimer(timer.getTotalRest());
            openingNextExecution = false;
        }

        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        if(!isForeground && timer != null) {
            timer.restoreTime();
        }

        handler.post(runnable);

        super.onResume();
    }

    @Override
    public void onFragmentInteraction(List<ExerciseWithGroup> exercises, int exerciseType, Superset superset) {
        //we always have normal exercise type
        timer.onDestroy();

        if(exercises == null || exercises.size() == 0){
            //close exercise picker
            getActivity().getSupportFragmentManager().popBackStack();

            if(isTimerFinished){
                openNextExecution();
            }else{
                isForeground = false;
            }

            return;
        }

        ExerciseSetFragment fragment = new ExerciseSetFragment();
        fragment.setListener(this);
        ExerciseWithSet exerciseWithSet = new ExerciseWithSet();
        exerciseWithSet.setExercise(exercises.get(0).exercise);

        exerciseWithSet.workoutExecution = new WorkoutExecution();
        exerciseWithSet.workoutExecution.workoutId = workout.workout.id;
        exerciseWithSet.workoutExecution.executionId = 1000000 + (long) (Math.random() * (Long.MAX_VALUE - 1000000));
        exerciseWithSet.workoutExecution.exerciseId = exerciseWithSet.getExercise().eId;

        exerciseWithSet.sets = new ArrayList<>();
        SetExecution setExecution = ApplicationHelper.getDefaultSetExecution(getContext());
        exerciseWithSet.sets.add(setExecution);

        fragment.setExerciseWithSet(exerciseWithSet);
        fragment.setIsFromExecution(true);
        ((HomeActivity)getActivity()).replaceFragment(fragment, null);
    }

    @Override
    public void onExerciseAdded(ExerciseWithSet exercise, int position) {
        //we are adding an exercise

        if(exercise.workoutExecution.executionId != workout.getCurrentExercise().workoutExecution.executionId){
            //close exercise picker
            getActivity().getSupportFragmentManager().popBackStack();

            ExerciseWithSet exerciseWithSet;
            ExerciseWithSet currentExercise = workout.workoutExecution.get(workout.currentExercise);

            int indexToInsert = -1;

            if(currentExercise.getSuperset() != null){
                //we search for the next exercise outside the superset
                for(int i = workout.currentExercise+1, len = workout.workoutExecution.size(); i < len; i++){
                    exerciseWithSet = workout.workoutExecution.get(i);
                    if((exerciseWithSet.getSuperset() == null
                            || exerciseWithSet.getSuperset().supersetId != currentExercise.getSuperset().supersetId)){
                        indexToInsert = i;
                        break;
                    }

                }
            }else{
                indexToInsert = workout.currentExercise+1;
            }

            if (indexToInsert != -1) {
                workout.workoutExecution.add(indexToInsert, exercise);
                Toast.makeText(getContext(), getString(R.string.manage_exercise_added, indexToInsert+1), Toast.LENGTH_SHORT).show();
            }else{
                workout.workoutExecution.add(exercise);
                Toast.makeText(getContext(), getString(R.string.manage_exercise_added, workout.workoutExecution.size()), Toast.LENGTH_SHORT).show();
            }
        }


        if(isTimerFinished)
            openNextExecution();
        else
            isForeground = false;
    }

    @Override
    public void onExerciseRemoved() {
        //not used
    }
}
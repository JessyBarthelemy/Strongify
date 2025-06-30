package com.jessy_barthelemy.strongify.fragment.execution;


import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.database.asyncTask.InsertHistoryTask;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.entity.WorkoutHistory;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;
import com.jessy_barthelemy.strongify.enumeration.ExerciseSetType;
import com.jessy_barthelemy.strongify.enumeration.ExerciseType;
import com.jessy_barthelemy.strongify.fragment.BaseFragment;
import com.jessy_barthelemy.strongify.fragment.WorkoutListFragment;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;

import java.util.Date;

public class WorkoutExecutionEndFragment extends BaseFragment {

    private WorkoutWithExercise workout;
    private long startTime;

    public WorkoutExecutionEndFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workout_execution_end, container, false);

        int totalRep = 0;
        int totalRepDone = 0;

        int totalDistance = 0;
        int totalDistanceDone = 0;

        double totalWeightLifted = 0;
        double totalWeight = 0;

        for(ExerciseWithSet exercise : workout.workoutExecution){
            for(SetExecution execution : exercise.sets){
                if(execution.exerciseSetType == ExerciseSetType.DISTANCE){
                    totalDistance += execution.reps;
                    totalDistanceDone += execution.repsDone;
                }else{
                    totalRep += execution.reps;
                    totalRepDone += execution.repsDone;
                }

                totalWeight += execution.weight;
                totalWeightLifted += execution.weightLifted;
            }
        }

        final int totalSeconds = (int) ((System.currentTimeMillis() - startTime) / 1000);
        int minutes = totalSeconds / 60;
        int seconds     = totalSeconds % 60;

        TextView totalTime = root.findViewById(R.id.history_total_time);
        totalTime.setText(getString(R.string.time, minutes, seconds));

        TextView exerciseCount = root.findViewById(R.id.history_exe);
        exerciseCount.setText(String.valueOf(workout.workoutExecution.size()));

        TextView repsCount = root.findViewById(R.id.history_reps);
        repsCount.setText(getString(R.string.result_ratio, totalRepDone, totalRep));

        TextView distanceCount = root.findViewById(R.id.history_distance);
        distanceCount.setText(getString(R.string.result_ratio, totalDistanceDone, totalDistance));

        ImageView distanceIcon = root.findViewById(R.id.history_distance_icon);
        distanceIcon.setImageResource(ApplicationHelper.getDistanceIcon(getActivity()));

        TextView weightCount = root.findViewById(R.id.history_weight);
        weightCount.setText(getString(R.string.result_ratio_double, totalWeightLifted, totalWeight));

        ImageView weightIcon = root.findViewById(R.id.history_weight_icon);
        weightIcon.setImageResource(ApplicationHelper.getWeightIcon(getActivity()));

        final EditText note = root.findViewById(R.id.note);
        note.setText(workout.note);

        Button ignoreButton = root.findViewById(R.id.cancel_action);

        ignoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkoutListFragment fragment = WorkoutListFragment.newInstance();
                if(getActivity() != null)
                    ((HomeActivity)getActivity()).replaceFragment(fragment, null);
            }
        });

        Button saveButton = root.findViewById(R.id.save_action);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkoutHistory history = new WorkoutHistory();
                history.workoutId = workout.workout.id;
                history.duration = totalSeconds;
                history.title = workout.workout.name;
                history.text = getWorkoutHistory();
                history.date = new Date();

                new InsertHistoryTask(getActivity(), history, workout).execute();
                WorkoutListFragment fragment = WorkoutListFragment.newInstance();
                if(getActivity() != null)
                    ((HomeActivity)getActivity()).replaceFragment(fragment, null);
            }
        });
        return root;
    }

    private String getWorkoutHistory(){
        StringBuilder history = new StringBuilder();

        int setCount;
        StringBuilder repsBuilder = new StringBuilder();
        StringBuilder weightBuilder = new StringBuilder();
        StringBuilder restBuilder = new StringBuilder();

        ExerciseWithSet exerciseWithSet;
        for(int i = 0, len = workout.workoutExecution.size(); i < len; i++){
            exerciseWithSet = workout.workoutExecution.get(i);
            history.append("<b>");
            history.append(ApplicationHelper.getExerciseTitle(getActivity(), exerciseWithSet.getExercise().title, exerciseWithSet.getExercise().base));

            history.append(" - ");
            setCount = exerciseWithSet.sets.size();
            history.append(getResources().getQuantityString(R.plurals.next_exercise_set_count, setCount, setCount));
            history.append("</b><br>");

            if(exerciseWithSet.getSuperset() != null){
                history.append("<i>");
                if(exerciseWithSet.getSuperset().exerciseType == ExerciseType.SUPERSET)
                    history.append(getString(R.string.workout_superset));
                else if(exerciseWithSet.getSuperset().exerciseType == ExerciseType.CIRCUIT)
                    history.append(getString(R.string.workout_circuit));
                history.append("</i><br>");
            }

            repsBuilder.setLength(0);
            weightBuilder.setLength(0);
            restBuilder.setLength(0);

            ApplicationHelper.formatExercise(getActivity(), exerciseWithSet, repsBuilder, weightBuilder, restBuilder, true);
            history.append(getString(R.string.exercise_set_repetition));
            history.append(" ");
            history.append( repsBuilder.toString());
            history.append("<br>");
            history.append(getString(R.string.exercise_set_weight));
            history.append(" ");
            history.append(weightBuilder.toString());
            history.append("<br>");
            history.append(getString(R.string.exercise_set_rest));
            history.append(" ");
            history.append(restBuilder.toString());

            if(i != (len-1)){
                history.append("<br>");
                history.append("<br>");
            }
        }

        if(workout.note != null){
            history.append("<br>");
            history.append("<br><b>");
            history.append(getString(R.string.workout_note));
            history.append("</b><br>");
            history.append(workout.note);
        }
        return history.toString();
    }

    public void setWorkout(WorkoutWithExercise workout) {
        this.workout = workout;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean isBackPressEnabled() {
        return false;
    }
}

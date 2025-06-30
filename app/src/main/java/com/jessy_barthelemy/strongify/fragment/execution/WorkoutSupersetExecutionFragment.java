package com.jessy_barthelemy.strongify.fragment.execution;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.adapter.customExecution.CustomExecutionListAdapter;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;
import com.jessy_barthelemy.strongify.enumeration.FragmentType;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;

import java.util.ArrayList;
import java.util.List;

public class WorkoutSupersetExecutionFragment extends BaseExecutionFragment {

    private List<ExerciseWithSet> exercises;
    private Handler handler;
    private Runnable runnable;

    public WorkoutSupersetExecutionFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workout_superset_execution, container, false);

        RecyclerView exerciseView = root.findViewById(R.id.custom_ex_list);
        int weightIconId = ApplicationHelper.getWeightIcon(getActivity());
        CustomExecutionListAdapter adapter = new CustomExecutionListAdapter(getActivity(), exercises, weightIconId);
        exerciseView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        exerciseView.setLayoutManager(layoutManager);
//        exerciseView.setHasFixedSize(true);

        Button action = root.findViewById(R.id.exercise_rest_action);

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){

                    WorkoutExecutionRestFragment fragment = new WorkoutExecutionRestFragment();
                    fragment.setWorkout(workout);
                    ((HomeActivity)getActivity()).replaceFragment(fragment, null);
                }
            }
        });

        TextView currentSet = root.findViewById(R.id.current_set);
        currentSet.setText(getString(R.string.current_set, exercises.get(0).currentSet + 1, exercises.get(0).sets.size()));

        workout.endTimer = 0;
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

        return root;
    }

    public void setWorkout(WorkoutWithExercise workout) {
        this.workout = workout;

        this.exercises = new ArrayList<>();
        ExerciseWithSet exerciseWithSet = this.workout.workoutExecution.get(this.workout.currentExercise);
        for(int i  = this.workout.currentExercise, len = this.workout.workoutExecution.size(); i < len; i++){
            if(this.workout.workoutExecution.get(i).getSuperset() != null &&
               this.workout.workoutExecution.get(i).getSuperset().supersetId == exerciseWithSet.getSuperset().supersetId){
                exercises.add(this.workout.workoutExecution.get(i));
            }
        }
    }

    @Override
    protected int getFragmentType() {
        return FragmentType.WORKOUT_SUPERSET;
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        handler.post(runnable);
        super.onResume();
    }
}

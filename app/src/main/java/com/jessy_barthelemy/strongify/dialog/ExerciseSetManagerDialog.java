package com.jessy_barthelemy.strongify.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.adapter.SimpleExercisesAdapter;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.fragment.execution.WorkoutExecutionRestFragment;

import java.util.ArrayList;
import java.util.List;

public class ExerciseSetManagerDialog extends DialogFragment {

    private List<ExerciseWithSet> exercises;
    private boolean canChangeExercise;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.dialog_manage_exercises, container, false);

        Button addExercise = v.findViewById(R.id.add_exercise);
        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(WorkoutExecutionRestFragment.ADD_EXERCISE, true);
                if(getTargetFragment() != null)
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dismiss();
            }
        });

        Button addSet = v.findViewById(R.id.add_set);
        addSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(WorkoutExecutionRestFragment.ADD_SET, true);
                if(getTargetFragment() != null)
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dismiss();
            }
        });

        Button ok = v.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final ListView changeExerciseList = v.findViewById(R.id.change_exercise);

        if(canChangeExercise){
            if(exercises != null && exercises.size() > 0){
                SimpleExercisesAdapter exercisesAdapter = new SimpleExercisesAdapter(getContext(), exercises);
                changeExerciseList.setAdapter(exercisesAdapter);
            }

            changeExerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra(WorkoutExecutionRestFragment.CHANGE_EXERCISE, exercises.get(position).exercisePosition);
                    if(getTargetFragment() != null)
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    dismiss();
                }
            });

            if(exercises == null || exercises.size() == 0){
                changeExerciseList.setVisibility(View.GONE);
                TextView noExercises = v.findViewById(R.id.change_no_exercise);
                noExercises.setVisibility(View.VISIBLE);
            }

        }else{
            changeExerciseList.setVisibility(View.GONE);
            TextView unavailable = v.findViewById(R.id.change_exercise_unavailable);
            unavailable.setVisibility(View.VISIBLE);

            addSet.setVisibility(View.GONE);
        }

        return v;
    }

    public void setExercises(List<ExerciseWithSet> exercises, int current) {
        this.exercises = new ArrayList<>();
        canChangeExercise = exercises.get(current).getSuperset() == null;

        if(canChangeExercise){
            for(int i = current + 1, len = exercises.size(); i < len; i++){
                if(exercises.get(i).getSuperset() == null){
                    exercises.get(i).exercisePosition = i;
                    this.exercises.add(exercises.get(i));
                }
            }
        }
    }
}

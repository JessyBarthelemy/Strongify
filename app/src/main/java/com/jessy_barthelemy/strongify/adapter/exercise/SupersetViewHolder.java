package com.jessy_barthelemy.strongify.adapter.exercise;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.TimeSpan;
import com.jessy_barthelemy.strongify.interfaces.CustomExerciseManager;
import com.jessy_barthelemy.strongify.interfaces.OnExerciseChanged;

public class SupersetViewHolder extends ExerciseViewHolder {
    private RelativeLayout supersetWrapper;
    private TextView repetition;
    private TextView rest;

    SupersetViewHolder(Context context, View itemView, int weightUnityId, OnExerciseChanged listener, CustomExerciseManager customExerciseManager) {
        super(context, itemView, weightUnityId, listener, customExerciseManager);
        supersetWrapper = itemView.findViewById(R.id.superset_wrapper);

        repetition = itemView.findViewById(R.id.exercise_wrapper_repetition);
        rest = itemView.findViewById(R.id.exercise_wrapper_rest);

        View addExercisesIcon = itemView.findViewById(R.id.custom_exercise_add_wrapper);
        addExercisesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExercises();
            }
        });

        View updateCustomExercisesIcon = itemView.findViewById(R.id.custom_exercise_update);
        updateCustomExercisesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCustomExercise();
            }
        });
    }

    public void bindExercise(ExerciseWithSet exercise, Context context){
        super.bindExercise(exercise);
        restViewWrapper.setVisibility(View.GONE);

        if(exercise.workoutExecution.supersetOrder == 0) {
            supersetWrapper.setVisibility(View.VISIBLE);
            repetition.setText(String.valueOf(exercise.sets.size()));
            TimeSpan timeSpan = ApplicationHelper.getTimeSpan(exercise.getSuperset().rest);
            rest.setText(context.getString(R.string.workout_rest, timeSpan.minutes, timeSpan.seconds));
        }else
            supersetWrapper.setVisibility(View.GONE);
    }
}
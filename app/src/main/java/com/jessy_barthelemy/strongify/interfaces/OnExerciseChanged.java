package com.jessy_barthelemy.strongify.interfaces;

import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;

public interface OnExerciseChanged {
    void onExerciseAdded(ExerciseWithSet exercise, int position);

    void onExerciseRemoved();
}

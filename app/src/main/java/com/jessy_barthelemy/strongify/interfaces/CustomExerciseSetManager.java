package com.jessy_barthelemy.strongify.interfaces;

import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;

import java.util.List;

public interface CustomExerciseSetManager {
    void onCustomExerciseSetChanged(List<ExerciseWithSet> exercises);
}

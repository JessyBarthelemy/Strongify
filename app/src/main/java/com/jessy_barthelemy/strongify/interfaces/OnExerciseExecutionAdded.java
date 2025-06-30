package com.jessy_barthelemy.strongify.interfaces;

import com.jessy_barthelemy.strongify.database.entity.Superset;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;

import java.util.List;

public interface OnExerciseExecutionAdded {
    void onExercisesAdded(List<ExerciseWithSet> exerciseWithSets, Superset superset);
}

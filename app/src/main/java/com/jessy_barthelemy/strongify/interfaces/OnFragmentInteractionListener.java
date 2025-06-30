package com.jessy_barthelemy.strongify.interfaces;

import com.jessy_barthelemy.strongify.database.entity.Superset;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithGroup;

import java.util.List;

public interface OnFragmentInteractionListener {
    void onFragmentInteraction(List<ExerciseWithGroup> exercises, int exerciseType, Superset superset);
}

package com.jessy_barthelemy.strongify.interfaces;

import android.content.Context;

import com.jessy_barthelemy.strongify.database.entity.Workout;

public interface WorkoutManager {
    void onWorkoutChanged(Context context, Workout workout);
}

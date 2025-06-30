package com.jessy_barthelemy.strongify.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.jessy_barthelemy.strongify.database.entity.Workout;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;

import java.util.List;

@Dao
public interface WorkoutDao extends BaseDao<Workout>{
    @Update
    void update(List<Workout> workouts);

    @Transaction
    @Query("SELECT * FROM workout " +
            "LEFT JOIN (SELECT MAX(date) As date, workoutId, duration " +
            "           FROM workouthistory " +
            "           GROUP BY workoutId) " +
            "ON id = workoutId ORDER BY `order`")
    LiveData<List<WorkoutWithExercise>> getAllAsync();

    @Query("SELECT * FROM workout")
    List<Workout> getAll();

    @Query("SELECT * FROM workout WHERE id = :workoutId")
    Workout get(long workoutId);
}

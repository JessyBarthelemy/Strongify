package com.jessy_barthelemy.strongify.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.jessy_barthelemy.strongify.database.entity.WorkoutHistory;

import java.util.List;

@Dao
public interface WorkoutHistoryDao extends BaseDao<WorkoutHistory>{
    @Query("SELECT * FROM WorkoutHistory WHERE date BETWEEN :start AND :end")
    LiveData<List<WorkoutHistory>> getBetween(long start, long end);
}

package com.jessy_barthelemy.strongify.database.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.jessy_barthelemy.strongify.database.entity.ExerciseHistory;
import com.jessy_barthelemy.strongify.database.entity.WeightMeasure;

import java.util.List;

@Dao
public interface ExerciseHistoryDao extends BaseDao<ExerciseHistory>{
    @Query("SELECT * FROM (SELECT id, exerciseId, measureDate, SUM(repetitions) As repetitions, SUM(weight) As weight FROM ExerciseHistory WHERE exerciseId = :exerciseId AND measureDate BETWEEN :startDate AND :endDate GROUP BY measureDate UNION SELECT * FROM (SELECT id, exerciseId, measureDate, SUM(repetitions) As repetitions, SUM(weight) As weight FROM ExerciseHistory WHERE exerciseId = :exerciseId AND measureDate < :startDate GROUP BY measureDate ORDER BY measureDate DESC LIMIT 1)) ORDER BY measureDate ASC ")
    LiveData<List<ExerciseHistory>> getRange(long exerciseId, long startDate, long endDate);

    @Query("DELETE FROM ExerciseHistory WHERE historyId = :historyId")
    void deleteByHistoryId(String historyId);
}

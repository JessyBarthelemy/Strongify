package com.jessy_barthelemy.strongify.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.jessy_barthelemy.strongify.database.entity.Exercise;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithGroup;

import java.util.List;

@Dao
public interface ExerciseDao extends BaseDao<Exercise>{
    @Transaction
    @Query("SELECT * FROM Exercise e LEFT JOIN ExerciseGroup g ON e.groupId = g.id ORDER BY g.sortOrder")
    LiveData<List<ExerciseWithGroup>> getAll();

    @Query("SELECT * FROM Exercise e LEFT JOIN ExerciseGroup g ON e.groupId = g.id LEFT JOIN ExerciseEquipment eq ON e.eId = eq.exerciseId WHERE eq.equipmentId IN(:ids) GROUP BY e.eId ORDER BY g.sortOrder")
    LiveData<List<ExerciseWithGroup>> getByEquipments(List<Long> ids);
}

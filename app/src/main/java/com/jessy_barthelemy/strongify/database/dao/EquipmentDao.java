package com.jessy_barthelemy.strongify.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.jessy_barthelemy.strongify.database.entity.Equipment;

import java.util.List;

@Dao
public interface EquipmentDao extends BaseDao<Equipment>{
    @Query("SELECT * FROM equipment e ORDER BY e.title")
    LiveData<List<Equipment>> getAll();

    @Query("SELECT e.*, CASE WHEN ex.equipmentId IS NULL THEN 0 ELSE 1 END As isSelected FROM equipment e LEFT JOIN ExerciseEquipment ex ON e.equipmentId = ex.equipmentId AND ex.exerciseId = :exerciseId")
    LiveData<List<Equipment>> getByExerciseId(long exerciseId);
}

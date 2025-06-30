package com.jessy_barthelemy.strongify.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.jessy_barthelemy.strongify.database.entity.ExerciseGroup;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithGroup;

import java.util.List;

@Dao
public interface ExerciseGroupDao extends BaseDao<ExerciseGroup>{
    @Query("SELECT * FROM ExerciseGroup")
    LiveData<List<ExerciseGroup>> getAll();
}

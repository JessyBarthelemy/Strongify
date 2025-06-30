package com.jessy_barthelemy.strongify.database.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.jessy_barthelemy.strongify.database.entity.Profile;
import com.jessy_barthelemy.strongify.database.entity.WeightMeasure;
import com.jessy_barthelemy.strongify.database.entity.Workout;

import java.util.List;

@Dao
public interface WeightMeasureDao extends BaseDao<WeightMeasure>{
    @Query("SELECT * FROM WeightMeasure ORDER BY weightDate DESC LIMIT 1")
    LiveData<WeightMeasure> getLast();

    @Query("SELECT * FROM WeightMeasure WHERE weightDate = :date LIMIT 1")
    LiveData<WeightMeasure> getByDate(long date);

    @Query("SELECT * FROM (SELECT * FROM WeightMeasure WHERE weightDate BETWEEN :startDate AND :endDate UNION SELECT * FROM (SELECT * FROM WeightMeasure WHERE weightDate < :startDate ORDER BY weightDate DESC LIMIT 1)) ORDER BY weightDate ASC ")
    LiveData<List<WeightMeasure>> getRange(long startDate, long endDate);
}

package com.jessy_barthelemy.strongify.database.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.jessy_barthelemy.strongify.database.entity.BodyMeasurements;
import com.jessy_barthelemy.strongify.database.entity.WeightMeasure;

import java.util.List;

@Dao
public interface BodyMeasurementsDao extends BaseDao<BodyMeasurements>{
    @Query("SELECT * FROM (SELECT * FROM BodyMeasurements WHERE measureDate BETWEEN :startDate AND :endDate UNION SELECT * FROM (SELECT * FROM BodyMeasurements WHERE measureDate < :startDate ORDER BY measureDate DESC LIMIT 1)) ORDER BY measureDate ASC ")
    LiveData<List<BodyMeasurements>> getRange(long startDate, long endDate);

    @Query("SELECT * FROM BodyMeasurements WHERE measureDate = :date LIMIT 1")
    LiveData<BodyMeasurements> getByDate(long date);

    @Query("SELECT * FROM BodyMeasurements ORDER BY measureDate DESC LIMIT 1")
    LiveData<BodyMeasurements> getLast();
}

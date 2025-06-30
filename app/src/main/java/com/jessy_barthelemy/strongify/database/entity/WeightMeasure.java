package com.jessy_barthelemy.strongify.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.jessy_barthelemy.strongify.database.converter.DateCalendarConverter;

import java.io.Serializable;
import java.util.Calendar;

@Entity
public class WeightMeasure implements Serializable{
    @PrimaryKey(autoGenerate = true)
    public long id;
    public float weight;
    public float fat;
    public float water;
    public float bones;
    public float muscle;
    @TypeConverters({DateCalendarConverter.class})
    @NonNull
    public Calendar weightDate;

    public WeightMeasure(){

        weightDate = Calendar.getInstance();
        weightDate.set(Calendar.HOUR_OF_DAY, 0);
        weightDate.set(Calendar.HOUR, 0);
        weightDate.set(Calendar.MINUTE, 0);
        weightDate.set(Calendar.SECOND, 0);
        weightDate.set(Calendar.MILLISECOND, 0);
    }

    public void clear(){
        id = 0;
        weight = 0;
        fat = 0;
        water = 0;
        bones = 0;
        muscle = 0;
    }
}
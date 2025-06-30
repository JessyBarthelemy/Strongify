package com.jessy_barthelemy.strongify.database.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.jessy_barthelemy.strongify.database.converter.DateCalendarConverter;

import java.io.Serializable;
import java.util.Calendar;

@Entity(foreignKeys = {@ForeignKey(entity =  Exercise.class,
        parentColumns = "eId",
        childColumns = "exerciseId",
        onDelete = ForeignKey.CASCADE)})
public class ExerciseHistory implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public int repetitions;
    public double weight;

    public long exerciseId;
    public String historyId;

    @TypeConverters({DateCalendarConverter.class})
    @NonNull
    public Calendar measureDate;

    public ExerciseHistory(){
        measureDate = Calendar.getInstance();
        measureDate.set(Calendar.HOUR_OF_DAY, 0);
        measureDate.set(Calendar.HOUR, 0);
        measureDate.set(Calendar.MINUTE, 0);
        measureDate.set(Calendar.SECOND, 0);
        measureDate.set(Calendar.MILLISECOND, 0);
    }
}
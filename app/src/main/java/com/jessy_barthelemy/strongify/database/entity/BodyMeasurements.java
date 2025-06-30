package com.jessy_barthelemy.strongify.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.jessy_barthelemy.strongify.database.converter.DateCalendarConverter;

import java.io.Serializable;
import java.util.Calendar;

@Entity
public class BodyMeasurements implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public float neck;
    public float shoulders;
    public float chest;
    public float bicepsLeft;
    public float bicepsRight;
    public float forearmLeft;
    public float forearmRight;
    public float wristRight;
    public float wristLeft;
    public float waist;
    public float hips;
    public float thighLeft;
    public float thighRight;
    public float calfLeft;
    public float calfRight;
    public float anklesLeft;
    public float anklesRight;

    @TypeConverters({DateCalendarConverter.class})
    @NonNull
    public Calendar measureDate;

    public BodyMeasurements(){
        measureDate = Calendar.getInstance();
        measureDate.set(Calendar.HOUR_OF_DAY, 0);
        measureDate.set(Calendar.HOUR, 0);
        measureDate.set(Calendar.MINUTE, 0);
        measureDate.set(Calendar.SECOND, 0);
        measureDate.set(Calendar.MILLISECOND, 0);
    }

    public void clear(){
        id = 0;
        neck = 0;
        shoulders = 0;
        chest = 0;
        bicepsLeft = bicepsRight = 0;
        forearmLeft = forearmRight = 0;
        wristLeft = wristRight = 0;
        waist = 0;
        hips = 0;
        thighLeft = thighRight = 0;
        calfLeft = calfRight = 0;
        anklesLeft = anklesRight = 0;
    }
}

package com.jessy_barthelemy.strongify.database.converter;

import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

public class DateCalendarConverter {
    @TypeConverter
    public static Calendar toDate(Long dateLong){

        if(dateLong == null)
            return null;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateLong);
        return cal;
    }

    @TypeConverter
    public static Long fromDate(Calendar date){
        return date == null ? null : date.getTimeInMillis();
    }
}
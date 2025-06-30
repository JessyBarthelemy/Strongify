package com.jessy_barthelemy.strongify.database.entity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.jessy_barthelemy.strongify.NotificationPublisher;
import com.jessy_barthelemy.strongify.database.converter.PeriodicityConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
public class Workout implements Comparable<Workout>, Serializable{

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    @TypeConverters(PeriodicityConverter.class)
    public List<Integer> periodicity;
    public int order;
    public Integer reminderHour;
    public Integer reminderMinute;

    @Override
    public int compareTo(@NonNull Workout workout) {
        return (order > workout.order)? 1 : -1;
    }

    @Override
    public Workout clone(){
        Workout clone = new Workout();
        clone.id = id;
        clone.name = name;
        clone.periodicity = new ArrayList<>(periodicity);
        clone.order = order;
        return clone;
    }

    public JSONObject toJSON() throws JSONException {
        PeriodicityConverter converter = new PeriodicityConverter();
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("name", name);
        result.put("periodicity", converter.fromOptionValuesList(periodicity));
        result.put("order", order);

        return result;
    }

    public void cancelAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent cancelIntent = new Intent(context.getApplicationContext(), NotificationPublisher.class);
        cancelIntent.putExtra(NotificationPublisher.NOTIFICATION_WORKOUT_ID, id);
        PendingIntent pendingCancelIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (int)id, cancelIntent, PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);
        pendingCancelIntent.cancel();
        alarmManager.cancel(pendingCancelIntent);
    }

    public void scheduleAlarm(Context context, boolean forceNext){
        if(reminderHour != null){
            cancelAlarm(context);

            Calendar calendar = Calendar.getInstance();

            if(forceNext)
                calendar.add(Calendar.DATE, 1);

            if(periodicity.contains(getCalendarDay(calendar.get(Calendar.DAY_OF_WEEK)))){
                if((reminderHour == calendar.get(Calendar.HOUR_OF_DAY) && reminderMinute <= calendar.get(Calendar.MINUTE))
                        || reminderHour < calendar.get(Calendar.HOUR_OF_DAY)){
                    calendar.add(Calendar.DATE, 7);
                }
            }else{
                while(!periodicity.contains(getCalendarDay(calendar.get(Calendar.DAY_OF_WEEK)))){
                    calendar.add(Calendar.DATE, 1);
                }
            }

            calendar.set(Calendar.HOUR_OF_DAY, reminderHour);
            calendar.set(Calendar.MINUTE, reminderMinute);

            long futureInMillis = calendar.getTimeInMillis();

            Intent notificationIntent = new Intent(context.getApplicationContext(), NotificationPublisher.class);
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_WORKOUT_ID, id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (int)id, notificationIntent, PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
            else
                alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
        }
    }

    private int getCalendarDay(int periodicityDay){
        switch (periodicityDay){
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            case Calendar.SATURDAY:
                return 5;
            default:
                return 6;
        }
    }
}

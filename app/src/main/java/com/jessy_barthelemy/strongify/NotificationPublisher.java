package com.jessy_barthelemy.strongify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jessy_barthelemy.strongify.database.asyncTask.AddNotificationTask;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_WORKOUT_ID = "workout_id";

    public void onReceive(final Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        long workoutId = 0;
        if(extras != null){
            workoutId = extras.getLong(NOTIFICATION_WORKOUT_ID);
            new AddNotificationTask(context, workoutId).execute();
        }
    }
}
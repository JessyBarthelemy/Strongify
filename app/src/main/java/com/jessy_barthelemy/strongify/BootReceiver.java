package com.jessy_barthelemy.strongify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.jessy_barthelemy.strongify.database.asyncTask.RescheduleAlarmTask;
import com.jessy_barthelemy.strongify.service.NetworkManager;

public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        new RescheduleAlarmTask(context).execute();
    }
}
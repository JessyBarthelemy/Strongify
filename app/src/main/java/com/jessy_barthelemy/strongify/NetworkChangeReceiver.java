package com.jessy_barthelemy.strongify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.jessy_barthelemy.strongify.service.NetworkManager;

public class NetworkChangeReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent)
    {
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            if (connectivityManager != null) {
//                NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
//                if (netInfo != null && netInfo.isConnected())
//                    NetworkManager.checkPurchase(context);
//            }
//        }
    }
}
package com.jessy_barthelemy.strongify.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.customLayout.Timer;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.interfaces.OnTimerFinishedListener;

public class ExecutionService extends Service {
    private final IBinder binder = new ExecutionBinder();
    private static final int NOTIFICATION_ID = 1;
    public static final String WORKOUT_PARAMETER = "workout";
    public static final String FRAGMENT_TYPE_PARAMETER = "fragmentToOpen";
    private static final String STOP_ACTION = "STOP_ACTION";
    private static final String CHANNEL_ID = "CHANNEL_ID";

    private Notification.Builder builder;
    public boolean isStarted;
    private WorkoutWithExercise workout;

    private WindowManager windowManager;
    private View timerWrapper;
    private Timer timerView;
    private boolean timerHeaderVisible;
    private NotificationManager nm;
    private Intent notificationIntent;
    private PendingIntent contentIntent;
    private long startTime;

    public class ExecutionBinder extends Binder {
        public ExecutionService getService() {
          return ExecutionService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        isStarted = true;
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        builder = new Notification.Builder(this);

        Intent stopSelf = new Intent(this, ExecutionService.class);
        stopSelf.setAction(STOP_ACTION);
        PendingIntent closePendingIntent = PendingIntent.getService(this, 0, stopSelf,PendingIntent.FLAG_CANCEL_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(null, null);
            if(notificationManager != null){
                notificationManager.createNotificationChannel(channel);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                builder.setChannelId(CHANNEL_ID);
                builder.setCategory(Notification.CATEGORY_SERVICE);
            }
        }

        builder.setSmallIcon(R.drawable.ic_time)
                .setContentTitle(getString(R.string.app_name))
                .addAction(R.drawable.ic_stop, getString(R.string.stop), closePendingIntent)
                .setVibrate(new long[]{0L})
                .setWhen(0);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeTimer();
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null)
            notificationManager.cancelAll();
        isStarted = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.getAction() != null && intent.getAction().equals(STOP_ACTION)) {
            stopForeground(true);
            stopSelf();
            isStarted = false;
        }

        return START_NOT_STICKY;
    }

    public void openTimer(int totalRest){
        if (ApplicationHelper.isTimerHeadEnabled(this) && (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this))) {
            timerWrapper = LayoutInflater.from(this).inflate(R.layout.timer_head, null);
            timerView = timerWrapper.findViewById(R.id.timer);
            timerView.setRest(workout.endTimer, totalRest);
            timerView.setOnTimerFinishedListener(new OnTimerFinishedListener() {
                @Override
                public void onTimerFinished() {
                    closeTimer();
                }
            });
            timerView.setSilent(true);
            timerView.start();
            timerHeaderVisible = true;

            int windowType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    windowType,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            params.gravity = Gravity.TOP | Gravity.START;
            params.x = 0;
            params.y = 100;

            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            if(windowManager != null)
                windowManager.addView(timerWrapper, params);

            ImageView closeView = timerWrapper.findViewById(R.id.close_timer);
            closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeTimer();
                }
            });

            timerWrapper.setOnTouchListener(new View.OnTouchListener() {
                long lastTouchDown;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = params.x;
                            initialY = params.y;

                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            lastTouchDown = System.currentTimeMillis();

                            return true;
                        case MotionEvent.ACTION_UP:
                            if ((event.getRawX() - initialTouchX) < 10 && (int) (event.getRawY() - initialTouchY) < 10) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        try{
                                            contentIntent.send();
                                        } catch (PendingIntent.CanceledException ignore){}
                                    }
                                }, 100);
                            }
                        return true;
                         case MotionEvent.ACTION_MOVE:
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);

                            windowManager.updateViewLayout(timerWrapper, params);
                            return true;
                    }
                    return false;
                }
            });
        }
    }

    public void closeTimer(){
        if (timerHeaderVisible && timerWrapper != null && windowManager != null){
            if(timerView != null)
                timerView.onDestroy();
            windowManager.removeView(timerWrapper);
            timerHeaderVisible = false;
        }
    }

    public void updateNotificationTitle(int rest){
        if(isStarted){
            builder.setContentText(getString(R.string.execution_rest, String.valueOf(rest)));
            builder.setOngoing(true);
            nm.notify(NOTIFICATION_ID, builder.build());
        }
    }

    public void setWorkout(WorkoutWithExercise workout, int fragmentToOpen) {
        if(isStarted){
            this.workout = workout;
            notificationIntent = new Intent(this, HomeActivity.class);
            notificationIntent.putExtra(FRAGMENT_TYPE_PARAMETER, fragmentToOpen);
            notificationIntent.putExtra(WORKOUT_PARAMETER, workout);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            contentIntent = PendingIntent.getActivity(this,5, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
            builder.setContentIntent(contentIntent);
            builder.setContentText("");
            builder.setOngoing(true);
            nm.notify(NOTIFICATION_ID, builder.build());
        }
    }

    public WorkoutWithExercise getWorkout() {
        return workout;
    }

    public long getStartTime() {
        return startTime;
    }
}

package com.jessy_barthelemy.strongify.database.asyncTask;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.Workout;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddNotificationTask {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final WeakReference<Context> weakContext;
    private final long workoutId;
    public static final String CHANNEL_ID = "notification_id";
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public AddNotificationTask(Context context, long workoutId) {
        this.weakContext = new WeakReference<>(context);
        this.workoutId = workoutId;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if (context == null) return;

            AppDatabase db = AppDatabase.getAppDatabase(context);
            Workout workout = db.workoutDao().get(workoutId);

            if (workout == null) return;

            // La création du NotificationChannel et la notification doivent se faire sur le thread principal
            mainHandler.post(() -> {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(
                            CHANNEL_ID,
                            context.getString(R.string.workout_creation_reminder),
                            NotificationManager.IMPORTANCE_HIGH);
                    notificationChannel.enableLights(true);
                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    notificationChannel.enableVibration(true);
                    notificationManager.createNotificationChannel(notificationChannel);
                }

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
                Intent contentIntent = new Intent(context, HomeActivity.class);
                PendingIntent contentPendingIntent = PendingIntent.getActivity(
                        context, 0, contentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_workout)
                        .setTicker(context.getString(R.string.app_name))
                        .setContentTitle(context.getString(R.string.workout_creation_reminder_notif, workout.name))
                        .setContentText(context.getString(R.string.app_name))
                        .setContentIntent(contentPendingIntent);

                notificationManager.notify(1, notificationBuilder.build());

                // Programmation de l’alarme (généralement OK depuis n’importe quel thread, mais on reste prudents)
                workout.scheduleAlarm(context, true);
            });
        });
    }
}

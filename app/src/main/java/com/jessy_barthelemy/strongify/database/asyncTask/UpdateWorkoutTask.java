package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.Workout;
import com.jessy_barthelemy.strongify.interfaces.WorkoutManager;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UpdateWorkoutTask {

    private WeakReference<Context> weakContext;
    private Workout workout;
    private WorkoutManager manager;

    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UpdateWorkoutTask(Context context, Workout workout, WorkoutManager manager){
        this.weakContext = new WeakReference<>(context);
        this.workout = workout;
        this.manager = manager;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if(context != null && workout != null){
                AppDatabase.getAppDatabase(context).workoutDao().update(workout);
                if(workout.reminderHour != null)
                    workout.scheduleAlarm(context, false);
                else
                    workout.cancelAlarm(context);

                if(manager != null){
                    mainHandler.post(() -> manager.onWorkoutChanged(context, workout));
                }
            }
        });
    }
}

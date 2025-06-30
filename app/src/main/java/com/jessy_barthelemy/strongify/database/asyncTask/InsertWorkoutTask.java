package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.Workout;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InsertWorkoutTask {

    private WeakReference<Context> weakContext;
    private Workout workout;
    private static final Executor executor = Executors.newSingleThreadExecutor();

    public InsertWorkoutTask(Context context, Workout workout){
        this.weakContext = new WeakReference<>(context);
        this.workout = workout;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if(context != null && workout != null){
                workout.id = AppDatabase.getAppDatabase(context).workoutDao().insert(workout);
                workout.scheduleAlarm(context, false);
            }
        });
    }
}

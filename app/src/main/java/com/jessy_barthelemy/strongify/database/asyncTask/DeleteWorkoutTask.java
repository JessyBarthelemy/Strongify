package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.Workout;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteWorkoutTask {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final WeakReference<Context> weakContext;
    private final Workout workout;

    public DeleteWorkoutTask(Context context, Workout workout){
        this.weakContext = new WeakReference<>(context);
        this.workout = workout;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if (context != null && workout != null) {
                AppDatabase.getAppDatabase(context).workoutDao().delete(workout);
                workout.cancelAlarm(context);
            }
        });
    }
}

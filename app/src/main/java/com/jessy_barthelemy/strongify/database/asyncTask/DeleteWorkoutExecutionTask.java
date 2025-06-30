package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.WorkoutExecution;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteWorkoutExecutionTask {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final WeakReference<Context> weakContext;
    private final WorkoutExecution execution;

    public DeleteWorkoutExecutionTask(Context context, WorkoutExecution execution){
        this.weakContext = new WeakReference<>(context);
        this.execution = execution;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if (context != null && execution != null) {
                AppDatabase.getAppDatabase(context).workoutExecutionDao().delete(execution);
            }
        });
    }
}

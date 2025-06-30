package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.Exercise;
import com.jessy_barthelemy.strongify.interfaces.OnExerciseDeletion;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DeleteExerciseTask {

    private final WeakReference<Context> weakContext;
    private final Exercise exercise;
    private final OnExerciseDeletion delegate;

    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public DeleteExerciseTask(Context context, Exercise exercise, OnExerciseDeletion delegate){
        this.weakContext = new WeakReference<>(context);
        this.exercise = exercise;
        this.delegate = delegate;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if(context != null && exercise != null) {
                AppDatabase.getAppDatabase(context).exerciseDao().delete(exercise);
            }

            // Callback sur le thread principal
            mainHandler.post(() -> {
                if (delegate != null) {
                    delegate.onExerciseDeleted();
                }
            });
        });
    }
}

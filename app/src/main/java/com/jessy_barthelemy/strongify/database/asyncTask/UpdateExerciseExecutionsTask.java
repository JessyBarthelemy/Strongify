package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.interfaces.OnExerciseChanged;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UpdateExerciseExecutionsTask {

    private final WeakReference<Context> weakContext;
    private final ExerciseWithSet exerciseWithSet;
    private final OnExerciseChanged listener;
    private final int position;

    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UpdateExerciseExecutionsTask(Context context, ExerciseWithSet exerciseWithSet, OnExerciseChanged listener, int position) {
        this.weakContext = new WeakReference<>(context);
        this.exerciseWithSet = exerciseWithSet;
        this.listener = listener;
        this.position = position;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if (context != null && exerciseWithSet != null) {
                AppDatabase db = AppDatabase.getAppDatabase(context);
                db.workoutExecutionDao().update(exerciseWithSet.workoutExecution);

                for (SetExecution set : exerciseWithSet.sets) {
                    if (set.id > 0) {
                        db.setDao().update(set);
                    } else {
                        set.id = db.setDao().insert(set);
                    }
                }
            }

            if(listener != null) {
                mainHandler.post(() -> listener.onExerciseAdded(exerciseWithSet, position));
            }
        });
    }
}

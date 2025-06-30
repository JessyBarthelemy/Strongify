package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.Superset;
import com.jessy_barthelemy.strongify.interfaces.CustomExerciseManager;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UpdateCustomExerciseTask {

    private final WeakReference<Context> weakContext;
    private final Superset superset;
    private final CustomExerciseManager manager;

    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UpdateCustomExerciseTask(Context context, Superset superset, CustomExerciseManager manager){
        this.weakContext = new WeakReference<>(context);
        this.superset = superset;
        this.manager = manager;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if(context != null && superset != null) {
                AppDatabase.getAppDatabase(context).supersetDao().update(superset);
            }
            // Post callback on main thread
            mainHandler.post(() -> {
                if(manager != null) {
                    manager.onCustomExerciseChanged(superset);
                }
            });
        });
    }
}

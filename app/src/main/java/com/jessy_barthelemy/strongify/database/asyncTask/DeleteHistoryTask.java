package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.WorkoutHistory;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteHistoryTask {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final WeakReference<Context> weakContext;
    private final WorkoutHistory history;

    public DeleteHistoryTask(Context context, WorkoutHistory history){
        this.weakContext = new WeakReference<>(context);
        this.history = history;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if (context != null && history != null) {
                AppDatabase db = AppDatabase.getAppDatabase(context);
                db.historyDao().delete(history);
                if (history.hId != null) {
                    db.exerciseHistoryDao().deleteByHistoryId(history.hId);
                }
            }
        });
    }
}

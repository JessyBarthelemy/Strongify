package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteSetExecutionTask {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final WeakReference<Context> weakContext;
    private final SetExecution setExecution;

    public DeleteSetExecutionTask(Context context, SetExecution setExecution){
        this.weakContext = new WeakReference<>(context);
        this.setExecution = setExecution;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if (context != null && setExecution != null) {
                AppDatabase.getAppDatabase(context).setDao().delete(setExecution);
            }
        });
    }
}

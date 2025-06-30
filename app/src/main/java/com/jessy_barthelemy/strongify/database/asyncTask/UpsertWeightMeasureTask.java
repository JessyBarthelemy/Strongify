package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.WeightMeasure;
import com.jessy_barthelemy.strongify.interfaces.OnMeasureChanged;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UpsertWeightMeasureTask {

    private WeakReference<Context> weakContext;
    private WeightMeasure weightMeasure;
    private OnMeasureChanged dispatcher;

    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UpsertWeightMeasureTask(Context context, WeightMeasure weightMeasure, OnMeasureChanged dispatcher){
        this.weakContext = new WeakReference<>(context);
        this.weightMeasure = weightMeasure;
        this.dispatcher = dispatcher;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if(context != null && weightMeasure != null){
                if(weightMeasure.id > 0) {
                    AppDatabase.getAppDatabase(context).weightMeasureDao().update(weightMeasure);
                } else {
                    weightMeasure.id = AppDatabase.getAppDatabase(context).weightMeasureDao().insert(weightMeasure);
                }
            }

            if(dispatcher != null){
                mainHandler.post(() -> dispatcher.onMeasureChanged());
            }
        });
    }
}

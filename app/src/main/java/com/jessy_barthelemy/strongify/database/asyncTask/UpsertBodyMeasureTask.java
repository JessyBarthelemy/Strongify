package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.BodyMeasurements;
import com.jessy_barthelemy.strongify.interfaces.OnMeasureChanged;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UpsertBodyMeasureTask {

    private WeakReference<Context> weakContext;
    private BodyMeasurements bodyMeasurements;
    private OnMeasureChanged dispatcher;

    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UpsertBodyMeasureTask(Context context, BodyMeasurements bodyMeasurements, OnMeasureChanged dispatcher){
        this.weakContext = new WeakReference<>(context);
        this.bodyMeasurements = bodyMeasurements;
        this.dispatcher = dispatcher;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if(context != null && bodyMeasurements != null){
                if(bodyMeasurements.id > 0) {
                    AppDatabase.getAppDatabase(context).bodyMeasurementsDao().update(bodyMeasurements);
                } else {
                    bodyMeasurements.id = AppDatabase.getAppDatabase(context).bodyMeasurementsDao().insert(bodyMeasurements);
                }
            }

            if(dispatcher != null){
                mainHandler.post(() -> dispatcher.onMeasureChanged());
            }
        });
    }
}

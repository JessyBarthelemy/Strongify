package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.BodyMeasurements;
import com.jessy_barthelemy.strongify.interfaces.OnMeasureChanged;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteBodyMeasureTask {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final WeakReference<Context> weakContext;
    private final BodyMeasurements measure;
    private final OnMeasureChanged delegate;

    public DeleteBodyMeasureTask(Context context, BodyMeasurements measure, OnMeasureChanged delegate) {
        this.weakContext = new WeakReference<>(context);
        this.measure = measure;
        this.delegate = delegate;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if (context == null || measure == null || measure.id <= 0) return;

            AppDatabase.getAppDatabase(context).bodyMeasurementsDao().delete(measure);
            measure.clear();

            mainHandler.post(() -> {
                if (delegate != null) {
                    delegate.onMeasureChanged();
                }
                Toast.makeText(context, context.getString(R.string.measure_removed), Toast.LENGTH_LONG).show();
            });
        });
    }
}

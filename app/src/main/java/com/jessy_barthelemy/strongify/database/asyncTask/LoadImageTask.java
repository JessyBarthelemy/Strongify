package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.entity.Exercise;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoadImageTask {
    private final int id;
    private final WeakReference<Context> contextWeakReference;
    private final WeakReference<ImageView> imageViewWeakReference;
    private final Exercise exercise;
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public LoadImageTask(Context context, Exercise exercise, int id, ImageView imageView){
        this.id = id;
        this.contextWeakReference = new WeakReference<>(context);
        this.imageViewWeakReference = new WeakReference<>(imageView);
        this.exercise = exercise;
    }

    public void execute() {
        executor.execute(() -> {
            Drawable drawable = null;
            Context context = contextWeakReference.get();

            if(context != null){
                if(exercise != null && !exercise.base){
                    if(exercise.path != null){
                        drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeFile(exercise.path));
                    }
                } else {
                    try {
                        drawable = context.getResources().getDrawable(id);
                    } catch (Resources.NotFoundException ignored) {}
                }
            }

            final Drawable finalDrawable = drawable;
            mainHandler.post(() -> {
                ImageView imageView = imageViewWeakReference.get();
                if(imageView != null){
                    if(finalDrawable != null){
                        imageView.setImageDrawable(finalDrawable);
                    } else {
                        imageView.setImageResource(R.drawable.select_image);
                    }
                }
            });
        });
    }
}

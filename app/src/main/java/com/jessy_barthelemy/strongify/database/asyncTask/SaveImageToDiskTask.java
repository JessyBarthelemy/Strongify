package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import com.jessy_barthelemy.strongify.interfaces.OnImageSavedToDisk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SaveImageToDiskTask {

    private final Bitmap image;
    private final WeakReference<Context> weakContext;
    private final String fileName;
    private final String oldFilePath;
    private final OnImageSavedToDisk delegate;

    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public SaveImageToDiskTask(Bitmap image, Context context, String fileName, String oldFilePath, OnImageSavedToDisk delegate){
        this.image = image;
        this.weakContext = new WeakReference<>(context);
        this.fileName = fileName;
        this.oldFilePath = oldFilePath;
        this.delegate = delegate;
    }

    public void execute() {
        executor.execute(() -> {
            String path = saveImage();
            mainHandler.post(() -> {
                if (delegate != null) {
                    delegate.onImageSaved(path);
                }
            });
        });
    }

    private String saveImage() {
        File cachedFile = getCacheFile();
        if (cachedFile == null || image == null) return null;

        try (FileOutputStream fos = new FileOutputStream(cachedFile)) {
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            if (oldFilePath != null) {
                File oldFile = new File(oldFilePath);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
            return cachedFile.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    private File getCacheFile() {
        Context context = weakContext.get();
        if (context == null) return null;

        int extensionIndex = this.fileName.indexOf('.');
        if (extensionIndex == -1) return null;

        String extension = fileName.substring(extensionIndex);
        String newFileName = UUID.randomUUID().toString() + extension;

        File[] dirs = ContextCompat.getExternalCacheDirs(context);
        File baseDir = (dirs != null && dirs.length > 0 && dirs[dirs.length - 1] != null)
                ? dirs[dirs.length - 1]
                : context.getExternalCacheDir();

        return baseDir != null ? new File(baseDir, newFileName) : null;
    }
}

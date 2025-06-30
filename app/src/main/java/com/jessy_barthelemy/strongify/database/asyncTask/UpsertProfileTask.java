package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.Profile;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UpsertProfileTask {

    private WeakReference<Context> weakContext;
    private Profile profile;

    private static final Executor executor = Executors.newSingleThreadExecutor();

    public UpsertProfileTask(Context context, Profile profile){
        this.weakContext = new WeakReference<>(context);
        this.profile = profile;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if(context != null && profile != null){
                if(profile.id > 0) {
                    AppDatabase.getAppDatabase(context).profileDao().update(profile);
                } else {
                    profile.id = AppDatabase.getAppDatabase(context).profileDao().insert(profile);
                }
            }
        });
    }
}

package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UpdateExercisesExecutionsTask {

    private WeakReference<Context> weakContext;
    private List<ExerciseWithSet> exerciseWithSets;
    private static final Executor executor = Executors.newSingleThreadExecutor();

    public UpdateExercisesExecutionsTask(Context context, List<ExerciseWithSet> exerciseWithSets){
        this.weakContext = new WeakReference<>(context);
        this.exerciseWithSets = exerciseWithSets;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if(context != null && exerciseWithSets != null){
                for(ExerciseWithSet exerciseWithSet : exerciseWithSets){
                    AppDatabase.getAppDatabase(context).workoutExecutionDao().update(exerciseWithSet.workoutExecution);
                }
            }
        });
    }
}

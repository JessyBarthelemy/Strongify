package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.entity.Superset;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.interfaces.CustomExerciseSetManager;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UpdateCustomExerciseSetTask {

    private WeakReference<Context> weakContext;
    private List<ExerciseWithSet> exercises;
    private CustomExerciseSetManager manager;
    private Superset superset;

    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UpdateCustomExerciseSetTask(Context context, List<ExerciseWithSet> exercises, Superset superset, CustomExerciseSetManager manager){
        this.weakContext = new WeakReference<>(context);
        this.exercises = exercises;
        this.manager = manager;
        this.superset = superset;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if(context == null) return;

            int size;
            for (int i = 0, len = exercises.size(); i < len; i++) {
                ExerciseWithSet exercise = exercises.get(i);
                if (exercise.workoutExecution.supersetId != null
                        && exercise.workoutExecution.supersetId.equals(superset.supersetId)) {

                    exercise.workoutExecution.supersetId = superset.supersetId;
                    exercise.setSuperset(superset);

                    size = exercise.sets.size();

                    if (superset.repetition > size) {
                        SetExecution last = exercise.sets.get(size - 1);
                        for (int j = size; j < superset.repetition; j++) {
                            SetExecution execution = last.clone();
                            execution.id = AppDatabase.getAppDatabase(context).setDao().insert(execution);
                            exercise.sets.add(execution);
                        }
                    } else if (superset.repetition < size) {
                        for (int j = size - 1; j >= superset.repetition; j--) {
                            AppDatabase.getAppDatabase(context).setDao().delete(exercise.sets.get(j));
                            exercise.sets.remove(j);
                        }
                    }
                }
            }

            if (manager != null) {
                mainHandler.post(() -> manager.onCustomExerciseSetChanged(exercises));
            }
        });
    }
}

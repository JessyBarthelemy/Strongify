package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.entity.Superset;
import com.jessy_barthelemy.strongify.database.entity.WorkoutExecution;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithGroup;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.enumeration.ExerciseType;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.interfaces.OnExerciseExecutionAdded;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddExercisesExecutions {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final WeakReference<Context> weakContext;
    private final List<ExerciseWithGroup> exercises;
    private final long workoutId;
    private final int lastOrder;
    private final int exerciseType;
    private final OnExerciseExecutionAdded notifier;
    private final Superset supersetSource;
    private final int supersetOrder;

    public AddExercisesExecutions(Context context,
                                  List<ExerciseWithGroup> exercises,
                                  long workoutId,
                                  int exerciseType,
                                  int lastOrder,
                                  OnExerciseExecutionAdded notifier,
                                  Superset supersetSource,
                                  int supersetOrder) {

        this.weakContext = new WeakReference<>(context);
        this.exercises = exercises;
        this.workoutId = workoutId;
        this.exerciseType = exerciseType;
        this.lastOrder = lastOrder;
        this.notifier = notifier;
        this.supersetSource = supersetSource;
        this.supersetOrder = supersetOrder;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if (context == null) return;

            AppDatabase db = AppDatabase.getAppDatabase(context);
            List<ExerciseWithSet> exerciseWithSets = new ArrayList<>();
            Superset superset = supersetSource;

            int currentOrder = lastOrder;
            int currentSupersetOrder = supersetOrder;

            if (exerciseType != ExerciseType.NORMAL && supersetSource == null) {
                superset = new Superset();
                superset.exerciseType = exerciseType;
                superset.repetition = 1;
                superset.rest = ApplicationHelper.DEFAULT_REST;
                superset.supersetId = db.supersetDao().insert(superset);
            }

            for (ExerciseWithGroup exerciseWithGroup : exercises) {
                WorkoutExecution execution = new WorkoutExecution();
                execution.workoutId = workoutId;
                execution.order = currentOrder;
                execution.exerciseId = exerciseWithGroup.exercise.eId;
                execution.supersetOrder = currentSupersetOrder;

                if (exerciseType != ExerciseType.NORMAL) {
                    currentSupersetOrder++;
                    execution.supersetId = superset.supersetId;
                } else {
                    currentOrder++;
                }

                long executionId = db.workoutExecutionDao().insert(execution);
                execution.executionId = executionId;

                SetExecution setExecution = ApplicationHelper.getDefaultSetExecution(context);
                setExecution.execId = executionId;
                setExecution.id = db.setDao().insert(setExecution);

                ExerciseWithSet exerciseWithSet = new ExerciseWithSet();
                exerciseWithSet.workoutExecution = execution;
                exerciseWithSet.setExercise(exerciseWithGroup.exercise);
                exerciseWithSet.sets = new ArrayList<>();
                exerciseWithSet.sets.add(setExecution);

                if (execution.supersetId != null) {
                    exerciseWithSet.setSuperset(superset);
                }

                exerciseWithSets.add(exerciseWithSet);
            }

            Superset finalSuperset = supersetSource; // important pour conserver la source originale
            mainHandler.post(() -> notifier.onExercisesAdded(exerciseWithSets, finalSuperset));
        });
    }
}

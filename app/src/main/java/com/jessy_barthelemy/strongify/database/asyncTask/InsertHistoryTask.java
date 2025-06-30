package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.ExerciseHistory;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.entity.WorkoutHistory;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InsertHistoryTask {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final WeakReference<Context> weakContext;
    private final WorkoutHistory history;
    private final WorkoutWithExercise workout;

    public InsertHistoryTask(Context context, WorkoutHistory history, WorkoutWithExercise workout) {
        this.weakContext = new WeakReference<>(context);
        this.history = history;
        this.workout = workout;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if (context != null && history != null) {
                history.hId = UUID.randomUUID().toString();
                AppDatabase.getAppDatabase(context).historyDao().insert(history);

                for (ExerciseWithSet exercise : workout.workoutExecution) {
                    int reps = 0;
                    double weight = 0;
                    for (SetExecution execution : exercise.sets) {
                        reps += execution.repsDone;
                        weight += execution.weightLifted;
                    }

                    ExerciseHistory exerciseHistory = new ExerciseHistory();
                    exerciseHistory.exerciseId = exercise.getExercise().eId;
                    exerciseHistory.historyId = history.hId;
                    exerciseHistory.repetitions = reps;
                    exerciseHistory.weight = weight;

                    AppDatabase.getAppDatabase(context).exerciseHistoryDao().insert(exerciseHistory);
                }
            }
        });
    }
}

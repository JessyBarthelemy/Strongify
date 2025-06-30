package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.Workout;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UpdateWorkoutsTask {

    private WeakReference<Context> weakContext;
    private List<Workout> workouts;
    private static final Executor executor = Executors.newSingleThreadExecutor();

    public UpdateWorkoutsTask(Context context, List<WorkoutWithExercise> workouts){
        this.weakContext = new WeakReference<>(context);

        this.workouts = new ArrayList<>();
        for(int i = 0, len = workouts.size(); i < len; i++){
            workouts.get(i).workout.order = i;
            this.workouts.add(workouts.get(i).workout);
        }
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if(context != null && workouts != null) {
                AppDatabase.getAppDatabase(context).workoutDao().update(workouts);
            }
        });
    }
}

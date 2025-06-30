package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.ExerciseEquipment;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteEquipementWorkoutTask {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final WeakReference<Context> weakContext;
    private final long exerciseId;
    private final long equipmentId;

    public DeleteEquipementWorkoutTask(Context context, long exerciseId, long equipmentId){
        this.weakContext = new WeakReference<>(context);
        this.exerciseId = exerciseId;
        this.equipmentId = equipmentId;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if (context != null) {
                ExerciseEquipment exerciseEquipment = new ExerciseEquipment();
                exerciseEquipment.exerciseId = exerciseId;
                exerciseEquipment.equipmentId = equipmentId;

                AppDatabase.getAppDatabase(context).exerciseEquipmentDao().delete(exerciseEquipment);
            }
        });
    }
}

package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;

import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.Equipment;
import com.jessy_barthelemy.strongify.database.entity.Exercise;
import com.jessy_barthelemy.strongify.database.entity.ExerciseEquipment;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddOrUpdateExerciseTask {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final WeakReference<Context> weakContext;
    private final Exercise exercise;
    private final List<Equipment> exerciseEquipments;
    private final List<Equipment> allEquipments;

    public AddOrUpdateExerciseTask(Context context, Exercise exercise,
                                   List<Equipment> exerciseEquipments,
                                   List<Equipment> allEquipments) {
        this.weakContext = new WeakReference<>(context);
        this.exercise = exercise;
        this.exerciseEquipments = exerciseEquipments;
        this.allEquipments = allEquipments;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if (context == null) return;

            AppDatabase db = AppDatabase.getAppDatabase(context);
            boolean creation = exercise.eId == 0;

            if (!creation) {
                db.exerciseDao().update(exercise);
            } else {
                long newId = db.exerciseDao().insert(exercise);
                exercise.eId = newId;
            }

            if (creation) {
                for (Equipment equipment : allEquipments) {
                    if (equipment.isSelected) {
                        ExerciseEquipment ee = new ExerciseEquipment();
                        ee.exerciseId = exercise.eId;
                        ee.equipmentId = equipment.equipmentId;
                        db.exerciseEquipmentDao().insert(ee);
                    }
                }
            } else {
                for (Equipment equipment : allEquipments) {
                    boolean isSelected = equipment.isSelected;
                    boolean wasSelected = exerciseEquipments.contains(equipment);

                    if (isSelected && !wasSelected) {
                        ExerciseEquipment ee = new ExerciseEquipment();
                        ee.exerciseId = exercise.eId;
                        ee.equipmentId = equipment.equipmentId;
                        db.exerciseEquipmentDao().insert(ee);
                    } else if (!isSelected && wasSelected) {
                        ExerciseEquipment ee = new ExerciseEquipment();
                        ee.exerciseId = exercise.eId;
                        ee.equipmentId = equipment.equipmentId;
                        db.exerciseEquipmentDao().delete(ee);
                    }
                }
            }
        });
    }
}

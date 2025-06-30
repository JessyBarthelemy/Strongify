package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.converter.PeriodicityConverter;
import com.jessy_barthelemy.strongify.database.entity.Exercise;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.entity.Superset;
import com.jessy_barthelemy.strongify.database.entity.Workout;
import com.jessy_barthelemy.strongify.database.entity.WorkoutExecution;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImportWorkoutTask {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final WeakReference<Context> weakContext;
    private final Uri path;

    public ImportWorkoutTask(Context context, Uri path) {
        this.weakContext = new WeakReference<>(context);
        this.path = path;
    }

    public void execute() {
        executor.execute(() -> {
            Context context = weakContext.get();
            if (context == null) return;

            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getContentResolver().openInputStream(path)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                showResult(false);
                return;
            }

            boolean success = true;
            HashMap<Long, Long> supersetEquivalenceIds = new HashMap<>();

            try {
                PeriodicityConverter converter = new PeriodicityConverter();
                JSONObject imported = new JSONObject(builder.toString());
                JSONObject workoutJson = imported.getJSONObject("workout");

                Workout workout = new Workout();
                workout.name = workoutJson.getString("name");
                workout.periodicity = converter.toOptionValuesList(workoutJson.getString("periodicity"));
                workout.order = workoutJson.getInt("order");
                workout.id = AppDatabase.getAppDatabase(context).workoutDao().insert(workout);

                if (imported.has("workoutExecution")) {
                    JSONArray exercisesWithSetJson = imported.getJSONArray("workoutExecution");
                    for (int i = 0; i < exercisesWithSetJson.length(); i++) {
                        JSONObject exerciseWithSetJson = exercisesWithSetJson.getJSONObject(i);
                        JSONObject workoutExecutionJson = exerciseWithSetJson.getJSONObject("workoutExecution");
                        JSONObject exerciseJson = exerciseWithSetJson.getJSONObject("exercise");
                        JSONObject supersetJson = exerciseWithSetJson.has("superset") ? exerciseWithSetJson.getJSONObject("superset") : null;

                        Exercise exercise = new Exercise();
                        exercise.base = exerciseJson.getBoolean("base");
                        if (!exercise.base) {
                            exercise.title = exerciseJson.getString("title");
                            exercise.description = exerciseJson.getString("description");
                            exercise.groupId = exerciseJson.getLong("groupId");
                            exercise.eId = AppDatabase.getAppDatabase(context).exerciseDao().insert(exercise);
                        } else {
                            exercise.eId = exerciseJson.getLong("eId");
                        }

                        WorkoutExecution execution = new WorkoutExecution();
                        if (supersetJson != null) {
                            Superset superset = new Superset();
                            superset.exerciseType = supersetJson.getInt("exerciseType");
                            superset.repetition = supersetJson.getInt("repetition");
                            superset.rest = supersetJson.getInt("rest");

                            long oldSuperset = supersetJson.getLong("supersetId");
                            if (supersetEquivalenceIds.containsKey(oldSuperset)) {
                                superset.supersetId = supersetEquivalenceIds.get(oldSuperset);
                            } else {
                                superset.supersetId = AppDatabase.getAppDatabase(context).supersetDao().insert(superset);
                                supersetEquivalenceIds.put(oldSuperset, superset.supersetId);
                            }
                            execution.supersetId = superset.supersetId;
                        }

                        execution.order = workoutExecutionJson.getInt("order");
                        execution.restTime = workoutExecutionJson.getInt("restTime");
                        execution.supersetOrder = workoutExecutionJson.getInt("supersetOrder");
                        execution.exerciseId = exercise.eId;
                        execution.workoutId = workout.id;
                        execution.executionId = AppDatabase.getAppDatabase(context).workoutExecutionDao().insert(execution);

                        if (exerciseWithSetJson.has("sets")) {
                            JSONArray setsJson = exerciseWithSetJson.getJSONArray("sets");
                            for (int j = 0; j < setsJson.length(); j++) {
                                JSONObject setJson = setsJson.getJSONObject(j);
                                SetExecution set = new SetExecution();
                                set.execId = execution.executionId;
                                set.reps = setJson.getInt("reps");
                                set.weight = setJson.getInt("weight");
                                set.rest = setJson.getInt("rest");
                                set.maxRepetition = setJson.getBoolean("maxRepetition");
                                set.exerciseSetType = setJson.getInt("exerciseSetType");
                                set.id = AppDatabase.getAppDatabase(context).setDao().insert(set);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                success = false;
            }

            showResult(success);
        });
    }

    private void showResult(boolean success) {
        Context context = weakContext.get();
        if (context == null) return;

        mainHandler.post(() -> {
            if (success) {
                Toast.makeText(context, R.string.workout_import_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.workout_import_error, Toast.LENGTH_LONG).show();
            }
        });
    }
}

package com.jessy_barthelemy.strongify.database.asyncTask;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.util.List;

public class RescheduleAlarmTask {

    private WeakReference<Context> weakContext;

    public RescheduleAlarmTask(Context context){
        this.weakContext = new WeakReference<>(context);
    }

    public void execute() {
        Context context = weakContext.get();

        List<Workout> workoutList = AppDatabase.getAppDatabase(context).workoutDao().getAll();
        for(Workout workout : workoutList){
            workout.scheduleAlarm(context, false);
        }
    }
}

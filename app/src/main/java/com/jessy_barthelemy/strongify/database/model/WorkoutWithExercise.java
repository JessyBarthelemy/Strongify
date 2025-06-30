package com.jessy_barthelemy.strongify.database.model;


import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.entity.Workout;
import com.jessy_barthelemy.strongify.database.entity.WorkoutExecution;
import com.jessy_barthelemy.strongify.database.entity.WorkoutHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class WorkoutWithExercise implements Serializable {
    @Embedded
    public Workout workout;

    @Embedded
    public WorkoutHistory history;

    @Relation(entity = WorkoutExecution.class, entityColumn = "workoutId", parentColumn = "id")
    public List<ExerciseWithSet> workoutExecution;

    @Ignore
    public int currentExercise;

    @Ignore
    public String note;

    @Ignore
    public long endTimer;

    @Ignore
    public long startTime;

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();

        result.put("workout", workout.toJSON());
        if(history != null)
            result.put("history", history.toJSON());
        result.put("currentExercise", currentExercise);

        if(workoutExecution != null){
            JSONArray exercises = new JSONArray();
            for(ExerciseWithSet exercise : workoutExecution){
                exercises.put(exercise.toJSON());
            }

            result.put("workoutExecution", exercises);
        }

        return result;
    }

    public ExerciseWithSet getCurrentExercise(){
        return workoutExecution.get(currentExercise);
    }

    public boolean isPlayable(){
        if(workoutExecution == null || workoutExecution.size() == 0)
            return false;

        for(ExerciseWithSet exercise : workoutExecution){
            if(exercise.sets == null || exercise.sets.size() == 0)
                return false;
        }

        return true;
    }
}
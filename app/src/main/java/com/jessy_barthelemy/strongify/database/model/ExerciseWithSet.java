package com.jessy_barthelemy.strongify.database.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.jessy_barthelemy.strongify.database.entity.Exercise;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.entity.Superset;
import com.jessy_barthelemy.strongify.database.entity.WorkoutExecution;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExerciseWithSet implements Comparable<ExerciseWithSet>, Serializable{
    @Embedded
    public WorkoutExecution workoutExecution;
    @Relation(parentColumn = "exerciseId", entityColumn = "eId")
    public List<Exercise> exercise;
    @Relation(parentColumn = "supersetId", entityColumn = "supersetId")
    public List<Superset> superset;

    @Relation(entityColumn = "execId", parentColumn = "executionId")
    public List<SetExecution> sets;

    @Override
    public int compareTo(@NonNull ExerciseWithSet o) {

        int result = Double.compare( workoutExecution.order, o.workoutExecution.order);
        if(result == 0)
            result = Double.compare( workoutExecution.supersetOrder, o.workoutExecution.supersetOrder);
        return result ;
    }

    @Ignore
    public int currentSet;

    @Ignore
    public int exercisePosition;

    public Superset getSuperset(){
        if(superset != null && superset.size() > 0)
            return superset.get(0);
        return null;
    }

    public void setSuperset(Superset superset){
        if(this.superset != null && this.superset.size() > 0)
            this.superset.set(0, superset);
        else{
            this.superset = new ArrayList<>();
            this.superset.add(superset);
        }
    }

    public Exercise getExercise(){
        if(exercise != null && exercise.size() > 0)
            return exercise.get(0);
        return null;
    }

    public void setExercise(Exercise exercise){
        if(this.exercise != null && this.exercise.size() > 0)
            this.exercise.set(0, exercise);
        else{
            this.exercise = new ArrayList<>();
            this.exercise.add(exercise);
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();

        result.put("workoutExecution", workoutExecution.toJSON());
        result.put("exercise", getExercise().toJSON());
        if(getSuperset() != null)
            result.put("superset", getSuperset().toJSON());

        JSONArray executions = new JSONArray();
        for(SetExecution execution : sets){
            executions.put(execution.toJSON());
        }

        result.put("sets", executions);

        return result;
    }
}

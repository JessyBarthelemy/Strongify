package com.jessy_barthelemy.strongify.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Entity(foreignKeys = @ForeignKey(entity =  WorkoutExecution.class,
                                  parentColumns = "executionId",
                                  childColumns = "execId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(name = "IndexExecId", value = "execId")})
public class SetExecution implements Cloneable, Serializable{
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long execId;
    public int reps;
    public double weight;
    public int rest;
    public boolean maxRepetition;

    @Ignore
    public boolean isNew;

    public int exerciseSetType;

    @Override
    public SetExecution clone(){
        SetExecution execution = new SetExecution();
        execution.execId = execId;
        execution.reps = reps;
        execution.weight = weight;
        execution.rest = rest;
        execution.maxRepetition = maxRepetition;
        execution.isNew = true;

        return execution;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("execId", execId);
        result.put("reps", reps);
        result.put("weight", weight);
        result.put("rest", rest);
        result.put("maxRepetition", maxRepetition);
        result.put("exerciseSetType", exerciseSetType);

        return result;
    }

    @Ignore
    public int repsDone;
    @Ignore
    public double weightLifted;
}

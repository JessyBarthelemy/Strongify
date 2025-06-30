package com.jessy_barthelemy.strongify.database.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Entity(foreignKeys = {@ForeignKey(entity = Workout.class,
                                  childColumns = "workoutId",
                                  parentColumns = "id",
                                  onDelete = ForeignKey.CASCADE),
                        @ForeignKey(entity = Exercise.class,
                                childColumns = "exerciseId",
                                parentColumns = "eId",
                                onDelete = ForeignKey.CASCADE)},
        indices = {@Index(name = "IndexWorkoutId", value = "workoutId"),
                   @Index(name = "IndexExerciseId", value = "exerciseId")})
public class WorkoutExecution implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long executionId;
    public long workoutId;

    public int order;
    public int restTime;
    public int supersetOrder;
    public long exerciseId;
    @Nullable
    public Long supersetId;

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("executionId", executionId);
        result.put("workoutId", workoutId);
        result.put("order", order);
        result.put("restTime", restTime);
        result.put("supersetOrder", supersetOrder);
        result.put("exerciseId", exerciseId);
        result.put("supersetId", supersetId);

        return result;
    }
}

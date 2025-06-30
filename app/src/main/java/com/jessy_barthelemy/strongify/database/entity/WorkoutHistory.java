package com.jessy_barthelemy.strongify.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import com.jessy_barthelemy.strongify.database.converter.DateConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

@Entity(primaryKeys={"workoutId","date"},
        foreignKeys = @ForeignKey(entity = Workout.class,
                parentColumns = "id",
                childColumns = "workoutId",
                onDelete = ForeignKey.CASCADE))
public class WorkoutHistory implements Serializable {
    //nullable to keep compatibility with previous version
    @Nullable
    public String hId;

    public long workoutId;
    public int duration;
    public String title;
    public String text;
    @TypeConverters({DateConverter.class})
    @NonNull
    public Date date = new Date();

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("workoutId", workoutId);
        result.put("duration", duration);
        result.put("title", title);
        result.put("text", text);
        result.put("date", DateConverter.fromDate(date));

        return result;
    }
}

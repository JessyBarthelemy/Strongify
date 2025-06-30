package com.jessy_barthelemy.strongify.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Entity
public class ExerciseGroup implements Comparable<ExerciseGroup>, Serializable{
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String label;
    public String groupIcon;
    public int sortOrder;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ExerciseGroup)
            return id == ((ExerciseGroup)obj).id;
        else
            return super.equals(obj);
    }

    @Override
    public int compareTo(@NonNull ExerciseGroup e) {
        return Double.compare(sortOrder, e.sortOrder);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("label", label);
        result.put("groupIcon", groupIcon);
        result.put("sortOrder", sortOrder);

        return result;
    }
}

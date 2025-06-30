package com.jessy_barthelemy.strongify.database.entity;

import androidx.room.Entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Entity(primaryKeys = { "equipmentId", "exerciseId" })
public class ExerciseEquipment implements Serializable {
    public long equipmentId;
    public long exerciseId;

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("equipmentId", equipmentId);
        result.put("exerciseId", exerciseId);

        return result;
    }
}
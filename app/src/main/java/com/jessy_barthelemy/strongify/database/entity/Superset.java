package com.jessy_barthelemy.strongify.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Entity
public class Superset implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long supersetId;

    public int exerciseType;

    public int repetition;

    public int rest;

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("supersetId", supersetId);
        result.put("exerciseType", exerciseType);
        result.put("repetition", repetition);
        result.put("rest", rest);

        return result;
    }
}

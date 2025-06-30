package com.jessy_barthelemy.strongify.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Entity
public class Equipment implements Cloneable, Serializable{
    @PrimaryKey(autoGenerate = true)
    public long equipmentId;
    public String title;
    public boolean base;
    public boolean isSelected;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Equipment){
            Equipment equipment = (Equipment) obj;
            return equipment.equipmentId == equipmentId;
        }
        return super.equals(obj);
    }

    @Override
    public Equipment clone(){
        Equipment equipment = new Equipment();
        equipment.equipmentId = equipmentId;
        equipment.title = title;
        equipment.base = base;
        equipment.isSelected = isSelected;
        return equipment;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("equipmentId", equipmentId);
        result.put("title", title);
        result.put("base", base);

        return result;
    }
}

package com.jessy_barthelemy.strongify.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Entity(foreignKeys = @ForeignKey(entity =  ExerciseGroup.class,
        parentColumns = "id",
        childColumns = "groupId",
        onDelete = ForeignKey.CASCADE),
        indices = {@Index(name = "IndexGroupId", value = "groupId")})
public class Exercise implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long eId;
    public String title;
    //if starting with @ then it references another exercise
    public String description;

    public long groupId;

    //true if the exercise if a pre-installed exercise
    public boolean base;

    public String path;

    @Ignore
    public int iconId;

    @Ignore
    public String translatedTitle;

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("eId", eId);
        result.put("title", title);
        result.put("description", description);
        result.put("groupId", groupId);
        result.put("base", base);

        return result;
    }
}

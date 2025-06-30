package com.jessy_barthelemy.strongify.database.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;

import com.jessy_barthelemy.strongify.database.entity.Exercise;
import com.jessy_barthelemy.strongify.database.entity.ExerciseGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;

public class ExerciseWithGroup implements Comparable<ExerciseWithGroup>{
    @Embedded
    public Exercise exercise;

    @Embedded
    public ExerciseGroup group;

    @Ignore
    public boolean selected;

    @Override
    public int compareTo(@NonNull ExerciseWithGroup e) {
        int result = group.label.compareTo(e.group.label);

        if(result == 0)
            result = Normalizer.normalize(exercise.translatedTitle, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                    .compareTo(Normalizer.normalize(e.exercise.translatedTitle, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""));

        return result;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("exercise", exercise.toJSON());
        result.put("group", group.toString());

        return result;
    }
}

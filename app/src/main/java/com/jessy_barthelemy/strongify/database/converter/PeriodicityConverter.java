package com.jessy_barthelemy.strongify.database.converter;

import androidx.room.TypeConverter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PeriodicityConverter {
    @TypeConverter
    public String fromOptionValuesList(List<Integer> optionValues) {
        if (optionValues == null)
            return (null);

        JSONArray json = new JSONArray();
        for(Integer value : optionValues)
            json.put(value);

        return json.toString();
    }

    @TypeConverter
    public List<Integer> toOptionValuesList(String optionValuesString) {
        if (optionValuesString == null)
            return (null);

        List<Integer> result = new ArrayList<>();
        try {
            JSONArray json = new JSONArray(optionValuesString);
            for(int i = 0; i < json.length(); i++){
                result.add(Integer.parseInt(json.get(i).toString()));
            }

            return result;
        } catch (JSONException e) {
            return null;
        }
    }
}

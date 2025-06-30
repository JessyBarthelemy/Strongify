package com.jessy_barthelemy.strongify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;

import java.util.List;

public class SimpleExercisesAdapter extends ArrayAdapter<ExerciseWithSet> {
    List<ExerciseWithSet> exercises;
    private Context ctx;

    public SimpleExercisesAdapter(Context ctx, List<ExerciseWithSet> exercises){
        super(ctx, 0, exercises);
        this.exercises = exercises;
        this.ctx = ctx;
    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(ctx).inflate(android.R.layout.simple_list_item_1, parent,false);

        ExerciseWithSet currentExercise = exercises.get(position);

        TextView name = listItem.findViewById(android.R.id.text1);
        name.setText(ApplicationHelper.getExerciseTitle(ctx, currentExercise.getExercise().title, currentExercise.getExercise().base));

        return listItem;
    }
}

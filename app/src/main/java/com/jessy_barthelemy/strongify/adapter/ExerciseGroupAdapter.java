package com.jessy_barthelemy.strongify.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.entity.ExerciseGroup;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;

import java.util.List;

public class ExerciseGroupAdapter implements SpinnerAdapter {

    private Context context;
    private List<ExerciseGroup> data;
    private int padding;

    public ExerciseGroupAdapter(Context context, List<ExerciseGroup> data){
        this.context = context;
        this.data = data;
        padding = (int)context.getResources().getDimension(R.dimen.little_margin);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {}

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ExerciseGroup getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).id;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(context, R.layout.spinner_exercise_group, null);
        ((TextView)convertView).setText(ApplicationHelper.getTranslation(context, data.get(position).label));
        return convertView;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(context, R.layout.spinner_exercise_group, null);
        ((TextView)convertView).setText(ApplicationHelper.getTranslation(context, data.get(position).label));
        convertView.setPadding(0, padding,0, padding);
        return convertView;
    }

    public List<ExerciseGroup> getData() {
        return data;
    }
}
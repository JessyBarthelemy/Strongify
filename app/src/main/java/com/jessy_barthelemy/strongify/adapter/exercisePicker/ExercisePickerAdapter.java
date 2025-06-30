package com.jessy_barthelemy.strongify.adapter.exercisePicker;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.asyncTask.LoadImageTask;
import com.jessy_barthelemy.strongify.database.entity.ExerciseGroup;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithGroup;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class ExercisePickerAdapter extends BaseExpandableListAdapter implements Filterable {

    private Context context;
    private List<ExerciseGroup> groupsFiltered;
    private List<ExerciseGroup> groups;
    private List<List<ExerciseWithGroup>> dataSetFiltered;
    private List<List<ExerciseWithGroup>> dataSet;

    private int listItemSelectedColor;
    private int backgroundColor;

    public ExercisePickerAdapter(Context context, List<ExerciseGroup> groups, List<List<ExerciseWithGroup>> dataSet) {
        this.context = context;

        this.groupsFiltered = groups;
        this.dataSetFiltered = dataSet;

        this.groups = new ArrayList<>();
        this.groups.addAll(groupsFiltered);

        this.dataSet = new ArrayList<>();
        this.dataSet.addAll(dataSetFiltered);

        loadIconId();

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.colorListItemSelected, typedValue, true);
        listItemSelectedColor = typedValue.data;

        theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true);
        backgroundColor = typedValue.data;
    }

    private void loadIconId(){
        for(int i = dataSetFiltered.size() -1; i >= 0; i--){
           for(int j = dataSetFiltered.get(i).size() -1; j >= 0; j--){
                dataSetFiltered.get(i).get(j).exercise.iconId = ApplicationHelper.getDrawable(context, "th_" + dataSetFiltered.get(i).get(j).exercise.title);
            }
        }
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.dataSetFiltered.get(listPosition).get(expandedListPosition);
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ExercisePickerChildViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(layoutInflater == null)
                return null;
            convertView = layoutInflater.inflate(R.layout.row_exercise_picker, null);

            viewHolder = new ExercisePickerChildViewHolder();
            viewHolder.title = convertView.findViewById(R.id.exercise_title);
            viewHolder.icon = convertView.findViewById(R.id.exercise_image);
            viewHolder.tick = convertView.findViewById(R.id.exercise_validation);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ExercisePickerChildViewHolder) convertView.getTag();
        }

        if(listPosition <= (this.dataSetFiltered.size() -1) && expandedListPosition <= (this.dataSetFiltered.get(listPosition).size() -1)) {
            final ExerciseWithGroup exercise = this.dataSetFiltered.get(listPosition).get(expandedListPosition);
            new LoadImageTask(context, exercise.exercise, exercise.exercise.iconId, viewHolder.icon).execute();
            viewHolder.title.setText(ApplicationHelper.getExerciseTitle(context, exercise.exercise.title, exercise.exercise.base));

            if (exercise.selected) {
                viewHolder.tick.setVisibility(View.VISIBLE);
                convertView.setBackgroundColor(listItemSelectedColor);
            } else {
                viewHolder.tick.setVisibility(View.GONE);
                convertView.setBackgroundColor(backgroundColor);
            }
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.dataSetFiltered.get(listPosition).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.groupsFiltered.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.groupsFiltered.size();
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(layoutInflater == null)
                return null;
            convertView = layoutInflater.inflate(R.layout.row_exercise_picker_group, null);
        }

        if(listPosition <= (this.groupsFiltered.size() -1)){
            ExerciseGroup group = this.groupsFiltered.get(listPosition);
            if(group != null){
                TextView title = convertView.findViewById(R.id.exercise_group_title);
                title.setText(ApplicationHelper.getTranslation(context, group.label));

                ImageView icon = convertView.findViewById(R.id.exercise_group_icon);
                int resId = context.getResources().getIdentifier(group.groupIcon, "drawable", context.getPackageName());
                icon.setImageResource(resId);
            }
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getGroupId(int listPosition) {
        return (listPosition >= (groupsFiltered.size() - 1) ) ? 0 : groupsFiltered.get(listPosition).id;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return (listPosition >= (dataSetFiltered.size() - 1) || expandedListPosition >= (dataSetFiltered.get(listPosition).size() -1)) ? 0 : dataSetFiltered.get(listPosition).get(expandedListPosition).exercise.eId;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    public List<List<ExerciseWithGroup>> getDataSetFiltered(){
        return dataSetFiltered;
    }

    public List<List<ExerciseWithGroup>> getDataSet(){
        return dataSet;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                constraint = constraint.toString().toLowerCase();
                String constraintStr = constraint.toString();
                if (constraint.length() > 0) {
                    for (int i = dataSetFiltered.size() -1; i >= 0 ; i--) {
                        for (int j = dataSetFiltered.get(i).size() -1; j >= 0; j--) {
                            if (!Normalizer.normalize(dataSetFiltered.get(i).get(j).exercise.translatedTitle.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").startsWith(constraintStr)) {
                                dataSetFiltered.get(i).remove(j);
                            }
                        }

                        if(dataSetFiltered.get(i).size() == 0){
                            dataSetFiltered.remove(i);
                            groupsFiltered.remove(i);
                        }

                    }

                    results.count = dataSetFiltered.size();
                    results.values = dataSetFiltered;
                }else{
                    results.count = dataSet.size();
                    results.values = dataSet;
                    groupsFiltered.clear();
                    groupsFiltered.addAll(groups);
                }

                return results;
            }
        };
    }

    public void setDataSet(List<List<ExerciseWithGroup>> dataSet){
        this.dataSetFiltered = dataSet;
    }

    public void setGroups(List<ExerciseGroup> groups){
        this.groupsFiltered = groups;
    }

    public void clearFilter(){
        this.dataSetFiltered.clear();
        this.groupsFiltered.clear();

        for(int i = 0, len = dataSet.size(); i < len; i++){
            List<ExerciseWithGroup> exercises = new ArrayList<>(dataSet.get(i));
            dataSetFiltered.add(exercises);
        }

        this.groupsFiltered.addAll(groups);
    }
}
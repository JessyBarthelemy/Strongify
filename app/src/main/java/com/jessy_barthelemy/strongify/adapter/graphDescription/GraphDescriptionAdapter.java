package com.jessy_barthelemy.strongify.adapter.graphDescription;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.pojo.DoubleMeasures;
import com.jessy_barthelemy.strongify.database.pojo.Measures;
import com.jessy_barthelemy.strongify.interfaces.Refreshable;

import java.text.DecimalFormat;
import java.util.List;

public class GraphDescriptionAdapter extends ArrayAdapter<DoubleMeasures>{

    private DecimalFormat decimalFormat;
    private Refreshable delegate;
    private boolean sumEnabled;
    private boolean isDoubleMeasure;

    // View lookup cache
    private static class ViewHolder {
        CheckBox selected;
        TextView name;
        TextView minLeft;
        TextView maxLeft;
        TextView averageLeft;
        TextView sumLeft;
        TextView minRight;
        TextView maxRight;
        TextView averageRight;
        TextView sumRight;
    }

    public GraphDescriptionAdapter(Context context, List<DoubleMeasures> data) {
        super(context, 0, data);
        decimalFormat = new DecimalFormat("#.##");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final DoubleMeasures measures = getItem(position);

        if(measures == null || measures.getLeftMeasures() == null)
            return convertView;

        isDoubleMeasure = measures.getRightMeasures() != null;
        ViewHolder viewHolder;

        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if(isDoubleMeasure){
            convertView = inflater.inflate(R.layout.row_graph_description_double, parent, false);
            viewHolder.minRight = convertView.findViewById(R.id.graph_description_min_right);
            viewHolder.maxRight = convertView.findViewById(R.id.graph_description_max_right);
            viewHolder.averageRight = convertView.findViewById(R.id.graph_description_average_right);
            viewHolder.sumRight = convertView.findViewById(R.id.graph_description_sum_right);
        }else{
            convertView = inflater.inflate(R.layout.row_graph_description, parent, false);
        }

        viewHolder.name = convertView.findViewById(R.id.graph_description_name);
        viewHolder.minLeft = convertView.findViewById(R.id.graph_description_min_left);
        viewHolder.maxLeft = convertView.findViewById(R.id.graph_description_max_left);
        viewHolder.averageLeft = convertView.findViewById(R.id.graph_description_average_left);
        viewHolder.sumLeft = convertView.findViewById(R.id.graph_description_sum_left);
        viewHolder.selected = convertView.findViewById(R.id.graph_description_checkbox);


        viewHolder.name.setText(measures.getName());
        viewHolder.name.setTextColor(measures.getLeftMeasures().getColor());

        if(isDoubleMeasure){
            viewHolder.minRight.setText(String.valueOf(measures.getRightMeasures().getMin()));
            viewHolder.maxRight.setText(String.valueOf(measures.getRightMeasures().getMax()));
            viewHolder.averageRight.setText(decimalFormat.format(measures.getRightMeasures().getAverage()));
            if(sumEnabled)
                viewHolder.sumRight.setText(decimalFormat.format(measures.getRightMeasures().getSum()));
            else
                viewHolder.sumRight.setText("-");
        }

        viewHolder.minLeft.setText(String.valueOf(measures.getLeftMeasures().getMin()));
        viewHolder.maxLeft.setText(String.valueOf(measures.getLeftMeasures().getMax()));
        viewHolder.averageLeft.setText(decimalFormat.format(measures.getLeftMeasures().getAverage()));
        if(sumEnabled)
            viewHolder.sumLeft.setText(decimalFormat.format(measures.getLeftMeasures().getSum()));
        else
            viewHolder.sumLeft.setText("-");

        viewHolder.selected.setOnCheckedChangeListener(null);
        viewHolder.selected.setChecked(measures.isActive());

        viewHolder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                measures.setActive(isChecked);

                if(delegate != null)
                    delegate.onRefresh();
            }
        });

        return convertView;
    }

    public void setSumEnabled(boolean sumEnabled) {
        this.sumEnabled = sumEnabled;
    }

    public void setDelegate(Refreshable delegate) {
        this.delegate = delegate;
    }
}

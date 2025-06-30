package com.jessy_barthelemy.strongify.adapter.periodicity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.pojo.Periodicity;
import com.jessy_barthelemy.strongify.interfaces.OnPeriodicityChanged;

import java.util.List;

public class PeriodicityAdapter extends RecyclerView.Adapter<PeriodicityViewHolder>{
    private List<Periodicity> periodicity;
    private OnPeriodicityChanged onPeriodicityChanged;

    public PeriodicityAdapter(List<Periodicity> periodicity){
        this.periodicity = periodicity;
    }

    @NonNull
    @Override
    public PeriodicityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_periodicity, parent, false);
        return new PeriodicityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PeriodicityViewHolder holder, int position) {
        holder.bindPeriodicity(periodicity.get(position));
        holder.getCheck().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                periodicity.get(holder.getAdapterPosition()).setChecked(isChecked);
                if(onPeriodicityChanged != null)
                    onPeriodicityChanged.onPeriodicityChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return periodicity.size();
    }

    public void setOnPeriodicityChanged(OnPeriodicityChanged onPeriodicityChanged){
        this.onPeriodicityChanged = onPeriodicityChanged;
    }

    public String getPeriodicityString(){
        StringBuilder builder = new StringBuilder();
        boolean showSeparator = false;
        for(int i = 0, len = periodicity.size(); i < len; i++){
            if(periodicity.get(i).isChecked()){
                if(showSeparator){
                    builder.append(", ");
                }

                builder.append(periodicity.get(i).getDay());
                showSeparator = true;
            }
        }

        return builder.toString();
    }
}

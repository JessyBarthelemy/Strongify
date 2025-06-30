package com.jessy_barthelemy.strongify.adapter.equipment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.entity.Equipment;

import java.util.List;

public class EquipmentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Equipment> dataSet;
    private Context context;

    public EquipmentListAdapter(List<Equipment> dataSet, Context context) {
        this.dataSet = dataSet;
        this.setHasStableIds(true);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_equipment, parent, false);
        return new EquipmentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((EquipmentViewHolder)viewHolder).bindExercise(this.dataSet.get(position), context);
    }

    @Override
    public int getItemCount() {
        return this.dataSet.size();
    }

    @Override
    public long getItemId(int position) {
        return dataSet.get(position).equipmentId;
    }
}
package com.jessy_barthelemy.strongify.adapter.setHistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;

import java.util.List;

public class SetHistoryListAdapter extends RecyclerView.Adapter<SetHistoryViewHolder>{
    private List<ExerciseWithSet> dataSet;
    private Context context;
    private int weightUnitId;

    public SetHistoryListAdapter(Context context, List<ExerciseWithSet> dataSet, int weightUnitId) {
        this.dataSet = dataSet;
        this.context = context;
        this.weightUnitId = weightUnitId;
    }

    @NonNull
    @Override
    public SetHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_execution_history, parent, false);
        return new SetHistoryViewHolder(context, v, weightUnitId);
    }

    @Override
    public void onBindViewHolder(@NonNull SetHistoryViewHolder viewHolder, int position) {
        viewHolder.bindSet(this.dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return this.dataSet.size();
    }
}
package com.jessy_barthelemy.strongify.adapter.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.asyncTask.DeleteHistoryTask;
import com.jessy_barthelemy.strongify.database.entity.WorkoutHistory;
import com.jessy_barthelemy.strongify.interfaces.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.List;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryViewHolder> implements ItemTouchHelperAdapter {
    private List<WorkoutHistory> dataSet;
    private List<WorkoutHistory> dataSetToRemove;
    private Context context;

    public HistoryListAdapter(Context context, List<WorkoutHistory> dataSet) {
        this.dataSet = dataSet;
        this.dataSetToRemove = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history, parent, false);
        return new HistoryViewHolder(context, v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder viewHolder, int position) {
        viewHolder.bindHistory(this.dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return this.dataSet.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {}

    @Override
    public void onItemDismiss(final RecyclerView view, final int position) {
        final WorkoutHistory history = this.dataSet.get(position);
        Snackbar snackbar = Snackbar.make(view, R.string.history_list_remove_title, Snackbar.LENGTH_SHORT)
                .setAction(R.string.workout_list_remove_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dataSet.add(position, history);
                        dataSetToRemove.remove(history);
                        notifyItemInserted(position);
                    }
                });
        snackbar.show();

        snackbar.addCallback(new Snackbar.Callback(){
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if(dataSetToRemove.size() > 0) {
                    if(context != null) {
                        new DeleteHistoryTask(context, dataSetToRemove.get(0)).execute();
                        dataSetToRemove.remove(0);
                    }
                }
            }
        });

        dataSet.remove(position);
        dataSetToRemove.add(history);
        notifyItemRemoved(position);
    }

    @Override
    public boolean isSwipeEnabled() {
        return true;
    }

    public void setDataSet(List<WorkoutHistory> dataSet) {
        this.dataSet = dataSet;
    }
}
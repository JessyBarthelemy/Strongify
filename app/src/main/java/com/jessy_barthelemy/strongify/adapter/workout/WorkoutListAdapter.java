package com.jessy_barthelemy.strongify.adapter.workout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.asyncTask.DeleteWorkoutTask;
import com.jessy_barthelemy.strongify.database.entity.Workout;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;
import com.jessy_barthelemy.strongify.interfaces.ItemTouchHelperAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkoutListAdapter extends RecyclerView.Adapter<WorkoutViewHolder> implements ItemTouchHelperAdapter {
    private List<WorkoutWithExercise> dataSet;
    private List<Workout> dataSetToRemove;
    private WeakReference<Context> weakContext;

    public WorkoutListAdapter(Context context, List<WorkoutWithExercise> dataSet) {
        this.dataSet = dataSet;
        this.dataSetToRemove = new ArrayList<>();
        this.weakContext = new WeakReference<>(context);
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_workout, parent, false);
        return new WorkoutViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder viewHolder, int position) {
        viewHolder.bindWorkout(this.dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return this.dataSet.size();
    }

    @Override
    public void onItemDismiss(final RecyclerView view, final int position) {
        final WorkoutWithExercise workout = this.dataSet.get(position);
        Snackbar snackbar = Snackbar.make(view, R.string.workout_list_remove_title, Snackbar.LENGTH_SHORT)
                .setAction(R.string.workout_list_remove_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dataSet.add(position, workout);
                        dataSetToRemove.remove(workout.workout);
                        notifyItemInserted(position);
                    }
                });
        snackbar.show();

        snackbar.addCallback(new Snackbar.Callback(){
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if(dataSetToRemove.size() > 0) {
                    Context context = weakContext.get();
                    if(context != null) {
                        new DeleteWorkoutTask(context, dataSetToRemove.get(0)).execute();
                        dataSetToRemove.remove(0);
                    }
                }
            }
        });

        dataSet.remove(position);
        dataSetToRemove.add(workout.workout);
        notifyItemRemoved(position);
    }

    @Override
    public boolean isSwipeEnabled() {
        return true;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
       if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(this.dataSet, i, i + 1);
            }
       } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(this.dataSet, i, i - 1);
            }
       }

       notifyItemMoved(fromPosition, toPosition);
    }

    public void setDataSet(List<WorkoutWithExercise> dataSet) {
        this.dataSet = dataSet;
    }
}
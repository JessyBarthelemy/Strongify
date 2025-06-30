package com.jessy_barthelemy.strongify.adapter.exerciseSet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.asyncTask.DeleteSetExecutionTask;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.enumeration.ExerciseType;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.interfaces.ItemTouchHelperAdapter;
import com.jessy_barthelemy.strongify.interfaces.OnExerciseChanged;

import java.util.ArrayList;
import java.util.List;

public class ExerciseSetListAdapter extends RecyclerView.Adapter<ExerciseSetViewHolder> implements ItemTouchHelperAdapter{
    private List<SetExecution> dataSet;
    private List<SetExecution> dataSetToRemove;
    private OnExerciseChanged onExerciseChanged;
    private Context context;
    private int exerciseType;
    private int weightUnitId;
    private boolean isDeleteOnlyNew;

    public ExerciseSetListAdapter(Context context, List<SetExecution> dataSet, int exerciseType, OnExerciseChanged onExerciseChanged) {
        this.dataSet = dataSet;
        this.dataSetToRemove = new ArrayList<>();
        this.context = context;
        this.exerciseType = exerciseType;
        this.onExerciseChanged = onExerciseChanged;
    }

    @NonNull
    @Override
    public ExerciseSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_exercise_set, parent, false);
        return new ExerciseSetViewHolder(context, v, weightUnitId, exerciseType);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseSetViewHolder viewHolder, int position) {
        viewHolder.bindSet(this.dataSet.get(position), position);
    }

    @Override
    public int getItemCount() {
        return this.dataSet.size();
    }

    public void setWeightUnitId(int weightUnitId){
        this.weightUnitId = weightUnitId;
    }

    public void cloneLastItem(long executionId){
        if(dataSet.size() > 0){
            dataSet.add(dataSet.get(dataSet.size() - 1).clone());
        }else{
            SetExecution execution = ApplicationHelper.getDefaultSetExecution(context);
            execution.isNew = true;
            execution.execId = executionId;
            dataSet.add(execution);
        }
        notifyDataSetChanged();
        onExerciseChanged.onExerciseAdded(null, 0);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {}

    @Override
    public void onItemDismiss(RecyclerView view, final int position) {

        if(isDeleteOnlyNew && !this.dataSet.get(position).isNew){
            Toast.makeText(context, R.string.manage_exercise_mini_set, Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
            return;
        }

        if(exerciseType == ExerciseType.NORMAL){
            final SetExecution execution = this.dataSet.get(position);
            Snackbar snackbar = Snackbar.make(view, R.string.exercise_set_remove_title, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.workout_list_remove_action, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dataSet.add(position, execution);
                            dataSetToRemove.remove(execution);
                            notifyDataSetChanged();
                        }
                    });
            snackbar.show();

            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if(dataSetToRemove.size() > 0) {
                        if(context != null) {
                            new DeleteSetExecutionTask(context, dataSetToRemove.get(0)).execute();
                            dataSetToRemove.remove(0);
                            onExerciseChanged.onExerciseRemoved();
                        }
                    }
                }
            });

            dataSet.remove(position);
            dataSetToRemove.add(execution);
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean isSwipeEnabled() {
        return exerciseType == ExerciseType.NORMAL;
    }

    public void setDeleteOnlyNew(boolean deleteOnlyNew) {
        isDeleteOnlyNew = deleteOnlyNew;
    }
}
package com.jessy_barthelemy.strongify.adapter.exercise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.asyncTask.DeleteWorkoutExecutionTask;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.enumeration.ExerciseType;
import com.jessy_barthelemy.strongify.interfaces.CustomExerciseManager;
import com.jessy_barthelemy.strongify.interfaces.ItemTouchHelperAdapter;
import com.jessy_barthelemy.strongify.interfaces.OnExerciseChanged;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExerciseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    private List<ExerciseWithSet> dataSet;
    private List<ExerciseWithSet> dataSetToRemove;
    private WeakReference<Context> weakContext;
    private OnExerciseChanged listener;
    private CustomExerciseManager customExerciseManager;
    private int weightUnityId;

    public ExerciseListAdapter(Context context, List<ExerciseWithSet> dataSet, OnExerciseChanged listener, CustomExerciseManager customExerciseManager) {
        this.dataSet = dataSet;
        this.weakContext = new WeakReference<>(context);
        this.dataSetToRemove = new ArrayList<>();
        this.setHasStableIds(true);
        this.listener = listener;
        this.customExerciseManager = customExerciseManager;
    }

    @Override
    public int getItemViewType(int position) {
        if(this.dataSet.get(position).getSuperset() != null)
            return dataSet.get(position).getSuperset().exerciseType;
        else
            return ExerciseType.NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if(viewType == ExerciseType.SUPERSET) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_exercise_superset, parent, false);
            return new SupersetViewHolder(weakContext.get(), v, weightUnityId, listener, customExerciseManager);
        }else if(viewType == ExerciseType.CIRCUIT){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_exercise_circuit, parent, false);
            return new CircuitViewHolder(weakContext.get(), v, weightUnityId, listener, customExerciseManager);
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_exercise, parent, false);
            return new ExerciseViewHolder(weakContext.get(), v, weightUnityId, listener, customExerciseManager);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()){
            case ExerciseType.NORMAL:
                ((ExerciseViewHolder)viewHolder).bindExercise(this.dataSet.get(position));
                break;
            case ExerciseType.SUPERSET:
                ((SupersetViewHolder)viewHolder).bindExercise(this.dataSet.get(position), weakContext.get());
                break;
            case ExerciseType.CIRCUIT:
                ((CircuitViewHolder)viewHolder).bindExercise(this.dataSet.get(position), weakContext.get());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return this.dataSet.size();
    }

    @Override
    public void onItemDismiss(final RecyclerView view, final int position) {
        final ExerciseWithSet exercise = this.dataSet.get(position);
        Snackbar snackbar = Snackbar.make(view, R.string.exercise_list_remove_title, Snackbar.LENGTH_SHORT)
                .setAction(R.string.workout_list_remove_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dataSet.add(position, exercise);
                        dataSetToRemove.remove(exercise);

                        reorderList();
                        notifyDataSetChanged();
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
                        new DeleteWorkoutExecutionTask(context, dataSetToRemove.get(0).workoutExecution).execute();
                        dataSetToRemove.remove(0);
                        listener.onExerciseRemoved();
                    }
                }
            }
        });

        dataSet.remove(position);
        dataSetToRemove.add(exercise);
        reorderList();
        notifyDataSetChanged();
    }

    @Override
    public boolean isSwipeEnabled() {
        return true;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        boolean noMove = false;
        boolean hasToRefreshUi = false;

        //if the source is not a superset
        if(dataSet.get(fromPosition).getSuperset() == null){
            //and the target is a superset
            if(dataSet.get(toPosition).getSuperset() != null){
                long supersetId = dataSet.get(toPosition).getSuperset().supersetId;

                if(fromPosition < toPosition){
                    for(int j = toPosition, end = dataSet.size(); j < end; j++){
                        if(dataSet.get(j).getSuperset() == null
                                || (dataSet.get(j).getSuperset() != null && dataSet.get(j).getSuperset().supersetId != supersetId))
                            break;
                        moveToPosition(j-1, j);
                    }
                }else{
                    for(int j = toPosition; j >= 0; j--){
                        if(dataSet.get(j).getSuperset() == null
                           || (dataSet.get(j).getSuperset() != null && dataSet.get(j).getSuperset().supersetId != supersetId))
                            break;
                        moveToPosition(j, j+1);
                    }
                }
            //the target is not a superset
            }else{
                moveToPosition(fromPosition, toPosition);
            }
        //if the source is a superset and we move the first element
        }else if(dataSet.get(fromPosition).workoutExecution.supersetOrder == 0){
            long supersetId = dataSet.get(fromPosition).getSuperset().supersetId;

            //and the target is a superset
            if(dataSet.get(toPosition).getSuperset() != null) {

                if(fromPosition < toPosition){
                    for(int j = dataSet.size() - 1; j >= fromPosition; j--){
                        if(j < dataSet.size()
                                && dataSet.get(j).getSuperset() != null
                                && dataSet.get(j).getSuperset().supersetId == supersetId){
                            for(int i = j; i < dataSet.size() -1; i++){
                                if(dataSet.get(i+1).getSuperset() == null){
                                    moveToPosition(i, i+1);
                                    break;
                                }else if(dataSet.get(i+1).getSuperset().supersetId != supersetId)
                                    moveToPosition(i, i+1);
                            }
                        }
                    }
                }else{
                    boolean targetIsSuperset = false;
                    for(int j = fromPosition; j <= dataSet.size()-1; j++){
                        if(j < dataSet.size()
                                && dataSet.get(j).getSuperset() != null
                                && dataSet.get(j).getSuperset().supersetId == supersetId){
                            for(int i = j; i > 0; i--){
                                if(dataSet.get(i-1).getSuperset() == null){
                                    if(!targetIsSuperset)
                                        moveToPosition(i, i-1);
                                    break;
                                }else if(dataSet.get(i-1).getSuperset().supersetId != supersetId){
                                    targetIsSuperset = true;
                                    moveToPosition(i, i-1);
                                }
                            }
                        }
                    }
                }

            // and the target is not a superset
            }else{
                if(fromPosition > toPosition){
                    for(int j = fromPosition, len = dataSet.size(); j < len; j++){
                        if(dataSet.get(j).getSuperset() == null || dataSet.get(j).getSuperset().supersetId != supersetId)
                            break;
                        moveToPosition(j, j-1);
                    }
                }
            }
         //the source is superset and we are moving inside the supersets
        }else{
            if(dataSet.get(toPosition).getSuperset() != null && dataSet.get(fromPosition).getSuperset().supersetId == dataSet.get(toPosition).getSuperset().supersetId){
                hasToRefreshUi = true;
                moveToPosition(fromPosition, toPosition);
            }else{
                noMove = true;
            }
        }

        if(!noMove){
            int order = 0;
            int superSetOrder = 0;
            long superSetId = 0;

            for(int i = 0, len = dataSet.size(); i < len; i++){
                if(dataSet.get(i).getSuperset() != null){
                    if(superSetId == dataSet.get(i).getSuperset().supersetId){
                        superSetOrder++;
                    }else{
                        superSetId = dataSet.get(i).getSuperset().supersetId;
                        superSetOrder = 0;
                        order++;
                    }

                    dataSet.get(i).workoutExecution.supersetOrder = superSetOrder;
                }else{
                    order++;
                }

                dataSet.get(i).workoutExecution.order = order;
            }

            reorderList();

            if(hasToRefreshUi)
                notifyDataSetChanged();
        }
    }

    private void moveToPosition(int start, int end){
        Collections.swap(this.dataSet, start, end);
        notifyItemMoved(end, start);
    }

    @Override
    public long getItemId(int position) {
        return dataSet.get(position).workoutExecution.executionId;
    }

    private void reorderList(){
        int order = 0;
        int superSetOrder = 0;
        long superSetId = 0;

        for(int i = 0, len = dataSet.size(); i < len; i++){
            if(dataSet.get(i).getSuperset() != null){
                if(superSetId == dataSet.get(i).getSuperset().supersetId){
                    superSetOrder++;
                }else{
                    superSetId = dataSet.get(i).getSuperset().supersetId;
                    superSetOrder = 0;
                    order++;
                }

                dataSet.get(i).workoutExecution.supersetOrder = superSetOrder;
            }else{
                order++;
            }

            dataSet.get(i).workoutExecution.order = order;
        }
    }

    public void setWeightUnityId(int weightUnityId){
        this.weightUnityId = weightUnityId;
    }

    public void setDataSetItem(ExerciseWithSet exercise, int position)
    {
        dataSet.set(position, exercise);
        notifyDataSetChanged();
    }

    public List<ExerciseWithSet> getDataSet(){
        return dataSet;
    }
}
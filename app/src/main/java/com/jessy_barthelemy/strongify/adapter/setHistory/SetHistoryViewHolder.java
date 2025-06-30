package com.jessy_barthelemy.strongify.adapter.setHistory;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.enumeration.ExerciseSetType;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.Goal;

public class SetHistoryViewHolder extends RecyclerView.ViewHolder{
    private Context context;
    private TextView title;
    private EditText repetition;
    private EditText weight;
    private TextView goalRest;
    private ImageView exerciseIcon;
    private ImageView repetitionIcon;
    private TextView max;

    SetHistoryViewHolder(final Context context, View itemView, int weightUnitId){
        super(itemView);
        this.context = context;
        title = itemView.findViewById(R.id.exercise_title);
        repetition = itemView.findViewById(R.id.set_repetition);
        weight = itemView.findViewById(R.id.set_weight);

        ImageView weightIcon = itemView.findViewById(R.id.set_weight_icon);
        weightIcon.setImageResource(weightUnitId);

        repetitionIcon = itemView.findViewById(R.id.set_repetition_icon);
        exerciseIcon = itemView.findViewById(R.id.exercise_icon);
        goalRest = itemView.findViewById(R.id.goal_rest);
        max = itemView.findViewById(R.id.set_max);
    }

    public void bindSet(final ExerciseWithSet exercise){
        SetExecution setExecution = exercise.sets.get(exercise.currentSet);

        ApplicationHelper.setExerciseIcon(context, exerciseIcon, exercise.getExercise(), true);
        title.setText(ApplicationHelper.getExerciseTitle(context, exercise.getExercise().title, exercise.getExercise().base));
        repetition.setText(String.valueOf(setExecution.reps));
        weight.setText(String.valueOf(String.valueOf(setExecution.weight)));

        Goal goal = ApplicationHelper.getGoalIconId(context, setExecution.exerciseSetType, setExecution.reps, setExecution.maxRepetition);
        if(setExecution.exerciseSetType == ExerciseSetType.DURATION){
            repetition.setVisibility(View.GONE);
            goalRest.setText(goal.text);
            goalRest.setVisibility(View.VISIBLE);
        }

        exercise.sets.get(exercise.currentSet).repsDone = exercise.sets.get(exercise.currentSet).reps;
        repetition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    exercise.sets.get(exercise.currentSet).repsDone = Integer.valueOf(s.toString());
                }catch (NumberFormatException ignore){}
            }
        });

        exercise.sets.get(exercise.currentSet).weightLifted = exercise.sets.get(exercise.currentSet).weight;
        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    exercise.sets.get(exercise.currentSet).weightLifted = Integer.valueOf(s.toString());
                }catch (NumberFormatException ignore){}
            }
        });

        if(exercise.sets.get(exercise.currentSet).maxRepetition)
            max.setVisibility(View.VISIBLE);

        Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, goal.goalIconId));
        repetitionIcon.setImageDrawable(drawable);

        int color = ContextCompat.getColor(context, goal.tint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            DrawableCompat.setTint(drawable, color);
        else
            drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}

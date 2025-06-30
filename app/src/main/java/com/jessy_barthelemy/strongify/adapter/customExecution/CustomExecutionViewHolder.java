package com.jessy_barthelemy.strongify.adapter.customExecution;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.enumeration.ExerciseType;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.Goal;
import com.jessy_barthelemy.strongify.helper.TimeSpan;

public class CustomExecutionViewHolder extends RecyclerView.ViewHolder{

    private Context context;
    private ImageView icon;
    private TextView title;
    private TextView repetition;
    private TextView weight;
    private TextView rest;
    private ImageView repetitionIcon;

    CustomExecutionViewHolder(final Context context, View itemView, int weightUnitId){
        super(itemView);
        this.context = context;

        icon = itemView.findViewById(R.id.exercise_icon);
        title = itemView.findViewById(R.id.exercise_title);
        repetition = itemView.findViewById(R.id.exercise_set_repetition);
        weight = itemView.findViewById(R.id.exercise_weight);
        rest = itemView.findViewById(R.id.exercise_rest);
        repetitionIcon = itemView.findViewById(R.id.exercise_set_repetition_icon);

        ImageView weightIcon = itemView.findViewById(R.id.exercise_weight_icon);
        weightIcon.setImageResource(weightUnitId);
    }

    public void bindSet(ExerciseWithSet exercise){
        icon.setImageResource(ApplicationHelper.getDrawable(context, "th_" + exercise.getExercise().title));
        title.setText(ApplicationHelper.getExerciseTitle(context, exercise.getExercise().title, exercise.getExercise().base));

        if(exercise.currentSet >= exercise.sets.size())
            return;

        SetExecution execution = exercise.sets.get(exercise.currentSet);

        weight.setText(String.valueOf(execution.weight));

        TimeSpan timeSpan;
        if(exercise.getSuperset() != null && exercise.getSuperset().exerciseType == ExerciseType.SUPERSET){
            timeSpan = ApplicationHelper.getTimeSpan(exercise.getSuperset().rest);
        }else{
            timeSpan = ApplicationHelper.getTimeSpan(execution.rest);
        }
        rest.setText(context.getString(R.string.workout_rest, timeSpan.minutes, timeSpan.seconds));
        Goal goal = ApplicationHelper.getGoalIconId(context, execution.exerciseSetType, execution.reps, execution.maxRepetition);
        repetition.setText(goal.text);

        Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, goal.goalIconId));
        repetitionIcon.setImageDrawable(drawable);

        int color = ContextCompat.getColor(context, goal.tint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            DrawableCompat.setTint(drawable, color);
        else
            drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}

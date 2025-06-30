package com.jessy_barthelemy.strongify.adapter.exerciseSet;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.dialog.TimeSpanPickerDialog;
import com.jessy_barthelemy.strongify.enumeration.ExerciseSetType;
import com.jessy_barthelemy.strongify.enumeration.ExerciseType;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.TimeSpan;
import com.jessy_barthelemy.strongify.interfaces.TimeSpanManager;

public class ExerciseSetViewHolder extends RecyclerView.ViewHolder implements TimeSpanManager{

    private static final int REST_TIME_SPAN = 0;
    private static final int GOAL_TIME_SPAN = 1;

    private Context context;
    private TextView title;
    private EditText repetition;
    private EditText weight;
    private TextView restMin;
    private TextView restSec;
    private TextView goalRestMin;
    private TextView goalRestSec;
    private Spinner type;
    private Button max;
    private SetExecution setExecution;

    private TimeSpan timeSpan;

    ExerciseSetViewHolder(final Context context, View itemView, int weightUnitId, final int exerciseType){
        super(itemView);
        this.context = context;
        title = itemView.findViewById(R.id.set_title);
        repetition = itemView.findViewById(R.id.set_repetition);
        weight = itemView.findViewById(R.id.set_weight);
        restMin = itemView.findViewById(R.id.set_rest_min);
        restSec = itemView.findViewById(R.id.set_rest_sec);
        max = itemView.findViewById(R.id.set_max);

        ImageView weightIcon = itemView.findViewById(R.id.set_weight_icon);
        weightIcon.setImageResource(weightUnitId);

        final ImageView repetitionIcon = itemView.findViewById(R.id.set_repetition_icon);
        final View goalRestWrapper = itemView.findViewById(R.id.goal_rest_wrapper);
        goalRestMin = itemView.findViewById(R.id.goal_rest_min);
        goalRestSec = itemView.findViewById(R.id.goal_rest_sec);
        View timeSpanWrapper = itemView.findViewById(R.id.set_rest_wrapper);

        if(exerciseType == ExerciseType.SUPERSET)
            timeSpanWrapper.setVisibility(View.GONE);

        timeSpanWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setExecution != null){
                    new TimeSpanPickerDialog(context, timeSpan, ExerciseSetViewHolder.this, context.getString(R.string.exercise_set_rest_title), REST_TIME_SPAN).show();
                }
            }
        });

        goalRestWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setExecution != null && !setExecution.maxRepetition){
                    new TimeSpanPickerDialog(context, ApplicationHelper.getTimeSpan(setExecution.reps), ExerciseSetViewHolder.this, context.getString(R.string.exercise_set_goal_title), GOAL_TIME_SPAN).show();
                }
            }
        });

        repetition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    setExecution.reps = Integer.parseInt(repetition.getText().toString());
                }catch (NumberFormatException e){
                    setExecution.reps = 0;
                }
            }
        });

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    setExecution.weight =  Double.parseDouble(weight.getText().toString());
                }catch (NumberFormatException e){
                    setExecution.weight = 0;
                }
            }
        });

        max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExecution.maxRepetition = !setExecution.maxRepetition;
                handleMaxButton();
            }
        });

        type = itemView.findViewById(R.id.set_type);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                setExecution.exerciseSetType = position;
                Drawable drawable;
                int color;
                switch (position){
                    case ExerciseSetType.REPETITION :
                        drawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.ic_set));
                        repetitionIcon.setImageDrawable(drawable);

                        color = ContextCompat.getColor(context, R.color.colorAccent);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            DrawableCompat.setTint(drawable, color);
                        else
                            drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);

                        goalRestWrapper.setVisibility(View.GONE);
                        repetition.setVisibility(View.VISIBLE);
                        repetition.setText(String.valueOf(setExecution.reps));

                        break;
                    case ExerciseSetType.DISTANCE:
                        drawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, ApplicationHelper.getDistanceIcon(context)));
                        repetitionIcon.setImageDrawable(drawable);

                        color = ContextCompat.getColor(context, R.color.colorAccent);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            DrawableCompat.setTint(drawable, color);
                        else
                            drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);

                        goalRestWrapper.setVisibility(View.GONE);
                        repetition.setVisibility(View.VISIBLE);
                        repetition.setText(String.valueOf(setExecution.reps));
                        break;
                    case ExerciseSetType.DURATION:
                        drawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.ic_time));
                        repetitionIcon.setImageDrawable(drawable);

                        color = ContextCompat.getColor(context, R.color.colorAccent2);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            DrawableCompat.setTint(drawable, color);
                        else
                            drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);

                        goalRestWrapper.setVisibility(View.VISIBLE);
                        repetition.setVisibility(View.GONE);

                        TimeSpan timeSpan = ApplicationHelper.getTimeSpan(setExecution.reps);
                        goalRestMin.setText(String.valueOf(timeSpan.minutes));
                        goalRestSec.setText(String.valueOf(timeSpan.seconds));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void bindSet(SetExecution exercise, int position){
        this.setExecution = exercise;
        title.setText(context.getString(R.string.exercise_set_title, (position + 1)));
        repetition.setText(String.valueOf(exercise.reps));
        weight.setText(String.valueOf(exercise.weight));

        timeSpan = ApplicationHelper.getTimeSpan(exercise.rest);

        restMin.setText(String.valueOf(timeSpan.minutes));
        restSec.setText(String.valueOf(timeSpan.seconds));
        type.setSelection(setExecution.exerciseSetType);
        handleMaxButton();
    }

    private void handleMaxButton(){
        if(setExecution.maxRepetition){
            max.setBackgroundColor(ApplicationHelper.getThemeColor(context, R.attr.colorAccent2));
            repetition.setEnabled(false);
        }else{
            max.setBackgroundColor(ApplicationHelper.getThemeColor(context, R.attr.colorDisabled));
            repetition.setEnabled(true);
        }
    }

    @Override
    public void onTimeSpanChanged(TimeSpan timeSpan, int sourceId) {

        switch (sourceId){
            case GOAL_TIME_SPAN:
                goalRestMin.setText(String.valueOf(timeSpan.minutes));
                goalRestSec.setText(String.valueOf(timeSpan.seconds));
                setExecution.reps = timeSpan.getTotalSeconds();
                break;
            default:
                restMin.setText(String.valueOf(timeSpan.minutes));
                restSec.setText(String.valueOf(timeSpan.seconds));
                setExecution.rest = timeSpan.getTotalSeconds();
        }
    }
}

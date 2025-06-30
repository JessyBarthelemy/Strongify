package com.jessy_barthelemy.strongify.adapter.exercise;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.fragment.CustomExerciseFragment;
import com.jessy_barthelemy.strongify.fragment.ExerciseListFragment;
import com.jessy_barthelemy.strongify.fragment.ExerciseSetFragment;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.Goal;
import com.jessy_barthelemy.strongify.interfaces.CustomExerciseManager;
import com.jessy_barthelemy.strongify.interfaces.OnExerciseChanged;
import com.jessy_barthelemy.strongify.interfaces.OnFragmentInteractionListener;

import java.util.HashMap;
import java.util.Map;

public class ExerciseViewHolder extends RecyclerView.ViewHolder{

    private TextView setCount;
    protected TextView title;
    private TextView setRepetitionView;
    private TextView setWeightView;
    private ImageView setRepetitionIcon;

    private TextView setRestView;
    View restViewWrapper;
    private ExerciseWithSet exercise;

    protected Context context;
    protected OnExerciseChanged listener;
    private CustomExerciseManager customExerciseManager;

    ExerciseViewHolder(final Context context, View itemView, int weightUnityId, final OnExerciseChanged listener, CustomExerciseManager customExerciseManager) {
        super(itemView);
        title = itemView.findViewById(R.id.exercise_title);
        setCount = itemView.findViewById(R.id.workout_set_count);

        setRepetitionView = itemView.findViewById(R.id.exercise_set_repetition);
        setWeightView = itemView.findViewById(R.id.exercise_weight);
        setRestView = itemView.findViewById(R.id.exercise_rest);
        restViewWrapper = itemView.findViewById(R.id.exercise_rest_wrapper);
        setRepetitionIcon = itemView.findViewById(R.id.exercise_set_repetition_icon);

        this.listener = listener;
        this.customExerciseManager = customExerciseManager;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity activity = (HomeActivity) context;
                ExerciseSetFragment fragment = new ExerciseSetFragment();
                Map<String, View> transitions = new HashMap<>();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    transitions.put(title.getTransitionName(), title);
                    fragment.setTitleTransitionName(title.getTransitionName());
                    fragment.setSharedElementEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.move));
                    fragment.setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));
                }
                fragment.setExerciseWithSet(exercise);
                fragment.setListener(listener);
                fragment.setPosition(getAdapterPosition());
                activity.replaceFragment(fragment, transitions);
            }
        });

        ImageView weightIcon = itemView.findViewById(R.id.exercise_weight_icon);
        weightIcon.setImageResource(weightUnityId);

        this.context = context;
    }

    public void bindExercise(ExerciseWithSet exercise){
        if(exercise == null)
            return;
        this.exercise = exercise;

        title.setText(ApplicationHelper.getExerciseTitle(context, exercise.getExercise().title, exercise.getExercise().base));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            title.setTransitionName(context.getString(R.string.transition, exercise.workoutExecution.executionId));
        int exerciseCount = exercise.sets != null ? exercise.sets.size() : 0;
        setCount.setText(context.getResources().getQuantityString(R.plurals.workout_exercise_set_count, exerciseCount, exerciseCount));

        StringBuilder setRepetition = new StringBuilder();
        StringBuilder setWeight = new StringBuilder();
        StringBuilder setRest = new StringBuilder();

        if(exercise.sets != null){
            int len =  exercise.sets.size();
            Goal goal;
            if(len > 0){
                goal = ApplicationHelper.getGoalIconId(context, exercise.sets.get(0).exerciseSetType,
                        exercise.sets.get(0).reps, exercise.sets.get(0).maxRepetition);

                Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, goal.goalIconId));
                setRepetitionIcon.setImageDrawable(drawable);

                int color = ContextCompat.getColor(context, goal.tint);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    DrawableCompat.setTint(drawable, color);
                else
                    drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }

            ApplicationHelper.formatExercise(context, exercise, setRepetition,setWeight, setRest, false);
        }
        setRepetitionView.setText(setRepetition.toString());
        setWeightView.setText(setWeight.toString());
        setRestView.setText(setRest.toString());
    }

    //reserved to custom type
    void addExercises(){
        ExerciseListFragment fragment = new ExerciseListFragment();
        fragment.setListener((OnFragmentInteractionListener)listener);
        fragment.setExerciseType(exercise.getSuperset().exerciseType);
        fragment.setSuperset(exercise.getSuperset());
        fragment.setPickerMode(true);
        ((HomeActivity)context).replaceFragment(fragment, null);
    }

    void updateCustomExercise(){
        CustomExerciseFragment fragment = new CustomExerciseFragment();
        fragment.setExercise(exercise);
        fragment.setCustomExerciseManager(customExerciseManager);
        ((HomeActivity)context).replaceFragment(fragment, null);
    }
}
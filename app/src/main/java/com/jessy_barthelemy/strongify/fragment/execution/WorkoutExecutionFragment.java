package com.jessy_barthelemy.strongify.fragment.execution;


import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.customLayout.Timer;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;
import com.jessy_barthelemy.strongify.enumeration.ExerciseSetType;
import com.jessy_barthelemy.strongify.enumeration.FragmentType;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.Goal;
import com.jessy_barthelemy.strongify.helper.TimeSpan;
import com.jessy_barthelemy.strongify.interfaces.OnTimerFinishedListener;

public class WorkoutExecutionFragment extends BaseExecutionFragment {

    private ExerciseWithSet exercise;
    private SetExecution execution;

    private boolean firstIcon;
    private int firstIconId;
    private int secondIconId;
    private Handler iconHandler;
    private Runnable iconRunnable;
    private Handler handler;
    private Runnable runnable;
    private Timer timer;

    public WorkoutExecutionFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_workout_execution, container, false);
        TextView setRepetition = root.findViewById(R.id.exercise_set_repetition);
        ImageView repetitionIcon = root.findViewById(R.id.exercise_set_repetition_icon);

        if(execution != null){
            Goal goal = ApplicationHelper.getGoalIconId(getActivity(), execution.exerciseSetType, execution.reps, execution.maxRepetition);
            setRepetition.setText(goal.text);
            if(getActivity() != null){
                Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(getActivity(), goal.goalIconId));
                repetitionIcon.setImageDrawable(drawable);

                int color = ContextCompat.getColor(getActivity(), goal.tint);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    DrawableCompat.setTint(drawable, color);
                else
                    drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }

            TextView setWeight = root.findViewById(R.id.exercise_weight);
            setWeight.setText(String.valueOf(execution.weight));
            ImageView setWeightIcon = root.findViewById(R.id.exercise_weight_icon);
            setWeightIcon.setImageResource(ApplicationHelper.getWeightIcon(getActivity()));

            TextView setRest = root.findViewById(R.id.exercise_rest);
            TimeSpan timeSpan = ApplicationHelper.getTimeSpan(execution.rest);
            setRest.setText(getString(R.string.workout_rest, timeSpan.minutes, timeSpan.seconds));

            TextView title = root.findViewById(R.id.exercise_title);
            title.setText(ApplicationHelper.getExerciseTitle(getActivity(), exercise.getExercise().title, exercise.getExercise().base));

            firstIconId = ApplicationHelper.getExerciseIcon(getActivity(), exercise.getExercise(), true);
            secondIconId = ApplicationHelper.getExerciseIcon(getActivity(), exercise.getExercise(), false);

            final ImageView exerciseIcon = root.findViewById(R.id.exercise_icon);

            if(exercise.getExercise().base)
                exerciseIcon.setImageResource(ApplicationHelper.getExerciseIcon(getActivity(), exercise.getExercise(), true));
            else{

                if(exercise.getExercise().path != null){
                    try {
                        exerciseIcon.setImageBitmap(BitmapFactory.decodeFile(exercise.getExercise().path));
                    }catch (OutOfMemoryError e){
                        Toast.makeText(getContext(), R.string.image_too_big, Toast.LENGTH_LONG).show();
                    }
                }
                else
                    exerciseIcon.setImageResource(R.drawable.select_image);
            }

            Button rest = root.findViewById(R.id.exercise_rest_action);

            if(execution.exerciseSetType == ExerciseSetType.DURATION){
                rest.setVisibility(View.GONE);

                workout.endTimer = System.currentTimeMillis() + (execution.reps*1000);

                timer = root.findViewById(R.id.timer);
                timer.setRest(workout.endTimer, execution.reps);
                timer.setVisibility(View.VISIBLE);
                timer.start();

                timer.setOnTimerFinishedListener(new OnTimerFinishedListener() {
                    @Override
                    public void onTimerFinished() {
                        workout.endTimer = 0;
                        showRestFragment();
                    }
                });
            }else
                workout.endTimer = 0;

            if(secondIconId > 0){
                iconHandler = new Handler();
                iconRunnable = new Runnable(){
                    public void run(){
                        if(firstIcon)
                            exerciseIcon.setImageResource(firstIconId);
                        else
                            exerciseIcon.setImageResource(secondIconId);
                        firstIcon = !firstIcon;
                        iconHandler.postDelayed(this, ApplicationHelper.ICON_DELAY);
                    }
                };
            }
            final boolean isLastSet = exercise.currentSet >= (exercise.sets.size() -1);

            rest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isLastSet && exercise.getSuperset() == null && ApplicationHelper.isRingerActivated(getActivity())){
                        Handler lastSoundHandler = new Handler();
                        lastSoundHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ApplicationHelper.makeVibration(getContext());
                                final MediaPlayer finalPlayer = MediaPlayer.create(getContext(), R.raw.final_tick);
                                finalPlayer.start();
                                finalPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        finalPlayer.release();
                                    }
                                });
                            }
                        });
                    }

                    WorkoutExecutionRestFragment fragment = new WorkoutExecutionRestFragment();
                    fragment.setWorkout(workout);
                    ((HomeActivity)getActivity()).replaceFragment(fragment, null);
                }
            });

            TextView currentSet = root.findViewById(R.id.current_set);
            currentSet.setText(getString(R.string.current_set, exercise.currentSet + 1, exercise.sets.size()));

            final TextView globalTimer = root.findViewById(R.id.global_timer);

            handler = new Handler();
            runnable = new Runnable() {

                @Override
                public void run() {
                    int seconds = (int) ((System.currentTimeMillis() - workout.startTime) / 1000);
                    int minutes = seconds / 60;
                    seconds     = seconds % 60;

                    if(globalTimer != null)
                        globalTimer.setText(getString(R.string.time, minutes, seconds));
                    handler.postDelayed(this, 500);
                }
            };

            handler.post(runnable);
        }

        return root;
    }

    private void showRestFragment(){
        if(getActivity() != null){
            WorkoutExecutionRestFragment fragment = new WorkoutExecutionRestFragment();
            fragment.setWorkout(workout);
            ((HomeActivity)getActivity()).replaceFragment(fragment, null);
        }
    }

    public void setWorkout(WorkoutWithExercise workout) {
        this.workout = workout;
        this.exercise = this.workout.workoutExecution.get(workout.currentExercise);
        this.execution = this.exercise.sets.get(this.exercise.currentSet);
    }

    @Override
    public void onStop() {
        handler.removeCallbacks(runnable);

        if(secondIconId > 0)
            iconHandler.removeCallbacks(iconRunnable);
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        if(secondIconId > 0)
            iconHandler.postDelayed(iconRunnable,  ApplicationHelper.ICON_DELAY);

        handler.post(runnable);
        super.onResume();
    }

    @Override
    protected int getFragmentType() {
        return FragmentType.WORKOUT_EXECUTION;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null)
            timer.onDestroy();
    }
}

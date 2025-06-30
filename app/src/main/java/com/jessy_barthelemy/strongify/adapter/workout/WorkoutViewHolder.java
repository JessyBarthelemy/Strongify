package com.jessy_barthelemy.strongify.adapter.workout;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;
import com.jessy_barthelemy.strongify.enumeration.ExerciseType;
import com.jessy_barthelemy.strongify.fragment.WorkoutFragment;
import com.jessy_barthelemy.strongify.fragment.execution.WorkoutExecutionFragment;
import com.jessy_barthelemy.strongify.fragment.execution.WorkoutExecutionRestFragment;
import com.jessy_barthelemy.strongify.fragment.execution.WorkoutSupersetExecutionFragment;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.TimeSpan;
import com.jessy_barthelemy.strongify.service.ExecutionService;

import java.util.HashMap;
import java.util.Map;

public class WorkoutViewHolder extends RecyclerView.ViewHolder{

    private TextView title;
    private TextView periodicity;
    private TextView exerciseCount;
    private TextView workoutDuration;
    private Context context;
    private WorkoutWithExercise workout;
    private ExecutionService executionService;

    public View playWorkout;

    WorkoutViewHolder(View itemView) {
        super(itemView);
        this.title = itemView.findViewById(R.id.list_workout_title);
        this.periodicity = itemView.findViewById(R.id.list_workout_periodicity);
        this.exerciseCount = itemView.findViewById(R.id.workout_exercises_count);
        this.workoutDuration = itemView.findViewById(R.id.workout_duration);
        this.context = itemView.getContext();
        View settings = itemView.findViewById(R.id.list_workout_settings);

        playWorkout = itemView.findViewById(R.id.play_workout);
        playWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    context.startForegroundService(new Intent(context, ExecutionService.class));
                else
                    ContextCompat.startForegroundService(context.getApplicationContext(), new Intent(context.getApplicationContext(), ExecutionService.class));
                context.startService(new Intent(context.getApplicationContext(), ExecutionService.class));
                final ServiceConnection connection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName className,
                                                   IBinder service) {
                        executionService = ((ExecutionService.ExecutionBinder)service).getService();
                        if(executionService.getWorkout() != null && executionService.getWorkout().workout.id == workout.workout.id){
                            openServiceFragment(executionService.getWorkout(), this, executionService.getStartTime());
                        }else{
                            openServiceFragment(workout, this, System.currentTimeMillis());
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        executionService = null;
                    }
                };

                Intent intent = new Intent(context.getApplicationContext(), ExecutionService.class);
                context.getApplicationContext().bindService(intent, connection, Context.BIND_IMPORTANT);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkoutFragment workoutFragment = new WorkoutFragment();
                workoutFragment.setWorkout(workout);

                String titleTransition = "";
                String periodicityTransition = "";
                Map<String, View> transitions = new HashMap<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    workoutFragment.setSharedElementEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.move));
                    workoutFragment.setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));

                    titleTransition = title.getTransitionName();
                    periodicityTransition = periodicity.getTransitionName();
                    workoutFragment.setTransitionName(titleTransition, periodicityTransition);
                    transitions.put(titleTransition, title);
                    transitions.put(periodicityTransition, periodicity);
                }

                ((HomeActivity)context).replaceFragment(workoutFragment, transitions);
            }
        });
    }

    private void openServiceFragment(WorkoutWithExercise currentWorkout, ServiceConnection connection, long startTime){
        if(currentWorkout.startTime == 0)
            currentWorkout.startTime = startTime;

        if(currentWorkout.workoutExecution == null || !currentWorkout.isPlayable()){
            Toast.makeText(context, R.string.workout_no_exercise, Toast.LENGTH_SHORT).show();
            context.stopService(new Intent(context.getApplicationContext(), ExecutionService.class));
        }else if(currentWorkout.endTimer > 0){
            WorkoutExecutionRestFragment fragment = new WorkoutExecutionRestFragment();
            fragment.setWorkout(currentWorkout);
            context.getApplicationContext().unbindService(connection);
            ((HomeActivity)context).replaceFragment(fragment, null);
        }
        else if(currentWorkout.workoutExecution.get(currentWorkout.currentExercise).getSuperset() != null
                && currentWorkout.workoutExecution.get(currentWorkout.currentExercise).getSuperset().exerciseType == ExerciseType.SUPERSET){
            WorkoutSupersetExecutionFragment fragment = new WorkoutSupersetExecutionFragment();
            fragment.setWorkout(currentWorkout);
            context.getApplicationContext().unbindService(connection);
            ((HomeActivity)context).replaceFragment(fragment, null);
        }else{
            WorkoutExecutionFragment fragment = new WorkoutExecutionFragment();
            fragment.setWorkout(currentWorkout);
            context.getApplicationContext().unbindService(connection);
            ((HomeActivity)context).replaceFragment(fragment, null);
        }
    }

    void bindWorkout(WorkoutWithExercise workout){
        this.workout = workout;
        this.title.setText(workout.workout.name);
        if(workout.workout.periodicity != null && workout.workout.periodicity.size() > 0)
            this.periodicity.setText(ApplicationHelper.getPeriodicityText(this.context, workout.workout.periodicity));
        if(workout.history != null) {
            TimeSpan timeSpan = ApplicationHelper.getTimeSpan(workout.history.duration);

            this.workoutDuration.setText(context.getString(R.string.time, timeSpan.hours, timeSpan.minutes));
        }

        if(workout.workoutExecution != null)
            this.exerciseCount.setText(String.valueOf(workout.workoutExecution.size()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            title.setTransitionName(context.getString(R.string.trans_workout_title, workout.workout.id));
            periodicity.setTransitionName(context.getString(R.string.trans_workout_periodicity, workout.workout.id));
        }
    }
}

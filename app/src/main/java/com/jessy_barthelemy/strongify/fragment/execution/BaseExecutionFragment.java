package com.jessy_barthelemy.strongify.fragment.execution;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;
import com.jessy_barthelemy.strongify.enumeration.FragmentType;
import com.jessy_barthelemy.strongify.fragment.BaseFragment;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.service.ExecutionService;

public abstract class BaseExecutionFragment extends BaseFragment{
    protected ExecutionService executionService;
    protected WorkoutWithExercise workout;
    protected ServiceConnection connection;

    @Override
    public void onStart() {
        super.onStart();
        if(getActivity() != null){
            connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName className,
                                               IBinder service) {
                    executionService = ((ExecutionService.ExecutionBinder)service).getService();
                    onServiceBind();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    if(executionService != null){
                        executionService.stopForeground(true);
                        executionService.stopSelf();
                        executionService = null;
                    }
                }
            };

            Intent intent = new Intent(getActivity().getApplicationContext(), ExecutionService.class);
            intent.putExtra(ExecutionService.WORKOUT_PARAMETER, workout);
            intent.putExtra(ExecutionService.FRAGMENT_TYPE_PARAMETER, workout);
            getActivity().getApplicationContext().bindService(intent, connection, Context.BIND_IMPORTANT);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ApplicationHelper.isTimerHeadEnabled(getActivity()) && getActivity() != null && getFragmentType() != FragmentType.WORKOUT_REST && !ApplicationHelper.isPermissionChecked(getActivity()))
            ((HomeActivity)getActivity()).checkPermission();
    }

    @Override
    public void onStop() {
        super.onStop();

        if(getActivity() != null && executionService != null && executionService.isStarted)
            getActivity().getApplicationContext().unbindService(connection);
    }

    protected void onServiceBind(){
        executionService.setWorkout(workout, getFragmentType());
        executionService.closeTimer();
    }

    protected abstract int getFragmentType();

    @Override
    public boolean isBackPressEnabled() {
        return false;
    }
}

package com.jessy_barthelemy.strongify.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.jessy_barthelemy.strongify.NotificationPublisher;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.adapter.periodicity.PeriodicityAdapter;
import com.jessy_barthelemy.strongify.database.asyncTask.InsertWorkoutTask;
import com.jessy_barthelemy.strongify.database.asyncTask.UpdateWorkoutTask;
import com.jessy_barthelemy.strongify.database.entity.Workout;
import com.jessy_barthelemy.strongify.database.pojo.Periodicity;
import com.jessy_barthelemy.strongify.helper.AnimationHelper;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.RevealAnimationSettings;
import com.jessy_barthelemy.strongify.interfaces.Dismissible;
import com.jessy_barthelemy.strongify.interfaces.OnPeriodicityChanged;
import com.jessy_barthelemy.strongify.interfaces.WorkoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WorkoutManagementFragment extends BaseFragment implements Dismissible{

    private RevealAnimationSettings settings;
    private View root;
    private int order;
    private Workout workout;
    private WorkoutManager workoutManager;
    private TextView timeReminder;
    private CheckBox timeCheck;
    private Integer selectedHour;
    private Integer selectedMinute;

    private TextInputLayout title;

    public WorkoutManagementFragment() {}

    public static WorkoutManagementFragment newInstance() {
        return new WorkoutManagementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_workout_creation, container, false);
        if(getActivity() != null && settings != null){
            int backgroundColor = ApplicationHelper.getThemeColor(getContext(), android.R.attr.windowBackground);
            AnimationHelper.registerCircularRevealAnimation(getActivity(), root, settings,
                    ContextCompat.getColor(getActivity(), R.color.colorAccent), backgroundColor);
        }

        //reminder
        timeReminder = root.findViewById(R.id.workout_creation_reminder_time);
        timeCheck = root.findViewById(R.id.workout_creation_reminder_check);

        timeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                timeReminder.setEnabled(b);

                if(!b){
                    selectedHour = null;
                    selectedMinute = null;
                }
            }
        });

        if(workout != null && (workout.reminderHour != null && workout.reminderMinute != null)){
            timeReminder.setEnabled(true);
            timeCheck.setChecked(true);
        }else{
            timeReminder.setEnabled(false);
            timeCheck.setChecked(false);
        }

        RecyclerView recyclerView = root.findViewById(R.id.workout_creation_periodicity);

        String[] periodicityStrings = getResources().getStringArray(R.array.periodicity);
        final List<Periodicity> periodicity = new ArrayList<>();

        for(int i = 0; i < periodicityStrings.length; i++){
            periodicity.add(new Periodicity(i, periodicityStrings[i], workout != null && workout.periodicity.contains(i)));
        }

        final PeriodicityAdapter adapter = new PeriodicityAdapter(periodicity);
        adapter.setOnPeriodicityChanged(new OnPeriodicityChanged() {
            @Override
            public void onPeriodicityChanged() {
                updatePeriodicityText(adapter);
            }
        });

        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        timeReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timeCheck.isChecked()){
                    new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            WorkoutManagementFragment.this.selectedHour = selectedHour;
                            WorkoutManagementFragment.this.selectedMinute = selectedMinute;
                            updatePeriodicityText(adapter);
                        }
                    }, selectedHour != null ? selectedHour : 0, selectedMinute != null ? selectedMinute : 0, true)
                            .show();
                }
            }
        });

        title = root.findViewById(R.id.workout_creation_title);
        title.setErrorEnabled(true);

        final ScrollView scrollView = root.findViewById(R.id.scroll);

        Button actionButton = root.findViewById(R.id.workout_management_action);
        if(workout != null){
            WorkoutManagementFragment.this.selectedHour = workout.reminderHour;
            WorkoutManagementFragment.this.selectedMinute = workout.reminderMinute;
            actionButton.setText(getString(R.string.workout_update_action));
            updatePeriodicityText(adapter);
        }

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleText = null;

                ApplicationHelper.closeKeyboard(getActivity());

                if(workout == null)
                    workout = new Workout();

                if(title.getEditText() != null)
                    titleText = title.getEditText().getText().toString();

                workout.name = titleText;
                workout.periodicity = new ArrayList<>();
                for (Periodicity periodicity : periodicity) {
                    if (periodicity.isChecked())
                        workout.periodicity.add(periodicity.getId());
                }

                workout.order = order;

                workout.reminderHour = selectedHour;
                workout.reminderMinute = selectedMinute;

                if (titleText == null || titleText.isEmpty()) {
                    ApplicationHelper.makeVibration(getActivity());
                    title.setError(getString(R.string.workout_creation_title_error));
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    title.requestFocus();
                    return;
                }else if(timeCheck.isChecked() && workout.periodicity.size() == 0){
                    ApplicationHelper.makeVibration(getActivity());
                    Toast.makeText(getActivity(), R.string.workout_creation_reminder_error, Toast.LENGTH_LONG).show();
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    return;
                }

                if(workout.id > 0){
                    new UpdateWorkoutTask(getActivity(), workout, workoutManager).execute();
                    workoutManager.onWorkoutChanged(getActivity(), workout);
                }else{
                    new InsertWorkoutTask(getActivity(), workout).execute();
                }

                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        if(workout != null && title.getEditText() != null)
            title.getEditText().setText(workout.name);

        return root;
    }

    @Override
    public void dismiss(OnDismissedListener listener) {
        if(getActivity() != null && settings != null) {
            int backgroundColor = ApplicationHelper.getThemeColor(getContext(), android.R.attr.windowBackground);
            AnimationHelper.startCircularExitAnimation(getActivity(), getView(), settings, ContextCompat.getColor(getActivity(), R.color.colorAccent), backgroundColor, listener);
        }else{
            listener.onDismissed();
        }
    }

    private void updatePeriodicityText(PeriodicityAdapter adapter){
        int hour = selectedHour != null ? selectedHour : 0;
        int minute = selectedMinute != null ? selectedMinute : 0;

        timeReminder.setText(Html.fromHtml(getString(R.string.workout_creation_reminder_time, adapter.getPeriodicityString(), hour, minute)));
    }

    public void setSettings(RevealAnimationSettings settings) {
        this.settings = settings;
    }

    public void hide(){
        root.setVisibility(View.INVISIBLE);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setWorkout(Workout workout){
        this.workout = workout;
    }

    public void setWorkoutManager(WorkoutManager workoutManager){
        this.workoutManager = workoutManager;
    }
}

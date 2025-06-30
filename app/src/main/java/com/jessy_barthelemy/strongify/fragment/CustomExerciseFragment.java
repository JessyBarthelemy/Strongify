package com.jessy_barthelemy.strongify.fragment;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.asyncTask.UpdateCustomExerciseTask;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.dialog.TimeSpanPickerDialog;
import com.jessy_barthelemy.strongify.enumeration.ExerciseType;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.TimeSpan;
import com.jessy_barthelemy.strongify.interfaces.CustomExerciseManager;
import com.jessy_barthelemy.strongify.interfaces.TimeSpanManager;

public class CustomExerciseFragment extends BaseFragment implements TimeSpanManager{
    private ExerciseWithSet exercise;
    private final int REST_TIME_SPAN = 0;
    private TextView minView;
    private TextView secView;
    private CustomExerciseManager customExerciseManager;
    private EditText repetition;

    public CustomExerciseFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_custom_exercise, container, false);
        TextView title = root.findViewById(R.id.title);

        if(exercise.getSuperset() != null){
            View timeSpanWrapper = root.findViewById(R.id.custom_rest_wrapper);

            final TimeSpan timeSpan = ApplicationHelper.getTimeSpan(exercise.getSuperset().rest);
            minView = root.findViewById(R.id.set_rest_min);
            secView = root.findViewById(R.id.set_rest_sec);

            minView.setText(String.valueOf(timeSpan.minutes));
            secView.setText(String.valueOf(timeSpan.seconds));

            switch (exercise.getSuperset() .exerciseType){
                case ExerciseType.SUPERSET:
                    title.setText(R.string.set_superset_title);
                    timeSpanWrapper.setOnClickListener(v -> {
                        if(getActivity() != null && exercise.getSuperset() != null)
                            new TimeSpanPickerDialog(getActivity(), timeSpan, CustomExerciseFragment.this, getString(R.string.exercise_set_rest_title), REST_TIME_SPAN).show();
                    });
                    break;
                case ExerciseType.CIRCUIT:
                    title.setText(R.string.set_circuit_title);
                    timeSpanWrapper.setVisibility(View.GONE);
                    break;
            }

            repetition = root.findViewById(R.id.set_repetition);
            repetition.setText(String.valueOf(exercise.getSuperset() .repetition));

            repetition.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.toString().equals("0")){
                        Toast.makeText(getActivity(), R.string.incorrect_value, Toast.LENGTH_SHORT).show();
                        repetition.setText("1");
                    }
                }
            });
        }

        ImageView backAction = root.findViewById(R.id.back_action);
        backAction.setOnClickListener(v -> {
            if(getActivity() != null)
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        Button saveButton = root.findViewById(R.id.save_action);
        saveButton.setOnClickListener(v -> {
            if(getActivity() != null)
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        return root;
    }

    public void setExercise(ExerciseWithSet exercise){
        this.exercise = exercise;
    }

    public void setCustomExerciseManager(CustomExerciseManager manager){
        this.customExerciseManager = manager;
    }

    @Override
    public void onTimeSpanChanged(TimeSpan timeSpan, int sourceId) {
        exercise.getSuperset().rest = (timeSpan.minutes * 60) + timeSpan.seconds;
        minView.setText(String.valueOf(timeSpan.minutes));
        secView.setText(String.valueOf(timeSpan.seconds));
    }

    @Override
    public void onStop() {
        super.onStop();
        try{
            exercise.getSuperset().repetition = Integer.parseInt(repetition.getText().toString());
        }catch (NumberFormatException e){
            exercise.getSuperset().repetition = 1;
        }
        new UpdateCustomExerciseTask(getActivity(), exercise.getSuperset(), customExerciseManager).execute();
    }
}

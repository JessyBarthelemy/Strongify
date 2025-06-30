package com.jessy_barthelemy.strongify.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.ExerciseHistory;
import com.jessy_barthelemy.strongify.database.pojo.DoubleMeasures;
import com.jessy_barthelemy.strongify.database.pojo.Measure;
import com.jessy_barthelemy.strongify.database.pojo.Measures;

import java.util.ArrayList;
import java.util.List;

public class ExerciseHistoryGraphicFragment extends GraphicFragment {

    public ExerciseHistoryGraphicFragment() {
        PREF_KEY_INTERVAL = "interval_exercise";
    }
    private long exerciseId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if(root != null) {
            FloatingActionButton fab = root.findViewById(R.id.fab);
            fab.setVisibility(View.GONE);
            descriptionAdapter.setSumEnabled(true);
        }

        return root;
    }

    protected void initMeasureList(){
        measuresList = new ArrayList<>();

        Measures weightMeasuresWrapper = new Measures();
        List<Measure> weightMeasures = new ArrayList<>();

        Measures repetitionsMeasuresWrapper = new Measures();
        List<Measure> repetitionsMeasures = new ArrayList<>();

        weightMeasuresWrapper.setMeasures(weightMeasures);
        repetitionsMeasuresWrapper.setMeasures(repetitionsMeasures);

        measuresList.add(new DoubleMeasures(getString(R.string.weight), weightMeasuresWrapper, true));
        measuresList.add(new DoubleMeasures(getString(R.string.repetitions), repetitionsMeasuresWrapper, false));
    }

    protected void fetchData(){
        final LiveData<List<ExerciseHistory>> liveData = AppDatabase.getAppDatabase(getContext()).exerciseHistoryDao().getRange(exerciseId, startDate.getTimeInMillis(), endDate.getTimeInMillis());
        liveData.observe(this, new Observer<>() {
            @Override
            public void onChanged(@Nullable List<ExerciseHistory> exerciseHistoryList) {
                liveData.removeObserver(this);

                if (exerciseHistoryList == null)
                    return;
                progressBar.setVisibility(View.GONE);
                graphic.setRange(range);

                for (DoubleMeasures mes : measuresList) {
                    mes.clear();
                }

                for (ExerciseHistory exercise : exerciseHistoryList) {
                    extractMeasure(exercise.measureDate, (float) exercise.weight, measuresList.get(0).getLeftMeasures());
                    extractMeasure(exercise.measureDate, exercise.repetitions, measuresList.get(1).getLeftMeasures());
                }

                calculateAvg();

                graphic.setMinAbscissa(startDate);
                graphic.setMeasuresList(measuresList);

                graphic.invalidate();

                descriptionAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setExerciseId(long exerciseId) {
        this.exerciseId = exerciseId;
    }
}

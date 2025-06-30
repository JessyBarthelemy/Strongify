package com.jessy_barthelemy.strongify.fragment.profile;

import android.app.Dialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.WeightMeasure;
import com.jessy_barthelemy.strongify.database.pojo.DoubleMeasures;
import com.jessy_barthelemy.strongify.database.pojo.Measure;
import com.jessy_barthelemy.strongify.database.pojo.Measures;
import com.jessy_barthelemy.strongify.dialog.WeightMeasureDialog;
import com.jessy_barthelemy.strongify.fragment.GraphicFragment;

import java.util.ArrayList;
import java.util.List;

public class WeightGraphicFragment extends GraphicFragment {

    public WeightGraphicFragment() {
        PREF_KEY_INTERVAL = "interval";
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        title = getString(R.string.weight);
        View root = super.onCreateView(inflater, container, savedInstanceState);

        final Dialog.OnDismissListener dismissListener = new Dialog.OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialog) {
                isMeasureDialogOpened = false;
            }
        };

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMeasureDialogOpened && getContext() != null && getActivity() != null){
                    WeightMeasureDialog dialog = new WeightMeasureDialog(getContext());
                    dialog.setOwnerActivity(getActivity());
                    dialog.setDispatcher(WeightGraphicFragment.this);
                    dialog.setOnDismissListener(dismissListener);
                    dialog.show();
                }

                isMeasureDialogOpened = true;
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchData();
    }

    @Override
    protected void initMeasureList() {
        measuresList = new ArrayList<>();
        Measures weightMeasuresWrapper = new Measures();
        List<Measure> weightMeasures = new ArrayList<>();

        Measures fatMeasuresWrapper = new Measures();
        List<Measure> fatMeasures = new ArrayList<>();

        Measures muscleMeasuresWrapper = new Measures();
        List<Measure> muscleMeasures = new ArrayList<>();

        Measures bonesMeasuresWrapper = new Measures();
        List<Measure> bonesMeasures = new ArrayList<>();

        weightMeasuresWrapper.setMeasures(weightMeasures);
        fatMeasuresWrapper.setMeasures(fatMeasures);
        muscleMeasuresWrapper.setMeasures(muscleMeasures);
        bonesMeasuresWrapper.setMeasures(bonesMeasures);

        measuresList.add(new DoubleMeasures(getString(R.string.weight), weightMeasuresWrapper, true));
        measuresList.add(new DoubleMeasures(getString(R.string.weight_fat), fatMeasuresWrapper, true));
        measuresList.add(new DoubleMeasures(getString(R.string.weight_muscle), muscleMeasuresWrapper, true));
        measuresList.add(new DoubleMeasures(getString(R.string.weight_bones), bonesMeasuresWrapper, true));
    }

    protected void fetchData(){
        // Récupération du LiveData sur le thread UI (appelé depuis onViewCreated)
        final LiveData<List<WeightMeasure>> liveData = AppDatabase.getAppDatabase(getContext())
                .weightMeasureDao()
                .getRange(startDate.getTimeInMillis(), endDate.getTimeInMillis());

        liveData.observe(getViewLifecycleOwner(), new Observer<List<WeightMeasure>>() {
            @Override
            public void onChanged(@Nullable List<WeightMeasure> weights) {
                liveData.removeObserver(this);

                if(weights == null)
                    return;
                progressBar.setVisibility(View.GONE);
                graphic.setRange(range);

                for(DoubleMeasures mes : measuresList){
                    mes.clear();
                }

                for(WeightMeasure weight : weights){
                    extractMeasure(weight.weightDate, weight.weight, measuresList.get(0).getLeftMeasures());
                    extractMeasure(weight.weightDate, weight.fat, measuresList.get(1).getLeftMeasures());
                    extractMeasure(weight.weightDate, weight.muscle, measuresList.get(2).getLeftMeasures());
                    extractMeasure(weight.weightDate, weight.bones, measuresList.get(3).getLeftMeasures());
                }

                calculateAvg();

                graphic.setMinAbscissa(startDate);
                graphic.setMeasuresList(measuresList);

                graphic.invalidate();

                descriptionAdapter.notifyDataSetChanged();
            }
        });
    }
}

package com.jessy_barthelemy.strongify.fragment.profile;

import android.app.Dialog;
import android.content.DialogInterface;
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
import com.jessy_barthelemy.strongify.database.entity.BodyMeasurements;
import com.jessy_barthelemy.strongify.database.pojo.DoubleMeasures;
import com.jessy_barthelemy.strongify.database.pojo.Measure;
import com.jessy_barthelemy.strongify.database.pojo.Measures;
import com.jessy_barthelemy.strongify.dialog.BodyMeasureDialog;
import com.jessy_barthelemy.strongify.dialog.WeightMeasureDialog;
import com.jessy_barthelemy.strongify.fragment.GraphicFragment;

import java.util.ArrayList;
import java.util.List;

public class BodyMeasurementsGraphicFragment extends GraphicFragment {

    public BodyMeasurementsGraphicFragment() {
        PREF_KEY_INTERVAL = "interval_body";
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        title = getString(R.string.body_measurements);
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
                    BodyMeasureDialog dialog = new BodyMeasureDialog(getContext());
                    dialog.setOwnerActivity(getActivity());
                    dialog.setDispatcher(BodyMeasurementsGraphicFragment.this);
                    dialog.setOnDismissListener(dismissListener);
                    dialog.show();
                }

                isMeasureDialogOpened = true;
            }
        });

        return root;
    }

    protected void initMeasureList(){
        measuresList = new ArrayList<>();

        Measures neckMeasuresWrapper = new Measures();
        List<Measure> neckMeasures = new ArrayList<>();

        Measures shouldersMeasuresWrapper = new Measures();
        List<Measure> shouldersMeasures = new ArrayList<>();

        Measures chestMeasuresWrapper = new Measures();
        List<Measure> chestMeasures = new ArrayList<>();

        Measures bicepsLeftMeasuresWrapper = new Measures();
        List<Measure> bicepsLeftMeasures = new ArrayList<>();

        Measures bicepsRightMeasuresWrapper = new Measures();
        List<Measure> bicepsRightMeasures = new ArrayList<>();

        Measures forearmLeftMeasuresWrapper = new Measures();
        List<Measure> forearmLeftMeasures = new ArrayList<>();

        Measures forearmRightMeasuresWrapper = new Measures();
        List<Measure> forearmRightMeasures = new ArrayList<>();

        Measures wristRightMeasuresWrapper = new Measures();
        List<Measure> wristRightMeasures = new ArrayList<>();

        Measures wristLeftMeasuresWrapper = new Measures();
        List<Measure> wristLeftMeasures = new ArrayList<>();

        Measures waistMeasuresWrapper = new Measures();
        List<Measure> waistMeasures = new ArrayList<>();

        Measures hipsMeasuresWrapper = new Measures();
        List<Measure> hipsMeasures = new ArrayList<>();

        Measures thighLeftMeasuresWrapper = new Measures();
        List<Measure> thighLeftMeasures = new ArrayList<>();

        Measures thighRightMeasuresWrapper = new Measures();
        List<Measure> thighRightMeasures = new ArrayList<>();

        Measures calfLeftMeasuresWrapper = new Measures();
        List<Measure> calfLeftMeasures = new ArrayList<>();

        Measures calfRightMeasuresWrapper = new Measures();
        List<Measure> calfRightMeasures = new ArrayList<>();

        Measures anklesLeftMeasuresWrapper = new Measures();
        List<Measure> anklesLeftMeasures = new ArrayList<>();

        Measures anklesRightMeasuresWrapper = new Measures();
        List<Measure> anklesRightMeasures = new ArrayList<>();

        neckMeasuresWrapper.setMeasures(neckMeasures);
        shouldersMeasuresWrapper.setMeasures(shouldersMeasures);
        chestMeasuresWrapper.setMeasures(chestMeasures);
        bicepsLeftMeasuresWrapper.setMeasures(bicepsLeftMeasures);
        bicepsRightMeasuresWrapper.setMeasures(bicepsRightMeasures);
        forearmLeftMeasuresWrapper.setMeasures(forearmLeftMeasures);
        forearmRightMeasuresWrapper.setMeasures(forearmRightMeasures);
        wristRightMeasuresWrapper.setMeasures(wristRightMeasures);
        wristLeftMeasuresWrapper.setMeasures(wristLeftMeasures);
        waistMeasuresWrapper.setMeasures(waistMeasures);
        hipsMeasuresWrapper.setMeasures(hipsMeasures);
        thighLeftMeasuresWrapper.setMeasures(thighLeftMeasures);
        thighRightMeasuresWrapper.setMeasures(thighRightMeasures);
        calfLeftMeasuresWrapper.setMeasures(calfLeftMeasures);
        calfRightMeasuresWrapper.setMeasures(calfRightMeasures);
        anklesLeftMeasuresWrapper.setMeasures(anklesLeftMeasures);
        anklesRightMeasuresWrapper.setMeasures(anklesRightMeasures);

        measuresList.add(new DoubleMeasures(getString(R.string.biceps), bicepsLeftMeasuresWrapper, bicepsRightMeasuresWrapper, true));
        measuresList.add(new DoubleMeasures(getString(R.string.chest), chestMeasuresWrapper, true));
        measuresList.add(new DoubleMeasures(getString(R.string.calf), calfLeftMeasuresWrapper, calfRightMeasuresWrapper, true));
        measuresList.add(new DoubleMeasures(getString(R.string.waist), waistMeasuresWrapper, true));
        measuresList.add(new DoubleMeasures(getString(R.string.neck), neckMeasuresWrapper, false));
        measuresList.add(new DoubleMeasures(getString(R.string.shoulders), shouldersMeasuresWrapper, false));
        measuresList.add(new DoubleMeasures(getString(R.string.forearm), forearmLeftMeasuresWrapper, forearmRightMeasuresWrapper, false));
        measuresList.add(new DoubleMeasures(getString(R.string.wrist), wristLeftMeasuresWrapper, wristRightMeasuresWrapper, false));
        measuresList.add(new DoubleMeasures(getString(R.string.hips), hipsMeasuresWrapper, false));
        measuresList.add(new DoubleMeasures(getString(R.string.thigh), thighLeftMeasuresWrapper, thighRightMeasuresWrapper, false));
        measuresList.add(new DoubleMeasures(getString(R.string.ankle), anklesLeftMeasuresWrapper, anklesRightMeasuresWrapper, false));
    }

    protected void fetchData(){
        final LiveData<List<BodyMeasurements>> liveData = AppDatabase.getAppDatabase(getContext()).bodyMeasurementsDao().getRange(startDate.getTimeInMillis(), endDate.getTimeInMillis());
        liveData.observe(this, new Observer<List<BodyMeasurements>>() {
            @Override
            public void onChanged(@Nullable List<BodyMeasurements> bodyMeasurements) {
                liveData.removeObserver(this);

                if(bodyMeasurements == null)
                    return;
                progressBar.setVisibility(View.GONE);
                graphic.setRange(range);

                for(DoubleMeasures mes : measuresList){
                    mes.clear();
                }

                for(BodyMeasurements body : bodyMeasurements){
                    extractMeasure(body.measureDate, body.bicepsLeft, measuresList.get(0).getLeftMeasures());
                    extractMeasure(body.measureDate, body.bicepsRight, measuresList.get(0).getRightMeasures());
                    extractMeasure(body.measureDate, body.chest, measuresList.get(1).getLeftMeasures());
                    extractMeasure(body.measureDate, body.calfLeft, measuresList.get(2).getLeftMeasures());
                    extractMeasure(body.measureDate, body.calfRight, measuresList.get(2).getRightMeasures());
                    extractMeasure(body.measureDate, body.waist, measuresList.get(3).getLeftMeasures());
                    extractMeasure(body.measureDate, body.neck, measuresList.get(4).getLeftMeasures());
                    extractMeasure(body.measureDate, body.shoulders, measuresList.get(5).getLeftMeasures());
                    extractMeasure(body.measureDate, body.forearmLeft, measuresList.get(6).getLeftMeasures());
                    extractMeasure(body.measureDate, body.forearmRight, measuresList.get(6).getRightMeasures());
                    extractMeasure(body.measureDate, body.wristRight, measuresList.get(7).getLeftMeasures());
                    extractMeasure(body.measureDate, body.wristLeft, measuresList.get(7).getRightMeasures());
                    extractMeasure(body.measureDate, body.hips, measuresList.get(8).getLeftMeasures());
                    extractMeasure(body.measureDate, body.thighLeft, measuresList.get(9).getLeftMeasures());
                    extractMeasure(body.measureDate, body.thighRight, measuresList.get(9).getRightMeasures());
                    extractMeasure(body.measureDate, body.anklesLeft, measuresList.get(10).getLeftMeasures());
                    extractMeasure(body.measureDate, body.anklesRight, measuresList.get(10).getRightMeasures());

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

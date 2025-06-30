package com.jessy_barthelemy.strongify.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.textfield.TextInputLayout;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.asyncTask.DeleteBodyMeasureTask;
import com.jessy_barthelemy.strongify.database.asyncTask.DeleteWeightMeasureTask;
import com.jessy_barthelemy.strongify.database.asyncTask.UpsertBodyMeasureTask;
import com.jessy_barthelemy.strongify.database.entity.BodyMeasurements;
import com.jessy_barthelemy.strongify.interfaces.OnMeasureChanged;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BodyMeasureDialog extends Dialog {
    private BodyMeasurements bodyMeasurements;
    private EditText dateField;
    private OnMeasureChanged dispatcher;

    private TextInputLayout neck;
    private TextInputLayout shoulders;
    private TextInputLayout chest;
    private TextInputLayout bicepsLeft;
    private TextInputLayout bicepsRight;
    private TextInputLayout forearmLeft;
    private TextInputLayout forearmRight;
    private TextInputLayout wristRight;
    private TextInputLayout wristLeft;
    private TextInputLayout waist;
    private TextInputLayout hips;
    private TextInputLayout thighLeft;
    private TextInputLayout thighRight;
    private TextInputLayout calfLeft;
    private TextInputLayout calfRight;
    private TextInputLayout anklesLeft;
    private TextInputLayout anklesRight;

    private ImageView delete;

    public BodyMeasureDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        bodyMeasurements = new BodyMeasurements();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_body_measurement);

        neck = findViewById(R.id.measure_neck);
        shoulders = findViewById(R.id.measure_shoulders);
        chest = findViewById(R.id.measure_chest);
        bicepsLeft = findViewById(R.id.measure_biceps_left);
        bicepsRight = findViewById(R.id.measure_biceps_right);
        forearmLeft = findViewById(R.id.measure_forearm_left);
        forearmRight = findViewById(R.id.measure_forearm_right);
        wristRight = findViewById(R.id.measure_wrist_left);
        wristLeft = findViewById(R.id.measure_wrist_right);
        waist = findViewById(R.id.measure_waist);
        hips = findViewById(R.id.measure_hips);
        thighLeft = findViewById(R.id.measure_thigh_left);
        thighRight = findViewById(R.id.measure_thigh_right);
        calfLeft = findViewById(R.id.measure_calf_left);
        calfRight = findViewById(R.id.measure_calf_right);
        anklesLeft = findViewById(R.id.measure_ankle_left);
        anklesRight = findViewById(R.id.measure_ankle_right);

        dateField = findViewById(R.id.measure_date);
        delete = findViewById(R.id.delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteBodyMeasureTask task = new DeleteBodyMeasureTask(getContext(), bodyMeasurements, dispatcher);
                task.execute();

                neck.getEditText().setText("");
                shoulders.getEditText().setText("");
                chest.getEditText().setText("");
                bicepsLeft.getEditText().setText("");
                bicepsRight.getEditText().setText("");
                forearmLeft.getEditText().setText("");
                forearmRight.getEditText().setText("");
                wristRight.getEditText().setText("");
                wristLeft.getEditText().setText("");
                waist.getEditText().setText("");
                hips.getEditText().setText("");
                thighLeft.getEditText().setText("");
                thighRight.getEditText().setText("");
                calfLeft.getEditText().setText("");
                calfRight.getEditText().setText("");
                anklesLeft.getEditText().setText("");
                anklesRight.getEditText().setText("");

                delete.setVisibility(View.GONE);
            }
        });

        updateDate();

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                bodyMeasurements.measureDate.set(Calendar.YEAR, year);
                bodyMeasurements.measureDate.set(Calendar.MONTH, monthOfYear);
                bodyMeasurements.measureDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                bodyMeasurements.measureDate.set(Calendar.HOUR_OF_DAY, 0);
                bodyMeasurements.measureDate.set(Calendar.HOUR, 0);
                bodyMeasurements.measureDate.set(Calendar.MINUTE, 0);
                bodyMeasurements.measureDate.set(Calendar.SECOND, 0);
                bodyMeasurements.measureDate.set(Calendar.MILLISECOND, 0);
                updateDate();

                retrieveBodyMeasure();
            }
        };

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog =  new DatePickerDialog(getContext(), dateSetListener, bodyMeasurements.measureDate.get(Calendar.YEAR),
                                                                    bodyMeasurements.measureDate.get(Calendar.MONTH),
                                                                    bodyMeasurements.measureDate.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });

        retrieveBodyMeasure();

        Button action = findViewById(R.id.measure_action);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null)
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                bodyMeasurements.neck = safeParseFloat(neck.getEditText().getText().toString());
                bodyMeasurements.shoulders = safeParseFloat(shoulders.getEditText().getText().toString());
                bodyMeasurements.chest = safeParseFloat(chest.getEditText().getText().toString());
                bodyMeasurements.bicepsLeft = safeParseFloat(bicepsLeft.getEditText().getText().toString());
                bodyMeasurements.bicepsRight = safeParseFloat(bicepsRight.getEditText().getText().toString());
                bodyMeasurements.forearmLeft = safeParseFloat(forearmLeft.getEditText().getText().toString());
                bodyMeasurements.forearmRight = safeParseFloat(forearmRight.getEditText().getText().toString());
                bodyMeasurements.wristRight = safeParseFloat(wristRight.getEditText().getText().toString());
                bodyMeasurements.wristLeft = safeParseFloat(wristLeft.getEditText().getText().toString());
                bodyMeasurements.waist = safeParseFloat(waist.getEditText().getText().toString());
                bodyMeasurements.hips = safeParseFloat(hips.getEditText().getText().toString());
                bodyMeasurements.thighLeft = safeParseFloat(thighLeft.getEditText().getText().toString());
                bodyMeasurements.thighRight = safeParseFloat(thighRight.getEditText().getText().toString());
                bodyMeasurements.calfLeft = safeParseFloat(calfLeft.getEditText().getText().toString());
                bodyMeasurements.calfRight = safeParseFloat(calfRight.getEditText().getText().toString());
                bodyMeasurements.anklesLeft = safeParseFloat(anklesLeft.getEditText().getText().toString());
                bodyMeasurements.anklesRight = safeParseFloat(anklesRight.getEditText().getText().toString());

                if(bodyMeasurements.neck > 0 || bodyMeasurements.shoulders > 0
                    || bodyMeasurements.chest > 0 || bodyMeasurements.bicepsLeft > 0
                    || bodyMeasurements.bicepsRight > 0 || bodyMeasurements.forearmLeft > 0
                    || bodyMeasurements.forearmRight > 0 || bodyMeasurements.wristRight > 0
                    || bodyMeasurements.wristLeft > 0 || bodyMeasurements.waist > 0
                    || bodyMeasurements.hips > 0 || bodyMeasurements.thighLeft > 0
                    || bodyMeasurements.thighRight > 0 || bodyMeasurements.calfLeft > 0
                    || bodyMeasurements.calfRight > 0 || bodyMeasurements.anklesLeft > 0
                    || bodyMeasurements.anklesRight > 0){
                    UpsertBodyMeasureTask task = new UpsertBodyMeasureTask(getContext(), bodyMeasurements, dispatcher);
                    task.execute();
                }

                dismiss();
            }
        });
    }

    private void retrieveBodyMeasure(){
        final LiveData<BodyMeasurements> liveData = AppDatabase.getAppDatabase(getOwnerActivity()).bodyMeasurementsDao().getByDate(bodyMeasurements.measureDate.getTimeInMillis());
        if(getOwnerActivity() != null){
            liveData.observe((FragmentActivity)getOwnerActivity(),new Observer<BodyMeasurements>(){
                @Override
                public void onChanged(@Nullable BodyMeasurements measure) {
                    if(measure == null) {
                        bodyMeasurements.id = 0;
                        delete.setVisibility(View.GONE);
                    }else{
                        bodyMeasurements = measure;

                        neck.getEditText().setText(String.valueOf(bodyMeasurements.neck));
                        shoulders.getEditText().setText(String.valueOf(bodyMeasurements.shoulders));
                        chest.getEditText().setText(String.valueOf(bodyMeasurements.chest));
                        bicepsLeft.getEditText().setText(String.valueOf(bodyMeasurements.bicepsLeft));
                        bicepsRight.getEditText().setText(String.valueOf(bodyMeasurements.bicepsRight));
                        forearmLeft.getEditText().setText(String.valueOf(bodyMeasurements.forearmLeft));
                        forearmRight.getEditText().setText(String.valueOf(bodyMeasurements.forearmRight));
                        wristRight.getEditText().setText(String.valueOf(bodyMeasurements.wristRight));
                        wristLeft.getEditText().setText(String.valueOf(bodyMeasurements.wristLeft));
                        waist.getEditText().setText(String.valueOf(bodyMeasurements.waist));
                        hips.getEditText().setText(String.valueOf(bodyMeasurements.hips));
                        thighLeft.getEditText().setText(String.valueOf(bodyMeasurements.thighLeft));
                        thighRight.getEditText().setText(String.valueOf(bodyMeasurements.thighRight));
                        calfLeft.getEditText().setText(String.valueOf(bodyMeasurements.calfLeft));
                        calfRight.getEditText().setText(String.valueOf(bodyMeasurements.calfRight));
                        anklesLeft.getEditText().setText(String.valueOf(bodyMeasurements.anklesLeft));
                        anklesRight.getEditText().setText(String.valueOf(bodyMeasurements.anklesRight));

                        delete.setVisibility(View.VISIBLE);
                    }

                    liveData.removeObserver(this);
                }
            });
        }
    }

    private float safeParseFloat(String val){
        if(val == null || val.isEmpty())
            return 0;

        try{
            return Float.parseFloat(val);
        }catch (NumberFormatException e){
            return 0f;
        }
    }

    private void updateDate(){
        dateField.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(bodyMeasurements.measureDate.getTime()));
    }

    public void setDispatcher(OnMeasureChanged dispatcher) {
        this.dispatcher = dispatcher;
    }
}

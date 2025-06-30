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
import com.jessy_barthelemy.strongify.database.asyncTask.DeleteWeightMeasureTask;
import com.jessy_barthelemy.strongify.database.asyncTask.UpsertWeightMeasureTask;
import com.jessy_barthelemy.strongify.database.entity.WeightMeasure;
import com.jessy_barthelemy.strongify.interfaces.OnMeasureChanged;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeightMeasureDialog extends Dialog {
    private WeightMeasure weightMeasure;
    private EditText dateField;
    private OnMeasureChanged dispatcher;

    private TextInputLayout massField;
    private TextInputLayout fatField;
    private TextInputLayout muscleField;
    private TextInputLayout bonesField;
    private ImageView delete;


    public WeightMeasureDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        weightMeasure = new WeightMeasure();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_weight_measure);

        dateField = findViewById(R.id.weight_date);
        massField = findViewById(R.id.weight_mass);
        fatField = findViewById(R.id.weight_fat);
        muscleField = findViewById(R.id.weight_muscle);
        bonesField = findViewById(R.id.weight_bones);

        delete = findViewById(R.id.delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteWeightMeasureTask task = new DeleteWeightMeasureTask(getContext(), weightMeasure, dispatcher);
                task.execute();

                massField.getEditText().setText("");
                fatField.getEditText().setText("");
                muscleField.getEditText().setText("");
                bonesField.getEditText().setText("");

                delete.setVisibility(View.GONE);
            }
        });

        updateDate();

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                weightMeasure.weightDate.set(Calendar.YEAR, year);
                weightMeasure.weightDate.set(Calendar.MONTH, monthOfYear);
                weightMeasure.weightDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                weightMeasure.weightDate.set(Calendar.HOUR_OF_DAY, 0);
                weightMeasure.weightDate.set(Calendar.HOUR, 0);
                weightMeasure.weightDate.set(Calendar.MINUTE, 0);
                weightMeasure.weightDate.set(Calendar.SECOND, 0);
                weightMeasure.weightDate.set(Calendar.MILLISECOND, 0);
                updateDate();

                retrieveWeightMeasure();
            }
        };

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog =  new DatePickerDialog(getContext(), dateSetListener, weightMeasure.weightDate.get(Calendar.YEAR),
                                                                    weightMeasure.weightDate.get(Calendar.MONTH),
                                                                    weightMeasure.weightDate.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });

        retrieveWeightMeasure();

        Button action = findViewById(R.id.weight_action);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null)
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                weightMeasure.weight = safeParseFloat(massField.getEditText().getText().toString());
                weightMeasure.fat = safeParseFloat(fatField.getEditText().getText().toString());
                weightMeasure.muscle = safeParseFloat(muscleField.getEditText().getText().toString());
                weightMeasure.bones = safeParseFloat(bonesField.getEditText().getText().toString());

                if(weightMeasure.weight > 0 || weightMeasure.muscle > 0
                   || weightMeasure.fat > 0 || weightMeasure.bones > 0){
                    UpsertWeightMeasureTask task = new UpsertWeightMeasureTask(getContext(), weightMeasure, dispatcher);
                    task.execute();
                }

                dismiss();
            }
        });
    }

    private void retrieveWeightMeasure(){
        final LiveData<WeightMeasure> liveData = AppDatabase.getAppDatabase(getOwnerActivity()).weightMeasureDao().getByDate(weightMeasure.weightDate.getTimeInMillis());
        if(getOwnerActivity() != null){
            liveData.observe((FragmentActivity)getOwnerActivity(),new Observer<WeightMeasure>(){
                @Override
                public void onChanged(@Nullable WeightMeasure measure) {
                    if(measure == null) {
                        weightMeasure.id = 0;
                        delete.setVisibility(View.GONE);
                    }else{
                        weightMeasure = measure;

                        massField.getEditText().setText(String.valueOf(weightMeasure.weight));
                        fatField.getEditText().setText(String.valueOf(weightMeasure.fat));
                        muscleField.getEditText().setText(String.valueOf(weightMeasure.muscle));
                        bonesField.getEditText().setText(String.valueOf(weightMeasure.bones));

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
        dateField.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(weightMeasure.weightDate.getTime()));
    }

    public void setDispatcher(OnMeasureChanged dispatcher) {
        this.dispatcher = dispatcher;
    }
}

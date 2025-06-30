package com.jessy_barthelemy.strongify.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.helper.TimeSpan;
import com.jessy_barthelemy.strongify.interfaces.TimeSpanManager;

public class TimeSpanPickerDialog extends Dialog{
    private static final int MAX_MINUTES = 60;
    private static final int MAX_SECONDS = 60;

    private TimeSpanManager manager;
    private TimeSpan timeSpan;
    private String title;
    private int sourceId;

    public TimeSpanPickerDialog(@NonNull Context context, TimeSpan timeSpan, TimeSpanManager manager, String title, int sourceId) {
        super(context);
        this.manager = manager;
        this.timeSpan = timeSpan;
        this.title = title;
        this.sourceId = sourceId;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timespan);

        final NumberPicker minutesPicker = findViewById(R.id.minutes);
        final NumberPicker secondsPicker = findViewById(R.id.seconds);

        TextView titleView = findViewById(R.id.timespan_title);
        titleView.setText(title);

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(MAX_MINUTES);

        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(MAX_SECONDS);

        minutesPicker.setValue(timeSpan.minutes);
        secondsPicker.setValue(timeSpan.seconds);

        Button action = findViewById(R.id.dialog_action);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timeSpan.minutes = minutesPicker.getValue();
                timeSpan.seconds = secondsPicker.getValue();

                manager.onTimeSpanChanged(timeSpan, sourceId);
                dismiss();
            }
        });
    }

}

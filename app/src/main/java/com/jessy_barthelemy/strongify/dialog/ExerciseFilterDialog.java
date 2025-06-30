package com.jessy_barthelemy.strongify.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.adapter.equipment.EquipmentListAdapter;
import com.jessy_barthelemy.strongify.database.entity.Equipment;
import com.jessy_barthelemy.strongify.interfaces.EquipmentManager;

import java.util.List;

public class ExerciseFilterDialog extends Dialog{
    private List<Equipment> dataSet;
    private EquipmentManager manager;
    private boolean isSearchDisabled;

    public ExerciseFilterDialog(@NonNull Context context, List<Equipment> dataSet, EquipmentManager manager) {
        super(context);
        this.dataSet = dataSet;
        this.manager = manager;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public ExerciseFilterDialog(@NonNull Context context, List<Equipment> dataSet, EquipmentManager manager, boolean isSearchDisabled) {
        this(context, dataSet, manager);
        this.isSearchDisabled = isSearchDisabled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_filter);

        RecyclerView recyclerView = findViewById(R.id.equipments_list);
        EquipmentListAdapter adapter = new EquipmentListAdapter(dataSet, getContext());
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        TextView title = findViewById(R.id.filter_title);
        title.setText(isSearchDisabled ? R.string.exercise_update_equipment : R.string.filter_title);

        Button action = findViewById(R.id.filter_action);
        action.setText(isSearchDisabled ? R.string.workout_update_action : R.string.filter_action);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.onEquipmentChosen(dataSet);
                dismiss();
            }
        });
    }

}

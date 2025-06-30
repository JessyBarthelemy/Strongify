package com.jessy_barthelemy.strongify.adapter.equipment;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.asyncTask.LoadImageTask;
import com.jessy_barthelemy.strongify.database.entity.Equipment;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;

public class EquipmentViewHolder extends RecyclerView.ViewHolder{

    private TextView title;
    private CheckBox check;
    private Equipment equipment;
    private ImageView imageView;

    EquipmentViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.equipment_title);
        check = itemView.findViewById(R.id.equipment_check);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check.toggle();
            }
        });

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                equipment.isSelected = isChecked;
            }
        });

        imageView = itemView.findViewById(R.id.equipment_icon);
    }

    public void bindExercise(Equipment equipment, Context context){
        if(equipment == null)
            return;

        this.equipment = equipment;

        title.setText(ApplicationHelper.getEquipmentTitle(context, equipment));
        check.setChecked(equipment.isSelected);
        new LoadImageTask(context, null , ApplicationHelper.getDrawable(context, equipment.title), imageView).execute();
    }
}
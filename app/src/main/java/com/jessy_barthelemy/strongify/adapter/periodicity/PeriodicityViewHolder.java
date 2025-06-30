package com.jessy_barthelemy.strongify.adapter.periodicity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.pojo.Periodicity;


public class PeriodicityViewHolder extends RecyclerView.ViewHolder{

    private TextView title;
    private CheckBox check;

    PeriodicityViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.periodicity_title);
        check = itemView.findViewById(R.id.periodicity_check);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check.toggle();
            }
        });
    }

    public CheckBox getCheck(){
        return check;
    }

    public void bindPeriodicity(Periodicity periodicity){
        title.setText(periodicity.getDay());
        check.setChecked(periodicity.isChecked());
    }
}

package com.jessy_barthelemy.strongify.adapter.history;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.entity.WorkoutHistory;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.TimeSpan;

import java.text.DateFormat;

public class HistoryViewHolder extends RecyclerView.ViewHolder{

    private Context context;
    private TextView title;
    private TextView content;
    private TextView date;
    private TextView duration;

    HistoryViewHolder(final Context context, View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.list_history_title);
        content = itemView.findViewById(R.id.list_history_content);
        date = itemView.findViewById(R.id.list_history_date);
        duration = itemView.findViewById(R.id.list_history_duration);
        this.context = context;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(content.getVisibility() == View.VISIBLE){
                    content.setVisibility(View.GONE);
                }else {
                    content.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void bindHistory(WorkoutHistory history){
        title.setText(history.title);
        content.setText(Html.fromHtml(history.text));
        date.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(history.date));

        TimeSpan timeSpan = ApplicationHelper.getTimeSpan(history.duration);
        duration.setText(context.getString(R.string.time, timeSpan.hours, timeSpan.minutes));
    }
}

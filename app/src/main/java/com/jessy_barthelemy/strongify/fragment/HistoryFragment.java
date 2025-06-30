package com.jessy_barthelemy.strongify.fragment;


import android.app.DatePickerDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.adapter.history.HistoryListAdapter;
import com.jessy_barthelemy.strongify.adapter.workout.SimpleItemTouchHelperCallback;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.WorkoutHistory;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class HistoryFragment extends BaseFragment {

    private RecyclerView historyView;
    private Calendar start;
    private Calendar end;
    private HistoryListAdapter adapter;
    private TextView noData;

    public HistoryFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        historyView = root.findViewById(R.id.history_list);

        start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);

        end = Calendar.getInstance();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);

        final TextView startDate = root.findViewById(R.id.history_start_date);
        final TextView endDate = root.findViewById(R.id.history_end_date);
        noData = root.findViewById(R.id.history_no_data);

        startDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(start.getTime()));
        endDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(end.getTime()));

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            start.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            start.set(Calendar.MONTH, month);
                            start.set(Calendar.YEAR, year);
                            start.set(Calendar.HOUR_OF_DAY, 0);

                            if(start.after(end)){
                                end.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                end.set(Calendar.MONTH, month);
                                end.set(Calendar.YEAR, year);
                                end.set(Calendar.HOUR_OF_DAY, 23);
                                end.set(Calendar.MINUTE, 59);
                                end.set(Calendar.SECOND, 59);
                                endDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(end.getTime()));
                            }
                            startDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(start.getTime()));
                            getHistory();
                        }
                    }, start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    new DatePickerDialog(
                            getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            end.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            end.set(Calendar.MONTH, month);
                            end.set(Calendar.YEAR, year);
                            end.set(Calendar.HOUR_OF_DAY, 23);
                            end.set(Calendar.MINUTE, 59);
                            end.set(Calendar.SECOND, 59);

                            if(end.before(start)){
                                start.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                start.set(Calendar.MONTH, month);
                                start.set(Calendar.YEAR, year);
                                start.set(Calendar.HOUR_OF_DAY, 0);
                                startDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(start.getTime()));
                            }
                            endDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(end.getTime()));
                            getHistory();
                        }
                    }, end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        getHistory();
        return root;
    }

    private void getHistory(){
        if(getActivity() != null){
            AppDatabase.getAppDatabase(getActivity()).historyDao().getBetween(start.getTimeInMillis(), end.getTimeInMillis()).observe(getActivity(), new Observer<List<WorkoutHistory>>() {
                @Override
                public void onChanged(@Nullable List<WorkoutHistory> workoutHistories) {
                    if(adapter == null){
                        adapter = new HistoryListAdapter(getActivity(), workoutHistories);
                        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        historyView.setAdapter(adapter);

                        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, historyView);
                        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                        touchHelper.attachToRecyclerView(historyView);
                        historyView.setLayoutManager(layoutManager);
                    }else{
                        adapter.setDataSet(workoutHistories);
                        adapter.notifyDataSetChanged();
                    }

                    if(workoutHistories != null && workoutHistories.size() == 0)
                        noData.setVisibility(View.VISIBLE);
                    else
                        noData.setVisibility(View.GONE);
                }
            });
        }
    }
}

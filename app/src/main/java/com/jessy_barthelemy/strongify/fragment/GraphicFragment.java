package com.jessy_barthelemy.strongify.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.adapter.graphDescription.GraphDescriptionAdapter;
import com.jessy_barthelemy.strongify.customLayout.Graphic;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.WeightMeasure;
import com.jessy_barthelemy.strongify.database.pojo.DoubleMeasures;
import com.jessy_barthelemy.strongify.database.pojo.Measure;
import com.jessy_barthelemy.strongify.database.pojo.Measures;
import com.jessy_barthelemy.strongify.dialog.WeightMeasureDialog;
import com.jessy_barthelemy.strongify.interfaces.GraphicLoader;
import com.jessy_barthelemy.strongify.interfaces.OnMeasureChanged;
import com.jessy_barthelemy.strongify.interfaces.Refreshable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class GraphicFragment extends BaseFragment implements OnMeasureChanged, GraphicLoader, Refreshable {

    protected boolean isMeasureDialogOpened;
    protected Calendar startDate;
    protected Calendar endDate;
    protected int range;
    protected ProgressBar progressBar;
    protected Graphic graphic;
    protected String title;
    protected List<DoubleMeasures> measuresList;
    protected GraphDescriptionAdapter descriptionAdapter;

    protected String PREF_KEY_INTERVAL; //= "interval";
    public static final int INTERVAL_MONTH = 0;
    public static final int INTERVAL_YEAR = 1;
    public static final int INTERVAL_3_MONTHS = 2;

    public GraphicFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_graphic, container, false);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        range = preferences.getInt(PREF_KEY_INTERVAL, INTERVAL_MONTH);
        loadInterval();

        progressBar = root.findViewById(R.id.graphic_loader);
        graphic = root.findViewById(R.id.graphic);
        graphic.setDelegate(this);

        final TextView titleView = root.findViewById(R.id.title);
        titleView.setText(title);

        final Spinner rangeSpinner = root.findViewById(R.id.range);
        rangeSpinner.setSelection(range,false);
        rangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(range != position) {
                    range = position;

                    preferences.edit().putInt(PREF_KEY_INTERVAL, range).apply();
                    loadInterval();
                    fetchData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ListView descriptionList = root.findViewById(R.id.graph_description);
        descriptionList.addHeaderView(getLayoutInflater().inflate(R.layout.header_graph_description, descriptionList, false));

        initMeasureList();

        descriptionAdapter = new GraphDescriptionAdapter(getContext(), measuresList);
        descriptionAdapter.setDelegate(this);
        descriptionAdapter.setSumEnabled(false);
        descriptionList.setAdapter(descriptionAdapter);

        fetchData();

        //menu
        final ImageView menu = root.findViewById(R.id.menu);

        final PopupMenu popup = new PopupMenu(getActivity(), menu);
        popup.getMenuInflater().inflate(R.menu.graph_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.hide_dots){
                    graphic.setShowDots(item.isChecked());
                    graphic.invalidate();
                    item.setChecked(!item.isChecked());
                }
                return true;
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });

        return root;
    }

    protected abstract void initMeasureList();

    protected abstract void fetchData();

    protected void extractMeasure(Calendar x, float y, Measures measures){
        if(y == 0)
            return;

        Measure measure = new Measure();
        measure.x = x;
        measure.y = y;
        measures.getMeasures().add(measure);

        if(y > measures.getMax())
            measures.setMax(y);

        if(y < measures.getMin() || measures.getMin() == 0)
            measures.setMin(y);

        measures.setSum(measures.getSum() + y);
    }

    @Override
    public void onMeasureChanged() {
        fetchData();
        isMeasureDialogOpened = false;
    }

    protected void loadInterval(){
        switch (range){
            case INTERVAL_MONTH :
                startDate = Calendar.getInstance();
                startDate.set(Calendar.DAY_OF_MONTH, 1);
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);
                break;
            case INTERVAL_YEAR :
                startDate = Calendar.getInstance();
                startDate.set(Calendar.MONTH, 0);
                startDate.set(Calendar.DAY_OF_MONTH, 1);
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);
                break;
            case INTERVAL_3_MONTHS :
                startDate = Calendar.getInstance();
                startDate.add(Calendar.MONTH, -2);
                startDate.set(Calendar.DAY_OF_MONTH, 1);
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);
                break;
        }

        endDate = Calendar.getInstance();
    }

    @Override
    public void onLoadFinished() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        fetchData();
    }

    protected void calculateAvg(){
        int size;
        for(DoubleMeasures mes : measuresList){
            //left
            if(mes.getLeftMeasures() == null || mes.getLeftMeasures().getMeasures() == null)
                continue;

            size = mes.getLeftMeasures().getMeasures().size();
            if(size > 0)
                mes.getLeftMeasures().setAverage(mes.getLeftMeasures().getSum() / size);

            //right
            if(mes.getRightMeasures() == null || mes.getLeftMeasures().getMeasures() == null)
                continue;

            size = mes.getRightMeasures().getMeasures().size();
            if(size > 0)
                mes.getRightMeasures().setAverage(mes.getRightMeasures().getSum() / size);
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

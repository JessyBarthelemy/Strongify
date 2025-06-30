package com.jessy_barthelemy.strongify.customLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.pojo.DoubleMeasures;
import com.jessy_barthelemy.strongify.database.pojo.Measure;
import com.jessy_barthelemy.strongify.database.pojo.Measures;
import com.jessy_barthelemy.strongify.fragment.profile.WeightGraphicFragment;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.interfaces.GraphicLoader;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Graphic extends View {

    private Paint paint;
    private List<DoubleMeasures> measuresList;
    private int range;
    private int gridAbscissa;
    private int gridOrdinate;
    private boolean showDots;

    //grid
    private Paint gridPaint;
    //units
    private Paint unitPaint;
    private Paint pointPaint;

    //axis
    private float axisMargin;
    private float axisUnitMargin;

    private Calendar minAbscissa;
    private int stepAbscissa;
    private int stepAbscissaUnit;
    private SimpleDateFormat formatAbscissa;

    private int minOrdinate;
    private int maxOrdinate;
    private Rect unitBounds = new Rect();

    private DecimalFormat decimalFormat;

    private GraphicLoader delegate;

    public Graphic(Context context) {
        super(context);
        init();
    }

    public Graphic(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Graphic(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        showDots = true;
        paint = new Paint();
        int axisColor = ApplicationHelper.getThemeColor(getContext(), R.attr.colorAccent2);
        int gridColor = ApplicationHelper.getThemeColor(getContext(), R.attr.colorSeparator);
        axisMargin = getResources().getDimension(R.dimen.graph_axis_margin);
        axisUnitMargin = axisMargin - getResources().getDimension(R.dimen.graph_unit_margin);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(axisColor);
        float strokeWidth = 3f;
        paint.setStrokeWidth(strokeWidth);

        gridPaint = new Paint();
        gridPaint.setAntiAlias(true);
        gridPaint.setStyle(Paint.Style.FILL);
        gridPaint.setColor(gridColor);
        gridPaint.setStrokeWidth(strokeWidth);

        unitPaint = new Paint();
        unitPaint.setAntiAlias(true);
        unitPaint.setStyle(Paint.Style.FILL);
        unitPaint.setColor(axisColor);
        unitPaint.setStrokeWidth(15f);
        unitPaint.setTextSize(getResources().getDimension(R.dimen.graph_unit));

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(axisColor);
        pointPaint.setStrokeWidth(3F);

        unitBounds = new Rect();

        decimalFormat = new DecimalFormat("#.##");
    }

    private void calibrateOne(Measures measures, int[] colors, int colorIndex){
        measures.setColor(colors[colorIndex]);
        if(measures.getMeasures() != null){
            for(Measure measure : measures.getMeasures()){
                if(measure.y < minOrdinate || minOrdinate == 0)
                    minOrdinate = (int)measure.y;
                if(measure.y > maxOrdinate )
                    maxOrdinate = (int)measure.y;
            }
        }
    }

    private void calibrate(){
        int[] colors = getContext().getResources().getIntArray(R.array.graph_measures);
        int colorIndex = 0;
        for(DoubleMeasures measures : measuresList){
            if(measures.getLeftMeasures() != null){
                calibrateOne(measures.getLeftMeasures(), colors, colorIndex);
                colorIndex++;
                if(colorIndex >= colors.length)
                    colorIndex = 0;
            }

            if(measures.getRightMeasures() != null){
                calibrateOne(measures.getRightMeasures(), colors, colorIndex);
                colorIndex++;
                if(colorIndex >= colors.length)
                    colorIndex = 0;
            }
        }

        if(maxOrdinate - minOrdinate > 10){
            minOrdinate = (int)Math.floor(minOrdinate / 10f) * 10;
            maxOrdinate = (int)Math.ceil(maxOrdinate / 10f) * 10;
            gridOrdinate = (maxOrdinate - minOrdinate) / 10;
        }

        gridOrdinate = 5;

        switch(range){
            case WeightGraphicFragment.INTERVAL_YEAR:
                gridAbscissa = 12;
                stepAbscissa = 1;
                stepAbscissaUnit = Calendar.MONTH;
                formatAbscissa = new SimpleDateFormat("MM" , Locale.getDefault());
                break;
            case WeightGraphicFragment.INTERVAL_3_MONTHS:
                gridAbscissa = 8;
                stepAbscissa = 12;
                stepAbscissaUnit = Calendar.DAY_OF_MONTH;

                formatAbscissa = new SimpleDateFormat("dd-MM" , Locale.getDefault());
                break;
            default:
                gridAbscissa = 8;
                stepAbscissa = 4;
                stepAbscissaUnit = Calendar.DAY_OF_MONTH;

                formatAbscissa = new SimpleDateFormat("dd" , Locale.getDefault());
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(measuresList == null || minAbscissa == null)
            return;

        int width = getWidth();
        int height = getHeight();
        float gridWidth = width - (axisMargin*2f);

        float gridSpacingX  = gridWidth / gridAbscissa;
        float gridSpacingY  = (height - (axisMargin*2f)) / gridOrdinate;

        float columnX = 0;
        float columnY = 0;
        Calendar abscissa = (Calendar)minAbscissa.clone();

        float ordinate = maxOrdinate;
        String unit = String.valueOf(formatAbscissa.format(abscissa.getTime()));
        unitPaint.getTextBounds(unit, 0, unit.length(), unitBounds);
        canvas.drawText(unit, axisMargin - (unitBounds.width() / 2f), height - axisUnitMargin + (unitBounds.height()/2f), unitPaint);

        float ordinateStep = ((float)(maxOrdinate - minOrdinate)) / gridOrdinate;
        //ordinate
        for(int i = 0; i < gridOrdinate; i++){
            columnY = axisMargin + (gridSpacingY*i);
            canvas.drawLine(axisMargin, columnY, width - axisMargin, columnY, gridPaint);

            //calculation of units
            unit = decimalFormat.format(ordinate);
            unitPaint.getTextBounds(unit, 0, unit.length(), unitBounds);
            canvas.drawText(unit, axisUnitMargin - unitBounds.width(), columnY + (unitBounds.height() / 2f), unitPaint);

            ordinate -= ordinateStep;
        }

        //abscissa
        for(int i = 0; i < gridAbscissa; i++){
            columnX = axisMargin + (gridSpacingX*(i+1));
            canvas.drawLine(columnX, axisMargin, columnX, height - axisMargin, gridPaint);

            //calculation of units
            abscissa.add(stepAbscissaUnit, stepAbscissa);

            unit = String.valueOf(formatAbscissa.format(abscissa.getTime()));
            unitPaint.getTextBounds(unit, 0, unit.length(), unitBounds);
            canvas.drawText(unit, columnX - (unitBounds.width() / 2f), (height - axisUnitMargin) + (unitBounds.height() / 2f), unitPaint);
        }

        //axis
        canvas.drawLine(axisMargin, height - axisMargin, width - axisMargin, height - axisMargin, paint);
        canvas.drawLine(axisMargin, axisMargin, axisMargin, height - axisMargin, paint);

        long timeSpan = abscissa.getTimeInMillis() - minAbscissa.getTimeInMillis();
        float valueSpan = maxOrdinate - minOrdinate;
        columnY += gridSpacingY;
        //drawing of curves
        float abscissaCoordinate = 0;
        float ordinateCoordinate = 0;
        int measureStart = 0;
        float lastX = 0;
        float lastY = 0;
        float firstPointX = 0;
        float firstPointY = 0;
        if(measuresList != null && measuresList.size() > 0){
            for(int i = 0, len = measuresList.size(); i < len; i++){
                if(measuresList.get(i).getLeftMeasures() != null){
                    drawOne(i, measuresList.get(i).getLeftMeasures(), measuresList.get(i).isActive(),
                    measureStart, lastX, lastY, firstPointX, firstPointY, canvas, columnX, columnY,
                    abscissaCoordinate, ordinateCoordinate, valueSpan, timeSpan);
                }

                if(measuresList.get(i).getRightMeasures() != null){
                    drawOne(i, measuresList.get(i).getRightMeasures(), measuresList.get(i).isActive(),
                            measureStart, lastX, lastY, firstPointX, firstPointY, canvas, columnX, columnY,
                            abscissaCoordinate, ordinateCoordinate, valueSpan, timeSpan);
                }
            }
        }

        if(delegate != null)
            delegate.onLoadFinished();
    }

    private void drawOne(int i, Measures measures , boolean isActive, int measureStart, float lastX, float lastY, float firstPointX, float firstPointY, Canvas canvas, float columnX, float columnY,
                            float abscissaCoordinate, float ordinateCoordinate, float valueSpan, float timeSpan){
        pointPaint.setColor(measures.getColor());
        if(isActive && measures.getMeasures().size() > 1){
            //we have a value before the minimum
            measureStart = measures.getMeasures().get(0).x.compareTo(minAbscissa) < 0 ? 2 : 1;

            lastX = firstPointX = axisMargin + ((measures.getMeasures().get(measureStart-1).x.getTimeInMillis() - minAbscissa.getTimeInMillis()) * (columnX - axisMargin) / timeSpan);
            lastY = firstPointY = axisMargin + (((maxOrdinate - measures.getMeasures().get(measureStart-1).y) * (columnY - axisMargin)) / valueSpan);
            if(showDots)
                canvas.drawCircle(firstPointX, firstPointY, 10, pointPaint);

            for(int j = measureStart, jLen = measures.getMeasures().size(); j < jLen; j++) {
                abscissaCoordinate = axisMargin + ((measures.getMeasures().get(j).x.getTimeInMillis() - minAbscissa.getTimeInMillis()) * (columnX - axisMargin) / timeSpan);
                ordinateCoordinate = axisMargin + (((maxOrdinate - measures.getMeasures().get(j).y) * (columnY - axisMargin)) / valueSpan);

                if(showDots)
                    canvas.drawCircle(abscissaCoordinate, ordinateCoordinate, 10, pointPaint);
                canvas.drawLine(lastX, lastY, abscissaCoordinate, ordinateCoordinate, pointPaint);

                lastX = abscissaCoordinate;
                lastY = ordinateCoordinate;
            }

            //if we have a measure before the minimum we draw a line on the y axis but not a point
            if(measureStart == 2){
                //we use Thales's theorem to place the starting point
                float heightOfTriangle = (((measures.getMeasures().get(0).y - measures.getMeasures().get(1).y) * (columnY - axisMargin)) / valueSpan);
                float widthOfTriangle = ((measures.getMeasures().get(1).x.getTimeInMillis() - measures.getMeasures().get(0).x.getTimeInMillis()) * (columnX - axisMargin) / timeSpan);
                float withOfTriangleFromOrigin = firstPointX - axisMargin;

                float heightOfOriginPoint = (withOfTriangleFromOrigin * heightOfTriangle) / widthOfTriangle;
                canvas.drawLine(axisMargin, firstPointY - heightOfOriginPoint, firstPointX, firstPointY, pointPaint);
            }
        }
    }

    public void setMeasuresList(List<DoubleMeasures> measuresList) {
        this.measuresList = measuresList;
        calibrate();
    }

    public void setDelegate(GraphicLoader delegate) {
        this.delegate = delegate;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setMinAbscissa(Calendar minAbscissa) {
        this.minAbscissa = minAbscissa;
    }

    public void setShowDots(boolean showDots) {
        this.showDots = showDots;
    }
}
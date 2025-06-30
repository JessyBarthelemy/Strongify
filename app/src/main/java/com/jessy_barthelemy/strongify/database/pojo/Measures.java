package com.jessy_barthelemy.strongify.database.pojo;

import java.util.List;

public class Measures {

    private int color;
    private float max;
    private float min;
    private float sum;
    private float average;

    private List<Measure> measures;

    public List<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public void clear(){
        getMeasures().clear();
        min = 0;
        max = 0;
        average = 0;
        sum = 0;
    }
}

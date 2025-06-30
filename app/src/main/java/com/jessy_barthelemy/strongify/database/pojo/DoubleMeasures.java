package com.jessy_barthelemy.strongify.database.pojo;

import java.util.List;

public class DoubleMeasures {

    private String name;
    private boolean isActive;
    private Measures leftMeasures;
    private Measures rightMeasures;

    public DoubleMeasures(String name, Measures leftMeasures, boolean isActive){
        this.name = name;
        this.leftMeasures = leftMeasures;
        this.isActive = isActive;
    }

    public DoubleMeasures(String name, Measures leftMeasures, Measures rightMeasures, boolean isActive){
        this.name = name;
        this.leftMeasures = leftMeasures;
        this.rightMeasures = rightMeasures;
        this.isActive = isActive;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Measures getLeftMeasures() {
        return leftMeasures;
    }

    public void setLeftMeasures(Measures leftMeasures) {
        this.leftMeasures = leftMeasures;
    }

    public Measures getRightMeasures() {
        return rightMeasures;
    }

    public void setRightMeasures(Measures rightMeasures) {
        this.rightMeasures = rightMeasures;
    }

    public void clear(){
        if(leftMeasures != null)
            leftMeasures.clear();

        if(rightMeasures != null)
            rightMeasures.clear();
    }

    public boolean isUnique(){
        return rightMeasures != null;
    }
}

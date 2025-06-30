package com.jessy_barthelemy.strongify.database.pojo;

public class Periodicity {
    private int id;
    private String day;
    private boolean checked;

    public Periodicity(int id, String day, boolean checked) {
        this.id = id;
        this.day = day;
        this.checked = checked;
    }

    public String getDay() {
        return day;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

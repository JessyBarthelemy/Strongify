package com.jessy_barthelemy.strongify.helper;

public class TimeSpan {
    public int hours;
    public int minutes;
    public int seconds;

    public int getTotalSeconds(){
        return (hours * 3600) + (minutes * 60) + seconds;
    }
}

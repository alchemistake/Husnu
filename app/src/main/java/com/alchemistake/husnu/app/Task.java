package com.alchemistake.husnu.app;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by Alchemistake on 08/02/16.
 */
public class Task {
    private String name;
    private double weeklyHour;
    private double currentHour;

    private boolean running;
    private long startDate;

    public String getName() {
        return name;
    }

    public double getWeeklyHour() {
        return weeklyHour;
    }

    public double getCurrentHour() {
        return currentHour;
    }

    public void setCurrentHour(double currentHour) {
        this.currentHour = currentHour;
    }

    public void setWeeklyHour(double weeklyHour) {
        this.weeklyHour = weeklyHour;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int statusColor() {
        if (currentHour > weeklyHour) {
            return currentHour > 2 * weeklyHour ? Color.rgb(255,0,0) : Color.rgb(255, (int) Math.floor(255 * (1 - (currentHour - weeklyHour) / weeklyHour)), 0);
        }
        return Color.rgb((int) Math.floor(255 * (1 - (weeklyHour - currentHour) / weeklyHour)), 255, 0);
    }

    public boolean isRunning() {
        return running;
    }

    public boolean start(){
        if(!running){
            startDate = System.currentTimeMillis();
            running = true;
            return true;
        }

        return false;
    }

    public boolean stop(){
        if(running){
            long diff = System.currentTimeMillis() - startDate;
            double diffInHour = diff / 3600000.0;

            if(diffInHour >= 8){
                return false;
            }

            currentHour = currentHour - diffInHour;
            if(currentHour < 0) currentHour = 0;

            running = false;
            return true;
        }

        return false;
    }

    public Task(String name, double weeklyHour) {
        this.name = name;
        this.weeklyHour = weeklyHour;
        currentHour = weeklyHour;
        running = false;
    }

    public void update(){
        currentHour += weeklyHour;
    }

    public void downdate(){
        currentHour -= weeklyHour;
    }
}

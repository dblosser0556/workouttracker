package com.sighe.workouttracker.data;

import com.sighe.workouttracker.utility.utils;

/**
 * Created by dad on 10/26/2016.
 */

public class DailyWorkOut {
    private int workoutMode;
    private String workoutDate;
    private double distance;
    private double steps;
    private double aveStepsPerMin;
    private double aveSpeed;
    private long elapseTime;

    public DailyWorkOut() {
    }

    public DailyWorkOut(int workoutMode, String date, double distance,
                        double steps, long elapseTime) {
        this.workoutMode = workoutMode;
        this.workoutDate = date;
        this.distance = distance;
        this.steps = steps;
        this.elapseTime = elapseTime;
        this.aveSpeed = distance / (elapseTime * utils.MilliToHours);
        this.aveStepsPerMin = steps / (elapseTime * utils.MilliToMins);
    }

    public int getWorkoutMode() {
        return workoutMode;
    }

    public String getDate() {
        return workoutDate;
    }

    public double getDistance() {
        return distance;
    }

    public double getSteps() {
        return steps;
    }

    public double getAveStepsPerMin() {
        return aveStepsPerMin;
    }

    public double getAveSpeed() {
        return aveSpeed;
    }

    public long getElapseTime() {
        return elapseTime;
    }
}

package com.sighe.workouttracker.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;

/**
 * Created by dad on 10/19/2016.
 */

public class WorkOut implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<WorkOut> CREATOR = new Parcelable.Creator<WorkOut>() {
        @Override
        public WorkOut createFromParcel(Parcel in) {
            return new WorkOut(in);
        }

        @Override
        public WorkOut[] newArray(int size) {
            return new WorkOut[size];
        }
    };
    private static String DATE_FORMAT = "MMM dd, yyyy hh:mm";
    private int eventNo;
    private int workoutMode;
    private long startDateMilli;
    private long endDateMilli;
    private double distance;
    private double steps;
    private double aveStepsPerMin;
    private double aveSpeed;
    private long elapseTime;

    public WorkOut(int eventNo, int workoutMode, long startDateMilli, long endDateMilli, double distance,
                   double steps, double aveStepsPerMin, double aveSpeed,
                   long elapseTime) {
        this.eventNo = eventNo;
        this.workoutMode = workoutMode;
        this.startDateMilli = startDateMilli;
        this.endDateMilli = endDateMilli;
        this.distance = distance;
        this.aveStepsPerMin = aveStepsPerMin;
        this.aveSpeed = aveSpeed;
        this.elapseTime = elapseTime;
        this.steps = steps;

    }

    protected WorkOut(Parcel in) {
        eventNo = in.readInt();
        workoutMode = in.readInt();
        startDateMilli = in.readLong();
        endDateMilli = in.readLong();
        distance = in.readDouble();
        steps = in.readDouble();
        aveStepsPerMin = in.readDouble();
        aveSpeed = in.readDouble();
        elapseTime = in.readLong();
    }

    public int getEventNo() {
        return eventNo;
    }

    public void setEventNo(int eventNo) {
        this.eventNo = eventNo;
    }

    public String getStartDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(startDateMilli);
    }

    public long getStartDateMilli() {
        return startDateMilli;
    }

    public void setStartDateMilli(long startDateMilli) {
        this.startDateMilli = startDateMilli;
    }

    public long getEndDateMilli() {
        return endDateMilli;
    }

    public void setEndDateMilli(long endDateMilli) {
        this.endDateMilli = endDateMilli;
    }

    public String getEndDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(endDateMilli);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getSteps() {
        return steps;
    }

    public void setSteps(double steps) {
        this.steps = steps;
    }

    public double getAveStepsPerMin() {
        return aveStepsPerMin;
    }

    public void setAveStepsPerMin(double aveStepsPerMin) {
        this.aveStepsPerMin = aveStepsPerMin;
    }

    public double getAveSpeed() {
        return aveSpeed;
    }

    public void setAveSpeed(double aveSpeed) {
        this.aveSpeed = aveSpeed;
    }

    public long getElapseTime() {
        return elapseTime;
    }

    public void setElapseTime(long elapseTime) {
        this.elapseTime = elapseTime;
    }

    public int getWorkoutMode() {
        return workoutMode;
    }

    @Override
    public String toString() {
        return "WorkOut{" +
                "eventNo=" + eventNo +
                ", workoutMode=" + workoutMode +
                ", startDateMilli=" + startDateMilli +
                ", endDateMilli=" + endDateMilli +
                ", distance=" + distance +
                ", aveStepsPerMin=" + aveStepsPerMin +
                ", aveSpeed=" + aveSpeed +
                ", elapseTime=" + elapseTime +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(eventNo);
        dest.writeInt(workoutMode);
        dest.writeLong(startDateMilli);
        dest.writeLong(endDateMilli);
        dest.writeDouble(distance);
        dest.writeDouble(steps);
        dest.writeDouble(aveStepsPerMin);
        dest.writeDouble(aveSpeed);
        dest.writeLong(elapseTime);
    }
}

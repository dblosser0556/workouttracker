package com.sighe.workouttracker.data;

/**
 * Created by dad on 10/19/2016.
 */

public class RouteDetail {
    private int eventNo;
    private long locationTime;
    private double locationLat;
    private double locationLon;
    private double steps;
    private long timeIncr;
    private double distIncr;

    public RouteDetail(int eventNo, long locationTime,
                       double locationLat,
                       double locationLon, double steps,
                       long timeIncr, double distIncr) {
        this.eventNo = eventNo;
        this.locationTime = locationTime;
        this.locationLat = locationLat;
        this.locationLon = locationLon;
        this.steps = steps;
        this.timeIncr = timeIncr;
        this.distIncr = distIncr;
    }

    public int getEventNo() {
        return eventNo;
    }

    public void setEventNo(int eventNo) {
        this.eventNo = eventNo;
    }

    public long getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(long locationTime) {
        this.locationTime = locationTime;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getLocationLon() {
        return locationLon;
    }

    public void setLocationLon(double locationLon) {
        this.locationLon = locationLon;
    }

    public double getSteps() {
        return steps;
    }

    public void setSteps(double steps) {
        this.steps = steps;
    }

    public long getTimeIncr() {
        return timeIncr;
    }

    public void setTimeIncr(long timeIncr) {
        this.timeIncr = timeIncr;
    }

    public double getDistIncr() {
        return distIncr;
    }

    public void setDistIncr(double distIncr) {
        this.distIncr = distIncr;
    }

    @Override
    public String toString() {
        return "RouteDetail{" +
                "eventNo=" + eventNo +
                ", locationTime=" + locationTime +
                ", locationLat=" + locationLat +
                ", locationLon=" + locationLon +
                ", steps=" + steps +
                ", timeIncr=" + timeIncr +
                ", distIncr=" + distIncr +
                '}';
    }
}

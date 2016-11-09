package com.sighe.workouttracker.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.sighe.workouttracker.utility.utils;

/**
 * Created by dad on 10/31/2016.
 */

public class Goal implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Goal> CREATOR = new Parcelable.Creator<Goal>() {
        @Override
        public Goal createFromParcel(Parcel in) {
            return new Goal(in);
        }

        @Override
        public Goal[] newArray(int size) {
            return new Goal[size];
        }
    };
    private int id;
    private utils.Mode modeType;
    private utils.GoalType valueType;
    private utils.Interval intervalType;
    private double value;
    private long effectiveDate;

    public Goal(int id, int mode, int valueType,
                int intervalType, double value, long effectiveDate) {

        this.id = id;

        setModeType(mode);
        setValueType(valueType);
        setIntervalType(intervalType);
        this.effectiveDate = effectiveDate;
        this.value = value;
    }

    protected Goal(Parcel in) {
        id = in.readInt();
        modeType.value = in.readInt();
        valueType.value = in.readInt();
        intervalType.value = in.readInt();
        value = in.readDouble();

    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getEffectiveDate() {
        return effectiveDate;
    }

    public utils.Mode getModeType() {
        return modeType;
    }

    public void setModeType(int mode) {

        switch (mode) {
            case 0:
                this.modeType = utils.Mode.walking;
                break;
            case 1:
                this.modeType = utils.Mode.running;
                break;
            case 2:
                this.modeType = utils.Mode.biking;
        }
    }

    public utils.GoalType getValueType() {
        return valueType;
    }

    public void setValueType(int valueType) {
        switch (valueType) {
            case 0:
                this.valueType = utils.GoalType.distance;
                break;
            case 1:
                this.valueType = utils.GoalType.time;
                break;
            case 2:
                this.valueType = utils.GoalType.steps;
        }

    }

    public utils.Interval getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(int intervalType) {
        switch (intervalType) {
            case 0:
                this.intervalType = utils.Interval.daily;
                break;
            case 1:
                this.intervalType = utils.Interval.weekly;
                break;
            case 2:
                this.intervalType = utils.Interval.monthly;
                break;
        }

    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(modeType.value);
        dest.writeInt(valueType.value);
        dest.writeInt(intervalType.value);
        dest.writeDouble(value);

    }

}

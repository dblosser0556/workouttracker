package com.sighe.workouttracker.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dad on 11/3/2016.
 */

public class Route implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
    private int id;
    private int eventNo;
    private double centerLat;
    private double centerLon;
    private String name;
    private long createDate;

    public Route(int id, int eventNo, double centerLat, double centerLon, String name, long createDate) {
        this.id = id;
        this.eventNo = eventNo;
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        this.name = name;
        this.createDate = createDate;
    }

    protected Route(Parcel in) {
        id = in.readInt();
        eventNo = in.readInt();
        centerLat = in.readDouble();
        centerLon = in.readDouble();
        name = in.readString();
        createDate = in.readLong();
    }

    public int getId() {
        return id;
    }

    public int getEventNo() {
        return eventNo;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public double getCenterLon() {
        return centerLon;
    }

    public String getName() {
        return name;
    }

    public long getCreateDate() {
        return createDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(eventNo);
        dest.writeDouble(centerLat);
        dest.writeDouble(centerLon);
        dest.writeString(name);
        dest.writeLong(createDate);
    }
}

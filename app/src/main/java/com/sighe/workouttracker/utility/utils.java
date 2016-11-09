package com.sighe.workouttracker.utility;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.sighe.workouttracker.data.DBHelper;
import com.sighe.workouttracker.data.RouteDetail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dad on 10/15/2016.
 */

public class utils {
    public static final String GPS_ERROR = "com.sighe.workouttracker.gps_error";
    public static final String GPS_OFFLINE = "com.sighe.workouttracker.gps_offline";
    public static final String GPS_ONLINE = "com.sighe.workouttracker.gps_online";
    public static final String UPDATES = "com.sighe.workouttracker.updates";
    public final static int MODE_BIKING = 2;
    public final static int MODE_RUNNING = 1;
    public final static int MODE_WALKING = 0;
    public static final double MilliToDays = 1d / (1000d * 60d * 60d * 24d);
    public static final double MilliToHours = 1d / (1000d * 60d * 60d);
    public static final double MilliToMins = 1d / (1000d * 60d);
    public static final double MilliToSecs = 1d / (1000d);
    public static final double FeetToMiles = (1.0d / 5280d);
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(100);
    public static double[] distUOMConv = {3.28084d / 5280d, .001d, 3.28084d, 1.0d};
    public static String[] distUOMAbbrv = {"mi", "km", "ft", "m"};
    public static double[] speedUOMconv = {3.28084d / (5280d * 60d * 60d),
            1.0d, 1000d / (60d * 60d), 5280 / (3.28084 * 60)};
    public static String[] speedUOMabbr = {"mph", "m/s", "kph", "mpm"};

    public static long getMidNight(long dateTimeMilli, boolean tonight) {
        Calendar c = new GregorianCalendar(TimeZone.getDefault());
        c.setTimeInMillis(dateTimeMilli);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR, 0);

        if (tonight)
            c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTimeInMillis();
    }

    public static String getFormattedInterval(final long ms) {
        // ms is in milliseconds and want to display in Hours:Mins:Secs
        long x = ms / 1000;
        long seconds = x % 60;
        x = x / 60;
        long minutes = x % 60;
        x /= 60;
        long hours = x % 24;

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    /*
    The geographic midpoint is calculated by finding the center of gravity for the
    locations in the routeDetails list. The latitude and longitude for each location
    is converted into Cartesian (x,y,z) coordinates. The x,y, and z coordinates are
    then multiplied by the weighting factor and added together. A line can be drawn
    from the center of the earth out to this new x, y, z coordinate, and the point
    where the line intersects the surface of the earth is the geographic midpoint.
    This surface point is converted into the latitude and longitude for the midpoint.
    */
    public static LatLng calcRouteMidPoint(List<RouteDetail> routeDetails) {

        double radConv = 3.14159265359 / 180;
        LatLng midPoint = null;


        Double xAve = 0d;
        Double yAve = 0d;
        Double zAve = 0d;

        List<Double> Xs = new ArrayList<>();
        List<Double> Ys = new ArrayList<>();
        List<Double> Zs = new ArrayList<>();
        for (RouteDetail routeDetail : routeDetails) {
            //convert lat and long to cartesian coordinates
            Double X = Math.cos(routeDetail.getLocationLat() * radConv) *
                    Math.cos(routeDetail.getLocationLon() * radConv);
            Double Y = Math.cos(routeDetail.getLocationLat() * radConv) *
                    Math.sin(routeDetail.getLocationLon() * radConv);
            Double Z = Math.sin(routeDetail.getLocationLat() * radConv);

            xAve += X;
            yAve += Y;
            zAve += Z;


        }
        xAve /= (double) routeDetails.size();
        yAve /= (double) routeDetails.size();
        zAve /= (double) routeDetails.size();

        double midLon = Math.atan2(yAve, xAve);
        double hyp = Math.sqrt(xAve * xAve + yAve * yAve);
        double midLat = Math.atan2(zAve, hyp);

        midPoint = new LatLng(midLat / radConv, midLon / radConv);

        return midPoint;
    }

    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public enum Interval {
        daily(0),
        weekly(1),
        monthly(2);

        public int value;

        Interval(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            switch (value) {
                case 0:
                    return "Daily";
                case 1:
                    return "Weekly";
                case 2:
                    return "Monthly";
                default:
                    return "Weekly";
            }
        }
    }

    public enum Mode {
        walking(0),
        running(1),
        biking(2);

        public int value;

        Mode(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            switch (value) {
                case 0:
                    return "Walking";
                case 1:
                    return "Running";
                case 2:
                    return "Biking";
                default:
                    return "Walking";
            }
        }

    }

    public enum GoalType {
        distance(0),
        time(1),
        steps(2);

        public int value;

        GoalType(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            switch (value) {
                case 0:
                    return "Distance";
                case 1:
                    return "Time";
                case 2:
                    return "Steps";
                default:
                    return "Distance";
            }
        }

    }


    public enum WorkOutStatus {
        stopped(0),
        paused(1),
        running(2),
        disabled(3),
        ready(4);
        private final int value;

        WorkOutStatus(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            switch (value) {
                case 0:
                    return "Stopped";
                case 1:
                    return "Paused";
                case 2:
                    return "Running";
                case 3:
                    return "Disabled";
                case 4:
                    return "Ready";

                default:
                    return "";
            }

        }
    }

    public enum distUOM {
        miles(0, 3.28084 / 5280, "mi"),
        kilometers(1, .001, "km"),
        feet(2, 3.28084, "ft"),
        meters(3, 1, "m");

        public final double convFactor;
        public final String abbrv;
        private int index;

        distUOM(int index, double convFactor, String abbrv) {
            this.index = index;
            this.convFactor = convFactor;
            this.abbrv = abbrv;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }


    public enum speedUOM {
        mph(0, 3.28084 / (5280 * 60 * 60), "mph"), //meters -> feet -> miles -> hours
        mps(1, 1, "m/s"),
        kph(2, 1000 / (60 * 60), "kph"),
        mpm(3, 5280 / (3.28084 * 60), "mpm");// 1/(meters/sec) -> feet -> miles -> minutes
        public final double convFactor;
        public final String abbrv;
        private int index;

        speedUOM(int index, double convFactor, String abbrv) {
            this.index = index;
            this.abbrv = abbrv;
            this.convFactor = convFactor;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}

package com.sighe.workouttracker.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.sighe.workouttracker.R;
import com.sighe.workouttracker.activity.MainActivity;
import com.sighe.workouttracker.data.DBHelper;
import com.sighe.workouttracker.data.RouteDetail;
import com.sighe.workouttracker.utility.utils;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by dad on 10/14/2016.
 */

public class TrackerService extends Service implements LocationListener {
    public static String EVENT_NO = "eventNo";
    private static String TAG = "TrackerService";
    private static int MIN_TIME_BEFORE_UPDATE = 250; //milliseconds
    private static int MIN_DISTANCE_BEFORE_UPDATE = 0; //meters
    private static int TWO_MINUTES = 2 * 60 * 1000;
    private static int MAX_TIME_FOR_UPDATE = 10000;  //10 seconds
    private final IBinder mBinder = new LocalBinder();
    public Long mTotalElapsedTime;
    public double mTotalDistance;
    public double mTotalSteps;
    public double mCurStepsPerMin;
    public double mAveStepsPerMin;
    public double mCurSpeed;
    public double mAveSpeed;
    public int mEventNo;
    public utils.WorkOutStatus mStatus = utils.WorkOutStatus.ready;
    List<RouteDetail> mRouteDetails;
    private DBHelper mDbHelper;
    private double mStepsPerUOM;
    private LocationManager mLocationManager;
    private Location mPrevLocation;
    private Location mCurLocation;
    private Long mCurTime;
    private Long mPrevTime;
    private ArrayList<Double> mCurStepsRunningAverage;
    private double mSpeedGoal;
    private boolean mNotifyRate;
    private int mSpeedGoalId = 100;
    private Handler gpsUpdating;
    private Handler userNotificationHandler;
    //checks to see if the Location is updating. Called when ever updates are started.
    private Runnable gspUpdatingChecker = new Runnable() {
        @Override
        public void run() {
            //if there a new updates then the mPrevTime and mCurTime
            //should no longer be the same.
            if (mPrevTime == mCurTime) {
                //check to see if our allotted time has passed.
                long curTime = SystemClock.elapsedRealtime();
                if (curTime - mCurTime > MAX_TIME_FOR_UPDATE) {
                    //let the user know we are recieving no updates
                    stopUpdates();
                    Intent intent = new Intent();
                    intent.setAction(utils.GPS_OFFLINE);
                    sendBroadcast(intent);
                } else {
                    gpsUpdating.postDelayed(this, 5000);
                }

            }
        }
    };
    private Runnable userNotifications = new Runnable() {
        @Override
        public void run() {
            if (mNotifyRate) {
                if (mCurSpeed < mSpeedGoal) {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                                    .setContentTitle("Speed")
                                    .setContentText("Get going");

                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("Running", true);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                    stackBuilder.addParentStack(MainActivity.class);

                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(mSpeedGoalId, mBuilder.build());

                }
            }
            userNotificationHandler.postDelayed(this, 5000);
        }
    };

    //handlers for Location Listener Implementation
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location, mCurLocation)) {
            mPrevLocation = mCurLocation;
            mCurLocation = location;
            //need two locations to calculated totals
            if (mPrevLocation != null) {
                updateTotals();
                Intent intent = new Intent();
                intent.setAction(utils.UPDATES);
                sendBroadcast(intent);
            }
        }
    }

    //handlers for Location Listener Implementation
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {


    }

    //handlers for Location Listener Implementation
    @Override
    public void onProviderEnabled(String provider) {

    }

    //handlers for Location Listener Implementation
    @Override
    public void onProviderDisabled(String provider) {

    }

    //service binding
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: 10/12/2016  get these from the preferences file
        Log.i(TAG, "onBind: ");

        if (mStatus == utils.WorkOutStatus.ready) {
            Log.i(TAG, "onBind: not running");
            mStepsPerUOM = 1D;
            mRouteDetails = new ArrayList<>();
            mSpeedGoal = 1.0D;
            mNotifyRate = true;

            gpsUpdating = new Handler();
            userNotificationHandler = new Handler();

            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mDbHelper = new DBHelper(getApplicationContext());
        }
        return mBinder;
    }

    //called from main activity responding to button click.
    public void OnButtonClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.btnStart:
                mStatus = utils.WorkOutStatus.running;
                startUpdates();
                break;
            case R.id.btnPause:
                mStatus = utils.WorkOutStatus.paused;
                stopUpdates();
                break;
            case R.id.btnReset:
                mStatus = utils.WorkOutStatus.ready;
                resetTotals();
                resetRouteDetails();
                stopUpdates();
                break;
            case R.id.btnStop:
                mStatus = utils.WorkOutStatus.stopped;
                stopUpdates();
                break;
            case R.id.btnSave:
                resetTotals();
                stopUpdates();
                break;
        }

    }

    private void stopUpdates() {
        try {
            mLocationManager.removeUpdates(this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void startUpdates() {

        if (isLocationEnabled(this)) {


            try {
                mPrevTime = mCurTime = SystemClock.elapsedRealtime();
                gpsUpdating.postDelayed(gspUpdatingChecker, 1000);
                userNotificationHandler.postDelayed(userNotifications, 1000);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BEFORE_UPDATE, MIN_DISTANCE_BEFORE_UPDATE, this);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BEFORE_UPDATE, MIN_DISTANCE_BEFORE_UPDATE, this);

            } catch (SecurityException e) {
                e.printStackTrace();

            }

        } else {
            Intent intent = new Intent();
            intent.setAction(utils.GPS_OFFLINE);
            sendBroadcast(intent);
        }
    }

    //resets the total distance, steps etc.
    private void resetTotals() {
        mPrevTime = 0L;
        mCurTime = 0L;
        mTotalElapsedTime = 0L;

        mTotalDistance = 0D;
        mTotalSteps = 0;
        mCurStepsPerMin = 0;
        mAveStepsPerMin = 0;
        mCurSpeed = 0;
        mAveSpeed = 0;
        mCurStepsRunningAverage = new ArrayList<>();


    }

    //update the total distance, steps etc.
    private void updateTotals() {

        mPrevTime = mCurTime;
        mCurTime = SystemClock.elapsedRealtime();


        long curElapsedTime = mCurTime - mPrevTime;  //milliseconds
        mTotalElapsedTime += curElapsedTime;
        Log.i(TAG, "updateUI - curElapsedTime: " + curElapsedTime);


        double newDistanceIncr = HaversineInMeters(mCurLocation, mPrevLocation); //meters
        mTotalDistance += newDistanceIncr; //meters
        Log.i(TAG, "updateUI - newDistanceIncr: " + newDistanceIncr);
        Log.i(TAG, "updateUI - mTotalDistance: " + mTotalDistance);

        double newSteps = newDistanceIncr * mStepsPerUOM;
        mTotalSteps += newSteps;
        Log.i(TAG, "updateUI - newSteps: " + newSteps);
        Log.i(TAG, "updateUI - mTotalSteps: " + mTotalSteps);


        mCurStepsPerMin = 0;
        mCurSpeed = 0;
        if (curElapsedTime != 0) {
            if (mCurStepsRunningAverage.size() > 10) mCurStepsRunningAverage.remove(0);

            //running average of the last ten current steps per minute
            double stepsPerMin = newSteps / (curElapsedTime * utils.MilliToMins); //seconds/60;
            mCurStepsRunningAverage.add(stepsPerMin);
            stepsPerMin = 0;
            for (int i = 0; i < mCurStepsRunningAverage.size(); i++) {
                stepsPerMin += mCurStepsRunningAverage.get(i);
            }
            mCurStepsPerMin = stepsPerMin / (double) mCurStepsRunningAverage.size();

            mCurSpeed = newDistanceIncr / (curElapsedTime * utils.MilliToSecs); //meters per second
        }
        mAveStepsPerMin = 0;
        mAveSpeed = 0;
        if (mTotalElapsedTime != 0) {
            mAveSpeed = (mTotalDistance) / (mTotalElapsedTime * utils.MilliToSecs);
            mAveStepsPerMin = (mTotalSteps / (mTotalElapsedTime * utils.MilliToMins));
        }

        RouteDetail routeDetail = new RouteDetail(mEventNo,
                mCurLocation.getTime(), mCurLocation.getLatitude(), mCurLocation.getLongitude(),
                newSteps, curElapsedTime, newDistanceIncr);

        mRouteDetails.add(routeDetail);

    }

    //calculates the difference between two locations.
    private double HaversineInMeters(Location end, Location start) {

        if (end == null || start == null) {


            if (isLocationEnabled(this)) {
                Intent intent = new Intent();
                intent.setAction(utils.GPS_ERROR);
                sendBroadcast(intent);
            } else {
                Intent intent = new Intent();
                intent.setAction(utils.GPS_OFFLINE);
                sendBroadcast(intent);
            }
            stopUpdates();


            return 0;
        }

        return CalcHaversineInMeters(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude());

    }

    private double CalcHaversineInMeters(double lat1, double long1, double lat2, double long2) {
        final double _eQuatorialEarthRadius = 6378.1370D; //kilometers
        final double _d2r = (Math.PI / 180D); //to convert to radians

        double dlong = (long2 - long1) * _d2r;
        double dlat = (lat2 - lat1) * _d2r;
        double a = Math.pow(Math.sin(dlat / 2D), 2D) + Math.cos(lat1 * _d2r) * Math.cos(lat2 * _d2r)
                * Math.pow(Math.sin(dlong / 2D), 2D);
        double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
        double d = _eQuatorialEarthRadius * c;

        return d * 1000D;
    }

    //check to ensure the Location provider is available.
    private boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        boolean providerIsEnabled = false;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                providerIsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF || providerIsEnabled;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            providerIsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return !TextUtils.isEmpty(locationProviders) || providerIsEnabled;
        }


    }

    //ensuring the updated location is better then the last.
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private void resetRouteDetails() {
        mRouteDetails.removeAll(mRouteDetails);
    }

    public void saveRouteDetails(String routeName) {
        //try to limit the number of location entries needed.
        //need more than 3 entries to do the smoothing.

        RouteDetail detail1;
        RouteDetail detail2;
        double addDist = 0;
        double calcDist = 0;
        int start = 0;
        int mid = 1;
        int last = 2;
        while (last < mRouteDetails.size()) {

            //the distance increment is stored on the second location object.
            addDist = mRouteDetails.get(mid).getDistIncr() + mRouteDetails.get(last).getDistIncr();

            //calc the distance between the first and last location
            detail1 = mRouteDetails.get(start);
            detail2 = mRouteDetails.get(last);
            calcDist = CalcHaversineInMeters(detail1.getLocationLat(), detail1.getLocationLon(),
                    detail2.getLocationLat(), detail2.getLocationLon());

            //if the distance difference is less than 0.01% don't save result
            if (calcDist >= addDist * 0.9999 && calcDist <= addDist * 1.0001) {
                //I don't need the middle location add the middle details to the last
                //remove the first
                detail2.setDistIncr(calcDist);
                detail2.setSteps(calcDist * mStepsPerUOM);
                detail2.setTimeIncr(detail2.getTimeIncr() + detail1.getTimeIncr());

                mRouteDetails.remove(mid);

            } else {
                start++;
                mid++;
                last++;
            }

        }
        //get the center of the route.
        LatLng routeCenter = utils.calcRouteMidPoint(mRouteDetails);

        long midNight = utils.getMidNight(
                new GregorianCalendar(TimeZone.getDefault()).getTimeInMillis(), true);

        //save the route into the database
        mDbHelper.saveRoute(routeName, mEventNo, routeCenter.latitude, routeCenter.longitude,
                midNight);

        //save the route details into the database.
        for (RouteDetail routeDetail : mRouteDetails) {
            mDbHelper.saveRouteDetail(routeDetail.getEventNo(), routeDetail.getLocationTime(),
                    routeDetail.getLocationLat(), routeDetail.getLocationLon(),
                    routeDetail.getSteps(), routeDetail.getTimeIncr(), routeDetail.getDistIncr());

        }
    }

    //service binding
    public class LocalBinder extends Binder {
        public TrackerService getService() {
            return TrackerService.this;
        }
    }
}

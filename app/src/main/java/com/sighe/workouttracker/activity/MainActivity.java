package com.sighe.workouttracker.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.sighe.workouttracker.R;
import com.sighe.workouttracker.data.DBHelper;
import com.sighe.workouttracker.service.TrackerService;
import com.sighe.workouttracker.utility.DismissiblePopup;
import com.sighe.workouttracker.utility.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static BroadcastReceiver mReceiver;
    private static IntentFilter mIntentFilter;
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 134;
    protected TextView tvElapsedTime;
    protected TextView tvStepsTotal;
    protected TextView tvTotalDistance;
    protected TextView tvAverageSpeed;
    protected TextView tvAverageStepsPerMin;
    protected TextView tvCurrentSpeed;
    protected TextView tvCurrentStepsPerMin;
    protected TextView tvStartTime;
    protected TextView tvEndTime;
    protected TextView tvStatus;
    protected Button mBtnStart;
    protected Button mBtnPause;
    protected Button mBtnReset;
    protected Button mBtnStop;
    protected Button mBtnSave;
    protected ImageButton mBtnWalking;
    protected ImageButton mBtnRunning;
    protected ImageButton mBtnBiking;
    private int mWorkoutMode = utils.MODE_WALKING;
    private boolean mBound = false;

    private Date mStartTime;
    private Date mEndTime;
    private utils.WorkOutStatus mWorkOutStatus;
    private int mEventNo;

    private int mUOMDist;
    private int mUOMSpeed;

    private TrackerService mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TrackerService.LocalBinder binder = (TrackerService.LocalBinder) service;
            Log.i(TAG, "onServiceConnected: ");
            mService = binder.getService();
            mBound = true;
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisConnected: ");
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i(TAG, "onCreate: ");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //check to see if I am already running
        if (getIntent().getBooleanExtra("Running", false)) {

            mWorkOutStatus = utils.WorkOutStatus.running;

        } else {

            if (havePermissions())
                mWorkOutStatus = utils.WorkOutStatus.ready;
            else
                mWorkOutStatus = utils.WorkOutStatus.disabled;

        }
        //get the preference data
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mWorkoutMode = Integer.valueOf(sharedPref.getString(SettingsActivity.KEY_WORKOUT_MODE, "0"));
        mUOMDist = Integer.valueOf(sharedPref.getString(SettingsActivity.KEY_DISTANCE_UOM, "0"));
        mUOMSpeed = Integer.valueOf(sharedPref.getString(SettingsActivity.KEY_SPEED_UOM, "0"));

        setUpBroadCastReceiver();
        setUpUI();
        resetMode();
        resetButtons();


    }

    private boolean havePermissions() {
        //make sure we have the required permissions

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("GPS Fine Location is required for the application to calculate distance and track location.")
                        .setTitle("Location Services are Required")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                return false;

            }

        }
        return true;
    }

    private void setUpBroadCastReceiver() {

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(utils.GPS_ONLINE);
        mIntentFilter.addAction(utils.GPS_OFFLINE);
        mIntentFilter.addAction(utils.GPS_ERROR);
        mIntentFilter.addAction(utils.UPDATES);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case utils.GPS_ERROR:
                        gpsAlert(getString(R.string.gps_error), getString(R.string.gps_error_text));
                        mWorkOutStatus = utils.WorkOutStatus.disabled;
                        resetButtons();
                        break;
                    case utils.GPS_OFFLINE:
                        gpsAlert(getString(R.string.gps_off), getString(R.string.gps_off_text));
                        mWorkOutStatus = utils.WorkOutStatus.ready;
                        mStartTime = null;
                        updateUI();
                        resetButtons();
                        break;
                    case utils.GPS_ONLINE:
                        //alert user with gps issues
                        gpsAlert(getString(R.string.gps_available_text), getString(R.string.gps_available_text));
                        mWorkOutStatus = utils.WorkOutStatus.ready;
                        resetButtons();
                        break;
                    case utils.UPDATES:
                        updateUI();
                        break;
                }

            }
        };
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();


        Intent intent = new Intent(this, TrackerService.class);
        Log.i(TAG, "onStart: ");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        if (mBound) {
            unbindService(mConnection);
            // mBound = false;
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause: ");
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            //mReceiver = null;
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {

            case R.id.nav_activity_chart:
                Intent intentChart = new Intent(this, ChartActivity.class);
                intentChart.putExtra(SettingsActivity.KEY_SPEED_UOM, mUOMSpeed);
                intentChart.putExtra(SettingsActivity.KEY_DISTANCE_UOM, mUOMDist);
                startActivity(intentChart);
                break;
            case R.id.nav_details:
                Intent intentDetails = new Intent(this, WorkOutListActivity.class);
                intentDetails.putExtra(SettingsActivity.KEY_SPEED_UOM, mUOMSpeed);
                intentDetails.putExtra(SettingsActivity.KEY_DISTANCE_UOM, mUOMDist);
                startActivity(intentDetails);
                break;

            case R.id.nav_goals:
                Intent intentGoals = new Intent(this, GoalsActivity.class);
                intentGoals.putExtra(SettingsActivity.KEY_SPEED_UOM, mUOMSpeed);
                intentGoals.putExtra(SettingsActivity.KEY_DISTANCE_UOM, mUOMDist);
                startActivity(intentGoals);
                break;

            case R.id.nav_gallery:
                Intent intentShowRoute = new Intent(this, MapsActivity.class);
                intentShowRoute.putExtra(SettingsActivity.KEY_SPEED_UOM, mUOMSpeed);
                intentShowRoute.putExtra(SettingsActivity.KEY_DISTANCE_UOM, mUOMDist);
                intentShowRoute.putExtra(MapsActivity.ROUTE_NAME, "first");
                startActivity(intentShowRoute);
                break;
            case R.id.nav_share:
            case R.id.nav_find_routes:
                Toast.makeText(this, "This feature is not implemented at this time", Toast.LENGTH_LONG).show();
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.btnStart:

                if (mStartTime == null) {
                    DBHelper dbHelper = new DBHelper(this);
                    mEventNo = dbHelper.getNextEventNo();
                    mService.mEventNo = mEventNo;
                    mStartTime = new Date();
                }
                mWorkOutStatus = utils.WorkOutStatus.running;
                break;
            case R.id.btnPause:
                mWorkOutStatus = utils.WorkOutStatus.paused;
                break;
            case R.id.btnReset:
                mWorkOutStatus = utils.WorkOutStatus.ready;
                mStartTime = null;
                mEndTime = null;
                break;
            case R.id.btnStop:
                mWorkOutStatus = utils.WorkOutStatus.stopped;
                mEndTime = new Date();

                break;
            case R.id.btnSave:
                saveWorkoutSummary();
                checkSaveRoute();
                mWorkOutStatus = utils.WorkOutStatus.ready;
                mStartTime = null;
                mEndTime = null;
                break;
            case R.id.btnBiking:
                mWorkoutMode = utils.MODE_BIKING;
                resetMode();
                break;
            case R.id.btnRunning:
                mWorkoutMode = utils.MODE_RUNNING;
                resetMode();
                break;
            case R.id.btnWalking:
                mWorkoutMode = utils.MODE_WALKING;
                resetMode();
                break;
        }
        resetButtons();
        mService.OnButtonClick(v);
        updateUI();
    }

    private void checkSaveRoute() {
        new AlertDialog.Builder(this)
                .setTitle("Save Route")
                .setMessage("Do you want to save this route?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveRouteDetails();

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void saveWorkoutSummary() {
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.saveWorkSummary(mEventNo, mWorkoutMode, mStartTime.getTime(), mEndTime.getTime(),
                mService.mTotalDistance, "mi", mService.mTotalSteps, mService.mAveStepsPerMin, mService.mAveSpeed,
                "mph", mService.mTotalElapsedTime);
    }

    private void saveRouteDetails() {

        //get the route name
        final DismissiblePopup popup = new DismissiblePopup(this, R.layout.popup_layout);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                String routeName = popup.getValue();
                if (routeName != null)
                    mService.saveRouteDetails(routeName);
                else
                    Snackbar.make(mBtnSave, "You must enter a route name", Snackbar.LENGTH_SHORT).show();
            }
        });
        popup.show(tvAverageStepsPerMin, 0, 0);

    }

    private void setUpUI() {
        tvElapsedTime = (TextView) findViewById(R.id.tvElapseTime);
        tvStepsTotal = (TextView) findViewById(R.id.tvStepTotal);

        tvTotalDistance = (TextView) findViewById(R.id.tvDistanceTotal);


        tvAverageStepsPerMin = (TextView) findViewById(R.id.tvAverageStepsPerMinute);
        tvCurrentStepsPerMin = (TextView) findViewById(R.id.tvCurrentStepsPerMinute);

        tvAverageStepsPerMin.setVisibility((mWorkoutMode == utils.MODE_WALKING) ? View.VISIBLE : View.GONE);
        tvCurrentStepsPerMin.setVisibility((mWorkoutMode == utils.MODE_WALKING) ? View.VISIBLE : View.GONE);

        tvCurrentSpeed = (TextView) findViewById(R.id.tvCurrentSpeed);
        tvAverageSpeed = (TextView) findViewById(R.id.tvAverageSpeed);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        tvStatus = (TextView) findViewById(R.id.tvStatus);

        mBtnStart = (Button) findViewById(R.id.btnStart);
        mBtnPause = (Button) findViewById(R.id.btnPause);
        mBtnStop = (Button) findViewById(R.id.btnStop);
        mBtnReset = (Button) findViewById(R.id.btnReset);
        mBtnSave = (Button) findViewById(R.id.btnSave);

        mBtnWalking = (ImageButton) findViewById(R.id.btnWalking);
        mBtnBiking = (ImageButton) findViewById(R.id.btnBiking);
        mBtnRunning = (ImageButton) findViewById(R.id.btnRunning);

        mBtnStart.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        mBtnReset.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);

        mBtnWalking.setOnClickListener(this);
        mBtnBiking.setOnClickListener(this);
        mBtnRunning.setOnClickListener(this);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mWorkOutStatus = utils.WorkOutStatus.ready;
                    resetButtons();


                } else {
                    mWorkOutStatus = utils.WorkOutStatus.disabled;
                    resetButtons();

                }
            }

        }
    }

    private void resetMode() {
        mBtnWalking.setSelected(mWorkoutMode == utils.MODE_WALKING);
        mBtnRunning.setSelected(mWorkoutMode == utils.MODE_RUNNING);
        mBtnBiking.setSelected(mWorkoutMode == utils.MODE_BIKING);

        tvAverageStepsPerMin.setVisibility((mWorkoutMode == utils.MODE_WALKING) ? View.VISIBLE : View.GONE);
        tvCurrentStepsPerMin.setVisibility((mWorkoutMode == utils.MODE_WALKING) ? View.VISIBLE : View.GONE);
    }

    private void updateUI() {
        //make sure we are bound to the service before accessing it values;
        Log.i(TAG, "updateUI: mBound - " + String.valueOf(mBound));
        if (!mBound) return;


        SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a", Locale.US);
        if (mStartTime == null)
            tvStartTime.setText(getResources().getString(R.string.start_time, mWorkOutStatus));
        else
            tvStartTime.setText(getResources().getString(R.string.start_time, sdf.format(mStartTime.getTime())));

        if (mEndTime == null)
            tvEndTime.setText(getResources().getString(R.string.end_time, mWorkOutStatus));
        else
            tvEndTime.setText(getResources().getString(R.string.end_time, sdf.format(mEndTime.getTime())));


        tvElapsedTime.setText(
                getResources().getString(R.string.elapsed_time, utils.getFormattedInterval(mService.mTotalElapsedTime)));

        NumberFormat numberFormat = new DecimalFormat("##0.00");

        tvTotalDistance.setText(
                getResources().getString(R.string.total_distance,
                        utils.distUOMAbbrv[mUOMDist],
                        numberFormat.format(mService.mTotalDistance * utils.distUOMConv[mUOMDist])));

        tvStepsTotal.setText(
                getResources().getString(R.string.step_total, String.valueOf((int) mService.mTotalSteps)));

        tvAverageStepsPerMin.setText(getString(R.string.avg_steps_min, numberFormat.format(mService.mAveStepsPerMin)));

        tvCurrentStepsPerMin.setText(
                getResources().getString(R.string.steps_min, numberFormat.format(mService.mCurStepsPerMin)));

        tvCurrentSpeed.setText(
                getResources().getString(R.string.cur_speed, utils.speedUOMabbr[mUOMSpeed], numberFormat.format(
                        (mUOMSpeed == 3)
                                ? utils.speedUOMconv[mUOMSpeed] / mService.mCurSpeed
                                : mService.mCurSpeed * utils.speedUOMconv[mUOMSpeed])));

        tvAverageSpeed.setText(getResources().getString(R.string.avg_speed, utils.speedUOMabbr[mUOMSpeed], numberFormat.format(
                (mUOMSpeed == 3)
                        ? utils.speedUOMconv[mUOMSpeed] / mService.mAveSpeed
                        : mService.mAveSpeed * utils.speedUOMconv[mUOMSpeed])));

        tvStatus.setText(mWorkOutStatus.toString());

    }

    private void resetButtons() {
        switch (mWorkOutStatus) {

            case ready:
                mBtnStop.setEnabled(false);
                mBtnPause.setEnabled(false);
                mBtnReset.setEnabled(false);
                mBtnStart.setEnabled(true);
                mBtnSave.setVisibility(View.GONE);
                mBtnSave.setEnabled(false);

                mBtnRunning.setEnabled(true);
                mBtnWalking.setEnabled(true);
                mBtnBiking.setEnabled(true);
                break;

            case stopped:
                mBtnStop.setEnabled(false);
                mBtnPause.setEnabled(false);
                mBtnReset.setEnabled(true);
                mBtnStart.setEnabled(false);
                mBtnSave.setVisibility(View.VISIBLE);
                mBtnSave.setEnabled(true);

                mBtnRunning.setEnabled(false);
                mBtnWalking.setEnabled(false);
                mBtnBiking.setEnabled(false);
                break;


            case paused:
                mBtnStop.setEnabled(true);
                mBtnPause.setEnabled(false);
                mBtnReset.setEnabled(false);
                mBtnStart.setEnabled(true);
                mBtnSave.setVisibility(View.GONE);
                mBtnSave.setEnabled(false);

                mBtnRunning.setEnabled(false);
                mBtnWalking.setEnabled(false);
                mBtnBiking.setEnabled(false);
                break;

            case running:
                mBtnStop.setEnabled(true);
                mBtnPause.setEnabled(true);
                mBtnReset.setEnabled(false);
                mBtnStart.setEnabled(false);
                mBtnSave.setVisibility(View.GONE);
                mBtnSave.setEnabled(false);

                mBtnRunning.setEnabled(false);
                mBtnWalking.setEnabled(false);
                mBtnBiking.setEnabled(false);
                break;

            case disabled:
                mBtnStop.setEnabled(false);
                mBtnPause.setEnabled(false);
                mBtnReset.setEnabled(false);
                mBtnStart.setEnabled(false);
                mBtnSave.setVisibility(View.GONE);
                mBtnSave.setEnabled(false);

                mBtnRunning.setEnabled(false);
                mBtnWalking.setEnabled(false);
                mBtnBiking.setEnabled(false);

                break;
            default:
                mBtnStop.setEnabled(true);
                mBtnPause.setEnabled(false);
                mBtnReset.setEnabled(false);
                mBtnStart.setEnabled(false);
                mBtnSave.setVisibility(View.GONE);
                mBtnSave.setEnabled(false);

                mBtnRunning.setEnabled(false);
                mBtnWalking.setEnabled(false);
                mBtnBiking.setEnabled(false);

        }
    }

    private void gpsAlert(String title, String desc) {


        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(desc)
                .setNegativeButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        MediaPlayer mp = MediaPlayer.create(this, R.raw.alert);
        mp.start();


    }
}

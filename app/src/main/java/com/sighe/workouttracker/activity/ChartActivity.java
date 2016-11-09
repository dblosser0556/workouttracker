package com.sighe.workouttracker.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.sighe.workouttracker.R;
import com.sighe.workouttracker.data.DBHelper;
import com.sighe.workouttracker.data.DailyWorkOut;
import com.sighe.workouttracker.utility.ChartDateFormatter;
import com.sighe.workouttracker.utility.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChartActivity extends AppCompatActivity {
    private static final int GROUP_BY_NONE = 0;
    private static final int GROUP_BY_MODE = 1;
    private static final int GROUP_BY_VALUE = 2;
    private static String TAG = "ChartActivity";
    protected BarChart mBarChart;

    private List<DailyWorkOut> mWalkingWorkouts;
    private List<DailyWorkOut> mBikingWorkouts;
    private List<DailyWorkOut> mRunningWorkouts;

    private int mBarCount;

    private BarData mDistBarData;
    private BarData mTimeBarData;
    private BarData mStepsBarData;
    private Calendar mStartDate;
    private Calendar mEndDate;

    private float mDistGoal;
    private float mTimeGoal;
    private float mStepsGoal;

    private int mDistUOMpref;
    private int mSpeedUOMpref;

    private boolean mIsDisplayRunning = false;
    private boolean mIsDisplayWalking = false;
    private boolean mIsDisplayBiking = false;

    private boolean mIsDisplayStacked = false;
    private boolean mIsDisplayGroup = false;

    private boolean mIsDisplayDistance = false;
    private boolean mIsDisplayTime = false;
    private boolean mIsDisplaySteps = false;

    private boolean mIsDisplayWeekly = false;
    private boolean mIsDisplayMonthly = false;

    private utils.Interval mChartStyle;

    private void getPreferences() {
        //get the passed preferences
        mDistUOMpref = getIntent().getIntExtra(SettingsActivity.KEY_DISTANCE_UOM, 0);
        mSpeedUOMpref = getIntent().getIntExtra(SettingsActivity.KEY_SPEED_UOM, 0);

        //get charting preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mIsDisplayWeekly = getIntent().getBooleanExtra(SettingsActivity.KEY_CHART_DISPLAY, true);

        mIsDisplayDistance = sharedPref.getBoolean(SettingsActivity.KEY_CHART_DISPLAY_DISTANCE, true);
        mIsDisplaySteps = sharedPref.getBoolean(SettingsActivity.KEY_CHART_DISPLAY_STEPS, false);
        mIsDisplayTime = sharedPref.getBoolean(SettingsActivity.KEY_CHART_DISPLAY_TIME, false);

        mIsDisplayWalking = sharedPref.getBoolean(SettingsActivity.KEY_CHART_DISPLAY_WALKING, true);
        mIsDisplayRunning = sharedPref.getBoolean(SettingsActivity.KEY_CHART_DISPLAY_RUNNING, false);
        mIsDisplayBiking = sharedPref.getBoolean(SettingsActivity.KEY_CHART_DISPLAY_BIKING, false);

        mIsDisplayStacked = sharedPref.getBoolean(SettingsActivity.KEY_CHART_DISPLAY_STACKED_OR_TOTAL, false);

        // TODO: 10/27/2016 add the goal preferences
        mDistGoal = sharedPref.getFloat(SettingsActivity.KEY_GOAL_DISTANCE, 8.0f);
        mTimeGoal = sharedPref.getFloat(SettingsActivity.KEY_GOAL_TIME, 30.0f);
        mStepsGoal = sharedPref.getFloat(SettingsActivity.KEY_GOAL_STEPS, 6000.0f);

    }

    private void getChartData() {
        DBHelper dbHelper = new DBHelper(this);
        mWalkingWorkouts = new ArrayList<>();
        mRunningWorkouts = new ArrayList<>();
        mBikingWorkouts = new ArrayList<>();

        mBarCount = 0;
        mWalkingWorkouts = dbHelper.getDailyWorkOuts(utils.MODE_WALKING);
        mBikingWorkouts = dbHelper.getDailyWorkOuts(utils.MODE_BIKING);
        mRunningWorkouts = dbHelper.getDailyWorkOuts(utils.MODE_RUNNING);

        if (mWalkingWorkouts != null)
            mBarCount = mWalkingWorkouts.size();
        else mBarCount = 0;


    }

    private void setUpUI() {
        mBarChart = (BarChart) findViewById(R.id.chart);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getPreferences();

        // TODO: 10/26/2016 need to handle time zone it date times.

        setUpUI();
        getChartData();
        addChartData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.chart_selections, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_option_show_distance:
                mIsDisplayDistance = !mIsDisplayDistance;
                item.setChecked(mIsDisplayDistance);
                break;
            case R.id.menu_option_show_steps:
                mIsDisplaySteps = !mIsDisplaySteps;
                item.setChecked(mIsDisplaySteps);
                break;
            case R.id.menu_option_show_time:
                mIsDisplayTime = !mIsDisplayTime;
                item.setChecked(mIsDisplayTime);
                break;
            case R.id.menu_option_show_walking:
                mIsDisplayWalking = !mIsDisplayWalking;
                item.setChecked(mIsDisplayWalking);
                break;
            case R.id.menu_option_show_running:
                mIsDisplayRunning = !mIsDisplayRunning;
                item.setChecked(mIsDisplayRunning);
                break;
            case R.id.menu_option_show_biking:
                mIsDisplayBiking = !mIsDisplayBiking;
                item.setChecked(mIsDisplayBiking);
                break;
            case R.id.menu_option_show_stacked:
                mIsDisplayStacked = !mIsDisplayStacked;
                item.setChecked(mIsDisplayStacked);
                break;

        }
        addChartData();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.menu_option_show_distance).setChecked(mIsDisplayDistance);
        menu.findItem(R.id.menu_option_show_time).setChecked(mIsDisplayTime);
        menu.findItem(R.id.menu_option_show_steps).setChecked(mIsDisplaySteps);

        menu.findItem(R.id.menu_option_show_walking).setChecked(mIsDisplayWalking);
        menu.findItem(R.id.menu_option_show_running).setChecked(mIsDisplayRunning);
        menu.findItem(R.id.menu_option_show_biking).setChecked(mIsDisplayBiking);
        menu.findItem(R.id.menu_option_show_stacked).setChecked(mIsDisplayStacked);

        return true;
    }

    private void addChartData() {

        if (!mBarChart.isEmpty()) mBarChart.clearValues();

        //calculate is we have multiple modes.
        boolean multiMode = false;
        if (mIsDisplayWalking && mIsDisplayRunning || mIsDisplayWalking && mIsDisplayBiking
                || mIsDisplayRunning && mIsDisplayBiking)
            multiMode = true;

        //calculate if we have multiple values displayes
        boolean multiValue = false;
        if (mIsDisplaySteps && mIsDisplayDistance || mIsDisplaySteps && mIsDisplayTime
                || mIsDisplayDistance && mIsDisplayTime)
            multiValue = true;

        int groupBy = GROUP_BY_NONE;

        if (multiMode && multiValue || !mIsDisplayStacked & multiMode || !mIsDisplayStacked & multiValue) {

            if (multiMode && multiValue) {
                groupBy = GROUP_BY_VALUE;
            } else if (multiMode)
                groupBy = GROUP_BY_MODE;
            else
                groupBy = GROUP_BY_VALUE;
        }


        List<BarEntry> barEntries = new ArrayList<>();
        List<BarEntry> barEntries1 = new ArrayList<>();
        List<BarEntry> barEntries2 = new ArrayList<>();
        List<String> startTimes = new ArrayList<>();

        if (mIsDisplayStacked) {
            float[] distance = new float[]{};
            float[] steps = new float[]{};
            float[] time = new float[]{};

            for (int i = 0; i < mBarCount; i++) {
                if (mIsDisplayBiking && mIsDisplayWalking && mIsDisplayRunning) {
                    distance = new float[]{
                            (float) (mBikingWorkouts.get(i).getDistance() * utils.distUOMConv[mDistUOMpref]),
                            (float) (mRunningWorkouts.get(i).getDistance() * utils.distUOMConv[mDistUOMpref]),
                            (float) (mWalkingWorkouts.get(i).getDistance() * utils.distUOMConv[mDistUOMpref])
                    };

                    steps = new float[]{0, 0, (float) (mWalkingWorkouts.get(i).getSteps())};

                    time = new float[]{
                            (float) (mBikingWorkouts.get(i).getElapseTime() * utils.MilliToMins),
                            (float) (mRunningWorkouts.get(i).getElapseTime() * utils.MilliToMins),
                            (float) (mWalkingWorkouts.get(i).getElapseTime() * utils.MilliToMins)
                    };

                } else if (mIsDisplayWalking && mIsDisplayRunning && !mIsDisplayBiking) {
                    distance = new float[]{
                            (float) (mRunningWorkouts.get(i).getDistance() * utils.distUOMConv[mDistUOMpref]),
                            (float) (mWalkingWorkouts.get(i).getDistance() * utils.distUOMConv[mDistUOMpref])
                    };

                    steps = new float[]{0, 0, (float) (mWalkingWorkouts.get(i).getSteps())};

                    time = new float[]{
                            (float) (mRunningWorkouts.get(i).getElapseTime() * utils.MilliToMins),
                            (float) (mWalkingWorkouts.get(i).getElapseTime() * utils.MilliToMins)
                    };
                } else if (mIsDisplayWalking && mIsDisplayBiking && !mIsDisplayRunning) {
                    distance = new float[]{
                            (float) (mBikingWorkouts.get(i).getDistance() * utils.distUOMConv[mDistUOMpref]),
                            (float) (mWalkingWorkouts.get(i).getDistance() * utils.distUOMConv[mDistUOMpref])
                    };

                    steps = new float[]{0, 0, (float) (mWalkingWorkouts.get(i).getSteps())};

                    time = new float[]{
                            (float) (mBikingWorkouts.get(i).getElapseTime() * utils.MilliToMins),
                            (float) (mWalkingWorkouts.get(i).getElapseTime() * utils.MilliToMins)
                    };
                } else if (mIsDisplayRunning && mIsDisplayBiking && !mIsDisplayWalking) {
                    distance = new float[]{
                            (float) (mBikingWorkouts.get(i).getDistance() * utils.distUOMConv[mDistUOMpref]),
                            (float) (mRunningWorkouts.get(i).getDistance() * utils.distUOMConv[mDistUOMpref])
                    };

                    steps = new float[]{0, 0, 0};

                    time = new float[]{
                            (float) (mBikingWorkouts.get(i).getElapseTime() * utils.MilliToMins),
                            (float) (mRunningWorkouts.get(i).getElapseTime() * utils.MilliToMins)
                    };
                }


                barEntries.add(new BarEntry(i, distance));
                barEntries1.add(new BarEntry(i, steps));
                barEntries2.add(new BarEntry(i, time));


                if (mIsDisplayWalking) startTimes.add(mWalkingWorkouts.get(i).getDate());
                else if (mIsDisplayRunning) startTimes.add(mRunningWorkouts.get(i).getDate());
                else if (mIsDisplayBiking) startTimes.add(mBikingWorkouts.get(i).getDate());


            }

        } else if (groupBy == GROUP_BY_VALUE || groupBy == GROUP_BY_NONE) {


            //not stacked then total
            float distance = 0;
            float steps = 0;
            float time = 0;
            for (int i = 0; i < mBarCount; i++) {
                distance = (float) ((((mIsDisplayBiking) ? mBikingWorkouts.get(i).getDistance() : 0) +
                        ((mIsDisplayRunning) ? mRunningWorkouts.get(i).getDistance() : 0) +
                        ((mIsDisplayWalking) ? mWalkingWorkouts.get(i).getDistance() : 0)) * utils.distUOMConv[mDistUOMpref]);
                barEntries.add(new BarEntry(i, distance));

                steps = (float) ((mIsDisplayWalking) ? mWalkingWorkouts.get(i).getSteps() : 0);
                barEntries1.add(new BarEntry(i, steps));

                time = (float) ((((mIsDisplayBiking) ? mBikingWorkouts.get(i).getElapseTime() : 0) +
                        ((mIsDisplayRunning) ? mRunningWorkouts.get(i).getElapseTime() : 0) +
                        ((mIsDisplayWalking) ? mWalkingWorkouts.get(i).getElapseTime() : 0)) * utils.MilliToMins);

                barEntries2.add(new BarEntry(i, time));

                if (mIsDisplayWalking) startTimes.add(mWalkingWorkouts.get(i).getDate());
                else if (mIsDisplayRunning) startTimes.add(mRunningWorkouts.get(i).getDate());
                else if (mIsDisplayBiking) startTimes.add(mBikingWorkouts.get(i).getDate());

            }


        } else if (groupBy == GROUP_BY_MODE) {
            //data is to be display by mode.
            float value = -1;
            float value1 = -1;
            float value2 = -1;

            for (int i = 0; i < mBarCount; i++) {
                if (mIsDisplayDistance) {
                    if (mIsDisplayWalking)
                        value = (float) (mWalkingWorkouts.get(i).getDistance() * utils.distUOMConv[mDistUOMpref]);
                    if (mIsDisplayRunning)
                        value1 = (float) (mRunningWorkouts.get(i).getDistance() * utils.distUOMConv[mDistUOMpref]);
                    if (mIsDisplayBiking)
                        value2 = (float) (mBikingWorkouts.get(i).getDistance() * utils.distUOMConv[mDistUOMpref]);
                } else if (mIsDisplaySteps) {
                    if (mIsDisplayWalking) value = (float) (mWalkingWorkouts.get(i).getSteps());
                    if (mIsDisplayRunning) value1 = (float) (mRunningWorkouts.get(i).getSteps());
                    if (mIsDisplayBiking) value2 = (float) (mBikingWorkouts.get(i).getSteps());
                } else if (mIsDisplayTime) {

                    if (mIsDisplayWalking)
                        value = (float) (mWalkingWorkouts.get(i).getElapseTime() * utils.MilliToMins);
                    if (mIsDisplayRunning)
                        value1 = (float) (mRunningWorkouts.get(i).getElapseTime() * utils.MilliToMins);
                    if (mIsDisplayBiking)
                        value2 = (float) (mBikingWorkouts.get(i).getElapseTime() * utils.MilliToMins);
                } else {
                    Toast.makeText(this, "Need at least one mode", Toast.LENGTH_SHORT).show();
                    return;
                }
                barEntries.add(new BarEntry(i, value));  //walking
                barEntries1.add(new BarEntry(i, value1)); //running
                barEntries2.add(new BarEntry(i, value2)); //biking
            }

        } else {
            Toast.makeText(this, "Nothing to display", Toast.LENGTH_LONG).show();
            return;
        }

        BarDataSet barDataSet = null;
        LimitLine limitLine = null;
        BarDataSet barDataSet1 = null;
        LimitLine limitLine1 = null;
        BarDataSet barDataSet2 = null;
        LimitLine limitLine2 = null;

        float groupSpace = 0.06f;
        float barSpace = 0.02f;
        float barWidth = 0.45f;
        switch (groupBy) {
            case GROUP_BY_NONE:
                //prepare the distance data set

                if (mIsDisplayDistance) {
                    barDataSet = new BarDataSet(barEntries, "Distance");
                    barDataSet.setColor(getResources().getColor(R.color.colorBarChart1));
                    if (mDistGoal > 0)
                        limitLine = new LimitLine(mDistGoal, "Miles Per Day");
                } else if (mIsDisplayTime) {
                    barDataSet = new BarDataSet(barEntries1, "Time");
                    barDataSet.setColor(getResources().getColor(R.color.colorBarChart2));
                    if (mTimeGoal > 0)
                        limitLine = new LimitLine(mTimeGoal, "Minutes Per Day");
                } else if (mIsDisplaySteps) {
                    barDataSet = new BarDataSet(barEntries2, "Steps");
                    barDataSet.setColor(getResources().getColor(R.color.colorBarChart3));
                    if (mStepsGoal > 0)
                        limitLine = new LimitLine(mStepsGoal, "Steps Per Day");
                } else {
                    Toast.makeText(this, "Nothing to display.  You must pick at least one value.", Toast.LENGTH_LONG).show();
                    return;
                }
                barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                BarData barData = new BarData(barDataSet);
                barData.setBarWidth(0.9f);
                barData.setValueTextSize(8f);
                mBarChart.setData(barData);

                FormatChart(barData, limitLine, startTimes);
                break;
            case GROUP_BY_MODE:

                BarData data = null;
                if (mIsDisplayDistance) {
                    if (mIsDisplayWalking)
                        barDataSet = new BarDataSet(barEntries, "Distance by Walking");
                    if (mIsDisplayRunning)
                        barDataSet1 = new BarDataSet(barEntries1, "Distance by Running");
                    if (mIsDisplayBiking)
                        barDataSet2 = new BarDataSet(barEntries2, "Distance by Biking");

                } else if (mIsDisplayTime) {
                    if (mIsDisplayWalking)
                        barDataSet = new BarDataSet(barEntries, "Distance by Walking");
                    if (mIsDisplayRunning)
                        barDataSet1 = new BarDataSet(barEntries1, "Distance by Running");
                    if (mIsDisplayBiking)
                        barDataSet2 = new BarDataSet(barEntries2, "Distance by Biking");


                } else if (mIsDisplaySteps) {
                    if (mIsDisplayWalking)
                        barDataSet = new BarDataSet(barEntries, "Steps by Walking");
                    if (mIsDisplayRunning)
                        barDataSet1 = new BarDataSet(barEntries1, "Steps by Running");
                    if (mIsDisplayBiking)
                        barDataSet2 = new BarDataSet(barEntries2, "Steps by Biking");
                }

                if (mIsDisplayWalking && mIsDisplayRunning && mIsDisplayBiking) {
                    groupSpace = 0.04f;
                    barSpace = 0.02f;
                    barWidth = 0.30f;


                    data = new BarData(barDataSet, barDataSet1, barDataSet2);

                } else if (mIsDisplayWalking && mIsDisplayRunning && !mIsDisplayBiking) {
                    data = new BarData(barDataSet, barDataSet1);
                } else if (mIsDisplayWalking && !mIsDisplayRunning && mIsDisplayBiking) {
                    data = new BarData(barDataSet, barDataSet2);
                } else if (!mIsDisplayWalking && mIsDisplayRunning && !mIsDisplayBiking) {
                    data = new BarData(barDataSet1, barDataSet2);
                } else {
                    Toast.makeText(this, "Nothing to display", Toast.LENGTH_LONG).show();
                    return;
                }

                if (barDataSet != null)
                    barDataSet.setColor(getResources().getColor(R.color.colorBarChart1));
                if (barDataSet1 != null)
                    barDataSet1.setColor(getResources().getColor(R.color.colorBarChart2));
                if (barDataSet2 != null)
                    barDataSet2.setColor(getResources().getColor(R.color.colorBarChart3));


                barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                mBarChart.getAxisRight().setEnabled(false);
                data.setBarWidth(barWidth);
                mBarChart.setData(data);
                mBarChart.groupBars(0f, groupSpace, barSpace);
                mBarChart.invalidate();
                break;
            case GROUP_BY_VALUE:
                if (mIsDisplayDistance) barDataSet = new BarDataSet(barEntries, "Distance");
                if (mIsDisplaySteps) barDataSet1 = new BarDataSet(barEntries1, "Steps");
                if (mIsDisplayTime) barDataSet2 = new BarDataSet(barEntries2, "Time");

                if (mIsDisplayStacked) {
                    if (barDataSet != null)
                        barDataSet.setColors(new int[]{R.color.colorStack0BarChart1,
                                R.color.colorStack0BarChart2,
                                R.color.colorStack0BarChart3}, this);
                   /* if (barDataSet1 != null)
                        barDataSet.setColors(new int[] {getResources().getColor(R.color.colorStack1BarChart1),
                                getResources().getColor(R.color.colorStack1BarChart2),
                                getResources().getColor(R.color.colorStack1BarChart3)}, this);
                    if (barDataSet2 != null)
                        barDataSet.setColors(new int[] {getResources().getColor(R.color.colorStack2BarChart1),
                                getResources().getColor(R.color.colorStack2BarChart2),
                                getResources().getColor(R.color.colorStack2BarChart3)}, this);*/
                    if (barDataSet1 != null)
                        barDataSet1.setColors(new int[]{R.color.colorStack1BarChart1,
                                R.color.colorStack1BarChart2,
                                R.color.colorStack1BarChart3}, this);
                    if (barDataSet2 != null)
                        barDataSet2.setColors(new int[]{R.color.colorStack2BarChart1,
                                R.color.colorStack2BarChart2,
                                R.color.colorStack2BarChart3}, this);
                } else {
                    if (barDataSet != null)
                        barDataSet.setColor(getResources().getColor(R.color.colorBarChart1));
                    if (barDataSet1 != null)
                        barDataSet1.setColor(getResources().getColor(R.color.colorBarChart2));
                    if (barDataSet2 != null)
                        barDataSet2.setColor(getResources().getColor(R.color.colorBarChart3));
                }


                if (mIsDisplayDistance && mIsDisplaySteps && mIsDisplayTime) {
                    groupSpace = 0.04f;
                    barSpace = 0.02f;
                    barWidth = 0.30f;

                    data = new BarData(barDataSet, barDataSet1, barDataSet2);
                    barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
                    barDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
                    barDataSet2.setAxisDependency(YAxis.AxisDependency.RIGHT);

                } else if (mIsDisplayDistance && mIsDisplaySteps && !mIsDisplayTime) {
                    data = new BarData(barDataSet, barDataSet1);
                    barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    barDataSet1.setAxisDependency(YAxis.AxisDependency.RIGHT);
                } else if (mIsDisplayDistance && !mIsDisplaySteps && mIsDisplayTime) {
                    data = new BarData(barDataSet, barDataSet2);
                    barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    barDataSet2.setAxisDependency(YAxis.AxisDependency.RIGHT);
                } else if (!mIsDisplayDistance && mIsDisplaySteps && mIsDisplayTime) {
                    data = new BarData(barDataSet1, barDataSet2);
                    barDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
                    barDataSet2.setAxisDependency(YAxis.AxisDependency.RIGHT);
                } else {
                    Toast.makeText(this, "Nothing to display", Toast.LENGTH_LONG).show();
                    return;
                }

                data.setBarWidth(barWidth);
                mBarChart.getAxisRight().setEnabled(true);
                mBarChart.setData(data);
                mBarChart.groupBars(0f, groupSpace, barSpace);
                FormatChart(data, null, startTimes);
                break;

        }

    }

    private void FormatChart(BarData barData, LimitLine limitLine, List<String> startTimes) {



        /*limitLine.setLineColor(ResourcesCompat.getColor(getResources(), R.color.colorBarChart1, null));
        limitLine.setLineWidth(4f);
        limitLine.setTextSize(12f);
*/
        //set up the xAxis
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(45f);
        xAxis.setValueFormatter(new ChartDateFormatter(startTimes));


        //add the data to the chart and display

        mBarChart.setFitBars(true);
        Description description = new Description();
        description.setText("Daily Workouts");
        //description.setPosition(f, 300f);
        description.setTextSize(14f);
        mBarChart.setDescription(description);


        mBarChart.invalidate();

    }

    private float calcXAxis(BarData barData, float goal) {
        if (barData.getYMax() > goal)
            return barData.getYMax() * 1.05f;
        else
            return goal * 1.05f;

    }

}

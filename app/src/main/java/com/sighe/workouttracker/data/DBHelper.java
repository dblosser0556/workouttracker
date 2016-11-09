package com.sighe.workouttracker.data;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.sighe.workouttracker.utility.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


/**
 * Created by dad on 6/23/16.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "WorkOutTracker.db";
    private static final int DATABASE_VERSION = 3;

    /*work out summary*/
    private static final String WO_SUM_TABLE_NAME = "workoutSummary";
    private static final String WO_SUM_COLUMN_ID = "_id";
    private static final String WO_SUM_COLUMN_EVENT_NO = "eventNo";
    private static final String WO_SUM_COLUMN_WORKOUT_MODE = "workoutMode";
    private static final String WO_SUM_COLUMN_STARTTIME = "startDateTime";
    private static final String WO_SUM_COLUMN_ENDTIME = "endDateTime";
    private static final String WO_SUM_COLUMN_TOTAL_DISTANCE = "distance";
    private static final String WO_SUM_COLUMN_TOTAL_DISTANCE_UOM = "distanceUOM";
    private static final String WO_SUM_COLUMN_TOTAL_STEPS = "totalSteps";
    private static final String WO_SUM_COLUMN_AVE_STEPS_PER_MIN = "aveStepsPerMin";
    private static final String WO_SUM_COLUMN_AVE_SPEED = "aveSpeed";
    private static final String WO_SUM_COLUMN_AVE_SPEED_UOM = "aveSpeedUOM";
    private static final String WO_SUM_COLUMN_ELAPSED_TIME = "elapseTime";

    /* route table */
    private static final String RT_TABLE_NAME = "routeGallery";
    private static final String RT_COLUMN_ID = "_id";
    private static final String RT_COLUMN_EVENT_NO = "eventNo";
    private static final String RT_COLUMN_ROUTE_NAME = "routeName";
    private static final String RT_COLUMN_SAVED_DATE = "routeDate";
    private static final String RT_COLUMN_LOCATION_LAT = "routeLat";
    private static final String RT_COLUMN_LOCATION_LON = "routeLon";

    /* route details table */
    private static final String RT_DET_TABLE_NAME = "routeDetails";
    private static final String RT_DET_COLUMN_ID = "_id";
    private static final String RT_DET_COLUMN_EVENT_NO = "eventNo";
    private static final String RT_DET_COLUMN_LOCATION_TIME = "time";
    private static final String RT_DET_COLUMN_LOCATION_LAT = "latitude";
    private static final String RT_DET_COLUMN_LOCATION_LON = "longitude";
    private static final String RT_DET_COLUMN_INCR_STEPS = "incrSteps";
    private static final String RT_DET_COLUMN_INCR_TIME_MILLI = "incrTimeMilli";
    private static final String RT_DET_COLUMN_INCR_DIST = "incrDist";

    /*counter table */
    private static final String COUNTER_TABLE_NAME = "Counter";
    private static final String COUNTER_COLUMN_ID = "_id";
    private static final String COUNTER_COLUMN_COUNTER = "counterName";
    private static final String COUNTER_COLUMN_COUNTER_NEXT_NUMBER = "nextNumber";

    /*counter table seed data*/
    private static final String EVENT_NO = "eventNo";
    private static final int EVENT_NO_SEED = 1;

    /* goals table */
    private static final String GOAL_TABLE_NAME = "Goals";
    private static final String GOAL_COLUMN_ID = "_id";
    private static final String GOAL_COLUMN_MODE = "mode";
    private static final String GOAL_COLUMN_VALUE_TYPE = "valueType";
    private static final String GOAL_COLUMN_HOW_OFTEN = "interval";
    private static final String GOAL_COLUMN_VALUE = "value";
    private static final String GOAL_COLUMN_EFFECTIVE_DATE = "effecitve_date";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + WO_SUM_TABLE_NAME +
                        "(" + WO_SUM_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        WO_SUM_COLUMN_EVENT_NO + " INTEGER, " +
                        WO_SUM_COLUMN_WORKOUT_MODE + " INTEGER, " +
                        WO_SUM_COLUMN_STARTTIME + " DATE, " +
                        WO_SUM_COLUMN_ENDTIME + " DATE, " +
                        WO_SUM_COLUMN_TOTAL_DISTANCE + " REAL, " +
                        WO_SUM_COLUMN_TOTAL_DISTANCE_UOM + " TEXT, " +
                        WO_SUM_COLUMN_TOTAL_STEPS + " REAL, " +
                        WO_SUM_COLUMN_AVE_STEPS_PER_MIN + " REAL, " +
                        WO_SUM_COLUMN_AVE_SPEED + "    REAL, " +
                        WO_SUM_COLUMN_AVE_SPEED_UOM + " TEXT, " +
                        WO_SUM_COLUMN_ELAPSED_TIME + " INTEGER) "
        );

        db.execSQL(
                "CREATE TABLE " + RT_DET_TABLE_NAME +
                        "(" + RT_DET_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        RT_DET_COLUMN_EVENT_NO + " INTEGER, " +
                        RT_DET_COLUMN_LOCATION_TIME + " INTEGER, " +
                        RT_DET_COLUMN_LOCATION_LAT + " REAL, " +
                        RT_DET_COLUMN_LOCATION_LON + " REAL, " +
                        RT_DET_COLUMN_INCR_STEPS + " REAL, " +
                        RT_DET_COLUMN_INCR_TIME_MILLI + " INTEGER, " +
                        RT_DET_COLUMN_INCR_DIST + " REAL) "


        );

        db.execSQL(
                "CREATE TABLE " + RT_TABLE_NAME +
                        "(" + RT_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        RT_COLUMN_EVENT_NO + " INTEGER, " +
                        RT_COLUMN_ROUTE_NAME + " TEXT, " +
                        RT_COLUMN_LOCATION_LAT + " REAL, " +
                        RT_COLUMN_LOCATION_LON + " REAL, " +
                        RT_COLUMN_SAVED_DATE + " INTEGER) "
        );

        db.execSQL(
                "CREATE TABLE " + COUNTER_TABLE_NAME +
                        "(" + COUNTER_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        COUNTER_COLUMN_COUNTER + " TEXT, " +
                        COUNTER_COLUMN_COUNTER_NEXT_NUMBER + " INTEGER) ");


        db.execSQL("INSERT INTO " + COUNTER_TABLE_NAME +
                "(" + COUNTER_COLUMN_COUNTER + "," + COUNTER_COLUMN_COUNTER_NEXT_NUMBER + ") " +
                "VALUES ('" + EVENT_NO + "'," + EVENT_NO_SEED + ")");


        db.execSQL("CREATE TABLE " + GOAL_TABLE_NAME +
                "(" + GOAL_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                GOAL_COLUMN_MODE + " INTEGER, " +
                GOAL_COLUMN_VALUE_TYPE + " INTEGER, " +
                GOAL_COLUMN_HOW_OFTEN + " INTEGER, " +
                GOAL_COLUMN_VALUE + " REAL, " +
                GOAL_COLUMN_EFFECTIVE_DATE + " INTEGER) ");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {

            case 3:

        }


    }

    //methods to save the route details  assumes the event number is known.
    public boolean saveWorkSummary(int eventNo, int workoutMode, long startDateMilli, long endDateMilli,
                                   double distance, String distUOM, double totalSteps,
                                   double aveStepsPerMIn, double aveSpeed, String speedUOM,
                                   long elapseTime) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues contentValues = new ContentValues();

        contentValues.put(WO_SUM_COLUMN_EVENT_NO, eventNo);
        contentValues.put(WO_SUM_COLUMN_WORKOUT_MODE, workoutMode);
        contentValues.put(WO_SUM_COLUMN_STARTTIME, startDateMilli);
        contentValues.put(WO_SUM_COLUMN_ENDTIME, endDateMilli);
        contentValues.put(WO_SUM_COLUMN_TOTAL_DISTANCE, distance);
        contentValues.put(WO_SUM_COLUMN_TOTAL_DISTANCE_UOM, distUOM);
        contentValues.put(WO_SUM_COLUMN_TOTAL_STEPS, totalSteps);
        contentValues.put(WO_SUM_COLUMN_AVE_STEPS_PER_MIN, aveStepsPerMIn);
        contentValues.put(WO_SUM_COLUMN_AVE_SPEED, aveSpeed);
        contentValues.put(WO_SUM_COLUMN_AVE_SPEED_UOM, speedUOM);
        contentValues.put(WO_SUM_COLUMN_ELAPSED_TIME, elapseTime);


        db.insert(WO_SUM_TABLE_NAME, null, contentValues);

        db.close();


        return true;
    }

    //methods to save the route details  assumes the event number is known.
    public boolean saveRouteDetail(int eventNo, long fixTime, double lat, double lon,
                                   double steps, long time, double dist) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues contentValues = new ContentValues();

        contentValues.put(RT_DET_COLUMN_EVENT_NO, eventNo);
        contentValues.put(RT_DET_COLUMN_LOCATION_LAT, lat);
        contentValues.put(RT_DET_COLUMN_LOCATION_LON, lon);
        contentValues.put(RT_DET_COLUMN_LOCATION_TIME, fixTime);
        contentValues.put(RT_DET_COLUMN_INCR_STEPS, steps);
        contentValues.put(RT_DET_COLUMN_INCR_TIME_MILLI, time);
        contentValues.put(RT_DET_COLUMN_INCR_DIST, dist);


        db.insert(RT_DET_TABLE_NAME, null, contentValues);
        db.close();


        return true;
    }

    public boolean saveRoute(String routeName, int eventNo, double lat, double lon, long date) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues contentValues = new ContentValues();

        contentValues.put(RT_COLUMN_EVENT_NO, eventNo);
        contentValues.put(RT_COLUMN_LOCATION_LAT, lat);
        contentValues.put(RT_COLUMN_LOCATION_LON, lon);
        contentValues.put(RT_COLUMN_ROUTE_NAME, routeName);
        contentValues.put(RT_COLUMN_SAVED_DATE, date);


        db.insert(RT_TABLE_NAME, null, contentValues);
        db.close();

        return true;
    }

    public int getNextEventNo() {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT " + COUNTER_COLUMN_COUNTER_NEXT_NUMBER + " FROM " +
                COUNTER_TABLE_NAME + " WHERE " +
                COUNTER_COLUMN_COUNTER + " = '" + EVENT_NO + "'";


        Cursor res = db.rawQuery(query, null);
        res.moveToFirst();
        try {
            int eventNo = res.getInt(res.getColumnIndex(COUNTER_COLUMN_COUNTER_NEXT_NUMBER));
            res.close();

            //update the next value
            ContentValues contentValues = new ContentValues();
            contentValues.put(COUNTER_COLUMN_COUNTER_NEXT_NUMBER, eventNo + 1);

            db.update(COUNTER_TABLE_NAME, contentValues,
                    COUNTER_COLUMN_COUNTER + " = ?",
                    new String[]{EVENT_NO});

            return eventNo;

        } catch (SQLiteException e) {
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }


    }

    public Integer deleteRouteDetails(int eventNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(RT_DET_TABLE_NAME,
                RT_DET_COLUMN_EVENT_NO + " = ? ",
                new String[]{String.valueOf(eventNo)});
    }


    public List<WorkOut> getWorkOuts() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<WorkOut> workOuts = new ArrayList<>();

        String query = "SELECT * FROM " + WO_SUM_TABLE_NAME;

        Cursor res = db.rawQuery(query, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            WorkOut workOut = new WorkOut(
                    res.getInt(res.getColumnIndex(WO_SUM_COLUMN_EVENT_NO)),
                    res.getInt(res.getColumnIndex(WO_SUM_COLUMN_WORKOUT_MODE)),
                    res.getLong(res.getColumnIndex(WO_SUM_COLUMN_STARTTIME)),
                    res.getLong(res.getColumnIndex(WO_SUM_COLUMN_ENDTIME)),
                    res.getDouble(res.getColumnIndex(WO_SUM_COLUMN_TOTAL_DISTANCE)),
                    res.getDouble(res.getColumnIndex(WO_SUM_COLUMN_TOTAL_STEPS)),
                    res.getDouble(res.getColumnIndex(WO_SUM_COLUMN_AVE_STEPS_PER_MIN)),
                    res.getDouble(res.getColumnIndex(WO_SUM_COLUMN_AVE_SPEED)),
                    res.getLong(res.getColumnIndex(WO_SUM_COLUMN_ELAPSED_TIME)));
            workOuts.add(workOut);
            res.moveToNext();
        }

        return workOuts;

    }

    public Long getEarliestStartDate() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT MIN(" + WO_SUM_COLUMN_STARTTIME + ") AS " +
                WO_SUM_COLUMN_STARTTIME + " FROM " + WO_SUM_TABLE_NAME;
        Cursor res = db.rawQuery(query, null);
        res.moveToFirst();

        String value = res.getString(res.getColumnIndex(WO_SUM_COLUMN_STARTTIME));
        if (value != null) {
            return Long.valueOf(res.getString(res.getColumnIndex(WO_SUM_COLUMN_STARTTIME)));
        } else {
            return null;
        }

    }

    public List<DailyWorkOut> getDailyWorkOuts(int workOutMode) {
        SQLiteDatabase db = this.getReadableDatabase();

        Long earliestStartDate = getEarliestStartDate();

        if (earliestStartDate == null)
            return null;

        //we want to return an array of daily workouts for all days from day 1 to now.
        //calculate the number of days between the start and end date
        //need to find the minimal start date.


        List<DailyWorkOut> workOuts = new ArrayList<>();

        Calendar startDate = GregorianCalendar.getInstance(TimeZone.getDefault());
        startDate.setTimeInMillis(utils.getMidNight(earliestStartDate, false));

        Calendar endDate = GregorianCalendar.getInstance(TimeZone.getDefault());
        endDate.setTimeInMillis(utils.getMidNight(endDate.getTimeInMillis(), true));


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //startDate = sdf.format(startDateMilli);

        String query = "SELECT STRFTIME('%Y-%m-%d'," + WO_SUM_COLUMN_STARTTIME + "/1000,'unixepoch') as " + WO_SUM_COLUMN_STARTTIME + ", " +
                WO_SUM_COLUMN_WORKOUT_MODE + ", " +
                "SUM(" + WO_SUM_COLUMN_TOTAL_DISTANCE + ") as " + WO_SUM_COLUMN_TOTAL_DISTANCE + ", " +
                "SUM(" + WO_SUM_COLUMN_TOTAL_STEPS + ") as " + WO_SUM_COLUMN_TOTAL_STEPS + ", " +
                "SUM(" + WO_SUM_COLUMN_ELAPSED_TIME + ") as " + WO_SUM_COLUMN_ELAPSED_TIME +
                " FROM " + WO_SUM_TABLE_NAME +
                " WHERE " + WO_SUM_COLUMN_WORKOUT_MODE + " = " + String.valueOf(workOutMode) +
                " GROUP BY " + WO_SUM_COLUMN_WORKOUT_MODE + ", " +
                " STRFTIME('%Y-%m-%d'," + WO_SUM_COLUMN_STARTTIME + "/1000,'unixepoch')";

        Cursor res = db.rawQuery(query, null);
        while (startDate.before(endDate)) {
            DailyWorkOut workOut = new DailyWorkOut();
            boolean found = false;
            res.moveToFirst();
            while (!res.isAfterLast() && !found) {
                if (sdf.format(startDate.getTime()).equals(res.getString(res.getColumnIndex(WO_SUM_COLUMN_STARTTIME)))) {
                    workOut = new DailyWorkOut(
                            res.getInt(res.getColumnIndex(WO_SUM_COLUMN_WORKOUT_MODE)),
                            res.getString(res.getColumnIndex(WO_SUM_COLUMN_STARTTIME)),
                            res.getDouble(res.getColumnIndex(WO_SUM_COLUMN_TOTAL_DISTANCE)),
                            res.getDouble(res.getColumnIndex(WO_SUM_COLUMN_TOTAL_STEPS)),
                            res.getLong(res.getColumnIndex(WO_SUM_COLUMN_ELAPSED_TIME)));
                    found = true;
                }

                res.moveToNext();
            }
            if (!found) {
                workOut = new DailyWorkOut(0, sdf.format(startDate.getTime()), 0, 0, 0);
            }
            workOuts.add(workOut);
            startDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        res.close();
        return workOuts;

    }

    public List<RouteDetail> getRouteDetails(int eventNo) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<RouteDetail> details = new ArrayList<>();

        String query = "SELECT * FROM " + RT_DET_TABLE_NAME +
                " WHERE " + RT_DET_COLUMN_EVENT_NO + " = " +
                String.valueOf(eventNo);

        Cursor res = db.rawQuery(query, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            RouteDetail detail = new RouteDetail(
                    res.getInt(res.getColumnIndex(RT_DET_COLUMN_EVENT_NO)),
                    res.getLong(res.getColumnIndex(RT_DET_COLUMN_LOCATION_TIME)),
                    res.getDouble(res.getColumnIndex(RT_DET_COLUMN_LOCATION_LAT)),
                    res.getDouble(res.getColumnIndex(RT_DET_COLUMN_LOCATION_LON)),
                    res.getDouble(res.getColumnIndex(RT_DET_COLUMN_INCR_STEPS)),
                    res.getLong(res.getColumnIndex(RT_DET_COLUMN_INCR_TIME_MILLI)),
                    res.getDouble(res.getColumnIndex(RT_DET_COLUMN_INCR_DIST)));
            details.add(detail);
            res.moveToNext();
        }
        return details;
    }

    public boolean saveOrUpdateGoal(Goal goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(GOAL_COLUMN_MODE, goal.getModeType().value);
        contentValues.put(GOAL_COLUMN_VALUE_TYPE, goal.getValueType().value);
        contentValues.put(GOAL_COLUMN_HOW_OFTEN, goal.getIntervalType().value);
        contentValues.put(GOAL_COLUMN_VALUE, goal.getValue());

        contentValues.put(GOAL_COLUMN_EFFECTIVE_DATE, goal.getEffectiveDate());

        if (goal.getId() == 0) {


            db.insert(GOAL_TABLE_NAME, null, contentValues);
        } else {
            db.update(GOAL_TABLE_NAME, contentValues,
                    GOAL_COLUMN_ID + " = ?",
                    new String[]{String.valueOf(goal.getId())});
        }
        db.close();


        return true;
    }

    public List<Goal> getGoals() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Goal> goals = new ArrayList<>();

        String query = "SELECT * FROM " + GOAL_TABLE_NAME;
        Cursor res = db.rawQuery(query, null);

        res.moveToFirst();

        while (!res.isAfterLast()) {
            Goal goal = new Goal(
                    res.getInt(res.getColumnIndex(GOAL_COLUMN_ID)),
                    res.getInt(res.getColumnIndex(GOAL_COLUMN_MODE)),
                    res.getInt(res.getColumnIndex(GOAL_COLUMN_VALUE_TYPE)),
                    res.getInt(res.getColumnIndex(GOAL_COLUMN_HOW_OFTEN)),
                    res.getDouble(res.getColumnIndex(GOAL_COLUMN_VALUE)),
                    res.getLong(res.getColumnIndex(GOAL_COLUMN_EFFECTIVE_DATE)));
            goals.add(goal);

            res.moveToNext();
        }
        res.close();
        return goals;
    }

    public List<Route> getRoutes() {
        List<Route> routes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        String query = "SELECT * FROM " + RT_TABLE_NAME;
        Cursor res = db.rawQuery(query, null);

        res.moveToFirst();

        while (!res.isAfterLast()) {
            Route route = new Route(
                    res.getInt(res.getColumnIndex(RT_COLUMN_ID)),
                    res.getInt(res.getColumnIndex(RT_COLUMN_EVENT_NO)),
                    res.getDouble(res.getColumnIndex(RT_COLUMN_LOCATION_LAT)),
                    res.getDouble(res.getColumnIndex(RT_COLUMN_LOCATION_LON)),
                    res.getString(res.getColumnIndex(RT_COLUMN_ROUTE_NAME)),
                    res.getLong(res.getColumnIndex(RT_COLUMN_SAVED_DATE)));
            routes.add(route);

            res.moveToNext();
        }
        res.close();
        return routes;

    }
    /*public boolean updateStation(Station station, String lineup) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(STATION_COLUMN_NAME,station.getName());
        contentValues.put(STATION_COLUMN_CALLSIGN,station.getCallsign());
        contentValues.put(STATION_COLUMN_AFFILIATE,station.getAffiliate());
        contentValues.put(STATION_COLUMN_BROADCAST_LANGUAGE,
                Utility.stringArrayToField(station.getBroadcastLanguage()));
        contentValues.put(STATION_COLUMN_DESCRIPTION_LANGUAGE,
                Utility.stringArrayToField(station.getDescriptionLanguage()));
        contentValues.put(STATION_COLUMN_SELECTED, station.getSelected());
        Broadcaster broadcaster = station.getBroadcaster();
        if (broadcaster != null) {
            contentValues.put(STATION_COLUMN_CITY, station.getBroadcaster().getCity());
            contentValues.put(STATION_COLUMN_COUNTRY, station.getBroadcaster().getCountry());
            contentValues.put(STATION_COLUMN_POSTALCODE, station.getBroadcaster().getPostalcode());
            contentValues.put(STATION_COLUMN_STATE, station.getBroadcaster().getState());
        }

        Logo logo = station.getLogo();
        if (logo != null) {
            contentValues.put(STATION_COLUMN_URL, station.getLogo().getURL());
            contentValues.put(STATION_COLUMN_HEIGHT, station.getLogo().getHeight());
            contentValues.put(STATION_COLUMN_WIDTH, station.getLogo().getWidth());
            contentValues.put(STATION_COLUMN_MD5, station.getLogo().getMd5());
        }

        db.update(STATION_TABLE_NAME, contentValues,
                STATION_COLUMN_STATION_ID + " = ? AND " +
                STATION_COLUMN_LINEUP + " = ?",
                new String[]{station.getStationID(),lineup});
        db.close();
        return true;
    }*/

    /*public Integer deleteStation( String lineup, Station station) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(STATION_TABLE_NAME,
                STATION_COLUMN_STATION_ID + " = ? AND " +
                        STATION_COLUMN_LINEUP + " = ?",
                new String[]{station.getStationID(),lineup});
    }
*/
    /*public Station getStation(String stationID, String lineup) {
        SQLiteDatabase db = this.getReadableDatabase();
        Station station = new Station();
        String query = "SELECT * FROM " + STATION_TABLE_NAME + " WHERE " +
                STATION_COLUMN_STATION_ID + " = '" + stationID + "' AND " +
                STATION_COLUMN_LINEUP + " = '" +
                lineup + "'";
        //String[] columns = {"stationID"};

        //String query = "SELECT * FROM station";
        Cursor res = db.rawQuery(query,null);

        try{
            res.moveToFirst();
            if (res.getCount()>0) {
                station.setStationID(stationID);
                station.setName(res.getString(res.getColumnIndex(STATION_COLUMN_NAME)));
                station.setCallsign(res.getString(res.getColumnIndex(STATION_COLUMN_CALLSIGN)));
                station.setAffiliate(res.getString(res.getColumnIndex(STATION_COLUMN_AFFILIATE)));
                station.setBroadcastLanguage(Utility.compactValueToStringArray(
                        res.getString(res.getColumnIndex(STATION_COLUMN_BROADCAST_LANGUAGE))));
                station.setDescriptionLanguage(
                        Utility.compactValueToStringArray(
                                res.getString(res.getColumnIndex(STATION_COLUMN_DESCRIPTION_LANGUAGE))));
                station.setSelected(res.getInt(res.getColumnIndex(STATION_COLUMN_SELECTED)));
                Broadcaster broadcaster = new Broadcaster();
                broadcaster.setCity(res.getString(res.getColumnIndex(STATION_COLUMN_CITY)));
                broadcaster.setState(res.getString(res.getColumnIndex(STATION_COLUMN_STATE)));
                broadcaster.setPostalcode(res.getString(res.getColumnIndex(STATION_COLUMN_POSTALCODE)));
                broadcaster.setCountry(res.getString(res.getColumnIndex(STATION_COLUMN_COUNTRY)));
                station.setBroadcaster(broadcaster);

                Logo logo = new Logo();
                logo.setURL(res.getString(res.getColumnIndex(STATION_COLUMN_URL)));
                logo.setHeight(res.getString(res.getColumnIndex(STATION_COLUMN_HEIGHT)));
                logo.setWidth(res.getString(res.getColumnIndex(STATION_COLUMN_WIDTH)));
                logo.setMd5(res.getString(res.getColumnIndex(STATION_COLUMN_MD5)));
                station.setLogo(logo);
            }
        }
        catch (Exception e){
            Log.d("GetStation", "getStation: dbError");
        }
        finally {
            res.close();
        }

        return station;
    }*/


}

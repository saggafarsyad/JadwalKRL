package net.area54labs.jadwalkrl2;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import net.area54labs.jadwalkrl2.data.AppContract;
import net.area54labs.jadwalkrl2.utils.PollingCheck;
import net.area54labs.jadwalkrl2.utils.SearchSetting;
import net.area54labs.jadwalkrl2.utils.Utility;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

/**
 * Created by Saggaf on 3/2/2015.
 */
public class TestUtilites extends AndroidTestCase {
    // Route
    public static int routeOneId = 1;
    // Route Path
    public static int routePathOneId = routeOneId;
    // Schedule
    public static int testScheduleRoute = routeOneId;
    public static String routeOneName = "Bogor - Jakarta Kota";
    public static int routeTwoId = 2;
    public static String routeTwoName = "Jakarta Kota - Bekasi";
    // Station
    public static String stationBogorId = "BOO";
    public static String testRoutePath1Station = stationBogorId;
    public static String testScheduleStation = stationBogorId;
    public static String stationBogorName = "Bogor";
    public static double stationBogorLatitude = 0.1;
    public static double stationBogorLongitude = 0.2;
    public static String stationDepokId = "DP";
    public static String testRoutePath2Station = stationDepokId;
    public static String stationDepokName = "Depok";
    public static double stationDepokLatitude = 0.3;
    public static double stationDepokLongitude = 0.4;
    public static String stationManggaraiId = "MRI";
    public static String stationManggaraiName = "Manggarai";
    public static double stationManggaraiLatitude = 0.5;
    public static double stationManggaraiLongitude = 0.6;
    public static int testRoutePath1No = 1;
    public static int testRoutePath1Status = 1;
    public static int testRoutePath2No = 2;
    public static int testRoutePath2Status = 1;
    public static int testScheduleDirection = 1;
    public static int testScheduleDepartTimestamp = 3600;
    public static String testScheduleUnitNo = "1234A";

    // Search Request
    public static String searchSetting1 = "DP";
    public static String searchSetting2 = "DP-BOO-1-24";
    public static long searchTimestamp = Calendar.getInstance().getTimeInMillis();

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    static ContentValues createRouteOneValues() {
        ContentValues routeValues = new ContentValues();
        routeValues.put(AppContract.RouteEntry._ID, routeOneId);
        routeValues.put(AppContract.RouteEntry.COLUMN_NAME, routeOneName);

        return routeValues;
    }

    static ContentValues createRouteTwoValues() {
        ContentValues routeValues = new ContentValues();
        routeValues.put(AppContract.RouteEntry._ID, routeTwoId);
        routeValues.put(AppContract.RouteEntry.COLUMN_NAME, routeTwoName);

        return routeValues;
    }

    static ContentValues createStationBogorValues() {
        ContentValues values = new ContentValues();
        values.put(AppContract.StationEntry._ID, stationBogorId);
        values.put(AppContract.StationEntry.COLUMN_NAME, stationBogorName);
        values.put(AppContract.StationEntry.COLUMN_LATITUDE, stationBogorLatitude);
        values.put(AppContract.StationEntry.COLUMN_LONGITUDE, stationBogorLongitude);

        return values;
    }

    static ContentValues createStationDepokValues() {
        ContentValues values = new ContentValues();
        values.put(AppContract.StationEntry._ID, stationDepokId);
        values.put(AppContract.StationEntry.COLUMN_NAME, stationDepokName);
        values.put(AppContract.StationEntry.COLUMN_LATITUDE, stationDepokLatitude);
        values.put(AppContract.StationEntry.COLUMN_LONGITUDE, stationDepokLongitude);

        return values;
    }

    static ContentValues createStationManggaraiValues() {
        ContentValues values = new ContentValues();
        values.put(AppContract.StationEntry._ID, stationManggaraiId);
        values.put(AppContract.StationEntry.COLUMN_NAME, stationManggaraiName);
        values.put(AppContract.StationEntry.COLUMN_LATITUDE, stationManggaraiLatitude);
        values.put(AppContract.StationEntry.COLUMN_LONGITUDE, stationManggaraiLongitude);

        return values;
    }

    static ContentValues createRoutePath1Values() {
        ContentValues routePath1Values = new ContentValues();
        routePath1Values.put(AppContract.RoutePathEntry.COLUMN_ROUTE_KEY, routePathOneId);
        routePath1Values.put(AppContract.RoutePathEntry.COLUMN_STATION_KEY, testRoutePath1Station);
        routePath1Values.put(AppContract.RoutePathEntry.COLUMN_NO, testRoutePath1No);
        routePath1Values.put(AppContract.RoutePathEntry.COLUMN_STATUS, testRoutePath1Status);

        return routePath1Values;
    }

    static ContentValues createRoutePath2Values() {
        ContentValues routePath2Values = new ContentValues();
        routePath2Values.put(AppContract.RoutePathEntry.COLUMN_ROUTE_KEY, routePathOneId);
        routePath2Values.put(AppContract.RoutePathEntry.COLUMN_STATION_KEY, testRoutePath2Station);
        routePath2Values.put(AppContract.RoutePathEntry.COLUMN_NO, testRoutePath2No);
        routePath2Values.put(AppContract.RoutePathEntry.COLUMN_STATUS, testRoutePath2Status);

        return routePath2Values;
    }

    static ContentValues createQuickSearchValues() {
        ContentValues searchRequestValues = new ContentValues();
        searchRequestValues.put(AppContract.SearchEntry.COLUMN_SETTING, searchSetting1);
        searchRequestValues.put(AppContract.SearchEntry.COLUMN_TIMESTAMP, searchTimestamp);

        return searchRequestValues;
    }

    static ContentValues createAdvancedSearchValues() {
        ContentValues searchRequestValues = new ContentValues();
        searchRequestValues.put(AppContract.SearchEntry.COLUMN_SETTING, searchSetting2);
        searchRequestValues.put(AppContract.SearchEntry.COLUMN_TIMESTAMP, searchTimestamp);

        return searchRequestValues;
    }

    static ContentValues createScheduleValues(long searchRequestRowId) {
        ContentValues scheduleValues = new ContentValues();
        scheduleValues.put(AppContract.ScheduleEntry.COLUMN_SEARCH_KEY, searchRequestRowId);
        scheduleValues.put(AppContract.ScheduleEntry.COLUMN_ROUTE_KEY, testScheduleRoute);
        scheduleValues.put(AppContract.ScheduleEntry.COLUMN_STATION_KEY, testScheduleStation);
        scheduleValues.put(AppContract.ScheduleEntry.COLUMN_DIRECTION, testScheduleDirection);
        scheduleValues.put(AppContract.ScheduleEntry.COLUMN_DEPART_TIMESTAMP, testScheduleDepartTimestamp);
        scheduleValues.put(AppContract.ScheduleEntry.COLUMN_UNIT_NO, testScheduleUnitNo);

        return scheduleValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() + " ' did not match the expected value '" + expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    public void testTimeRangeValidator() {
        SearchSetting searchSetting = new SearchSetting(getContext());

        searchSetting.setTimeBottomLimit(-1);
        searchSetting.setTimeTopLimit(25);

        searchSetting.validateTimeRange();

        assertTrue("Bottom limit is not valid: " + searchSetting.getTimeBottomLimit(), searchSetting.getTimeBottomLimit() >= 1 && searchSetting.getTimeBottomLimit() <= 24);
        assertTrue("Top limit is not valid: " + searchSetting.getTimeTopLimit(), searchSetting.getTimeTopLimit() >= 1 && searchSetting.getTimeTopLimit() <= 24);
        assertTrue("Range is not valid ", searchSetting.getTimeBottomLimit() < searchSetting.getTimeTopLimit());

        searchSetting.setTimeBottomLimit(23);
        searchSetting.setTimeTopLimit(20);

        searchSetting.validateTimeRange();

        assertTrue("Bottom limit is not valid: " + searchSetting.getTimeBottomLimit(), searchSetting.getTimeBottomLimit() >= 1 && searchSetting.getTimeBottomLimit() <= 24);
        assertTrue("Top limit is not valid: " + searchSetting.getTimeTopLimit(), searchSetting.getTimeTopLimit() >= 1 && searchSetting.getTimeTopLimit() <= 24);
        assertTrue("Range is not valid ", searchSetting.getTimeBottomLimit() < searchSetting.getTimeTopLimit());

        searchSetting.setTimeBottomLimit(25);
        searchSetting.setTimeTopLimit(25);

        searchSetting.validateTimeRange();

        assertTrue("Bottom limit is not valid: " + searchSetting.getTimeBottomLimit(), searchSetting.getTimeBottomLimit() >= 1 && searchSetting.getTimeBottomLimit() <= 24);
        assertTrue("Top limit is not valid: " + searchSetting.getTimeTopLimit(), searchSetting.getTimeTopLimit() >= 1 && searchSetting.getTimeTopLimit() <= 24);
        assertTrue("Range is not valid ", searchSetting.getTimeBottomLimit() < searchSetting.getTimeTopLimit());

        searchSetting.setTimeBottomLimit(23);
        searchSetting.setTimeTopLimit(25);

        searchSetting.validateTimeRange();

        assertTrue("Bottom limit is not valid: " + searchSetting.getTimeBottomLimit(), searchSetting.getTimeBottomLimit() >= 1 && searchSetting.getTimeBottomLimit() <= 24);
        assertTrue("Top limit is not valid: " + searchSetting.getTimeTopLimit(), searchSetting.getTimeTopLimit() >= 1 && searchSetting.getTimeTopLimit() <= 24);
        assertTrue("Range is not valid ", searchSetting.getTimeBottomLimit() < searchSetting.getTimeTopLimit());

        searchSetting.setTimeBottomLimit(-1);
        searchSetting.setTimeTopLimit(-2);

        searchSetting.validateTimeRange();

        assertTrue("Bottom limit is not valid: " + searchSetting.getTimeBottomLimit(), searchSetting.getTimeBottomLimit() >= 1 && searchSetting.getTimeBottomLimit() <= 24);
        assertTrue("Top limit is not valid: " + searchSetting.getTimeTopLimit(), searchSetting.getTimeTopLimit() >= 1 && searchSetting.getTimeTopLimit() <= 24);
        assertTrue("Range is not valid ", searchSetting.getTimeBottomLimit() < searchSetting.getTimeTopLimit());

        searchSetting.setTimeBottomLimit(1);
        searchSetting.setTimeTopLimit(3);

        searchSetting.validateTimeRange();

        assertTrue("Bottom limit is not valid: " + searchSetting.getTimeBottomLimit(), searchSetting.getTimeBottomLimit() >= 1 && searchSetting.getTimeBottomLimit() <= 24);
        assertTrue("Top limit is not valid: " + searchSetting.getTimeTopLimit(), searchSetting.getTimeTopLimit() >= 1 && searchSetting.getTimeTopLimit() <= 24);
        assertTrue("Range is not valid ", searchSetting.getTimeBottomLimit() < searchSetting.getTimeTopLimit());

        searchSetting.setTimeBottomLimit(0);
        searchSetting.setTimeTopLimit(2);

        searchSetting.validateTimeRange();

        assertTrue("Bottom limit is not valid: " + searchSetting.getTimeBottomLimit(), searchSetting.getTimeBottomLimit() >= 1 && searchSetting.getTimeBottomLimit() <= 24);
        assertTrue("Top limit is not valid: " + searchSetting.getTimeTopLimit(), searchSetting.getTimeTopLimit() >= 1 && searchSetting.getTimeTopLimit() <= 24);
        assertTrue("Range is not valid ", searchSetting.getTimeBottomLimit() < searchSetting.getTimeTopLimit());

        searchSetting.setTimeBottomLimit(5);
        searchSetting.setTimeTopLimit(2);

        searchSetting.validateTimeRange();

        assertTrue("Bottom limit is not valid: " + searchSetting.getTimeBottomLimit(), searchSetting.getTimeBottomLimit() >= 1 && searchSetting.getTimeBottomLimit() <= 24);
        assertTrue("Top limit is not valid: " + searchSetting.getTimeTopLimit(), searchSetting.getTimeTopLimit() >= 1 && searchSetting.getTimeTopLimit() <= 24);
        assertTrue("Range is not valid ", searchSetting.getTimeBottomLimit() < searchSetting.getTimeTopLimit());
    }

    public void testGenerateNotificationTimestamp() {
        // Depart Timestamp is 5:00, Notification time must be 10 minutes before

        int expectedHour = 0;
        int expectedMinutes = 1;
        long expectedTimestamp = 1425919860;

        int minutes = 10 * 60;
        long mDepartTimestamp = 60;

        int departHour = Utility.getHourFromTimestamp(mDepartTimestamp);
        int departMinutes = Utility.getMinutesFromTimestamp(mDepartTimestamp);

        assertEquals("Hour is not expected. returns: " + expectedHour, expectedHour, departHour);
        assertEquals("Minutes time is not expected. returns: " + expectedMinutes, expectedMinutes, departMinutes);

        if (departHour == 0) departHour = 24;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, departMinutes);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, departHour);

        long notificationTimestamp = cal.getTimeInMillis() / 1000 - minutes;

        assertEquals("Notification time is not expected. returns: " + notificationTimestamp, expectedTimestamp, notificationTimestamp);
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

}


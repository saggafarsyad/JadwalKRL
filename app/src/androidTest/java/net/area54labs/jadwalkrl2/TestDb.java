package net.area54labs.jadwalkrl2;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import net.area54labs.jadwalkrl2.data.AppDbHelper;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(AppDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new AppDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

//    public void testInsertReadDb() {
//        // Get Database
//        AppDbHelper dbHelper = new AppDbHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        // Add to content values
//        ContentValues routeValues = TestUtilites.createRouteOneValues();
//
//        ContentValues station1Values = TestUtilites.createStationBogorValues();
//
//        ContentValues station2Values = TestUtilites.createStationDepokValues();
//
//        ContentValues routePath1Values = TestUtilites.createRoutePath1Values();
//
//        ContentValues routePath2Values = TestUtilites.createRoutePath2Values();
//
//        ContentValues searchRequestValues = TestUtilites.createQuickSearchValues();
//        // Insert new data, get row id
//        long routeRowId;
//        routeRowId = db.insert(RouteEntry.TABLE_NAME, null, routeValues);
//
//        long station1RowId;
//        station1RowId = db.insert(StationEntry.TABLE_NAME, null, station1Values);
//
//        long station2RowId;
//        station2RowId = db.insert(StationEntry.TABLE_NAME, null, station2Values);
//
//        long routePath1RowId;
//        routePath1RowId = db.insert(RoutePathEntry.TABLE_NAME, null, routePath1Values);
//
//        long routePath2RowId;
//        routePath2RowId = db.insert(RoutePathEntry.TABLE_NAME, null, routePath2Values);
//
//        long searchRequestRowId;
//        searchRequestRowId = db.insert(SearchEntry.TABLE_NAME, null, searchRequestValues);
//
//        ContentValues scheduleValues = TestUtilites.createScheduleValues(searchRequestRowId);
//
//        long scheduleRowId;
//        scheduleRowId = db.insert(ScheduleEntry.TABLE_NAME, null, scheduleValues);
//
//
//        // Check if data successfully inserted
//        assertTrue(routeRowId != -1);
//        Log.d(LOG_TAG, "New Route row id: " + routeRowId);
//
//        assertTrue(station1RowId != -1);
//        Log.d(LOG_TAG, "New Station row id: " + station1RowId);
//
//        assertTrue(station2RowId != -1);
//        Log.d(LOG_TAG, "New Station row id: " + station2RowId);
//
//        assertTrue(routePath1RowId != -1);
//        Log.d(LOG_TAG, "New Route Path row id: " + routePath1RowId);
//
//        assertTrue(routePath2RowId != -1);
//        Log.d(LOG_TAG, "New Route Path row id: " + routePath2RowId);
//
//        assertTrue(scheduleRowId != -1);
//        Log.d(LOG_TAG, "New Schedule row id: " + scheduleRowId);
//
//        assertTrue(searchRequestRowId != -1);
//        Log.d(LOG_TAG, "New Search Request row id: " + searchRequestRowId);
//
//        // Pull data from database
//        // Get all columns
//        String[] routeCols = {
//                RouteEntry._ID,
//                RouteEntry.COLUMN_NAME
//        };
//
//        String[] stationCols = {
//                StationEntry._ID,
//                StationEntry.COLUMN_NAME,
//                StationEntry.COLUMN_LATITUDE,
//                StationEntry.COLUMN_LONGITUDE
//        };
//
//        String[] routePathCols = {
//                RoutePathEntry.COLUMN_ROUTE_KEY,
//                RoutePathEntry.COLUMN_STATION_KEY,
//                RoutePathEntry.COLUMN_NO,
//                RoutePathEntry.COLUMN_STATUS
//        };
//
//        String[] scheduleCols = {
//                ScheduleEntry._ID,
//                ScheduleEntry.COLUMN_ROUTE_KEY,
//                ScheduleEntry.COLUMN_STATION_KEY,
//                ScheduleEntry.COLUMN_DIRECTION,
//                ScheduleEntry.COLUMN_DEPART_TIMESTAMP,
//                ScheduleEntry.COLUMN_UNIT_NO
//        };
//
//        String[] searchRequestCols = {
//                SearchEntry._ID,
//                SearchEntry.COLUMN_SETTING,
//                SearchEntry.COLUMN_TIMESTAMP
//        };
//
//        // Get and test data using cursor
//        // Get data using cursor
//        Cursor cursor;
//
//        // Route
//        cursor = db.query(
//                RouteEntry.TABLE_NAME,
//                routeCols, null, null, null, null, null
//        );
//
//        if (cursor.moveToFirst()) {
//            int idIndex = cursor.getColumnIndex(RouteEntry._ID);
//            int routeId = cursor.getInt(idIndex);
//
//            int nameIndex = cursor.getColumnIndex(RouteEntry.COLUMN_NAME);
//            String routeName = cursor.getString(nameIndex);
//
//            assertEquals(TestUtilites.routeOneId, routeId);
//            assertEquals(TestUtilites.routeOneName, routeName);
//        } else {
//            fail("No values returned");
//        }
//
//        // Station
//        cursor = db.query(
//                StationEntry.TABLE_NAME,
//                stationCols, null, null, null, null, null
//        );
//
//        if (cursor.moveToFirst()) {
//            int idIndex = cursor.getColumnIndex(StationEntry._ID);
//            String stationId = cursor.getString(idIndex);
//
//            int nameIndex = cursor.getColumnIndex(StationEntry.COLUMN_NAME);
//            String stationName = cursor.getString(nameIndex);
//
//            int latIndex = cursor.getColumnIndex(StationEntry.COLUMN_LATITUDE);
//            Double stationLat = cursor.getDouble(latIndex);
//
//            int lngIndex = cursor.getColumnIndex(StationEntry.COLUMN_LONGITUDE);
//            Double stationLng = cursor.getDouble(lngIndex);
//
//            assertEquals(TestUtilites.stationBogorId, stationId);
//            assertEquals(TestUtilites.stationBogorName, stationName);
//            assertEquals(TestUtilites.stationBogorLatitude, stationLat);
//            assertEquals(TestUtilites.stationBogorLongitude, stationLng);
//        } else {
//            fail("No values returned");
//        }
//
//        if (cursor.moveToLast()) {
//            int idIndex = cursor.getColumnIndex(StationEntry._ID);
//            String stationId = cursor.getString(idIndex);
//
//            int nameIndex = cursor.getColumnIndex(StationEntry.COLUMN_NAME);
//            String stationName = cursor.getString(nameIndex);
//
//            int latIndex = cursor.getColumnIndex(StationEntry.COLUMN_LATITUDE);
//            Double stationLat = cursor.getDouble(latIndex);
//
//            int lngIndex = cursor.getColumnIndex(StationEntry.COLUMN_LONGITUDE);
//            Double stationLng = cursor.getDouble(lngIndex);
//
//            assertEquals(TestUtilites.stationDepokId, stationId);
//            assertEquals(TestUtilites.stationDepokName, stationName);
//            assertEquals(TestUtilites.stationDepokLatitude, stationLat);
//            assertEquals(TestUtilites.stationDepokLongitude, stationLng);
//        } else {
//            fail("No values returned");
//        }
//
//        // Route Path
//        cursor = db.query(
//                RoutePathEntry.TABLE_NAME,
//                routePathCols, null, null, null, null, null
//        );
//
//        if (cursor.moveToFirst()) {
//            int routeIndex = cursor.getColumnIndex(RoutePathEntry.COLUMN_ROUTE_KEY);
//            int routeId = cursor.getInt(routeIndex);
//
//            int stationIndex = cursor.getColumnIndex(RoutePathEntry.COLUMN_STATION_KEY);
//            String stationId = cursor.getString(stationIndex);
//
//            int noIndex = cursor.getColumnIndex(RoutePathEntry.COLUMN_NO);
//            int no = cursor.getInt(noIndex);
//
//            int statusIndex = cursor.getColumnIndex(RoutePathEntry.COLUMN_STATUS);
//            int status = cursor.getInt(statusIndex);
//
//            assertEquals(TestUtilites.routePathOneId, routeId);
//            assertEquals(TestUtilites.testRoutePath1Station, stationId);
//            assertEquals(TestUtilites.testRoutePath1No, no);
//            assertEquals(TestUtilites.testRoutePath1Status, status);
//        } else {
//            fail("No values returned");
//        }
//
//        if (cursor.moveToLast()) {
//            int routeIndex = cursor.getColumnIndex(RoutePathEntry.COLUMN_ROUTE_KEY);
//            int routeId = cursor.getInt(routeIndex);
//
//            int stationIndex = cursor.getColumnIndex(RoutePathEntry.COLUMN_STATION_KEY);
//            String stationId = cursor.getString(stationIndex);
//
//            int noIndex = cursor.getColumnIndex(RoutePathEntry.COLUMN_NO);
//            int no = cursor.getInt(noIndex);
//
//            int statusIndex = cursor.getColumnIndex(RoutePathEntry.COLUMN_STATUS);
//            int status = cursor.getInt(statusIndex);
//
//            assertEquals(TestUtilites.routePathOneId, routeId);
//            assertEquals(TestUtilites.testRoutePath2Station, stationId);
//            assertEquals(TestUtilites.testRoutePath2No, no);
//            assertEquals(TestUtilites.testRoutePath2Status, status);
//        } else {
//            fail("No values returned");
//        }
//
//        // Request Search
//        cursor = db.query(
//                SearchEntry.TABLE_NAME,
//                searchRequestCols, null, null, null, null, null
//        );
//
//        if (cursor.moveToFirst()) {
//            int paramIndex = cursor.getColumnIndex(SearchEntry.COLUMN_SETTING);
//            String param = cursor.getString(paramIndex);
//
//            int timestampIndex = cursor.getColumnIndex(SearchEntry.COLUMN_TIMESTAMP);
//            long timestamp = cursor.getLong(timestampIndex);
//
//            assertEquals(TestUtilites.searchSetting1, param);
//            assertEquals(TestUtilites.searchTimestamp, timestamp);
//        } else {
//            fail("No values returned");
//        }
//
//        // Schedule
//        cursor = db.query(
//                ScheduleEntry.TABLE_NAME,
//                scheduleCols, null, null, null, null, null
//        );
//
//        if (cursor.moveToFirst()) {
//            int routeIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_ROUTE_KEY);
//            int routeId = cursor.getInt(routeIndex);
//
//            int stationIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_STATION_KEY);
//            String stationId = cursor.getString(stationIndex);
//
//            int directionIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_DIRECTION);
//            int direction = cursor.getInt(directionIndex);
//
//            int departTimestampIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_DEPART_TIMESTAMP);
//            int departTimestamp = cursor.getInt(departTimestampIndex);
//
//            int unitNoIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_UNIT_NO);
//            String unitNo = cursor.getString(unitNoIndex);
//
//            assertEquals(TestUtilites.testScheduleRoute, routeId);
//            assertEquals(TestUtilites.testScheduleStation, stationId);
//            assertEquals(TestUtilites.testScheduleDirection, direction);
//            assertEquals(TestUtilites.testScheduleDepartTimestamp, departTimestamp);
//            assertEquals(TestUtilites.testScheduleUnitNo, unitNo);
//        } else {
//            fail("No values returned");
//        }
//
//
//        cursor.close();
//        dbHelper.close();
//    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
}

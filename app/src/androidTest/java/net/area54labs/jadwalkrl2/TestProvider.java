package net.area54labs.jadwalkrl2;

import android.database.Cursor;
import android.test.AndroidTestCase;

import static net.area54labs.jadwalkrl2.data.AppContract.RouteEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.ScheduleEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.SearchEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.StationEntry;

public class TestProvider extends AndroidTestCase {
    // @TODO: 1. testGetType
    public void testGetType() {

        int testRouteId = 1;
        String testStationBogorId = "BOO";
        String testStationDepokId = "DP";
        int testScheduleId = 2;
        long testSearchId = 1;
        String testSearchSettings = "DP-1-24";

        assertEquals("Error: the RouteEntry.CONTENT_URI should return RouteEntry.CONTENT_DIR_TYPE",
                RouteEntry.CONTENT_DIR_TYPE,
                mContext.getContentResolver().getType(RouteEntry.CONTENT_URI)
        );
        assertEquals("Error: the RouteEntry.buildUri should return RouteEntry.CONTENT_ITEM_TYPE",
                RouteEntry.CONTENT_ITEM_TYPE,
                mContext.getContentResolver().getType(RouteEntry.buildUri(testRouteId))
        );
        assertEquals("Error: the RouteEntry.buildPathUri should return RouteEntry.CONTENT_DIR_TYPE",
                RouteEntry.CONTENT_DIR_TYPE,
                mContext.getContentResolver().getType(RouteEntry.buildPathUri(testRouteId))
        );
        assertEquals("Error: the RouteEntry.buildPathPrevStationUri should return RouteEntry.CONTENT_ITEM_TYPE",
                RouteEntry.CONTENT_ITEM_TYPE,
                mContext.getContentResolver().getType(RouteEntry.buildPathPrevStationUri(testRouteId, testStationBogorId))
        );
        assertEquals("Error: the RouteEntry.buildPathNextStationUri should return RouteEntry.CONTENT_ITEM_TYPE",
                RouteEntry.CONTENT_ITEM_TYPE,
                mContext.getContentResolver().getType(RouteEntry.buildPathNextStationUri(testRouteId, testStationDepokId))
        );

        assertEquals("Error: the StationEntry.CONTENT_URI should return StationEntry.CONTENT_DIR_TYPE",
                StationEntry.CONTENT_DIR_TYPE,
                mContext.getContentResolver().getType(StationEntry.CONTENT_URI)
        );
        assertEquals("Error: the StationEntry.buildUri should return StationEntry.CONTENT_ITEM_TYPE",
                StationEntry.CONTENT_ITEM_TYPE,
                mContext.getContentResolver().getType(StationEntry.buildUri(testStationBogorId))
        );

        assertEquals("Error: the SearchEntry.CONTENT_URI should return SearchEntry.CONTENT_DIR_TYPE",
                SearchEntry.CONTENT_DIR_TYPE,
                mContext.getContentResolver().getType(SearchEntry.CONTENT_URI)
        );
        assertEquals("Error: the SearchEntry.buildUri should return SearchEntry.CONTENT_ITEM_TYPE",
                SearchEntry.CONTENT_ITEM_TYPE,
                mContext.getContentResolver().getType(SearchEntry.buildUri(testSearchId))
        );

        assertEquals("Error: the ScheduleEntry.CONTENT_URI should return ScheduleEntry.CONTENT_DIR_TYPE",
                ScheduleEntry.CONTENT_DIR_TYPE,
                mContext.getContentResolver().getType(ScheduleEntry.CONTENT_URI)
        );
        assertEquals("Error: the ScheduleEntry.buildUri should return ScheduleEntry.CONTENT_ITEM_TYPE",
                ScheduleEntry.CONTENT_ITEM_TYPE,
                mContext.getContentResolver().getType(ScheduleEntry.buildUri(testScheduleId))
        );
        assertEquals("Error: the ScheduleEntry CONTENT_URI should return ScheduleEntry.CONTENT_ITEM_TYPE",
                ScheduleEntry.CONTENT_DIR_TYPE,
                mContext.getContentResolver().getType(ScheduleEntry.buildSearchUri(testSearchSettings))
        );

    }

    public void testInitData() {
        Cursor testCursor = mContext.getContentResolver().query(StationEntry.CONTENT_URI, null, null, null, null);

        int count = testCursor.getCount();
        assertTrue("Station count is not expected: " + count, 71 == count);


        testCursor = mContext.getContentResolver().query(RouteEntry.CONTENT_URI, null, null, null, null);

        count = testCursor.getCount();
        assertTrue("Station count is not expected: " + count, 65 == count);

        testCursor = mContext.getContentResolver().query(RouteEntry.buildPathDirUri(), null, null, null, null);

        count = testCursor.getCount();
        assertTrue("Station count is not expected: " + count, count > 600);
    }

//    public void testProviderQuery() {
//        // Route
//        Cursor testCursor = mContext.getContentResolver().query(
//                RouteEntry.CONTENT_URI,
//                null,
//                null, null, null
//        );
//
//        assertTrue("Empty Route cursor returned", testCursor.moveToFirst());
//        TestUtilites.validateCurrentRecord("testProviderQuery", testCursor, TestUtilites.createRouteOneValues());
//
//        // Station
//        testCursor = mContext.getContentResolver().query(
//                StationEntry.CONTENT_URI,
//                null,
//                null, null, null
//        );
//
//        assertTrue("Empty Station cursor returned", testCursor.moveToFirst());
//        TestUtilites.validateCurrentRecord("testProviderQuery", testCursor, TestUtilites.createStationBogorValues());
//
//        Log.d("TestProvider", "Station Cursor count " + testCursor.getCount());
//
//        assertTrue("Empty Station cursor returned", testCursor.moveToNext());
//        TestUtilites.validateCurrentRecord("testProviderQuery", testCursor, TestUtilites.createStationDepokValues());
//
//        // Route Path
//        testCursor = mContext.getContentResolver().query(
//                RoutePathEntry.buildUri(TestUtilites.routeOneId),
//                null,
//                null, null, null
//        );
//
//        assertTrue("Empty Route Path cursor returned", testCursor.moveToFirst());
//
//        // Search Settings
//        testCursor = mContext.getContentResolver().query(
//                ScheduleEntry.buildScheduleSearchSetting(TestUtilites.searchSetting1),
//                null,
//                null, null, null
//        );
//
//        assertTrue("Empty Search cursor returned", testCursor.moveToFirst());
//
//        testCursor.close();
//    }
//
//    public void testProviderNextStation() {
//        Cursor routePathCursor = mContext.getContentResolver().query(
//            RoutePathEntry.buildRoutePathNextStationUri(TestUtilites.stationBogorId,TestUtilites.routeOneId),
//                null,
//                null,
//                null,
//                null
//        );
//
//        assertTrue("Empty Route Path returned", routePathCursor.moveToFirst());
//
//        String stationId = routePathCursor.getString(routePathCursor.getColumnIndex(StationEntry._ID));
//        String stationName = routePathCursor.getString(routePathCursor.getColumnIndex(StationEntry.COLUMN_NAME));
//
//        assertEquals("Wrong Station ID", TestUtilites.stationDepokId, stationId);
//        assertEquals("Wrong Station Name", TestUtilites.stationDepokName, stationName);
//    }
//
//    public void testProviderPrevStation() {
//        Cursor routePathCursor = mContext.getContentResolver().query(
//                RoutePathEntry.buildRoutePathPrevStationUri(TestUtilites.stationDepokId,TestUtilites.routeOneId),
//                null,
//                null,
//                null,
//                null
//        );
//
//        assertTrue("Empty Route Path returned", routePathCursor.moveToFirst());
//
//        String stationId = routePathCursor.getString(routePathCursor.getColumnIndex(StationEntry._ID));
//        String stationName = routePathCursor.getString(routePathCursor.getColumnIndex(StationEntry.COLUMN_NAME));
//
//        assertEquals("Wrong Station ID", TestUtilites.stationBogorId, stationId);
//        assertEquals("Wrong Station Name", TestUtilites.stationBogorName, stationName);
//
//        routePathCursor.close();
//    }
//
//    public void testProviderRoutePathNotFound() {
//        Cursor routePathCursor = mContext.getContentResolver().query(
//                RoutePathEntry.buildRoutePathPrevStationUri(TestUtilites.stationBogorId,TestUtilites.routeOneId),
//                null,
//                null,
//                null,
//                null
//        );
//
//        assertFalse("Expected to be null",routePathCursor.moveToFirst());
//
//        routePathCursor = mContext.getContentResolver().query(
//                RoutePathEntry.buildRoutePathNextStationUri("DPB",TestUtilites.routeOneId),
//                null,
//                null,
//                null,
//                null
//        );
//
//        assertFalse("Expected to be null",routePathCursor.moveToFirst());
//
//        routePathCursor.close();
//    }
//
//    public void testRouteInsertReadProvider() {
//        ContentValues testValues = TestUtilites.createRouteTwoValues();
//
//        TestUtilites.TestContentObserver tco = TestUtilites.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(RouteEntry.CONTENT_URI, true, tco);
//        Uri routeUri = mContext.getContentResolver().insert(RouteEntry.CONTENT_URI, testValues);
//
//        tco.waitForNotificationOrFail();
//        mContext.getContentResolver().unregisterContentObserver(tco);
//
//        long routeRowId = ContentUris.parseId(routeUri);
//
//        assertTrue(routeRowId != -1);
//
//        // Pull data and validate
//        Cursor cursor = mContext.getContentResolver().query(
//                routeUri,
//                null,
//                null,
//                null,
//                null
//        );
//
//        assertTrue("Returned empty cursor: " + routeUri, cursor.moveToFirst());
//        TestUtilites.validateCurrentRecord("Error validating entry", cursor, testValues);
//    }
//
//    public void testScheduleInsertReadProvider() {
//        ContentValues testValues = TestUtilites.createAdvancedSearchValues();
//
//        TestUtilites.TestContentObserver tco = TestUtilites.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(SearchEntry.CONTENT_URI, true, tco);
//        Uri uri = mContext.getContentResolver().insert(SearchEntry.CONTENT_URI, testValues);
//
//        tco.waitForNotificationOrFail();
//        mContext.getContentResolver().unregisterContentObserver(tco);
//
//        long rowId = ContentUris.parseId(uri);
//
//        assertTrue(rowId != -1);
//
//        // Pull data and validate
//        Cursor cursor = mContext.getContentResolver().query(
//                uri,
//                null,
//                null,
//                null,
//                null
//        );
//
//        assertTrue("Returned empty cursor: " + uri, cursor.moveToFirst());
//        TestUtilites.validateCurrentRecord("Error validating entry", cursor, testValues);
//
//
//    }
}

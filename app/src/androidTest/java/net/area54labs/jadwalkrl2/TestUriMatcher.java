package net.area54labs.jadwalkrl2;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import net.area54labs.jadwalkrl2.data.AppProvider;

import static net.area54labs.jadwalkrl2.data.AppContract.RouteEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.ScheduleEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.SearchEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.StationEntry;

/**
 * Created by Saggaf on 3/1/2015.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final int testRouteId = 1;
    private static final Uri TEST_ROUTE_ID = RouteEntry.buildUri(testRouteId);
    private static final Uri TEST_ROUTE_PATH = RouteEntry.buildPathUri(testRouteId);
    private static final String testSearchSettings = "DP-1-24";
    private static final Uri TEST_SCHEDULE_BY_SEARCH = ScheduleEntry.buildSearchUri(testSearchSettings);
    private static final int testSearchId = 3;
    private static final Uri TEST_SEARCH_ID = SearchEntry.buildUri(testSearchId);
    private static final int testScheduleId = 2;
    private static final Uri TEST_SCHEDULE_ID = ScheduleEntry.buildUri(testScheduleId);
    private static final String testStationId = "BOO";
    private static final Uri TEST_STATION_ID = StationEntry.buildUri(testStationId);
    private static final Uri TEST_ROUTE = RouteEntry.CONTENT_URI;
    private static final Uri TEST_STATION = StationEntry.CONTENT_URI;
    private static final Uri TEST_SEARCH = SearchEntry.CONTENT_URI;
    private static final Uri TEST_SCHEDULE = ScheduleEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = AppProvider.buildUriMatcher();

        assertEquals("Error: the URI_ROUTE URI was matched incorrectly.",
                testMatcher.match(TEST_ROUTE), AppProvider.URI_ROUTE);
        assertEquals("Error: the URI_ROUTE_ID URI was matched incorrectly.",
                testMatcher.match(TEST_ROUTE_ID), AppProvider.URI_ROUTE_ID);
        assertEquals("Error: the URI_ROUTE_PATH_ID URI was matched incorrectly.",
                testMatcher.match(TEST_ROUTE_PATH), AppProvider.URI_ROUTE_PATH_ID);


        assertEquals("Error: the URI_STATION URI was matched incorrectly.",
                testMatcher.match(TEST_STATION), AppProvider.URI_STATION);
        assertEquals("Error: the URI_STATION_ID URI was matched incorrectly.",
                testMatcher.match(TEST_STATION_ID), AppProvider.URI_STATION_ID);

        assertEquals("Error: the URI_SEARCH URI was matched incorrectly.",
                testMatcher.match(TEST_SEARCH), AppProvider.URI_SEARCH);
        assertEquals("Error: the URI_SEARCH_ID URI was matched incorrectly.",
                testMatcher.match(TEST_SEARCH_ID), AppProvider.URI_SEARCH_ID);

        assertEquals("Error: the URI_SCHEDULE URI was matched incorrectly.",
                testMatcher.match(TEST_SCHEDULE), AppProvider.URI_SCHEDULE);
        assertEquals("Error: the URI_SCHEDULE_ID URI was matched incorrectly.",
                testMatcher.match(TEST_SCHEDULE_ID), AppProvider.URI_SCHEDULE_ID);
        assertEquals("Error: the URI_SCHEDULE_BY_SEARCH_SETTING URI was matched incorrectly.",
                testMatcher.match(TEST_SCHEDULE_BY_SEARCH), AppProvider.URI_SCHEDULE_BY_SEARCH_SETTING);
    }

}

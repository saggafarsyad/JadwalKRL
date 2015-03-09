package net.area54labs.jadwalkrl2;

import android.net.Uri;
import android.test.AndroidTestCase;

import static net.area54labs.jadwalkrl2.data.AppContract.ScheduleEntry;

/**
 * Created by Saggaf on 3/1/2015.
 */
public class TestContract extends AndroidTestCase {
    private static final String TEST_SEARCH_SETTING = "DP";

    public void testBuildScheduleSearch() {
        Uri scheduleUri = ScheduleEntry.buildSearchUri(TEST_SEARCH_SETTING);

        assertNotNull("Error: Null Uri returned. You must fill-in buildScheduleSearchSetting in AppContract",
                scheduleUri);
        assertEquals("Error: Search location not properly appended to the end of the Uri",
                TEST_SEARCH_SETTING, ScheduleEntry.parseSearchSettings(scheduleUri));
        assertEquals("Error: Search location Uri doesn't match our expected result",
                scheduleUri.toString(),
                "content://net.area54labs.jadwalkrl2/schedule/search/DP");
    }
}

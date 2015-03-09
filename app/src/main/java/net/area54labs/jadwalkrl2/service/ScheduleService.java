package net.area54labs.jadwalkrl2.service;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import net.area54labs.jadwalkrl2.utils.SearchSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Vector;

import static net.area54labs.jadwalkrl2.data.AppContract.ScheduleEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.SearchEntry;

/**
 * Created by Saggaf on 3/7/2015.
 */
public class ScheduleService extends IntentService {
    public static final String SEARCH_SETTING_EXTRA = "search_setting";
    private final static String API_BASE_URL = "http://jadwalkrl.com/api/v6/test/schedule/";
    private static final String SERVICE_NAME = "JadwalKRLService";
    private String LOG_TAG = this.getClass().getSimpleName();

    public ScheduleService() {
        super(SERVICE_NAME);
    }

    private long getTodayTimestamp() {
        //Today timestamp
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR, 1);

        return cal.getTimeInMillis() / 1000;
    }

    private boolean isSearchAvailable(String searchSetting) {
        long todayTimestamp = getTodayTimestamp();
        Cursor cursor = getContentResolver().query(
                SearchEntry.CONTENT_URI,
                new String[]{
                        SearchEntry._ID
                },
                SearchEntry.COLUMN_SETTING + " = ? AND " +
                        SearchEntry.COLUMN_TIMESTAMP + " >= ? ",
                new String[]{
                        searchSetting, String.valueOf(todayTimestamp)
                },
                null);

        if (cursor.getCount() > 0) return true;
        return false;
    }

    private long addSearch(long searchId, String searchSetting, long searchTimestamp) {
        long todayTimestamp = getTodayTimestamp();
        Cursor cursor = getContentResolver().query(
                SearchEntry.CONTENT_URI,
                new String[]{
                        SearchEntry._ID
                },
                SearchEntry.COLUMN_SETTING + " = ? AND " +
                        SearchEntry.COLUMN_TIMESTAMP + " < ? ",
                new String[]{
                        searchSetting, String.valueOf(todayTimestamp)
                },
                null);
        if (cursor.moveToFirst()) {
            Log.v(LOG_TAG, "Params found in the database!, returning");
            return -1;
        } else {
            Log.v(LOG_TAG, "Didn't find it in the database, Insert");
            ContentValues searchRequestValues = new ContentValues();
            searchRequestValues.put(SearchEntry._ID, searchId);
            searchRequestValues.put(SearchEntry.COLUMN_SETTING, searchSetting);
            searchRequestValues.put(SearchEntry.COLUMN_TIMESTAMP, searchTimestamp);

            Uri searchInsertUri = getContentResolver().insert(SearchEntry.CONTENT_URI, searchRequestValues);

            return ContentUris.parseId(searchInsertUri);
        }
    }

    private long deleteOldSearch(String strSearchSetting) {
        // Get search with the same
        long strDate = getTodayTimestamp();
        Cursor searchCursor = getContentResolver().query(
                SearchEntry.CONTENT_URI,
                null,
                SearchEntry.COLUMN_SETTING + " = ? AND " +
                        SearchEntry.COLUMN_TIMESTAMP + " < ? ",
                new String[]{
                        strSearchSetting, String.valueOf(strDate)
                },
                null);


        if (searchCursor.getCount() > 0) {
            while (searchCursor.moveToNext()) {
                long searchId = searchCursor.getInt(searchCursor.getColumnIndex(SearchEntry._ID));
                long searchTimestamp = searchCursor.getLong(searchCursor.getColumnIndex(SearchEntry.COLUMN_TIMESTAMP));
                getContentResolver().delete(
                        ScheduleEntry.CONTENT_URI,
                        ScheduleEntry.COLUMN_SEARCH_KEY + " = ? ",
                        new String[]{
                                String.valueOf(searchId)
                        }
                );

                long result = getContentResolver().delete(
                        SearchEntry.CONTENT_URI,
                        SearchEntry._ID + " = ? ",
                        new String[]{
                                String.valueOf(searchId)
                        }
                );

                return result;
            }
        }

        return -1;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String strSearchSetting = intent.getStringExtra(SEARCH_SETTING_EXTRA);

        // If search available, use in cache search
        if (isSearchAvailable(strSearchSetting)) {
            Log.v(LOG_TAG, "Search cache available!, returning");
            getContentResolver().notifyChange(ScheduleEntry.CONTENT_URI, null);
            return;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String scheduleJsonStr = null;
        Uri builtUri;
        // Parse search settings

        SearchSetting searchSetting = new SearchSetting(getApplicationContext(), strSearchSetting);

        if (searchSetting.isAdvanced()) {
            builtUri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendPath(searchSetting.getStationDepartFrom())
                    .appendPath(searchSetting.getStationDepartFor())
                    .appendPath(String.valueOf(searchSetting.getTimeBottomLimit()))
                    .appendPath(String.valueOf(searchSetting.getTimeTopLimit()))
                    .build();
        } else {
            builtUri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendPath(searchSetting.getStationDepartFrom())
                    .build();
        }

        try {
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                scheduleJsonStr = null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                scheduleJsonStr = null;
            }

            scheduleJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error, e");
            scheduleJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            Log.v(LOG_TAG, "Parsing JSON");
            getScheduleFromJson(scheduleJsonStr);
        } catch (JSONException e) {
            getContentResolver().notifyChange(ScheduleEntry.CONTENT_URI, null);
            Log.v(LOG_TAG, "Failed Parsing JSON");
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void getScheduleFromJson(String scheduleJsonStr) throws JSONException {
        // JSON key
        final String OWM_ID = "id";
        final String OWM_DEPART_FROM = "depart_from";
        final String OWM_DEPART_TO = "depart_for";
        final String OWM_TIME_LOWER_LIMIT = "t1";
        final String OWM_TIME_UPPER_LIMIT = "t2";
        final String OWN_SEARCH_PAGE = "page";
        final String OWM_SEARCH_TIMESTAMP = "timestamp";

        final String OWM_ROUTE = "route";
        final String OWN_SCHEDULES_COUNT = "count";
        final String OWM_SCHEDULES = "schedules";
        final String OWM_UNIT_NO = "unit_no";
        final String OWM_DEPART_TIMESTAMP = "depart_time";

        JSONObject searchJSON = new JSONObject(scheduleJsonStr);
        JSONArray scheduleArray = searchJSON.getJSONArray(OWM_SCHEDULES);

        int lowerTimeLimit = searchJSON.getInt(OWM_TIME_LOWER_LIMIT);
        int upperTimeLimit = searchJSON.getInt(OWM_TIME_UPPER_LIMIT);
        String departFromId = searchJSON.getString(OWM_DEPART_FROM);
        String departForId = searchJSON.getString(OWM_DEPART_TO);
        long searchId = searchJSON.getLong(OWM_ID);
        long searchTimestamp = searchJSON.getLong(OWM_SEARCH_TIMESTAMP);

        // Generate search settings
        boolean isAdvanced;
        if (departForId.equals("-")) {
            isAdvanced = false;
            departForId = "BOO";
        } else {
            isAdvanced = true;
        }

        SearchSetting searchSetting = new SearchSetting(getApplicationContext(), isAdvanced, departFromId, departForId, lowerTimeLimit, upperTimeLimit);

        long resultSearchId = addSearch(searchId, searchSetting.toString(), searchTimestamp);
        if (resultSearchId == -1) {
            getContentResolver().notifyChange(ScheduleEntry.CONTENT_URI, null);
            return;
        }

        searchSetting.save();
        int schedulesCount = searchJSON.getInt(OWN_SCHEDULES_COUNT);

        Vector<ContentValues> contentValuesVector = new Vector<>(schedulesCount);

        for (int i = 0; i < schedulesCount; i++) {
            JSONObject schedule = scheduleArray.getJSONObject(i);
            JSONObject route = schedule.getJSONObject(OWM_ROUTE);

            int routeId = route.getInt(OWM_ID);
            String unitNo = schedule.getString(OWM_UNIT_NO);
            long departTimestamp = schedule.getLong(OWM_DEPART_TIMESTAMP);
            int direction = 1;

            ContentValues scheduleValues = new ContentValues();
            scheduleValues.put(ScheduleEntry.COLUMN_SEARCH_KEY, searchId);
            scheduleValues.put(ScheduleEntry.COLUMN_ROUTE_KEY, routeId);
            scheduleValues.put(ScheduleEntry.COLUMN_STATION_KEY, departFromId);
            scheduleValues.put(ScheduleEntry.COLUMN_DIRECTION, direction);
            scheduleValues.put(ScheduleEntry.COLUMN_DEPART_TIMESTAMP, departTimestamp);
            scheduleValues.put(ScheduleEntry.COLUMN_UNIT_NO, unitNo);

            contentValuesVector.add(scheduleValues);
        }

        if (contentValuesVector.size() > 0) {
            ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
            contentValuesVector.toArray(contentValuesArray);
            getContentResolver().bulkInsert(ScheduleEntry.CONTENT_URI, contentValuesArray);

            Log.d(LOG_TAG, "ScheduleService bulk insert complete. " + contentValuesVector.size() + " Inserted");

            deleteOldSearch(searchSetting.toString());
        }
    }
}

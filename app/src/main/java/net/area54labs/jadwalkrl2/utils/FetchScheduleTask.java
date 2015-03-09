package net.area54labs.jadwalkrl2.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import net.area54labs.jadwalkrl2.data.AppContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Saggaf on 3/7/2015.
 */
public class FetchScheduleTask extends AsyncTask<String, Void, Void> {
    private static final String LOG_TAG = "FetchScheduleTask";
    private final static String API_BASE_URL = "http://jadwalkrl.com/api/v6/test/schedule/";
    private Context mContext;


    public FetchScheduleTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String strSearchSetting = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String scheduleJsonStr = null;
        Uri builtUri;
        // Parse search settings

        SearchSetting searchSetting = new SearchSetting(mContext, strSearchSetting);

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
            // Parse json
            getScheduleFromJson(scheduleJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private long addSearch(long searchId, String searchSetting, long searchTimestamp) {
        Cursor cursor = mContext.getContentResolver().query(
                AppContract.SearchEntry.CONTENT_URI,
                new String[]{
                        AppContract.SearchEntry._ID
                },
                AppContract.SearchEntry.COLUMN_SETTING + " = ?",
                new String[]{
                        searchSetting
                },
                null);
        if (cursor.moveToFirst()) {
            Log.v(LOG_TAG, "Params found in the database!");
            return -1;
        } else {
            Log.v(LOG_TAG, "Didn't find it in the database, Insert");
            ContentValues searchRequestValues = new ContentValues();
            searchRequestValues.put(AppContract.SearchEntry._ID, searchId);
            searchRequestValues.put(AppContract.SearchEntry.COLUMN_SETTING, searchSetting);
            searchRequestValues.put(AppContract.SearchEntry.COLUMN_TIMESTAMP, searchTimestamp);

            Uri searchInsertUri = mContext.getContentResolver().insert(AppContract.SearchEntry.CONTENT_URI, searchRequestValues);

            return ContentUris.parseId(searchInsertUri);
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

        SearchSetting searchSetting = new SearchSetting(mContext, isAdvanced, departFromId, departForId, lowerTimeLimit, upperTimeLimit);

        long resultSearchId = addSearch(searchId, searchSetting.toString(), searchTimestamp);

        if (resultSearchId == -1) return;

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
            scheduleValues.put(AppContract.ScheduleEntry.COLUMN_SEARCH_KEY, searchId);
            scheduleValues.put(AppContract.ScheduleEntry.COLUMN_ROUTE_KEY, routeId);
            scheduleValues.put(AppContract.ScheduleEntry.COLUMN_STATION_KEY, departFromId);
            scheduleValues.put(AppContract.ScheduleEntry.COLUMN_DIRECTION, direction);
            scheduleValues.put(AppContract.ScheduleEntry.COLUMN_DEPART_TIMESTAMP, departTimestamp);
            scheduleValues.put(AppContract.ScheduleEntry.COLUMN_UNIT_NO, unitNo);

            contentValuesVector.add(scheduleValues);
        }

        if (contentValuesVector.size() > 0) {
            ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
            contentValuesVector.toArray(contentValuesArray);
            mContext.getContentResolver().bulkInsert(AppContract.ScheduleEntry.CONTENT_URI, contentValuesArray);
        }

        Log.d(LOG_TAG, "FetchScheduleTask Complete. " + contentValuesVector.size() + " Inserted");
//            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.DATE, 0);
//
//            String todayTimestamp = cal.toString();
//
//            // Yesterday search id
//            long[] searcshId;

//            getContentResolver().query(
//                    SearchEntry.CONTENT_URI,
//                    SearchEntry.COLUMN_SETTING
//            )
//
//            getContentResolver().delete(
//                    ScheduleEntry.CONTENT_URI,
//                    ScheduleEntry.COLUMN_DEPART_TIMESTAMP + " <= ?",
//                    new String[]{
//                            String.valueOf(yesterdayTimestamp)
//                    }
//            );

    }
}

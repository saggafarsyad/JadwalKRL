package net.area54labs.jadwalkrl2.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static net.area54labs.jadwalkrl2.data.AppContract.RouteEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.RoutePathEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.ScheduleEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.SearchEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.StationEntry;

public class AppDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "jadwalkrl.db";

    private final String LOG_TAG = getClass().getSimpleName();
    private Context mContext;

    public AppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ROUTE_TABLE = "CREATE TABLE " + RouteEntry.TABLE_NAME + " (" +
                RouteEntry._ID + " INTEGER PRIMARY KEY NOT NULL," +
                RouteEntry.COLUMN_NAME + " CHAR(70) NOT NULL);";

        final String SQL_CREATE_STATION_TABLE = "CREATE TABLE " + StationEntry.TABLE_NAME + " (" +
                StationEntry._ID + " CHAR(4) PRIMARY KEY NOT NULL," +
                StationEntry.COLUMN_NAME + " CHAR(60) NOT NULL," +
                StationEntry.COLUMN_LATITUDE + " REAL," +
                StationEntry.COLUMN_LONGITUDE + " REAL);";

        final String SQL_CREATE_ROUTE_PATH_TABLE = "CREATE TABLE " + RoutePathEntry.TABLE_NAME + " (" +
                RoutePathEntry.COLUMN_ROUTE_KEY + " INTEGER NOT NULL," +
                RoutePathEntry.COLUMN_STATION_KEY + " CHAR(4) NOT NULL," +
                RoutePathEntry.COLUMN_NO + " INTEGER NOT NULL," +
                RoutePathEntry.COLUMN_STATUS + " INTEGER NULL," +
                "PRIMARY KEY (" +
                RoutePathEntry.COLUMN_ROUTE_KEY + ", " +
                RoutePathEntry.COLUMN_STATION_KEY + ", " +
                RoutePathEntry.COLUMN_NO + ")," +
                "FOREIGN KEY (" +
                RoutePathEntry.COLUMN_ROUTE_KEY + ") " +
                "REFERENCES " +
                RouteEntry.TABLE_NAME + "(" +
                RouteEntry._ID + ")," +
                "FOREIGN KEY (" +
                RoutePathEntry.COLUMN_STATION_KEY + ") " +
                "REFERENCES " +
                StationEntry.TABLE_NAME + "(" +
                StationEntry._ID + "));";

        final String SQL_CREATE_SCHEDULE_TABLE = "CREATE TABLE " + ScheduleEntry.TABLE_NAME + " (" +
                ScheduleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ScheduleEntry.COLUMN_SEARCH_KEY + " INTEGER NOT NULL, " +
                ScheduleEntry.COLUMN_ROUTE_KEY + " INTEGER NOT NULL," +
                ScheduleEntry.COLUMN_STATION_KEY + " CHAR(4) NOT NULL," +
                ScheduleEntry.COLUMN_DIRECTION + " INTEGER NOT NULL," +
                ScheduleEntry.COLUMN_DEPART_TIMESTAMP + " INTEGER NOT NULL," +
                ScheduleEntry.COLUMN_UNIT_NO + " CHAR(6) NULL," +
                "FOREIGN KEY (" +
                ScheduleEntry.COLUMN_SEARCH_KEY + ") " +
                "REFERENCES " +
                SearchEntry.TABLE_NAME + "(" +
                SearchEntry._ID + ")," +
                "FOREIGN KEY (" +
                ScheduleEntry.COLUMN_ROUTE_KEY + ") " +
                "REFERENCES " +
                RouteEntry.TABLE_NAME + "(" +
                RouteEntry._ID + ")," +
                "FOREIGN KEY (" +
                ScheduleEntry.COLUMN_STATION_KEY + ") " +
                "REFERENCES " +
                StationEntry.TABLE_NAME + "(" +
                StationEntry._ID + "));";

        final String SQL_CREATE_SEARCH_REQUEST_TABLE = "CREATE TABLE " + SearchEntry.TABLE_NAME + " (" +
                SearchEntry._ID + " INTEGER PRIMARY KEY NOT NULL," +
                SearchEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL," +
                SearchEntry.COLUMN_SETTING + " CHAR(30) NOT NULL);";

        db.execSQL(SQL_CREATE_ROUTE_TABLE);
        db.execSQL(SQL_CREATE_STATION_TABLE);
        db.execSQL(SQL_CREATE_ROUTE_PATH_TABLE);
        db.execSQL(SQL_CREATE_SEARCH_REQUEST_TABLE);
        db.execSQL(SQL_CREATE_SCHEDULE_TABLE);

        db.beginTransaction();
        try {
            ContentValues[] insertValues = generateInsertStation();

            for (ContentValues value : insertValues) {
                db.insert(StationEntry.TABLE_NAME, null, value);
            }

            insertValues = generateInsertRoute();

            for (ContentValues value : insertValues) {
                db.insert(RouteEntry.TABLE_NAME, null, value);
            }

            insertValues = generateInsertRoutePath();

            for (ContentValues value : insertValues) {
                db.insert(RoutePathEntry.TABLE_NAME, null, value);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SearchEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RoutePathEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RouteEntry.TABLE_NAME);
        onCreate(db);
    }

    private String readInitFiles(String fileName) {
        AssetManager assetManager = mContext.getAssets();

        InputStream input;

        try {
            input = assetManager.open("init_" + fileName + ".json");

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            return new String(buffer);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to read assets: " + fileName, e);
        }

        return null;
    }


    private ContentValues[] generateInsertStation() {
        // Read JSON
        String jsonString = readInitFiles("station");

        final String OWM_COUNT = "count";
        final String OWM_STATION_ARRAY = "stations";

        final String OWM_ID = "id";
        final String OWM_NAME = "name";
        final String OWM_LATITUDE = "lat";
        final String OWM_LONGITUDE = "lng";

        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray stations = json.getJSONArray(OWM_STATION_ARRAY);
            int count = json.getInt(OWM_COUNT);

            ContentValues[] values = new ContentValues[count];

            for (int i = 0; i < count; i++) {
                JSONObject station = stations.getJSONObject(i);

                ContentValues value = new ContentValues();
                value.put(StationEntry._ID, station.getString(OWM_ID));
                value.put(StationEntry.COLUMN_NAME, station.getString(OWM_NAME));
                value.put(StationEntry.COLUMN_LATITUDE, station.getDouble(OWM_LATITUDE));
                value.put(StationEntry.COLUMN_LONGITUDE, station.getDouble(OWM_LONGITUDE));

                values[i] = value;
            }

            return values;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Failed to generate station insert query", e);
        }

        return null;
    }

    private ContentValues[] generateInsertRoute() {
        // Read JSON
        String jsonString = readInitFiles("route");

        final String OWM_COUNT = "count";
        final String OWM_ROUTE_ARRAY = "routes";

        final String OWM_ID = "id";
        final String OWM_NAME = "name";

        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray routes = json.getJSONArray(OWM_ROUTE_ARRAY);
            int count = json.getInt(OWM_COUNT);

            ContentValues[] values = new ContentValues[count];

            for (int i = 0; i < count; i++) {
                JSONObject route = routes.getJSONObject(i);

                ContentValues routeValues = new ContentValues();
                routeValues.put(RouteEntry._ID, route.getInt(OWM_ID));
                routeValues.put(RouteEntry.COLUMN_NAME, route.getString(OWM_NAME));

                values[i] = routeValues;
            }

            return values;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Failed to generate route insert query", e);
        }

        return null;
    }

    private ContentValues[] generateInsertRoutePath() {
        // Read JSON
        String jsonString = readInitFiles("route_path");

        final String OWM_COUNT = "count";
        final String OWM_ROUTE_ARRAY = "routes";
        final String OWM_ROUTE_PATH_ARRAY = "path";

        final String OWM_ID = "id";
        final String OWM_NO = "no";
        final String OWM_STATUS = "status";
        final String OWM_STATION_KEY = "station_id";

        try {
            ArrayList<ContentValues> values = new ArrayList<>();

            JSONObject json = new JSONObject(jsonString);
            JSONArray routes = json.getJSONArray(OWM_ROUTE_ARRAY);
            int count = json.getInt(OWM_COUNT);

            for (int i = 0; i < count; i++) {
                JSONObject route = routes.getJSONObject(i);
                JSONArray pathArray = route.getJSONArray(OWM_ROUTE_PATH_ARRAY);

                int routeId = route.getInt(OWM_ID);
                int pathCount = route.getInt(OWM_COUNT);

                for (int j = 0; j < pathCount; j++) {
                    JSONObject path = pathArray.getJSONObject(j);

                    ContentValues tmpValues = new ContentValues();
                    tmpValues.put(RoutePathEntry.COLUMN_ROUTE_KEY, routeId);
                    tmpValues.put(RoutePathEntry.COLUMN_STATION_KEY, path.getString(OWM_STATION_KEY));
                    tmpValues.put(RoutePathEntry.COLUMN_NO, path.getInt(OWM_NO));
                    tmpValues.put(RoutePathEntry.COLUMN_STATUS, path.getInt(OWM_STATUS));

                    values.add(tmpValues);
                }
            }

            ContentValues[] resultValues = new ContentValues[values.size()];
            values.toArray(resultValues);
            return resultValues;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Failed to generate route path", e);
            e.printStackTrace();
        }

        return null;
    }


}

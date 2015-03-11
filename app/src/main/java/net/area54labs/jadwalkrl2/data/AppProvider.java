package net.area54labs.jadwalkrl2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import static net.area54labs.jadwalkrl2.data.AppContract.CONTENT_AUTHORITY;
import static net.area54labs.jadwalkrl2.data.AppContract.PATH_ROUTE;
import static net.area54labs.jadwalkrl2.data.AppContract.PATH_SCHEDULE;
import static net.area54labs.jadwalkrl2.data.AppContract.PATH_SEARCH;
import static net.area54labs.jadwalkrl2.data.AppContract.PATH_STATION;
import static net.area54labs.jadwalkrl2.data.AppContract.RouteEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.RoutePathEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.ScheduleEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.SearchEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.StationEntry;

/**
 * JadwalKRL Content Provider
 * <p/>
 * Dev : Saggaf Arsyad
 */
public class AppProvider extends ContentProvider {
    public static final int URI_ROUTE = 100;
    public static final int URI_ROUTE_ID = 101;

    // @TODO: 1. Define Uri constants
    // _ID Uri returns cursor row id, for checking the database change is success or not
    public static final int URI_ROUTE_PATH = 102;
    public static final int URI_ROUTE_PATH_ID = 103;
    public static final int URI_STATION = 200;
    public static final int URI_STATION_ID = 201;
    public static final int URI_SEARCH = 400;
    public static final int URI_SEARCH_ID = 401;
    public static final int URI_SCHEDULE = 500;
    public static final int URI_SCHEDULE_ID = 501;
    public static final int URI_SCHEDULE_BY_SEARCH_SETTING = 502;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sRoutePathQueryBuilder;
    private static final String sRoutePathSelection =
            RoutePathEntry.TABLE_NAME + "." + RoutePathEntry.COLUMN_ROUTE_KEY + " = ? ";
    private static final SQLiteQueryBuilder sScheduleSearchQueryBuilder;

    static {
        sRoutePathQueryBuilder = new SQLiteQueryBuilder();
        sRoutePathQueryBuilder.setTables(
                RoutePathEntry.TABLE_NAME +
                        " INNER JOIN " + StationEntry.TABLE_NAME +
                        " ON " + RoutePathEntry.TABLE_NAME + "." + RoutePathEntry.COLUMN_STATION_KEY +
                        " = " + StationEntry.TABLE_NAME + "." + StationEntry._ID
        );
    }

    static {
        sScheduleSearchQueryBuilder = new SQLiteQueryBuilder();
        sScheduleSearchQueryBuilder.setTables(
                ScheduleEntry.TABLE_NAME +
                        " INNER JOIN " + SearchEntry.TABLE_NAME +
                        " ON " + ScheduleEntry.TABLE_NAME + "." + ScheduleEntry.COLUMN_SEARCH_KEY +
                        " = " + SearchEntry.TABLE_NAME + "." + SearchEntry._ID +
                        " INNER JOIN " + RouteEntry.TABLE_NAME +
                        " ON " + ScheduleEntry.TABLE_NAME + "." + ScheduleEntry.COLUMN_ROUTE_KEY +
                        " = " + RouteEntry.TABLE_NAME + "." + RouteEntry._ID +
                        " INNER JOIN " + StationEntry.TABLE_NAME +
                        " ON " + ScheduleEntry.TABLE_NAME + "." + ScheduleEntry.COLUMN_STATION_KEY +
                        " = " + StationEntry.TABLE_NAME + "." + StationEntry._ID
        );
    }

    private AppDbHelper mOpenHelper;


    // @TODO: 2. Build a uri matcher function
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        // Route URI
        matcher.addURI(authority, PATH_ROUTE, URI_ROUTE);
        matcher.addURI(authority, PATH_ROUTE + "/#", URI_ROUTE_ID);
        matcher.addURI(authority, PATH_ROUTE + "/path", URI_ROUTE_PATH);
        matcher.addURI(authority, PATH_ROUTE + "/path/#", URI_ROUTE_PATH_ID);
        // Station URI
        matcher.addURI(authority, PATH_STATION, URI_STATION);
        matcher.addURI(authority, PATH_STATION + "/*", URI_STATION_ID);
        // Search Request URI
        matcher.addURI(authority, PATH_SEARCH, URI_SEARCH);
        matcher.addURI(authority, PATH_SEARCH + "/#", URI_SEARCH_ID);
        // Schedule URI
        matcher.addURI(authority, PATH_SCHEDULE, URI_SCHEDULE);
        matcher.addURI(authority, PATH_SCHEDULE + "/#", URI_SCHEDULE_ID);
        matcher.addURI(authority, PATH_SCHEDULE + "/search/*", URI_SCHEDULE_BY_SEARCH_SETTING);

        return matcher;
    }

    private Cursor getRoutePathById(Uri uri) {
        long routeId = ContentUris.parseId(uri);

        String[] selectionArgs = new String[]{String.valueOf(routeId)};

        String[] projection = new String[]{
                RoutePathEntry.TABLE_NAME + "." + RoutePathEntry.COLUMN_STATION_KEY + " AS " + StationEntry._ID,
                RoutePathEntry.TABLE_NAME + "." + RoutePathEntry.COLUMN_NO + " AS " + RoutePathEntry.COLUMN_NO,
                StationEntry.TABLE_NAME + "." + StationEntry.COLUMN_NAME + " AS " + StationEntry.COLUMN_NAME
        };

        String sortOrder = RoutePathEntry.TABLE_NAME + "." + RoutePathEntry.COLUMN_NO + " ASC";

        return sRoutePathQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sRoutePathSelection,
                selectionArgs,
                null,
                null,
                sortOrder,
                null);
    }

    private Cursor getRoutePath(String selection, String[] selectionArgs) {
        String[] projection = new String[]{

                RoutePathEntry.TABLE_NAME + "." + RoutePathEntry.COLUMN_STATION_KEY + " AS " + StationEntry._ID,
                StationEntry.TABLE_NAME + "." + StationEntry.COLUMN_NAME + " AS " + StationEntry.COLUMN_NAME,
                RoutePathEntry.TABLE_NAME + "." + RoutePathEntry.COLUMN_NO + " AS " + RoutePathEntry.COLUMN_NO,
                RoutePathEntry.TABLE_NAME + "." + RoutePathEntry.COLUMN_ROUTE_KEY + " AS  " + RoutePathEntry.COLUMN_ROUTE_KEY
        };

        String sortOrder = RoutePathEntry.TABLE_NAME + "." + RoutePathEntry.COLUMN_NO + " ASC";

        return sRoutePathQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                null);
    }

    private Cursor getSearchSchedule(String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        return sScheduleSearchQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                null);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AppDbHelper(getContext());

        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case URI_ROUTE:
                return RouteEntry.CONTENT_DIR_TYPE;
            case URI_ROUTE_ID:
                return RouteEntry.CONTENT_ITEM_TYPE;
            case URI_ROUTE_PATH:
                return RouteEntry.CONTENT_DIR_TYPE;
            case URI_ROUTE_PATH_ID:
                return RouteEntry.CONTENT_DIR_TYPE;
            case URI_STATION:
                return StationEntry.CONTENT_DIR_TYPE;
            case URI_STATION_ID:
                return StationEntry.CONTENT_ITEM_TYPE;
            case URI_SEARCH:
                return SearchEntry.CONTENT_DIR_TYPE;
            case URI_SEARCH_ID:
                return SearchEntry.CONTENT_ITEM_TYPE;
            case URI_SCHEDULE:
                return ScheduleEntry.CONTENT_DIR_TYPE;
            case URI_SCHEDULE_ID:
                return ScheduleEntry.CONTENT_ITEM_TYPE;
            case URI_SCHEDULE_BY_SEARCH_SETTING:
                return ScheduleEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case URI_ROUTE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RouteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case URI_ROUTE_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RouteEntry.TABLE_NAME,
                        projection,
                        RouteEntry._ID + " = " + "'" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case URI_ROUTE_PATH: {
                retCursor = getRoutePath(selection, selectionArgs);
                break;
            }
            case URI_ROUTE_PATH_ID: {
                retCursor = getRoutePathById(uri);
                break;
            }
            case URI_STATION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        StationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case URI_STATION_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        StationEntry.TABLE_NAME,
                        projection,
                        StationEntry._ID + " = " + "'" + StationEntry.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case URI_SEARCH: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SearchEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case URI_SEARCH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SearchEntry.TABLE_NAME,
                        projection,
                        SearchEntry._ID + " = " + "'" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case URI_SCHEDULE_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScheduleEntry.TABLE_NAME,
                        projection,
                        ScheduleEntry._ID + " = " + "'" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case URI_SCHEDULE_BY_SEARCH_SETTING: {
//                String strSearchSetting = ScheduleEntry.parseSearchSettings(uri);

                retCursor = getSearchSchedule(projection, selection, selectionArgs, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case URI_ROUTE: {
                long _id = db.insert(RouteEntry.TABLE_NAME, null, values);

                if (_id > 0) {
                    int routeId = values.getAsInteger(RouteEntry._ID);
                    returnUri = RouteEntry.buildUri(routeId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case URI_STATION: {
                long _id = db.insert(StationEntry.TABLE_NAME, null, values);

                if (_id > 0) {
                    String stationId = values.getAsString(StationEntry._ID);
                    returnUri = StationEntry.buildUri(stationId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case URI_ROUTE_PATH_ID: {
                long _id = db.insert(RoutePathEntry.TABLE_NAME, null, values);

                if (_id > 0) {
                    int routeId = values.getAsInteger(RoutePathEntry.COLUMN_ROUTE_KEY);
                    returnUri = RouteEntry.buildPathUri(routeId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case URI_SEARCH: {
                long _id = db.insert(SearchEntry.TABLE_NAME, null, values);

                if (_id > 0) {
                    returnUri = SearchEntry.buildUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case URI_SCHEDULE: {
                long _id = db.insert(ScheduleEntry.TABLE_NAME, null, values);

                if (_id > 0) {
                    returnUri = ScheduleEntry.buildUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsDeleted;

        switch (match) {
            case URI_ROUTE:
                rowsDeleted = db.delete(RouteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_STATION:
                rowsDeleted = db.delete(StationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_ROUTE_PATH_ID:
                rowsDeleted = db.delete(RoutePathEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_SEARCH:
                rowsDeleted = db.delete(SearchEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_SCHEDULE:
                rowsDeleted = db.delete(ScheduleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsUpdated;

        switch (match) {
            case URI_ROUTE:
                rowsUpdated = db.update(RouteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_STATION:
                rowsUpdated = db.update(RouteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_ROUTE_PATH_ID:
                rowsUpdated = db.update(RouteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_SEARCH:
                rowsUpdated = db.update(RouteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_SCHEDULE:
                rowsUpdated = db.update(RouteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case URI_ROUTE:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RouteEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case URI_STATION:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(StationEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case URI_ROUTE_PATH:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RoutePathEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case URI_SCHEDULE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ScheduleEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }


}

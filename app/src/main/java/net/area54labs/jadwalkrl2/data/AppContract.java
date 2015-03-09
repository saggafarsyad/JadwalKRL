package net.area54labs.jadwalkrl2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class AppContract {
    public static final String CONTENT_AUTHORITY = "net.area54labs.jadwalkrl2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ROUTE = "route";
    public static final String PATH_STATION = "station";
    public static final String PATH_SEARCH = "search";
    public static final String PATH_SCHEDULE = "schedule";

    public static final class RouteEntry implements BaseColumns {
        public static final String TABLE_NAME = "route";
        public static final String COLUMN_NAME = "name";
        public static final String PATH_ROUTE_PATH = "path";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROUTE).build();

        // For insert and quering
        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROUTE;

        public static Uri buildPathUri(long id) {
            return ContentUris.withAppendedId(
                    CONTENT_URI.buildUpon().appendPath(PATH_ROUTE_PATH).build(),
                    id
            );
        }

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROUTE;

        public static Uri buildPathDirUri() {
            return CONTENT_URI.buildUpon().appendPath(PATH_ROUTE_PATH).build();
        }


    }

    public static final class StationEntry implements BaseColumns {
        public static final String TABLE_NAME = "station";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LATITUDE = "lat";
        public static final String COLUMN_LONGITUDE = "lng";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STATION).build();

        // Custom uri build for String id
        public static Uri buildUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STATION;

        // Custom parser for String id
        public static String parseId(Uri uri) {
            return uri.getLastPathSegment();
        }

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STATION;

        // Custom parser for String name
        public static String parseName(Uri uri) {
            return uri.getLastPathSegment();
        }


    }

    public static final class RoutePathEntry implements BaseColumns {
        public static final String TABLE_NAME = "route_path";
        public static final String COLUMN_ROUTE_KEY = "route_id";
        public static final String COLUMN_STATION_KEY = "station_id";
        public static final String COLUMN_NO = "no";
        public static final String COLUMN_STATUS = "status";
    }

    public static final class SearchEntry implements BaseColumns {
        public static final String TABLE_NAME = "search";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_SETTING = "search_setting";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH).build();

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCHEDULE;


    }

    public static final class ScheduleEntry implements BaseColumns {
        public static final String TABLE_NAME = "schedule";
        public static final String COLUMN_SEARCH_KEY = "search_id";
        public static final String COLUMN_ROUTE_KEY = "route_id";
        public static final String COLUMN_STATION_KEY = "station_id";
        public static final String COLUMN_DIRECTION = "direction";
        public static final String COLUMN_DEPART_TIMESTAMP = "depart_timestamp";
        public static final String COLUMN_UNIT_NO = "unit_no";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHEDULE).build();

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCHEDULE;

        public static Uri buildSearchUri(String settings) {
            return CONTENT_URI.buildUpon().appendPath(PATH_SEARCH).appendPath(settings).build();
        }

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCHEDULE;

        public static String parseSearchSettings(Uri uri) {
            return uri.getLastPathSegment();
        }


    }
}

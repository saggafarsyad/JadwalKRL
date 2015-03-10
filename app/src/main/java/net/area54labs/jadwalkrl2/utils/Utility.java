package net.area54labs.jadwalkrl2.utils;

import android.content.Context;
import android.database.Cursor;

import static net.area54labs.jadwalkrl2.data.AppContract.RouteEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.RoutePathEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.SearchEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.StationEntry;

/**
 * Utility Class
 */
public class Utility {
    public static final String SELECTED_STATION_KEY = "selected_station";
    public static final String TWO_PANE_KEY = "two_pane";

    public static final int MODE_RANDOM = 0;
    public static final int MODE_PREV = 1;
    public static final int MODE_NEXT = 2;
    public static final int MODE_FIRST = 3;
    public static final int MODE_LAST = 4;
    public static final int NO_ROUTE = -1;

    public static String[] getStationFromRoute(Context context, int mode, int routeId) {
        String[] result = null;
        Cursor cursor = context.getContentResolver().query(
                RouteEntry.buildPathDirUri(), null,
                RoutePathEntry.COLUMN_ROUTE_KEY + " = ?",
                new String[]{
                        String.valueOf(routeId)
                }, RoutePathEntry.COLUMN_NO
        );

        if (cursor.moveToFirst()) {
            if (mode == MODE_FIRST) {
                result = new String[]{
                        cursor.getString(cursor.getColumnIndex(StationEntry._ID)),
                        cursor.getString(cursor.getColumnIndex(StationEntry.COLUMN_NAME))
                };
            } else if (mode == MODE_LAST) {
                cursor.moveToLast();
                result = new String[]{
                        cursor.getString(cursor.getColumnIndex(StationEntry._ID)),
                        cursor.getString(cursor.getColumnIndex(StationEntry.COLUMN_NAME))
                };
            }
        }

        cursor.close();
        return result;
    }

    public static String[] getStationFromRoute(Context context, int mode, int routeId, String stationId) {
        String[] result = null;
        String selection = null;
        String[] selectionArgs = null;

        if (mode == MODE_RANDOM) {
            selection = RoutePathEntry.COLUMN_STATION_KEY + " = ?";
            selectionArgs = new String[]{stationId};
        } else if (mode == MODE_PREV || mode == MODE_NEXT) {
            selection = RoutePathEntry.COLUMN_STATION_KEY + " = ? AND " +
                    RoutePathEntry.COLUMN_ROUTE_KEY + " = ?";
            selectionArgs = new String[]{
                    stationId, String.valueOf(routeId)
            };
        }

        // Get route of the station
        Cursor cursor = context.getContentResolver().query(
                RouteEntry.buildPathDirUri(), null,
                selection,
                selectionArgs, RoutePathEntry.COLUMN_NO
        );

        if (cursor.moveToFirst()) {
            if (mode == MODE_RANDOM)
                routeId = cursor.getInt(cursor.getColumnIndex(RoutePathEntry.COLUMN_ROUTE_KEY));

            int pathNo = cursor.getInt(cursor.getColumnIndex(RoutePathEntry.COLUMN_NO));

            cursor.close();
            cursor = context.getContentResolver().query(
                    RouteEntry.buildPathDirUri(), null,
                    RoutePathEntry.COLUMN_ROUTE_KEY + " = ?",
                    new String[]{
                            String.valueOf(routeId)
                    }, RoutePathEntry.COLUMN_NO
            );

            if (cursor.moveToFirst()) {
                int pathCount = cursor.getCount();

                int curPos = -1;

                switch (mode) {
                    case MODE_RANDOM:
                        if (pathNo == pathCount) {
                            curPos = pathNo - 2;
                        } else {
                            curPos = pathNo;
                        }
                        break;
                    case MODE_PREV:
                        if (pathNo > 1) curPos = pathNo - 2;
                        break;
                    case MODE_NEXT:
                        if (pathNo < cursor.getCount()) curPos = pathNo;
                        break;
                }

                if (curPos < 0) {
                    cursor.close();
                    return null;
                }

                cursor.moveToPosition(curPos);
                result = new String[]{
                        cursor.getString(cursor.getColumnIndex(StationEntry._ID)),
                        cursor.getString(cursor.getColumnIndex(StationEntry.COLUMN_NAME))
                };
            }
        }

        cursor.close();
        return result;
    }


    public static String getStationName(Context context, String stationId) {
        Cursor cursor = context.getContentResolver().query(
                StationEntry.buildUri(stationId), null, null, null, null
        );

        String result = null;

        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(StationEntry.COLUMN_NAME));
        }

        cursor.close();
        return result;
    }

    public static String getRouteName(Context context, int routeId) {
        Cursor cursor = context.getContentResolver().query(
                RouteEntry.buildUri(routeId), null, null, null, null
        );

        String result = null;

        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(RouteEntry.COLUMN_NAME));
        }

        cursor.close();
        return result;
    }

    public static String getSearchSettingString(Context context, long searchId) {
        Cursor cursor = context.getContentResolver().query(
                SearchEntry.buildUri(searchId), null, null, null, null
        );

        if (cursor.moveToFirst()) {
            String strSearchSetting = cursor.getString(cursor.getColumnIndex(SearchEntry.COLUMN_SETTING));
            cursor.close();

            return strSearchSetting;
        }
        cursor.close();
        return null;
    }


    public static int getHourFromTimestamp(long timestamp) {
        return (int) timestamp / 3600;
    }

    public static int getMinutesFromTimestamp(long timestamp) {
        return (int) (timestamp % 3600) / 60;
    }

    public static String departTimestampToString(long timestamp) {
        int hour = getHourFromTimestamp(timestamp);
        int minutes = getMinutesFromTimestamp(timestamp);

        String strHour = String.valueOf(hour);

        if (hour < 10) strHour = "0" + strHour;

        String strMinutes = String.valueOf(minutes);
        if (minutes < 10) strMinutes = "0" + strMinutes;

        return strHour + ":" + strMinutes;
    }
}

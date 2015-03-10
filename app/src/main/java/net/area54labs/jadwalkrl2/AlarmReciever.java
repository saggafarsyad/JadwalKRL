package net.area54labs.jadwalkrl2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import net.area54labs.jadwalkrl2.utils.SearchSetting;
import net.area54labs.jadwalkrl2.utils.Utility;

import static net.area54labs.jadwalkrl2.data.AppContract.ScheduleEntry;

/**
 * Created by Saggaf on 3/9/2015.
 */
public class AlarmReciever extends BroadcastReceiver {

    public static final String SCHEDULE_GROUP_KEY = "group_schedule";
    public static final String SCHEDULE_ID_KEY = "schedule_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        long scheduleId;
        // Get schedule id from intent
        if (intent.hasExtra(SCHEDULE_ID_KEY)) {
            scheduleId = intent.getLongExtra(SCHEDULE_ID_KEY, -1);
        } else {
            return;
        }

        // Get shared preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Get notification time from settings

        int minutes = Integer.parseInt(prefs.getString(context.getString(R.string.pref_notification_time_key),
                context.getString(R.string.pref_minutes_10)));

        Resources res = context.getResources();

        String timeRemainingString;

        switch (minutes) {
            case 10:
                timeRemainingString = context.getString(R.string.pref_minutes_10_label);
                break;
            case 30:
                timeRemainingString = context.getString(R.string.pref_minutes_30_label);
                break;
            case 60:
                timeRemainingString = context.getString(R.string.pref_minutes_60_label);
                break;
            default:
                return;
        }

        // Get schedule
        Cursor cursor = context.getContentResolver().query(
                ScheduleEntry.buildUri(scheduleId), null, null, null, null
        );

        if (cursor.moveToFirst()) {
            long searchId = cursor.getLong(cursor.getColumnIndex(ScheduleEntry.COLUMN_SEARCH_KEY));
            int routeId = cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_ROUTE_KEY));
            String stationId = cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_STATION_KEY));
            long departTimestamp = cursor.getLong(cursor.getColumnIndex(ScheduleEntry.COLUMN_DEPART_TIMESTAMP));
            String unitNo = cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_UNIT_NO));

            cursor.close();

            // Get search settings
            SearchSetting searchSetting = new SearchSetting(context, Utility.getSearchSettingString(context, searchId));


            String fromStationName = Utility.getStationName(context, stationId);
            String forStationName;

            if (searchSetting.isAdvanced()) {
                forStationName = Utility.getStationName(context, searchSetting.getStationDepartFor());
            } else {
                forStationName = Utility.getStationFromRoute(context, Utility.MODE_LAST, routeId)[1];
            }

            String departTime = Utility.departTimestampToString(departTimestamp);

            String notificationTitle = String.format(context.getString(R.string.format_notification_title), timeRemainingString);
            String notificationContent = String.format(context.getString(R.string.format_notification),
                    unitNo,
                    fromStationName,
                    forStationName,
                    departTime);

            int smallIcon = R.drawable.ic_notification_train;

            int notifyID = 1;

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setColor(res.getColor(R.color.primary))
                            .setSmallIcon(smallIcon)
                            .setGroup(SCHEDULE_GROUP_KEY)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationContent);

            Notification notif = new NotificationCompat.BigTextStyle(mBuilder)
                    .bigText(notificationContent).build();
            Intent resultIntent = new Intent(context, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(notifyID, notif);
        }

        cursor.close();
    }
}

package net.area54labs.jadwalkrl2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.area54labs.jadwalkrl2.utils.SearchSetting;
import net.area54labs.jadwalkrl2.utils.Utility;

import java.util.Calendar;

import static net.area54labs.jadwalkrl2.data.AppContract.ScheduleEntry;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String URI_EXTRA = "uri";
    public static final int DETAIL_LOADER = 2;
    public static final String[] DETAIL_COLUMNS = {
            ScheduleEntry.TABLE_NAME + "." + ScheduleEntry._ID,
            ScheduleEntry.COLUMN_ROUTE_KEY,
            ScheduleEntry.COLUMN_STATION_KEY,
            ScheduleEntry.COLUMN_DEPART_TIMESTAMP,
            ScheduleEntry.COLUMN_UNIT_NO,
            ScheduleEntry.COLUMN_SEARCH_KEY
    };
    public static final int COL_ID = 0;
    public static final int COL_ROUTE_KEY = 1;
    public static final int COL_STATION_KEY = 2;
    public static final int COL_DEPART_TIMESTAMP = 3;
    public static final int COL_UNIT_NO = 4;
    public static final int COL_SEARCH_KEY = 5;
    private static final String SCHEDULE_SHARE_HASHTAG = " #jadwalkrl.com";
    private final String LOG_TAG = this.getClass().getSimpleName();
    long mScheduleId = -1;
    long mDepartTimestamp = -1;
    TextView mDepartFromStationTextView;
    TextView mDepartForStationTextView;
    TextView mNextStationTextView;
    TextView mRouteNameTextView;
    TextView mDepartTimeTextView;
    TextView mTrainNoTextView;
    ImageButton mNotifyButton;
    private String strShareSchedule;
    private ShareActionProvider mShareActionProvider;
    private Uri mUri;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (strShareSchedule != null) {
            mShareActionProvider.setShareIntent(createShareScheduleIntent());
        }
    }

    private Intent createShareScheduleIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        } else {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, strShareSchedule + SCHEDULE_SHARE_HASHTAG);

        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();

        if (args != null) {
            mUri = args.getParcelable(DetailFragment.URI_EXTRA);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Get all views
        mDepartFromStationTextView = (TextView) rootView.findViewById(R.id.depart_from_station_text);
        mDepartForStationTextView = (TextView) rootView.findViewById(R.id.depart_for_station_text);
        mNextStationTextView = (TextView) rootView.findViewById(R.id.next_station_text);
        mRouteNameTextView = (TextView) rootView.findViewById(R.id.route_text);
        mDepartTimeTextView = (TextView) rootView.findViewById(R.id.depart_time_text);
        mTrainNoTextView = (TextView) rootView.findViewById(R.id.train_no_text);
        mNotifyButton = (ImageButton) rootView.findViewById(R.id.notification_button);

        mNotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScheduleId != -1) {
                    setNotifySchedule();
                }
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS, null, null, null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mScheduleId = data.getLong(COL_ID);
            long mSearchId = data.getLong(COL_SEARCH_KEY);

            // Get search params
            String strSearchSetting = Utility.getSearchSettingString(getActivity(), mSearchId);
            SearchSetting searchSetting = new SearchSetting(getActivity(), strSearchSetting);

            String stationId = data.getString(COL_STATION_KEY);
            int routeId = data.getInt(COL_ROUTE_KEY);
            mDepartTimestamp = data.getLong(COL_DEPART_TIMESTAMP);

            String mDepartFromStation = Utility.getStationName(getActivity(), stationId);

            String mDepartForStation;

            if (searchSetting.isAdvanced()) {
                mDepartForStation = Utility.getStationName(getActivity(), searchSetting.getStationDepartFor());
            } else {
                mDepartForStation = Utility.getStationFromRoute(getActivity(), Utility.MODE_LAST, routeId)[1];
            }

            String nextStation = Utility.getStationFromRoute(getActivity(), Utility.MODE_NEXT, routeId, stationId)[1];
            String mRouteName = Utility.getRouteName(getActivity(), routeId);
            String departTime = Utility.departTimestampToString(mDepartTimestamp);
            String mUnitNo = data.getString(COL_UNIT_NO);

            mDepartFromStationTextView.setText(mDepartFromStation);
            mDepartForStationTextView.setText(mDepartForStation);
            mNextStationTextView.setText(nextStation);
            mRouteNameTextView.setText(mRouteName);
            mDepartTimeTextView.setText(departTime);
            mTrainNoTextView.setText(mUnitNo);

            // @TODO: Change to string format

            strShareSchedule = String.format(getActivity().getString(R.string.format_share_text),
                    departTime,
                    mDepartFromStation,
                    mDepartForStation);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareScheduleIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void setNotifySchedule() {
        // Get notification time
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        int minutes = Integer.parseInt(prefs.getString(getActivity().getString(R.string.pref_notification_time_key),
                getActivity().getString(R.string.pref_minutes_10))) * 60;

        // Generate timestamp
        int departHour = Utility.getHourFromTimestamp(mDepartTimestamp);
        int departMinutes = Utility.getMinutesFromTimestamp(mDepartTimestamp);

        if (departHour == 0) departHour = 24;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, departMinutes);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, departHour);

        long notificationTimestamp = cal.getTimeInMillis() - (minutes * 1000);
//        long notificationTimestamp = Calendar.getInstance().getTimeInMillis() + 10000;
        Intent alarmIntent = new Intent(getActivity(), AlarmReciever.class);
        alarmIntent.putExtra(AlarmReciever.SCHEDULE_ID_KEY, mScheduleId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTimestamp, pendingIntent);

        String strMessage = getActivity().getString(R.string.message_notification_set);
        Toast.makeText(getActivity(), strMessage, Toast.LENGTH_SHORT).show();
    }
}
package net.area54labs.jadwalkrl2;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.area54labs.jadwalkrl2.utils.SearchSetting;
import net.area54labs.jadwalkrl2.utils.Utility;

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
    public static final int COL_ROUTE_KEY = 1;
    public static final int COL_STATION_KEY = 2;
    public static final int COL_DEPART_TIMESTAMP = 3;
    public static final int COL_UNIT_NO = 4;
    public static final int COL_SEARCH_KEY = 5;
    TextView mDepartFromStationTextView;
    TextView mDepartForStationTextView;
    TextView mNextStationTextView;
    TextView mRouteNameTextView;
    TextView mDepartTimeTextView;
    TextView mTrainNoTextView;
    private Uri mUri;

    public DetailFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();

        if (args != null) {
            mUri = args.getParcelable(DetailFragment.URI_EXTRA);
        }

        View rootView = inflater.inflate(R.layout.fragment_route_detail, container, false);

        // Get all views
        mDepartFromStationTextView = (TextView) rootView.findViewById(R.id.depart_from_station_text);
        mDepartForStationTextView = (TextView) rootView.findViewById(R.id.depart_for_station_text);
        mNextStationTextView = (TextView) rootView.findViewById(R.id.next_station_text);
        mRouteNameTextView = (TextView) rootView.findViewById(R.id.route_text);
        mDepartTimeTextView = (TextView) rootView.findViewById(R.id.depart_time_text);
        mTrainNoTextView = (TextView) rootView.findViewById(R.id.train_no_text);

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
            long searchId = data.getLong(COL_SEARCH_KEY);

            // Get search params
            String strSearchSetting = Utility.getSearchSetting(getActivity(), searchId);
            SearchSetting searchSetting = new SearchSetting(getActivity(), strSearchSetting);

            String stationId = data.getString(COL_STATION_KEY);
            int routeId = data.getInt(COL_ROUTE_KEY);
            long departTimestamp = data.getLong(COL_DEPART_TIMESTAMP);

            String departFromStation = Utility.getStationName(getActivity(), stationId);
            String departForStation;
            if (searchSetting.isAdvanced()) {
                departForStation = Utility.getStationName(getActivity(), searchSetting.getStationDepartFor());
            } else {
                departForStation = Utility.getStationFromRoute(getActivity(), Utility.MODE_LAST, routeId)[1];
            }

            String nextStation = Utility.getStationFromRoute(getActivity(), Utility.MODE_NEXT, routeId, stationId)[1];
            String routeName = Utility.getRouteName(getActivity(), routeId);
            String departTime = Utility.departTimestampToString(departTimestamp);
            String unitNo = data.getString(COL_UNIT_NO);

            mDepartFromStationTextView.setText(departFromStation);
            mDepartForStationTextView.setText(departForStation);
            mNextStationTextView.setText(nextStation);
            mRouteNameTextView.setText(routeName);
            mDepartTimeTextView.setText(departTime);
            mTrainNoTextView.setText(unitNo);
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
}
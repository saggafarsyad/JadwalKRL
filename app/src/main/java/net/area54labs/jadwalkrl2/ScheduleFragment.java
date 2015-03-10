package net.area54labs.jadwalkrl2;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.area54labs.jadwalkrl2.adapter.ScheduleAdapter;
import net.area54labs.jadwalkrl2.data.AppContract;
import net.area54labs.jadwalkrl2.service.ScheduleService;
import net.area54labs.jadwalkrl2.utils.SearchSetting;
import net.area54labs.jadwalkrl2.utils.Utility;

import static net.area54labs.jadwalkrl2.data.AppContract.ScheduleEntry;

public class ScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String[] ADVANCED_SCHEDULE_RESULT_COLUMNS = new String[]{
            ScheduleEntry.TABLE_NAME + "." + ScheduleEntry._ID + " AS " + ScheduleEntry._ID,
            ScheduleEntry.TABLE_NAME + "." + ScheduleEntry.COLUMN_DEPART_TIMESTAMP + " AS " + ScheduleEntry.COLUMN_DEPART_TIMESTAMP,
            AppContract.RouteEntry.TABLE_NAME + "." + AppContract.RouteEntry.COLUMN_NAME + " AS " + AppContract.RouteEntry.TABLE_NAME + "_" + AppContract.RouteEntry.COLUMN_NAME,
            ScheduleEntry.TABLE_NAME + "." + ScheduleEntry.COLUMN_UNIT_NO + " AS " + ScheduleEntry.COLUMN_UNIT_NO
    };
    public static final String[] QUICK_SCHEDULE_RESULT_COLUMNS = new String[]{
            ScheduleEntry.TABLE_NAME + "." + ScheduleEntry._ID + " AS " + ScheduleEntry._ID,
            ScheduleEntry.TABLE_NAME + "." + ScheduleEntry.COLUMN_DEPART_TIMESTAMP + " AS " + ScheduleEntry.COLUMN_DEPART_TIMESTAMP,
            ScheduleEntry.TABLE_NAME + "." + ScheduleEntry.COLUMN_ROUTE_KEY,
            ScheduleEntry.TABLE_NAME + "." + ScheduleEntry.COLUMN_STATION_KEY,
    };
    private static final String SELECTED_KEY = "selected_schedule";
    private static final int SCHEDULE_LOADER = 1;
    ProgressBar progressBar;
    TextView departFromTextView;
    TextView departForTextView;
    TextView timeBottomLimitTextView;
    TextView timeTopLimitTextView;
    ImageView iconView;
    ListView scheduleList;
    int counter = 0;
    private int mPosition = ListView.INVALID_POSITION;
    private ScheduleAdapter mScheduleAdapter;
    private SearchSetting mSearchSetting;

    private boolean mTwoPane;

    public ScheduleFragment() {
    }

    public static ScheduleFragment newInstance(boolean twoPane) {
        ScheduleFragment fragment = new ScheduleFragment();

        Bundle args = new Bundle();
        args.putBoolean(Utility.TWO_PANE_KEY, twoPane);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchSchedule();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    private void fetchSchedule() {
        Intent intent = new Intent(getActivity(), ScheduleService.class);
        intent.putExtra(ScheduleService.SEARCH_SETTING_EXTRA, mSearchSetting.toString());
        getActivity().startService(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            mTwoPane = getArguments().getBoolean(Utility.TWO_PANE_KEY);
        }

        mSearchSetting = new SearchSetting(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        View headerLayout = rootView.findViewById(R.id.header);
        View timeLayout = rootView.findViewById(R.id.time_layout);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        if (mTwoPane) {
            headerLayout.setVisibility(View.GONE);
        } else {
            iconView = (ImageView) headerLayout.findViewById(R.id.icon);
            departFromTextView = (TextView) headerLayout.findViewById(R.id.depart_from_station_text);
            departForTextView = (TextView) headerLayout.findViewById(R.id.depart_for_station_text);
            timeBottomLimitTextView = (TextView) headerLayout.findViewById(R.id.time_bottom_limit_text);
            timeTopLimitTextView = (TextView) headerLayout.findViewById(R.id.time_top_limit_text);

            departFromTextView.setText(Utility.getStationName(getActivity(), mSearchSetting.getStationDepartFrom()));

            if (mSearchSetting.isAdvanced()) {
                int height = (int) getResources().getDimension(R.dimen.icon_advanced_height);
                iconView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height));
                iconView.setImageResource(R.drawable.ic_route);

                departForTextView.setVisibility(View.VISIBLE);
                timeLayout.setVisibility(View.VISIBLE);

                departForTextView.setText(Utility.getStationName(getActivity(), mSearchSetting.getStationDepartFor()));

                timeBottomLimitTextView.setText(String.valueOf(mSearchSetting.getTimeBottomLimit()) + ".00");
                timeTopLimitTextView.setText(String.valueOf(mSearchSetting.getTimeTopLimit()) + ".00");
            } else {
                iconView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                iconView.setImageResource(R.drawable.ic_station);

                departForTextView.setVisibility(View.GONE);
                timeLayout.setVisibility(View.GONE);
            }
        }

        mScheduleAdapter = new ScheduleAdapter(getActivity(), null, 0);
        mScheduleAdapter.setSearchResultMode(mSearchSetting.isAdvanced());

        scheduleList = (ListView) rootView.findViewById(R.id.schedule_list);
        scheduleList.setAdapter(mScheduleAdapter);

        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    long scheduleId = cursor.getLong(cursor.getColumnIndex(ScheduleEntry._ID));

                    ((Callback) getActivity())
                            .onItemSelected(ScheduleEntry.buildUri(scheduleId));
                }

                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);

        Uri scheduleUri = ScheduleEntry.buildSearchUri(mSearchSetting.toString());

        String[] projection;

        if (mSearchSetting.isAdvanced()) {
            projection = ADVANCED_SCHEDULE_RESULT_COLUMNS;
        } else {
            projection = QUICK_SCHEDULE_RESULT_COLUMNS;
        }

        String sortOrder = ScheduleEntry.TABLE_NAME + "." + ScheduleEntry.COLUMN_DEPART_TIMESTAMP + " ASC";

        return new CursorLoader(getActivity(), scheduleUri, projection, null, null, sortOrder);
    }

    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(SCHEDULE_LOADER, null, this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Cursor will be notified twice
        // First notify means the data has not been fetch from database/internet
        // Second notify means the data has been fetch. It is either return a list of schedule or empty
        counter++;

        if (counter >= 2) {
            progressBar.setVisibility(View.GONE);

            if (data.getCount() > 0) {
                mScheduleAdapter.swapCursor(data);

                if (mPosition != ListView.INVALID_POSITION) {
                    scheduleList.smoothScrollToPosition(mPosition);
                }
            } else {
                // No schedules returned. Back to main screen
                Toast.makeText(getActivity(), "Schedule not found", Toast.LENGTH_SHORT).show();

                if (!mTwoPane) {
                    getActivity().finish();
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mScheduleAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(SCHEDULE_LOADER, null, this);
    }

    public interface Callback {
        public void onItemSelected(Uri scheduleUri);
    }
}
package net.area54labs.jadwalkrl2;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.area54labs.jadwalkrl2.data.AppContract;
import net.area54labs.jadwalkrl2.utils.SearchSetting;
import net.area54labs.jadwalkrl2.utils.Utility;

/**
 * Created by Saggaf on 3/10/2015.
 */
public class SelectorFragment extends Fragment {
    private static final String BUTTON_KEY = "button_id";
    LinearLayout headerLayout;
    TextView selectedStationTextView;
    ListView stationListView;

    SearchSetting searchSetting;

    int mButtonId;
    Boolean mTwoPane;

    public SelectorFragment() {
    }

    public static SelectorFragment newInstance(boolean twoPane, int buttonId) {
        SelectorFragment fragment = new SelectorFragment();

        Bundle args = new Bundle();
        args.putBoolean(Utility.TWO_PANE_KEY, twoPane);
        args.putInt(BUTTON_KEY, buttonId);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            mTwoPane = getArguments().getBoolean(Utility.TWO_PANE_KEY);
            mButtonId = getArguments().getInt(BUTTON_KEY);
        } else {
            mTwoPane = false;
            mButtonId = getActivity().getIntent().getIntExtra(Utility.SELECTED_STATION_KEY, 0);
        }

        // Get search setting
        searchSetting = new SearchSetting(getActivity());

        // If phone, get button id from intent

        // Inflate views
        View rootView = inflater.inflate(R.layout.fragment_selector, container, false);
        headerLayout = (LinearLayout) rootView.findViewById(R.id.header);
        stationListView = (ListView) rootView.findViewById(R.id.station_list);

        // Get station Id
        String stationId = new String();

        switch (mButtonId) {
            case R.id.station_button:
                stationId = searchSetting.getStationDepartFrom();
                break;

            case R.id.depart_from_station_button:
                stationId = searchSetting.getStationDepartFrom();
                break;

            case R.id.depart_for_station_button:
                stationId = searchSetting.getStationDepartFor();
                break;
        }

        // Get station name
        String stationName = Utility.getStationName(getActivity(), stationId);

        // If tablet, don't show header. Else show header
        if (mTwoPane) {
            headerLayout.setVisibility(View.GONE);
        } else {
            headerLayout.setVisibility(View.VISIBLE);
            selectedStationTextView = (TextView) headerLayout.findViewById(R.id.selected_station);

            selectedStationTextView.setText(stationName);
        }

        // Load station list
        final Cursor stationCursor = getActivity().getContentResolver().query(
                AppContract.StationEntry.CONTENT_URI,
                null, null, null, AppContract.StationEntry.COLUMN_NAME + " ASC"
        );

        SimpleCursorAdapter mStationAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                stationCursor,
                new String[]{
                        AppContract.StationEntry.COLUMN_NAME
                }, new int[]{
                android.R.id.text1
        }, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        stationListView.setAdapter(mStationAdapter);

        stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stationCursor.moveToPosition(position);
                String stationId = stationCursor.getString(stationCursor.getColumnIndex(AppContract.StationEntry._ID));

                switch (mButtonId) {
                    case R.id.station_button:
                        searchSetting.setStationDepartFrom(stationId);
                        break;

                    case R.id.depart_from_station_button:
                        searchSetting.setStationDepartFrom(stationId);
                        break;

                    case R.id.depart_for_station_button:
                        searchSetting.setStationDepartFor(stationId);
                        break;
                }

                searchSetting.save();
                stationCursor.close();

                if (mTwoPane) {
                    SearchFragment searchFragment = (SearchFragment) getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.SEARCH_FRAGMENT_TAG);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_right, new DetailFragment(), MainActivity.DETAIL_FRAGMENT_TAG)
                            .commit();

                    searchFragment.refreshView();
                } else {
                    getActivity().finish();
                }
            }
        });

        return rootView;
    }
}

package net.area54labs.jadwalkrl2;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.area54labs.jadwalkrl2.data.AppContract;
import net.area54labs.jadwalkrl2.utils.SearchSetting;
import net.area54labs.jadwalkrl2.utils.Utility;


public class SelectorActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        LinearLayout headerLayout;
        TextView selectedStationTextView;
        ListView stationListView;

        SearchSetting searchSetting;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_selector, container, false);

            headerLayout = (LinearLayout) rootView.findViewById(R.id.header);
            selectedStationTextView = (TextView) headerLayout.findViewById(R.id.selected_station);

            stationListView = (ListView) rootView.findViewById(R.id.station_list);

            searchSetting = new SearchSetting(getActivity());

            // Get what button called this selector from intent
            final int stationButtonId = getActivity().getIntent().getIntExtra(Utility.SELECTED_STATION_KEY, 0);

            String stationId = new String();

            switch (stationButtonId) {
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

            // Get selected station by parsing search settings
            String stationName = Utility.getStationName(getActivity(), stationId);

            selectedStationTextView.setText(stationName);

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

                    switch (stationButtonId) {
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
                    getActivity().finish();
                }
            });

            return rootView;
        }
    }
}

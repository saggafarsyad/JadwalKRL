package net.area54labs.jadwalkrl2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import net.area54labs.jadwalkrl2.utils.SearchSetting;
import net.area54labs.jadwalkrl2.utils.Utility;

public class SearchFragment extends Fragment {

    boolean isAdvancedSearchMode;

    LinearLayout layoutQuickSearch;
    LinearLayout layoutAdvancedSearch;

    Button buttonStation;
    Button buttonDepartFromStation;
    Button buttonDepartForStation;

    ImageButton buttonSwitchQuickSearch;
    ImageButton buttonSwitchAdvancedSearch;
    ImageButton buttonSwapStation;
    ImageButton buttonSearch;
    EditText inputTimeTopLimit;
    EditText inputTimeBottomLimit;

    SearchSetting searchSetting;

    public SearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_map) {
            startActivity(new Intent(getActivity(), RouteMapActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showQuickSearch() {
        setStationOptions(R.id.station_button, searchSetting.getStationDepartFrom());

        layoutQuickSearch.setVisibility(View.VISIBLE);
        layoutAdvancedSearch.setVisibility(View.GONE);

        isAdvancedSearchMode = false;
    }

    private void showAdvancedSearch() {
        layoutQuickSearch.setVisibility(View.GONE);
        layoutAdvancedSearch.setVisibility(View.VISIBLE);

        isAdvancedSearchMode = true;

        setStationOptions(R.id.depart_from_station_button, searchSetting.getStationDepartFrom());
        setStationOptions(R.id.depart_for_station_button, searchSetting.getStationDepartFor());
        inputTimeBottomLimit.setText(String.valueOf(searchSetting.getTimeBottomLimit()));
        inputTimeTopLimit.setText(String.valueOf(searchSetting.getTimeTopLimit()));
    }


    @Override
    public void onResume() {
        super.onResume();

        // Load search settings
        searchSetting = new SearchSetting(getActivity());

        if (searchSetting.isAdvanced()) {
            showAdvancedSearch();
        } else {
            showQuickSearch();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // Inflate layouts
        layoutQuickSearch = (LinearLayout) rootView.findViewById(R.id.quick_search_layout);
        buttonStation = (Button) layoutQuickSearch.findViewById(R.id.station_button);
        buttonSwitchAdvancedSearch = (ImageButton) layoutQuickSearch.findViewById(R.id.advanced_search_button);

        layoutAdvancedSearch = (LinearLayout) rootView.findViewById(R.id.advanced_search_layout);
        buttonDepartFromStation = (Button) layoutAdvancedSearch.findViewById(R.id.depart_from_station_button);
        buttonDepartForStation = (Button) layoutAdvancedSearch.findViewById(R.id.depart_for_station_button);
        buttonSwapStation = (ImageButton) layoutAdvancedSearch.findViewById(R.id.swap_station_button);
        inputTimeBottomLimit = (EditText) layoutAdvancedSearch.findViewById(R.id.time_bottom_limit_input);
        inputTimeTopLimit = (EditText) layoutAdvancedSearch.findViewById(R.id.time_top_limit_input);
        buttonSwitchQuickSearch = (ImageButton) layoutAdvancedSearch.findViewById(R.id.quick_search_button);
        buttonSearch = (ImageButton) rootView.findViewById(R.id.search_button);

        // Set button listeners
        buttonSwitchQuickSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSetting.switchMode();
                searchSetting.save();
                showQuickSearch();
            }
        });

        buttonSwitchAdvancedSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSetting.switchMode();
                searchSetting.save();
                showAdvancedSearch();
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchSetting.isAdvanced()) {
                    int topLimit = Integer.parseInt(inputTimeTopLimit.getText().toString());
                    int bottomLimit = Integer.parseInt(inputTimeBottomLimit.getText().toString());

                    searchSetting.setTimeBottomLimit(bottomLimit);
                    searchSetting.setTimeTopLimit(topLimit);

                    searchSetting.validateTimeRange();
                }

                searchSetting.save();

                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                startActivity(intent);
            }
        });

        buttonStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open station select fragment
                Intent intent = new Intent(getActivity(), SelectorActivity.class);
                intent.putExtra(Utility.SELECTED_STATION_KEY, R.id.station_button);
                startActivity(intent);
            }
        });

        buttonDepartFromStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open station select fragment
                Intent intent = new Intent(getActivity(), SelectorActivity.class);
                intent.putExtra(Utility.SELECTED_STATION_KEY, R.id.depart_from_station_button);
                startActivity(intent);
            }
        });

        buttonDepartForStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open station select fragment
                Intent intent = new Intent(getActivity(), SelectorActivity.class);
                intent.putExtra(Utility.SELECTED_STATION_KEY, R.id.depart_for_station_button);
                startActivity(intent);
            }
        });

        buttonSwapStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSetting.swapStation();
                searchSetting.save();

                // Swap view
                String departForStationId = String.valueOf(buttonDepartForStation.getTag());
                String departFromStationId = String.valueOf(buttonDepartFromStation.getTag());

                String departForStationName = String.valueOf(buttonDepartForStation.getText());
                String departFromStationName = String.valueOf(buttonDepartFromStation.getText());

                buttonDepartForStation.setText(departFromStationName);
                buttonDepartForStation.setTag(departFromStationId);

                buttonDepartFromStation.setText(departForStationName);
                buttonDepartFromStation.setTag(departForStationId);
            }
        });

        return rootView;
    }

    private void setStationOptions(int buttonId, String stationId) {
        String stationName = Utility.getStationName(getActivity(), stationId);
        switch (buttonId) {
            case R.id.station_button:
                buttonStation.setText(stationName);
                buttonStation.setTag(stationId);
                searchSetting.setStationDepartFrom(stationId);
                break;
            case R.id.depart_from_station_button:
                buttonDepartFromStation.setText(stationName);
                buttonDepartFromStation.setTag(stationId);
                searchSetting.setStationDepartFrom(stationId);
                break;
            case R.id.depart_for_station_button:
                buttonDepartForStation.setText(stationName);
                buttonDepartForStation.setTag(stationId);
                searchSetting.setStationDepartFor(stationId);
                break;
            default:
        }
    }
}

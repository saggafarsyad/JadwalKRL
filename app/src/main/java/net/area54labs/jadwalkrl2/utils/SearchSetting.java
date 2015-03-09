package net.area54labs.jadwalkrl2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.area54labs.jadwalkrl2.R;

import java.util.Calendar;

/**
 * Created by Saggaf on 3/6/2015.
 */
public class SearchSetting {
    private static final String DELIMITER = "-";
    private static final int QUICK_SEARCH = 3;
    private static final int ADVANCED_SEARCH = 4;
    private final int TIME_DIFFERENCE = 2;
    private boolean isAdvanced;
    private String mStationDepartFromId;
    private String mStationDepartForId;
    private int mTimeBottomLimit;
    private int mTimeTopLimit;
    private Context mContext;


    public SearchSetting(Context mContext) {
        this.mContext = mContext;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        String strSearchSetting = prefs.getString(mContext.getString(R.string.pref_search_setting_key), mContext.getString(R.string.pref_search_setting_default));

        String[] searchSetting = strSearchSetting.split(DELIMITER);

        if (searchSetting.length == QUICK_SEARCH) {
            isAdvanced = false;

            mStationDepartFromId = searchSetting[0];
            setTimeRange();
        } else if (searchSetting.length == ADVANCED_SEARCH) {
            isAdvanced = true;

            mStationDepartFromId = searchSetting[0];
            mStationDepartForId = searchSetting[1];
            mTimeBottomLimit = Integer.valueOf(searchSetting[2]);
            mTimeTopLimit = Integer.valueOf(searchSetting[3]);
        }
    }

    public SearchSetting(Context mContext, boolean isAdvanced, String mStationDepartFromId, String mStationDepartForId, int mTimeBottomLimit, int mTimeTopLimit) {

        this.isAdvanced = isAdvanced;
        this.mStationDepartFromId = mStationDepartFromId;
        this.mStationDepartForId = mStationDepartForId;
        this.mTimeBottomLimit = mTimeBottomLimit;
        this.mTimeTopLimit = mTimeTopLimit;
        this.mContext = mContext;
    }

    public SearchSetting(Context context, String strSearchSettings) {
        this.mContext = context;

        String[] searchSetting = strSearchSettings.split(DELIMITER);

        this.mStationDepartFromId = searchSetting[0];

        if (searchSetting.length == QUICK_SEARCH) {
            this.isAdvanced = false;
            this.mTimeBottomLimit = Integer.parseInt(searchSetting[1]);
            this.mTimeTopLimit = Integer.parseInt(searchSetting[2]);
        } else if (searchSetting.length == ADVANCED_SEARCH) {
            this.isAdvanced = true;
            this.mStationDepartForId = searchSetting[1];
            this.mTimeBottomLimit = Integer.parseInt(searchSetting[2]);
            this.mTimeTopLimit = Integer.parseInt(searchSetting[3]);
        }
    }

    @Override
    public String toString() {
        String strSearchString = mStationDepartFromId;

        if (isAdvanced) strSearchString += DELIMITER + mStationDepartForId;

        strSearchString += DELIMITER + mTimeBottomLimit + DELIMITER + mTimeTopLimit;

        return strSearchString;
    }

    public void save() {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(mContext).edit();

        prefs.putString(mContext.getString(R.string.pref_search_setting_key), toString());
        prefs.apply();
    }

    public void switchMode() {
        if (isAdvanced) {
            isAdvanced = false;
            setTimeRange();
        } else {
            isAdvanced = true;

            String[] station = Utility.getStationFromRoute(mContext, Utility.MODE_RANDOM, Utility.NO_ROUTE, mStationDepartFromId);

            mStationDepartForId = station[0];
            setTimeRange();
        }
    }

    public void swapStation() {
        String tmp = mStationDepartFromId;
        mStationDepartFromId = mStationDepartForId;
        mStationDepartForId = tmp;
    }

    // Time range must be between 1 to 24. This method will validate and fix input time range if the time range is not valid
    public void validateTimeRange() {
        if (mTimeBottomLimit == mTimeTopLimit) {
            mTimeTopLimit += TIME_DIFFERENCE;
        } else if (mTimeBottomLimit > mTimeTopLimit) {
            mTimeBottomLimit = mTimeTopLimit - TIME_DIFFERENCE;
        }

        if (mTimeBottomLimit <= 0) {
            mTimeBottomLimit = 1;
        } else if (mTimeBottomLimit == 23) {
            mTimeTopLimit = 24;
            return;
        } else if (mTimeBottomLimit >= 24) {
            mTimeBottomLimit = 23;
            mTimeTopLimit = 24;
            return;
        }

        if (mTimeTopLimit > 24) {
            mTimeTopLimit = 24;
        } else if (mTimeTopLimit <= 0) {
            mTimeTopLimit = mTimeBottomLimit + TIME_DIFFERENCE;
        }
    }

    public void setTimeRange() {
        mTimeBottomLimit = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (mTimeBottomLimit == 0) {
            mTimeBottomLimit = 1;
            mTimeTopLimit = mTimeBottomLimit + TIME_DIFFERENCE;
        } else if (mTimeBottomLimit == 23) {
            mTimeTopLimit = 24;
        } else mTimeTopLimit = mTimeBottomLimit + TIME_DIFFERENCE;
    }

    public boolean isAdvanced() {
        return isAdvanced;
    }

    public void setMode(boolean isAdvanced) {
        this.isAdvanced = isAdvanced;
    }

    public String getStationDepartFrom() {
        return mStationDepartFromId;
    }

    public void setStationDepartFrom(String mStationDepartFrom) {
        this.mStationDepartFromId = mStationDepartFrom;
    }

    public String getStationDepartFor() {
        return mStationDepartForId;
    }

    public void setStationDepartFor(String mStationDepartFor) {
        this.mStationDepartForId = mStationDepartFor;
    }

    public int getTimeBottomLimit() {
        return mTimeBottomLimit;
    }

    public void setTimeBottomLimit(int mTimeBottomLimit) {
        this.mTimeBottomLimit = mTimeBottomLimit;
    }

    public int getTimeTopLimit() {
        return mTimeTopLimit;
    }

    public void setTimeTopLimit(int mTimeTopLimit) {
        this.mTimeTopLimit = mTimeTopLimit;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }
}

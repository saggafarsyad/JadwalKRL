package net.area54labs.jadwalkrl2.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.area54labs.jadwalkrl2.R;
import net.area54labs.jadwalkrl2.utils.Utility;

import static net.area54labs.jadwalkrl2.data.AppContract.RouteEntry;
import static net.area54labs.jadwalkrl2.data.AppContract.ScheduleEntry;

/**
 * Created by Saggaf on 3/7/2015.
 */
public class ScheduleAdapter extends CursorAdapter {
    private boolean isAdvanced;

    public ScheduleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setSearchResultMode(boolean isAdvanced) {
        this.isAdvanced = isAdvanced;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_schedule, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        long timestamp = cursor.getLong(cursor.getColumnIndex(ScheduleEntry.COLUMN_DEPART_TIMESTAMP));

        String departTime = Utility.departTimestampToString(timestamp);
        viewHolder.departTimeView.setText(departTime);

        if (isAdvanced) {
            String unitNo = cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_UNIT_NO));
            String routeName = cursor.getString(cursor.getColumnIndex(RouteEntry.TABLE_NAME + "_" + RouteEntry.COLUMN_NAME));

            viewHolder.textOneView.setText(routeName);
            viewHolder.textTwoView.setText(unitNo);
        } else {
            String departFromStationId = cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_STATION_KEY));

            int routIdCol = cursor.getColumnIndex(ScheduleEntry.COLUMN_ROUTE_KEY);

            int routeId = cursor.getInt(routIdCol);
            String nextStation = Utility.getStationFromRoute(context, Utility.MODE_NEXT, routeId, departFromStationId)[1];
            String lastStation = Utility.getStationFromRoute(context, Utility.MODE_LAST, routeId)[1];

            viewHolder.textOneView.setText(lastStation);
            viewHolder.textTwoView.setText(nextStation);
        }
    }

    public static class ViewHolder {
        public final TextView departTimeView;
        public final TextView textOneView;
        public final TextView textTwoView;

        public ViewHolder(View view) {
            departTimeView = (TextView) view.findViewById(R.id.depart_time_text);
            textOneView = (TextView) view.findViewById(R.id.text_1);
            textTwoView = (TextView) view.findViewById(R.id.text_2);
        }
    }


}

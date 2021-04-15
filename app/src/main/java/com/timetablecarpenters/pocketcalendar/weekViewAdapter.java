package com.timetablecarpenters.pocketcalendar;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class weekViewAdapter extends CursorAdapter {
    public weekViewAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.week_view_list_item, parent, false );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView timeText = (TextView) view.findViewById(R.id.time_text);
        TextView eventText = (TextView) view.findViewById(R.id.event_text);
        String timeTextString;
        String eventEndString;

        timeTextString = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_START));
        eventEndString = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_END));
        if (eventEndString == null || eventEndString.equalsIgnoreCase(timeTextString)) {
            timeTextString = timeTextString + " - " + eventEndString;
        }
        timeText.setText(timeTextString);
        eventText.setText(cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_NAME)));
    }
}

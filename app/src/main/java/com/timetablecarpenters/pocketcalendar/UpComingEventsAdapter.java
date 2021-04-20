package com.timetablecarpenters.pocketcalendar;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.time.Month;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class UpComingEventsAdapter extends CursorAdapter {
    final static int MAX_DAYS = 14;
    Calendar today;

    public UpComingEventsAdapter(Context context, Cursor c, Calendar today) {
        super(context, c, 0);
        this.today = today;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.events_list_item, parent, false );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        View cardViewContents = view.findViewById(R.id.cardview_content);
        TextView dateText = (TextView) cardViewContents.findViewById(R.id.dateText);
        TextView timeText = (TextView) cardViewContents.findViewById(R.id.time_text);
        TextView remainingTimeText = (TextView) cardViewContents.findViewById(R.id.remaining_text);
        TextView eventDescText = (TextView) cardViewContents.findViewById(R.id.event_desc_text);
        ProgressBar progressBar = (ProgressBar) cardViewContents.findViewById(R.id.progressBar);

        String startTime = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_START));
        String endTime = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_END));
        int year = cursor.getInt(cursor.getColumnIndex(DBHelper.YEAR));
        int month = cursor.getInt(cursor.getColumnIndex(DBHelper.MONTH));
        int day = cursor.getInt(cursor.getColumnIndex(DBHelper.DAY));
        String title = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_NAME));
        String eventType = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_TYPE));

        String dateTextString = String.format("%d.%d.%d.", day, month, year);
        dateText.setText(dateTextString);

        String timeTextString = startTime;
        if (startTime.equalsIgnoreCase(endTime))
            dateTextString += " - " + endTime;
        timeText.setText(timeTextString);

        String eventDescTextString = title + " (" + eventType + ")";
        eventDescText.setText(eventDescTextString);

        String remainingTimeTextString;
        Calendar eventDate = Calendar.getInstance();
        eventDate.set(year, month, day, Integer.parseInt(startTime.substring(0, 2)), Integer.parseInt(startTime.substring(3)));
        int minuteDifference = (int) TimeUnit.MINUTES.convert(eventDate.getTime().getTime() - today.getTime().getTime(), TimeUnit.MILLISECONDS);

        if (minuteDifference > 60 * 24 )
            remainingTimeTextString = minuteDifference / (24 * 60) + " Days " + minuteDifference / 60 + " Hours left";
        else if (minuteDifference <= 24 * 60)
            remainingTimeTextString = minuteDifference / 60 + " Hours " + minuteDifference + " Minutes";
        else if (minuteDifference < 60)
            remainingTimeTextString = minuteDifference + " Minutes";
        else
            remainingTimeTextString = "Event Passed!";

        Calendar max = (Calendar) today.clone();
        max.add(Calendar.DATE, MAX_DAYS + 1);
        max.set(Calendar.HOUR, 0);
        max.set(Calendar.MINUTE, 0);
        max.set(Calendar.SECOND, 0);

        int todayToMax =  (int) TimeUnit.MINUTES.convert(max.getTime().getTime() - today.getTime().getTime(), TimeUnit.MILLISECONDS);
        progressBar.setMax(todayToMax);
        progressBar.setProgress(todayToMax - minuteDifference);



    }
}

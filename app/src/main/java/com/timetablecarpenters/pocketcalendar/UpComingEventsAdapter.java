package com.timetablecarpenters.pocketcalendar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import static android.content.Context.MODE_PRIVATE;


import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * CursorAdapter class, takes raw data from cursor and fills the elements of list items within the
 * ListView in UpcomingEvents activity.
 * @version  01.05.2021
 * @author Deniz Mert Dilaverler
 */
public class UpComingEventsAdapter extends CursorAdapter {
    private static final String TAG = "UpComingEventsAdapter";
    public final static int MAX_DAYS = 14;
    public Calendar today;
    public Context context;

    /**
     * Today's date is saved for time comparrasant.
     * super constructor is initialized
     * @param context
     * @param c
     * @param today
     */
    public UpComingEventsAdapter(Context context, Cursor c, Calendar today) {
        super(context, c, 0);
        this.today = today;
        this.context = context;

    }

    /**
     * Inflates an empty list item to be filled with the data from a cursor row
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.events_list_item, parent, false );
    }

    /**
     * Takes data from the cursor and appropriately fills the data within it
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        View cardViewContents = view.findViewById(R.id.cardview_content);

        TextView dateText =  cardViewContents.findViewById(R.id.date_text);
        TextView timeText =  cardViewContents.findViewById(R.id.hour_text);
        //TextView remainingTimeText = cardViewContents.findViewById(R.id.remaining_text);
        TextView eventDescText =  cardViewContents.findViewById(R.id.event_desc_text);
        editParagraphFont(dateText);
        editParagraphFont(timeText);
        //editParagraphFont(remainingTimeText);
        editParagraphFont(eventDescText);
        ProgressBar progressBar = cardViewContents.findViewById(R.id.progressBar);
        RelativeLayout relativeLayout = cardViewContents.findViewById(R.id.upcoming_background);

        String startTime = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_START));
        String endTime = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_END));
        int year = cursor.getInt(cursor.getColumnIndex(DBHelper.YEAR));
        int month = cursor.getInt(cursor.getColumnIndex(DBHelper.MONTH));
        int day = cursor.getInt(cursor.getColumnIndex(DBHelper.DAY));
        String title = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_NAME));
        String eventType = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_TYPE));
        int color = cursor.getInt(cursor.getColumnIndex(DBHelper.COLOR));

        String dateTextString = String.format("%d.%d.%d", day, month + 1, year); //increment month by 1 because the db and the Calendar class stores month values starting from 0
        dateText.setText(dateTextString);

        relativeLayout.setBackgroundColor( CustomizableScreen.getButtonColor());
        dateText.setTextColor( CustomizableScreen.getBackGColor());
        timeText.setTextColor( CustomizableScreen.getBackGColor());
        //remainingTimeText.setTextColor( CustomizableScreen.getBackGColor());
        eventDescText.setTextColor( CustomizableScreen.getBackGColor());
        progressBar.setDrawingCacheBackgroundColor( CustomizableScreen.getBackGColor());

        String timeTextString = startTime;
        if (startTime.equalsIgnoreCase(endTime))
            dateTextString += " - " + endTime;
        timeText.setText(timeTextString);

        String eventDescTextString = title + " (" + eventType + ")";
        eventDescText.setText(eventDescTextString);
        eventDescText.setTextColor(color);

        String remainingTimeTextString;
        Calendar eventDate = Calendar.getInstance();
        eventDate.set(year, month, day, Integer.parseInt(startTime.substring(0, 2)), Integer.parseInt(startTime.substring(3)));
        Calendar eventDateEnd = Calendar.getInstance();

        eventDate.set(year, month, day, Integer.parseInt(endTime.substring(0, 2)), Integer.parseInt(endTime.substring(3)));
        int minuteDifference = (int) TimeUnit.MINUTES.convert(eventDate.getTime().getTime() - today.getTime().getTime(), TimeUnit.MILLISECONDS);
        // int minuteDifferenceEventEnd = (int) TimeUnit.MINUTES.convert(eventDateEnd.getTime().getTime() - today.getTime().getTime(), TimeUnit.MILLISECONDS);

        //formats the "Time remaining" text according to how much time left until the event
        /*
        if (minuteDifference <= 0) {
            if (minuteDifferenceEventEnd > 0)
                remainingTimeTextString = "Event Has Started!";
            else
                remainingTimeTextString = "Event Passed!";
        }
        else if (minuteDifference > 60 * 24 )
            remainingTimeTextString = minuteDifference / (24 * 60) + " Days " + minuteDifference % 24 + " Hours left";
        else if (minuteDifference <= 24 * 60)
            remainingTimeTextString = minuteDifference / 60 + " Hours " + minuteDifference % 60 + " Minutes";
        else if (minuteDifference < 60 )
            remainingTimeTextString = minuteDifference + " Minutes";
        else
            remainingTimeTextString = "";

         */

        //remainingTimeText.setText(remainingTimeTextString);
        Calendar max = (Calendar) today.clone();
        max.add(Calendar.DATE, MAX_DAYS + 1);
        max.set(Calendar.HOUR, 0);
        max.set(Calendar.MINUTE, 0);
        max.set(Calendar.SECOND, 0);

        int todayToMax =  (int) TimeUnit.MINUTES.convert(max.getTime().getTime() - today.getTime().getTime(), TimeUnit.MILLISECONDS);
        progressBar.setMax(todayToMax);
        progressBar.setProgress(todayToMax - minuteDifference);
        Log.d(TAG, "bindView: End");
    }
    /**
     * Edits the font sizes of textViews according to settings
     * It changes the font sizes of paragraphs
     * @param text is the TextView
     */
    public void editParagraphFont(TextView text){
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("paragraphPref", MODE_PRIVATE);
        String paragraphFontSize = sp.getString("paragraphFontSize","");
        if (paragraphFontSize.equals("Small"))
        {
            text.setTextSize(10);
        }
        if (paragraphFontSize.equals("Medium"))
        {
            text.setTextSize(12);
        }
        if (paragraphFontSize.equals("Large"))
        {
            text.setTextSize(14);
        }
    }
}

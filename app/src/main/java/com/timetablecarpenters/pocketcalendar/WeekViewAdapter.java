package com.timetablecarpenters.pocketcalendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Takes a Cursor from the DB and inflates the elements of a list with the data
 * @author Deniz Mert Dilaverler
 * @version 17.04.21
 */
public class WeekViewAdapter extends CursorAdapter {
    public Context context;
    public WeekViewAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.context = context;
    }

    /**
     * inflates an empty list element for the adapter to fill
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.week_view_list_item, parent, false );
    }

    /**
     * extracts the data out of the cursor and fills the inflated listElement with it
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView timeText = (TextView) view.findViewById(R.id.time_text);
        TextView eventText = (TextView) view.findViewById(R.id.event_text);
        String timeTextString;
        String eventEndString;
        int color;

        timeTextString = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_START));
        eventEndString = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_END));
        color = cursor.getInt(cursor.getColumnIndex(DBHelper.COLOR));

        if (eventEndString != null && !eventEndString.equalsIgnoreCase(timeTextString)) {
            timeTextString = timeTextString + " - " + eventEndString;
        }
        timeText.setText(timeTextString);
        timeText.setTextColor(color);
        eventText.setText(cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_NAME)));
        eventText.setTextColor(color);
        editParagraphFont(timeText);
        editParagraphFont(eventText);
    }

    /**
     * Edits the font sizes of textViews according to settings
     * It changes the font sizes of paragraphs
     * @param text is TextWiev that will be changed
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
            text.setTextSize(16);
        }
    }
}

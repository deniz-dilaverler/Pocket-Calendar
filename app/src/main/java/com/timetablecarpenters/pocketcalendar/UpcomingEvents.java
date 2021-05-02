package com.timetablecarpenters.pocketcalendar;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import java.util.Calendar;

/**
 * An activity that shows the user events coming up on the next 14 days
 * @author Deniz Mert Dilaverler
 * @version 01.05.2021
 */
public class UpcomingEvents extends BaseActivity {
    private static final String TAG = "UpcomingEvents";
    public Calendar today;
    public final int TOTAL_DAYS = 14;

    /**
     * calls when the Activity is started, receives data from the db about the events on the next 14 days.
     * Passes the cursor that comes from the DBHelper to UpComingEventsAdapter for the ListView to be inflated
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Startsss");
        setContentView(R.layout.activity_upcoming_events);
        super.onCreate(savedInstanceState);
        //toolbar.setTitle("Upcoming");
        Cursor cursor;
        DBHelper dbHelper;
        today = Calendar.getInstance();
        ListView eventsList = (ListView) findViewById(R.id.events_list);
        dbHelper = new DBHelper(this, DBHelper.DB_NAME, null);
        Calendar maxDay = (Calendar) today.clone();
        maxDay.add(Calendar.DATE, TOTAL_DAYS);
        cursor = dbHelper.getEventsInAnInterval(today, maxDay);

        UpComingEventsAdapter adapter = new UpComingEventsAdapter(this, cursor, today);

        eventsList.setAdapter(adapter);

    }
}
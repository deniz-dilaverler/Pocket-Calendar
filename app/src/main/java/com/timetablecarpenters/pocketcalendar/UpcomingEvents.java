package com.timetablecarpenters.pocketcalendar;

import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ListView;

import java.util.Calendar;

public class UpcomingEvents extends BaseActivity {
    private static final String TAG = "UpcomingEvents";
    Calendar today;
    final int TOTAL_DAYS = 14;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Startsss");
        setContentView(R.layout.activity_upcoming_events);
        super.onCreate(savedInstanceState);
        toolbar.setTitle("Upcoming");
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
package com.timetablecarpenters.pocketcalendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class WeekActivity extends BaseActivity {
    private static final String TAG = "WeekActivity";
    public int[] rowIds = {R.id.monday_row, R.id.tuesday_row, R.id.wednesday_row, R.id.thursday_row, R.id.friday_row, R.id.saturday_row, R.id.sunday_row};
    public Calendar startOfWeek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_week);
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starts" );
        Calendar day;
        Cursor cursor;
        if (startOfWeek != null) {
            startOfWeek = Calendar.getInstance();
            startOfWeek.set(Calendar.DATE, 1);
        }

        day = startOfWeek.getInstance();
        DBHelper dbHelper = new DBHelper(this, "calendar.db", null);
        for(Integer i : rowIds) {
            cursor = dbHelper.getEventsInAnInterval(day.get(Calendar.YEAR), day.get(Calendar.MONTH),
                    day.get(Calendar.DAY_OF_MONTH), day.get(Calendar.DAY_OF_MONTH));
            if (cursor != null ) {
                ((ListView) findViewById(R.id.week_content).findViewById(i).findViewById(R.id.events_of_day_list)).setAdapter(new weekViewAdapter(this, cursor));
            }
            day.add(Calendar.DATE, 1);
        }



    }
}
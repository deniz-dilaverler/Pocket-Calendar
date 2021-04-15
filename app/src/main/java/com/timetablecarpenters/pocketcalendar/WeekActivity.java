package com.timetablecarpenters.pocketcalendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.Nullable;

import java.util.Date;


public class WeekActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);
        SQLiteOpenHelper dbHelper = new DBHelper(this, "calendar.db", null);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Date monday;
        Date tuesday;
        Date wednesday;
        Date thursday;
        Date friday;
        Date saturday;
        Date sunday;

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.week_view_list_item,db.getEventsInInterval() );

    }
}
package com.timetablecarpenters.pocketcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String EVENTS_TABLE = "events_table";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String EVENT_TYPE = "event_type";
    private static final String EVENT_NAME = "event_name";
    private static final String NOTES = "notes";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String NOTIF_TIME = "notification_time";



    // constructor
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, "evemts.db", factory, 1);
    }

    // methods
    // first time DB is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + EVENTS_TABLE + " ( " +     
                YEAR + " INTEGER," +
                MONTH + " INTEGER, " +
                DAY + " INTEGER, " +
                EVENT_TYPE + " TEXT, " +
                EVENT_NAME + " TEXT," +


                LONGITUDE + " REAL," +
                LATITUDE + " REAL," +
                NOTES + " TEXT, " +
                NOTIF_TIME + " TEXT);";
        db.execSQL(createTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
/*
    public boolean addOne(CalenderEvent event) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        cv.put(YEAR, event.getYear());
        cv.put(MONTH, event.getMonth());
        cv.put(DAY, event.getDay());
        cv.put(EVENT_TYPE, event.getType());
        cv.put(EVENT_NAME, event.getName());
        cv.put(NOTES, event.getNotes());
        cv.put(LONGITUDE, event.getLogitude());
        cv.put(LATITUDE, event.getLatitued());
        cv.put(NOTIF_TIME, event.getNotifTime());

        long insert = db.insert(EVENTS_TABLE, null, cv);
        return insert <= 0;
    }
*/
    
}

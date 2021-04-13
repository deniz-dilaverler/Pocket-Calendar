package com.timetablecarpenters.pocketcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    private static final String EVENT_START = "event_start";
    private static final String EVENT_END = "event_end";
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

                EVENT_TYPE + " TEXT, " +
                EVENT_NAME + " TEXT," +
                YEAR + " INTEGER," +
                MONTH + " INTEGER," +
                DAY + " INTEGER," +
                EVENT_START + " TEXT," +
                EVENT_END + " TEXT," +
                LONGITUDE + " REAL," +
                LATITUDE + " REAL," +
                NOTES + " TEXT, " +
                NOTIF_TIME + " TEXT);";
        db.execSQL(createTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }

    /**
     * adds the Data of an event object into the DB and returns the success status of the method as a long value
     * @param event
     * @return row number if event is succesful, -1 if an error has occured, -2 if the event already exists
     */
    public long insertEvent(CalenderEvent event) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        if (checkIsDataAlreadyInDB(event)) {
            cv.put(YEAR, event.getYear());
            cv.put(MONTH, event.getMonth());
            cv.put(DAY, event.getDay());
            cv.put(EVENT_TYPE, event.getType());
            cv.put(EVENT_NAME, event.getName());
            cv.put(NOTES, event.getNotes());
            cv.put(LONGITUDE, event.getLogitude());
            cv.put(LATITUDE, event.getLatitued());
            cv.put(NOTIF_TIME, event.getNotifTime());
            cv.put(EVENT_START, event.getEventStart());
            cv.put(EVENT_END, event.getEventEnd());

            long insert = db.insert(EVENTS_TABLE, null, cv);
            return insert;
        }
        else
            return -2;

    }

    public boolean checkIsDataAlreadyInDB( CalenderEvent event) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "Select * from " + EVENTS_TABLE + " where " + YEAR + " = " + event.getYear() + " AND "
                                                                   + MONTH + " = " + event.getMonth() + " AND "
                                                                   + DAY + " = " + event.getDay() + " AND "
                                                                   + EVENT_NAME + " = " + event.getName() + " AND "
                                                                   + EVENT_START + " = " + event.getStart() + " AND "
                                                                   + EVENT_END + " = " + event.getEnd() + " ;"
                ;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * retrieves all the events within a givin day interval
     * @param year
     * @param month
     * @param dayFrom
     * @param dayTo
     * @return Cursor that houses the data of an event.
     */
    public Cursor getEventsInAnInterval(int year, int month, int dayFrom, int dayTo) {
        String query = "Select * from " + EVENTS_TABLE + " where " + YEAR + " = " + year + " AND "
                + MONTH + " = " + month + " AND "
                + DAY + " BETWEEN "  + dayFrom + " AND " + dayTo
                + " ORDER BY " + DAY + " ;";
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(query, new String[] {"Year", "Month", "Day", "Event Type", "Event Name", "Event Start", "Event End", "Notes", "Latitued", "Longitude", "Notification"});

    }

    
}

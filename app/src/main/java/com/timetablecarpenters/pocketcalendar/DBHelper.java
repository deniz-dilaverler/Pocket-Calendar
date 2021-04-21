package com.timetablecarpenters.pocketcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.time.Month;
import java.util.Calendar;

public class DBHelper extends SQLiteOpenHelper {
    public static String DB_NAME = "events.db";
    private static final String TAG = "DBHelper";

    public static final String EVENTS_TABLE = "events_table";
    public static final String ID = "_id";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String EVENT_TYPE = "event_type";
    public static final String EVENT_NAME = "event_name";
    public static final String EVENT_START = "event_start";
    public static final String EVENT_END = "event_end";
    public static final String NOTES = "notes";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String NOTIF_TIME = "notification_time";




    // constructor
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, 3);
    }

    // methods
    // first time DB is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + EVENTS_TABLE + " ( " +
                ID + " INTEGER, " +
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
/*
    public long insertEvent(CalenderEvent event) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        if (checkIsDataAlreadyInDB(event)) {
            cv.put(ID, event.getId());
            cv.put(YEAR, event.getYear());
            cv.put(MONTH, event.getMonth());
            cv.put(DAY, event.getDay());
            cv.put(EVENT_TYPE, event.getType());
            cv.put(EVENT_NAME, event.getName());
            cv.put(NOTES, event.getNotes());
            cv.put(LONGITUDE, event.getLongitude());
            cv.put(LATITUDE, event.getLatitude());
            cv.put(NOTIF_TIME, event.getNotifTime());
            cv.put(EVENT_START, event.getEventStart());
            cv.put(EVENT_END, event.getEventEnd());

            long insert = db.insert(EVENTS_TABLE, null, cv);
            return insert;
        }
        else
            return -2;

    }

    public boolean checkIsDataAlreadyInDB(CalenderEvent event) {
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

    public boolean deleteEvent(CalendarEvent event) {
        String sqlStatement = "Delete * From " + EVENTS_TABLE + " where " + YEAR + " = " + event.getYear() + " AND "
                                                                        + MONTH + " = " + event.getMonth() + " AND "
                                                                        + DAY + " = " + event.getDay() + " AND "
                                                                        + EVENT_NAME + " = " + event.getName() + " AND "
                                                                        + EVENT_START + " = " + event.getStart() + " AND "
                                                                        + EVENT_END + " = " + event.getEnd() + " ;";
        SQLiteDatabase db = getWritableDatabase();
         if (!checkIsDataAlreadyInDB(event)) {
             return false;
         } else {
             db.execSQL(sqlStatement);
             return true;
         }
    }
*/
    /**
     * retrieves all the events within a given day interval
     * don't pass the dates being 2 months apart
     * @param from
     * @Param to
     * @return Cursor that houses the data of an event.
     */
    public Cursor getEventsInAnInterval(Calendar from, Calendar to) {
        String queryStart = "Select * from " + EVENTS_TABLE + " where ";
        String queryMiddle;

        if (to.getTime().getTime() - from.getTime().getTime() < 0)
            return null;
        // if both are in the same month
        else if (from.get(Calendar.MONTH) == to.get(Calendar.MONTH)) {
            queryMiddle = YEAR + " =  " + to.get(Calendar.YEAR) + " AND "
                + MONTH + " = " + to.get(Calendar.MONTH) + " AND "
                + DAY + " BETWEEN "  + from.get(Calendar.DATE) + " AND " + to.get(Calendar.DATE);
            Log.d(TAG, "getEventsInAnInterval: On the same month");
        }
        // if in consecutive months
        else {
            queryMiddle = YEAR + " =  " + from.get(Calendar.YEAR) + " AND "
                    + MONTH + " = " + from.get(Calendar.MONTH) + " AND "
                    + DAY + " BETWEEN "  + from.get(Calendar.DATE) + " AND " + 31 + " OR " +
                     YEAR + " =  " + to.get(Calendar.YEAR) + " AND "
                    + MONTH + " = " + to.get(Calendar.MONTH) + " AND "
                    + DAY + " BETWEEN "  + 1 + " AND " + to.get(Calendar.DATE);
            Log.d(TAG, "getEventsInAnInterval: Not on the same month");
        }



        String orderStatement = " ORDER BY "
                + YEAR + " ASC, "
                + MONTH + " ASC, "
                + DAY + " ASC, "
                + EVENT_START + " ASC, "
                + EVENT_END + " ASC;" ;
        SQLiteDatabase db = getReadableDatabase();
        String query = queryStart + queryMiddle + orderStatement;
        Log.d(TAG, "getEventsInAnInterval: SQL statement: " + query);

        return db.rawQuery(query, new String[] {});

    }




    
}

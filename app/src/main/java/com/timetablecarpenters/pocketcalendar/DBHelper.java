package com.timetablecarpenters.pocketcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * SQLiteOpenHelper class that creates, manages and resets database.
 * CalendarEvents can be inserted, deleted or extracted
 * @version 30.04.2021
 * @author  Deniz Mert Dilaverler
 */
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
    public static final String COLOR = "color";

    // constructor

    /**
     * creates a db file if a file with the name parameter doesn't exist, otherwise creates the class that interacts with the db
     * with the name passed.
     * @param context
     * @param name
     * @param factory
     */
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, 6);
    }

    /**
     * creates the table and Collumn identities
     * @param db
     */
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
                COLOR + " INTEGER, " +
                NOTIF_TIME + " TEXT);";
        db.execSQL(createTableStatement);

    }

    /**
     * if the database in the device's version no is smaller than the version it is initialised with,
     * onUpgrade is called and resets the db.
     * @param database
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        resetDb(database);
    }

    public void resetDb(SQLiteDatabase db) {
        if (db == null)
            db = getWritableDatabase();
        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE);

        // Create tables again
        onCreate(db);
    }

    /**
     * adds the Data of an event object into the DB and returns the success status of the method as a long value
     * @param event
     * @return row number if event is succesful, -1 if an error has occured, -2 if the event already exists
     */

    public long insertEvent(CalendarEvent event) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();


        if (!checkIsDataAlreadyInDB(event)) {
            cv.put(ID, event.getId());
            cv.put(YEAR, event.getYear());
            cv.put(MONTH, event.getMonth());
            cv.put(DAY, event.getDay());
            cv.put(EVENT_TYPE, event.getType());
            cv.put(EVENT_NAME, event.getName());
            cv.put(EVENT_START, event.getEventStartTime().toString());
            cv.put(EVENT_END, event.getEventEndTime().toString());
            try {
                cv.put(NOTES, event.getNotes());
            } catch (Exception e) {
                Log.e(TAG, "insertEvent: " + e);
                cv.putNull(NOTES);
            }
            try {
                cv.put(COLOR, event.color);

            } catch(Exception e) {
                Log.d(TAG, "insertEvent: no Color value " + e);
                cv.putNull(COLOR);
            }

                LatLng location = event.getLocation();
            if (location != null) {
                cv.put(LATITUDE, location.latitude);
                cv.put(LONGITUDE, location.longitude);
            } else {
                cv.putNull(LONGITUDE);
                cv.putNull(LATITUDE);
            }

            try {
                cv.put(NOTIF_TIME, event.getNotifTime());
            } catch (Exception e) {
                Log.e(TAG, "insertEvent: NotifTime " + e);
            }

            long insert = db.insert(EVENTS_TABLE, null, cv);
            return insert;
        } else
            return -2;
    }

    /**
     * checks wether the event is already in the db
     * Date, name, event start and end are checked, and if there is an event in the db that correspond to these values
     * returns false
     * @param event
     * @returnwether event is already on the db or not
     */
    public boolean checkIsDataAlreadyInDB(CalendarEvent event) {
        SQLiteDatabase db = getReadableDatabase();
        Log.d(TAG, "checkIsDataAlreadyInDB: " + event.getYear() + " " + event.getMonth() + " " + event.getDay());
        String query = "Select * from " + EVENTS_TABLE + " where " + YEAR + " = ?" + " AND "
                                                                   + MONTH + " = ?" + " AND "
                                                                   + DAY + " = ?" + " AND "
                                                                   + EVENT_NAME + " = ?" + " AND "
                                                                   + EVENT_START + " = ?" + " AND "
                                                                   + EVENT_END + " = ?" + " ;"
                ;
        Cursor cursor = db.rawQuery(query, new String[] {event.getYear()+"", event.getMonth()+"", event.getDay()+"", event.getName(), event.getEventStartTime(), event.getEventEndTime() });
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * deletes the event with these values that match:
     * Date, name, event start and end
     * @param event
     * @return
     */
    public boolean deleteEvent(CalendarEvent event) {
        String sqlStatement = "Delete * From " + EVENTS_TABLE + " where " + YEAR + " = " + event.getYear() + " AND "
                                                                        + MONTH + " = " + event.getMonth() + " AND "
                                                                        + DAY + " = " + event.getDay() + " AND "
                                                                        + EVENT_NAME + " = " + event.getName() + " AND "
                                                                        + EVENT_START + " = " + event.getEventStartTime() + " AND "
                                                                        + EVENT_END + " = " + event.getEventEndTime() + " ;";
        SQLiteDatabase db = getWritableDatabase();
         if (!checkIsDataAlreadyInDB(event)) {
             return false;
         } else {
             db.execSQL(sqlStatement);
             return true;
         }
    }

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

    /**
     * Calls the eventsInAnInterval method to retrieve event information in a day Interval.
     * Instead of returning a Cursor, the data is wrapped into the CalendarEvent class and each CalendarEvent is
     * added to the ArrayList to be returned.
     * @param today
     * @param until
     * @return ArrayList of events that are in the specified day interval.
     */
    public ArrayList<CalendarEvent> getEventsInAnIntervalInArray(Calendar today , Calendar until) {

        Cursor cursor = getEventsInAnInterval(today, until);
        ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent>();

        if (cursor.moveToFirst()) {
            do {
                double longitude;
                double latitude;
                String notes;
                int color;
                String notifTime;
                CalendarEvent eventToAdd;
                //TODO: add code for setting the notifTime and Location
                Calendar eventStart = Calendar.getInstance();
                eventStart.set(Calendar.YEAR, cursor.getInt(cursor.getColumnIndex(DBHelper.YEAR)));
                eventStart.set(Calendar.MONTH, cursor.getInt(cursor.getColumnIndex(DBHelper.MONTH)));
                eventStart.set(Calendar.DATE, cursor.getInt(cursor.getColumnIndex(DBHelper.DAY)));
                // times are stored as strings in the db in HH:MM format, this code beneath parses the hour and minutes into an
                // int value and later sets the Calendar class
                try {
                    eventStart.set(Calendar.HOUR_OF_DAY,
                            Integer.parseInt((cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_START))).substring(0, 2)));
                } catch (Exception e) {
                    Log.e(TAG, "onCreate: eventStart setting the hour: " + e);
                }
                try {
                    eventStart.set(Calendar.MINUTE,
                            Integer.parseInt((cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_START))).substring(3)));
                } catch (Exception e) {
                    Log.e(TAG, "onCreate: eventStart setting the minute: " + e);
                }

                Calendar eventEnd = (Calendar) eventStart.clone();

                try {
                    eventEnd.set(Calendar.HOUR_OF_DAY,
                            Integer.parseInt((cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_END))).substring(0, 2)));
                } catch (Exception e) {
                    Log.e(TAG, "onCreate: eventEnd setting the hour: " + e);
                }
                try {
                    eventEnd.set(Calendar.MINUTE,
                            Integer.parseInt((cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_END))).substring(3)));
                } catch (Exception e) {
                    Log.e(TAG, "onCreate: eventEnd setting the minute: " + e);
                }
                String eventName = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_NAME));
                String eventType = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_TYPE));
                int id = cursor.getInt(cursor.getColumnIndex(DBHelper.ID));
                // rest of the properties are not needed for the widget to be shown
                eventToAdd = new CalendarEvent(eventStart, eventEnd, eventName, id, eventType);

               notes = cursor.getString(cursor.getColumnIndex(NOTES));
               eventToAdd.setNotes(notes);
               if (!cursor.isNull(cursor.getColumnIndex(COLOR)))
                   eventToAdd.setColor(cursor.getInt(cursor.getColumnIndex(COLOR)));

               events.add(eventToAdd);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return events;
    }




    
}

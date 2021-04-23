package com.timetablecarpenters.pocketcalendar;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;

import 	java.util.Calendar;


/**
 * @author Yarkın Sakıncı
 * @version 21.04.2021
 */
public class CalendarEvent extends BaseActivity{
    protected int day;
    protected int month;
    protected int year;
    protected String latitude;
    protected String longitude;
    protected String id;
    protected String type;
    protected String name;
    protected String color;
    protected Calendar eventStart;
    protected Calendar eventEnd;
    protected String notifTime;
    protected StringBuffer notes;
    protected String repetitionFrequency;
    protected int repetitionTime;
    protected boolean repeated;
    protected boolean hasNotification;
    private final static String INTENT_KEY = "first_date";
    private static final String TAG = "WeekActivity";
    public int[] rowIds = {R.id.monday_row, R.id.tuesday_row, R.id.wednesday_row, R.id.thursday_row, R.id.friday_row, R.id.saturday_row, R.id.sunday_row};
    public Calendar first;
    TextView dateText;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public CalendarEvent(int day, int month, int year, int startingHour, int startingMinute, String id,
                         String type, String name, int endingHour, int endingMinute, String color,
                         String notifTime, StringBuffer notes, String latitude, String longitude,String repetitionFrequency,
                         int repetitionTime)
    {
        this.day = day;
        this.month = month;
        this.year = year;
        this.id = id;
        this.type = type;
        this.name = name;
        this.color = color;
        eventStart = Calendar.getInstance();
        eventEnd = Calendar.getInstance();
        this.eventStart.set( year, month, day, startingHour, startingMinute, 0);
        this.eventEnd.set( year, month, day, endingHour, endingMinute, 0);
        this.color = color;
        this.notifTime = notifTime;
        this.notes = notes;
        this.latitude = latitude;
        this.longitude = longitude;
        repeated = false;
        if ( repetitionFrequency.equalsIgnoreCase("Monthly")&& repetitionTime > 0)
        {
            repeateMonthly( repetitionTime);
            repeated = true;
        }
        else if ( repetitionFrequency.equalsIgnoreCase("Weekly")&& repetitionTime > 0)
        {
            repeateWeekly( repetitionTime);
            repeated = true;
        }
        else if ( repetitionFrequency.equalsIgnoreCase("Annually")&& repetitionTime > 0)
        {
            repeateAnnually( repetitionTime);
            repeated = true;
        }
        else if ( repetitionFrequency.equalsIgnoreCase("Daily")&& repetitionTime > 0)
        {
            repeateDaily( repetitionTime);
            repeated = true;
        }

    }
    public CalendarEvent(int year, int month, int day, int startingHour, int startingMinute, String id,
                         String type, String name, int endingHour, int endingMinute, String color,
                         String notifTime, StringBuffer notes, String latitude, String longitude)
    {
        this.day = day;
        this.month = month;
        this.year = year;
        this.id = id;
        this.type = type;
        this.name = name;
        this.color = color;
        eventStart = Calendar.getInstance();
        eventEnd = Calendar.getInstance();
        this.eventStart.set( year, month, day, startingHour, startingMinute, 0);
        this.eventEnd.set( year, month, day, endingHour, endingMinute, 0);
        this.color = color;
        this.notifTime = notifTime;
        this.notes = notes;
        this.latitude = latitude;
        this.longitude = longitude;
        repeated = false;
    }
    public CalendarEvent(int day, int month, int year, Calendar eventStart, String id, String type, String name,
                        Calendar eventEnd, String color, String notifTime,StringBuffer notes, String repetitionFrequency,
                         int repetitionTime)
    {
        this.day = day;
        this.month = month;
        this.year = year;
        this.id = id;
        this.type = type;
        this.name = name;
        this.color = color;
        this.eventStart = Calendar.getInstance();
        this.eventEnd = Calendar.getInstance();
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.color = color;
        this.notifTime = notifTime;
        this.notes = notes;

    }
    public CalendarEvent(int day, int month, int year, int dueHour, int dueMinute, String id,
                         String type, String name, String color,
                         String notifTime, StringBuffer notes, String latitude, String longitude, String repetitionFrequency,
                         int repetitionTime)
    {
        this.day = day;
        this.month = month;
        this.year = year;
        this.id = id;
        this.type = type;
        this.name = name;
        this.color = color;
        eventStart = Calendar.getInstance();
        eventEnd = Calendar.getInstance();
        this.eventStart.set( year, month, day, dueHour, dueMinute, 0);
        this.eventEnd.set( year, month, day, dueHour, dueMinute, 0);
        this.color = color;
        this.notifTime = notifTime;
        this.notes = notes;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public CalendarEvent( Calendar eventStart, Calendar eventEnd, String id, String type,String name, String color,
                          String notifTime, StringBuffer notes, String latitude, String longitude)
    {
        this.day = eventStart.get( Calendar.DAY_OF_MONTH);
        this.year = eventStart.get( Calendar.YEAR );
        this.month = eventStart.get( Calendar.MONTH);
        this.id = id;
        this.type = type;
        this.name = name;
        this.color = color;
        this.color = color;
        this.notifTime = notifTime;
        this.notes = notes;
        this.latitude = latitude;
        this.longitude = longitude;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;

    }
    public void repeateMonthly( int i) {


        WeekActivity weekActivity = new WeekActivity();

        DBHelper dbHelper = new DBHelper(weekActivity, DBHelper.DB_NAME, null);
        Calendar nextEventStart = (Calendar) eventStart.clone();
        Calendar nextEventEnd = (Calendar) eventEnd.clone();
        for( int a = 0; a < i; a++)
        {

            nextEventStart.add( Calendar.MONTH, 1);
            nextEventEnd.add( Calendar.MONTH, 1);
            dbHelper.insertEvent( new CalendarEvent(nextEventStart, nextEventEnd,this.id, this.type,this.name,
            this.color, this.notifTime, this.notes,this.latitude,this.longitude));
            
        }
    }
    public void repeateDaily(int i){


    }
    public void repeateWeekly(int i){}
    public void repeateAnnually(int i){}



    public void setNotification() {}

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Calendar getEventStart() {
        return eventStart;
    }

    public void setEventStart(Calendar eventStart) {
        this.eventStart = eventStart;
    }

    public Calendar getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(Calendar eventEnd) {
        this.eventEnd = eventEnd;
    }

    public String getNotifTime() {
        return notifTime;
    }

    public void setNotifTime(String notifTime) {
        this.notifTime = notifTime;
    }

    public StringBuffer getNotes() {
        return notes;
    }

    public void setNotes(StringBuffer notes) {
        this.notes = notes;
    }

    public boolean isHasNotification() {
        return hasNotification;
    }

    public void setHasNotification(boolean hasNotification) {
        this.hasNotification = hasNotification;
    }
}

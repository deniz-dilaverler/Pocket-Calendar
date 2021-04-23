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
 * CalendarEvent class that creates a CalendarEvent instance with the specified properties and methods
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


    /**
     * Inıtialises the properties of the repeated event by taking properties including the int starting hour and int ending hour
     * @param day
     * @param month
     * @param year
     * @param startingHour
     * @param startingMinute
     * @param id
     * @param type
     * @param name
     * @param endingHour
     * @param endingMinute
     * @param color
     * @param notifTime
     * @param notes
     * @param latitude
     * @param longitude
     * @param repetitionFrequency
     * @param repetitionTime
     */
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

        // repeates the activity monthly
        if ( repetitionFrequency.equalsIgnoreCase("Monthly")&& repetitionTime > 0)
        {
            repeateMonthly( repetitionTime);
            repeated = true;
        }

        // repeates the activity weekly
        else if ( repetitionFrequency.equalsIgnoreCase("Weekly")&& repetitionTime > 0)
        {
            repeateWeekly( repetitionTime);
            repeated = true;
        }

        // repeates the activity annually
        else if ( repetitionFrequency.equalsIgnoreCase("Annually")&& repetitionTime > 0)
        {
            repeateAnnually( repetitionTime);
            repeated = true;
        }

        // repeates the activity daily
        else if ( repetitionFrequency.equalsIgnoreCase("Daily")&& repetitionTime > 0)
        {
            repeateDaily( repetitionTime);
            repeated = true;
        }

    }

    /**
     * Inıtialises the properties of the non-repeated event by taking properties including the int starting hour and int ending hour     * @param year
     * @param month
     * @param day
     * @param startingHour
     * @param startingMinute
     * @param id
     * @param type
     * @param name
     * @param endingHour
     * @param endingMinute
     * @param color
     * @param notifTime
     * @param notes
     * @param latitude
     * @param longitude
     */
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

    /**
     * Inıtialises the properties of the repeated event by taking properties including the Calendar type starting hour
     * and Calendar type ending hour
     * @param year
     * @param day
     * @param month
     * @param year
     * @param eventStart
     * @param id
     * @param type
     * @param name
     * @param eventEnd
     * @param color
     * @param notifTime
     * @param notes
     * @param repetitionFrequency
     * @param repetitionTime
     */
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
        // repeates the activity monthly
        if ( repetitionFrequency.equalsIgnoreCase("Monthly")&& repetitionTime > 0)
        {
            repeateMonthly( repetitionTime);
            repeated = true;
        }

        // repeates the activity weekly
        else if ( repetitionFrequency.equalsIgnoreCase("Weekly")&& repetitionTime > 0)
        {
            repeateWeekly( repetitionTime);
            repeated = true;
        }

        // repeates the activity annually
        else if ( repetitionFrequency.equalsIgnoreCase("Annually")&& repetitionTime > 0)
        {
            repeateAnnually( repetitionTime);
            repeated = true;
        }

        // repeates the activity daily
        else if ( repetitionFrequency.equalsIgnoreCase("Daily")&& repetitionTime > 0)
        {
            repeateDaily( repetitionTime);
            repeated = true;
        }

    }

    /**
     * Inıtialises the properties of the repeated event which has an exact due time by taking properties including the
     * int types dueHour and dueMinute
     * @param day
     * @param month
     * @param year
     * @param dueHour
     * @param dueMinute
     * @param id
     * @param type
     * @param name
     * @param color
     * @param notifTime
     * @param notes
     * @param latitude
     * @param longitude
     * @param repetitionFrequency
     * @param repetitionTime
     */
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

        // repeates the activity monthly
        if ( repetitionFrequency.equalsIgnoreCase("Monthly")&& repetitionTime > 0)
        {
            repeateMonthly( repetitionTime);
            repeated = true;
        }

        // repeates the activity weekly
        else if ( repetitionFrequency.equalsIgnoreCase("Weekly")&& repetitionTime > 0)
        {
            repeateWeekly( repetitionTime);
            repeated = true;
        }

        // repeates the activity annually
        else if ( repetitionFrequency.equalsIgnoreCase("Annually")&& repetitionTime > 0)
        {
            repeateAnnually( repetitionTime);
            repeated = true;
        }

        // repeates the activity daily
        else if ( repetitionFrequency.equalsIgnoreCase("Daily")&& repetitionTime > 0)
        {
            repeateDaily( repetitionTime);
            repeated = true;
        }


    }


    /**
     * Inıtialises the properties of the non-repeated event by taking properties including the Calendar type starting hour
     * and Calendar type ending hour
     * @param eventStart
     * @param eventEnd
     * @param id
     * @param type
     * @param name
     * @param color
     * @param notifTime
     * @param notes
     * @param latitude
     * @param longitude
     */
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

    /**
     * adds the desired event for the amount of month specified
     * @param i, months repeated
     */
    public void repeateMonthly( int i) {

        DBHelper dbHelper = new DBHelper(this, DBHelper.DB_NAME, null);
        Calendar nextEventStart = (Calendar) eventStart.clone();
        Calendar nextEventEnd = (Calendar) eventEnd.clone();

        // the events are added to the database
        for( int a = 0; a < i; a++)
        {
            nextEventStart.add( Calendar.MONTH, 1);
            nextEventEnd.add( Calendar.MONTH, 1);
            dbHelper.insertEvent( new CalendarEvent(nextEventStart, nextEventEnd,this.id, this.type,this.name,
            this.color, this.notifTime, this.notes,this.latitude,this.longitude));
        }
    }

    /**
     * adds the desired event for the amount of days specified
     * @param i, days repeated
     */
    public void repeateDaily(int i){
        DBHelper dbHelper = new DBHelper(this, DBHelper.DB_NAME, null);
        Calendar nextEventStart = (Calendar) eventStart.clone();
        Calendar nextEventEnd = (Calendar) eventEnd.clone();

        // the events are added to the database
        for( int a = 0; a < i; a++)
        {
            nextEventStart.add( Calendar.DAY_OF_MONTH, 1);
            nextEventEnd.add( Calendar.DAY_OF_MONTH, 1);
            dbHelper.insertEvent( new CalendarEvent(nextEventStart, nextEventEnd,this.id, this.type,this.name,
                    this.color, this.notifTime, this.notes,this.latitude,this.longitude));
        }

    }

    /**
     * adds the desired event for the amount of weeks specified
     * @param i, weeks repeated
     */
    public void repeateWeekly(int i) {
        DBHelper dbHelper = new DBHelper(this, DBHelper.DB_NAME, null);
        Calendar nextEventStart = (Calendar) eventStart.clone();
        Calendar nextEventEnd = (Calendar) eventEnd.clone();

        // adds the events to the database
        for (int a = 0; a < i; a++) {

            nextEventStart.add(Calendar.DAY_OF_MONTH, 7);
            nextEventEnd.add(Calendar.DAY_OF_MONTH, 7);
            dbHelper.insertEvent(new CalendarEvent(nextEventStart, nextEventEnd, this.id, this.type, this.name,
                    this.color, this.notifTime, this.notes, this.latitude, this.longitude));
        }
    }

    /**
     * adds the desired event for the amount of years specified
     * @param i, years repeated
     */
    public void repeateAnnually(int i) {
        DBHelper dbHelper = new DBHelper(this, DBHelper.DB_NAME, null);
        Calendar nextEventStart = (Calendar) eventStart.clone();
        Calendar nextEventEnd = (Calendar) eventEnd.clone();

        // adds events to the database
        for (int a = 0; a < i; a++) {

            nextEventStart.add(Calendar.YEAR, 1);
            nextEventEnd.add(Calendar.YEAR, 1);
            dbHelper.insertEvent(new CalendarEvent(nextEventStart, nextEventEnd, this.id, this.type, this.name,
                    this.color, this.notifTime, this.notes, this.latitude, this.longitude));
        }
    }

    /**
     * Sets the desired notification
     */
    public void setNotification() {}

    /**
     * returns the day of the calendar event
     * @return day
     */
    public int getDay() {
        return day;
    }

    /**
     * sets the day of the calendar event using the parameter
     * @param day
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * returns the month of the calendar event
     * @return month
     */
    public int getMonth() {
        return month;
    }

    /**
     * sets the month of the calendar event using the parameter
     * @param month
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * returns the year of the calendar event
     * @return year
     */
    public int getYear() {
        return year;
    }

    /**
     * sets the year of the calendar event using the parameter
     * @param year
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * returns the id of the calendar event
     * @return id
     */
    public String getId() {
        return id;
    }


    /**
     * sets the id of the calendar event using the parameter
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * returns the type of the calendar event
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * sets the type of the calendar event using the parameter
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * returns the name of the calendar event
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of the calendar event using the parameter
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns the color of the calendar event
     * @return color
     */
    public String getColor() {
        return color;
    }

    /**
     * sets the color of the calendar event using the parameter
     * @param color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * returns the event start of the calendar event
     * @return eventStart
     */
    public Calendar getEventStart() {
        return eventStart;
    }

    /**
     * sets the event start of the calendar event using the parameter
     * @param eventStart
     */
    public void setEventStart(Calendar eventStart) {
        this.eventStart = eventStart;
    }

    /**
     * returns the event end of the calendar event
     * @return event end
     */
    public Calendar getEventEnd() {
        return eventEnd;
    }

    /**
     * sets the event end of the calendar event using the parameter
     * @param eventEnd
     */
    public void setEventEnd(Calendar eventEnd) {
        this.eventEnd = eventEnd;
    }

    /**
     * returns the notification time of the calendar event
     * @return notifTime
     */
    public String getNotifTime() {
        return notifTime;
    }

    /**
     * sets the notification time of the calendar event using the parameter
     * @param notifTime
     */
    public void setNotifTime(String notifTime) {
        this.notifTime = notifTime;
    }

    /**
     * returns the notes of the calendar event
     * @return notes
     */
    public StringBuffer getNotes() {
        return notes;
    }

    /**
     * sets the notes of the calendar event using the parameter
     * @param notes
     */
    public void setNotes(StringBuffer notes) {
        this.notes = notes;
    }


    /**
     * returns if the calendar event has notification
     * @return hasNotification
     */
    public boolean isHasNotification() {
        return hasNotification;
    }

    /**
     * sets the boolean if the calendar event has notification by using the parameter
     * @param hasNotification
     */
    public void setHasNotification(boolean hasNotification) {
        this.hasNotification = hasNotification;
    }

    /**
     * returns the longitude( x coordinate) of the calendar event
     * @return longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * sets the longitude( x coordinate) of the calendar event using the parameter
     * @param longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * returns the latitude( x coordinate) of the calendar event
     * @return latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * sets the latitude(y coordinate) of the calendar event using the parameter
     * @param latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}

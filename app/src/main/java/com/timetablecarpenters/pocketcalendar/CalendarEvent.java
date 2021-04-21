package com.timetablecarpenters.pocketcalendar;

import android.location.Location;
import 	java.util.Calendar;


/**
 * @author Yarkın Sakıncı
 * @version 21.04.2021
 */
public class CalendarEvent {
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
    protected boolean hasNotification;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public CalendarEvent(int day, int month, int year, int startingHour, int startingMinute, String id,
                         String type, String name, int endingHour, int endingMinute, String color,
                         String notifTime, StringBuffer notes, String latitude, String longitude )
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
        this.eventStart.set( day, month, year, startingHour, startingMinute, 0);
        this.eventEnd.set( day, month, year, endingHour, endingMinute, 0);
        this.color = color;
        this.notifTime = notifTime;
        this.notes = notes;
        this.latitude = latitude;
        this.longitude = longitude;

    }
    public CalendarEvent(int day, int month, int year, Calendar eventStart, String id, String type, String name,
                        Calendar eventEnd, String color, String notifTime,StringBuffer notes)
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
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.color = color;
        this.notifTime = notifTime;
        this.notes = notes;

    }
    public CalendarEvent(int day, int month, int year, int dueHour, int dueMinute, String id,
                         String type, String name, String color,
                         String notifTime, StringBuffer notes, String latitude, String longitude )
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
        this.eventStart.set( day, month, year, dueHour, dueMinute, 0);
        this.eventEnd.set( day, month, year, dueHour, dueMinute, 0);
        this.color = color;
        this.notifTime = notifTime;
        this.notes = notes;
        this.latitude = latitude;
        this.longitude = longitude;

    }


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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setYear(int year) {
        this.year = year;
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

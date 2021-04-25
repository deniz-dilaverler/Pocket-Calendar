package com.timetablecarpenters.pocketcalendar;


import android.location.Location;

import 	java.util.Calendar;

/**
 * CalendarEvent class that creates a CalendarEvent instance with the specified properties and methods
 * @author Yarkın Sakıncı
 * @version 21.04.2021
 */

public class CalendarEvent {
    protected String type;
    protected Calendar eventStart;
    protected Calendar eventEnd;
    protected String name;
    protected Location location;
    protected String notes;
    protected String notifTime;
    protected long id;

    public CalendarEvent(Calendar eventStart, Calendar eventEnd, String name, long id, String type) {
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.name = name;
        this.id = id;
        this.type = type;
    }

    // returns long value depending on the result of operation
    public long saveEvent(DBHelper dbHelper) {
        return dbHelper.insertEvent(this);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotifTime() {
        return notifTime;
    }

    public void setNotifTime(String notifTime) {
        this.notifTime = notifTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDay(){
        return eventStart.get(Calendar.DATE);
    }

    public int getMonth(){
        return eventStart.get(Calendar.MONTH);
    }

    public int getYear(){
        return eventStart.get(Calendar.YEAR);
    }

    public String getEventStartTime() {
        String result;
        int hour = eventStart.get(Calendar.HOUR);
        int minute = eventStart.get(Calendar.MINUTE);

        result = hour+"";
        // making sure that the result is in HH:MM format
        if(hour < 10)
            result = 0 + result;
        if (minute < 10) {
            result = result + ":0" + minute;
        } else {
            result = result + ":" + minute;
        }

        return result;
    }

    public String getEventEndTime() {
        String result;
        int hour = eventEnd.get(Calendar.HOUR);
        int minute = eventEnd.get(Calendar.MINUTE);

        result = hour+"";
        // making sure that the result is in HH:MM format
        if(hour < 10)
            result = 0 + result;
        if (minute < 10) {
            result = result + ":0" + minute;
        } else {
            result = result + ":" + minute;
        }

        return result;
    }


}


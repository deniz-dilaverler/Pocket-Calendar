package com.timetablecarpenters.pocketcalendar;

import android.location.Location;


/**
 * @author Elifsena Öz
 * @version 20.04.2021
 */
public abstract class CalendarEvent {
    protected int day;
    protected int month;
    protected int year;
    protected String id;
    protected String type;
    protected String name;
    protected String color;
    protected String eventStart;
    protected String eventEnd;
    protected String notifTime;
    protected StringBuffer notes;
    protected boolean hasNotification;

    public CalendarEvent() {

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

    public String getEventStart() {
        return eventStart;
    }

    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    public String getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(String eventEnd) {
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

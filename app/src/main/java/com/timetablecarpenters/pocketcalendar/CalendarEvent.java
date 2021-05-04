package com.timetablecarpenters.pocketcalendar;


import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;
import 	java.util.Calendar;

/**
 * CalendarEvent class that creates a CalendarEvent instance with the specified properties and methods
 * implements Parcelable interface to be sent trough Intents
 * @author Yarkın Sakıncı
 * @version 21.04.2021
 */

public class CalendarEvent implements Parcelable {
    protected String type;
    protected Calendar eventStart;
    protected Calendar eventEnd;
    protected String name;
    protected LatLng location;
    protected String notes;
    protected String notifTime;
    protected long id;
    protected int color;

    /**
     * initializes the core values of CalendarEvent
     * @param eventStart
     * @param eventEnd
     * @param name
     * @param id
     * @param type
     */
    public CalendarEvent(Calendar eventStart, Calendar eventEnd, String name, long id, String type) {
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.name = name;
        this.id = id;
        this.type = type;
    }

    /**
     * unwraps the Parcel back to CalendarEvent
     * @param in
     */
    protected CalendarEvent(Parcel in) {
        type = in.readString();
        name = in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
        notes = in.readString();
        notifTime = in.readString();
        id = in.readLong();
        color = in.readInt();
        eventStart = (Calendar) in.readSerializable();
        eventEnd = (Calendar) in.readSerializable();
    }

    /**
     * manages the disassembly of the parcel
     */
    public static final Creator<CalendarEvent> CREATOR = new Creator<CalendarEvent>() {
        @Override
        public CalendarEvent createFromParcel(Parcel in) {
            return new CalendarEvent(in);
        }

        @Override
        public CalendarEvent[] newArray(int size) {
            return new CalendarEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * writes the data of the class to a Parcel object
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(name);
        dest.writeParcelable(location, flags);
        dest.writeString(notes);
        dest.writeString(notifTime);
        dest.writeLong(id);
        dest.writeInt(color);
        dest.writeSerializable(eventStart);
        dest.writeSerializable(eventEnd);
    }

    // returns long value depending on the result of operation
    public long saveEvent(DBHelper dbHelper) {
        return dbHelper.insertEvent(this);
    }


    /**
     * Returns event start
     * @return eventStart
     */
    public Calendar getEventStart() {
        return eventStart;
    }

    /**
     * Sets the event start
     * @param eventStart
     */
    public void setEventStart(Calendar eventStart) {
        this.eventStart = eventStart;
    }

    /**
     * Returns event end
     * @return eventEnd
     */
    public Calendar getEventEnd() {
        return eventEnd;
    }

    /**
     * Sets the event end
     * @param eventEnd
     */
    public void setEventEnd(Calendar eventEnd) {
        this.eventEnd = eventEnd;
    }

    /**
     * Returns event name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the event name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns event location
     * @return location
     */
    public LatLng getLocation() {
        return location;
    }

    /**
     * Sets the event location
     * @param location
     */
    public void setLocation(LatLng location) {
        this.location = location;
    }

    /**
     * Returns event notes
     * @return notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the event notes
     * @param notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns event notification time
     * @return notifTime
     */
    public String getNotifTime() {
        return notifTime;
    }

    /**
     * Sets the event notifTime
     * @param notifTime
     */
    public void setNotifTime(String notifTime) {
        this.notifTime = notifTime;
    }

    /**
     * Returns event id
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the event id
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns event type
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the event type
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns event start day as integer
     * @return eventStart day
     */
    public int getDay(){
        return eventStart.get(Calendar.DATE);
    }

    /**
     * Returns event start month as integer
     * @return eventStart month
     */
    public int getMonth(){
        return eventStart.get(Calendar.MONTH);
    }

    /**
     * Returns event start year as integer
     * @return eventStart year
     */
    public int getYear(){
        return eventStart.get(Calendar.YEAR);
    }


    /**
     * Returns the event start time as a String
     * @return event start time
     */
    public String getEventStartTime() {
        String result;
        int hour = eventStart.get(Calendar.HOUR_OF_DAY);
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

    /**
     * Returns the event end time as a String
     * @return event end time
     */
    public String getEventEndTime() {
        String result;
        int hour = eventEnd.get(Calendar.HOUR_OF_DAY);
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

    /**
     * Sets the event color
     * @param c
     */
    public void setColor(int c) {
        color = c;
    }

    /**
     * Returns event color
     * @return color
     */
    public int getColor() {
        return this.color;
    }

    /**
     * Compares two events in terms of names
     * @param anEvent
     * @return if the events are equal in terms of name
     */
    public boolean equals2( CalendarEvent anEvent) {
        if ( anEvent.getName().equals( this.getName())) {
            return true;
        }
        else {
            return false;
        }
    }
}


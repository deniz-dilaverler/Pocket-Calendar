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

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
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

    //TODO: ADDED BY ALPEREN TEMPORARILY
    public void setColor(int c) {
        color = c;
    }
    public boolean equals2( CalendarEvent anEvent) {
        if ( anEvent.getName().equals( this.getName())) {
            return true;
        }
        else {
            return false;
        }
    }
    public void repeatMonthly(int times,Context context){
        Calendar newEventStart = eventStart;
        Calendar newEventEnd = eventEnd;
        DBHelper dbHelper = new DBHelper(context,"helper",null);
        for (int i = 0; i< times;i++) {
            newEventStart.add(Calendar.MONTH,1);
            newEventEnd.add(Calendar.MONTH,1);
            dbHelper.insertEvent( new CalendarEvent(newEventStart,newEventEnd, this.name,this.id, this.type));
        }
    }
    public void repeatWeekly(int times,Context context){
        Calendar newEventStart = eventStart;
        Calendar newEventEnd = eventEnd;
        DBHelper dbHelper = new DBHelper(context,"helper",null);
        for (int i = 0; i< times;i++) {
            newEventStart.add(Calendar.DAY_OF_MONTH,7);
            newEventEnd.add(Calendar.DAY_OF_MONTH,7);
            dbHelper.insertEvent( new CalendarEvent(newEventStart,newEventEnd, this.name,this.id, this.type));
        }

    }
    public void repeatDaily(int times, Context context){
        Calendar newEventStart = eventStart;
        Calendar newEventEnd = eventEnd;
        DBHelper dbHelper = new DBHelper(context,"helper",null);
        for (int i = 0; i< times;i++) {
            newEventStart.add(Calendar.DAY_OF_MONTH,1);
            newEventEnd.add(Calendar.DAY_OF_MONTH,1);
            dbHelper.insertEvent( new CalendarEvent(newEventStart,newEventEnd, this.name,this.id, this.type));
        }
    }
    public void repeatAnnually(int times,Context context){
        Calendar newEventStart = eventStart;
        Calendar newEventEnd = eventEnd;
        DBHelper dbHelper = new DBHelper(context,"helper",null);
        for (int i = 0; i< times;i++) {
            newEventStart.add(Calendar.YEAR,1);
            newEventEnd.add(Calendar.YEAR,1);
            dbHelper.insertEvent( new CalendarEvent(newEventStart,newEventEnd, this.name,this.id, this.type));
        }
    }


}


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
        this.eventStart.set( day, month, year, dueHour, dueMinute, 0);
        this.eventEnd.set( day, month, year, dueHour, dueMinute, 0);
        this.color = color;
        this.notifTime = notifTime;
        this.notes = notes;
        this.latitude = latitude;
        this.longitude = longitude;

    }
    public void repeateMonthly( int i) {
        Log.d(TAG, "onCreate: Starts" );
        setContentView(R.layout.activity_week);
        super.onCreate(savedInstanceState);
        Calendar day;
        Calendar last;
        Calendar today;
        Cursor cursor;
        String dateString;
        Bundle extras;

        extras = getIntent().getExtras();
        if (extras != null)
            first = (Calendar) extras.get(INTENT_KEY);
        View content = findViewById(R.id.week_content);
        if (first == null) {
            Log.d(TAG, "onCreate: SA" );
            // set the date
            today = Calendar.getInstance();
            today.setFirstDayOfWeek(Calendar.MONDAY);
            // "calculate" the start date of the week
            first = (Calendar) today.clone();
            first.add(Calendar.DAY_OF_WEEK,
                    first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        }
        // and add six days to the end date
        last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, 6);

        dateText = (TextView) findViewById(R.id.dateText);
        dateString = monthNames[first.get(Calendar.MONTH)] + " " + first.get(Calendar.DATE) + "  -  " +
                monthNames[last.get(Calendar.MONTH)] + " " + last.get(Calendar.DATE);
        dateText.setText(dateString);
        WeekActivity weekActivity = new WeekActivity();

        day = (Calendar) first.clone();
        DBHelper dbHelper = new DBHelper(weekActivity, DBHelper.DB_NAME, null);
        for(int i1 = 0 ; i1 < 7 ; i1++) {
            View row = content.findViewById(rowIds[i1]);
            ((TextView) row.findViewById(R.id.text_date_name)).setText(dateNames[i1]);
            Log.d(TAG, "onCreate: day = " + day.get(Calendar.YEAR)+ " " + day.get(Calendar.MONTH)+ " " + day.get(Calendar.DAY_OF_MONTH));
            cursor = dbHelper.getEventsInAnInterval(day, day);
            // check if there are any events on that day
            if (cursor.getColumnCount() > 0) {
                ListView list = row.findViewById(R.id.events_of_day_list);
                list.setAdapter(new WeekViewAdapter(weekActivity, cursor));
                list.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return onTouchEvent(event);
                    }

                });
            }
                day.add(Calendar.DATE, 1);
            /*
            Calendar nextEventStart = eventStart.clone();
            Calendar nextEventEnd = eventEnd.clone();
            nextEventStart.add( Calendar.MONTH, 1);
            nextEventEnd.add( Calendar.MONTH, 1);

             */
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

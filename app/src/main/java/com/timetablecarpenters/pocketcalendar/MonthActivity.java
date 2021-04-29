package com.timetablecarpenters.pocketcalendar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Calendar;


public class MonthActivity extends BaseActivity {
    private View[] weeks, dayInWeek;
    private CalendarEvent[] events;
    private CalendarEvent[][] eventsInDays;
    private Calendar today;
    private DBHelper database;

    private static final String TAG = "MonthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_month);
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);

        database = new DBHelper(this, DBHelper.DB_NAME, null);

        /*
        if ( extras != null) {
            thisDay = (Calendar) extras.get(INTENT_KEY);
        }
        if (thisDay == null) {
            Log.d(TAG, "onCreate: SA" );
            thisDay = Calendar.getInstance();
        }
        TextView date = findViewById( R.id.dateText);
        date.setText( thisDay.get( Calendar.YEAR) + " " + formattedMonth( thisDay.get(
                Calendar.MONTH)) + " " + thisDay.get( Calendar.DAY_OF_MONTH));
        */
        eventsInDays = new CalendarEvent[42][];
        today = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DATE, 1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.DATE, calendar1.getActualMaximum(Calendar.DATE));
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        pullEvents( database.getEventsInAnIntervalInArray( calendar1, calendar2));

        initiateWeeks();


        View week1 = findViewById(R.id.week1_row);
        View day1 = week1.findViewById(R.id.monday_box);
        day1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = day1.findViewById(R.id.text_date_name);
                text.setText("Boom");
            }
        });


    }

    private void initiateWeeks() {
        weeks = new View[6];
        weeks[0] = findViewById(R.id.week1_row);
        weeks[1] = findViewById(R.id.week2_row);
        weeks[2] = findViewById(R.id.week3_row);
        weeks[3] = findViewById(R.id.week4_row);
        weeks[4] = findViewById(R.id.week5_row);
        weeks[5] = findViewById(R.id.week6_row);
        for (int i = 0; i < 6; i++) {
            initiateDayInWeek(weeks[i], i + 1);
        }
    }

    private void initiateDayInWeek(View aWeek, int week) { // week 1 to 6
        dayInWeek = new View[7];
        dayInWeek[0] = aWeek.findViewById(R.id.monday_box);
        dayInWeek[1] = aWeek.findViewById(R.id.tuesday_box);
        dayInWeek[2] = aWeek.findViewById(R.id.wednesday_box);
        dayInWeek[3] = aWeek.findViewById(R.id.thursday_box);
        dayInWeek[4] = aWeek.findViewById(R.id.friday_box);
        dayInWeek[5] = aWeek.findViewById(R.id.saturday_box);
        dayInWeek[6] = aWeek.findViewById(R.id.sunday_box);
        for (int i = 0; i < 7; i++) {
            dayCreate(dayInWeek[i], i + 1, week);
        }
    }

    private void dayCreate(View day, int dayNo, int week) { //dayNo 1 to 7
        TextView date = day.findViewById( R.id.text_date_name);
        int dayCount;
        /*
        int eventCount;
        if ( eventsInDays[week * 7 - 7 + dayNo] != null) {
            eventCount = eventsInDays[week * 7 - 7 + dayNo].length;
            date.setText( eventCount + "Events");
        }
        else {
            date.setText( "Empty");
        }
        */
        Calendar c = Calendar.getInstance();
        c.set( Calendar.DATE, 1);
        dayCount = week * 7 - 6 + dayNo - dayCorrector( c.get( Calendar.DAY_OF_WEEK));
        date.setText( "" + dayCount + " ");
    }

    private void pullEvents( ArrayList<CalendarEvent> dBEvents) {
        int counter;
        int firstDayOfMonth;
        Calendar cal;
        counter = 0;
        for (int i = 0; i < dBEvents.size(); i++) {
            if (dBEvents.get(i) != null) {
                counter++;
            }
        }
        events = new CalendarEvent[counter];
        counter = 0;
        for (int i = 0; i < dBEvents.size(); i++) {
            if ( dBEvents.get(i) != null && dBEvents.get(i).getEventStart().get(Calendar.MONTH) == today.get(Calendar.MONTH)) {
                events[counter] = dBEvents.get(i);
                counter++;
                System.out.println("  ---   WORKING  ---   BBBBBBBBBBBBBBBBBBBBBBB " + counter + " " );
            }
        }
        cal = today;
        cal.set(Calendar.DATE, 1);
        firstDayOfMonth = dayCorrector( cal.get( Calendar.DAY_OF_WEEK));
        for (int i = 0; i < today.getActualMaximum( Calendar.DAY_OF_MONTH); i++) {
            eventsInDays[ i + firstDayOfMonth] = new CalendarEvent[( pullEventsOfDay( dBEvents, i + 1).length)];
            System.out.println( ( "  ---   WORKING  ---   BBBBBBBBBBBBBBBBBBBBBBB " + 1 + i + " AAAAAAAAA " + pullEventsOfDay( dBEvents, i + 1).length));
            eventsInDays[i + firstDayOfMonth] = pullEventsOfDay( dBEvents, i + 1);
        }
    }

    public int dayCorrector(int a) {
        if (a == 1) {
            return 7;
        } else {
            return a - 1;
        }
    }
    /**
     * gets today's events and puts them all in an array as events property
     * @author Alperen Utku Yalçın
     */
    private CalendarEvent[] pullEventsOfDay( ArrayList<CalendarEvent> calEvents, int day) {
        int counter;
        counter = 0;
        CalendarEvent[] dayEvents;
        for ( int i = 0; i < calEvents.size(); i++) {
            if ( calEvents.get( i) != null && calEvents.get( i).getEventStart().get( Calendar.DATE) == day) {
                counter++;
            }
        }
        dayEvents = new CalendarEvent[ counter];
        counter = 0;
        for ( int i = 0; i < calEvents.size(); i++) {
            if ( calEvents.get( i) != null && calEvents.get(i).getEventStart().get( Calendar.DATE) == day ) {
                dayEvents[counter] = calEvents.get( i);
                counter++;
            }
        }
        return dayEvents;
    }


















}
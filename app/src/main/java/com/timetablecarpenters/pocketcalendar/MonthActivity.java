package com.timetablecarpenters.pocketcalendar;

import android.annotation.SuppressLint;
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
    private final static String INTENT_KEY = "today_date";

    private static final String TAG = "MonthActivity";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_month);
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        Bundle extras;

        database = new DBHelper(this, DBHelper.DB_NAME, null);


        extras = getIntent().getExtras();
        if ( extras != null) {
            today = (Calendar) extras.get(INTENT_KEY);
        }
        if (today == null) {
            Log.d(TAG, "onCreate: SA" );
            today = Calendar.getInstance();
        }
        TextView date = findViewById( R.id.month_content).findViewById( R.id.dateText);
        date.setText( today.get( Calendar.YEAR) + " " + formattedMonth( today.get(
                Calendar.MONTH)));

        eventsInDays = new CalendarEvent[42][];
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set( Calendar.MONTH, today.get( Calendar.MONTH));
        calendar1.set( Calendar.YEAR, today.get( Calendar.YEAR));
        calendar1.set( Calendar.DATE, today.get( Calendar.DATE));
        calendar1.set(Calendar.DATE, 0);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        Calendar calendar2 = calendar1;
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
    /**
     * Reports the name of the month according to the number
     * @param month number
     * @return Sring month name
     */
    private String formattedMonth(int month) {
        if (month == 0)
            return "Jan";
        if (month == 1)
            return "Feb";
        if (month == 2)
            return "Mar";
        if (month == 3)
            return "Apr";
        if (month == 4)
            return "May";
        if (month == 5)
            return "Jun";
        if (month == 6)
            return "Jul";
        if (month == 7)
            return "Aug";
        if (month == 8)
            return "Sep";
        if (month == 9)
            return "Oct";
        if (month == 10)
            return "Nov";

        // For 11th month and if anything goes wrong
        return "Dec";
    }

    private void initiateWeeks() {
        weeks = new View[6];
        int count;
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.MONTH, today.get( Calendar.MONTH));
        cal.set( Calendar.YEAR, today.get( Calendar.YEAR));
        cal.set( Calendar.DATE, 0);
        count = dayCorrector( cal.get( Calendar.DAY_OF_WEEK));
        System.out.println( "AAAAAAAAAAAAAAAAAAAAA" + count);
        weeks[0] = findViewById(R.id.week1_row);
        cal.add( Calendar.DATE, - count);
        initiateDayInWeek( weeks[0], cal);
        weeks[1] = findViewById(R.id.week2_row);
        //cal.add( Calendar.DATE,  7);
        initiateDayInWeek( weeks[1], cal);
        weeks[2] = findViewById(R.id.week3_row);
        //cal.add( Calendar.DATE,  7);
        initiateDayInWeek( weeks[2], cal);
        weeks[3] = findViewById(R.id.week4_row);
        //cal.add( Calendar.DATE,  7);
        initiateDayInWeek( weeks[3], cal);
        weeks[4] = findViewById(R.id.week5_row);
        //cal.add( Calendar.DATE,  7);
        initiateDayInWeek( weeks[4], cal);
        weeks[5] = findViewById(R.id.week6_row);
        //cal.add( Calendar.DATE,  7);
        initiateDayInWeek( weeks[5], cal);
    }

    private void initiateDayInWeek(View aWeek, Calendar cal) { // week 1 to 6
        dayInWeek = new View[7];
        dayInWeek[0] = aWeek.findViewById(R.id.monday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[0], cal);
        dayInWeek[1] = aWeek.findViewById(R.id.tuesday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[1], cal);
        dayInWeek[2] = aWeek.findViewById(R.id.wednesday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[2], cal);
        dayInWeek[3] = aWeek.findViewById(R.id.thursday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[3], cal);
        dayInWeek[4] = aWeek.findViewById(R.id.friday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[4], cal);
        dayInWeek[5] = aWeek.findViewById(R.id.saturday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[5], cal);
        dayInWeek[6] = aWeek.findViewById(R.id.sunday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[6], cal);
    }

    @SuppressLint("SetTextI18n")
    private void dayCreate(View day, Calendar cal) { //dayNo 1 to 7
        TextView date = day.findViewById( R.id.text_date_name);

        date.setText( cal.get(Calendar.DATE));
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
        cal = Calendar.getInstance();
        cal.set( Calendar.MONTH, today.get( Calendar.MONTH));
        cal.set( Calendar.YEAR, today.get( Calendar.YEAR));
        cal.set( Calendar.DATE, today.get( Calendar.DATE));
        cal.set(Calendar.DATE, 1);
        firstDayOfMonth = dayCorrector( cal.get( Calendar.DAY_OF_WEEK));
        for (int i = 0; i < today.getActualMaximum( Calendar.DAY_OF_MONTH); i++) {
            eventsInDays[ i + firstDayOfMonth] = new CalendarEvent[( pullEventsOfDay( dBEvents, i + 1).length)];
            System.out.println( ( "  ---   WORKING  ---   BBBBBBBBBBBBBBBBBBBBBBB " + 1 + i + "" +
                    " AAAAAAAAA " + pullEventsOfDay( dBEvents, i + 1).length));
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
    /**
     *
     */
    @Override
    public void leftSwipe() {
        Log.d(TAG, "leftSwipe: MonthActivity");
        super.leftSwipe();
        finish();
        Intent intent = new Intent(this, MonthActivity.class);
        today.add( Calendar.MONTH, 1);
        intent.putExtra( INTENT_KEY, today);
        startActivity(intent);
        overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     *
     */
    @Override
    public void rightSwipe() {
        Log.d(TAG, "rightSwipe: MonthActivity");
        super.rightSwipe();
        finish();
        Intent intent = new Intent(this, MonthActivity.class);
        today.add( Calendar.MONTH, -1);
        intent.putExtra( INTENT_KEY, today);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

















}
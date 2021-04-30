package com.timetablecarpenters.pocketcalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.timetablecarpenters.pocketcalendar.BaseActivity;
import com.timetablecarpenters.pocketcalendar.CalendarEvent;
import com.timetablecarpenters.pocketcalendar.DBHelper;
import com.timetablecarpenters.pocketcalendar.R;
import com.timetablecarpenters.pocketcalendar.R.color;

import java.util.ArrayList;
import java.util.Calendar;

import static com.timetablecarpenters.pocketcalendar.R.color.*;


public class MonthActivity extends BaseActivity {
    private View[] weeks, dayInWeek;
    private CalendarEvent[] events;
    private CalendarEvent[][] eventsInDays;
    private Calendar today;
    private DBHelper database;
    private final static String INTENT_KEY = "today_date";
    private CalendarEvent defaultEvent;
    private CalendarEvent[][] eventCountDays;
    private boolean[] isSpareDay;
    private static final String TAG = "MonthActivity";
    Intent intent;
    private Context context;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_month);
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        Bundle extras;
        context = this;
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
        calendar1.set(Calendar.DATE, today.getActualMinimum( Calendar.DATE));
        calendar1.set(Calendar.HOUR_OF_DAY, calendar1.getActualMinimum( Calendar.HOUR_OF_DAY));
        calendar1.set(Calendar.MINUTE, calendar1.getActualMinimum( Calendar.MINUTE));
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set( Calendar.MONTH, today.get( Calendar.MONTH));
        calendar2.set( Calendar.YEAR, today.get( Calendar.YEAR));
        calendar2.set(Calendar.DATE, today.getActualMaximum(Calendar.DATE));
        calendar2.set(Calendar.HOUR_OF_DAY, calendar2.getActualMaximum( Calendar.HOUR_OF_DAY));
        calendar2.set(Calendar.MINUTE, calendar2.getActualMaximum( Calendar.MINUTE));

        insertEvents();
        pullEvents( database.getEventsInAnIntervalInArray( calendar1, calendar2));
        initiateWeeks();




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
        int dayCount = 0;
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.MONTH, today.get( Calendar.MONTH));
        cal.set( Calendar.YEAR, today.get( Calendar.YEAR));
        cal.set( Calendar.DATE, 0);
        count = dayCorrector( cal.get( Calendar.DAY_OF_WEEK));
        weeks[0] = findViewById(R.id.week1_row);
        cal.add( Calendar.DATE, - ( count % 7));
        initiateDayInWeek( weeks[0], cal, dayCount);
        weeks[1] = findViewById(R.id.week2_row);
        dayCount += 7;
        initiateDayInWeek( weeks[1], cal, dayCount);
        weeks[2] = findViewById(R.id.week3_row);
        dayCount += 7;
        initiateDayInWeek( weeks[2], cal, dayCount);
        weeks[3] = findViewById(R.id.week4_row);
        dayCount += 7;
        initiateDayInWeek( weeks[3], cal, dayCount);
        weeks[4] = findViewById(R.id.week5_row);
        dayCount += 7;
        initiateDayInWeek( weeks[4], cal, dayCount);
        weeks[5] = findViewById(R.id.week6_row);
        dayCount += 7;
        initiateDayInWeek( weeks[5], cal, dayCount);
    }

    private void initiateDayInWeek(View aWeek, Calendar cal , int dayCount) { // week 1 to 6
        dayInWeek = new View[7];
        dayInWeek[0] = aWeek.findViewById(R.id.monday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[0], cal, dayCount);
        dayCount ++;
        dayInWeek[1] = aWeek.findViewById(R.id.tuesday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[1], cal, dayCount);
        dayCount ++;
        dayInWeek[2] = aWeek.findViewById(R.id.wednesday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[2], cal, dayCount);
        dayCount ++;
        dayInWeek[3] = aWeek.findViewById(R.id.thursday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[3], cal, dayCount);
        dayCount ++;
        dayInWeek[4] = aWeek.findViewById(R.id.friday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[4], cal, dayCount);
        dayCount ++;
        dayInWeek[5] = aWeek.findViewById(R.id.saturday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[5], cal, dayCount);
        dayCount ++;
        dayInWeek[6] = aWeek.findViewById(R.id.sunday_box);
        cal.add( Calendar.DATE,1);
        dayCreate( dayInWeek[6], cal, dayCount);
    }


    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void dayCreate(View day, Calendar cal, int dayCount) { //dayNo 1 to 7
        TextView date = day.findViewById( R.id.text_date_name);
        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.MONTH, cal.get( Calendar.MONTH));
        calendar.set( Calendar.YEAR, cal.get( Calendar.YEAR));
        calendar.set( Calendar.DATE, cal.get( Calendar.DATE));
        calendar.set( Calendar.HOUR_OF_DAY, cal.get( Calendar.HOUR_OF_DAY));
        calendar.set( Calendar.MINUTE, cal.get( Calendar.MINUTE));
        int i;
        if ( eventCountDays[ dayCount] != null) {
            i = eventCountDays[ dayCount].length;
        }
        else {
            i = 0;
        }
        String str = "" + calendar.get( Calendar.DATE) + "." + i;

        if ( isSpareDay[dayCount]) {
            str = "";
            date.setBackgroundColor( ResourcesCompat.getColor(getResources(), R.color.month_activity_beckground_unusable, null)); //with theme));//"#5481A4"));
            date.setText( str );
        }
        else {
            // Put the circles.

            if ( eventCountDays[ dayCount] != null) {
                //addCircles( eventCountDays[dayCount], day);
            }



            date.setText( str );

            day.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    intent = new Intent( context, DayActivity.class);
                    intent.putExtra( INTENT_KEY, calendar);
                    startActivity(intent);
                    finish();
                }
            });
        }


    }
    private void addCircles( CalendarEvent[] events, View day) {
        RelativeLayout rl = day.findViewById( R.id.rlC);
        if ( events == null) {
            return;
        }
        else if ( events.length == 1) {
            ImageView pic = rl.findViewById( R.id.event_circle1);
            //rl.( pic);
        }
    }

    private void pullEvents( ArrayList<CalendarEvent> dBEvents) {
        int counter;
        int firstDayOfMonth;
        Calendar cal;
        counter = 0;
        eventCountDays = new CalendarEvent[42][];
        isSpareDay = new boolean[42];
        for ( int i = 0; i < 42; i++) {
            //eventCountDays[i] = 0;
            isSpareDay[i] = true;
        }

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
            }
        }
        cal = Calendar.getInstance();
        cal.set( Calendar.MONTH, today.get( Calendar.MONTH));
        cal.set( Calendar.YEAR, today.get( Calendar.YEAR));
        cal.set(Calendar.DATE, 0);
        firstDayOfMonth = ( dayCorrector( cal.get( Calendar.DAY_OF_WEEK)) % 7);
        for (int i = 0; i < today.getActualMaximum( Calendar.DAY_OF_MONTH); i++) {
            eventCountDays[i+ firstDayOfMonth] = new CalendarEvent[ pullEventsOfDay( dBEvents, i + 1).length];
            eventCountDays[i + firstDayOfMonth] = pullEventsOfDay( dBEvents, i + 1);
            isSpareDay[i + firstDayOfMonth] = false;
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
    /*
    private  void createDefault() {
        CalendarEvent defaultEvent;
        Calendar calendar11 = Calendar.getInstance();
        Calendar calendar12 = Calendar.getInstance();
        calendar11.set( Calendar.MONTH, today.get( Calendar.MONTH));
        calendar11.set( Calendar.YEAR, today.get( Calendar.YEAR));
        calendar11.set( Calendar.DATE, today.get( Calendar.DATE));
        calendar11.set( Calendar.HOUR_OF_DAY , 0);
        calendar11.set( Calendar.MINUTE , 1);

        calendar12.set( Calendar.MONTH, today.get( Calendar.MONTH));
        calendar12.set( Calendar.YEAR, today.get( Calendar.YEAR));
        calendar12.set( Calendar.DATE, today.get( Calendar.DATE));
        calendar12.set( Calendar.HOUR_OF_DAY , 0);
        calendar12.set( Calendar.MINUTE , 10);
        defaultEvent = new CalendarEvent( calendar11, calendar12, "Default", 123456789, "None");
        defaultEvent.setNotes( "None");
        defaultEvent.setColor( Color.MAGENTA);
    }
     */
    @Override
    public void leftSwipe() {
        Log.d(TAG, "leftSwipe: MonthActivity");
        super.leftSwipe();
        Intent intent = new Intent(this, MonthActivity.class);
        today.add( Calendar.MONTH, 1);
        intent.putExtra( INTENT_KEY, today);
        startActivity(intent);
        overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    /**
     *
     */
    @Override
    public void rightSwipe() {
        Log.d(TAG, "rightSwipe: MonthActivity");
        super.rightSwipe();
        Intent intent = new Intent(this, MonthActivity.class);
        today.add( Calendar.MONTH, -1);
        intent.putExtra( INTENT_KEY, today);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void insertEvents() {
        Calendar calendar3 = Calendar.getInstance();
        Calendar calendar4 = Calendar.getInstance();
        calendar3.set( Calendar.MONTH, 3);
        calendar3.set( Calendar.YEAR, 2021);
        calendar3.set( Calendar.DATE, 20);
        calendar4.set( Calendar.MONTH, calendar3.get( Calendar.MONTH));
        calendar4.set( Calendar.YEAR, calendar3.get( Calendar.YEAR));
        calendar4.set( Calendar.DATE, calendar3.get( Calendar.DATE));
        calendar3.set( Calendar.HOUR_OF_DAY , 10);
        calendar3.set( Calendar.MINUTE , 30);
        calendar4.set( Calendar.HOUR_OF_DAY , 13);
        calendar4.set( Calendar.MINUTE , 33);
        CalendarEvent cal1 = new CalendarEvent( calendar3, calendar4, "You Have A Meeting" + calendar3.get( Calendar.MINUTE), 10, "Meeting");
        cal1.setColor( Color.RED);
        database.insertEvent( cal1);

        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar.set( Calendar.HOUR_OF_DAY , 0);
        calendar.set( Calendar.MINUTE , 1);
        calendar2.set( Calendar.HOUR_OF_DAY , 2);
        calendar2.set( Calendar.MINUTE , 3);
        CalendarEvent cal2=  new CalendarEvent( calendar, calendar2, "You Have A Meeting"+ calendar.get( Calendar.MINUTE), 11, "Meeting");
        cal2.setColor( Color.BLUE);
        database.insertEvent( cal2);

        Calendar calendar5 = Calendar.getInstance();
        Calendar calendar6 = Calendar.getInstance();
        calendar5.set( Calendar.HOUR_OF_DAY , 2);
        calendar5.set( Calendar.MINUTE , 1);
        calendar6.set( Calendar.HOUR_OF_DAY , 2);
        calendar6.set( Calendar.MINUTE , 3);
        CalendarEvent cal3 =  new CalendarEvent( calendar5, calendar6, "Bruh", 12, "Meeting");
        cal3.setColor( Color.DKGRAY);
        database.insertEvent( cal3);

        Calendar calendar7 = Calendar.getInstance();
        Calendar calendar8 = Calendar.getInstance();
        calendar7.set( Calendar.HOUR_OF_DAY , 0);
        calendar7.set( Calendar.MINUTE , 1);
        calendar8.set( Calendar.HOUR_OF_DAY , 0);
        calendar8.set( Calendar.MINUTE , 3);
        CalendarEvent cal4 =  new CalendarEvent( calendar7, calendar8, "You Have A Meeting Too", 13, "Meeting");
        cal4.setColor( Color.GREEN);
        database.insertEvent( cal4);

        Calendar calendar9 = Calendar.getInstance();
        Calendar calendar10 = Calendar.getInstance();
        calendar9.set( Calendar.HOUR_OF_DAY , 11);
        calendar9.set( Calendar.MINUTE , 10);
        calendar10.set( Calendar.HOUR_OF_DAY , 11);
        calendar10.set( Calendar.MINUTE , 20);
        CalendarEvent cal5 =  new CalendarEvent( calendar9, calendar10, "You Have A Meeting Too" + calendar9.get( Calendar.MINUTE), 14, "Meeting");
        cal5.setColor( Color.BLUE);
        database.insertEvent( cal5);

        Calendar calendar11 = Calendar.getInstance();
        Calendar calendar12 = Calendar.getInstance();
        calendar11.set( Calendar.HOUR_OF_DAY , 20);
        calendar11.set( Calendar.MINUTE , 1);
        calendar12.set( Calendar.HOUR_OF_DAY , 21);
        calendar12.set( Calendar.MINUTE , 50);
        CalendarEvent cal6 =  new CalendarEvent( calendar11, calendar12, "Meet with friends", 15, "Meeting");
        cal6.setColor( Color.MAGENTA);
        database.insertEvent( cal6);

        Calendar calendar13 = Calendar.getInstance();
        Calendar calendar14 = Calendar.getInstance();
        calendar13.set( Calendar.HOUR_OF_DAY , 18);
        calendar13.set( Calendar.MINUTE , 10);
        calendar14.set( Calendar.HOUR_OF_DAY , 18);
        calendar14.set( Calendar.MINUTE , 20);
        CalendarEvent cal7 =  new CalendarEvent( calendar13, calendar14, "Have a cup of cofee" + calendar9.get( Calendar.MINUTE), 16, "Meeting");
        cal7.setColor( Color.CYAN);
        database.insertEvent( cal7);
    }















}
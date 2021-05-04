package com.timetablecarpenters.pocketcalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * MonthActivity does everything in the month view part of the project.
 * @author Alperen Utku Yalçın
 * @version 1.0
 */
public class MonthActivity extends BaseActivity {
    private static final String INTENT_KEY = "today_date";
    private static final String TAG = "MonthActivity";
    private View[] weeks, dayInWeek;
    private Calendar today;
    private DBHelper database;
    private CalendarEvent[][] eventCountDays;
    private boolean[] isSpareDay;
    protected Intent intent;
    private Context context;
    /**
     * this method works once a MonthActivity is created.
     * @param savedInstanceState
     */
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
        TextView date = findViewById( R.id.rgb1).findViewById( R.id.dateText);
        date.setText( today.get( Calendar.YEAR) + " " + formattedMonth( today.get(
                Calendar.MONTH)));
        //Create two calendar instances to get the events in a such interval from the database.
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

        pullEvents( database.getEventsInAnIntervalInArray( calendar1, calendar2));
        initiateWeeks();

        View someView = findViewById( R.id.back_background);
        someView.setBackgroundColor( CustomizableScreen.backgroundColor);
        setOtherTexts();
    }
    /**
     * Reports the name of the month according to the number
     * @author Elifsena
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
    /**
     * Creates the 6 weeks that are going to be seen in Month View
     * @author Alperen
     */
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
    /**
     * This method is called once for every row (there are 6 for every week)
     * in the Month View. this method creates each day in a row.
     * @author Alperen
     * @param aWeek
     * @param cal
     * @param dayCount
     */
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

        ( ( TextView)findViewById( R.id.monday)).setTextColor( CustomizableScreen.getBackGColor());
        ( ( TextView)findViewById( R.id.tuesday)).setTextColor( CustomizableScreen.getBackGColor());
        ( ( TextView)findViewById( R.id.wednesday)).setTextColor( CustomizableScreen.getBackGColor());
        ( ( TextView)findViewById( R.id.thursday)).setTextColor( CustomizableScreen.getBackGColor());
        ( ( TextView)findViewById( R.id.friday)).setTextColor( CustomizableScreen.getBackGColor());
        ( ( TextView)findViewById( R.id.saturday)).setTextColor( CustomizableScreen.getBackGColor());
        ( ( TextView)findViewById( R.id.sunday)).setTextColor( CustomizableScreen.getBackGColor());
    }
    /**
     * Activates each day in MonthView.
     * @author Alperen
     * @param day the "day"s view in one of the week rows
     * @param cal the exact day that the "day" view represents
     * @param dayCount the exact place that the day is located from the start to the day.(42 places)
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void dayCreate(View day, Calendar cal, int dayCount) {
        TextView date = day.findViewById(R.id.text_date_name);
        View backG = (View) day.findViewById(R.id.downwards_layout);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, cal.get(Calendar.MONTH));
        calendar.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        calendar.set(Calendar.DATE, cal.get(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        String str = "" + calendar.get(Calendar.DATE);

        if (isSpareDay[dayCount]) {
            str = "";
            backG.setBackgroundColor(CustomizableScreen.getBackGColor());
            date.setText(str);
            LinearLayout rl = (LinearLayout) day.findViewById(R.id.linear_layout1);
            rl.removeAllViews();
        } else {
            if (eventCountDays[dayCount] != null) {
                addCircles(eventCountDays[dayCount], day);
            }
            backG.setBackgroundColor( CustomizableScreen.getButtonColor());
            date.setText(str);
            date.setTextColor( CustomizableScreen.getBackGColor());
            day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(context, DayActivity.class);
                    intent.putExtra(INTENT_KEY, calendar);
                    startActivity(intent);
                    //finish();
                }
            });
        }
    }
    /**
     * A helper method that helps addCircles method and sets the right text color to the text.
     * @author Alperen
     * @param c the view that the textView belongs to
     * @param i the index of the event in eventlist
     * @param eventList array of CalendarEvent values
     * @return view that has been updated
     */
    private View setView( View c, int i, CalendarEvent[] eventList) {
        TextView txt = c.findViewById( R.id.mark);
        txt.setTextColor( eventList[i].color);
        return c;
    }
    /**
     * for each active day of the month, according to the amount of events in the day, octagens are
     * created. Each octogen has the color of the event that it represents.
     * If the amount of events is bigger than 6, than the 6th octogen becomes a plus instead and
     * its color is set as the textcolor.
     * @author Alperen
     * @param events the events in month
     * @param day the day in week row of month View.
     */
    private void addCircles( CalendarEvent[] events, View day) {
        LinearLayout rl = (LinearLayout) day.findViewById( R.id.linear_layout1);
        LinearLayout rl2 = (LinearLayout) day.findViewById( R.id.linear_layout2);
        View c1 = rl.findViewById( R.id.circle1);
        View c2 = rl.findViewById( R.id.circle2);
        View c3 = rl.findViewById( R.id.circle3);
        View c4 = rl.findViewById( R.id.circle4);
        View c5 = rl.findViewById( R.id.circle5);
        View c6 = rl.findViewById( R.id.circle6);
        View viewPlus = rl.findViewById( R.id.three_dot);
        rl.removeAllViews( );
        if ( events == null) {
            return;
        }
        else if ( events.length == 1) {
            rl.addView( setView( c1, 0, events), 0);
        }
        else if ( events.length == 2) {
            rl.addView( setView( c1, 0, events), 0);
            rl.addView( setView( c2, 1, events), 1);
        }
        else if ( events.length == 3) {
            rl.addView( setView( c1, 0, events), 0);
            rl.addView( setView( c2, 1, events), 1);
            rl.addView( setView( c3, 2, events), 2);
        }
        else if ( events.length == 4) {
            rl.addView( setView( c1, 0, events), 0);
            rl.addView( setView( c2, 1, events), 1);
            rl.addView( setView( c3, 2, events), 2);
            rl2.addView( setView( c4, 3, events), 0);
        }
        else if ( events.length == 5) {
            rl.addView( setView( c1, 0, events), 0);
            rl.addView( setView( c2, 1, events), 1);
            rl.addView( setView( c3, 2, events), 2);
            rl2.addView( setView( c4, 3, events), 0);
            rl2.addView( setView( c5, 4, events), 1);
        }
        else if ( events.length == 6) {
            rl.addView( setView( c1, 0, events), 0);
            rl.addView( setView( c2, 1, events), 1);
            rl.addView( setView( c3, 2, events), 2);
            rl2.addView( setView( c4, 3, events), 0);
            rl2.addView( setView( c5, 4, events), 1);
            rl2.addView( setView( c6, 5, events), 2);
        }
        else if ( events.length >= 7) {
            rl.addView( setView( c1, 0, events), 0);
            rl.addView( setView( c2, 1, events), 1);
            rl.addView( setView( c3, 2, events), 2);
            rl2.addView( setView( c4, 3, events), 0);
            rl2.addView( setView( c5, 4, events), 1);
            TextView txt = viewPlus.findViewById( R.id.plus_sign);
            txt.setTextColor( CustomizableScreen.getBackGColor());
            rl2.addView( viewPlus, 2);
        }
    }
    /**
     *pulls event array list from database and turns it into an array of events and initiates
     * eventCountDays and isSpareDay.
     * @author Alperen
     * @param dBEvents ArrayList of CalendarEvent's from the database
     */
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
        counter = 0;
        for (int i = 0; i < dBEvents.size(); i++) {
            if ( dBEvents.get(i) != null && dBEvents.get(i).getEventStart().get(Calendar.MONTH) == today.get(Calendar.MONTH)) {
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
    /**
     * Helper method.
     * Turns the 0 to 6 values starting from sunday to 1 to 7 values starting from Monday.
     * @author Alperen Utku Yalçın
     * @param a a day value (Starts from sunday)
     * @return a day value (Starts from monday)
     */
    public int dayCorrector(int a) {
        if (a == 1) {
            return 7;
        } else {
            return a - 1;
        }
    }
    /**
     * Helper method for pullEvents.
     * gets today's events and puts them all in an array as events property.
     * @author Alperen Utku Yalçın
     * @param calEvents today's events as an ArrayList
     * @param day the day in the 6 weeks corresponding to 6 rows(1 to 42)
     * @return today's events as an array.
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
     * When right swiped, it opens the next month's view
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
     *When left swiped, it opens the previous month's view
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
    private void setOtherTexts() {
        int color = CustomizableScreen.getBackGColor();
        ( ( TextView)findViewById( R.id.dateText)).setTextColor( color);
    }
}
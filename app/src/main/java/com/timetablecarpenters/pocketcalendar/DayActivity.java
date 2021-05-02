package com.timetablecarpenters.pocketcalendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;


public class DayActivity extends BaseActivity  {
    private static final String TAG = "DayActivity";
    public static final String EVENT_KEY = "event";

    private CalendarEvent[] events; //the events in day
    private CalendarEvent[] orderedEventEnds; //the events in day
    // first [] for hours of day, next [] for the event starting times and end times in order.
    private String[][] textsOfHours;
    private CalendarEvent[] allEventsChron; //chronologically all events
    private boolean[] isEventStart; //if false, event is for the ending, not starting
    private DBHelper database;
    private Calendar thisDay;
    public final static String INTENT_KEY = "today_date";
    public final static String MAPS_INTENT_KEY = "map_intent";
    public final static String ACTIVITY_NAME = "day_activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_day); //Changed to test it normally activity_day
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: DayActivity Starts");

        Bundle extras;

        //initiate...
        extras = getIntent().getExtras();
        if ( extras != null) {
            try {
                thisDay = (Calendar) extras.get(INTENT_KEY);
            } catch (Exception e) {
                Log.d(TAG, "onCreate: no  date came out of the intent");
            }
            try {
                Log.d(TAG, "onCreate: event called back!");
            } catch (Exception e) {
                Log.d(TAG, "onCreate: no event came out of intent " + e);
            }
        }

        if (thisDay == null) {
            Log.d(TAG, "onCreate: thisDay is Set" );
            thisDay = Calendar.getInstance();
        }
        String heading = thisDay.get( Calendar.YEAR) + " " + formattedMonth( thisDay.get(
                Calendar.MONTH)) + " " + thisDay.get( Calendar.DAY_OF_MONTH);
        TextView date = findViewById( R.id.dateText);
        date.setText( heading);

        database = new DBHelper(this, DBHelper.DB_NAME, null);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set( Calendar.MONTH, thisDay.get( Calendar.MONTH));
        calendar1.set( Calendar.YEAR, thisDay.get( Calendar.YEAR));
        calendar1.set( Calendar.DATE, thisDay.get( Calendar.DATE));
        calendar1.add( Calendar.DATE, -1);
        calendar1.set( Calendar.HOUR_OF_DAY, 0);
        calendar1.set( Calendar.MINUTE, 0);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set( Calendar.MONTH, thisDay.get( Calendar.MONTH));
        calendar2.set( Calendar.YEAR, thisDay.get( Calendar.YEAR));
        calendar2.set( Calendar.DATE, thisDay.get( Calendar.DATE));
        calendar2.add( Calendar.DATE, 1);
        calendar2.set( Calendar.HOUR_OF_DAY, 23);
        calendar2.set( Calendar.MINUTE, 59);
        pullEventsOfDay( database.getEventsInAnIntervalInArray( calendar1, calendar2));

        setOrderedEventStarts();
        setOrderedEventEnds();
        initiateRelativeLayouts();

        View someView = findViewById( R.id.day_back_color1);
        someView.setBackgroundColor( CustomizableScreen2.backgroundColor);
        someView = findViewById( R.id.day_back_color2);
        someView.setBackgroundColor( getButtonColor());
        setOtherTexts();

        editInTextFont( findViewById( R.id.textView8));

        FloatingActionButton fab = findViewById(R.id.add_event_button);

        fab.setOnClickListener(new DayActivity.ViewChangeClickListener());
    }

    /**
     * Sets the text colors to a specific value (For testing purposes only)
     * @author Alperen
     * @return int color value
     */
    private int getBackGColor() {
        if (CustomizableScreen2.textColor != 0) {
            return CustomizableScreen2.textColor;
        } else {
            CustomizableScreen2.initiate();
            return CustomizableScreen2.textColor;
        }
    }
    /**
     * Sets the text colors to a specific value (For testing purposes only)
     * @author Alperen
     * @return int color value
     */
    private int getButtonColor() {
        if (CustomizableScreen2.buttonBackgroundColor != 0) {
            return CustomizableScreen2.buttonBackgroundColor;
        }
        else {
            CustomizableScreen2.initiate();
            return CustomizableScreen2.buttonBackgroundColor;
        }
    }
    private void setOtherTexts() {
        int color = getBackGColor();
        ((TextView) findViewById(R.id.dateText)).setTextColor(color);
    }

    /**
     * gets today's events and puts them all in an array as events property
     * @author Alperen Utku Yalçın
     */
    private void pullEventsOfDay( ArrayList<CalendarEvent> calEvents) {
        int counter;
        counter = 0;
        events = new CalendarEvent[ calEvents.size()];
        for ( int i = 0; i < calEvents.size(); i++) {
            if ( calEvents.get( i) != null) {
                counter++;
            }
        }
        events = new CalendarEvent[ counter];
        counter = 0;
        for ( int i = 0; i < calEvents.size(); i++) {
            if ( calEvents.get( i) != null && calEvents.get(i).getEventStart().get( Calendar.DATE) == thisDay.get( Calendar.DATE) ) {
                events[counter] = calEvents.get( i);
                counter++;
            }
        }
        orderedEventEnds = events;
    }

    private int clockToInt( Calendar cal) {
        int result;
        result = cal.get( Calendar.HOUR_OF_DAY) * 60 + cal.get( Calendar.MINUTE);
        return result;
    }
    /**
     * orders today's events in chronological order and puts the result value to events property
     * @author Alperen Utku Yalçın
     */
    private void setOrderedEventStarts() {
        Calendar calendar1000 = Calendar.getInstance();
        calendar1000.set( Calendar.HOUR_OF_DAY , 23);
        calendar1000.set( Calendar.MINUTE , 59);
        CalendarEvent[] result = new CalendarEvent[events.length];
        boolean commander[] = new boolean[events.length];
        int counter;
        for ( int i = 0; i < events.length; i++) {
            commander[i] = true;
        }

        for ( int a = 0; a < events.length; a++) {
            counter = 0;
            CalendarEvent current = new CalendarEvent( calendar1000, calendar1000, "dsladjas" + calendar1000.get( Calendar.MINUTE), 0, ".");
            for ( int i = 0; i < events.length && events[i] != null; i++) {
                if ( commander[i] && clockToInt(current.getEventStart()) > clockToInt(events[i].getEventStart())) {
                    current = events[i];
                    counter = i;
                }
            }
            result[a] = current;
            commander[counter] = false;
        }
        events = result;
    }
    /**
     * orders today's events in chronological order and puts the result value to events property
     * @author Alperen Utku Yalçın
     */
    private void setOrderedEventEnds() {
        Calendar calendar2000 = Calendar.getInstance();
        calendar2000.set( Calendar.HOUR_OF_DAY , 23);
        calendar2000.set( Calendar.MINUTE , 59);
        CalendarEvent[] result = new CalendarEvent[orderedEventEnds.length];
        boolean commander[] = new boolean[orderedEventEnds.length];
        int counter;

        for ( int i = 0; i < orderedEventEnds.length; i++) {
            commander[i] = true;
        }

        for ( int a = 0; a < orderedEventEnds.length; a++) {
            counter = 0;
            CalendarEvent current = new CalendarEvent( calendar2000, calendar2000, "sth " + calendar2000.get( Calendar.MINUTE), 0, ".");
            for ( int i = 0; i < orderedEventEnds.length && orderedEventEnds[i] != null; i++) {
                if ( commander[i] && clockToInt(current.getEventEnd()) > clockToInt(orderedEventEnds[i].getEventEnd())) {
                    current = orderedEventEnds[i];
                    counter = i;
                }
            }
            result[a] = current;
            commander[counter] = false;
        }
        orderedEventEnds = result;
    }
    /**
     * Creates an event arraqy with chronologically ordered event start-end times.
     * @author Alperen Utku Yalçın
     */
    public void EventChronologyCreate() {
        setOrderedEventStarts();
        setOrderedEventEnds();

        Calendar calendar2000 = Calendar.getInstance();
        calendar2000.set( Calendar.MONTH, thisDay.get( Calendar.MONTH));
        calendar2000.set( Calendar.YEAR, thisDay.get( Calendar.YEAR));
        calendar2000.set( Calendar.DATE, thisDay.get( Calendar.DATE));
        calendar2000.set( Calendar.HOUR_OF_DAY , 23);
        calendar2000.set( Calendar.MINUTE , 59);
        Calendar calendar1000 = Calendar.getInstance();
        calendar1000.set( Calendar.MONTH, thisDay.get( Calendar.MONTH));
        calendar1000.set( Calendar.YEAR, thisDay.get( Calendar.YEAR));
        calendar1000.set( Calendar.DATE, thisDay.get( Calendar.DATE));
        calendar1000.set( Calendar.HOUR_OF_DAY , 23);
        calendar1000.set( Calendar.MINUTE , 59);
        CalendarEvent current1, current2;

        int eventsSmallest = 10000;
        int orderedSmallest = 10000;
        int counter1 = 0;
        int counter2 = 0;


        allEventsChron = new CalendarEvent[events.length + orderedEventEnds.length];
        isEventStart = new boolean[events.length + orderedEventEnds.length];
        for ( int i = 0; i < events.length + orderedEventEnds.length; i++) {
            isEventStart[i] = false;
        }

        for( int i = 0; i < events.length + orderedEventEnds.length; i++) {
            current1 = new CalendarEvent( calendar2000, calendar2000,
                    "dsladjas" + calendar2000.get( Calendar.MINUTE), 0, ".");
            current2 = new CalendarEvent( calendar1000, calendar1000,
                    "dsladjas" + calendar1000.get( Calendar.MINUTE), 1, ".");

            for ( int a = counter2; a < orderedEventEnds.length; a++) {
                if ( clockToInt( orderedEventEnds[a].getEventEnd())
                        <  clockToInt( current1.getEventStart())) {
                    current1 = orderedEventEnds[a];
                    orderedSmallest = a;
                }
            }
            for ( int a = counter1; a < events.length; a++) {
                if ( clockToInt( events[a].getEventStart())
                        <  clockToInt( current2.getEventStart())) {
                    current2 = events[a];
                    eventsSmallest = a;
                }
            }

            if ( counter1 < events.length && clockToInt( current1.getEventEnd()) > clockToInt( current2.getEventStart())) {
                allEventsChron[i] = events[eventsSmallest];
                CalendarEvent calev = events[counter1];
                events[counter1] = events[eventsSmallest];
                events[eventsSmallest] = calev;
                isEventStart[i] = true;
                counter1++;
            }
            else if ( counter2 < orderedEventEnds.length && clockToInt( current1.getEventEnd()) < clockToInt( current2.getEventStart())) {
                allEventsChron[i] = orderedEventEnds[orderedSmallest];
                CalendarEvent calEv = orderedEventEnds[counter2];
                orderedEventEnds[counter2] = orderedEventEnds[orderedSmallest];
                orderedEventEnds[orderedSmallest] = calEv;
                isEventStart[i] = false;
                counter2++;
            }
        }
    }

    public String hourConvert( int i) {
        String result;
        result = i + "";
        if( i < 10) {
            result = 0 + "" + result;
        }
        result = result + ":00";
        return result;
    }
    /**
     * Creates and sets the textViews with appropriate texts
     * @author Alperen Utku Yalçın
     */
    private void createTextView( View view, int hour) {
        String str;
        TextView text = view.findViewById( R.id.textView1);
        editInTextFont(text);
        RelativeLayout layout = view.findViewById( R.id.rl1);
        text.setText( hourConvert( hour));
        text.setTextColor( getBackGColor());

        View recent = layout;
        // i is a global variable so that the clickListener can see the value
        for ( int i = 0; i < allEventsChron.length ; i++) {
            if ( allEventsChron[i] != null) {
                if (isEventStart[i]) {
                    str = "[Start] " + allEventsChron[i].getEventStartTime();
                } else {
                    str = "[End] " + allEventsChron[i].getEventEndTime();
                }
                str += " " + allEventsChron[i].getName();

                if (discriminateEvent(allEventsChron[i], isEventStart[i]).get(Calendar.HOUR_OF_DAY) == hour) {
                    TextView textView = new TextView(DayActivity.this);
                    textView.setId((int) (Math.random() * 10000)); // It is not a very good solution
                    RelativeLayout.LayoutParams layoutParams = new
                            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.BELOW, recent.getId());

                    recent = textView;
                    textView.setText(str);
                    editParagraphFont(textView);
                    if ( allEventsChron[i].color != 0) {
                        textView.setTextColor(allEventsChron[i].color);
                    }
                    textView.setSingleLine();
                    layout.addView(textView, layoutParams);

                    int finalI1 = i;
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: event clicked");
                            Intent intent = new Intent(DayActivity.this, EventActivity.class);
                            intent.putExtra(EventActivity.EVENT_VIEW_INTENT_KEY, allEventsChron[finalI1]);
                            startActivity(intent);
                        }
                    });
                }
            }
        }
    }

    private Calendar discriminateEvent( CalendarEvent event, boolean b) {
        if ( b)
            return event.getEventStart();
        else
            return event.getEventEnd();
    }

    /**
     * creates insides of the RelativeLayouts.
     * @author Alperen Utku Yalçın
     */
    private void initiateRelativeLayouts( ) {
        //todo
        EventChronologyCreate();
        View[] views = new View[24];
        views[0] = findViewById( R.id.day_content).findViewById( R.id.hr0);
        views[1] = findViewById( R.id.day_content).findViewById( R.id.hr1);
        views[2] = findViewById( R.id.day_content).findViewById( R.id.hr2);
        views[3] = findViewById( R.id.day_content).findViewById( R.id.hr3);
        views[4] = findViewById( R.id.day_content).findViewById( R.id.hr4);
        views[5] = findViewById( R.id.day_content).findViewById( R.id.hr5);
        views[6] = findViewById( R.id.day_content).findViewById( R.id.hr6);
        views[7] = findViewById( R.id.day_content).findViewById( R.id.hr7);
        views[8] = findViewById( R.id.day_content).findViewById( R.id.hr8);
        views[9] = findViewById( R.id.day_content).findViewById( R.id.hr9);
        views[10] = findViewById( R.id.day_content).findViewById( R.id.hr10);
        views[11] = findViewById( R.id.day_content).findViewById( R.id.hr11);
        views[12] = findViewById( R.id.day_content).findViewById( R.id.hr12);
        views[13] = findViewById( R.id.day_content).findViewById( R.id.hr13);
        views[14] = findViewById( R.id.day_content).findViewById( R.id.hr14);
        views[15] = findViewById( R.id.day_content).findViewById( R.id.hr15);
        views[16] = findViewById( R.id.day_content).findViewById( R.id.hr16);
        views[17] = findViewById( R.id.day_content).findViewById( R.id.hr17);
        views[18] = findViewById( R.id.day_content).findViewById( R.id.hr18);
        views[19] = findViewById( R.id.day_content).findViewById( R.id.hr19);
        views[20] = findViewById( R.id.day_content).findViewById( R.id.hr20);
        views[21] = findViewById( R.id.day_content).findViewById( R.id.hr21);
        views[22] = findViewById( R.id.day_content).findViewById( R.id.hr22);
        views[23] = findViewById( R.id.day_content).findViewById( R.id.hr23);
        for ( int i = 0; i < 24 ; i++) {
            createTextView( views[i], i);
        }
        TextView txt = findViewById( R.id.textView8);
        txt.setTextColor( getBackGColor());
    }

    /**
     * Edits the font sizes of textViews according to settings
     * It changes the font sizes of in-texts
     * @param text is the hour of the day
     */
    public void editInTextFont(TextView text){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("inTextPref", MODE_PRIVATE);
        String inTextFontSize = sp.getString("inTextFontSize","");
        if (inTextFontSize.equals(SMALL))
        {
            text.setTextSize(10);
        }
        if (inTextFontSize.equals(MEDIUM))
        {
            text.setTextSize(14);
        }
        if (inTextFontSize.equals(LARGE))
        {
            text.setTextSize(18);
        }
    }

    /**
     * Edits the font sizes of textViews according to settings
     * It changes the font sizes of paragraphs
     * @param text is the hour of the day
     */
    public void editParagraphFont(TextView text){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("paragraphPref", MODE_PRIVATE);
        String paragraphFontSize = sp.getString("paragraphFontSize","");
        if (paragraphFontSize.equals(SMALL))
        {
            text.setTextSize(10);
        }
        if (paragraphFontSize.equals(MEDIUM))
        {
            text.setTextSize(12);
        }
        if (paragraphFontSize.equals(LARGE))
        {
            text.setTextSize(16);
        }
    }

    /**
     *
     */
    @Override
    public void leftSwipe() {
        Log.d(TAG, "leftSwipe: DayActivity");
        super.leftSwipe();
        finish();
        Intent intent = new Intent(this, DayActivity.class);
        thisDay.add( Calendar.DATE, 1);
        intent.putExtra( INTENT_KEY, thisDay);
        startActivity(intent);
        overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     *
     */
    @Override
    public void rightSwipe() {
        Log.d(TAG, "rightSwipe: DayActivity");
        super.rightSwipe();
        finish();
        Intent intent = new Intent(this, DayActivity.class);
        thisDay.add( Calendar.DATE, -1);
        intent.putExtra( INTENT_KEY, thisDay);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

    public class ViewChangeClickListener implements View.OnClickListener {
        /**
         * when clicked creates an intent of the desired activity and starts the activity
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.add_event_button:
                    Log.d(TAG, "onClick: add event opening");
                    intent = new Intent(DayActivity.this, AddEvent.class);
                    intent.putExtra(AddEvent.DATE_KEY, thisDay);
                    startActivity(intent);
                    break;
            }
        }
    }

    private long compareDates(Calendar calendar1, Calendar calendar2){
        long result;
        result = 0;
        long yearDifferenceInMs = 0;
        long monthDifferenceInMs = 0;
        long dayDifferenceInMs = 0;
        long hourDifferenceInMs = 0;
        long minuteDifferenceInMs =0;
        long secondDifferenceInMs = 0;

        secondDifferenceInMs = calendar1.get(Calendar.SECOND) - calendar2.get(Calendar.SECOND);
        minuteDifferenceInMs = (calendar1.get(Calendar.MINUTE) - calendar2.get(Calendar.MINUTE)) * 60;
        hourDifferenceInMs = (calendar1.get(Calendar.HOUR_OF_DAY) - calendar2.get(Calendar.HOUR_OF_DAY)) * 60 * 60;
        dayDifferenceInMs = (calendar1.get(Calendar.DAY_OF_MONTH) - calendar2.get(Calendar.DAY_OF_MONTH))* 60 * 60 * 24;
        monthDifferenceInMs = (calendar1.get(Calendar.MONTH) - calendar2.get(Calendar.MONTH))* 60 * 60 * 24 * 30;
        yearDifferenceInMs = (calendar1.get(Calendar.YEAR) - calendar2.get(Calendar.YEAR))* 60 * 60 * 24 * 12 * 30;
        result = secondDifferenceInMs + minuteDifferenceInMs + hourDifferenceInMs + dayDifferenceInMs
                + monthDifferenceInMs + yearDifferenceInMs;

        return result * 1000;

    }

}
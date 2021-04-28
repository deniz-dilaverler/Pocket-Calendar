package com.timetablecarpenters.pocketcalendar;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;



import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;


public class DayActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "DayActivity";
    private AlertDialog.Builder addEventBuilder;
    private AlertDialog addEventDialog;
    private Spinner event_type_spinner, notification_spinner;
    private Spinner repetition_type;
    private TextView event_due_time, event_due_date, event_start, event_end;
    private EditText event_name, number_of_repetitions, notes;
    private LinearLayout addEventPopupView;
    private Button next, done, blue;
    private CheckBox repeat, notification;
    private String eventType, eventName;
    private ScrollView scrollView;
    private CalendarEvent addedEvent;
    private int endHour, endMinute, startHour, startMinute;
    private Calendar eventStart, eventEnd;

    private CalendarEvent[] events; //the events in day
    private CalendarEvent[] orderedEventEnds; //the events in day
    // first [] for hours of day, next [] for the event starting times and end times in order.
    private String[][] textsOfHours;
    private CalendarEvent[] allEventsChron; //chronologically all events
    private boolean[] isEventStart; //if false, event is for the ending, not starting
    final private String GAP = "     ";


    //BORROWED
    public Calendar first;
    private final static String INTENT_KEY = "first_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.content_day2); //Changed to test it normally activity_day
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starts");


        //initiate...
        pullEventsOfDay();
        setOrderedEventStarts();
        setOrderedEventEnds();
        initiateRelativeLayouts();


        FloatingActionButton fab = findViewById(R.id.add_event_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DayActivity.this, "YAZILIM ÖĞREN", Toast.LENGTH_SHORT).show();
                openDialog();
            }
        });
    }

    /**
     * Creates a popup that adds an event. Initially shows choices for event types and name, displays
     * other properties according to them
     * @author Elifsena Öz
     */
    public void openDialog() {
        addEventBuilder = new AlertDialog.Builder(this);
        scrollView = (ScrollView) getLayoutInflater().inflate(R.layout.add_event_popup,null);
        addEventPopupView = (LinearLayout) scrollView.findViewById(R.id.add_event_linear);

        addNameAndType();

        // Next button removes itself from the popup if event type and name are given
        final View nextButtonView = (View) getLayoutInflater().inflate(R.layout.add_event_next, null);
        next = (Button) nextButtonView.findViewById(R.id.add_event_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventName = event_name.getText().toString();
                if (!eventName.equals("") && eventType != null) {
                    next.setEnabled(false);
                    event_type_spinner.setEnabled(false);
                    addEventPopupView.removeView(nextButtonView);
                    updatePopupView();
                }
                else {
                    Toast.makeText(DayActivity.this, "Please enter event name",
                                        Toast.LENGTH_SHORT).show();
                }
            }
        });
        addEventPopupView.addView(nextButtonView);

        addEventBuilder.setView(scrollView);
        addEventDialog = addEventBuilder.create();
        addEventDialog.show();
    }

    /**
     * Adds views to the popup according to the type of event chosen
     * @author Elifsena Öz
     */
    private void updatePopupView() {
        if (eventType.equals("Assignment")) {
            addDueDate();
        }
        else if (eventType.equals("Birthday")) {
            // todo gift bought
        }
        else {
            addInterval();
            addRepetition();
        }
        addCommonItems();
        addEventBuilder.setView(scrollView);
    }

    /**
     * Adds name and type choices to add event popup
     * @author Elifsena Öz
     */
    private void addNameAndType() {
        final View typeAndNameView = getLayoutInflater().inflate(R.layout.add_event_type_and_name_item, null);

        event_type_spinner = (Spinner) typeAndNameView.findViewById(R.id.event_type_spinner);
        ArrayAdapter<String> eventNamesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.event_types));
        event_type_spinner.setAdapter(eventNamesAdapter);
        event_type_spinner.setOnItemSelectedListener(this);
        event_name = (EditText) typeAndNameView.findViewById(R.id.event_name_edit);
        addEventPopupView.addView(typeAndNameView);
    }

    /**
     * Adds due date view to add event popup
     * @author Elifsena Öz
     */
    private void addDueDate() {
        final View dueDateView = getLayoutInflater().inflate(R.layout.add_event_due_date_item, null);
        event_due_time = (TextView) dueDateView.findViewById(R.id.due_time);

        event_due_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        DayActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                endHour = hourOfDay;
                                endMinute = minute;
                                eventEnd = Calendar.getInstance();
                                eventEnd.set(0,0,0, endHour, endMinute);
                                event_due_time.setText(endHour + ":" + endMinute);
                            }
                        },24,0,true
                );
                timePickerDialog.updateTime(endHour, endMinute);
                timePickerDialog.show();
            }
        });
        addEventPopupView.addView(dueDateView);
    }

    /**
     * Adds interval view to add event popup
     * @author Elifsena Öz
     */
    private void addInterval() {
        final View intervalView = getLayoutInflater().inflate(R.layout.add_event_interval_item, null);

        event_start = (TextView) intervalView.findViewById(R.id.add_event_start);
        event_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        DayActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                startHour = hourOfDay;
                                startMinute = minute;
                                eventStart = Calendar.getInstance();
                                eventStart.set(0,0,0, startHour, startMinute);
                                event_start.setText("Start: " + startHour + ":" + startMinute);
                            }
                        },24,0,true
                );
                timePickerDialog.updateTime(startHour, startMinute);
                timePickerDialog.show();
            }
        });

        event_end = (TextView) intervalView.findViewById(R.id.add_event_end);

        event_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        DayActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                endHour = hourOfDay;
                                endMinute = minute;
                                eventEnd = Calendar.getInstance();
                                eventEnd.set(0,0,0, endHour, endMinute);
                                event_end.setText("End: " + endHour + ":" + endMinute);
                            }
                        },24,0,true
                );
                timePickerDialog.updateTime(endHour, endMinute);
                timePickerDialog.show();
            }
        });
        addEventPopupView.addView(intervalView);
    }

    /**
     * Adds repetition view to add event popup
     * @author Elifsena Öz
     */
    private void addRepetition() {
        if (!eventType.equals("Exam")) {
            final View repetitionView = getLayoutInflater().inflate(R.layout.add_event_repetition_item, null);

            repetition_type = (Spinner) repetitionView.findViewById(R.id.repetition_type);
            ArrayAdapter<String> repetitionTypesAdapter = new ArrayAdapter<>(DayActivity.this,
                    android.R.layout.simple_spinner_item,
                    getResources().getStringArray(R.array.repetition_types));
            repetition_type.setAdapter(repetitionTypesAdapter);
            repetition_type.setOnItemSelectedListener(DayActivity.this);
            repetition_type.setEnabled(false);
            number_of_repetitions = (EditText) repetitionView.findViewById(R.id.num_of_reperitions);
            number_of_repetitions.setEnabled(false);

            // displays repetition options if the box is checked (the box is initially checked)
            repeat = (CheckBox) repetitionView.findViewById(R.id.repeatition_checkbox);
            repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!repeat.isChecked()) {
                        repetition_type.setEnabled(false);
                        number_of_repetitions.setEnabled(false);
                    }
                    else {
                        repetition_type.setEnabled(true);
                        number_of_repetitions.setEnabled(true);
                    }
                }
            });

            addEventPopupView.addView(repetitionView);
        }
    }

    /**
     * Adds common views of events (notification, notes, colour, location ect) to add event popup
     * @author Elifsena Öz
     */
    private void addCommonItems() {
        final View commonItemsView = getLayoutInflater().inflate(R.layout.add_event_common_items, null);

        notification_spinner = (Spinner) commonItemsView.findViewById(R.id.notifications_spinner);
        ArrayAdapter<String> notificationTimesAdapter = new ArrayAdapter<>(DayActivity.this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.notification_times));
        notification_spinner.setAdapter(notificationTimesAdapter);
        notification_spinner.setEnabled(false);

        notification = (CheckBox) commonItemsView.findViewById(R.id.notification_checkbox);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.isChecked())
                    notification_spinner.setEnabled(true);
                else
                    notification_spinner.setEnabled(false);
            }
        });

        // todo color and notification

        notes = (EditText) commonItemsView.findViewById(R.id.notes);

        // todo location

        done = (Button) commonItemsView.findViewById(R.id.add_event_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addEventPopupView.addView(commonItemsView);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.event_type_spinner) {
            eventType = parent.getItemAtPosition(position).toString();
        }
        if (parent.getId() == R.id.repetition_type) {
            // todo - use set methods to create repetition
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }












    /**
     * gets today's events and puts them all in an array as events property
     * @author Alperen Utku Yalçın
     */
    private void pullEventsOfDay() {
        //todo - once the other methods work
        //for now..
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        events = new CalendarEvent[5];

        Calendar calendar3 = Calendar.getInstance();
        Calendar calendar4 = Calendar.getInstance();
        calendar3.set( Calendar.HOUR_OF_DAY , 1);
        calendar3.set( Calendar.MINUTE , 30);
        calendar4.set( Calendar.HOUR_OF_DAY , 3);
        calendar4.set( Calendar.MINUTE , 33);
        events[0] = new CalendarEvent( calendar3, calendar4, "You Have A Meeting" + calendar3.get( Calendar.MINUTE), 2, "Meeting");
        events[0].setColor( Color.RED);

        calendar.set( Calendar.HOUR_OF_DAY , 1);
        calendar.set( Calendar.MINUTE , 1);
        calendar2.set( Calendar.HOUR_OF_DAY , 2);
        calendar2.set( Calendar.MINUTE , 3);
        events[1] = new CalendarEvent( calendar, calendar2, "You Have A Meeting"+ calendar.get( Calendar.MINUTE), 1, "Meeting");
        events[1].setColor( Color.BLUE);

        Calendar calendar5 = Calendar.getInstance();
        Calendar calendar6 = Calendar.getInstance();
        calendar5.set( Calendar.HOUR_OF_DAY , 2);
        calendar5.set( Calendar.MINUTE , 1);
        calendar6.set( Calendar.HOUR_OF_DAY , 2);
        calendar6.set( Calendar.MINUTE , 3);
        events[2] = new CalendarEvent( calendar5, calendar6, "Bruh", 3, "Meeting");
        events[2].setColor( Color.DKGRAY);

        Calendar calendar7 = Calendar.getInstance();
        Calendar calendar8 = Calendar.getInstance();
        calendar7.set( Calendar.HOUR_OF_DAY , 0);
        calendar7.set( Calendar.MINUTE , 1);
        calendar8.set( Calendar.HOUR_OF_DAY , 0);
        calendar8.set( Calendar.MINUTE , 3);
        events[3] = new CalendarEvent( calendar7, calendar8, "You Have A Meeting Too", 4, "Meeting");
        events[3].setColor( Color.GREEN);

        Calendar calendar9 = Calendar.getInstance();
        Calendar calendar10 = Calendar.getInstance();
        calendar9.set( Calendar.HOUR_OF_DAY , 1);
        calendar9.set( Calendar.MINUTE , 10);
        calendar10.set( Calendar.HOUR_OF_DAY , 1);
        calendar10.set( Calendar.MINUTE , 20);
        events[4] = new CalendarEvent( calendar9, calendar10, "You Have A Meeting Too" + calendar9.get( Calendar.MINUTE), 5, "Meeting");
        events[3].setColor( Color.CYAN);

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
        calendar1000.set( Calendar.HOUR_OF_DAY , 11);
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
            for ( int i = 0; i < events.length; i++) {
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
            for ( int i = 0; i < orderedEventEnds.length; i++) {
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
     * finds the appropriate texts for each event start time and end time for the textViews.
     * @author Alperen Utku Yalçın
     */
    private void getTextsOfEvents( ) {
        textsOfHours = new String[24][];
        int[] eventCount = new int[24];
        for ( int i = 0; i < events.length ; i++) {
            eventCount[ events[i].getEventStart().get(Calendar.HOUR_OF_DAY) ] += 1;
            eventCount[ events[i].getEventEnd().get(Calendar.HOUR_OF_DAY) ] += 1;
        }

        for ( int i = 0; i < events.length ; i++) {
            textsOfHours[ events[i].getEventStart().get(Calendar.HOUR_OF_DAY) ][ eventCount[i]] =
                    events[i].getEventStart().get(Calendar.HOUR_OF_DAY) + ":" +
                            events[i].getEventStart().get(Calendar.MINUTE);
            textsOfHours[ events[i].getEventEnd().get(Calendar.HOUR_OF_DAY) ][ eventCount[i]] =
                    events[i].getEventStart().get(Calendar.HOUR_OF_DAY) + ":" +
                            events[i].getEventStart().get(Calendar.MINUTE) + " " +
                            events[i].getName();
        }

    }
    /**
     * Creates an event arraqy with chronologically ordered event start-end times.
     * @author Alperen Utku Yalçın
     */
    public void EventChronologyCreate() {
        setOrderedEventStarts();
        setOrderedEventEnds();

        Calendar calendar2000 = Calendar.getInstance();
        calendar2000.set( Calendar.HOUR_OF_DAY , 23);
        calendar2000.set( Calendar.MINUTE , 59);
        Calendar calendar1000 = Calendar.getInstance();
        calendar1000.set( Calendar.HOUR_OF_DAY , 23);
        calendar1000.set( Calendar.MINUTE , 59);
        Calendar calendarDef = Calendar.getInstance();
        calendarDef.set( Calendar.HOUR_OF_DAY ,3);
        calendarDef.set( Calendar.MINUTE , 0);
        CalendarEvent current1, current2, default1;

        int eventsSmallest = 10000;
        int orderedSmallest = 10000;
        int counter1 = 0;
        int counter2 = 0;

        default1 = new CalendarEvent( calendar2000, calendar2000,
                "-ERROR-", 0, ".");

        allEventsChron = new CalendarEvent[events.length + orderedEventEnds.length];
        isEventStart = new boolean[events.length + orderedEventEnds.length];
        for ( int i = 0; i < events.length + orderedEventEnds.length; i++) {
            isEventStart[i] = false;
        }

        for( int i = 0; i < events.length + orderedEventEnds.length; i++) {
            current1 = new CalendarEvent( calendar2000, calendar2000,
                    "dsladjas" + calendar2000.get( Calendar.MINUTE), 0, ".");
            current2 = new CalendarEvent( calendar1000, calendar1000,
                    "dsladjas" + calendar1000.get( Calendar.MINUTE), 0, ".");

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

        /* FOR TESTING PURPOSES:
        setOrderedEventStarts();
        allEventsChron = new CalendarEvent[ events.length];
        isEventStart = new boolean[ events.length];
        for ( int i = 0 ; i < events.length; i++) {
            isEventStart[i] = false;
        }
        allEventsChron = orderedEventEnds;
        */
    }
    /**
     * Creates and sets the textViews with appropriate texts
     * @author Alperen Utku Yalçın
     */
    private void createTextView( RelativeLayout layout, int hour) {
        //todo
        int gapCounter = 0;
        int[] gaps;
        String str;
        View recent = layout;

        gaps = new int[ allEventsChron.length];
        for ( int i = 0; i < gaps.length; i++) {
            if ( isEventStart[i]) {
                gaps[i] = gapCounter;
                gapCounter++;
            }
            else {
                gapCounter--;
                for ( int a = 0; a < allEventsChron.length; a++) {
                    //if ( allEventsChron[a].equals( allEventsChron[i]) && a != i) {
                    //    gaps[i] = gaps[a];
                    //}
                }
            }
        }
        gapCounter = 0;
        for ( int i = 0; i < allEventsChron.length; i++) {
            if ( isEventStart[i]) {
                str = "[Start] " + allEventsChron[i].getEventStartTime();
                str += " " + allEventsChron[i].getName() ;
                for ( int a = gaps[i]; a > 0; a--) {
                    str = GAP + str;
                }
                gapCounter++;
            }
            else {
                str = "[End] " +  allEventsChron[i].getEventEndTime();
                str += " " + allEventsChron[i].getName();
                gapCounter--;
                for ( int a = gaps[i]; a > 0; a--) {
                    str = GAP + str;
                }
            }

            if ( discriminateEvent( allEventsChron[i], isEventStart[i]).get( Calendar.HOUR_OF_DAY) == hour) {
                TextView textView = new TextView(DayActivity.this);
                textView.setId( (int) ( Math.random() * 10000)); // It is not a very good solution
                RelativeLayout.LayoutParams layoutParams = new
                        RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.BELOW, recent.getId());

                textView.setText( str);
                textView.setTextColor( allEventsChron[i].color);
                textView.setSingleLine();
                layout.addView(textView, layoutParams);


                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textView.setText( "POG");

                    }
                });

                recent = textView;
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
        RelativeLayout[] layouts = new RelativeLayout[24];
        RelativeLayout rl1 = findViewById( R.id.rl1);
        layouts[0] = rl1;
        RelativeLayout rl2 = findViewById( R.id.rl2);
        layouts[1] = rl2;
        RelativeLayout rl3 = findViewById( R.id.rl3);
        layouts[2] = rl3;
        RelativeLayout rl4 = findViewById( R.id.rl4);
        layouts[3] = rl4;
        RelativeLayout rl5 = findViewById( R.id.rl5);
        layouts[4] = rl5;
        //... more will be added.
        for ( int i = 0; i < 24 ; i++) {
            createTextView( layouts[i], i);
        }
    }
    /* TO BE ADDED: ( by Alperen)
    - get events from database

    */

    //BORROWED CODE
    /*

     //when a leftSwipe is notifed by the super class adds a week to the date of weekView and refreshes the activity

    @Override
    public void leftSwipe() {
    TODO
        super.leftSwipe();
        first.add(Calendar.DATE, 1);
        finish();
        Intent intent = new Intent(this, DayActivity.class);
        intent.putExtra(INTENT_KEY, first);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


     //when a rightSwipe is notifed by the super class subtracts a week from the date of weekView and refreshes the activity

    @Override
    public void rightSwipe() {
    TODO
        super.rightSwipe();
        first.add(Calendar.DATE, -1);
        finish();
        Intent intent = new Intent(this, DayActivity.class);
        intent.putExtra(INTENT_KEY, first);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


    }
    */
}
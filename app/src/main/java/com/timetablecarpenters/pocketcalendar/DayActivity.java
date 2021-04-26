package com.timetablecarpenters.pocketcalendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;


public class DayActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "DayActivity";
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Spinner event_type_spinner;
    private Spinner repetition_type;
    private EditText event_name, event_start, event_end, event_due, number_of_repetitions;
    private LinearLayout addEventPopupView;
    private Button next, done;
    private CheckBox repeat;
    private String eventType, eventName;

    private CalendarEvent[] events; //the events in day
    private CalendarEvent[] orderedEventEnds; //the events in day
    // first [] for hours of day, next [] for the event starting times and end times in order.
    private String[][] textsOfHours;
    private CalendarEvent[] allEventsChron; //chronologically all events
    private boolean[] isEventStart; //if false, event is for the ending, not starting



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.content_day2); //Changed to test it normally activity_day
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starts");


        //initiate...
        pullEventsOfDay();
        setOrderedEventStarts();
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
        dialogBuilder = new AlertDialog.Builder(this);
        addEventPopupView = (LinearLayout) getLayoutInflater().inflate(R.layout.add_event_popup, null);

        // Adding event type and name choices to popup
        final View typeAndNameView = getLayoutInflater().inflate(R.layout.add_event_type_and_name_item, null);

        event_type_spinner = (Spinner) typeAndNameView.findViewById(R.id.event_type_spinner);
        ArrayAdapter<String> eventNamesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.event_types));
        event_type_spinner.setAdapter(eventNamesAdapter);
        event_type_spinner.setOnItemSelectedListener(this);
        event_name = (EditText) typeAndNameView.findViewById(R.id.event_name_edit);

        // If event name and type are given, adds other properties when clicked
        next = (Button) typeAndNameView.findViewById(R.id.add_event_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventName = event_name.getText().toString();
                if (!eventName.equals("") && eventType != null) {
                    // todo create event object
                    next.setEnabled(false);
                    updatePopupView();
                }
                else {
                    Toast.makeText(DayActivity.this, "Please enter event name",
                                        Toast.LENGTH_SHORT).show();
                }
            }
        });
        addEventPopupView.addView(typeAndNameView);

        dialogBuilder.setView(addEventPopupView);
        dialog = dialogBuilder.create();
        dialog.show();
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
        dialogBuilder.setView(addEventPopupView);
    }

    /**
     * Adds due date view to add event popup
     * @author Elifsena Öz
     */
    private void addDueDate() {
        final View dueDateView = getLayoutInflater().inflate(R.layout.add_event_due_date_item, null);
        event_due = (EditText) dueDateView.findViewById(R.id.due_time);
        addEventPopupView.addView(dueDateView);
    }

    /**
     * Adds interval view to add event popup
     * @author Elifsena Öz
     */
    private void addInterval() {
        final View intervalView = getLayoutInflater().inflate(R.layout.add_event_interval_item, null);
        event_start = (EditText) intervalView.findViewById(R.id.add_event_start_time);
        event_end = (EditText) intervalView.findViewById(R.id.add_event_end_time);
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
            number_of_repetitions = (EditText) repetitionView.findViewById(R.id.num_of_reperitions);

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
            repeat.performClick();
            addEventPopupView.addView(repetitionView);
        }
    }

    /**
     * Adds common views of events (notification, notes, colour, location ect) to add event popup
     * @author Elifsena Öz
     */
    private void addCommonItems() {
        // todo
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
        calendar3.set( Calendar.HOUR , 1);
        calendar3.set( Calendar.MINUTE , 30);
        calendar4.set( Calendar.HOUR , 1);
        calendar4.set( Calendar.MINUTE , 33);
        events[0] = new CalendarEvent( calendar3, calendar4, "You Have A Meeting" + calendar3.get( Calendar.MINUTE), 2, "Meeting");


        calendar.set( Calendar.HOUR , 1);
        calendar.set( Calendar.MINUTE , 1);
        calendar2.set( Calendar.HOUR , 1);
        calendar2.set( Calendar.MINUTE , 3);
        events[1] = new CalendarEvent( calendar, calendar2, "You Have A Meeting"+ calendar.get( Calendar.MINUTE), 1, "Meeting");


        Calendar calendar5 = Calendar.getInstance();
        Calendar calendar6 = Calendar.getInstance();
        calendar5.set( Calendar.HOUR , 2);
        calendar5.set( Calendar.MINUTE , 1);
        calendar6.set( Calendar.HOUR , 2);
        calendar6.set( Calendar.MINUTE , 3);
        events[2] = new CalendarEvent( calendar5, calendar6, "Bruh", 3, "Meeting");

        Calendar calendar7 = Calendar.getInstance();
        Calendar calendar8 = Calendar.getInstance();
        calendar7.set( Calendar.HOUR , 0);
        calendar7.set( Calendar.MINUTE , 1);
        calendar8.set( Calendar.HOUR , 0);
        calendar8.set( Calendar.MINUTE , 3);
        events[3] = new CalendarEvent( calendar7, calendar8, "You Have A Meeting Too", 4, "Meeting");

        Calendar calendar9 = Calendar.getInstance();
        Calendar calendar10 = Calendar.getInstance();
        calendar9.set( Calendar.HOUR , 1);
        calendar9.set( Calendar.MINUTE , 10);
        calendar10.set( Calendar.HOUR , 1);
        calendar10.set( Calendar.MINUTE , 20);
        events[4] = new CalendarEvent( calendar9, calendar10, "You Have A Meeting Too" + calendar9.get( Calendar.MINUTE), 5, "Meeting");

    }
    private int clockToInt( Calendar cal) {
        int result;
        result = cal.get( Calendar.HOUR) * 60 + cal.get( Calendar.MINUTE);
        return result;
    }
    /**
     * orders today's events in chronological order and puts the result value to events property
     * @author Alperen Utku Yalçın
     */
    private void setOrderedEventStarts() {
        Calendar calendar1000 = Calendar.getInstance();
        calendar1000.set( Calendar.HOUR , 11);
        calendar1000.set( Calendar.MINUTE , 59);
        CalendarEvent[] result = new CalendarEvent[events.length];
        boolean commander[] = new boolean[events.length];
        int counter;
        int size = events.length;
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
        int length = events.length;
        CalendarEvent[] result = new CalendarEvent[ length];
        for ( int i = 0; i < length; i++) {
            result[i] = events[i];
        }
        for ( int i = result.length - 1; i >= 0; i--) {
            for ( int a = i - 1; a >= 0; a--) {
                if ( result[i].getEventStart().get( Calendar.HOUR) * 60 + result[i].getEventStart().get( Calendar.MINUTE)
                        < result[a].getEventEnd().get( Calendar.HOUR) * 60 + result[a].getEventEnd().get( Calendar.MINUTE)) {
                    result = moveEventArrayByOne( result, i);
                    result[ i] = result[ a];
                    result = discardEventArrayByOne( result, a);
                }
            }
        }
        orderedEventEnds = result;
    }
    /**
     * helper method for setOrderedEventEnds() or setOrderedEventStarts()
     * lengthens the array by one from a specified index
     * @author Alperen Utku Yalçın
     */
    private CalendarEvent[] moveEventArrayByOne( CalendarEvent[] eventArray, int index) {
        CalendarEvent[] result = new CalendarEvent[ eventArray.length + 1];
        for ( int i = 0; i <= index; i ++ ) {
            result[i] = eventArray[i];
        }
        for (int i = index; i < eventArray.length; i++) {
            result[i + 1] = eventArray[i];
        }
        return  result;
    }
    /**
     * helper method for setOrderedEventEnds() or setOrderedEventStarts()
     * discards a value in an array. Array does not get shortened.
     * @author Alperen Utku Yalçın
     */
    private CalendarEvent[] discardEventArrayByOne( CalendarEvent[] eventArray, int index) {
        CalendarEvent[] newEventArray = new CalendarEvent[eventArray.length - 1];

        if ( eventArray.length > 1) {
            for ( int i = 0; i < index; i++) {
            newEventArray[i] = eventArray[i];
            }
            for ( int i = index + 1; i < eventArray.length; i++) {
                newEventArray[i - 1] = eventArray[ i];
            }
        }

        return newEventArray;
    }
    /**
     * finds the appropriate texts for each event start time and end time for the textViews.
     * @author Alperen Utku Yalçın
     */
    private void getTextsOfEvents( ) {
        textsOfHours = new String[24][];
        int[] eventCount = new int[24];
        for ( int i = 0; i < events.length ; i++) {
            eventCount[ events[i].getEventStart().get(Calendar.HOUR) ] += 1;
            eventCount[ events[i].getEventEnd().get(Calendar.HOUR) ] += 1;
        }

        for ( int i = 0; i < events.length ; i++) {
            textsOfHours[ events[i].getEventStart().get(Calendar.HOUR) ][ eventCount[i]] =
                            events[i].getEventStart().get(Calendar.HOUR) + ":" +
                            events[i].getEventStart().get(Calendar.MINUTE);
            textsOfHours[ events[i].getEventEnd().get(Calendar.HOUR) ][ eventCount[i]] =
                            events[i].getEventStart().get(Calendar.HOUR) + ":" +
                            events[i].getEventStart().get(Calendar.MINUTE) + " " +
                            events[i].getName();
        }

    }
    /**
     * Creates an event arraqy with chronologically ordered event start-end times.
     * @author Alperen Utku Yalçın
     */
    public void EventChronologyCreate() {
        /*
        int counter = 0;
        setOrderedEventStarts();
        setOrderedEventEnds();
        boolean breaker = false;

        allEventsChron = new CalendarEvent[( events.length + orderedEventEnds.length) - 1];
        isEventStart = new boolean[(events.length + orderedEventEnds.length) - 1];

        for( int i = 0; i < events.length; i++) {
            breaker = false;
            for ( int a = 0; i < orderedEventEnds.length; i++) {
                if ( orderedEventEnds[a].getEventEnd().get( Calendar.HOUR) * 60
                        + orderedEventEnds[a].getEventEnd().get( Calendar.MINUTE)
                        < events[i].getEventStart().get( Calendar.HOUR) * 60
                        + events[i].getEventStart().get( Calendar.MINUTE) && ! breaker) {
                    allEventsChron[counter] = orderedEventEnds[a];
                    orderedEventEnds = discardEventArrayByOne( orderedEventEnds, a);
                    isEventStart[counter] = false;
                }
                else {
                    allEventsChron[counter] = events[i];
                    events = discardEventArrayByOne( events, i);
                    breaker = true;
                    isEventStart[counter] = true;
                }
                counter++;
            }
        }
        */
        setOrderedEventStarts();
        allEventsChron = new CalendarEvent[ events.length];
        isEventStart = new boolean[ events.length];
        for ( int i = 0 ; i < events.length; i++) {
            isEventStart[i] = true;
        }
        allEventsChron = events;
    }
    /**
     * Creates and sets the textViews with appropriate texts
     * @author Alperen Utku Yalçın
     */
    private void createTextView( RelativeLayout layout, int hour) {
        //todo
        String str;
        View recent = layout;
        for ( int i = 0; i < allEventsChron.length; i++) {
            if ( isEventStart[i]) {
                str = hour + ":" + allEventsChron[i].getEventStart().get( Calendar.MINUTE);
                str += " " + allEventsChron[i].getName();
            }
            else {
                str = hour + ":" + allEventsChron[i].getEventEnd().get( Calendar.MINUTE);
                str += " " + allEventsChron[i].getName();
            }

            if ( discriminateEvent( allEventsChron[i], isEventStart[i]).get( Calendar.HOUR) == hour) {
                TextView textView = new TextView(DayActivity.this);

                textView.setId( hour * 60 + discriminateEvent( allEventsChron[i], isEventStart[i]).get( Calendar.MINUTE));
                RelativeLayout.LayoutParams layoutParams = new
                        RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.BELOW, recent.getId());

                textView.setText( str);
                textView.setSingleLine();
                System.out.println( str);
                layout.addView(textView, layoutParams);
                recent = textView;

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textView.setText( "POG");

                    }
                });


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
    - find a way to make a line for each event. //CANCELLED
    - a method that finds which textviews are in an interval
                of events and gives them gaps at their start
    - get events from database
     and other methods I haven't thought about yet....
    */
}
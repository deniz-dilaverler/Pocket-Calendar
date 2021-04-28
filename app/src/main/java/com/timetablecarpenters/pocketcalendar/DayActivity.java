package com.timetablecarpenters.pocketcalendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.RequiresApi;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;


public class DayActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "DayActivity";
    public static final String EVENT_ID_PREF = "eventID";
    public static final String EVENT_ID_VALUE = "value";
    private AlertDialog.Builder addEventBuilder;
    private AlertDialog addEventDialog;
    private Spinner event_type_spinner, notification_spinner;
    private Spinner repetition_type;
    private TextView event_due_time, event_date, event_start, event_end;
    private EditText event_name, number_of_repetitions, notes;
    private LinearLayout addEventPopupView;
    private Button next, save, blue;
    private CheckBox repeat, notification;
    private String eventType, eventName;
    private ScrollView scrollView;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private CalendarEvent addedEvent;
    private int endHour, endMinute, startHour, startMinute;
    private Calendar eventStart, eventEnd, date;
    private long eventID;


    private CalendarEvent[] events; //the events in day
    private CalendarEvent[] orderedEventEnds; //the events in day
    // first [] for hours of day, next [] for the event starting times and end times in order.
    private String[][] textsOfHours;
    private CalendarEvent[] allEventsChron; //chronologically all events
    private boolean[] isEventStart; //if false, event is for the ending, not starting
    private DBHelper database;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.content_day3); //Changed to test it normally activity_day
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starts");
        //initiate...
        database = new DBHelper(this, DBHelper.DB_NAME, null);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set( Calendar.HOUR, 0);
        calendar1.set( Calendar.MINUTE, 0);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set( Calendar.HOUR, 23);
        calendar2.set( Calendar.MINUTE, 59);
        pullEventsOfDay( database.getEventsInAnIntervalInArray( calendar1, calendar2));
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

        // Displays a dialog to pick a date
        event_date = (TextView) dueDateView.findViewById(R.id.due_date);
        event_date.setText(getTodaysDate());

        event_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = Calendar.getInstance();
                final int day = date.get(Calendar.DAY_OF_MONTH);
                final int month = date.get(Calendar.MONTH);
                final int year = date.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(DayActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                event_date.setText(formattedMonth(month) + " " + dayOfMonth + " " + year);
            }
        };

        // Displays a dialog to pick time
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
                                eventEnd.set(date.YEAR, date.MONTH, date.DAY_OF_MONTH, endHour, endMinute);
                                eventStart = (Calendar) eventEnd.clone();
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
     * Reports the date of the current day in string form
     * @return Month Day Year (ex. May 1 2021)
     */
    private String getTodaysDate() {
        Calendar todaysDate = Calendar.getInstance();
        final int day = todaysDate.get(Calendar.DAY_OF_MONTH);
        final int month = todaysDate.get(Calendar.MONTH);
        final int year = todaysDate.get(Calendar.YEAR);
        return formattedMonth(month) + " " + day + " " + year;
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

    /**
     * Adds interval view to add event popup
     * @author Elifsena Öz
     */
    private void addInterval() {
        final View intervalView = getLayoutInflater().inflate(R.layout.add_event_interval_item, null);

        // Displays a dialog to pick a date
        event_date = (TextView) intervalView.findViewById(R.id.event_date);
        event_date.setText(getTodaysDate());
        event_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = Calendar.getInstance();
                final int day = date.get(Calendar.DAY_OF_MONTH);
                final int month = date.get(Calendar.MONTH);
                final int year = date.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(DayActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                event_date.setText(formattedMonth(month) + " " + dayOfMonth + " " + year);
            }
        };

        // Displays a dialog to pick starting time
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
                                eventStart.set(date.YEAR, date.MONTH, date.DAY_OF_MONTH, startHour, startMinute);
                                event_start.setText("Start: " + startHour + ":" + startMinute);
                            }
                        },24,0,true
                );
                timePickerDialog.updateTime(startHour, startMinute);
                timePickerDialog.show();
            }
        });

        // Displays a dialog to pick ending time
        // todo check if event_end is after event_start
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
                                eventEnd.set(date.YEAR, date.MONTH, date.DAY_OF_MONTH, endHour, endMinute);
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

            // displays repetition options if the box is checked
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

        save = (Button) commonItemsView.findViewById(R.id.add_event_done);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                loadData();
                addedEvent = new CalendarEvent(eventStart, eventEnd, eventName, eventID, eventType);
                DBHelper dbHelper = new DBHelper(DayActivity.this, DBHelper.DB_NAME, null);
                dbHelper.insertEvent(addedEvent);
            }
        });

        addEventPopupView.addView(commonItemsView);
    }

    public void saveData() {
        SharedPreferences eventIDPref = getSharedPreferences(EVENT_ID_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = eventIDPref.edit();
        editor.putLong(EVENT_ID_VALUE, eventID + 1);
    }

    public void loadData() {
        SharedPreferences eventIDPref = getSharedPreferences(EVENT_ID_PREF, MODE_PRIVATE);
        eventID = eventIDPref.getLong(EVENT_ID_VALUE, 7); // for testing purposes
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void pullEventsOfDay( ArrayList<CalendarEvent> calEvents) {
        //todo - once the other methods work
        //for now
        int counter;

        ArrayList<CalendarEvent> arrayListEvents = new ArrayList<>();
        Calendar calendar3 = Calendar.getInstance();
        Calendar calendar4 = Calendar.getInstance();
        calendar3.set( Calendar.HOUR_OF_DAY , 10);
        calendar3.set( Calendar.MINUTE , 30);
        calendar4.set( Calendar.HOUR_OF_DAY , 13);
        calendar4.set( Calendar.MINUTE , 33);
        arrayListEvents.add( new CalendarEvent( calendar3, calendar4, "You Have A Meeting" + calendar3.get( Calendar.MINUTE), 2, "Meeting"));
        arrayListEvents.get(0).setColor( Color.RED);

        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar.set( Calendar.HOUR_OF_DAY , 0);
        calendar.set( Calendar.MINUTE , 1);
        calendar2.set( Calendar.HOUR_OF_DAY , 2);
        calendar2.set( Calendar.MINUTE , 3);
        arrayListEvents.add( new CalendarEvent( calendar, calendar2, "You Have A Meeting"+ calendar.get( Calendar.MINUTE), 1, "Meeting"));
        arrayListEvents.get(1).setColor( Color.BLUE);

        Calendar calendar5 = Calendar.getInstance();
        Calendar calendar6 = Calendar.getInstance();
        calendar5.set( Calendar.HOUR_OF_DAY , 2);
        calendar5.set( Calendar.MINUTE , 1);
        calendar6.set( Calendar.HOUR_OF_DAY , 2);
        calendar6.set( Calendar.MINUTE , 3);
        arrayListEvents.add( new CalendarEvent( calendar5, calendar6, "Bruh", 3, "Meeting"));
        arrayListEvents.get(2).setColor( Color.DKGRAY);

        Calendar calendar7 = Calendar.getInstance();
        Calendar calendar8 = Calendar.getInstance();
        calendar7.set( Calendar.HOUR_OF_DAY , 0);
        calendar7.set( Calendar.MINUTE , 1);
        calendar8.set( Calendar.HOUR_OF_DAY , 0);
        calendar8.set( Calendar.MINUTE , 3);
        arrayListEvents.add( new CalendarEvent( calendar7, calendar8, "You Have A Meeting Too", 4, "Meeting"));
        arrayListEvents.get(3).setColor( Color.GREEN);

        Calendar calendar9 = Calendar.getInstance();
        Calendar calendar10 = Calendar.getInstance();
        calendar9.set( Calendar.HOUR_OF_DAY , 11);
        calendar9.set( Calendar.MINUTE , 10);
        calendar10.set( Calendar.HOUR_OF_DAY , 11);
        calendar10.set( Calendar.MINUTE , 20);
        arrayListEvents.add( new CalendarEvent( calendar9, calendar10, "You Have A Meeting Too" + calendar9.get( Calendar.MINUTE), 5, "Meeting"));
        arrayListEvents.get(4).setColor( Color.BLUE);

        Calendar calendar11 = Calendar.getInstance();
        Calendar calendar12 = Calendar.getInstance();
        calendar11.set( Calendar.HOUR_OF_DAY , 20);
        calendar11.set( Calendar.MINUTE , 1);
        calendar12.set( Calendar.HOUR_OF_DAY , 21);
        calendar12.set( Calendar.MINUTE , 50);
        arrayListEvents.add( new CalendarEvent( calendar11, calendar12, "Meet with friends", 4, "Meeting"));
        arrayListEvents.get(5).setColor( Color.MAGENTA);

        Calendar calendar13 = Calendar.getInstance();
        Calendar calendar14 = Calendar.getInstance();
        calendar13.set( Calendar.HOUR_OF_DAY , 18);
        calendar13.set( Calendar.MINUTE , 10);
        calendar14.set( Calendar.HOUR_OF_DAY , 18);
        calendar14.set( Calendar.MINUTE , 20);
        arrayListEvents.add( new CalendarEvent( calendar13, calendar14, "Have a cup of cofee" + calendar9.get( Calendar.MINUTE), 5, "Meeting"));
        arrayListEvents.get(6).setColor( Color.CYAN);

        counter = 0;
        events = new CalendarEvent[ arrayListEvents.size()];
        for ( CalendarEvent event : arrayListEvents) {
            events[counter] = event;
            counter++;
        }
/*
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
            if ( calEvents.get( i) != null) {
                events[counter] = calEvents.get( i);
                counter++;
                System.out.println( "ERROR ERROR ERROR ERROR AAAAAAAAAAAAAAAAAAAA");
            }
        }*/
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
    }
    /**
     * Creates and sets the textViews with appropriate texts
     * @author Alperen Utku Yalçın
     */
    private void createTextView( RelativeLayout layout, int hour) {
        String str;
        View recent = layout;
        for ( int i = 0; i < allEventsChron.length; i++) {
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
                textView.setTextColor(allEventsChron[i].color);
                textView.setSingleLine();
                layout.addView(textView, layoutParams);


                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textView.setText("POG");

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
        RelativeLayout rl6 = findViewById( R.id.rl6);
        layouts[5] = rl6;
        RelativeLayout rl7 = findViewById( R.id.rl7);
        layouts[6] = rl7;
        RelativeLayout rl8 = findViewById( R.id.rl8);
        layouts[7] = rl8;
        RelativeLayout rl9 = findViewById( R.id.rl9);
        layouts[8] = rl9;
        RelativeLayout rl10 = findViewById( R.id.rl10);
        layouts[9] = rl10;
        RelativeLayout rl11 = findViewById( R.id.rl11);
        layouts[10] = rl11;
        RelativeLayout rl12 = findViewById( R.id.rl12);
        layouts[11] = rl12;
        RelativeLayout rl13 = findViewById( R.id.rl13);
        layouts[12] = rl13;
        RelativeLayout rl14 = findViewById( R.id.rl14);
        layouts[13] = rl14;
        RelativeLayout rl15 = findViewById( R.id.rl15);
        layouts[14] = rl15;
        RelativeLayout rl16 = findViewById( R.id.rl16);
        layouts[15] = rl16;
        RelativeLayout rl17 = findViewById( R.id.rl17);
        layouts[16] = rl17;
        RelativeLayout rl18 = findViewById( R.id.rl18);
        layouts[17] = rl18;
        RelativeLayout rl19 = findViewById( R.id.rl19);
        layouts[18] = rl19;
        RelativeLayout rl20 = findViewById( R.id.rl20);
        layouts[19] = rl20;
        RelativeLayout rl21 = findViewById( R.id.rl21);
        layouts[20] = rl21;
        RelativeLayout rl22 = findViewById( R.id.rl22);
        layouts[21] = rl22;
        RelativeLayout rl23 = findViewById( R.id.rl23);
        layouts[22] = rl23;
        RelativeLayout rl24 = findViewById( R.id.rl24);
        layouts[23] = rl24;
        for ( int i = 0; i < 24 ; i++) {
            createTextView( layouts[i], i);
        }
    }

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
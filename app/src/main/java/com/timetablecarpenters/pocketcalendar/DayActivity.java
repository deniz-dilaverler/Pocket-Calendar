package com.timetablecarpenters.pocketcalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;


public class DayActivity extends BaseActivity  {
    private static final String TAG = "DayActivity";
    public static final String EVENT_KEY = "event";
    public static final String EVENT_ID_PREF = "eventID";
    public static final String EVENT_ID_VALUE = "value";

    private AlertDialog.Builder addEventBuilder, eventBuilder;
    private AlertDialog addEventDialog, eventDialog;
    private Spinner event_type_spinner, notification_spinner;
    private Spinner repetition_type, color_spinner;
    private TextView event_due_time, event_date, event_start, event_end;
    private EditText event_name, number_of_repetitions, notes;
    private LinearLayout addEventPopupView;
    private Button next, save;
    private CheckBox repeat, notification;
    private String notifType, repetitionType;
    private ScrollView scrollView;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button[] colour_buttons;
    private CalendarEvent addedEvent;
    private int startHour, startMinute, endHour, endMinute, eventColour;
    private Calendar eventDate;
    private long eventID;
    private Button locationSelect;
    private MapFragment mapFragment;


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
                addedEvent = (CalendarEvent) extras.get(MapActivity.EVENT_KEY);
                thisDay = addedEvent.getEventStart();
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
        createNotificationChannel();

        View someView = findViewById( R.id.day_back_color1);
        someView.setBackgroundColor( CustomizableScreen2.backgroundColor);
        someView = findViewById( R.id.day_back_color2);
        someView.setBackgroundColor( getButtonColor());
        setOtherTexts();

        editInTextFont( findViewById( R.id.textView8));

        FloatingActionButton fab = findViewById(R.id.add_event_button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        if(addedEvent == null) {
            addedEvent = new CalendarEvent(null, null, null, eventID, null);
            addedEvent.setColor(R.color.primary_text);
        }
        addNameAndType();

        // Next button removes itself from the popup if event type and name are given
        final View nextButtonView = (View) getLayoutInflater().inflate(R.layout.add_event_next, null);
        next = (Button) nextButtonView.findViewById(R.id.add_event_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!event_name.getText().toString().equals("") && addedEvent.getType() != null) {
                    addedEvent.setName(event_name.getText().toString());
                    event_name.setEnabled(false);
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
        if (addedEvent.getType().equals("Assignment")) {
            addDueDate();
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
        event_name = (EditText) typeAndNameView.findViewById(R.id.add_event_name);
        editParagraphFont(event_name);
        addEventPopupView.addView(typeAndNameView);
    }

    /**
     * Adds due date view to add event popup
     * @author Elifsena Öz
     */
    private void addDueDate() {
        final View dueDateView = getLayoutInflater().inflate(R.layout.add_event_due_date_item, null);

        // Displays a dialog to pick a date
        event_date = (TextView) dueDateView.findViewById(R.id.add_due_date);
        event_date.setText(getTodaysDate());
        eventDate = (Calendar) thisDay.clone();

        event_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int day = eventDate.get(Calendar.DATE);
                final int month = eventDate.get(Calendar.MONTH);
                final int year = eventDate.get(Calendar.YEAR);

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
                eventDate.set(year, month, dayOfMonth);
                event_date.setText(formattedMonth(month) + " " + dayOfMonth + " " + year);
            }
        };

        // Displays a dialog to pick time
        event_due_time = (TextView) dueDateView.findViewById(R.id.add_due_time);
        event_due_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        DayActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Log.d(TAG, "due date picked");
                                endHour = hourOfDay;
                                endMinute = minute;
                                Calendar dueTime = Calendar.getInstance();
                                dueTime.set(eventDate.get(Calendar.YEAR), eventDate.get(Calendar.MONTH),
                                        eventDate.get(Calendar.DATE), endHour, endMinute);
                                addedEvent.setEventEnd(dueTime);
                                addedEvent.setEventStart(dueTime);
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

        // Displays a dialog to pick a date
        event_date = (TextView) intervalView.findViewById(R.id.add_event_date);
        event_date.setText(getTodaysDate());
        eventDate = (Calendar) thisDay.clone();

        event_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int day = eventDate.get(Calendar.DATE);
                final int month = eventDate.get(Calendar.MONTH);
                final int year = eventDate.get(Calendar.YEAR);

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
                eventDate.set(year, month, dayOfMonth);
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
                                Calendar eventStart = Calendar.getInstance();
                                eventStart.set(eventDate.get(Calendar.YEAR), eventDate.get(Calendar.MONTH),
                                        eventDate.get(Calendar.DAY_OF_MONTH), startHour, startMinute);
                                addedEvent.setEventStart(eventStart);
                                event_start.setText("Start: " + startHour + ":" + startMinute);
                            }
                        },24,0,true
                );
                timePickerDialog.updateTime(startHour, startMinute);
                timePickerDialog.show();
            }
        });

        // Displays a dialog to pick ending time
        event_end = (TextView) intervalView.findViewById(R.id.add_event_end);
        event_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        DayActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                int endHour = hourOfDay;
                                int endMinute = minute;
                                Calendar eventEnd = Calendar.getInstance();
                                eventEnd.set(eventDate.get(Calendar.YEAR), eventDate.get(Calendar.MONTH),
                                        eventDate.get(Calendar.DATE), endHour, endMinute);
                                addedEvent.setEventEnd(eventEnd);
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
     * Reports the date of the current day in string form
     * @return Month Day Year (ex. May 1 2021)
     */
    private String getTodaysDate() {
        final int day = thisDay.get(Calendar.DAY_OF_MONTH);
        final int month = thisDay.get(Calendar.MONTH);
        final int year = thisDay.get(Calendar.YEAR);
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
     * Adds repetition view to add event popup
     * @author Elifsena Öz
     */
    private void addRepetition() {
        if (!addedEvent.getType().equals("Exam")) {
            final View repetitionView = getLayoutInflater().inflate(R.layout.add_event_repetition_item, null);

            repetition_type = (Spinner) repetitionView.findViewById(R.id.repetition_type);
            ArrayAdapter<String> repetitionTypesAdapter = new ArrayAdapter<>(DayActivity.this,
                    android.R.layout.simple_spinner_item,
                    getResources().getStringArray(R.array.repetition_types));
            repetition_type.setAdapter(repetitionTypesAdapter);
            repetition_type.setOnItemSelectedListener(DayActivity.this);
            repetition_type.setEnabled(false);
            number_of_repetitions = (EditText) repetitionView.findViewById(R.id.num_of_repetitions);
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

        // initialize the spinner for notifications
        notification_spinner = (Spinner) commonItemsView.findViewById(R.id.notifications_spinner);
        ArrayAdapter<String> notificationTimesAdapter = new ArrayAdapter<>(DayActivity.this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.notification_times));
        notification_spinner.setAdapter(notificationTimesAdapter);
        notification_spinner.setEnabled(false);

        // initialize the checkbox for notifications
        notification = (CheckBox) commonItemsView.findViewById(R.id.notification_checkbox);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // disable spinner when checkbox is not checked
                if (notification.isChecked())
                    notification_spinner.setEnabled(true);
                else
                    notification_spinner.setEnabled(false);
            }
        });

        // initialize colour buttons
        colour_buttons = new Button[8];
        colour_buttons[0] = commonItemsView.findViewById(R.id.colour_ligth_blue);
        colour_buttons[1] = commonItemsView.findViewById(R.id.colour_blue);
        colour_buttons[2] = commonItemsView.findViewById(R.id.colour_purple);
        colour_buttons[3] = commonItemsView.findViewById(R.id.colour_pink);
        colour_buttons[4] = commonItemsView.findViewById(R.id.colour_red);
        colour_buttons[5] = commonItemsView.findViewById(R.id.colour_orange);
        colour_buttons[6] = commonItemsView.findViewById(R.id.colour_yellow);
        colour_buttons[7] = commonItemsView.findViewById(R.id.colour_green);

        for (Button b : colour_buttons) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // enable all buttons except the one clicked
                    for ( Button d : colour_buttons) {
                        d.setEnabled(true);
                    }
                    b.setEnabled(false);

                    // check which color is choosen
                    if (b.getId() == R.id.colour_ligth_blue)
                        addedEvent.setColor(R.color.ligth_blue);
                    else if (b.getId() == R.id.colour_blue)
                        addedEvent.setColor(R.color.dark_blue);
                    else if (b.getId() == R.id.colour_purple)
                        addedEvent.setColor(R.color.purple);
                    else if (b.getId() == R.id.colour_pink)
                        addedEvent.setColor(R.color.pink);
                    else if (b.getId() == R.id.colour_red)
                        addedEvent.setColor(R.color.red);
                    else if (b.getId() == R.id.colour_orange)
                        addedEvent.setColor(R.color.orange);
                    else if (b.getId() == R.id.colour_yellow)
                        addedEvent.setColor(R.color.yellow);
                    else if (b.getId() == R.id.colour_green)
                        addedEvent.setColor(R.color.green);

                    // create message to inform the user
                    if (addedEvent.getColor() != R.color.primary_text)
                        Toast.makeText(DayActivity.this, "Colour chosen",
                                Toast.LENGTH_SHORT).show();
                }
            });
        }

        notes = (EditText) commonItemsView.findViewById(R.id.add_notes);
        editParagraphFont(notes);

        save = (Button) commonItemsView.findViewById(R.id.add_event_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: eventToAdd: " + addedEvent.getEventStart() + " end: " + addedEvent.getEventEnd());
                if (!addedEvent.getType().equals("Assignment") &&  (addedEvent.getEventEnd() == null || addedEvent.getEventEnd() == null)) {
                    Log.d(TAG, "onClick: error message 1");
                    Toast.makeText( DayActivity.this, "Please  choose event interval",
                                Toast.LENGTH_LONG).show();
                }
                else if (addedEvent.getType().equals("Assignment") && addedEvent.getEventEnd() == null) {
                    Log.d(TAG, "onClick: error message 2");
                    Toast.makeText( DayActivity.this, "Please choose due time",
                            Toast.LENGTH_LONG).show();
                }
                else if (addedEvent.getEventStart().compareTo(addedEvent.getEventEnd()) > 0) {
                    Log.d(TAG, "onClick: error message 3");
                    Toast.makeText(DayActivity.this, "Event start cannot be after event end",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    saveData();
                    loadData();
                    if (notes.getText() !=  null) {
                        addedEvent.setNotes(notes.getText().toString());
                    }
                    DBHelper dbHelper = new DBHelper(DayActivity.this, DBHelper.DB_NAME, null);
                    Log.d(TAG, "onClick: Right before event inserted");
                    long insertResult = dbHelper.insertEvent(addedEvent);
                    if (insertResult == -1)
                        Toast.makeText(DayActivity.this, "Event couldn't be saved", Toast.LENGTH_SHORT).show();
                    else if (insertResult == -2)
                        Toast.makeText(DayActivity.this, "Event already exists", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(DayActivity.this, "Event successfully added", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DayActivity.this, DayActivity.class);
                    intent.putExtra(INTENT_KEY, thisDay );
                    addEventDialog.dismiss();
                    startActivity(intent);
                }
                if ( notification.isChecked()) {
                    String notificationSpinner = notification_spinner.getSelectedItem().toString();
                    Intent intent = new Intent(DayActivity.this, ReminderBroadCast.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(DayActivity.this, 0, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    long dueTimeInMs = addedEvent.getEventStart().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                    long differenceToDue;
                    if( notificationSpinner.equalsIgnoreCase("5 minutes prior"))
                    {
                        differenceToDue = 5 * 60 * 1000;
                    }
                    else if( notificationSpinner.equalsIgnoreCase("10 minutes prior"))
                    {
                        differenceToDue = 10 * 60 * 1000;
                    }
                    else if( notificationSpinner.equalsIgnoreCase("30 minutes prior"))
                    {
                        differenceToDue = 30 * 60 * 1000;
                    }
                    else if( notificationSpinner.equalsIgnoreCase("1 hour prior"))
                    {
                        differenceToDue = 60 * 60 * 1000;
                    }
                    else if( notificationSpinner.equalsIgnoreCase("6 hours prior"))
                    {
                        differenceToDue = 6* 60 * 60 * 1000;
                    }
                    else
                    {
                        differenceToDue = 12 * 60 * 60 * 1000;
                    }
                    Log.d(TAG, "dueTimeInMs - differenceToDue " + (dueTimeInMs - differenceToDue) );
                    Log.d(TAG, "dueTimeInMs - differenceToDue " + (dueTimeInMs + "-" + differenceToDue) );
                    Log.d(TAG, "dueTimeInMs - differenceToDue " + addedEvent.getEventStart().toString() );

                    alarmManager.set(AlarmManager.RTC_WAKEUP, dueTimeInMs - differenceToDue, pendingIntent);
                }
                if( repeat.isChecked()) {
                    repetition_type.setEnabled(false);
                    number_of_repetitions.setEnabled(false);
                    if (number_of_repetitions != null && repetitionType != null) {
                        int number = Integer.parseInt(number_of_repetitions.getText().toString());
                        Log.d(TAG, "onClick: repetition number " + number );

                        if (repetitionType.equalsIgnoreCase("Monthly")) {
                            Calendar newEventStart = addedEvent.getEventStart();
                            Calendar newEventEnd = addedEvent.getEventEnd();
                            DBHelper dbHelper = new DBHelper(DayActivity.this,DBHelper.DB_NAME,null);
                            for (int i = 0; i< number;i++) {
                                newEventStart.add(Calendar.MONTH, 1);
                                newEventEnd.add(Calendar.MONTH, 1);
                                saveData();
                                loadData();
                                CalendarEvent newEvent = new CalendarEvent(newEventStart, newEventEnd, addedEvent.getName(),
                                        eventID, addedEvent.getType());
                                newEvent.setColor(addedEvent.getColor());
                                newEvent.setLocation(addedEvent.getLocation());
                                newEvent.setNotes(addedEvent.getNotes());
                                newEvent.setNotifTime(addedEvent.getNotifTime());
                                dbHelper.insertEvent(newEvent);
                            }
                        }
                        else if (repetitionType.equalsIgnoreCase("Daily")) {
                            Calendar newEventStart = addedEvent.getEventStart();
                            Calendar newEventEnd = addedEvent.getEventEnd();
                            DBHelper dbHelper = new DBHelper(DayActivity.this,DBHelper.DB_NAME,null);
                            Log.d(TAG, "onClick: database helper intialized");
                            for (int i = 0; i< number;i++) {
                                newEventStart.add(Calendar.DAY_OF_MONTH, 1);
                                newEventEnd.add(Calendar.DAY_OF_MONTH, 1);
                                saveData();
                                loadData();
                                CalendarEvent newEvent = new CalendarEvent(newEventStart, newEventEnd, addedEvent.getName(),
                                        eventID, addedEvent.getType());
                                newEvent.setColor(addedEvent.getColor());
                                newEvent.setLocation(addedEvent.getLocation());
                                newEvent.setNotes(addedEvent.getNotes());
                                newEvent.setNotifTime(addedEvent.getNotifTime());
                                dbHelper.insertEvent(newEvent);
                            }
                        }
                        else if (repetitionType.equalsIgnoreCase("Annually")) {
                            Calendar newEventStart = addedEvent.getEventStart();
                            Calendar newEventEnd = addedEvent.getEventEnd();
                            DBHelper dbHelper = new DBHelper(DayActivity.this,DBHelper.DB_NAME,null);
                            for (int i = 0; i< number;i++) {
                                newEventStart.add(Calendar.YEAR, 7);
                                newEventEnd.add(Calendar.YEAR, 7);
                                saveData();
                                loadData();
                                CalendarEvent newEvent = new CalendarEvent(newEventStart, newEventEnd, addedEvent.getName(),
                                        eventID, addedEvent.getType());
                                newEvent.setColor(addedEvent.getColor());
                                newEvent.setLocation(addedEvent.getLocation());
                                newEvent.setNotes(addedEvent.getNotes());
                                newEvent.setNotifTime(addedEvent.getNotifTime());
                                dbHelper.insertEvent(newEvent);
                            }
                        }
                        else if (repetitionType.equalsIgnoreCase("Weekly")) {
                            Calendar newEventStart = addedEvent.getEventStart();
                            Calendar newEventEnd = addedEvent.getEventEnd();
                            DBHelper dbHelper = new DBHelper(DayActivity.this,DBHelper.DB_NAME,null);
                            for (int i = 0; i< number;i++) {
                                newEventStart.add(Calendar.DAY_OF_MONTH,7);
                                newEventEnd.add(Calendar.DAY_OF_MONTH,7);
                                saveData();
                                loadData();
                                CalendarEvent newEvent = new CalendarEvent(newEventStart, newEventEnd, addedEvent.getName(),
                                        eventID, addedEvent.getType());
                                newEvent.setColor(addedEvent.getColor());
                                newEvent.setLocation(addedEvent.getLocation());
                                newEvent.setNotes(addedEvent.getNotes());
                                newEvent.setNotifTime(addedEvent.getNotifTime());
                                dbHelper.insertEvent( newEvent);
                            }
                        }
                    }
                }
            }
        });

        addEventPopupView.addView(commonItemsView);
    }

    /**
     * Tracks event id
     */
    public void saveData() {
        SharedPreferences eventIDPref = getSharedPreferences(EVENT_ID_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = eventIDPref.edit();
        editor.putLong(EVENT_ID_VALUE, eventID + 1);
    }

    /**
     * Tracks event id
     */
    public void loadData() {
        SharedPreferences eventIDPref = getSharedPreferences(EVENT_ID_PREF, MODE_PRIVATE);
        eventID = eventIDPref.getLong(EVENT_ID_VALUE, 1); // for testing purposes
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.event_type_spinner)
            addedEvent.setType(parent.getItemAtPosition(position).toString());
        else if (parent.getId() == R.id.repetition_type) {
            repetitionType = parent.getItemAtPosition(position).toString();
        }
        if (parent.getId() == R.id.notifications_spinner)
            notifType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
        ( ( TextView) findViewById( R.id.dateText)).setTextColor( color);
    }
    /**
=======
        fab.setOnClickListener(new DayActivity.ViewChangeClickListener());
    }

    /**
>>>>>>> eventAddActivity
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
                            intent.putExtra(EventActivity.EVENT_VIEW_INTENT_KEY, allEventsChron[finalI1] );
                            startActivity(intent);


                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            

                        }
                    });
                }
            }
        }
    }

    /**
     * Opens a popup that shows information about the event
     * @param event
     */
    private void openEventDialog(CalendarEvent event) {
        eventBuilder = new AlertDialog.Builder(this);
        final View eventView = getLayoutInflater().inflate(R.layout.event_view, null);

        TextView eventName = (TextView) eventView.findViewById(R.id.event_name);
        eventName.setText(event.getName());

        TextView eventType = (TextView) eventView.findViewById(R.id.event_type);
        eventType.setText(event.getType());

        TextView eventTime = (TextView) eventView.findViewById(R.id.event_time);
        if (event.getEventEnd().equals(event.getEventStart())) {
            eventTime.setText(event.getEventStartTime());
        }
        else {
            eventTime.setText(event.getEventStartTime() + " - " + event.getEventEndTime());
        }

        TextView eventNotifications = (TextView) eventView.findViewById(R.id.event_notifications);
        if (event.getNotifTime() != null)
            eventNotifications.setText("Notifications" + event.getNotifTime());

        TextView eventNotes = (TextView) eventView.findViewById(R.id.event_notes);
        if (event.getNotes() != null) {
            eventNotes.setText("Notes: " + event.getNotes());
        }


        //todo initialize buttons

        eventBuilder.setView(eventView);
        eventDialog = eventBuilder.create();
        eventDialog.show();
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
    private int ConvertIntoNumeric(String xVal)
    {
        try
        {
            return Integer.parseInt(xVal);
        }
        catch(Exception ex)
        {
            return 0;
        }
    }

    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("channel","channel1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("simple channel");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


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
                    Log.d(TAG, "onClick: oldu");
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
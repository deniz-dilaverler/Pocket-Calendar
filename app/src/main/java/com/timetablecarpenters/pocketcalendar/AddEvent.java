package com.timetablecarpenters.pocketcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Shows add event view
 * @author Elifsena Öz
 */

public class AddEvent extends BaseActivity implements AdapterView.OnItemSelectedListener{

    public static final String DATE_KEY = "Date";
    public static final String ADD_ACTIVITY_NAME = "add_activity";
    public static final String TAG = "AddEventActivity";
    public static final String EVENT_ID_PREF = "eventID";
    public static final String EVENT_ID_VALUE = "value";
    private Spinner event_type_spinner, notification_spinner;
    private Spinner repetition_type;
    private TextView event_due_time, event_date, event_start, event_end;
    private EditText event_name, number_of_repetitions, notes;
    private LinearLayout linearLayout;
    private Button next, save;
    private CheckBox repeat, notification;
    private String notifType, repetitionType;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button[] colour_buttons;
    private CalendarEvent addedEvent;
    private int startHour, startMinute, endHour, endMinute, eventTypePosition;
    private Calendar eventDate, thisDay;
    private long eventID;
    private Button locationSelect;
    private MapFragment mapFragment;
    LayoutInflater layoutInflater;
    private View addEventView;
    private ArrayAdapter<String> eventTypesAdapter;
    private boolean oldEvent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);
        layoutInflater = getLayoutInflater();

        createNotificationChannel();

        Bundle extras = getIntent().getExtras();
        if ( extras != null) {
            try {
                thisDay = (Calendar) extras.get(DATE_KEY);
            } catch (Exception e) {
                Log.d(TAG, "onCreate: no  date came out of the intent");
            }
            addEvent();
            try {
                if (extras.get(MapActivity.EVENT_KEY) != null) {
                    addedEvent = (CalendarEvent) extras.get(MapActivity.EVENT_KEY);
                    Log.d(TAG, "onCreate: event called back!");
                    setAddEventView();
                }
            } catch (Exception e) {
                Log.d(TAG, "onCreate: no event came out of intent " + e);
            }
        }


    }

    /**
     * Creates initial view
     */
    public void addEvent() {

        addEventView = (View) layoutInflater.inflate(R.layout.activity_event_add, null);
        linearLayout = (LinearLayout) findViewById(R.id.add_event_linear);

        if(addedEvent == null)
            addedEvent = new CalendarEvent(null, null, null, eventID, null);
            addedEvent.setColor(R.color.primary_text);

        final View typeAndNameView = layoutInflater.inflate(R.layout.add_event_type_and_name_item, null);

        event_type_spinner = (Spinner) typeAndNameView.findViewById(R.id.event_type_spinner);
        eventTypesAdapter = new ArrayAdapter<>(AddEvent.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.event_types));
        event_type_spinner.setAdapter(eventTypesAdapter);
        event_type_spinner.setOnItemSelectedListener(this);
        event_name = (EditText) typeAndNameView.findViewById(R.id.add_event_name);
        // editParagraphFont(event_name);

        linearLayout.addView(typeAndNameView);
        Log.d(TAG, "addEvent: successful");

        // Next button removes itself from the popup if event type and name are given
        final View nextButtonView = (View) layoutInflater.inflate(R.layout.add_event_next, null);
        next = (Button) nextButtonView.findViewById(R.id.add_event_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!event_name.getText().toString().equals("") && addedEvent.getType() != null) {
                    addedEvent.setName(event_name.getText().toString());
                    event_name.setEnabled(false);
                    event_type_spinner.setEnabled(false);
                    linearLayout.removeView(nextButtonView);
                    updatePopupView();
                }
                else {
                    Toast.makeText(AddEvent.this, "Please enter event name",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        linearLayout.addView(nextButtonView);
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
    }

    /**
     * Adds due date view to add event popup
     * @author Elifsena Öz
     */
    private void addDueDate() {
        final View dueDateView = layoutInflater.inflate(R.layout.add_event_due_date_item, null);

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

                DatePickerDialog dialog = new DatePickerDialog(AddEvent.this,
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
                        AddEvent.this,
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
        linearLayout.addView(dueDateView);
    }

    /**
     * Adds interval view to add event popup
     * @author Elifsena Öz
     */
    private void addInterval() {
        final View intervalView = layoutInflater.inflate(R.layout.add_event_interval_item, null);

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

                DatePickerDialog dialog = new DatePickerDialog(AddEvent.this,
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
                        AddEvent.this,
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
                        AddEvent.this,
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
        linearLayout.addView(intervalView);
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
            final View repetitionView = layoutInflater.inflate(R.layout.add_event_repetition_item, null);

            repetition_type = (Spinner) repetitionView.findViewById(R.id.repetition_type);
            ArrayAdapter<String> repetitionTypesAdapter = new ArrayAdapter<>(AddEvent.this,
                    android.R.layout.simple_spinner_item,
                    getResources().getStringArray(R.array.repetition_types));
            repetition_type.setAdapter(repetitionTypesAdapter);
            repetition_type.setOnItemSelectedListener(this);
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

            linearLayout.addView(repetitionView);
        }
    }

    /**
     * Adds common views of events (notification, notes, colour, location ect) to add event popup
     * @author Elifsena Öz
     */
    private void addCommonItems() {
        final View commonItemsView = layoutInflater.inflate(R.layout.add_event_common_items, null);

        // initialize the spinner for notifications
        notification_spinner = (Spinner) commonItemsView.findViewById(R.id.notifications_spinner);
        ArrayAdapter<String> notificationTimesAdapter = new ArrayAdapter<>(AddEvent.this,
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

        // set default event colour
        addedEvent.setColor(R.color.primary_text);

        // add onclick listener to buttons
        for (Button b : colour_buttons) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // enable all buttons except the one clicked
                    for ( Button d : colour_buttons) {
                        d.setEnabled(true);
                    }
                    b.setEnabled(false);

                    // check which color is chosen
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
                        Toast.makeText(AddEvent.this, "Colour chosen",
                                Toast.LENGTH_SHORT).show();
                }
            });
        }

        notes = (EditText) commonItemsView.findViewById(R.id.add_notes);
        // editParagraphFont(notes);


        // initialize Location editing UI elements
        MapFragment mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.map_container, mapFragment).commit();

        locationSelect = (Button) commonItemsView.findViewById(R.id.open_map);
        // show the location on the map if the user has chosen one
        if(addedEvent.getLocation()!= null) {
            mapFragment.moveToLocation(addedEvent.getLocation());
        }

        if(GoogleMapsAvailability.isServicesOK(AddEvent.this)) {
            locationSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddEvent.this, MapActivity.class);
                    intent.putExtra(MapActivity.EVENT_KEY, addedEvent);
                    intent.putExtra(MapActivity.INTENT_ID_KEY, ADD_ACTIVITY_NAME);
                    startActivity(intent);
                }
            });
        }

        // initialize the button that saves events
        save = (Button) commonItemsView.findViewById(R.id.add_event_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: eventToAdd: " + addedEvent.getEventStart() + " end: " + addedEvent.getEventEnd());

                // Check if event interval is given for events except assignment
                if (!addedEvent.getType().equals("Assignment") &&  (addedEvent.getEventEnd() == null || addedEvent.getEventEnd() == null)) {
                    Log.d(TAG, "onClick: error message 1");
                    Toast.makeText(AddEvent.this, "Please  choose event interval",
                            Toast.LENGTH_LONG).show();
                }
                // check if due time is given for assignment
                else if (addedEvent.getType().equals("Assignment") && addedEvent.getEventEnd() == null) {
                    Log.d(TAG, "onClick: error message 2");
                    Toast.makeText(AddEvent.this, "Please choose due time",
                            Toast.LENGTH_LONG).show();
                }
                // check event start and event end
                else if (addedEvent.getEventStart().compareTo(addedEvent.getEventEnd()) > 0) {
                    Log.d(TAG, "onClick: error message 3");
                    Toast.makeText(AddEvent.this, "Event start cannot be after event end",
                            Toast.LENGTH_LONG).show();
                }
                // finalize the event and add to the database
                else {
                    linearLayout.removeView(commonItemsView);
                    saveData();
                    loadData();

                    if (notes.getText() !=  null) {
                        addedEvent.setNotes(notes.getText().toString());
                    }

                    DBHelper dbHelper = new DBHelper(AddEvent.this, DBHelper.DB_NAME, null);
                    Log.d(TAG, "onClick: Right before event inserted");
                    long insertResult = dbHelper.insertEvent(addedEvent);

                    // report the user if the event is saved
                    if (insertResult == -1)
                        Toast.makeText(AddEvent.this, "Event couldn't be saved", Toast.LENGTH_SHORT).show();
                    else if (insertResult == -2)
                        Toast.makeText(AddEvent.this, "Event already exists", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(AddEvent.this, "Event successfully added", Toast.LENGTH_SHORT).show();
                }

                if ( notification.isChecked()) {
                    addNotificationToEvent();
                }
                if( repeat.isChecked()) {
                    repeatEvent();
                }
                Intent intent = new Intent(AddEvent.this, DayActivity.class);
                intent.putExtra(DayActivity.INTENT_KEY , thisDay);
                startActivity(intent);
            }
        });

        linearLayout.addView(commonItemsView);
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
        if (parent.getId() == R.id.event_type_spinner) {
            eventTypePosition = position;
            if (addedEvent != null)
                addedEvent.setType(parent.getItemAtPosition(position).toString());
        }
        else if (parent.getId() == R.id.repetition_type) {
            repetitionType = parent.getItemAtPosition(position).toString();
        }
        if (parent.getId() == R.id.notifications_spinner)
            notifType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel", "channel1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("simple channel");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void addNotificationToEvent() {
        String notificationSpinner = notification_spinner.getSelectedItem().toString();
        Intent intent = new Intent(AddEvent.this, ReminderBroadCast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddEvent.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long dueTimeInMs = addedEvent.getEventStart().getTimeInMillis() ;
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

    public void repeatEvent() {
        repetition_type.setEnabled(false);
        number_of_repetitions.setEnabled(false);
        if (number_of_repetitions != null && repetitionType != null) {
            int number = ConvertIntoNumeric(number_of_repetitions.getText().toString());
            Log.d(TAG, "onClick: repetition number " + number );

            if (repetitionType.equalsIgnoreCase("Monthly")) {
                Calendar newEventStart = addedEvent.getEventStart();
                Calendar newEventEnd = addedEvent.getEventEnd();
                DBHelper dbHelper = new DBHelper(AddEvent.this, DBHelper.DB_NAME,null);
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
                DBHelper dbHelper = new DBHelper(AddEvent.this, DBHelper.DB_NAME,null);
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
                DBHelper dbHelper = new DBHelper(AddEvent.this, DBHelper.DB_NAME,null);
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
                DBHelper dbHelper = new DBHelper(AddEvent.this, DBHelper.DB_NAME,null);
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

    /**
     * Sets options for the current event
     */
    public void setAddEventView() {
        Log.d(TAG, "setAddEventView: nonono");
        event_type_spinner.setSelection(eventTypePosition);
        event_name.setText(addedEvent.getName());
        next.performClick();
        event_date.setText(formattedMonth(addedEvent.getEventStart().get(Calendar.MONTH))
                            + " " + addedEvent.getEventStart().get(Calendar.DAY_OF_MONTH)
                            + " " + addedEvent.getEventStart().get(Calendar.YEAR));
        if (addedEvent.getType().equals("Assignment"))
            event_due_time.setText(addedEvent.getEventStartTime());
        else {
            event_start.setText(addedEvent.getEventStartTime());
            event_end.setText(addedEvent.getEventEndTime());
        }
        notes.setText(addedEvent.getNotes());
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

}
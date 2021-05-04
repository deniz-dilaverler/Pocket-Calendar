package com.timetablecarpenters.pocketcalendar;

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

import com.google.android.material.dialog.MaterialDialogs;

import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * Shows add event view
 * @author Elifsena Öz
 */

public class AddEvent extends BaseActivity implements AdapterView.OnItemSelectedListener{

    public static final String DATE_KEY = "Date";
    public static final String EDIT_EVENT_KEY = "edit_event";
    public static final String ADD_ACTIVITY_NAME = "add_activity";
    public static final String EDIT_ACTIVITY_NAME = "edit_activity";
    public static final String TAG = "AddEventActivity";
    public static final String EVENT_ID_PREF = "eventID";
    public static final String EVENT_ID_VALUE = "value";
    private Spinner event_type_spinner, notification_spinner;
    private Spinner repetition_type;
    private TextView title, event_due_time, event_date, event_start, event_end;
    private EditText event_name, number_of_repetitions, notes;
    private LinearLayout linearLayout;
    private Button next, save;
    private CheckBox repeat, notification;
    private String notifType, repetitionType;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button[] colour_buttons;
    private CalendarEvent event;
    private int startHour, startMinute, endHour, endMinute;
    private Calendar eventDate, thisDay;
    private long eventID;
    private Button locationSelect;
    LayoutInflater layoutInflater;
    private View addEventView, repetitionView;
    private ArrayAdapter<String> eventTypesAdapter;
    private boolean isEditView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);
        ( ( TextView) findViewById( R.id.add_event)).setTextColor( CustomizableScreen.getBackGColor());

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
                    event = (CalendarEvent) extras.get(MapActivity.EVENT_KEY);
                    Log.d(TAG, "onCreate: event called back!");
                    Log.d(TAG, "onCreate: event name: " + event.getName() + " start: " + event.getEventStart() +
                            " end: " + event.getEventStart() + " location " + event.getLocation());
                    setAddEventView();
                }
            } catch (Exception e) {
                Log.d(TAG, "onCreate: no event came out of intent " + e);
                e.printStackTrace();
            }
            try {
                if (extras.get(EDIT_EVENT_KEY) != null) {
                    isEditView = true;
                    event = (CalendarEvent) extras.get(EDIT_EVENT_KEY);
                    Log.d(TAG, "onCreate: event to be edited");
                    Log.d(TAG, "onCreate: event name: " + event.getName() + " start: " + event.getEventStart() +
                            " end: " + event.getEventStart() + " location " + event.getLocation());
                    DBHelper dpHelper = new DBHelper(AddEvent.this, DBHelper.DB_NAME, null);
                    dpHelper.deleteEvent(event);
                    setAddEventView();
                    rearrangeToEdit();
                }
            } catch (Exception e ) {
                Log.d(TAG, "onCreate: no event came out of edit event intent " + e);
            }
        }
    }

    /**
     * Creates initial view
     */
    public void addEvent() {

        addEventView = (View) layoutInflater.inflate(R.layout.activity_event_add, null);
        linearLayout = (LinearLayout) findViewById(R.id.add_event_linear);

        if (event == null) {
            event = new CalendarEvent(null, null, null, eventID, null);
        event.setColor(R.color.primary_text);
        }

        title = linearLayout.findViewById(R.id.add_event);
        title.setText("Add Event");
        title.setTextColor( CustomizableScreen.getBackGColor());

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

        // Set colors
        findViewById( R.id.add_event_linear).setBackgroundColor( CustomizableScreen.getButtonColor());
        findViewById( R.id.scroller_back).setBackgroundColor( CustomizableScreen.backgroundColor);
        ( ( TextView) findViewById( R.id.type_textView)).setTextColor( CustomizableScreen.getBackGColor());
        ( ( TextView) findViewById( R.id.name_textView)).setTextColor( CustomizableScreen.getBackGColor());
        ( ( EditText) findViewById( R.id.add_event_name)).setTextColor( CustomizableScreen.getBackGColor());

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!event_name.getText().toString().equals("") && event.getType() != null) {
                    event.setName(event_name.getText().toString());
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
        if (event.getType().equals("Assignment")) {
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
        event_date.setTextColor( CustomizableScreen.getBackGColor());
        ( ( TextView) dueDateView.findViewById( R.id.add_event_due_date)).setTextColor( CustomizableScreen.getBackGColor());
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
        event_due_time.setTextColor( CustomizableScreen.getBackGColor());
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
                                event.setEventEnd(dueTime);
                                event.setEventStart(dueTime);
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
        event_date.setTextColor( CustomizableScreen.getBackGColor());
        ( ( TextView) intervalView.findViewById( R.id.add_event_interval)).setTextColor( CustomizableScreen.getBackGColor());
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
        event_start.setTextColor( CustomizableScreen.getBackGColor());
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
                                event.setEventStart(eventStart);
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
        event_end.setTextColor( CustomizableScreen.getBackGColor());
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
                                event.setEventEnd(eventEnd);
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
        if (thisDay == null) {
            thisDay = Calendar.getInstance();
        }
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
        if (!event.getType().equals("Exam")) {
            repetitionView = layoutInflater.inflate(R.layout.add_event_repetition_item, null);

            repetition_type = (Spinner) repetitionView.findViewById(R.id.repetition_type);
            ArrayAdapter<String> repetitionTypesAdapter = new ArrayAdapter<>(AddEvent.this,
                    android.R.layout.simple_spinner_item,
                    getResources().getStringArray(R.array.repetition_types));
            repetition_type.setAdapter(repetitionTypesAdapter);
            repetition_type.setOnItemSelectedListener(this);
            repetition_type.setEnabled(false);
            number_of_repetitions = (EditText) repetitionView.findViewById(R.id.num_of_repetitions);
            number_of_repetitions.setEnabled(false);

            // Sets the color
            ( ( TextView)repetitionView.findViewById( R.id.add_event_repeat)).setTextColor( CustomizableScreen.getBackGColor());
            ( ( TextView)repetitionView.findViewById( R.id.add_repetition_times)).setTextColor( CustomizableScreen.getBackGColor());
            ( ( EditText)repetitionView.findViewById( R.id.num_of_repetitions)).setTextColor( CustomizableScreen.getBackGColor());

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

        // set colors
        ( ( TextView) commonItemsView.findViewById( R.id.add_event_notification)).setTextColor( CustomizableScreen.getBackGColor());
        notification_spinner.setOnItemSelectedListener(this);


        // initialize the checkbox for notifications
        notification = (CheckBox) commonItemsView.findViewById(R.id.notification_checkbox);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // disable spinner when  is not checked
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
        event.setColor(R.color.primary_text);

        // set all other colors
        ( ( TextView) commonItemsView.findViewById( R.id.add_notes)).setTextColor( CustomizableScreen.getBackGColor());
        commonItemsView.findViewById( R.id.add_notes).setBackgroundColor( CustomizableScreen.backgroundColor);
        commonItemsView.findViewById( R.id.constraint).setBackgroundColor( CustomizableScreen.getButtonColor());
        ( ( TextView) commonItemsView.findViewById( R.id.add_event_colour)).setTextColor( CustomizableScreen.getBackGColor());
        ( ( TextView) commonItemsView.findViewById( R.id.add_event_notes)).setTextColor( CustomizableScreen.getBackGColor());
        ( ( TextView) commonItemsView.findViewById( R.id.add_event_colour)).setTextColor( CustomizableScreen.getBackGColor());
        ( ( TextView) commonItemsView.findViewById( R.id.textView2)).setTextColor( CustomizableScreen.getBackGColor());

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
                        event.setColor(Color.rgb(0, 188, 212));
                    else if (b.getId() == R.id.colour_blue)
                        event.setColor(Color.rgb(25, 116, 189));
                    else if (b.getId() == R.id.colour_purple)
                        event.setColor(Color.rgb(156, 39, 176));
                    else if (b.getId() == R.id.colour_pink)
                        event.setColor(Color.rgb(233, 30, 99));
                    else if (b.getId() == R.id.colour_red)
                        event.setColor(Color.rgb(232, 16, 0));
                    else if (b.getId() == R.id.colour_orange)
                        event.setColor(Color.rgb(255, 87, 34));
                    else if (b.getId() == R.id.colour_yellow)
                        event.setColor(Color.rgb(255, 235, 59));
                    else if (b.getId() == R.id.colour_green)
                        event.setColor(Color.rgb(76, 175, 80));

                    Log.d(TAG, "onClick: event color" + event.getColor());

                    // create message to inform the user
                    if (event.getColor() != R.color.primary_text)
                        Toast.makeText(AddEvent.this, "Colour chosen",
                                Toast.LENGTH_SHORT).show();
                }
            });
        }

        notes = (EditText) commonItemsView.findViewById(R.id.add_notes);
        editParagraphFont(notes);


        // initialize Location editing UI elements
        MapFragment mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.map_container, mapFragment).commit();
        mapFragment.addEvent(event);

        locationSelect = (Button) commonItemsView.findViewById(R.id.open_map);
        // show the location on the map if the user has chosen one
        if(event.getLocation()!= null) {
            mapFragment.moveToLocation(event.getLocation());
        }

        if(GoogleMapsAvailability.isServicesOK(AddEvent.this)) {
            locationSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notes.getText() !=  null) {
                        event.setNotes(notes.getText().toString());
                    }
                    Intent intent = new Intent(AddEvent.this, MapActivity.class);
                    intent.putExtra(MapActivity.EVENT_KEY, event);
                    if (isEditView)
                        intent.putExtra(MapActivity.INTENT_ID_KEY, EDIT_ACTIVITY_NAME);
                    else
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
                Log.d(TAG, "onClick: eventToAdd: " + event.getEventStart() + " end: " + event.getEventEnd());

                // Check if event interval is given for events except assignment
                if (!event.getType().equals("Assignment") &&  (event.getEventEnd() == null || event.getEventEnd() == null)) {
                    Log.d(TAG, "onClick: error message 1");
                    Toast.makeText(AddEvent.this, "Please  choose event interval",
                            Toast.LENGTH_LONG).show();
                }
                // check if due time is given for assignment
                else if (event.getType().equals("Assignment") && event.getEventEnd() == null) {
                    Log.d(TAG, "onClick: error message 2");
                    Toast.makeText(AddEvent.this, "Please choose due time",
                            Toast.LENGTH_LONG).show();
                }
                // check event start and event end
                else if (event.getEventStart().compareTo(event.getEventEnd()) > 0) {
                    Log.d(TAG, "onClick: error message 3");
                    Toast.makeText(AddEvent.this, "Event start cannot be after event end",
                            Toast.LENGTH_LONG).show();
                }
                // finalize the event and add to the database
                else {
                    saveData();
                    loadData();

                    if (notes.getText() !=  null) {
                        event.setNotes(notes.getText().toString());
                    }

                    if ( notification.isChecked()) {
                        addNotificationToEvent();
                    }

                    DBHelper dbHelper = new DBHelper(AddEvent.this, DBHelper.DB_NAME, null);
                    Log.d(TAG, "onClick: Right before event inserted");
                    long insertResult = dbHelper.insertEvent(event);

                    if (!event.getType().equals("Assignment")) {
                        if (repeat.isChecked()) {
                            repeatEvent();
                        }
                    }

                    // report the user if the event is saved
                    if (insertResult == -1)
                        Toast.makeText(AddEvent.this, "Event couldn't be saved", Toast.LENGTH_SHORT).show();
                    else if (insertResult == -2)
                        Toast.makeText(AddEvent.this, "Event already exists", Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(AddEvent.this, "Event successfully added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddEvent.this, DayActivity.class);
                        intent.putExtra(DayActivity.INTENT_KEY, event.getEventStart());
                        startActivity(intent);
                    }
                }
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
            if (event != null) {
                event.setType(parent.getItemAtPosition(position).toString());
            }
        }
        else if (parent.getId() == R.id.repetition_type) {
            repetitionType = parent.getItemAtPosition(position).toString();
        }
        if (parent.getId() == R.id.notifications_spinner)
            notifType = parent.getItemAtPosition(position).toString();
        ((TextView) view).setTextColor( CustomizableScreen.getBackGColor());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Creates a notification channel through which notifications could be sent
     */
    public void createNotificationChannel() {

        // notification channels are a must for Android level higher than or equal to Oreo.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel", "channel1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("simple channel");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Adds notification to the event present as a field in the AddEvent class
     */
    public void addNotificationToEvent() {
        String notificationSpinner = notification_spinner.getSelectedItem().toString();
        Intent intent = new Intent(AddEvent.this, ReminderBroadCast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddEvent.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);



        long dueTimeInMs = event.getEventStart().getTimeInMillis() ;
        long differenceToDue;

        // arranges the time of difference between notification and time of event
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
        Log.d(TAG, "dueTimeInMs - differenceToDue " + event.getEventStart().toString() );

        alarmManager.set(AlarmManager.RTC_WAKEUP, dueTimeInMs - differenceToDue, pendingIntent);

        Calendar notificationDate = Calendar.getInstance();
        notificationDate.setTimeInMillis(dueTimeInMs - differenceToDue);
        event.setNotifTime( notificationDate(notificationDate) + " " + notificationHourMinute(notificationDate));
    }


    /**
     * Repeats the event present as a field in the AddEvent class
     */
    public void repeatEvent() {
        repetition_type.setEnabled(false);
        number_of_repetitions.setEnabled(false);


        if (number_of_repetitions != null && repetitionType != null) {
            int number = ConvertIntoNumeric(number_of_repetitions.getText().toString());
            Log.d(TAG, "onClick: repetition number " + number );

            if (repetitionType.equalsIgnoreCase("Monthly")) {
                Calendar newEventStart = event.getEventStart();
                Calendar newEventEnd = event.getEventEnd();
                DBHelper dbHelper = new DBHelper(AddEvent.this, DBHelper.DB_NAME,null);

                // adding the repeated events with the same properties
                for (int i = 0; i< number;i++) {
                    newEventStart.add(Calendar.MONTH, 1);
                    newEventEnd.add(Calendar.MONTH, 1);
                    saveData();
                    loadData();
                    CalendarEvent newEvent = new CalendarEvent(newEventStart, newEventEnd, event.getName(),
                            eventID, event.getType());
                    newEvent.setColor(event.getColor());
                    newEvent.setLocation(event.getLocation());
                    newEvent.setNotes(event.getNotes());
                    newEvent.setNotifTime(event.getNotifTime());
                    dbHelper.insertEvent(newEvent);
                }
            }

            else if (repetitionType.equalsIgnoreCase("Daily")) {
                Calendar newEventStart = event.getEventStart();
                Calendar newEventEnd = event.getEventEnd();
                DBHelper dbHelper = new DBHelper(AddEvent.this, DBHelper.DB_NAME,null);
                Log.d(TAG, "onClick: database helper intialized");

                // adding the repeated events with the same properties
                for (int i = 0; i< number;i++) {
                    newEventStart.add(Calendar.DAY_OF_MONTH, 1);
                    newEventEnd.add(Calendar.DAY_OF_MONTH, 1);
                    saveData();
                    loadData();
                    CalendarEvent newEvent = new CalendarEvent(newEventStart, newEventEnd, event.getName(),
                            eventID, event.getType());
                    newEvent.setColor(event.getColor());
                    newEvent.setLocation(event.getLocation());
                    newEvent.setNotes(event.getNotes());
                    newEvent.setNotifTime(event.getNotifTime());
                    dbHelper.insertEvent(newEvent);
                }
            }

            else if (repetitionType.equalsIgnoreCase("Annually")) {
                Calendar newEventStart = event.getEventStart();
                Calendar newEventEnd = event.getEventEnd();
                DBHelper dbHelper = new DBHelper(AddEvent.this, DBHelper.DB_NAME,null);

                // adding the repeated events with the same properties
                for (int i = 0; i< number;i++) {
                    newEventStart.add(Calendar.YEAR, 7);
                    newEventEnd.add(Calendar.YEAR, 7);
                    saveData();
                    loadData();
                    CalendarEvent newEvent = new CalendarEvent(newEventStart, newEventEnd, event.getName(),
                            eventID, event.getType());
                    newEvent.setColor(event.getColor());
                    newEvent.setLocation(event.getLocation());
                    newEvent.setNotes(event.getNotes());
                    newEvent.setNotifTime(event.getNotifTime());
                    dbHelper.insertEvent(newEvent);
                }
            }

            else if (repetitionType.equalsIgnoreCase("Weekly")) {
                Calendar newEventStart = event.getEventStart();
                Calendar newEventEnd = event.getEventEnd();
                DBHelper dbHelper = new DBHelper(AddEvent.this, DBHelper.DB_NAME,null);

                // adding the repeated events with the same properties
                for (int i = 0; i< number;i++) {
                    newEventStart.add(Calendar.DAY_OF_MONTH,7);
                    newEventEnd.add(Calendar.DAY_OF_MONTH,7);
                    saveData();
                    loadData();
                    CalendarEvent newEvent = new CalendarEvent(newEventStart, newEventEnd, event.getName(),
                            eventID, event.getType());
                    newEvent.setColor(event.getColor());
                    newEvent.setLocation(event.getLocation());
                    newEvent.setNotes(event.getNotes());
                    newEvent.setNotifTime(event.getNotifTime());
                    dbHelper.insertEvent( newEvent);
                }
            }
        }
    }

    /**
     * Sets options for the current event
     */
    public void setAddEventView() {
        Log.d(TAG, "setAddEventView: setting the view");
        event_type_spinner.setSelection(eventTypesAdapter.getPosition(event.getType()));
        event_name.setText(event.getName());
        next.performClick();
        if (event.getEventStart() != null) {
            Log.d(TAG, "setAddEventView: event start " + event.getEventStart());
            event_date.setText(formattedMonth(event.getEventStart().get(Calendar.MONTH))
                    + " " + event.getEventStart().get(Calendar.DAY_OF_MONTH)
                    + " " + event.getEventStart().get(Calendar.YEAR));
        }
        if (event.getType().equals("Assignment") && event.getEventStartTime() != null)
            event_due_time.setText(event.getEventStartTime());
        else if (event.getEventStartTime() != null && event.getEventEndTime() != null) {
            event_start.setText("Start: " + event.getEventStartTime());
            event_end.setText("End: " + event.getEventEndTime());
        }
        Log.d(TAG, "onClick: notes" + event.getNotes());
        if (event.getNotes() != null)
            Log.d(TAG, "setAddEventView: event notes " + event.getNotes());
            notes.setText(event.getNotes());
    }

    /**
     * Arranges add event view to be edit event view
     */
    public void rearrangeToEdit() {
        title.setText("Edit Event");
        linearLayout.removeView(repetitionView);
        notification.setEnabled(false);

        // reset the clicklistener for the save button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.setName(event_name.getText().toString());
                saveData();
                loadData();

                if (notes.getText() !=  null) {
                    event.setNotes(notes.getText().toString());
                }

                DBHelper dbHelper = new DBHelper(AddEvent.this, DBHelper.DB_NAME,null);
                long insertResult = dbHelper.insertEvent(event);

                // report the user if the event is saved
                if (insertResult == -1)
                    Toast.makeText(AddEvent.this, "Event couldn't be saved", Toast.LENGTH_SHORT).show();
                else if (insertResult == -2)
                    Toast.makeText(AddEvent.this, "Event already exists", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(AddEvent.this, "Changes made", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AddEvent.this, EventActivity.class);
                intent.putExtra(EventActivity.EVENT_VIEW_INTENT_KEY , event);
                startActivity(intent);
            }
        });
    }

    /**
     * Converts String to numeric by checking for the null pointer exception
     * @param xVal
     * @return
     */
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
    /**
     * Returns the notification hour and minute as a String
     * @return notification time
     * @param date
     */
    public String notificationHourMinute( Calendar date) {
        String result;
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);

        result = hour+"";
        // making sure that the result is in HH:MM format
        if(hour < 10)
            result = 0 + result;
        if (minute < 10) {
            result = result + ":0" + minute;
        } else {
            result = result + ":" + minute;
        }

        return result;
    }

    /**
     * Returns the notification day, month and year as a String
     * @return notification time
     * @param date
     */
    public String notificationDate(Calendar date) {
        String result;
        int year = date.get(Calendar.YEAR);
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);

        result = day+"";
        // making sure that the result is in HH:MM format
        if(day < 10)
            result = 0 + result;
        if (month < 10) {
            result = result + "/0" + month;
        }
        else {
            result = result + "/" + month;
        }
        result = result + "/" + year;

        return result;
    }
}
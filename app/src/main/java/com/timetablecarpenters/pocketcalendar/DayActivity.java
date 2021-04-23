package com.timetablecarpenters.pocketcalendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
    // first [] for hours of day, next [] for the event starting times and end times in order.
    private String[][] textsOfHours;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_day);
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starts");

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

    private void pullEventsOfDay() {

    }
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
    private void setTextViews( ) {

    }



}
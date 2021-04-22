package com.timetablecarpenters.pocketcalendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class DayActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "DayActivity";
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView add_event, event_type, event_name;
    private Spinner event_type_spinner;
    private EditText event_name_edit;
    private LinearLayout addEventPopupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_day);
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starts");

        FloatingActionButton fab = findViewById(R.id.add_event_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DayActivity.this, "YAZILIM ÖĞEREN", Toast.LENGTH_SHORT).show();
                openDialog();
            }
        });
    }

    public void openDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        addEventPopupView = (LinearLayout) getLayoutInflater().inflate(R.layout.add_event_popup, null);
        final View typeAndNameView = getLayoutInflater().inflate(R.layout.add_event_type_and_name, null);

        add_event = (TextView) typeAndNameView.findViewById(R.id.add_event);
        event_type = (TextView) typeAndNameView.findViewById(R.id.event_type);
        event_name = (TextView) typeAndNameView.findViewById(R.id.event_name);
        event_type_spinner = (Spinner) typeAndNameView.findViewById(R.id.event_type_spinner);
        ArrayAdapter<String> eventNamesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.event_types));
        event_name_edit = (EditText) typeAndNameView.findViewById(R.id.event_name_edit);
        event_type_spinner.setAdapter(eventNamesAdapter);

        addEventPopupView.addView(typeAndNameView);
        dialogBuilder.setView(addEventPopupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }




    
}
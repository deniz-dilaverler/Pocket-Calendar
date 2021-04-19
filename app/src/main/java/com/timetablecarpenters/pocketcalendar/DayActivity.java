package com.timetablecarpenters.pocketcalendar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class DayActivity extends BaseActivity {
    private static final String TAG = "DayActivity";
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

    public void openDialog() {}
}
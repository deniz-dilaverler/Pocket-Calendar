package com.timetablecarpenters.pocketcalendar;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.CheckBox;

import android.os.Bundle;
import android.widget.TextView;

public class NotifyActivity extends AppCompatActivity {
    CheckBox bNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.add_event_common_items);
        bNotification = findViewById(R.id.notification_checkbox);
        if( bNotification.isChecked()) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_notify);
            TextView textView = findViewById(R.id.text_view);
            String message = getIntent().getStringExtra("message");
            textView.setText(message);
        }
    }
}
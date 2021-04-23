package com.timetablecarpenters.pocketcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
/**
 * In the settings menu, the user will be able to adjust the in text font size,
 * paragraph font size, notification sound, the calendar reset button.
 * @version 22.04.21
 *
 */
public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting");
        setContentView(R.layout.activity_settings);
        super.onCreate(savedInstanceState);

    }
}
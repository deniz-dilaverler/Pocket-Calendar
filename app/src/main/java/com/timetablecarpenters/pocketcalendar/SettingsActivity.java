package com.timetablecarpenters.pocketcalendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;


/**
 * In the settings menu, the user will be able to adjust the in text font size,
 * paragraph font size, notification sound, the calendar reset button.
 * @version 22.04.21
 *
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private Toolbar toolbar;
    /**
     * Creates the settings menu and enables it
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting");
        setContentView(R.layout.activity_settings);
        super.onCreate(savedInstanceState);
        toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        if (findViewById(R.id.fragment_container) != null) {
            if ( savedInstanceState != null)
                return;
            getFragmentManager().beginTransaction().add(R.id.fragment_container, new SettingsFragment()).commit();
        }
       // SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    }
}
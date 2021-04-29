package com.timetablecarpenters.pocketcalendar;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.DatePicker;

import java.security.Key;
import java.util.Calendar;

public class SettingsFragment extends PreferenceFragment {
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    public static final String IN_TEXT_FONT_SIZE = "In_text_font_size";
    public static final String PARAGRAPH_FONT_SIZE = "Paragraph_font_size";
    public static final String NOTIFICATION_SOUND = "Notification_sound";


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.preferences);
        Preference pref = findPreference("myButton");
        if (pref != null) {
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    return true;
                }


                });
            }

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals(IN_TEXT_FONT_SIZE))
                {
                    Preference inText = findPreference(key);
                    inText.setSummary(sharedPreferences.getString(key,""));
                }
                if(key.equals(PARAGRAPH_FONT_SIZE))
                {
                    Preference paragraphText = findPreference(key);
                    paragraphText.setSummary(sharedPreferences.getString(key,""));
                }
                if(key.equals(NOTIFICATION_SOUND))
                {
                    Preference notifSound = findPreference(key);
                    notifSound.setSummary(sharedPreferences.getString(key,""));
                }




            }
            };
        }

    @Override
    public void onResume(){
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        Preference inText = findPreference(IN_TEXT_FONT_SIZE);
        Preference paragraphText = findPreference(PARAGRAPH_FONT_SIZE);
        Preference notifSound = findPreference(NOTIFICATION_SOUND);

        inText.setSummary(getPreferenceScreen().getSharedPreferences().getString(IN_TEXT_FONT_SIZE,""));
        paragraphText.setSummary(getPreferenceScreen().getSharedPreferences().getString(PARAGRAPH_FONT_SIZE,""));
        notifSound.setSummary(getPreferenceScreen().getSharedPreferences().getString(NOTIFICATION_SOUND,""));


    }
    @Override
    public void onPause(){
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }
    }


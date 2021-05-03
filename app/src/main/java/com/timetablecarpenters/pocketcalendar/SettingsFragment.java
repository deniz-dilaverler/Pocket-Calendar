package com.timetablecarpenters.pocketcalendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.DatePicker;
import android.widget.Toast;

import java.security.Key;
import java.util.Calendar;
/**
 * Listens the settings functions and implement their tasks
 * Creates SharedPreferences to save and use data
 * @author Yusuf Şenyüz
 * @version 28.04.2021
 */
public class SettingsFragment extends PreferenceFragment {
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    public static final String IN_TEXT_FONT_SIZE = "In_text_font_size";
    public static final String PARAGRAPH_FONT_SIZE = "Paragraph_font_size";

    public static final String RESET_BUTTON = "resetButton";
    public static final String MEDIUM = "Medium";
    public static final String LARGE = "Large";
    public DBHelper dbHelper;

    /**
     * listens to the inputs of the settings items
     * @param savedInstance
     */
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.preferences);
        Preference pref = findPreference("resetButton");
        if (pref != null) {
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getContext(),"Calendar is reset",Toast.LENGTH_LONG).show();
                    dbHelper = new DBHelper(getContext(),DBHelper.DB_NAME, null);
                    dbHelper.resetDb(null);
                    ListPreference inText = (ListPreference) findPreference(IN_TEXT_FONT_SIZE);
                    ListPreference paragraph = (ListPreference) findPreference(PARAGRAPH_FONT_SIZE);
                    inText.setValue(MEDIUM);
                    paragraph.setValue(LARGE);
                    
                    return true;
                }


                });
            }

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            /**
             * listens to the inputs of the settings items
             * @param sharedPreferences is the shared preference that will be used
             * @param key is a key of the item
             */
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals(IN_TEXT_FONT_SIZE))
                {
                    Preference inText = findPreference(key);
                    inText.setSummary(sharedPreferences.getString(key,""));
                    ListPreference inTextList = (ListPreference) inText;

                    SharedPreferences inTextPref;
                    inTextPref = getContext().getSharedPreferences("inTextPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = inTextPref.edit();
                    editor.putString("inTextFontSize", inTextList.getValue());
                    editor.commit();
                    //editor.apply();
                    Toast.makeText(getContext(),"Font size is selected",Toast.LENGTH_LONG).show();

                }
                if(key.equals(PARAGRAPH_FONT_SIZE))
                {
                    Preference paragraphText = findPreference(key);
                    paragraphText.setSummary(sharedPreferences.getString(key,""));
                    ListPreference paragraphList = (ListPreference) paragraphText;

                    SharedPreferences paragraphPref;
                    paragraphPref = getContext().getSharedPreferences("paragraphPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = paragraphPref.edit();
                    editor.putString("paragraphFontSize", paragraphList.getValue());
                    editor.commit();
                    //editor.apply();
                    Toast.makeText(getContext(),"Font size is selected",Toast.LENGTH_LONG).show();
                }
            }
            };
        }

    /**
     * 
     */
    @Override
    public void onResume(){
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        Preference inText = findPreference(IN_TEXT_FONT_SIZE);
        Preference paragraphText = findPreference(PARAGRAPH_FONT_SIZE);


        inText.setSummary(getPreferenceScreen().getSharedPreferences().getString(IN_TEXT_FONT_SIZE,""));
        paragraphText.setSummary(getPreferenceScreen().getSharedPreferences().getString(PARAGRAPH_FONT_SIZE,""));
    }
    @Override

    public void onPause(){
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    }


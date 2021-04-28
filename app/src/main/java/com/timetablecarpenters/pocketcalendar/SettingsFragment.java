package com.timetablecarpenters.pocketcalendar;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class SettingsFragment extends PreferenceFragment {
    private DatePickerDialog.OnDateSetListener datePicker;
    private DatePickerDialog dialog;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.preferences);
        Preference pref = findPreference("myButton");
        if (pref != null) {
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    dialog = new DatePickerDialog(getContext(),
                            android.R.style.Theme_DeviceDefault,
                            datePicker,
                            year, month, day);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                    dialog.show();
                    return true;
                }


            });
        }

            datePicker = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    // TODO make changes ew 34r

                }
            };
        }
    }


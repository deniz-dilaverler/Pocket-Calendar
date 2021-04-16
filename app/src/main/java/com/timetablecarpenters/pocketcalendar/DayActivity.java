package com.timetablecarpenters.pocketcalendar;

import android.os.Bundle;
import android.util.Log;


public class DayActivity extends BaseActivity {
    private static final String TAG = "DayActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_day);
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starts");

    }
}
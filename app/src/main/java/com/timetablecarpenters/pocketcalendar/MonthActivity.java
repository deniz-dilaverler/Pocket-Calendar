package com.timetablecarpenters.pocketcalendar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MonthActivity extends BaseActivity {
    private static final String TAG = "MonthActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_month);
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);

    }


}
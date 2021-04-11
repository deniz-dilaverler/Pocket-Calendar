package com.timetablecarpenters.pocketcalendar;

import android.os.Bundle;


public class MonthActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_month);
        super.onCreate(savedInstanceState);



        // DBHelper is not finished yet. Comment out if causes problems
        // DBHelper dbHelper = new DBHelper(MainActivity.this, null, null);


    }


}
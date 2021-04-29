package com.timetablecarpenters.pocketcalendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MonthActivity extends BaseActivity {
    private View[] weeks, dayInWeek;


    private static final String TAG = "MonthActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_month);
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);

        initiateWeeks();



        View week1 = findViewById( R.id.week1_row);
        View day1 = week1.findViewById( R.id.monday_box);
        day1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView text = day1.findViewById( R.id.text_date_name);
                text.setText( "Boom");
            }
        });


    }
    private void initiateWeeks() {
        weeks = new View[6];
        weeks[0] = findViewById( R.id.week1_row);
        weeks[1] = findViewById( R.id.week2_row);
        weeks[2] = findViewById( R.id.week3_row);
        weeks[3] = findViewById( R.id.week4_row);
        weeks[4] = findViewById( R.id.week5_row);
        weeks[5] = findViewById( R.id.week6_row);
        for ( int i = 0; i < 6; i++) {
            initiateDayInWeek( weeks[i]);
        }
    }
    private void initiateDayInWeek( View aWeek) {
        dayInWeek = new View[7];
        dayInWeek[0] = aWeek.findViewById( R.id.monday_box);
        dayInWeek[1] = aWeek.findViewById( R.id.tuesday_box);
        dayInWeek[2] = aWeek.findViewById( R.id.wednesday_box);
        dayInWeek[3] = aWeek.findViewById( R.id.thursday_box);
        dayInWeek[4] = aWeek.findViewById( R.id.friday_box);
        dayInWeek[5] = aWeek.findViewById( R.id.saturday_box);
        dayInWeek[6] = aWeek.findViewById( R.id.sunday_box);
        for ( int i = 0; i < 7; i++) {
            dayCreate( dayInWeek[i]);
        }
    }
    private void dayCreate( View day) {

    }


















}
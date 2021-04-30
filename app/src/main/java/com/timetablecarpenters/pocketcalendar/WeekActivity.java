package com.timetablecarpenters.pocketcalendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import androidx.core.content.res.ResourcesCompat;

import java.util.Calendar;

/**
 * @author Deniz Mert Dilaverler
 * @version 17.04.21
 */
public class WeekActivity extends BaseActivity {
    private final static String INTENT_KEY = "first_date";
    private static final String TAG = "WeekActivity";
    public int[] rowIds = {R.id.monday_row, R.id.tuesday_row, R.id.wednesday_row, R.id.thursday_row, R.id.friday_row, R.id.saturday_row, R.id.sunday_row};
    public Calendar first;
    TextView dateText;

    /**
     * Set's up the weekActivity view and its functionalities
     * calls methods to calculate todays date, change the date, fill the grid elements
     * @param savedInstanceState
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starts" );
        setContentView(R.layout.activity_week);
        super.onCreate(savedInstanceState);
        Calendar day;
        Calendar last;
        Calendar today;
        Cursor cursor;
        String dateString;
        Bundle extras;

        extras = getIntent().getExtras();
        if (extras != null)
            first = (Calendar) extras.get(INTENT_KEY);
        View content = findViewById(R.id.week_content);
        if (first == null) {
            Log.d(TAG, "onCreate: SA" );
            // set the date
            today = Calendar.getInstance();
            today.setFirstDayOfWeek(Calendar.MONDAY);
            // "calculate" the start date of the week
            first = (Calendar) today.clone();
            first.add(Calendar.DAY_OF_WEEK,
                    first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        }
        // and add six days to the end date
        last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, 6);

        dateText = (TextView) findViewById(R.id.dateText);
        dateString = monthNames[first.get(Calendar.MONTH)] + " " + first.get(Calendar.DATE) + "  -  " +
                monthNames[last.get(Calendar.MONTH)] + " " + last.get(Calendar.DATE);
        dateText.setText(dateString);

        day = (Calendar) first.clone();
        DBHelper dbHelper = new DBHelper(this, DBHelper.DB_NAME, null);
        for(int i = 0 ; i < 7 ; i++) {
            View row = content.findViewById(rowIds[i]);
            TextView weekDayName = (row.findViewById(R.id.text_date_name));
            weekDayName.setText(dateNames[i]);
            weekDayName.setTextColor( getBackGColor());
            Log.d(TAG, "onCreate: day = " + day.get(Calendar.YEAR)+ " " + day.get(Calendar.MONTH)+ " " + day.get(Calendar.DAY_OF_MONTH));
            cursor = dbHelper.getEventsInAnInterval(day, day);
            // check if there are any events on that day
            if (cursor.getColumnCount() > 0) {
                ListView list = row.findViewById(R.id.events_of_day_list);
                list.setAdapter(new WeekViewAdapter(this, cursor));
                list.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return onTouchEvent(event);
                    }

                });
            }
            day.add(Calendar.DATE, 1);
        }
    }
    private int getBackGColor() {
        return ResourcesCompat.getColor(getResources(), R.color.month_activity_beckground_unusable, null);
    }
    /**
     * when a leftSwipe is notifed by the super class adds a week to the date of weekView and refreshes the activity
     */
    @Override
    public void leftSwipe() {
        super.leftSwipe();
        first.add(Calendar.DATE, 7);
        finish();
        Intent intent = new Intent(this, WeekActivity.class);
        intent.putExtra(INTENT_KEY, first);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * when a rightSwipe is notifed by the super class subtracts a week from the date of weekView and refreshes the activity
     */
    @Override
    public void rightSwipe() {
        super.rightSwipe();
        first.add(Calendar.DATE, -7);
        finish();
        Intent intent = new Intent(this, WeekActivity.class);
        intent.putExtra(INTENT_KEY, first);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


    }
}
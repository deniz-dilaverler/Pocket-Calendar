package com.timetablecarpenters.pocketcalendar;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.RowId;
import java.util.Calendar;

/**
 * @author Deniz Mert Dilaverler
 * @version 17.04.21
 */
public class WeekActivity extends BaseActivity {
    private static final String TAG = "WeekActivity";
    public int[] rowIds = {R.id.monday_row, R.id.tuesday_row, R.id.wednesday_row, R.id.thursday_row, R.id.friday_row, R.id.saturday_row, R.id.sunday_row};
    public Calendar first;
    private View content;
    TextView dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_week);
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starts" );
        Calendar day;
        Calendar last;
        Calendar today;
        Cursor cursor;
        String dateString;

        content = findViewById(R.id.week_content);
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

        dateText = findViewById(R.id.dateText);
        dateString = monthNames[first.get(Calendar.MONTH)] + " " + first.get(Calendar.DATE) + "  -  " +
                monthNames[last.get(Calendar.MONTH)] + " " + last.get(Calendar.DATE);
        dateText.setText(dateString);

        day = (Calendar) first.clone();
        DBHelper dbHelper = new DBHelper(this, "calendar.db", null);
        for(int i = 0 ; i < 7 ; i++) {
            View row = content.findViewById(rowIds[i]);
            ((TextView) row.findViewById(R.id.text_date_name)).setText(dateNames[i]);
            Log.d(TAG, "onCreate: day = " + day.get(Calendar.YEAR)+ " "+ day.get(Calendar.MONTH)+ " " + day.get(Calendar.DAY_OF_MONTH));
            cursor = dbHelper.getEventsInAnInterval(day.get(Calendar.YEAR), day.get(Calendar.MONTH),
                    day.get(Calendar.DAY_OF_MONTH), day.get(Calendar.DAY_OF_MONTH));
            // check if there are any events on that day
            if (cursor.getColumnCount() > 0) {

                ((ListView) row.findViewById(R.id.events_of_day_list)).setAdapter(new weekViewAdapter(this, cursor));
            }
            day.add(Calendar.DATE, 1);
        }
    }

    

    @Override
    public void leftSwipe() {
        super.leftSwipe();
        first.add(Calendar.DATE, -7);
        Bundle tempBundle = new Bundle();
        onCreate(tempBundle);
    }

    @Override
    public void rightSwipe() {
        super.rightSwipe();
        first.add(Calendar.DATE, 7);
        Bundle tempBundle = new Bundle();
        onCreate(tempBundle);
    }
}
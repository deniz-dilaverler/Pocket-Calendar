package com.timetablecarpenters.pocketcalendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * BaseActivity supplies the common functionalities like the menu and the toolbar for the other
 * sub-class activities.
 *
 */
public class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button monthlyButton = findViewById(R.id.monthly_button);
        Button weeklyButton = findViewById(R.id.weekly_button);
        Button dailyButton = findViewById(R.id.daily_button);
        monthlyButton.setOnClickListener(new BaseActivity.ViewChangeClickListener());
        weeklyButton.setOnClickListener(new BaseActivity.ViewChangeClickListener());
        dailyButton.setOnClickListener(new BaseActivity.ViewChangeClickListener());

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public class ViewChangeClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.monthly_button:
                    intent = new Intent(BaseActivity.this, MonthActivity.class);
                    break;
                case R.id.weekly_button:
                    intent = new Intent(BaseActivity.this, WeekActivity.class);
                    break;
                case R.id.daily_button:
                    intent = new Intent(BaseActivity.this, DayActivity.class);
                    break;
                default:
                    intent = null;
            }
            if (intent != null)
                BaseActivity.this.startActivity(intent);

        }
    }




}

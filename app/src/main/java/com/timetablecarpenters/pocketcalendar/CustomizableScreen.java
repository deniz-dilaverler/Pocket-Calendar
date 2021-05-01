package com.timetablecarpenters.pocketcalendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * User can change background calors via customizable screen
 * @author Yusuf Şenyüz
 * @version 30.04.21
 *
 */

public class CustomizableScreen extends AppCompatActivity {
    Button buttonWhite;
    Button buttonBlack;
    Button buttonPurple;
    Button buttonGreen;
    Button buttonRed;
    Button buttonPink;
    Button buttonBlue;
    Button buttonYellow;
    CoordinatorLayout monthLayout;
    CoordinatorLayout weekLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customizable_screen);
        buttonRed = findViewById(R.id.buttonRed);

        monthLayout=  (CoordinatorLayout) findViewById(R.id.activity_month_id);
        weekLayout = (CoordinatorLayout) findViewById(R.id.activity_week_id);


        buttonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthLayout.setBackgroundColor(Color.RED);
                weekLayout.setBackgroundColor(Color.RED);
            }
        });
    }
}
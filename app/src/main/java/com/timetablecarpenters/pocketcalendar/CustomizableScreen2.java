package com.timetablecarpenters.pocketcalendar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

/**
 * User can change background calors via customizable screen
 * @author Alpren Utku Yalçın
 * @version 30.04.21
 *
 */

public class CustomizableScreen2 extends AppCompatActivity {
    private static final String TAG = "CustomiseSettings";
    public static int backgroundColor;
    public static int buttonBackgroundColor;
    public static int buttonBackgroundColorUn;
    public static int textColor;
    private static boolean applied;
    private static int backgroundColorUnapplied, textColorUnapplied;
    public final int[] ids = {R.id.default1, R.id.default2, R.id.default3, R.id.default4,
            R.id.default5, R.id.default6, R.id.default7};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.customise);

        initiate();
        for ( int i : ids) {
            createClickListener( findViewById( i));
        }
        findViewById( R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply();
            }
        });
    }
    public static void initiate() {
        backgroundColorUnapplied = 0;
        textColorUnapplied = 0;
        buttonBackgroundColorUn = 0;

        if ( backgroundColor == 0 && textColor == 0) {
            backgroundColorUnapplied = Color.WHITE;
            textColorUnapplied = Color.BLACK;
            buttonBackgroundColorUn = Color.LTGRAY;
        }
        apply();
    }
    private void createClickListener( TextView colorText) {
        colorText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundAndTextColor( colorText);
            }
        });
    }

    public static void setBackgroundAndTextColor( TextView input) {
        ColorDrawable cd = (ColorDrawable) input.getBackground();
        backgroundColorUnapplied = cd.getColor();
        textColorUnapplied = input.getCurrentTextColor();
        if ( Color.red( backgroundColorUnapplied) > 125 && Color.green( backgroundColorUnapplied) > 125 && Color.blue( backgroundColorUnapplied) > 125) {
            buttonBackgroundColorUn = manipulateColor( backgroundColorUnapplied, (float) 0.7);
        }
        else if ( Color.red( backgroundColorUnapplied) < 125 && Color.green( backgroundColorUnapplied) < 125 && Color.blue( backgroundColorUnapplied) < 125) {
            if ( Color.red( backgroundColorUnapplied) < 1 || Color.green( backgroundColorUnapplied) < 1 || Color.blue( backgroundColorUnapplied) < 1)
                buttonBackgroundColorUn = Color.DKGRAY;
            else
                buttonBackgroundColorUn = manipulateColor( backgroundColorUnapplied, (float) 1.2);
        }
        else {
            buttonBackgroundColorUn = manipulateColor( backgroundColorUnapplied, (float) 0.8);
        }
    }
    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round( Color.red(color) * factor);
        int g = Math.round( Color.green(color) * factor);
        int b = Math.round( Color.blue(color) * factor);
        return Color.argb(a,
                Math.min( r,255),
                Math.min( g,255),
                Math.min( b,255));
    }
    public static void apply() {
        backgroundColor = backgroundColorUnapplied;
        textColor = textColorUnapplied;
        buttonBackgroundColor = buttonBackgroundColorUn;
    }
}
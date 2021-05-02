package com.timetablecarpenters.pocketcalendar;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

/**
 * User can change background calors via customizable screen
 * @author Yusuf Şenyüz
 * @version 30.04.21
 *
 */

public class CustomizableScreen2 extends AppCompatActivity {
    private static final String TAG = "CustomiseSettings";
    public static int backgroundColor;
    public static int textColor;
    public final int[] ids = {R.id.default1, R.id.default2, R.id.default3, R.id.default4,
            R.id.default5, R.id.default6, R.id.default7};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.customise);
            //if () {
            setBackgroundAndTextColor( findViewById( ids[0]));

        //}

        for ( int i : ids) {
            createClickListener( findViewById( i));
        }
    }

    private void createClickListener( TextView colorText) {
        colorText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundAndTextColor( colorText);
                Log.d(TAG, "going to: MonthActivity");

            }
        });
    }

    public static void setBackgroundAndTextColor( TextView input) {
        if ( input.getBackground() instanceof ColorDrawable) {
            ColorDrawable cd = (ColorDrawable) input.getBackground();
            backgroundColor = cd.getColor();
        }
        else {
            System.out.println( "AAAAAAAAAAAAAAAA------------AAAAAAAAAAA");
        }
        textColor = input.getCurrentTextColor();
    }
}
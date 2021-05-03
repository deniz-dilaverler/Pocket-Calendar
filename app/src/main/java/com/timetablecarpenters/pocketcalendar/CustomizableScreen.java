package com.timetablecarpenters.pocketcalendar;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * User can change custom calors via customizable screen
 * @author Alpren Utku Yalçın
 * @version 30.04.21
 *
 */

public class CustomizableScreen extends AppCompatActivity {
    private static final String TAG = "CustomiseSettings";
    public static int backgroundColor;
    public static int buttonBackgroundColor;
    public static int buttonBackgroundColorUn;
    public static int textColor;
    private static boolean applied;
    private static int backgroundColorUnapplied, textColorUnapplied;
    public final int[] ids = {R.id.default1, R.id.default2, R.id.default3, R.id.default4,
            R.id.default5, R.id.default6, R.id.default14, R.id.default8, R.id.default9, R.id.default10,
            R.id.default11, R.id.default12, R.id.default13, R.id.default7};

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
                Toast.makeText(CustomizableScreen.this, "Color has been applied",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void initiate() {

        if ( backgroundColor == 0 && textColor == 0) {
            backgroundColorUnapplied = Color.WHITE;
            textColorUnapplied = Color.BLACK;
            buttonBackgroundColorUn = Color.LTGRAY;
        }
        apply();
    }

    /**
     * Sets the text colors to a specific value (For testing purposes only)
     * @author Alperen
     * @return int color value
     */
    public static int getBackGColor() {
        if (CustomizableScreen.textColor != 0) {
            return CustomizableScreen.textColor;
        }
        else {
            CustomizableScreen.initiate();
            return CustomizableScreen.textColor;
        }
    }
    /**
     * Sets the text colors to a specific value (For testing purposes only)
     * @author Alperen
     * @return int color value
     */
    public static int getButtonColor() {
        if ( buttonBackgroundColor != 0) {
            return buttonBackgroundColor;
        }
        else {
            initiate();
            return buttonBackgroundColor;
        }
    }
    private void createClickListener( TextView colorText) {
        colorText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundAndTextColor( colorText);
                Toast.makeText(CustomizableScreen.this, "Color is selected",
                        Toast.LENGTH_SHORT).show();
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
            buttonBackgroundColorUn = manipulateColor( backgroundColorUnapplied, textColorUnapplied, (float) 0.8); // %70 first one then.
        }
    }
    public static int manipulateColor(int color1, int color2, float factor) {
        int a = Color.alpha( ( color1 + color2) /2 );
        int r = Math.round( Color.red( color1) * factor + ( 1 - factor) * Color.red( color2));
        int g = Math.round( Color.green( color1) * factor + ( 1 - factor) * Color.green( color2));
        int b = Math.round( Color.blue( color1) * factor + ( 1 - factor) * Color.blue( color2));
        return Color.argb(a,
                Math.min( r,255),
                Math.min( g,255),
                Math.min( b,255));
    }
    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }
    public static void apply() {
        backgroundColor = backgroundColorUnapplied;
        textColor = textColorUnapplied;
        buttonBackgroundColor = buttonBackgroundColorUn;
    }
}
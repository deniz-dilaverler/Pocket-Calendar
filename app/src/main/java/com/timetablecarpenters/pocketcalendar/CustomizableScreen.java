package com.timetablecarpenters.pocketcalendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
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
    public static SharedPreferences recordedValues;
    public static SharedPreferences.Editor editValues;
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

        recordedValues = PreferenceManager.getDefaultSharedPreferences( this);
        editValues = recordedValues.edit();




        checkSharedPreferences();
        apply();
        for ( int i : ids) {
            createClickListener( findViewById( i));
        }
        findViewById( R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (createRGBReceiver( findViewById( R.id.rgb1)) != 1000000) {
                    buttonBackgroundColorUn = createRGBReceiver( findViewById( R.id.rgb1));
                }
                if (createRGBReceiver( findViewById( R.id.rgb2)) != 1000000) {
                    textColorUnapplied = createRGBReceiver( findViewById( R.id.rgb2));
                }
                if (createRGBReceiver( findViewById( R.id.rgb3)) != 1000000) {
                    backgroundColorUnapplied = createRGBReceiver( findViewById( R.id.rgb3));
                }


                apply();
                Toast.makeText(CustomizableScreen.this, "Color has been applied",
                        Toast.LENGTH_SHORT).show();



                editValues.putInt( "Background_value", backgroundColor);
                editValues.putInt( "Text_color_value", textColor);
                editValues.putInt( "Button_color_value", buttonBackgroundColor);
                editValues.apply();
                Intent intent = new Intent(CustomizableScreen.this, MonthActivity.class);
                startActivity(intent);
            }
        });
    }
    public static void checkSharedPreferences() {
        if ( recordedValues != null) {
            backgroundColorUnapplied = recordedValues.getInt( "Background_value", Color.WHITE);
            textColorUnapplied = recordedValues.getInt( "Text_color_value", Color.BLACK);
            buttonBackgroundColorUn = recordedValues.getInt( "Button_color_value", Color.LTGRAY);
        }
        else {
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
        checkSharedPreferences();
        return textColor;
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
            checkSharedPreferences();
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
    private int createRGBReceiver( View rGB) {
        int t1,t2,t3;
        if (! (((EditText) rGB.findViewById( R.id.editTextNumber)).getText().toString()).equals( "")) {
            t1 = Integer.parseInt(((EditText) rGB.findViewById(R.id.editTextNumber)).getText().toString());
        } else { t1 = 0;}
        if (! (((EditText) rGB.findViewById( R.id.editTextNumber1)).getText().toString()).equals( "")) {
            t2 = Integer.parseInt(((EditText) rGB.findViewById(R.id.editTextNumber1)).getText().toString());
        } else { t2 = 0;}
        if (! (((EditText) rGB.findViewById( R.id.editTextNumber2)).getText().toString()).equals( "")) {
            t3 = Integer.parseInt(((EditText) rGB.findViewById(R.id.editTextNumber2)).getText().toString());
        } else { t3 = 0;}
        if ( t1 == 0 && t2 == 0 && t3 == 0) {
            return 1000000;
        }
        return android.graphics.Color.rgb( t1, t2, t3);
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
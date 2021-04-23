package com.timetablecarpenters.pocketcalendar;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;

import 	java.util.Calendar;
/**
 * @author Yarkın badboy_karizmatik!
 * @version 21.04.2021
 */

public class Birthday extends CalendarEvent{
    boolean giftBought;

    public Birthday (int year, int month, int day, String id, String type, String name, String color, String notifTime,
                     StringBuffer notes, boolean giftBought, String latitude, String longitude )
    {
        super (year, month, day, 0, 0, id,  type, name,
                23, 59 , color, notifTime, notes, latitude, longitude, "annually", 100);
        this.giftBought = giftBought;
    }

    public boolean isGiftBought() {
        return giftBought;
    }

    public void setGiftBought(boolean giftBought) {
        this.giftBought = giftBought;
    }

}

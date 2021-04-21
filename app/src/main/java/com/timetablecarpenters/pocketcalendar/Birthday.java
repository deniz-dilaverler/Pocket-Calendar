package com.timetablecarpenters.pocketcalendar;

import android.location.Location;
import 	java.util.Calendar;


/**
 * @author Yarkın Sakıncı
 * @version 21.04.2021
 */

public class Birthday extends CalendarEvent{
    boolean giftBought;

    public Birthday (int day, int month, int year, String id, String type, String name, int endingHour, int endingMinute,
                     String color, String notifTime, StringBuffer notes, boolean giftBought, String latitude, String longitude )
    {
        super (day, month, year, 0, 0, id,  type, name,
                23, 59 , color, notifTime, notes, latitude, longitude);
        this.giftBought = giftBought;
    }

    public boolean isGiftBought() {
        return giftBought;
    }

    public void setGiftBought(boolean giftBought) {
        this.giftBought = giftBought;
    }

}

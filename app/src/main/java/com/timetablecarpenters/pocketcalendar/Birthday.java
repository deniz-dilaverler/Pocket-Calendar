package com.timetablecarpenters.pocketcalendar;

public class Birthday extends CalendarEvent{
    boolean giftBought;

    public Birthday (int day, int month, int year, String id, String type, String name,
                     int endingHour, int endingMinute, String color, String notifTime, StringBuffer notes, boolean giftBought)
    {
        super (day, month, year, 0, 0, id,  type, name,
                23, 59 , color, notifTime, notes);
        this.giftBought = giftBought;
    }

    public boolean isGiftBought() {
        return giftBought;
    }

    public void setGiftBought(boolean giftBought) {
        this.giftBought = giftBought;
    }


}

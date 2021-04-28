package com.timetablecarpenters.pocketcalendar;

import java.util.Calendar;

/**
 * Birthday class that extends CalendarEvent, therefore using its methods and properties also with its own giftBought property
 * @author Yarkın Sakıncı
 * @version 23.04.2021
 */
public class Birthday extends CalendarEvent{
    boolean giftBought;

    /**
     * Inıitialises the birthday object by calling the super's constructor and initialising giftBought
     * @param eventStart
     * @param eventEnd
     * @param name
     * @param id
     * @param type
     */
    public Birthday (Calendar eventStart, Calendar eventEnd, String name, long id, String type,boolean giftBought)
    {

        super (eventStart,eventEnd, name, id, type);

        this.giftBought = giftBought;
    }


    /**
     * returns if the gift is bought
     * @return giftBought
     */
    public boolean isGiftBought() {
        return giftBought;
    }

    /**
     * sets the giftBought property of the Birthday object
     * @param giftBought
     */
    public void setGiftBought(boolean giftBought) {
        this.giftBought = giftBought;
    }
}

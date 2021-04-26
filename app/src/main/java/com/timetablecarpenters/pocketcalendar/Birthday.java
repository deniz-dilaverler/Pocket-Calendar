package com.timetablecarpenters.pocketcalendar;

/**
 * Birthday class that extends CalendarEvent, therefore using its methods and properties also with its own giftBought property
 * @author Yarkın Sakıncı
 * @version 23.04.2021
 */
//TODO: class must be adjusted to the newly changed Calendar event
public class Birthday extends CalendarEvent{
    boolean giftBought;

    /**
     * Inıitialises the birthday object by calling the super's constructor, repeating annualy and initialising giftBought
     * @param year
     * @param month
     * @param day
     * @param id
     * @param type
     * @param name
     * @param color
     * @param notifTime
     * @param notes
     * @param giftBought
     * @param latitude
     * @param longitude
     */

    public Birthday (int year, int month, int day, String id, String type, String name, String color, String notifTime,
                     StringBuffer notes, boolean giftBought, String latitude, String longitude )
    {

        super (null, null, null, 1, null);
       // repeateAnnually(100);

      //  this.giftBought = giftBought;
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

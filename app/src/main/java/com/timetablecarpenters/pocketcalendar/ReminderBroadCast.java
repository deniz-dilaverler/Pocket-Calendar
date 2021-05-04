package com.timetablecarpenters.pocketcalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * ReminderBroadCast class that extends BroadCastReceiver, therefore makes the emulator
 * receive the designed message
 * @author Yarkın Sakıncı
 * @version 03.05.2021
 */
public class ReminderBroadCast extends BroadcastReceiver {

    /**
     * Builds the notification by the help of NotificationCompat and NotificationCompatManager
     * by referencing the channelid through whcih receive occurs.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel")
                .setSmallIcon(R.drawable.ic_baseline_message_24)
                .setContentTitle("You have an upcoming event!!")
                .setContentText("Click on the message to view more")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager =  NotificationManagerCompat.from( context);
        notificationManager.notify(200,builder.build());
    }
}

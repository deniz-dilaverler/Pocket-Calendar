package com.timetablecarpenters.pocketcalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadCast extends BroadcastReceiver {
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

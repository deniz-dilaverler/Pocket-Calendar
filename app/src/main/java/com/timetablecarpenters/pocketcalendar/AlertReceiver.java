package com.timetablecarpenters.pocketcalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationActivity notificationActivity = new NotificationActivity(context);
        NotificationCompat.Builder nb = notificationActivity.getChannelNotification();
        notificationActivity.getManager().notify(1, nb.build());
    }
}
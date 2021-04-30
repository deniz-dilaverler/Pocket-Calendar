package com.timetablecarpenters.pocketcalendar;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationChannelCreator extends Application {
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }
    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID, "Channel1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Sinyal veriyom alyon mu");
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID, "Channel2", NotificationManager.IMPORTANCE_LOW);
            channel2.setDescription("komsuuu");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }
}

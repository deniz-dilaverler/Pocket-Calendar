package com.timetablecarpenters.pocketcalendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

import static com.timetablecarpenters.pocketcalendar.NotificationChannelCreator.CHANNEL_1_ID;

public class NotificationActivity extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_common_items);
        notificationManager = NotificationManagerCompat.from(this);


    }
    public void sendOnChannel( View v){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_baseline_message_24)
                .setContentTitle("Recep ivedik")
                .setContentText("Sinyal veriyom alyon mu?")
                .setPriority( NotificationCompat.PRIORITY_HIGH)
                .build();
        notificationManager.notify( 1, notification);
    }

}

package com.timetablecarpenters.pocketcalendar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AddEventNotification extends AppCompatActivity {
    Button addButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_common_items);
        addButton = findViewById(R.id.add_event_done);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Add event notification",
                    "Notification Channel", NotificationManager.IMPORTANCE_HIGH );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        AddEventNotification.this,"Add event notification");
                builder.setContentTitle( "Pocket Calendar");
                builder.setContentText("You have added an event");
                builder.setSmallIcon(R.drawable.ic_baseline_message_24);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(AddEventNotification.this);
                managerCompat.notify(1,builder.build());

            }
        });
    }
}

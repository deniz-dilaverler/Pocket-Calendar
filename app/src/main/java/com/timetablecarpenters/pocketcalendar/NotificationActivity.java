package com.timetablecarpenters.pocketcalendar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class NotificationActivity extends AppCompatActivity {

    CheckBox bNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_common_items);
        bNotification = (CheckBox) findViewById(R.id.notification_checkbox);

        bNotification.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                String message = "Sinyal veriyom alıyon mu?";
                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        NotificationActivity.this
                )
                        .setSmallIcon(R.drawable.ic_baseline_message)
                        .setContentTitle("Recep Ivedik kisisinden bir yeni mesaj")
                        .setContentText(message)
                        .setAutoCancel(true);
                Intent intent = new Intent(NotificationActivity.this,
                        NotifyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("message", message);
                PendingIntent pendingIntent = PendingIntent.getActivity(NotificationActivity.this,
                        0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = ( NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0,builder.build());
            }
        });
    }
}

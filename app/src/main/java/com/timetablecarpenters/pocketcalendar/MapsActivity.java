package com.timetablecarpenters.pocketcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MapsActivity extends AppCompatActivity {
    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MapFragment mapFragment = (MapFragment) (getSupportFragmentManager().findFragmentById(R.id.map_fragment));

        if (LocationAvailability.isServicesOK(this)) {
            Button btnMap = (Button) findViewById(R.id.btnMap);

            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MapsActivity.this, MapActivity.class);
                    startActivity(intent);
                }
            });
        }
    }



}
package com.timetablecarpenters.pocketcalendar;

import android.app.Dialog;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LocationAvailability {
    private static final String TAG = "LocationAvailability";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    public static boolean isServicesOK(AppCompatActivity activity) {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);

        if (available == ConnectionResult.SUCCESS) {
            // everything is fine can make map request
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error ocured but we can resolve it
            Log.d(TAG, "isServicesOK: an error ocured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Log.d(TAG, "isServicesOK: User can't make map request");
        }
        return false;
    }
}

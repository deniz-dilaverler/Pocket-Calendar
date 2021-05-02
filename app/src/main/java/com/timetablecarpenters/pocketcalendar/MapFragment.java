package com.timetablecarpenters.pocketcalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Google Maps fragment that shows the passed location
 * doesn't allow user interaction, solely for location viewing.
 * @version 1.05.2021
 * @author Deniz Mert Dilaverler
 */
public class MapFragment extends Fragment {
    private static final String TAG = "MapForView";
    
    private static final float ZOOM = 13f;
    protected boolean mapReady = false;
    protected GoogleMap map;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
  
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         *
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(false);
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            
            map = googleMap;

            mapReady = true;
        }
    };

    /**
     * Creates the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_for_view, container, false);
    }

    /**
     * Called immediately after onCreateView. Initializes the fragment properties
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment_encapsulater);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    /**
     * checks if the map is ready or not, if ready
     * moves the camera of Map to the passed location
     * @param location
     * @return whether the operation was successful
     */
    public boolean moveToLocation(LatLng location) {
        if (mapReady) {
            if (location == null) {
                Log.d(TAG, "moveToLocation: location is null");
                return false;
            }
                    
            LatLng postion = new LatLng(location.latitude, location.longitude);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(postion, ZOOM));
            MarkerOptions options = new MarkerOptions().position(postion);
            map.addMarker(options);
        } else {
            Log.d(TAG, "moveToLocation: map isn't ready ");
        }
        return mapReady;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapFragment f = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (f != null)
            getChildFragmentManager().beginTransaction().remove(f).commit();
    }


}
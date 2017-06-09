package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.R.attr.colorPrimary;
import static com.google.firebase.codelab.friendlychat.R.attr.colorAccent;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMyLocationButtonClickListener {
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;

    private TrackGPS gps;
    double longitude;
    double latitude;
    int zoomLevel = 16;
    public static boolean check;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        gps = new TrackGPS(MapsActivity.this);
        if(gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();

                       //Show longitude and latitude for user
            //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        }

    }


    final public boolean checkMyLocationInChat(double longitude, double latitude){

        if(latitude <= 47.202207+0.0005 && latitude >= 47.202207-0.0005 &&
                longitude <= 38.935407+0.0005 && longitude >= 38.935407-0.0005){
            return  true;
        } else {
            return false;
        }


    }

        /**
         * Enables the My Location layer if the fine location permission has been granted.
         */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setMinZoomPreference(18);


        // /.performClick();

        enableMyLocation();


        // Find user and optionally add point
        LatLng userPos = new LatLng(latitude, longitude);
        if (latitude != 0 && longitude != 0) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPos, zoomLevel));
                //mMap.addMarker(new MarkerOptions().position(userPos));
            }


/*        // Add a marker on SFEDU commun
        LatLng sfcommun = new LatLng(47.207083, 38.939926);
        mMap.addMarker(new MarkerOptions().position(sfcommun).title("Marker in SFEDU Community num 6"));
        mMap.addCircle(new CircleOptions()
                .center(sfcommun)
                .radius(50)
                .strokeColor(CYAN)
                .fillColor(0));
*/

        // Add marker on SFEDU corps D
        LatLng sfeduD = new LatLng(47.202207, 38.935407);
        mMap.addMarker(new MarkerOptions().position(sfeduD).title("Marker on Sfedu commun D"));
        mMap.addCircle(new CircleOptions()
                .center(sfeduD)
                .radius(50)
                .strokeColor(colorAccent)
                .fillColor(0));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(checkMyLocationInChat(gps.getLongitude(), gps.getLatitude())) {
                    startActivity(new Intent(MapsActivity.this, MainActivity.class));
                }
                return false;
            }
        });

    }


}

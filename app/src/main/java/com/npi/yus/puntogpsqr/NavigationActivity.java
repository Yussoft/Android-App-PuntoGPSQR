package com.npi.yus.puntogpsqr;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/*
    *Copyright (C) 2016  Jesús Sánchez de Castro
    *This program is free software: you can redistribute it and/or modify
    *it under the terms of the GNU General Public License as published by
    *the Free Software Foundation, either version 3 of the License, or
    *(at your option) any later version.
    *This program is distributed in the hope that it will be useful,
    *but WITHOUT ANY WARRANTY; without even the implied warranty of
    *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    *GNU General Public License for more details.
    *You should have received a copy of the GNU General Public License
    *along with this program.  If not, see <http://www.gnu.org/licenses/>
    *
    * @author Jesús Sánchez de Castro
    * @version 19.09.2016
    *
    Last Modification: 19/09/2016
    https://justyusblog.wordpress.com/
    https://github.com/Yussoft

    Bibliography:
    - https://developer.android.com/training/location/retrieve-current.html#play-services

    App idea: This app will read a QR code that gives geographical coordinates(Latitude,Altitude)
    them It will start a google maps activity and start a navigation from out current location
    to the given coordinates (QR).
    */
/*
    NavigationActivity class:

    Implements all the methods needed to used launch a google maps activity with the geographical
    coordinates of the QR code read in the MainActivity. User can start navigation from current
    location to the one provided.

 */
public class NavigationActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //(Latitude, Altitude)
    private boolean firstTime = true;
    private String[] parts;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private double mLastLLatitude = 0;
    private double mLastLLongitude = 0;

    /**********************************************************************************************/
    /*
        Method called when the Activity is created. Gets the message from the intent and splits it
        in Latitude and Altitude. Then starts google maps navigation activity.

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*
            Gets the destiny coordinates from the MainActivity.
            Saves it in a string array.
         */
        Intent intentInfo = this.getIntent();
        String message = intentInfo.getStringExtra("message");
        parts = message.split("_");

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient.isConnected() == false) {
            mGoogleApiClient.connect();
        } else {
            System.out.print("Connected.");
        }


        if(mLastLLongitude != 0 && mLastLLatitude != 0){
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + parts[1] + "," + parts[3]);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }

        /*
            Start navigation from current location to destiny. (Google maps activity).
            */

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + mLastLLatitude + "," + mLastLLongitude + "&daddr=" + parts[1] + "," + parts[3]));
        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLastLLatitude = mLastLocation.getLatitude();
            mLastLLongitude = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(mGoogleApiClient.isConnected()==false) {
            mGoogleApiClient.connect();
        }
    }
}

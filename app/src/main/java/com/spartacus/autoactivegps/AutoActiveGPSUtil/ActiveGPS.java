package com.spartacus.autoactivegps.AutoActiveGPSUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.spartacus.autoactivegps.MainActivity;
import com.spartacus.autoactivegps.PermissionUtil.PermissionUtil;

import java.security.Permission;
import java.util.Random;

/**
 * Created by Abandah on 1/18/2018.
 */

public class ActiveGPS implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static OnResuleListener onResuleListener = null;
    private static GoogleApiClient googleApiClient;
    private static Location mylocation;
    private static int requestCode;
    private static Context context;




    public void TurnOnGPS(Context context, GPSListener listener) {
        this.context=context;
        setUpGClient(context);
        onResuleListener=listener;

    }
    private synchronized void setUpGClient(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity) context, RandomInt(), this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getMyLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
       /* mylocation = location;
        if (mylocation != null) {
            Double latitude=mylocation.getLatitude();
            Double longitude=mylocation.getLongitude();
            latitudeTextView.setText("Latitude : "+latitude);
            longitudeTextView.setText("Longitude : "+longitude);
            //Or Do whatever you want with your location
        }*/
    }
    @SuppressLint("MissingPermission")
    private static void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) context);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        requestCode= RandomInt();
                                        status.startResolutionForResult((Activity) context,
                                                requestCode);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
        }
    }
    public static int RandomInt() {
        int min = 0;
        int max = 500;

        Random r = new Random();
        return r.nextInt(max - min + 1) + min;

    }
    public abstract static class GPSListener extends OnResuleListener {
        @Override
        public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
            super.onActivityResult(context, requestCode, resultCode, data);
            if(requestCode==ActiveGPS.requestCode)
            {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        ActiveGPS.getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
            }

        }

        public void GPS_IS_ON() {
        }

        public void Permission_Grand_Cant_Turn_GPS_ON() {
        }

        public void Permission_Denied() {

        }
    }

    public abstract static class OnResuleListener {
        public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        }

        public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {

        }

        public void onResume(Context context) {

        }
    }
}

package com.spartacus.autoactivegps.AutoActiveGPSUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

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
import com.spartacus.autoactivegps.AutoActiveGPSUtil.PermissionUtil.Constants;
import com.spartacus.autoactivegps.AutoActiveGPSUtil.PermissionUtil.PermissionUtil;

import java.util.Random;

import static com.spartacus.autoactivegps.AutoActiveGPSUtil.PermissionUtil.Constants.ACCESS_FINE_LOCATION;

/**
 * Created by Abandah on 1/18/2018.
 *
 */

public class ActiveGPS implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static OnResuleListener onResuleListener = null;
    private GPSListener listener = null;
    private static GoogleApiClient googleApiClient;
    private static Location mylocation;
    private static int requestCode;
    private Context context;
    private static ActiveGPS activeGPS = null;
    private static boolean ActiveJustOnce=false;

    public void TurnOnGPS(final Context context, ActiveGPS activeGPS, final GPSListener listener) {
        TurnOnGPS( context, activeGPS, listener, false) ;
    }

    public void TurnOnGPS(final Context context, ActiveGPS activeGPS, final GPSListener listener,boolean ActiveJustOnce) {
        this.ActiveJustOnce=ActiveJustOnce;
        this.context = context;
        onResuleListener = listener;
        this.listener = listener;
        this.activeGPS = activeGPS;
        PermissionUtil.checkPermission(context, ACCESS_FINE_LOCATION,listener, new PermissionUtil.PermissionAskListener() {
            @Override
            public void onDenied() {
                super.onDenied();
                listener.Permission_Denied();
            }

            @Override
            public void onGranted() {
                super.onGranted();
                AskForLocationPerm(context);
            }

            @Override
            public void AllReadyGranted() {
                super.AllReadyGranted();
                AskForLocationPerm(context);

            }
        });

    }

    private void AskForLocationPerm(final Context context) {
        PermissionUtil.checkPermission(context, Constants.ACCESS_COARSE_LOCATION, new PermissionUtil.PermissionAskListener() {
            @Override
            public void onDenied() {
                super.onDenied();
                listener.Permission_Denied();

            }

            @Override
            public void onGranted() {
                super.onGranted();
                setUpGClient(context);


            }

            @Override
            public void AllReadyGranted() {
                super.AllReadyGranted();
                setUpGClient(context);


            }

            @Override
            public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
                super.onActivityResult(context, requestCode, resultCode, data);
                ActiveGPS.onResuleListener.onActivityResult(context, requestCode, resultCode, data);

            }
        });

    }

    private synchronized void setUpGClient(Context context) {
        try {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .enableAutoManage((FragmentActivity) context, RandomInt(), this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
            onResuleListener.OnError(e.getMessage());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getMyLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {
        onResuleListener.OnError("ConnectionSuspended" + i);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            Double latitude = mylocation.getLatitude();
            Double longitude = mylocation.getLongitude();
            listener.GPS_IS_ON(mylocation);
            //Or Do whatever you want with your location
        }
    }

    @SuppressLint("MissingPermission")
    private void getMyLocation() {
        if (googleApiClient != null) {
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
                        .requestLocationUpdates(googleApiClient, locationRequest, this);
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
                                    requestCode = RandomInt();
                                    status.startResolutionForResult((Activity) context,
                                            requestCode);
                                } catch (IntentSender.SendIntentException e) {
                                    onResuleListener.OnError(e.getMessage());
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
            } else
                onResuleListener.OnError("googleApiClient is not Connected");
        } else onResuleListener.OnError("googleApiClient == null");
    }

    private static int RandomInt() {
        int min = 0;
        int max = 500;

        Random r = new Random();
        return r.nextInt(max - min + 1) + min;

    }

    public abstract static class GPSListener extends OnResuleListener {

        @Override
        public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
            super.onActivityResult(context, requestCode, resultCode, data);
            if (requestCode == ActiveGPS.requestCode) {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        activeGPS.getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        onResuleListener.OnError("RESULT_CANCELED");
                        break;
                }
            }

        }

        public void GPS_IS_ON(Location location) {
            if(ActiveJustOnce)
                googleApiClient.disconnect();

        }

        public void Permission_Grand_Cant_Turn_GPS_ON() {
        }

        public void Permission_Denied() {

        }

        @Override
        public void OnError(String error) {

        }

    }

    public abstract static class OnResuleListener {
        public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        }

        public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {

        }

        public void onResume(Context context) {


        }

        public void OnError(String error) {

        }
    }
}

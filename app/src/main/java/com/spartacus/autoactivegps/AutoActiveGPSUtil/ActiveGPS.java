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
import com.spartacus.autoactivegps.AutoActiveGPSUtil.PermissionUtil.PermissionHelper;

import java.util.Random;

import static com.spartacus.autoactivegps.AutoActiveGPSUtil.PermissionUtil.Constants.ACCESS_FINE_LOCATION;

/**
 * Created by Abandah on 1/18/2018.
 */

public class ActiveGPS implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GPSListener listener = null;
    private GoogleApiClient googleApiClient;
    private Location mylocation;
    private static int requestCode;
    private Context context;
    private static boolean ActiveJustOnce = false;
    PermissionHelper permissionHelper=null;

    public ActiveGPS(Context context) {
        this.context = context;
    }


    public void TurnOnGPS(GPSListener listener) {
        TurnOnGPS(listener, false);
    }

    public void TurnOnGPS(final GPSListener listener, boolean ActiveJustOnce) {
        this.ActiveJustOnce = ActiveJustOnce;
        this.listener = listener;
        permissionHelper= new PermissionHelper(context);
        permissionHelper.checkPermission(ACCESS_FINE_LOCATION, new PermissionHelper.PermissionAskListener() {
            @Override
            public void onDenied() {
                listener.Permission_Denied();
            }

            @Override
            public void onGranted() {
                AskForLocationPerm(context);
            }

            @Override
            public void AllReadyGranted() {
                AskForLocationPerm(context);

            }
        });

    }

    private void AskForLocationPerm(final Context context) {
        permissionHelper.checkPermission( Constants.ACCESS_COARSE_LOCATION, new PermissionHelper.PermissionAskListener() {
            @Override
            public void onDenied() {
                listener.Permission_Denied();

            }

            @Override
            public void onGranted() {
                setUpGClient(context);


            }

            @Override
            public void AllReadyGranted() {
                setUpGClient(context);


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
            listener.OnError(e.getMessage());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getMyLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {
        listener.OnError("ConnectionSuspended" + i);

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
            if (ActiveJustOnce)
                googleApiClient.disconnect();
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
                                    listener.OnError(e.getMessage());
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
                listener.OnError("googleApiClient is not Connected");
        } else listener.OnError("googleApiClient == null");
    }

    private int RandomInt() {
        int min = 0;
        int max = 500;

        Random r = new Random();
        return r.nextInt(max - min + 1) + min;

    }

    public interface GPSListener {


        public void GPS_IS_ON(Location location) ;

        public void Permission_Grand_Cant_Turn_GPS_ON();
        public void Permission_Denied() ;

        public void OnError(String error) ;

    }

    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        if (permissionHelper!=null)

            permissionHelper.onRequestPermissionsResult(context, requestCode,permissions,grantResults);

    }

    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (requestCode == ActiveGPS.requestCode) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    getMyLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    listener.OnError("RESULT_CANCELED");
                    break;
            }
        }
        if (permissionHelper!=null)

            permissionHelper.onActivityResult(context,requestCode,resultCode,data);


    }

    public void onResume(Context context) {
        if (permissionHelper!=null)
        permissionHelper.onResume(context);

    }



}

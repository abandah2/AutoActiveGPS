package com.spartacus.autoactivegps;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.spartacus.autoactivegps.AutoActiveGPSUtil.ActiveGPS;
import com.spartacus.autoactivegps.PermissionUtil.Constants;
import com.spartacus.autoactivegps.PermissionUtil.PermissionUtil;

import static com.spartacus.autoactivegps.PermissionUtil.Constants.ACCESS_FINE_LOCATION;
import static com.spartacus.autoactivegps.PermissionUtil.PermissionUtil.onResuleListener;

public class MainActivity extends AppCompatActivity {
    private final String TAG =this.getClass().getName() ;
    private TextView latitudeTextView, longitudeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitudeTextView =  findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);


        Button crashButton = new Button(this);
        crashButton.setText("Crash!");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                ActiveGPS activeGPS = new ActiveGPS();
                activeGPS.TurnOnGPS(MainActivity.this,activeGPS, new ActiveGPS.GPSListener() {

                    @Override
                    public void GPS_IS_ON(Location location) {
                        super.GPS_IS_ON(location);
                        Log.e(TAG,new Object(){}.getClass().getEnclosingMethod().getName());
                        Log.e(TAG,location.toString());
                        latitudeTextView.setText(location.getLatitude()+"");
                        longitudeTextView.setText(location.getLongitude()+"");
                    }

                    @Override
                    public void Permission_Grand_Cant_Turn_GPS_ON() {
                        super.Permission_Grand_Cant_Turn_GPS_ON();
                        Log.e(TAG,new Object(){}.getClass().getEnclosingMethod().getName());
                    }

                    @Override
                    public void Permission_Denied() {
                        super.Permission_Denied();
                        Log.e(TAG,new Object(){}.getClass().getEnclosingMethod().getName());
                    }
                    @Override
                    public void OnError(String error) {
                        super.OnError(error);
                        Log.e(TAG,new Object(){}.getClass().getEnclosingMethod().getName());
                        Log.e(TAG,error);
                    }

                });
                }

        });
        addContentView(crashButton,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (onResuleListener != null)
            onResuleListener.onRequestPermissionsResult(this, requestCode, permissions, grantResults);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (onResuleListener != null)
            onResuleListener.onResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (onResuleListener != null)
            onResuleListener.onActivityResult(this, requestCode, resultCode, data);

    }
}

package com.spartacus.autoactivegps;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.spartacus.autoactivegps.AutoActiveGPSUtil.ActiveGPS;


public class MainActivity extends AppCompatActivity {
    private final String TAG =this.getClass().getName() ;
    private TextView latitudeTextView, longitudeTextView;

    ActiveGPS activeGPS=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeGPS = new ActiveGPS(MainActivity.this);
        setContentView(R.layout.activity_main);
        latitudeTextView =  findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);


        Button crashButton = new Button(this);
        crashButton.setText("Crash!");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                
                activeGPS.TurnOnGPS( new ActiveGPS.GPSListener() {

                    @Override
                    public void GPS_IS_ON(Location location) {
                        Log.e(TAG,new Object(){}.getClass().getEnclosingMethod().getName());
                        Log.e(TAG,location.toString());
                        latitudeTextView.setText(location.getLatitude()+"");
                        longitudeTextView.setText(location.getLongitude()+"");
                    }

                    @Override
                    public void Permission_Grand_Cant_Turn_GPS_ON() {
                        Log.e(TAG,new Object(){}.getClass().getEnclosingMethod().getName());
                    }

                    @Override
                    public void Permission_Denied() {
                        Log.e(TAG,new Object(){}.getClass().getEnclosingMethod().getName());
                    }
                    @Override
                    public void OnError(String error) {
                        Log.e(TAG,new Object(){}.getClass().getEnclosingMethod().getName());
                        Log.e(TAG,error);
                    }

                },true);
                }

        });
        addContentView(crashButton,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            activeGPS.onRequestPermissionsResult(this, requestCode, permissions, grantResults);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
            activeGPS.onResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            activeGPS.onActivityResult(this, requestCode, resultCode, data);

    }
}

package com.spartacus.autoactivegps;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.spartacus.autoactivegps.AutoActiveGPSUtil.ActiveGPS;
import com.spartacus.autoactivegps.PermissionUtil.Constants;
import com.spartacus.autoactivegps.PermissionUtil.PermissionUtil;

import static com.spartacus.autoactivegps.PermissionUtil.Constants.ACCESS_FINE_LOCATION;
import static com.spartacus.autoactivegps.PermissionUtil.PermissionUtil.onResuleListener;

public class MainActivity extends AppCompatActivity {
    private TextView latitudeTextView,longitudeTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitudeTextView=(TextView)findViewById(R.id.latitudeTextView);
        longitudeTextView=(TextView)findViewById(R.id.longitudeTextView);


        Button crashButton = new Button(this);
        crashButton.setText("Crash!");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PermissionUtil.checkPermission(MainActivity.this, ACCESS_FINE_LOCATION, new PermissionUtil.PermissionAskListener() {
                    @Override
                    public void onDenied() {
                        super.onDenied();
                        Log.e("ASDASDASD","onDenied");

                    }

                    @Override
                    public void onGranted() {
                        super.onGranted();
                        AskForLocationPerm();
                    }

                    @Override
                    public void AllReadyGranted() {
                        super.AllReadyGranted();
                        AskForLocationPerm();

                    }
                });

            }

        });
        addContentView(crashButton,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void AskForLocationPerm() {
        PermissionUtil.checkPermission(MainActivity.this, Constants.ACCESS_COARSE_LOCATION, new PermissionUtil.PermissionAskListener() {
            @Override
            public void onDenied() {
                super.onDenied();

            }

            @Override
            public void onGranted() {
                super.onGranted();

            }

            @Override
            public void AllReadyGranted() {
                super.AllReadyGranted();

                ActiveGPS activeGPS= new ActiveGPS();
                activeGPS.TurnOnGPS(MainActivity.this, new ActiveGPS.GPSListener() {
                    @Override
                    public void GPS_IS_ON() {
                        super.GPS_IS_ON();
                    }

                    @Override
                    public void Permission_Grand_Cant_Turn_GPS_ON() {
                        super.Permission_Grand_Cant_Turn_GPS_ON();
                    }

                    @Override
                    public void Permission_Denied() {
                        super.Permission_Denied();
                    }
                });
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (onResuleListener != null)
            onResuleListener.onRequestPermissionsResult(this, requestCode,permissions,grantResults);
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
            onResuleListener.onActivityResult(this,requestCode,resultCode,data);
        ActiveGPS.onResuleListener.onActivityResult(this,requestCode,resultCode,data);

    }
}

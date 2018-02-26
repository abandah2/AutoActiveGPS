package com.spartacus.autoactivegps.AutoActiveGPSUtil.PermissionUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Abandah on 1/7/2018.
 */
public class PermissionHelper {

    public PermissionAskListener permissionAskListener = null;


    private int permission_requestCode;
    private int openSetting_requestCode;
    private Context mcontext;
    private String permissionname;
    private String usermessage;
    private boolean sentToSettings = false;

    public PermissionHelper(Context context) {
        this.mcontext = context;
    }

    private boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    private boolean shouldAskPermission(Context context, String permission) {
        if (shouldAskPermission()) {
            int permissionResult = ActivityCompat.checkSelfPermission(context, permission);
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }


    public void checkPermission(String permission, PermissionAskListener listener) {
        String[] per = permission.split("\\.");
        checkPermission(permission, listener, RegMethods.getApplicationName(mcontext) + " " + "Need your permission to Use " + per[per.length - 1]);
    }

    public void checkPermission(String permission, final PermissionAskListener listener, String message) {

       usermessage = message;
       permissionname = permission;
        //mcontext = context;
        permissionAskListener = listener;
        permission_requestCode = RegMethods.RandomInt();
        openSetting_requestCode = RegMethods.RandomInt();
        while (permission_requestCode == openSetting_requestCode) {
            openSetting_requestCode = RegMethods.RandomInt();
        }


        if (shouldAskPermission(mcontext, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mcontext, permission)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                builder.setTitle("Need Permission");
                builder.setMessage(message);
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions((Activity) mcontext,
                                new String[]{permissionname},
                                permission_requestCode);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDenied();
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (SharedPreferences.isFirstTimeAskingPermission(mcontext, permission)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                builder.setTitle("really Need Permission");
                builder.setMessage("This app really needs this permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mcontext.getPackageName(), null);
                        intent.setData(uri);
                        ((Activity) mcontext).startActivityForResult(intent, openSetting_requestCode);
                        Toast.makeText(((Activity) mcontext).getBaseContext(), "Go to Permissions ", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDenied();
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions((Activity) mcontext, new String[]{permission}, permission_requestCode);
            }

            boolean firsttime = SharedPreferences.firstTimeAskingPermission(mcontext, permission);
            Log.e("first time ", firsttime + "");

        } else {
            listener.AllReadyGranted();
        }
    }

    public interface  PermissionAskListener {

         void AllReadyGranted() ;


         void onGranted() ;

         void onDenied() ;
    }

    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == permission_requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                permissionAskListener.onGranted();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissionname)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Need Permission");
                    builder.setMessage(usermessage);
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();


                            ActivityCompat.requestPermissions((Activity) mcontext, new String[]{permissionname}, permission_requestCode);


                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            permissionAskListener.onDenied();
                        }
                    });
                    builder.show();
                } else {
                    permissionAskListener.onDenied();
                    Toast.makeText(((Activity) context).getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (requestCode == openSetting_requestCode) {
            if (ActivityCompat.checkSelfPermission(context, permissionname) == PackageManager.PERMISSION_GRANTED) {
                permissionAskListener.onGranted();
            }
        }
    }

    public void onResume(Context context) {
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(context, permissionname) != PackageManager.PERMISSION_GRANTED) {
                permissionAskListener.onDenied();
            }
        }
    }


}






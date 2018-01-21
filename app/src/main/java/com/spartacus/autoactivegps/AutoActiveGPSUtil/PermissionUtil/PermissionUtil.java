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

import com.spartacus.autoactivegps.AutoActiveGPSUtil.ActiveGPS;

/**
 * Created by Abandah on 1/7/2018.
 *
 */
public class PermissionUtil {

    @SuppressWarnings("WeakerAccess")
    public static OnResuleListener onResuleListener = null;

    private static ActiveGPS.OnResuleListener GPSonResuleListener = null;


    private static int permission_requestCode;
    private static int openSetting_requestCode;
    //private static Context mcontext;
    private static String permissionname;
    private static String usermessage;
    private static boolean sentToSettings = false;

    private static boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    private static boolean shouldAskPermission(Context context, String permission) {
        if (shouldAskPermission()) {
            int permissionResult = ActivityCompat.checkSelfPermission(context, permission);
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("WeakerAccess")
    public static void checkPermission(Context context, String permission, ActiveGPS.OnResuleListener listener2, PermissionAskListener listener) {
        GPSonResuleListener = listener2;
        String[] per = permission.split("\\.");
        checkPermission(context, permission, listener, RegMethods.getApplicationName(context) + " " + "Need your permission to Use " + per[per.length - 1]);
    }

    @SuppressWarnings("WeakerAccess")
    public static void checkPermission(Context context, String permission, PermissionAskListener listener) {
        String[] per = permission.split("\\.");
        checkPermission(context, permission, listener, RegMethods.getApplicationName(context) + " " + "Need your permission to Use " + per[per.length - 1]);
    }

    @SuppressWarnings("WeakerAccess")
    public static void checkPermission(final Context context, String permission, final PermissionAskListener listener, String message) {

        PermissionUtil.usermessage = message;
        PermissionUtil.permissionname = permission;
        //mcontext = context;
        onResuleListener = listener;

        permission_requestCode = RegMethods.RandomInt();
        openSetting_requestCode = RegMethods.RandomInt();
        while (permission_requestCode == openSetting_requestCode) {
            openSetting_requestCode = RegMethods.RandomInt();
        }


        if (shouldAskPermission(context, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Need Permission");
                builder.setMessage(message);
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions((Activity) context,
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
            } else if (SharedPreferences.isFirstTimeAskingPermission(context, permission)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("really Need Permission");
                builder.setMessage("This app really needs this permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        PermissionUtil.sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        ((Activity) context).startActivityForResult(intent, openSetting_requestCode);
                        Toast.makeText(((Activity) context).getBaseContext(), "Go to Permissions ", Toast.LENGTH_LONG).show();
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
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, permission_requestCode);
            }

            boolean firsttime = SharedPreferences.firstTimeAskingPermission(context, permission);
            Log.e("first time ", firsttime + "");

        } else {
            listener.AllReadyGranted();
        }
    }

    public abstract static class PermissionAskListener extends OnResuleListener {

        @Override
        public void onRequestPermissionsResult(final Context context, int requestCode, final String[] permissions, final int[] grantResults) {
            if (requestCode == permission_requestCode) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //The External Storage Write Permission is granted to you... Continue your left job...
                    onGranted();
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


                                ActivityCompat.requestPermissions((Activity) context, new String[]{permissionname}, permission_requestCode);


                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                onDenied();
                            }
                        });
                        builder.show();
                    } else {
                        onDenied();
                        Toast.makeText(((Activity) context).getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                    }
                }
            }



       /*     if (PermissionUtil.permission_requestCode != requestCode) return;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != 0) {
                    onDenied(permissions[i], requestCode);
                    return;
                }

            }
            onGranted(permissions, requestCode);*/
        }

        @Override
        public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
            super.onActivityResult(context, requestCode, resultCode, data);
            if (requestCode == openSetting_requestCode) {
                if (ActivityCompat.checkSelfPermission(context, permissionname) == PackageManager.PERMISSION_GRANTED) {
                    onGranted();
                }
            }
        }

        @Override
        public void onResume(Context context) {
            super.onResume(context);
            if (sentToSettings) {
                if (ActivityCompat.checkSelfPermission(context, permissionname) != PackageManager.PERMISSION_GRANTED) {
                    onDenied();
                }
            }
        }

        public void AllReadyGranted() {

        }


        public void onGranted() {

        }

        public void onDenied() {

        }
    }

    public abstract static class OnResuleListener {
        public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
            if (GPSonResuleListener != null)
                GPSonResuleListener.onRequestPermissionsResult(context, requestCode, permissions, grantResults);
        }

        public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
            if (GPSonResuleListener != null)
                GPSonResuleListener.onActivityResult(context, requestCode, resultCode, data);

        }

        public void onResume(Context context) {
            if (GPSonResuleListener != null)
                GPSonResuleListener.onResume(context);

        }
    }


}






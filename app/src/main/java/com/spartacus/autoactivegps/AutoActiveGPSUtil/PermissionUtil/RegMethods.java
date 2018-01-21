package com.spartacus.autoactivegps.AutoActiveGPSUtil.PermissionUtil;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.util.Random;

/**
 * Created by Abandah on 1/7/2018.
 *
 */
@SuppressWarnings("WeakerAccess")
public class RegMethods {
    @SuppressWarnings("WeakerAccess")
    public static int RandomInt() {
        int min = 0;
        int max = 500;

        Random r = new Random();
        return r.nextInt(max - min + 1) + min;

    }
    @SuppressWarnings("WeakerAccess")
    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
}

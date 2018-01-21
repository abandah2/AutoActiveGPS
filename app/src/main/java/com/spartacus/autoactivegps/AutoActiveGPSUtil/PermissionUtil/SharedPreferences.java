package com.spartacus.autoactivegps.AutoActiveGPSUtil.PermissionUtil;

import android.content.Context;

/**
 * Created by Abandah on 1/7/2018.
 *
 */
@SuppressWarnings("WeakerAccess")
class SharedPreferences {
    private static final String PREFERENCE = "PermissionUtil";

    @SuppressWarnings("WeakerAccess")
    public static boolean isFirstTimeAskingPermission(Context context, String prefKey) {
        return context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE).getBoolean(prefKey, false);
    }
    @SuppressWarnings("WeakerAccess")
    public static boolean firstTimeAskingPermission(Context context, String prefKey) {
        return context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE).edit().putBoolean(prefKey, true).commit();
    }

}

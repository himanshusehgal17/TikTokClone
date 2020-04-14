package com.himanshu.housingtask.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

public class KEYS {
    public static final int CAMERA_AND_STORAGE_PERMISSION = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int UPLOAD_NEW_VIDEO = 3;

    @SuppressLint("HardwareIds")
    public static String DEVICE_ID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}

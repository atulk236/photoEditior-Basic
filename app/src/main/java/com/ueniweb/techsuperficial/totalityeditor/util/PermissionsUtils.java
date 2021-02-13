package com.ueniweb.techsuperficial.totalityeditor.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static com.ueniweb.techsuperficial.totalityeditor.views.EditorActivity.READ_WRITE_STORAGE;

public class PermissionsUtils {
    public static boolean checkCameraPermission(Activity activity) {
        boolean z = ContextCompat.checkSelfPermission(activity.getApplicationContext(), "android.permission.CAMERA") == 0;
        if (!z) {
            ActivityCompat.requestPermissions(activity, PermissionsConstant.PERMISSIONS_CAMERA, 1);
        }
        return z;
    }

    public static boolean checkWriteStoragePermission(Activity activity) {
        boolean z = ContextCompat.checkSelfPermission(activity.getApplicationContext(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
        if (!z) {
            ActivityCompat.requestPermissions(activity, PermissionsConstant.PERMISSIONS_EXTERNAL_WRITE, 3);
        }
        return z;
    }
}

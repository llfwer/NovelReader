package com.example.newbiechen.ireader.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class UPPermissionUtil {

    public static final int CODE = 666;

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static boolean checkPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        List<String> list = new ArrayList<>();
        for (String item : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), item) != PackageManager.PERMISSION_GRANTED) {
                list.add(item);
            }
        }
        if (list.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(activity, list.toArray(new String[]{}), CODE);
        return false;
    }

    public static boolean checkResult(int[] results) {
        boolean isGranted = true;
        for (int item : results) {
            if (item != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }
        return isGranted;
    }
}
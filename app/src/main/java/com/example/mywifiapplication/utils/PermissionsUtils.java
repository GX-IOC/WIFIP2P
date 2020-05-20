package com.example.mywifiapplication.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class PermissionsUtils {

    private PermissionsUtils() {
    }

    private static PermissionsUtils permissionsUtils;

    public static PermissionsUtils getInstance() {
        if (permissionsUtils == null) {
            permissionsUtils = new PermissionsUtils();
        }
        return permissionsUtils;
    }

    public void checkPermissions(Activity context, String[] permissions) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        List<String> mPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        }

        if (mPermissionList.size() > 0) {
            //权限请求码
            ActivityCompat.requestPermissions(context, permissions, 100);
        }
    }

}

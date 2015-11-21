package im.amomo.homework.util;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/21/15.
 */
public class Utils {

    public static boolean isNetworkConnect(@NonNull Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) ==
                PackageManager.PERMISSION_GRANTED) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Service.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }
        return true;
    }
}

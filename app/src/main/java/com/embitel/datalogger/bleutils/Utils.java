package com.embitel.datalogger.bleutils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Utils {

    private static boolean useRunTimePermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean checkLocationPermission(Activity baseActivity) {
        if (baseActivity == null) {
            return true;
        }

        try {
            if (useRunTimePermission()) {
                if (ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(baseActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return false;
                }else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getCityName(double wayLatitude, double wayLongitude, Context context) throws IOException {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(wayLatitude, wayLongitude, 1);
        if(addresses.get(0).getLocality()==null)
        {
            return null;
        }
        return addresses.get(0).getLocality();
    }

}

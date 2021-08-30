package com.pfa.pfaapp.locationutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

/**
 * Defines app-wide constants and utilities
 */
public final class LocationUtils {

    public static boolean isLocationServiceEnabled(Context context) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled;
            assert locationManager != null;
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return isGPSEnabled;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void showSettingsAlert(final Activity activity) {
        if (activity != null) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                  alertDialog.setMessage("Kindly Enable location service provider to get location.");
            alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    dialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(intent);
                }
            });

            alertDialog.setCancelable(false);
            if (!activity.isFinishing()) alertDialog.show();
        }
    }
}

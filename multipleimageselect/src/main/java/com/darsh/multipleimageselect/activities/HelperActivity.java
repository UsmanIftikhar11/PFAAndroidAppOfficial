package com.darsh.multipleimageselect.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.darsh.multipleimageselect.R;
import com.darsh.multipleimageselect.helpers.Constants;

/**
 * Created by darshan on 26/9/16.
 */
@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class HelperActivity extends AppCompatActivity {
    protected View view;

    private final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO};
    private final String[] permissions_c = new String[]{Manifest.permission.CAMERA};

    protected void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted();

        } else {
            ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, permissions_c, Constants.PERMISSION_REQUEST_CODE);

        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showRequestPermissionRationale();

        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
            showRequestPermissionRationale();

        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_VIDEO)) {
            showRequestPermissionRationale();

        } else {
            showAppPermissionSettings();
        }
    }

    private void showRequestPermissionRationale() {
        Snackbar snackbar = Snackbar.make(
                view,
                getString(R.string.permission_info),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.permission_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(
                                HelperActivity.this,
                                permissions,
                                Constants.PERMISSION_REQUEST_CODE);
                    }
                });

        snackbar.show();
    }

    private void showAppPermissionSettings() {
        Snackbar snackbar = Snackbar.make(
                view,
                getString(R.string.permission_force),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.permission_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.fromParts(
                                getString(R.string.permission_package),
                                HelperActivity.this.getPackageName(),
                                null);

                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setData(uri);
                        startActivityForResult(intent, Constants.PERMISSION_REQUEST_CODE);
                    }
                });

        snackbar.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != Constants.PERMISSION_REQUEST_CODE
                || grantResults.length == 0
                || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            permissionDenied();

        } else {
            permissionGranted();
        }

    }

    protected void permissionGranted() {
    }

    private void permissionDenied() {
        hideViews();
        requestPermission();
    }

    protected void hideViews() {
    }

    protected void setView(View view) {
        this.view = view;
    }
}

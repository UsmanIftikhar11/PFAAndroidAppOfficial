package com.pfa.pfaapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.samsung.android.sdk.pass.SpassFingerprint;

public class PasswordBR extends BroadcastReceiver {
    @Override
    public void onReceive(Context mContext, Intent intent) {
        final String action = intent.getAction();
        if (SpassFingerprint.ACTION_FINGERPRINT_RESET.equals(action)) {
            Toast.makeText(mContext, "all fingerprints are removed", Toast.LENGTH_SHORT).show();
        } else if (SpassFingerprint.ACTION_FINGERPRINT_REMOVED.equals(action)) {
            int fingerIndex = intent.getIntExtra("fingerIndex", 0);
            Toast.makeText(mContext, fingerIndex + " fingerprints is removed", Toast.LENGTH_SHORT).show();
        } else if (SpassFingerprint.ACTION_FINGERPRINT_ADDED.equals(action)) {
            int fingerIndex = intent.getIntExtra("fingerIndex", 0);
            Toast.makeText(mContext, fingerIndex + " fingerprints is added", Toast.LENGTH_SHORT).show();
        }
    }
}

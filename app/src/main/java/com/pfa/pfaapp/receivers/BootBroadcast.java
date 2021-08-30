package com.pfa.pfaapp.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcast extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context ctx, Intent intent) {
//        if ((new SharedPrefUtils(ctx).getUserInfo() != null))
//            if (LocationUtils.isLocationServiceEnabled(ctx))
//                (new LocationUpdatesService()).requestLocationUpdates(ctx);
    }
}
package com.pfa.pfaapp.emergency;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class RegisterReceiverService extends Service {

    Handler handler = new Handler();
    ScreenReceiver mReceiver;
    public static boolean isService = true;

    @Override
    public void onCreate() {
        super.onCreate();
        isService = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerScreenReceiver();

        return START_STICKY;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    private void registerScreenReceiver() {
        IntentFilter filter = null;
        try {
            filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);

            // Customized BroadcastReceiver class
            // Will be defined soon..

            if (mReceiver == null) {
                mReceiver = new ScreenReceiver();
                registerReceiver(mReceiver, filter);
            }
        } catch (Exception e) {
            if (mReceiver != null)
                unregisterReceiver(mReceiver);

            mReceiver = new ScreenReceiver();
            registerReceiver(mReceiver, filter);


            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

        handler = null;
        Toast.makeText(this, " Service closed", Toast.LENGTH_SHORT).show();

        isService = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

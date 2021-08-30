package com.pfa.pfaapp.emergency;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.pfa.pfaapp.utils.SharedPrefUtils;

import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;


public class ScreenReceiver extends BroadcastReceiver {
    SharedPrefUtils sharedPrefUtils;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent == null || intent.getAction() == null)
            return;
        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_OFF:

                break;
            case Intent.ACTION_SCREEN_ON:
                sharedPrefUtils = new SharedPrefUtils(context);
                if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent myIntent = new Intent(context, LockHelpActivity.class);
                            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(myIntent);
                        }
                    }, 1000);
                }
                break;
            case Intent.ACTION_USER_PRESENT:
//                Log.e("ScreenReceiver", "In Method:  ACTION_USER_PRESENT");
                // Handle resuming events
                break;
        }
    }

}
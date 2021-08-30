package com.pfa.pfaapp;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.receivers.PasswordBR;
import com.pfa.pfaapp.utils.AppUtils;
import com.samsung.android.sdk.pass.SpassFingerprint;
import com.samsung.android.sdk.pass.SpassInvalidStateException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_FP_ACTION;
import static com.pfa.pfaapp.utils.AppConst.FP_LOGIN;
import static com.pfa.pfaapp.utils.AppConst.FP_SIGNUP;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_SECURITY_CODE;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;

public class FPrintActivity extends BaseActivity {

    private Context mContext;
    private ArrayList<Integer> designatedFingers = null;

    private boolean needRetryIdentify = false;
    private boolean onReadyIdentify = false;
    private boolean onReadyEnroll = false;

    private PasswordBR mPassReceiver = new PasswordBR();

    TextView fpMsgTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fprint);
        fpMsgTV = findViewById(R.id.fpMsgTV);
        mContext = this;
        enableFP();

        sharedPrefUtils.applyFont(fpMsgTV, AppUtils.FONTS.HelveticaNeue);

    }

    private void enableFP() {
        if (!isFPFeatureEnabled()) {
            sharedPrefUtils.showMsgDialog("Fingerprint Service Is Not Supported in this Device.", null);
            return;
        }
        registerBroadcastReceiver();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey(EXTRA_FP_ACTION)) {
            String extraStr = bundle.getString(EXTRA_FP_ACTION);
            if (extraStr!=null && extraStr.equalsIgnoreCase(FP_SIGNUP)) {
                String message = "<b>Scan your fingerprint</b> </br> <p>Place a finger on the fingerprint sensor, lift it off, then repeat. Move your finger left or right slightly between attempts.</p>";
                fpMsgTV.setText(Html.fromHtml(message));
                registerFingerprint();
            } else if (extraStr!=null && extraStr.equalsIgnoreCase(FP_LOGIN)) {
                String message = "<b>Fingerprint Authentication</b> </br> <p>Place your fingertip on the Home button to verify your identity</p>";
                fpMsgTV.setText(Html.fromHtml(message));
                startIdentityHander(false);

            }
        }


//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        getConfirmation();
//                    }
//                });
//            }
//        }, 1500);

    }

//    private void getConfirmation() {
//        String pincode = "";
//        String userId = "";
//        if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN) != null) {
//            userId = sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID);
//        }
//        pincode = sharedPrefUtils.getSharedPrefValue(SP_SECURITY_CODE);
//        httpService.getUserConfirmation(userId, pincode, new HttpResponseCallback() {
//            @Override
//            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
//                if (response!= null)
//                {
//                    try {
//                        String status = response.getString("status");
//                        if (status == "false"){
//                            if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN) != null) {
//                                sharedPrefUtils.logoutFromApp(httpService);
//                                Toast.makeText(FPrintActivity.this, "Unauthentic User", Toast.LENGTH_SHORT).show();
//
//                            } else {
//                                sharedPrefUtils.startNewActivity(LoginActivity.class, null, false);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }

    private void resetAll() {
        designatedFingers = null;
        needRetryIdentify = false;
        onReadyIdentify = false;
        onReadyEnroll = false;
    }

    private SpassFingerprint.IdentifyListener mIdentifyListener = new SpassFingerprint.IdentifyListener() {
        @Override
        public void onFinished(int eventStatus) {
            log("identify finished : reason =" + getEventStatusName(eventStatus));
            int FingerprintIndex = 0;
            String FingerprintGuideText;
            try {
                FingerprintIndex = mSpassFingerprint.getIdentifiedFingerprintIndex();
            } catch (IllegalStateException ise) {
                log(ise.getMessage());
            }
            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
                log("onFinished() : Identify authentication Success with FingerprintIndex : " + FingerprintIndex);
                sharedPrefUtils.startHomeActivity(PFADrawerActivity.class, null);

            } else if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
                log("onFinished() : Password authentication Success");
            } else if (eventStatus == SpassFingerprint.STATUS_OPERATION_DENIED) {
                log("onFinished() : Authentication is blocked because of fingerprint service internally.");
            } else if (eventStatus == SpassFingerprint.STATUS_USER_CANCELLED) {
                log("onFinished() : User cancel this identify.");
            } else if (eventStatus == SpassFingerprint.STATUS_TIMEOUT_FAILED) {
                log("onFinished() : The time for identify is finished.");
            } else if (eventStatus == SpassFingerprint.STATUS_QUALITY_FAILED) {
                log("onFinished() : Authentication Fail for identify.");
                needRetryIdentify = true;
                FingerprintGuideText = mSpassFingerprint.getGuideForPoorQuality();
                Toast.makeText(mContext, FingerprintGuideText, Toast.LENGTH_SHORT).show();
            } else {
                log("onFinished() : Authentication Fail for identify");
                needRetryIdentify = true;
            }
            if (!needRetryIdentify) {
                resetIdentifyIndex();
            }
        }

        @Override
        public void onReady() {
            log("identify state is ready");
        }

        @Override
        public void onStarted() {
            log("User touched fingerprint sensor");
        }

        @Override
        public void onCompleted() {
            log("the identify is completed");
            onReadyIdentify = false;

            startIdentityHander(false);

        }
    };

    private static String getEventStatusName(int eventStatus) {
        switch (eventStatus) {
            case SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS:
                return "STATUS_AUTHENTIFICATION_SUCCESS";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS:
                return "STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS";
            case SpassFingerprint.STATUS_TIMEOUT_FAILED:
                return "STATUS_TIMEOUT";
            case SpassFingerprint.STATUS_SENSOR_FAILED:
                return "STATUS_SENSOR_ERROR";
            case SpassFingerprint.STATUS_USER_CANCELLED:
                return "STATUS_USER_CANCELLED";
            case SpassFingerprint.STATUS_QUALITY_FAILED:
                return "STATUS_QUALITY_FAILED";
            case SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE:
                return "STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE";
            case SpassFingerprint.STATUS_BUTTON_PRESSED:
                return "STATUS_BUTTON_PRESSED";
            case SpassFingerprint.STATUS_OPERATION_DENIED:
                return "STATUS_OPERATION_DENIED";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_FAILED:
            default:
                return "STATUS_AUTHENTIFICATION_FAILED";
        }
    }

    private void startIdentityHander(boolean showCancel) {
        if(showCancel)
            cancelIdentify();
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startIdentify();
            }
        }, 100);

    }

    private void startIdentify() {
        if (!onReadyIdentify) {
            try {
                onReadyIdentify = true;
                if (mSpassFingerprint != null) {
                    setIdentifyIndex();
                    mSpassFingerprint.startIdentify(mIdentifyListener);
                }
                if (designatedFingers != null) {
                    log("Please identify finger to verify you with " + designatedFingers.toString() + " finger");
                } else {
                    log("Please identify finger to verify you");
                }
            } catch (SpassInvalidStateException ise) {
                onReadyIdentify = false;
                resetIdentifyIndex();
                if (ise.getType() == SpassInvalidStateException.STATUS_OPERATION_DENIED) {
                    sharedPrefUtils.showMsgDialog("" + (ise.getLocalizedMessage()), null);
                }
            } catch (IllegalStateException e) {
                onReadyIdentify = false;
                resetIdentifyIndex();
                startIdentityHander(true);
            }
        } else {

            sharedPrefUtils.showMsgDialog("The previous request in progress. Please Finish or Cancel first", new SendMessageCallback() {
                @Override
                public void sendMsg(String message) {
                    startIdentityHander(true);
                }
            });

        }
    }


    private void cancelIdentify() {
        if (onReadyIdentify) {
            try {
                if (mSpassFingerprint != null) {
                    mSpassFingerprint.cancelIdentify();
                }
                log("cancel Identify is called");
            } catch (IllegalStateException ise) {
                log(ise.getMessage());
            }
            onReadyIdentify = false;
            needRetryIdentify = false;
        } else {
            log("Please request Identity first");
        }
    }

    private void registerFingerprint() {
        if (!onReadyIdentify) {
            if (!onReadyEnroll) {
                onReadyEnroll = true;
                if (mSpassFingerprint != null) {
                    mSpassFingerprint.registerFinger(FPrintActivity.this, mRegisterListener);
                }
                log("Jump to the Enroll screen");
            } else {
                sharedPrefUtils.showMsgDialog("Please wait and try to register again", null);
            }
        } else {
//            "Please cancel Identify first"
            cancelIdentify();
        }
    }

    private SpassFingerprint.RegisterListener mRegisterListener = new SpassFingerprint.RegisterListener() {
        @Override
        public void onFinished() {
            onReadyEnroll = false;
            log("RegisterListener.onFinished()");
            sharedPrefUtils.startHomeActivity(PFADrawerActivity.class, null);

        }
    };

    private void setIdentifyIndex() {
        if (isFPIndexEnabled()) {
            if (mSpassFingerprint != null && designatedFingers != null) {
                mSpassFingerprint.setIntendedFingerprintIndex(designatedFingers);
            }
        }
    }

    private void resetIdentifyIndex() {
        designatedFingers = null;
    }


    private void resetFP() {
        unregisterBroadcastReceiver();
        resetAll();
    }

    private void log(String text) {
        sharedPrefUtils.printLog("FPrintActivity", "" + text);
    }


    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_RESET);
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_REMOVED);
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_ADDED);
        mContext.registerReceiver(mPassReceiver, filter);
    }

    private void unregisterBroadcastReceiver() {
        try {
            if (mContext != null) {
                mContext.unregisterReceiver(mPassReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        resetFP();
        super.onPause();
    }


}

package com.pfa.pfaapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_FP_ACTION;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.FP_LOGIN;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;

public class SplashActivity extends BaseActivity {
    private String currentVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getRefreshToken();

        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

        } catch (PackageManager.NameNotFoundException e) {
            sharedPrefUtils.printStackTrace(e);
        }

        sharedPrefUtils.applyFont(findViewById(R.id.logoTV), AppUtils.FONTS.HelveticaNeueMedium);

        // if open from whatsapp/or any other source link
        if (getIntent().getData() != null) {
            Uri uri = getIntent().getData();// this is the url
            List<String> segments = uri.getPathSegments();// this is the url segments
            sharedPrefUtils.printLog("uri 1=>", uri.toString());
            startLoginScreen(segments);
        } else {
            startMain();
        }
//        updateLocale();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getData() != null) {
            Uri uri = intent.getData();
            List<String> segments = uri.getPathSegments();

            sharedPrefUtils.printLog("uri 2=>", uri.toString());
            startLoginScreen(segments);
        }
    }


    private void getRefreshToken() {
        // get current fcm device token
        FirebaseInstanceId.getInstance().getToken();
    }

    private void startMain() {
        /*
         * This is the method for what activity to start:
         * Steps:
         * - Check Internet Connect
         * - Get current version of app available on playstore using GetVersionCode AsyncTask
         * - If the current app version and version of playstore is same (then check if user is already logged on or not) then ask user to update app from playstore
         * - If user logged in then start PFADrawerActivity otherwise start LoginActivity
         * */

        if (httpService.isNetworkDisconnected()) {
            startLoginScreen(null);
            return;
        }

        httpService.getListsData("menu/api_version", new HashMap<String, String>(), new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                //"account/api_version?type=softwareVersion"

                if (response != null) {

                    if (response.optBoolean("status")) {
                        JSONObject dataObject = response.optJSONObject("data");

                        Log.d("currentApiVersion" , "version from api= " + dataObject.optString("api_version"));
                        Log.d("currentApiVersion" , "version from playstore= " + currentVersion);

                        if (currentVersion != null && (currentVersion.equals(dataObject.optString("api_version")))) {
                            startLoginScreen(null);
                        } else {

                            sharedPrefUtils.showUpdateAppDialog(getPackageName());
                        }

                    } else {
                        sharedPrefUtils.showMsgDialog(response.optString("message_code"), null);
                    }

                } else {
                    sharedPrefUtils.showMsgDialog("No data received from server", new SendMessageCallback() {
                        @Override
                        public void sendMsg(String message) {
                            startMain();
                        }
                    });
                }
            }
        }, false);
    }


    private void startLoginScreen(final List<String> segments) {

        if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") == null) {
            sharedPrefUtils.startHomeActivity(FBOMainGridActivity.class, null);

        } else {
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    sharedPrefUtils.printLog("LoginType==>", "" + (sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, "")));
                    if (String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase(sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, ""))) {
                        sharedPrefUtils.startHomeActivity(FBOMainGridActivity.class, null);
                    } else if (String.valueOf(AppUtils.USER_LOGIN_TYPE.mto).equalsIgnoreCase((sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, "")))) {
                        sharedPrefUtils.startHomeActivity(WebAppActivity.class, null);
                    } else {

//                        If url of shared inspection is opened from whatsapp, then that inspection is loaded directly
                        if (segments != null) {
                            if (segments.size() > 2) {
                                String clientID = segments.get(segments.size() - 1);
                                String inspectionID = segments.get(segments.size() - 2);
//                                String url ="https://cell.pfa.gop.pk/dev/api/inspections/conducted_form/"+clientID+"/edit/"+inspectionID+"?conducted=1";

                                String url = "inspections/conducted_form/" + clientID + "/edit/" + inspectionID + "?conducted=1";

                                final Bundle bundle = new Bundle();
                                bundle.putString(EXTRA_URL_TO_CALL, url);

                                httpService.getListsData(url, new HashMap<String, String>(), new HttpResponseCallback() {
                                    @Override
                                    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                        bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                                        sharedPrefUtils.startNewActivity(LocalFormsActivity.class, bundle, true);
                                    }
                                }, false);
                            }
                        } else {
//                            If the device has fingerprint support (Samsung device) then it asks to login using fingerprint
                            if (isFPFeatureEnabled()) {
                                if ((getFPList() != null && getFPList().size() > 0)) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(EXTRA_FP_ACTION, FP_LOGIN);
                                    sharedPrefUtils.startHomeActivity(FPrintActivity.class, bundle);
                                } else {
                                    sharedPrefUtils.startHomeActivity(PFADrawerActivity.class, null);
                                }
                            } else {
//                                start the main (for staff, with drawer menu)
                                sharedPrefUtils.startHomeActivity(PFADrawerActivity.class, null);
                            }
                        }
                    }

                }
            }, 1000);
        }
    }
}

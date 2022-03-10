package com.pfa.pfaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.utils.AppUtils;
import com.rey.material.widget.SnackBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import eu.dkaratzas.android.inapp.update.Constants;
import eu.dkaratzas.android.inapp.update.InAppUpdateManager;
import eu.dkaratzas.android.inapp.update.InAppUpdateStatus;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_FP_ACTION;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.FP_LOGIN;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;

import androidx.appcompat.app.AlertDialog;

public class SplashActivity extends BaseActivity {
    private String currentVersion;
    private AppUpdateManager appUpdateManager;
    private boolean updateAvailable;
    public static String BaseUrl = null;

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
/*
        if (android.os.Build.VERSION.SDK_INT < 25){
            new AlertDialog.Builder(SplashActivity.this)
                    .setTitle("Old Android Version")
                    .setMessage("You are using the old Android version. \n" +
                            "please update your phone to at least Android 8 or above to continue using app.")

                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            finishAffinity();
                        }
                    })
//                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            appUpdateManager = AppUpdateManagerFactory.create(SplashActivity.this);

            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                Log.d("onCreateActv", "fbo here 16 ");
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    updateAvailable = true;
                    Log.d("onCreateActv", "fbo here 17 ");
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, 1001);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("onCreateActv", "fbo here 15 ");
                    updateAvailable = false;
                    if (getIntent().getData() != null) {
                        Log.d("onCreateActv", "fbo here 5 ");
                        Uri uri = getIntent().getData();// this is the url
                        List<String> segments = uri.getPathSegments();// this is the url segments
                        sharedPrefUtils.printLog("uri 1=>", uri.toString());
                        startLoginScreen(segments);
                    } else {

                        startMain();

                    }
                }
            });
        }*/

            if (!updateAvailable) {
                if (getIntent().getData() != null) {
                    Log.d("onCreateActv", "fbo here 4 ");
                    Uri uri = getIntent().getData();// this is the url
                    List<String> segments = uri.getPathSegments();// this is the url segments
                    sharedPrefUtils.printLog("uri 1=>", uri.toString());
                    startLoginScreen(segments);
                } else {
                    startMain();
                }
            }




        //        appUpdateManager.registerListener(installStateUpdatedListener);
//        updateLocale();
    }

    private InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED)
                showCompletedUpdate();
        }
    };

    private void showCompletedUpdate() {
        Toast.makeText(this, "Update Completed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode != RESULT_OK) {
                Log.d("", "Update flow failed! Result code: " + resultCode);
                finish();
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
            if (resultCode == RESULT_OK)
                startMain();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                updateAvailable = true;
//                try {
//                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, 1001);
//                } catch (IntentSender.SendIntentException e) {
//                    e.printStackTrace();
//                }
//            }
//            else {
////                Toast.makeText(this, "on Resume", Toast.LENGTH_SHORT).show();
//                if (getIntent().getData() != null) {
//                    Log.d("onCreateActv" , "fbo here 3 ");
//                    Uri uri = getIntent().getData();// this is the url
//                    List<String> segments = uri.getPathSegments();// this is the url segments
//                    sharedPrefUtils.printLog("uri 1=>", uri.toString());
//                    startLoginScreen(segments);
//                } else {
//                    startMain();
//                }
//            }
//        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!updateAvailable) {
            if (intent.getData() != null) {
                Log.d("onCreateActv", "fbo here 2 ");
                Uri uri = intent.getData();
                List<String> segments = uri.getPathSegments();

                sharedPrefUtils.printLog("uri 2=>", uri.toString());
                startLoginScreen(segments);
            }
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

        startLoginScreen(null);
    /*
        httpService.getListsData1("https://app.pfa.gop.pk/api/BaseURL/GetBaseURL?applicationName=cellpfagop", new HashMap<String, String>(), new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                //"account/api_version?type=softwareVersion"

                if (response != null) {

                    try {
                        if (response.getString("Message").equals("success")) {
                            JSONObject dataObject = response.optJSONObject("Result");

                            Log.d("currentApiVersion", "version from playstore= " + currentVersion);

//                            assert dataObject != null;
                            BaseUrl = dataObject.optString("Data");

                            Log.d("currentApiVersion", "version from api= " + BaseUrl);
//                            startLoginScreen(null);

                            if (!BaseUrl.isEmpty())
                                startLoginScreen(null);
                            else
                                sharedPrefUtils.showMsgDialog("No Base Url Found", null);

                        } else {
                            sharedPrefUtils.showMsgDialog("No Base Url Found", null);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    sharedPrefUtils.showMsgDialog("No data received from server", new SendMessageCallback() {
                        @Override
                        public void sendMsg(String message) {
//                            startMain();
                        }
                    });
                }
            }
        }, false);
    */
    }


    private void startLoginScreen(final List<String> segments) {

        if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") == null) {
            sharedPrefUtils.startHomeActivity(FBOMainGridActivity.class, null);
            Log.d("onCreateActv", "fbo here");

        } else {
            Log.d("onCreateActv", "fbo here 1");
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
                                        if (response != null)
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

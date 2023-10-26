package com.pfa.pfaapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.Result;
import com.pfa.pfaapp.customviews.PFASideMenuRB;
import com.pfa.pfaapp.fragments.CiTabbedFragment;
import com.pfa.pfaapp.fragments.DraftsFragment;
import com.pfa.pfaapp.fragments.LocalTabbedFragment;
import com.pfa.pfaapp.fragments.MenuFormFragment;
import com.pfa.pfaapp.fragments.MenuGridFragment;
import com.pfa.pfaapp.fragments.MenuListFragment;
import com.pfa.pfaapp.fragments.MenuMapFragment;
import com.pfa.pfaapp.fragments.ShareFragment;
import com.pfa.pfaapp.fragments.TabbedFragment;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.ListDataFetchedInterface;
import com.pfa.pfaapp.interfaces.RBClickCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.models.UserInfo;
import com.pfa.pfaapp.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.pfa.pfaapp.AppController.TAG;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_ACTIVITY_TITLE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_DETAIL_MENU;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_FILTERS_DATA;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_FORM_SECTION_LIST;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_FP_ACTION;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_SEARCH_FRAGMENT;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.FP_SIGNUP;
import static com.pfa.pfaapp.utils.AppConst.RC_ACTIVITY;
import static com.pfa.pfaapp.utils.AppConst.SP_DRAWER_MENU;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_DELETE_DB_DELETED;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;
import static com.pfa.pfaapp.utils.AppConst.SP_SECURITY_CODE;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;
import static com.pfa.pfaapp.utils.AppConst.SP_USER_INFO;

public class PFADrawerActivity extends BaseActivity implements HttpResponseCallback, RBClickCallback {

    Bundle mySaveInstanceState;
    private String currentTab = "";
    private int lastClicked = -1;
    private boolean isHomeAlreadyAdded = false;
    private static final String KEY_FRAG_FIRST = "firstFrag";

    DrawerLayout drawer;
    RadioGroup sideMenuOptionsRG;
    List<PFAMenuInfo> pfaMenuInfos;

    TextView userNameInitTV, loggedUserNameTV, userAddressTV , appVersionTV;
    public static TextView notificationCountTV;
    public AppCompatImageView imgAnnoucement;

    List<Fragment> menuItemFragments = new ArrayList<>();
    UserInfo userInfo;

    private static final int REQUEST_CODE_QR_SCAN = 101;
    private static boolean firstTime = true;
    private boolean tabClickable;
    private int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pfadrawer);

        drawer = findViewById(R.id.drawer_layout);

        setDownloadInspBtnClick();

        Log.d("onCreateActv", "PFADrawerActivity");

        dbQueriesUtil.deleteExpiredInspections();
        if (sharedPrefUtils.getSharedPrefValue(SP_IS_DELETE_DB_DELETED, "") == null) {
            updateConfigData();
        }


        filterIV = findViewById(R.id.filterIV);
        searchFilterFL = findViewById(R.id.searchFilterFL);

        onClickPanicBtn((ImageButton) findViewById(R.id.panicAlertBtn));
        filterCountTV = findViewById(R.id.filterCountTV);
        if (filterCountTV != null)
            sharedPrefUtils.applyFont(filterCountTV, AppUtils.FONTS.HelveticaNeue);

        sideMenuOptionsRG = findViewById(R.id.sideMenuOptionsRG);
        imgAnnoucement = findViewById(R.id.imgAnnoucement);
        loggedUserNameTV = findViewById(R.id.loggedUserNameTV);
        notificationCountTV = findViewById(R.id.notificationCountTV);
        sharedPrefUtils.applyFont(loggedUserNameTV, AppUtils.FONTS.HelveticaNeueMedium);
        sharedPrefUtils.applyFont(notificationCountTV, AppUtils.FONTS.HelveticaNeueMedium);

        userNameInitTV = findViewById(R.id.userNameInitTV);
        sharedPrefUtils.applyFont(userNameInitTV, AppUtils.FONTS.HelveticaNeueMedium);

        userAddressTV = findViewById(R.id.userAddressTV);
        appVersionTV = findViewById(R.id.appVersionTV);
        sharedPrefUtils.applyFont(userAddressTV, AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(appVersionTV, AppUtils.FONTS.HelveticaNeue);

        if (sharedPrefUtils.getSharedPrefValue(SP_USER_INFO, "") == null) {
            fetchUserInfo(new HttpResponseCallback() {
                @Override
                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                    Log.d("SideDrawerMenu", "SP_USER_INFO_null");
                    if (response != null)
                        PFADrawerActivity.this.onCompleteHttpResponse(response, requestUrl);
                }
            }, false);
            updateConfigData();
        } else {
            Log.d("SideDrawerMenu", "SP_USER_INFO_not_null");
            getSideMenu();
        }

        sharedPrefUtils.clearAllNotifications(-1);
        setFilterIVClick();

//        register broadcast receiver for showing the help activity [on locked screen]
        registerScreenReceiver();

//        MenuListFragment menuListFragment = null;
//        if (menuItemFragments.get(lastClicked) instanceof MenuListFragment) {
//            menuListFragment = (MenuListFragment) menuItemFragments.get(lastClicked);
//        }
//        if (menuListFragment!=null) {
//            filterCountTV.setText(menuListFragment.formFilteredData.size());
//            filterCountTV.setVisibility(View.VISIBLE);
//        }

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

        /*final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                        getConfirmation();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60000);*/

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) ==
                            PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) ==
                            PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.CALL_PHONE) ==
                            PackageManager.PERMISSION_DENIED) {
                //permission not enabled, request it
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO};
                //show popup to request permissions
                requestPermissions(permission, 11);
            }

        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) ==
                            PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) ==
                            PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.CALL_PHONE) ==
                            PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                            PackageManager.PERMISSION_DENIED) {
                //permission not enabled, request it
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CALL_PHONE, Manifest.permission.POST_NOTIFICATIONS};
                //show popup to request permissions
                requestPermissions(permission, 10);
            }
        } else {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.CALL_PHONE) ==
                            PackageManager.PERMISSION_DENIED) {
                //permission not enabled, request it
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE};
                //show popup to request permissions
                requestPermissions(permission, 10);
            }
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (!Environment.isExternalStorageManager())
//            {
//                Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION );
//                startActivity(permissionIntent);
//            }
//        }

        /*httpService.getListsData("announcementCount", null, new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                if (response!= null){

                }
            }
        } , false);*/

        imgAnnoucement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = PFASideMenuRB.itemId - 1;
                if (lastClicked == id) {
                    Log.d("NavDrawerClick", "last click and id same ");
                    drawer.closeDrawer(GravityCompat.START);
                    return;
                }

                addFragment(menuItemFragments.get(id), id == 0, pfaMenuInfos.get(id).getMenuItemName());
                Log.d("NavDrawerClick", "PFASideMenuRB item name = " + pfaMenuInfos.get(id).getMenuItemName());
                if (drawer != null)
                    drawer.closeDrawer(GravityCompat.START);

                lastClicked = id;

//                if (lastClicked == 0)
//                    hideNoDataImg();

                removeFilter();
                setTitle(pfaMenuInfos.get(id).getMenuItemName(), false);
                hideShowFilters();
            }
        });

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCMToken", "token = " + refreshedToken);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*if (requestCode == 11) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

                String permission_status = "Permission_granted";
                sharedPrefUtils.savePermissionStatus(permission_status);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        } else*/ if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
//            else
//                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();

            if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_DENIED && grantResults[2] == PackageManager.PERMISSION_DENIED)
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_LONG).show();
//            else
//                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_LONG).show();

            if (grantResults.length > 0 && grantResults[3] == PackageManager.PERMISSION_DENIED)
                Toast.makeText(this, "Call permission denied", Toast.LENGTH_LONG).show();
//            else
//                Toast.makeText(this, "Call permission denied", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Error in permissions", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void getConfirmation() {
        String pincode = "";
        String userId = "";
        if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {
            userId = sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID, "");
        }
        pincode = sharedPrefUtils.getSharedPrefValue(SP_SECURITY_CODE, "");
        httpService.getUserConfirmation(userId, pincode, new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                if (response != null) {
                    try {
                        String status = response.getString("status");
                        if (status == "false") {
                            if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {
                                sharedPrefUtils.logoutFromApp(httpService);
                                Toast.makeText(PFADrawerActivity.this, "Unauthentic User", Toast.LENGTH_SHORT).show();

                            } else {
                                sharedPrefUtils.startNewActivity(LoginActivity.class, null, false);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setFilterIVClick() {
        searchFilterFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MenuListFragment menuListFragment = null;
                MenuMapFragment menuMapFragment = null;
                if (menuItemFragments.get(lastClicked) instanceof TabbedFragment) {
                    Fragment fragment = ((TabbedFragment) menuItemFragments.get(lastClicked)).getCurrentFragment();

                    if (fragment instanceof MenuListFragment) {
                        menuListFragment = (MenuListFragment) fragment;
                    }
                } else if (menuItemFragments.get(lastClicked) instanceof CiTabbedFragment) {
                    Fragment fragment = ((CiTabbedFragment) menuItemFragments.get(lastClicked)).getCurrentFragment();

                    if (fragment instanceof MenuListFragment) {
                        menuListFragment = (MenuListFragment) fragment;
                    }
                } else if (menuItemFragments.get(lastClicked) instanceof MenuListFragment) {
                    menuListFragment = (MenuListFragment) menuItemFragments.get(lastClicked);
                } else if (menuItemFragments.get(lastClicked) instanceof MenuMapFragment) {
                    menuMapFragment = (MenuMapFragment) menuItemFragments.get(lastClicked);
                }

                if (menuListFragment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_ACTIVITY_TITLE, "Select List Filters");
                    if (filterCountTV.getText().toString().isEmpty()) {
                        if (menuListFragment.formFilteredData != null && menuListFragment.formFilteredData.size() > 0)
                            menuListFragment.formFilteredData.clear();
                    }
                    bundle.putSerializable(EXTRA_FILTERS_DATA, menuListFragment.formFilteredData);
                    bundle.putString(EXTRA_SEARCH_FRAGMENT, "list");
                    bundle.putSerializable(EXTRA_FORM_SECTION_LIST, (Serializable) menuListFragment.formSectionInfos);

                    sharedPrefUtils.startActivityForResult(PFADrawerActivity.this, PFAFiltersActivity.class, bundle, RC_ACTIVITY);
                } else if (menuMapFragment != null) {
                    Log.d("mapSearchData", "map pfa drawer instance");
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_ACTIVITY_TITLE, "Select List Filters");
                    if (filterCountTV.getText().toString().isEmpty()) {
                        if (menuMapFragment.formFilteredData != null && menuMapFragment.formFilteredData.size() > 0)
                            menuMapFragment.formFilteredData.clear();
                    }
                    bundle.putSerializable(EXTRA_FILTERS_DATA, menuMapFragment.formFilteredData);
                    bundle.putString(EXTRA_SEARCH_FRAGMENT, "map");
                    bundle.putSerializable(EXTRA_FORM_SECTION_LIST, (Serializable) menuMapFragment.formSectionInfos);

                    sharedPrefUtils.startActivityForResult(PFADrawerActivity.this, PFAFiltersActivity.class, bundle, RC_ACTIVITY);
                }
            }
        });

    }

    private void backPressedAction() {
        downloadInspImgBtn.setVisibility(View.GONE);

        lastClicked = 0;
        currentTab = pfaMenuInfos.get(lastClicked).getMenuItemName();
        removeFilter();

        actionOnViewChange();

        if (sideMenuOptionsRG != null) {
            ((RadioButton) sideMenuOptionsRG.getChildAt(lastClicked)).setChecked(true);
        }

        setTitle(currentTab, false);
    }

    @Override
    public void onBackPressed() {
        if (pfaMenuInfos == null || pfaMenuInfos.size() == 0) {
            finish();
            return;
        }
        backPressedAction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("imagePath", "onActivityResult = " + "PFADrawerActivity");

        if (lastClicked >= 0) {
            menuItemFragments.get(lastClicked).onActivityResult(requestCode, resultCode, data);
        }

        if (resultCode != Activity.RESULT_OK) {
            Log.d("LOGTAG", "COULD NOT GET A GOOD RESULT.");
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(PFADrawerActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            String businessId = result.substring(result.lastIndexOf("=") + 1);
            Log.d("LOGTAG", "Have scan result in your app activity :" + result);

            HashMap<String, String> reqParams = new HashMap<>();
            reqParams.put("LicenseNo", businessId);
            String suffix = "business_menu/business_url_from_license_id";
            httpService.checkExistingBusiness(suffix, reqParams, new HttpResponseCallback() {
                @Override
                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

                    if (response != null && response.optBoolean("status")) {

                        try {
//                            "business_menu/411/" + 409285 + "?menu_type=business_profile"
                            String suffix = response.getString("data");
                            httpService.getListsData(suffix, new HashMap<String, String>(), new HttpResponseCallback() {
                                @Override
                                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                    if (response != null) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString(EXTRA_URL_TO_CALL, suffix);
                                        bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                                        sharedPrefUtils.startNewActivity(PFADetailActivity.class, bundle, true);
                                    } else
                                        sharedPrefUtils.showMsgDialog("No Business Found", null);
                                }
                            }, true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        sharedPrefUtils.showMsgDialog("No Data Received from the Server", null);
                }
            }, true);

            /*Bundle bundle = new Bundle();
            bundle.putString(EXTRA_DETAIL_MENU, response.toString());
            baseActivity.sharedPrefUtils.startNewActivity(PFADetailActivity.class, bundle, true);*/

//            AlertDialog alertDialog = new AlertDialog.Builder(PFADrawerActivity.this).create();
//            alertDialog.setTitle("Scan result");
//            alertDialog.setMessage("Business Id = " + businessId);
//            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            alertDialog.show();

        }
    }

    private void getSideMenu() {
        Log.d("SideDrawerMenu", "getSideMenu");
        userInfo = sharedPrefUtils.getUserInfo();
        if (userInfo != null) {
            String fullNameStr = String.format(Locale.getDefault(), "%s %s", userInfo.getFirstname(), userInfo.getLastname());
            loggedUserNameTV.setText(sharedPrefUtils.capitalize(fullNameStr));
            userNameInitTV.setText(("" + userInfo.getFirstname().charAt(0)).toUpperCase());
            StringBuilder addressStr = new StringBuilder();
            if (userInfo.getAddress_obj().getSubtown_name() != null && (!userInfo.getAddress_obj().getSubtown_name().isEmpty())) {
                addressStr.append(userInfo.getAddress_obj().getSubtown_name());
            }
            if (userInfo.getAddress_obj().getTown_name() != null && (!userInfo.getAddress_obj().getTown_name().isEmpty())) {
                if (!addressStr.toString().isEmpty()) {
                    addressStr.append(",");
                }
                addressStr.append(userInfo.getAddress_obj().getTown_name());
            }

            if (userInfo.getAddress_obj().getDistrict_name() != null && (!userInfo.getAddress_obj().getDistrict_name().isEmpty())) {
                if (!addressStr.toString().isEmpty()) {
                    addressStr.append(",");
                }
                addressStr.append(userInfo.getAddress_obj().getDistrict_name());
            }

            userAddressTV.setText(addressStr.toString());
            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            appVersionTV.setText("version: " + versionName);
        }

        if (sharedPrefUtils.getDrawerMenu() == null) {
            Log.d("SideDrawerMenu", "getDrawerMenu_null");
            httpService.getSideMenu("" + sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID, ""), sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, ""), this);
        } else {
//            counter = 0;
            Log.d("SideDrawerMenu", "populateSideMenu");
//            if (counter == 0)
            populateSideMenu();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lastClicked >= 0) {
            menuItemFragments.get(lastClicked).onResume();
        }
        startLocation();
    }

    private void hideNoDataImg() {

        ///////////
        try {
            downloadInspImgBtn.setVisibility(View.GONE);
            getSupportFragmentManager().getFragments();
            if (lastClicked >= getSupportFragmentManager().getFragments().size()) {
                return;
            }

            if (getSupportFragmentManager().getFragments().get(lastClicked) instanceof MenuListFragment) {
                if (lastClicked < (getSupportFragmentManager().getFragments().size())) {
                    View view = getSupportFragmentManager().getFragments().get(lastClicked).getView().findViewById(R.id.sorry_iv);
                    if (view != null)
                        view.setVisibility(View.GONE);
                }
                if (lastClicked == 0) {
                    Log.d("refreshData", "refresh listener 1");
                    ((MenuListFragment) getSupportFragmentManager().getFragments().get(lastClicked)).onRefreshListener.onRefresh();
                }

            } else if (getSupportFragmentManager().getFragments().get(lastClicked) instanceof TabbedFragment) {
                Log.d("refreshData", "refresh listener 2");
                ((TabbedFragment) getSupportFragmentManager().getFragments().get(lastClicked)).refreshData();
            } else if (getSupportFragmentManager().getFragments().get(lastClicked) instanceof CiTabbedFragment) {
                ((CiTabbedFragment) getSupportFragmentManager().getFragments().get(lastClicked)).refreshData();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //////////
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

        if (response != null) {
            /*try {
                int tabClick = response.getInt("tabClickable");
                if (tabClick == 1){
                    Log.d("tabClickable123", "response = true");
                    tabClickable = true;
                    MenuListFragment.firstTimee = false;
                } else if (tabClick == 0){
                    Log.d("tabClickable123", "response = false)");
                    tabClickable = false;
                    MenuListFragment.firstTimee = false;
                }
            } catch (JSONException e) {
                Log.d("tabClickable123", "response = exception)");
                e.printStackTrace();
            }*/
            /*if (response.optBoolean("tabClickable")) {
                Log.d("tabClickable123", "response = true");
                tabClickable = true;
                MenuListFragment.firstTimee = false;
            }
            else {
                Log.d("tabClickable123", "response = false)");
                tabClickable = false;
                MenuListFragment.firstTimee = true;
            }*/
            if (response.optBoolean("status")) {

                if (requestUrl.contains("/account/users/")) {
                    sharedPrefUtils.saveSharedPrefValue(SP_USER_INFO, response.optJSONObject("data").toString());

                    getSideMenu();
                    Log.d("SideDrawerMenu", "getSideMenu requestUrl.contains users");

                } else if (requestUrl.contains("/api/menu/")) {
                    try {
                        JSONObject jsonObject = response.getJSONObject("data");

                        JSONArray formJSONArray = jsonObject.getJSONArray("menus");

                        sharedPrefUtils.saveSharedPrefValue(SP_DRAWER_MENU, formJSONArray.toString());
                        Log.d("SideDrawerMenu", "populateSideMenu requestUrl.contains api");
                        populateSideMenu();

                    } catch (JSONException e) {
                        sharedPrefUtils.printStackTrace(e);
                    }
                }
            }
        }
    }

    private void populateSideMenu() {
        pfaMenuInfos = sharedPrefUtils.getDrawerMenu();

        new PFASideMenuRB(PFADrawerActivity.this, sideMenuOptionsRG, pfaMenuInfos, PFADrawerActivity.this);

        if (pfaMenuInfos != null && pfaMenuInfos.size() > 0) {
            for (final PFAMenuInfo pfaMenuInfo : pfaMenuInfos) {

                Fragment menuItemFragment;
                switch (pfaMenuInfo.getMenuType()) {
                    case "list":
//                        if (!pfaMenuInfo.getMenuItemName().equals("Enforcements")) {
                        menuItemFragment = MenuListFragment.newInstance(pfaMenuInfo, true, true, true, null);
                        ((MenuListFragment) menuItemFragment).setFetchDataInterface(new ListDataFetchedInterface() {
                            @Override
                            public void listDataFetched() {
                                hideShowFilters();
                            }
                        });
                        Log.d("SideMenuType", "MenuListFragment enforcement");
                        Log.d("multipleRequestFrag", "MenuListFragment enforcement123");
                        firstTime = false;
                        counter++;
                        break;
//                        }
                    case "menu":
                        menuItemFragment = TabbedFragment.newInstance(pfaMenuInfo, true);
                        Log.d("SideMenuType", "TabbedFragment");
                        break;
                    case "ci_menu":
                        menuItemFragment = CiTabbedFragment.newInstance(pfaMenuInfo, true);
                        Log.d("SideMenuType", "CiTabbedFragment");
                        break;
                    case "localMenu":
                        menuItemFragment = LocalTabbedFragment.newInstance(pfaMenuInfo, true);
                        Log.d("SideMenuType", "LocalTabbedFragment");
                        break;
                    case "googlemap":
                        menuItemFragment = MenuMapFragment.newInstance(pfaMenuInfo, null);
                        ((MenuMapFragment) menuItemFragment).setFetchDataInterface(new ListDataFetchedInterface() {
                            @Override
                            public void listDataFetched() {
                                hideShowFilters();
                            }
                        });
                        Log.d("SideMenuType", "MenuMapFragment");
                        break;
                    case "dashboard":
                    case "grid":
                        menuItemFragment = MenuGridFragment.newInstance(pfaMenuInfo);
                        Log.d("SideMenuType", "MenuGridFragment");
                        break;
                    case "logout":
                        Log.d("SideMenuType", "MenuListFragmentlohouyu");
//                        AlertDialog.Builder builder = new AlertDialog.Builder(PFADrawerActivity.this);
//                        builder.setTitle("Log out");
//
//                        String[] options = {"Logout","Logout from all devices"};
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                switch (which) {
//                                    case 0:
//                                        if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {
//                                            sharedPrefUtils.logoutFromApp(httpService);
//                                        } else {
//                                            sharedPrefUtils.startNewActivity(LoginActivity.class, null, false);
//                                        }
//                                        break;
//                                    case 1:
//
//                                        if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {
//                                            sharedPrefUtils.logoutFromAllDevices(httpService);
//                                        } else {
//                                            sharedPrefUtils.startNewActivity(LoginActivity.class, null, false);
//                                        }
//                                        break;
//                                }
//                            }
//                        });
//
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
//                        break;

                    case "fingerPrint":
                        menuItemFragment = new Fragment();
                        Log.d("SideMenuType", "ndew MenuListFragment");
                        break;
                    case "share":
                        menuItemFragment = ShareFragment.newInstance(pfaMenuInfo);
                        Log.d("SideMenuType", "ShareFragment");
                        break;

                    case "draft":
                        menuItemFragment = DraftsFragment.newInstance(pfaMenuInfo, new SendMessageCallback() {
                            @Override
                            public void sendMsg(String message) {
                            }
                        });
                        Log.d("SideMenuType", "DraftsFragment");
                        break;
                    default:
                        menuItemFragment = MenuFormFragment.newInstance(pfaMenuInfo, null);
                        Log.d("SideMenuType", "MenuFormFragment");
                        break;
                }

                if (menuItemFragment != null)
                    menuItemFragments.add(menuItemFragment);
            }
            lastClicked = 0;

            addFragment(menuItemFragments.get(0), true, pfaMenuInfos.get(0).getMenuItemName());
        }
    }

    @Override
    public void onClickRB(View view) {

        Log.d("NavDrawerClick", "PFASideMenuRB after click tag = " + view.getTag());
        Log.d("NavDrawerClick", "PFASideMenuRB after click id = " + view.getId());


        if (view.getTag().toString().equalsIgnoreCase("logout")) {
//            sharedPrefUtils.logoutFromApp(httpService);
            AlertDialog.Builder builder = new AlertDialog.Builder(PFADrawerActivity.this);
//                builder.setTitle("Log out");

            String[] options = {"Log Out", "Log Out from All Devices"};
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {
                                sharedPrefUtils.logoutFromApp(httpService);
                            } else {
                                sharedPrefUtils.startNewActivity(LoginActivity.class, null, false);
                            }
                            break;
                        case 1:

                            if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {
                                sharedPrefUtils.logoutFromAllDevices(httpService);
                            } else {
                                sharedPrefUtils.startNewActivity(LoginActivity.class, null, false);
                            }
                            break;
                    }
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        if (view.getTag().toString().equalsIgnoreCase("Scan Business By QR")) {
//            CodeScannerView scannerView = new CodeScannerView(PFADrawerActivity.this);
//            CodeScanner mCodeScanner = new CodeScanner(this, scannerView);
            /*mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PFADrawerActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });*/

//            mCodeScanner.startPreview();
            Log.d("NavDrawerClick", "Scan Business By QR after click");
            Intent i = new Intent(PFADrawerActivity.this, QrCodeActivity.class);
            startActivityForResult(i, REQUEST_CODE_QR_SCAN);
            return;
        }

        if (view.getTag().toString().equalsIgnoreCase("Privacy Policy")) {
            /*String url = "https://pfa.gop.pk/privacy-policy/";
            Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
            defaultBrowser.setData(Uri.parse(url));
            startActivity(defaultBrowser);*/
            Intent intent = new Intent(this, PrivacyPolicy.class);
            startActivity(intent);
            this.overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);
            return;
        }

        if (view.getTag().toString().equalsIgnoreCase("Add Fingerprint")) {
            drawer.closeDrawer(GravityCompat.START);

            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_FP_ACTION, FP_SIGNUP);
            sharedPrefUtils.startNewActivity(FPrintActivity.class, bundle, false);
            return;
        }


        int id = view.getId();
        MenuListFragment.firstTimee = false;
        /*MenuListFragment.firstTimee = false;
        if (pfaMenuInfos.get(id).getAPI_URL().contains("enforcementsListing_tabs")){
            Log.d("SideMenuType1", "PFASideMenuRB view= " + view.getId());
            Log.d("SideMenuType1", "PFASideMenuRB url= " + pfaMenuInfos.get(id).getAPI_URL());
            Fragment menuItemFragment;
            menuItemFragment = MenuListFragment.newInstance(pfaMenuInfos.get(id), true, true, true, null);
            ((MenuListFragment) menuItemFragment).setFetchDataInterface(new ListDataFetchedInterface() {
                @Override
                public void listDataFetched() {
                    hideShowFilters();
                }
            });
            if (menuItemFragment != null)
                menuItemFragments.add(menuItemFragment);

            lastClicked = 0;

            addFragment(menuItemFragments.get(0), true, pfaMenuInfos.get(0).getMenuItemName());
            return;
        }

*/


        /*if (view.getTag().equals("Inspections")){
            TabbedFragment.onResumeTabbedFragment();
            return;
        }*/

//        if (view.getTag().toString().equalsIgnoreCase("Contact Support")) {
//            drawer.closeDrawer(GravityCompat.START);
//
//            String phone = pfaMenuInfos.get(id).getMenuTypeLink();
//
//            Intent intent = new Intent(Intent.ACTION_DIAL);
//            intent.setData(Uri.parse("tel:"+phone));
//            startActivity(intent);
//
//            return;
//        }


        if (lastClicked == id) {
            Log.d("NavDrawerClick", "last click and id same ");
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        addFragment(menuItemFragments.get(id), id == 0, pfaMenuInfos.get(id).getMenuItemName());
        Log.d("NavDrawerClick", "PFASideMenuRB item name = " + pfaMenuInfos.get(id).getMenuItemName());
        if (drawer != null)
            drawer.closeDrawer(GravityCompat.START);

        lastClicked = id;

        if (lastClicked == 0)
            hideNoDataImg();

        removeFilter();
        setTitle(pfaMenuInfos.get(id).getMenuItemName(), false);
        hideShowFilters();

    }

    @Override
    public void onClickCallUrl(String url) {

    }

    private void hideShowFilters() {
        Log.d("hideShowFilters", "hideShowFilters = 1");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("hideShowFilters", "hideShowFilters = 2");
                searchFilterFL.setVisibility(View.GONE);
                if (menuItemFragments.get(lastClicked) instanceof MenuListFragment) {
                    Log.d("hideShowFilters", "hideShowFilters = 3");
                    if (((MenuListFragment) menuItemFragments.get(lastClicked)).showFilter) {
                        Log.d("hideShowFilters", "hideShowFilters = 4");
                        searchFilterFL.setVisibility(View.VISIBLE);
                    }
                }
                if (menuItemFragments.get(lastClicked) instanceof MenuMapFragment) {
                    Log.d("hideShowFilters", "hideShowFilters = 3");
                    if (((MenuMapFragment) menuItemFragments.get(lastClicked)).showFilter) {
                        Log.d("hideShowFilters", "hideShowFilters = 4");
                        searchFilterFL.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, 200);
    }

    private void actionOnViewChange() {
        if (drawer != null)
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (backStackCount == 1) {
            sharedPrefUtils.showExitDialog();
            return;
        }

        if (backStackCount > 1) {
            hideNoDataImg();
            getSupportFragmentManager().popBackStack();
            lastClicked = 0;
        }
    }

    public void addFragment(Fragment frag, boolean isHome, String fragmentTitleStr) {
        Log.d("NavDrawerClick", "PFASideMenuRB item name = addFragment()");
//        if (menuItemFragments.get(lastClicked) == frag) {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            actionOnViewChange();
        }

        if (mySaveInstanceState == null) {
            if (isHome) {
                currentTab = fragmentTitleStr;
                if (isHomeAlreadyAdded)
                    return;
                else {
                    isHomeAlreadyAdded = true;
                }
            }

            if (isHome || (!fragmentTitleStr.equals(currentTab))) {
                currentTab = fragmentTitleStr;
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setReorderingAllowed(true);
                transaction.add(getFrameLayoutId(isHome), frag);
                transaction.addToBackStack(getSupportFragmentManager().getBackStackEntryCount() == 0 ? KEY_FRAG_FIRST : currentTab).commit();
//                transaction.addToBackStack(getSupportFragmentManager().getBackStackEntryCount() == 0 ? KEY_FRAG_FIRST : currentTab).commitAllowingStateLoss();
            }
        }

        setTitle(currentTab, false);
//        }
    }

    public int getFrameLayoutId(boolean isHomeScreen) {
        if (isHomeScreen) {
            return R.id.mainContentFL;
        } else {
            return R.id.main_screen_fragmentsFL;
        }
    }

    public void onClickMenuImgBtn(View view) {
        hideKeyBoard();
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();
    }

    public void onClickNotifMsgTV(View view) {
        sharedPrefUtils.startNewActivity(NotificationActivity.class, null, false);
    }


}

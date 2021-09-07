package com.pfa.pfaapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.pfa.pfaapp.dbutils.DBQueriesUtil;
import com.pfa.pfaapp.helper.LocaleHelper;
import com.pfa.pfaapp.httputils.ConfigHttpUtils;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.locationutils.LocationUpdatesService;
import com.pfa.pfaapp.locationutils.LocationUtils;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.utils.AddInspectionUtils;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.CustomDateUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.pfa.pfaapp.utils.AppConst.APP_LATITUDE;
import static com.pfa.pfaapp.utils.AppConst.APP_LONGITUDE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.SP_APP_AUTH_TOKEN;
import static com.pfa.pfaapp.utils.AppConst.SP_APP_LANG;
import static com.pfa.pfaapp.utils.AppConst.SP_CNIC;
import static com.pfa.pfaapp.utils.AppConst.SP_FCM_ID;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;
import static com.pfa.pfaapp.utils.AppConst.SP_MAIN_MENU;
import static com.pfa.pfaapp.utils.AppConst.SP_PHONE_NUM;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;
import static com.pfa.pfaapp.utils.AppConst.SP_USER_INFO;

//import static com.pfa.pfaapp.utils.AppConst.SP_SECURITY_CODE;

public class BaseActivity extends AppCompatActivity {

    public SharedPrefUtils sharedPrefUtils;
    public CustomDateUtils customDateUtils;
    public HttpService httpService;
    private LocationUpdatesService locationUpdatesService;
    public DBQueriesUtil dbQueriesUtil;
    OutputStream opstream = null;

    /* Filter Views */
    public ImageView filterIV;
    public RelativeLayout searchFilterFL;
    public TextView filterCountTV;
    public AddInspectionUtils addInspectionUtils;
    public ImageButton downloadInspImgBtn;

    byte FONT_TYPE;
    private static BluetoothSocket btsocket;
    private static OutputStream btoutputstream;

    private String app_token;
    private Spass mSpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefUtils = new SharedPrefUtils(this);
        app_token = "PFAqVOIYfqs:PUT1bGDNXx-8JELLbelRQcb9EN9srz";
       sharedPrefUtils.saveSharedPrefValue(SP_APP_AUTH_TOKEN,app_token);
        updateLocale();

        httpService = new HttpService(this);
        customDateUtils = new CustomDateUtils();
        dbQueriesUtil = new DBQueriesUtil(this);


        mSpass = new Spass();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        if (findViewById(R.id.headerPanicImgBtn) != null) {
            onClickPanicBtn((ImageButton) findViewById(R.id.headerPanicImgBtn));
        }
    }

    public void removeFilter() {
        if (filterCountTV != null) {
            filterCountTV.setText("");
            filterCountTV.setVisibility(View.GONE);
        }
    }

    public void fetchUserInfo(final HttpResponseCallback callback, boolean showProgress) {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sharedPrefUtils.saveSharedPrefValue(SP_FCM_ID, "" + refreshedToken);
        httpService.getUserInfo("" + sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID, ""), "" + refreshedToken, new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                if (response != null) {
                    if (response.optBoolean("status")) {

                        if (requestUrl.contains("/account/users/")) {
                            sharedPrefUtils.saveSharedPrefValue(SP_USER_INFO, response.optJSONObject("data").toString());

                            if (callback != null)
                                callback.onCompleteHttpResponse(response, requestUrl);
                        }
                    }
                }
            }
        }, showProgress);
    }

    public void updateConfigData() {
        new ConfigHttpUtils(this).fetchConfigData();
    }

    public void setupWindowAnimations(ViewGroup viewGroup) {
        if (viewGroup != null) {
            sharedPrefUtils.printLog("setupWindowAnimations viewGroup", "setupWindowAnimations viewGroup");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideKeyBoard();
        return true;
    }

    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        assert imm != null;
        {
            View currentView = getCurrentFocus();
            if (currentView != null) {
                imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
                currentView.clearFocus();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        try {
            if (getCurrentFocus() instanceof EditText) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN &&
                        !getLocationOnScreen((EditText) getCurrentFocus()).contains(x, y)) {
                    InputMethodManager input = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert input != null;
                    input.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            sharedPrefUtils.printStackTrace(e);
        }
        return super.dispatchTouchEvent(ev);
    }

    private Rect getLocationOnScreen(EditText mEditText) {
        Rect mRect = new Rect();
        int[] location = new int[2];

        mEditText.getLocationOnScreen(location);

        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + mEditText.getWidth();
        mRect.bottom = location[1] + mEditText.getHeight();

        return mRect;
    }

    public void stopLocation() {
        if (LocationUtils.isLocationServiceEnabled(this)) {

            if (locationUpdatesService != null) {
                locationUpdatesService.stopLocationService();
                if (handler != null)
                    handler.removeCallbacks(locationRunable);
            }
        }
    }

    public void startLocation() {
        if (!LocationUtils.isLocationServiceEnabled(this)) {
            LocationUtils.showSettingsAlert(this);
        } else {
            startLocationService();
        }
    }

    Handler handler = new Handler();
    Runnable locationRunable = new Runnable() {
        @Override
        public void run() {
            startLocationService();
        }
    };

    private void startLocationService() {
        if (LocationUtils.isLocationServiceEnabled(this)) {
            locationUpdatesService = new LocationUpdatesService();
            locationUpdatesService.requestLocationUpdates(this);
            handler.removeCallbacks(locationRunable);
        } else {
            handler.postDelayed(locationRunable, 1000);
        }
    }

    TextView titleTV;
    RelativeLayout ttl_bar;
    ImageButton backImgBtn;

    public void setTitle(String title, boolean showBackBtn) {

        titleTV = findViewById(R.id.titleTV);

        if (findViewById(R.id.ttl_bar) instanceof RelativeLayout)
            ttl_bar = findViewById(R.id.ttl_bar);

        sharedPrefUtils.applyFont(titleTV, AppUtils.FONTS.HelveticaNeueBold);

        sharedPrefUtils.applyFont(findViewById(R.id.clearFilterBtn), AppUtils.FONTS.HelveticaNeue);
//        sharedPrefUtils.applyFont(findViewById(R.id.PrintIconView), AppUtils.FONTS.HelveticaNeue);

        if (showBackBtn) {
            titleTV.setPadding(0, 0, 0, 0);
            backImgBtn = findViewById(R.id.backImgBtn);

            if (backImgBtn != null) {
                backImgBtn.setVisibility(View.VISIBLE);
                backImgBtn.setImageResource(sharedPrefUtils.isEnglishLang() ? R.mipmap.left_arrow_white : R.mipmap.ur_left_arrow_white);
            }

            titleTV.setCompoundDrawables(null, null, null, null);
            titleTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (title != null && (!title.trim().isEmpty())) {
            titleTV.setText(title);

        } else {
            if (!showBackBtn) {
                if (sharedPrefUtils.isEnglishLang()) {
                    titleTV.setCompoundDrawables(getResources().getDrawable(R.mipmap.title_logo), null, null, null);
                    titleTV.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.title_logo, 0, 0, 0);
                } else {
                    titleTV.setCompoundDrawables(null, null, getResources().getDrawable(R.mipmap.title_logo), null);
                    titleTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.title_logo, 0);
                }

            }

            if (ttl_bar != null) {
                if (showBackBtn) {
                    ttl_bar.setBackgroundColor(getResources().getColor(R.color.white));
                } else {
                    ttl_bar.setBackgroundColor(getResources().getColor(R.color.top_bar_default_color));
                    if (findViewById(R.id.horizontalSep) != null)
                        findViewById(R.id.horizontalSep).setVisibility(View.VISIBLE);
                }
            }
            if (backImgBtn != null)
                backImgBtn.setImageResource(sharedPrefUtils.isEnglishLang() ? R.mipmap.left_arrow_grey : R.mipmap.ur_left_arrow_grey);
        }
    }


    public void onClickBackImgBtn(View view) {
        onBackPressed();
    }

    public void setDownloadInspBtnClick() {
        downloadInspImgBtn = findViewById(R.id.downloadInspImgBtn);

        downloadInspImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addInspectionUtils != null)
                    addInspectionUtils.onClickDownloadAsDraftBtn(null);
            }
        });
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED)
            ) {
                //Can add more as per requirement
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE
                                , Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.SYSTEM_ALERT_WINDOW},
                        123);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void checkManageStorage(){
        if (!Environment.isExternalStorageManager())
        {
            Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION );
            startActivity(permissionIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 123 && grantResults.length>0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                checkManageStorage();
            }
        }
    }

    SpassFingerprint mSpassFingerprint;

//    private void initFP() {
//        try {
//            mSpass.initialize(this);
//        } catch (SsdkUnsupportedException e) {
//            sharedPrefUtils.printLog("SsdkUnsupportedException", "Exception: " + e);
//
//        } catch (UnsupportedOperationException e) {
//            sharedPrefUtils.printLog("UnsupportedOperationException", "Fingerprint Service is not supported in the device");
//        }
//    }

    public boolean isFPFeatureEnabled() {
        //                try {
//            String manufacturer = android.os.Build.MANUFACTURER;
//            sharedPrefUtils.printLog("Manufacturer", "" + manufacturer);
//            if (manufacturer.equalsIgnoreCase("samsung")) {
//                initFP();
//                isFeatureEnabled = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);
//                if (isFeatureEnabled) {
//                    mSpassFingerprint = new SpassFingerprint(this);
////           sharedPrefUtils.printLog("isFPFeatureEnabled", "Fingerprint Service is supported in the device.");
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return false;
    }

    public boolean isFPIndexEnabled() {
        return mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_FINGER_INDEX);

    }

    public SparseArray getFPList() {
        SparseArray mList = null;
        if (mSpassFingerprint != null) {
            mList = mSpassFingerprint.getRegisteredFingerprintName();
        }

        return mList;
    }

    void updateLocale() {
        AppController.getInstance().updateLocale();

        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(getBaseContext());
        String lang = sharedPrefUtils.getSharedPrefValue(SP_APP_LANG, "");
        if (lang == null || sharedPrefUtils.isEnglishLang()) {
            lang = String.valueOf(AppUtils.APP_LANGUAGE.en);
        }

        LocaleHelper.setLocale(this, lang);
    }


    public void setVerification(final Bundle bundle, PFAMenuInfo pfaMenuInfo) {
        setNotifChannel();
        sharedPrefUtils.saveSharedPrefValue(SP_STAFF_ID, "" + bundle.getInt(SP_STAFF_ID));
//        sharedPrefUtils.saveSharedPrefValue(SP_SECURITY_CODE, bundle.getString(SP_SECURITY_CODE));
        sharedPrefUtils.saveSharedPrefValue(SP_CNIC, bundle.getString(SP_CNIC));
        sharedPrefUtils.saveSharedPrefValue(SP_PHONE_NUM, bundle.getString(SP_PHONE_NUM));
        sharedPrefUtils.saveSharedPrefValue(SP_LOGIN_TYPE, bundle.getString(SP_LOGIN_TYPE));
        sharedPrefUtils.saveSharedPrefValue(SP_IS_LOGED_IN, SP_IS_LOGED_IN);
        if (String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase((bundle.getString(SP_LOGIN_TYPE)))) {

            sharedPrefUtils.removeSharedPrefValue(SP_MAIN_MENU);
            if (pfaMenuInfo != null && pfaMenuInfo.getAPI_URL() != null && (!pfaMenuInfo.getAPI_URL().isEmpty())) {

                httpService.getListsData(pfaMenuInfo.getAPI_URL() + "/" + bundle.getInt(SP_STAFF_ID), new HashMap<String, String>(), new HttpResponseCallback() {
                    @Override
                    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

                        bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                        bundle.putBoolean("extraFetchConfig", true);
                        //////////// Fetch User Information
                        if (sharedPrefUtils.getSharedPrefValue(SP_USER_INFO, "") == null) {
                            fetchUserInfo(new HttpResponseCallback() {
                                @Override
                                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
//                                            showAddNewFragment();
                                    sharedPrefUtils.startHomeActivity(PFAAddNewActivity.class, bundle);
                                    finish();
                                }
                            }, true);

                        } else {
//                                    showAddNewFragment();
                            sharedPrefUtils.startHomeActivity(PFAAddNewActivity.class, bundle);
                            finish();
                        }
                        ///////// End fetch user information

                    }
                }, true);
            } else {
                sharedPrefUtils.startHomeActivity(FBOMainGridActivity.class, bundle);
                finish();
            }
        } else if (String.valueOf(AppUtils.USER_LOGIN_TYPE.mto).equalsIgnoreCase((bundle.getString(SP_LOGIN_TYPE)))) {
            sharedPrefUtils.startHomeActivity(WebAppActivity.class, null);
        } else {
            sharedPrefUtils.startHomeActivity(PFADrawerActivity.class, bundle);
            finish();
        }
    }

    public void setNotifChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
    }


    public void registerScreenReceiver() {
//        try {
//            Intent myIntent = new Intent(this, RegisterReceiverService.class);
//
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                startForegroundService(myIntent);
//            } else {
//                startService(myIntent);
//            }
//            startService(myIntent);
//
//        } catch (Exception e) {
//            sharedPrefUtils.printStackTrace(e);
//        }
    }


    public void onClickPanicBtn(ImageButton panicAlertBtn) {
        if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null && (!String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase(sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, "")))) {
            if (panicAlertBtn != null) {
                panicAlertBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendEmergencyMsgs();

                    }
                });
            }
        } else {
            if (panicAlertBtn != null)
                panicAlertBtn.setVisibility(View.GONE);
        }


    }

    public void sendEmergencyMsgs() {

        sharedPrefUtils.showTwoBtnsMsgDialog("Are you sure you want to send emergency message?", new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {
                if (message.isEmpty()) {
                    HashMap<String, String> reqParams = new HashMap<>();

                    if (sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID, "") != null && sharedPrefUtils.getSharedPrefValue(APP_LATITUDE, "") != null) {
                        reqParams.put(SP_STAFF_ID, sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID, ""));
                        reqParams.put("HTTP_CURRENT_LAT", sharedPrefUtils.getSharedPrefValue(APP_LATITUDE, ""));
                        reqParams.put("HTTP_CURRENT_LNG", sharedPrefUtils.getSharedPrefValue(APP_LONGITUDE, ""));

                        HttpService httpService = new HttpService(getApplicationContext());
                        httpService.sendEmergencyMessage(reqParams, null);
                    }
                }

            }
        });

    }
    private void printPhoto2(String receipt_logo) {

        Glide.with(this)
                .asBitmap()
                .load(receipt_logo)
                .override(60,60)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            byte[] command = Utils.decodeBitmap(resource);
                            opstream.write(PrinterCommands.ESC_ALIGN_LEFT);
                            printText(command);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {


                    }
                });
    }
    private void printPhoto1(String barcode_url) {

              Glide.with(this)
                    .asBitmap()
                    .load(barcode_url)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    byte[] command = Utils.decodeBitmap(resource);
                                    opstream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                                    printText(command);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });

    }
    private void printString(String shareHtmlStr) {
        try {
                opstream.write(PrinterCommands.ESC_ALIGN_LEFT);
                btoutputstream.write(shareHtmlStr.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the string isn't exists");
        }
    }


        //print photo
    public void printPhoto(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    img);
            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                opstream.write(PrinterCommands.ESC_ALIGN_LEFT);
                printText1(command);
            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }

    }
    private void printQR(String name) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(name, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            byte[] command = Utils.decodeBitmap(bitmap);
            opstream.write(PrinterCommands.ESC_ALIGN_RIGHT);
            opstream.write(command);

        } catch (IOException | WriterException e) {
            e.printStackTrace();
        }
    }
    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            opstream.write(msg);
//            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        //print byte[]
    private void printText1(byte[] msg) {
        try {
            // Print normal text
            opstream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //print new line
    private void printNewLine() {
        try {
            opstream.write(PrinterCommands.FEED_LINE); 
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public void Connect(final String Receipt_logo, final String barcode_url, final String printHtmlStr) {
//        if (btsocket == null) {
//            Intent BTIntent = new Intent(getApplicationContext(), BTDeviceList.class);
//            this.startActivityForResult(BTIntent, BTDeviceList.REQUEST_CONNECT_BT);
//        } else {
//
//            OutputStream opstream = null;
//            try {
//                opstream = btsocket.getOutputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            btoutputstream = opstream;
//            print_bt(Receipt_logo, barcode_url, printHtmlStr);
//
//        }
//    }
//    private void print_bt(String receipt_logo, String barcode_url, final String printHtmlStr) {
//        try {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            btoutputstream = btsocket.getOutputStream();
//
//            byte[] printformat = { 0x1B, 0x21, FONT_TYPE };
//
//                printPhoto2(receipt_logo);
//                printPhoto1(barcode_url);
////                printPhoto(R.drawable.comp);
//            final OutputStream finalOpstream = opstream;
//            new Timer().schedule(
//                    new TimerTask(){
//
//                        @Override
//                        public void run(){
//
//                            try {
//                                finalOpstream.write(new byte[]{PrinterCommands.GS,'L',(byte)20,(byte) 0});
//                                btoutputstream.write(printHtmlStr.getBytes());
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }, 500);
//            btoutputstream.write(0x0D);
//            btoutputstream.write(0x0D);
//            btoutputstream.write(0x0D);
//            btoutputstream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void Connect(final String printHtmlStr) {
        if (btsocket == null) {
            Intent BTIntent = new Intent(getApplicationContext(), BTDeviceList.class);
            startActivityForResult(BTIntent, BTDeviceList.REQUEST_CONNECT_BT);
        } else {
            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                try {
//                    btoutputstream = opstream;

                    Thread.sleep(2000);
                   } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                btoutputstream = btsocket.getOutputStream();
                byte[] printformat = {0x1B, 0x21, FONT_TYPE};

                btoutputstream.write(printformat);

//                printPhoto2(receipt_logo);
//                printPhoto1(barcode_url);
//                printPhoto(R.drawable.comp);
                final OutputStream finalOpstream = opstream;
                new Timer().schedule(
                    new TimerTask(){

                        @Override
                        public void run(){

                            try {
                                finalOpstream.write(new byte[]{PrinterCommands.GS,'L',(byte)20,(byte) 0});
                                btoutputstream.write(printHtmlStr.getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }, 500);

                btoutputstream.write(0x0D);
                btoutputstream.write(0x0D);
                btoutputstream.write(0x0D);
                btoutputstream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    protected void onStop() {
        try {
            if (btsocket != null) {
                btoutputstream.close();
                btsocket.close();
                btsocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("imagePath" , "onActivityResult = " + "BaseActivity");

        if (requestCode == 253){
            Toast.makeText(locationUpdatesService, "Permission files granted", Toast.LENGTH_LONG).show();
        }

        try {
            btsocket = BTDeviceList.getSocket();
            if (btsocket != null) {
                try {
                    opstream = btsocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                    Toast.makeText(this, "Device is ready for Printing", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void TestMe() {


    }
}



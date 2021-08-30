package com.pfa.pfaapp.emergency;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.models.UserInfo;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import java.util.HashMap;

import static com.pfa.pfaapp.utils.AppConst.APP_LATITUDE;
import static com.pfa.pfaapp.utils.AppConst.APP_LONGITUDE;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;

public class LockHelpActivity extends BaseActivity implements View.OnClickListener {


    SharedPrefUtils sharedPrefUtils;
    String myName = "", phoneNumber = "", contactName = "";

    TextView emergencyContactInfoTv, helptDetailTtl;
    ImageView closeAppImgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_lock_help);
        sharedPrefUtils = new SharedPrefUtils(this);

        closeAppImgBtn = findViewById(R.id.closeAppImgBtn);
        closeAppImgBtn.setOnClickListener(this);

        Button helpBtn = findViewById(R.id.helpBtn);
        sharedPrefUtils.applyFont(helpBtn, AppUtils.FONTS.HelveticaNeueMedium);
        helpBtn.setOnClickListener(this);

        Button viewDetailBtn = findViewById(R.id.viewDetailBtn);
        sharedPrefUtils.applyFont(viewDetailBtn, AppUtils.FONTS.HelveticaNeueMedium);
        viewDetailBtn.setOnClickListener(this);

        emergencyContactInfoTv = findViewById(R.id.emergencyContactInfoTv);
        sharedPrefUtils.applyFont(emergencyContactInfoTv, AppUtils.FONTS.HelveticaNeue);

        helptDetailTtl = findViewById(R.id.helptDetailTtl);
        sharedPrefUtils.applyFont(helptDetailTtl, AppUtils.FONTS.HelveticaNeue);

        UserInfo userInfo = sharedPrefUtils.getUserInfo();
        myName = userInfo.getFirstname() + " " + userInfo.getLastname();

        phoneNumber = "(042)99330211";
        contactName = "Punjab Food Authority";


        String html = "<b>My Name: </b>" + myName + " <br/> <b> Emergency Phone #: </b>" +
                phoneNumber + " <br/> <b> Organization: </b>" + contactName;
        emergencyContactInfoTv.setText(Html.fromHtml(html));

    }

    @Override
    public void onAttachedToWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.helpBtn:

                if (phoneNumber != null) {

                    if (!isFinishing()) {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            callIntent.setPackage("com.android.server.telecom");
                        } else {
                            callIntent.setPackage("com.android.phone");
                        }
                        callIntent.setData(Uri.parse("tel:" + phoneNumber));
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(callIntent);

                        sendEmergencyMsgs();

                    }
//                    else
//                        sharedPrefUtils.showToast("Help button clicked");

                }
//               sharedPrefUtils.showToast("Help button clicked");
                break;
            case R.id.viewDetailBtn:
                if (emergencyContactInfoTv.getVisibility() == View.VISIBLE) {
                    emergencyContactInfoTv.setVisibility(View.GONE);
                } else {
                    emergencyContactInfoTv.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.closeAppImgBtn:
                finish();
                break;
            default:
                break;
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

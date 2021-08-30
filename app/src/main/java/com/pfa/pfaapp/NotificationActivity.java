package com.pfa.pfaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pfa.pfaapp.utils.AppUtils;

import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;

public class NotificationActivity extends BaseActivity {

    TextView notifTtlTV, notifDescTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificatin);

        notifTtlTV = findViewById(R.id.notifTtlTV);
        notifDescTv = findViewById(R.id.notifDescTv);

        sharedPrefUtils.applyFont(notifTtlTV, AppUtils.FONTS.HelveticaNeueMedium);
        sharedPrefUtils.applyFont(notifDescTv, AppUtils.FONTS.HelveticaNeue);


        String title = sharedPrefUtils.getSharedPrefValue("notifTitle", "");
        String messageBody = sharedPrefUtils.getSharedPrefValue("notifMessageBody", "");

        notifTtlTV.setText(title);
        notifDescTv.setText(messageBody);

        sharedPrefUtils.removeSharedPrefValue("notifTitle");
        sharedPrefUtils.removeSharedPrefValue("notifMessageBody");



    }



    public void onClickBackImgBtn(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {


        if (isTaskRoot()) {
            if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") == null)
                finish();
            else {
                if (String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase((sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, "")))) {
                    sharedPrefUtils.startNewActivity(FBOMainGridActivity.class, null, true);
                } else {
                    sharedPrefUtils.startNewActivity(PFADrawerActivity.class, null, true);
                }
            }
        } else {
            finish();
        }
    }
}

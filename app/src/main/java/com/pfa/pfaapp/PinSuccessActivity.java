package com.pfa.pfaapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.AppUtils;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_SINGLE_TOP;
import static com.pfa.pfaapp.utils.AppConst.SP_CNIC;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;
import static com.pfa.pfaapp.utils.AppConst.SP_PHONE_NUM;
//import static com.pfa.pfaapp.utils.AppConst.SP_SECURITY_CODE;
import static com.pfa.pfaapp.utils.AppConst.SP_PIN_CODE_MESSAGE;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;

public class PinSuccessActivity extends BaseActivity {
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_success);

        sharedPrefUtils.applyFont(findViewById(R.id.successMsgTV), AppUtils.FONTS.HelveticaNeue);

        sharedPrefUtils.applyFont(findViewById(R.id.callUsNumBtn), AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(findViewById(R.id.emailBtn), AppUtils.FONTS.HelveticaNeue);

        bundle = getIntent().getExtras();
    }

    private void saveLoginValues() {
        assert bundle != null;
        Log.d("loginType" , "type = " + bundle.getString(SP_LOGIN_TYPE));
//        bundle.putInt(SP_STAFF_ID, bundle.getInt(SP_STAFF_ID, -1));
//        bundle.putString(SP_LOGIN_TYPE, bundle.getString(SP_LOGIN_TYPE));
//        bundle.putString(AppConst.SP_CNIC, bundle.getString(SP_CNIC));
//        bundle.putString(AppConst.SP_PHONE_NUM, bundle.getString(SP_PHONE_NUM));
//        bundle.putBoolean("isLogin", true);
//        bundle.putBoolean(EXTRA_SINGLE_TOP, true);
        sharedPrefUtils.saveSharedPrefValue(SP_STAFF_ID, "" + bundle.getInt(SP_STAFF_ID));
        sharedPrefUtils.saveSharedPrefValue(SP_CNIC, bundle.getString(SP_CNIC));
        sharedPrefUtils.saveSharedPrefValue(SP_PHONE_NUM, bundle.getString(SP_PHONE_NUM));
        sharedPrefUtils.saveSharedPrefValue(SP_LOGIN_TYPE, bundle.getString(SP_LOGIN_TYPE));
        sharedPrefUtils.saveSharedPrefValue(SP_IS_LOGED_IN, SP_IS_LOGED_IN);
    }

    public void onClickCallUsBtn(View view) {
        sharedPrefUtils.doPhoneCall(getString(R.string.contact_phone_num));
    }

    public void onClickEmailBtn(View view) {
        sharedPrefUtils.sendEmail(getString(R.string.contact_email));

    }

    public void onClickSuccessBtn(View view) {

    }

    public void onClickContinueBtn(View view) {
        saveLoginValues();
        if (bundle.getString(SP_LOGIN_TYPE).equals("staff"))
            sharedPrefUtils.startHomeActivity(PFADrawerActivity.class, bundle);
        else
            setVerification(bundle , null);
//            sharedPrefUtils.startHomeActivity(FBOMainGridActivity.class, bundle);
    }
}

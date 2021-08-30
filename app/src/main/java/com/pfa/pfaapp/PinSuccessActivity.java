package com.pfa.pfaapp;

import android.os.Bundle;
import android.view.View;

import com.pfa.pfaapp.utils.AppUtils;

import static com.pfa.pfaapp.utils.AppConst.SP_CNIC;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;
import static com.pfa.pfaapp.utils.AppConst.SP_PHONE_NUM;
//import static com.pfa.pfaapp.utils.AppConst.SP_SECURITY_CODE;
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
        sharedPrefUtils.startHomeActivity(PFADrawerActivity.class, bundle);
    }
}

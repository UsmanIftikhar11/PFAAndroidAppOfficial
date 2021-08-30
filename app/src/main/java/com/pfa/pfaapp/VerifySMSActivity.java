package com.pfa.pfaapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;

import static com.pfa.pfaapp.utils.AppConst.SP_SECURITY_CODE;

public class VerifySMSActivity extends BaseActivity implements HttpResponseCallback {

    EditText pinCodeET1, pinCodeET2, pinCodeET3, pinCodeET4, pinCodeET5, pinCodeET6;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_sms);

        setTitle(getString(R.string.verify_code), true);

        sharedPrefUtils.applyFont(findViewById(R.id.tempPinTV), AppUtils.FONTS.HelveticaNeue);

        sharedPrefUtils.applyFont(findViewById(R.id.changeNumBtn), AppUtils.FONTS.HelveticaNeueMedium);


        pinCodeET1 = findViewById(R.id.pinCodeET1);
        sharedPrefUtils.applyFont(pinCodeET1, AppUtils.FONTS.HelveticaNeueMedium);

        pinCodeET2 = findViewById(R.id.pinCodeET2);
        sharedPrefUtils.applyFont(pinCodeET2, AppUtils.FONTS.HelveticaNeueMedium);

        pinCodeET3 = findViewById(R.id.pinCodeET3);
        sharedPrefUtils.applyFont(pinCodeET3, AppUtils.FONTS.HelveticaNeueMedium);

        pinCodeET4 = findViewById(R.id.pinCodeET4);
        sharedPrefUtils.applyFont(pinCodeET4, AppUtils.FONTS.HelveticaNeueMedium);

        pinCodeET5 = findViewById(R.id.pinCodeET5);
        sharedPrefUtils.applyFont(pinCodeET5, AppUtils.FONTS.HelveticaNeueMedium);

        pinCodeET6 = findViewById(R.id.pinCodeET6);
        sharedPrefUtils.applyFont(pinCodeET6, AppUtils.FONTS.HelveticaNeueMedium);


        sharedPrefUtils.applyFont(findViewById(R.id.forgotPinTV), AppUtils.FONTS.HelveticaNeueMedium);

        pinCodeET1.addTextChangedListener(textWatcher);
        pinCodeET2.addTextChangedListener(textWatcher);
        pinCodeET3.addTextChangedListener(textWatcher);
        pinCodeET4.addTextChangedListener(textWatcher);
        pinCodeET5.addTextChangedListener(textWatcher);
        pinCodeET6.addTextChangedListener(textWatcher);


        bundle = null;
        bundle = getIntent().getExtras();

        HashMap<String, String> reqParams = new HashMap<>();


        String cnic = bundle.getString(AppConst.SP_CNIC);
//            Replace (-) with empty space while sending cnic to server
        assert cnic != null;
        cnic = cnic.replaceAll("-", "");
        reqParams.put("cnic_number", cnic);

        reqParams.put("mobile_number", bundle.getString(AppConst.SP_PHONE_NUM));

        httpService.forgetPin(reqParams, this);


    }

    public void onClickVerifyBtn(View view) {
        String verificationCode = getPinCode();
        if (verificationCode.isEmpty() || verificationCode.length() < 6) {
            sharedPrefUtils.showMsgDialog("Please enter verification code", null);
            return;
        }

        if (verificationCode.equals(bundle.getString(SP_SECURITY_CODE))) {
//            sharedPrefUtils.startNewActivity(PinSuccessActivity.class, bundle, false);
            sharedPrefUtils.startNewActivity(SetPinActivity.class, bundle, true);
        } else {
            sharedPrefUtils.showMsgDialog(getString(R.string.invalid_verification_code), null);
        }
    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EditText text = (EditText) getCurrentFocus();

            if (text != null && text.length() > 0) {
                View next = text.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
                if (next != null)
                    next.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private String getPinCode() {
        String pin = pinCodeET1.getText().toString();
        pin += pinCodeET2.getText().toString();
        pin += pinCodeET3.getText().toString();
        pin += pinCodeET4.getText().toString();
        pin += pinCodeET5.getText().toString();
        pin += pinCodeET6.getText().toString();

        return pin;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
            EditText text = (EditText) getCurrentFocus();

            assert text != null;
            String previousPin = getPinCode();

            text.setText("");
            if (previousPin.length() != 6) {
                View next = text.focusSearch(View.FOCUS_LEFT); // or FOCUS_FORWARD
                if (next != null) {
                    next.requestFocus();
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        if (response != null) {

            if (response.optBoolean("status")) {
                JSONObject dataObject = response.optJSONObject("data");

                bundle.putString(SP_SECURITY_CODE, dataObject.optString(SP_SECURITY_CODE));

            } else {
                sharedPrefUtils.showMsgDialog(response.optString("message_code"), null);
            }

        } else {
            sharedPrefUtils.showMsgDialog("No data received from server", null);
        }

    }

    @Override
    public void onClickBackImgBtn(View view) {
        onBackPressed();
    }

}

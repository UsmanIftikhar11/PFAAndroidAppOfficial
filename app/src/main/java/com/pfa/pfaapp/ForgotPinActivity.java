package com.pfa.pfaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pfa.pfaapp.customviews.PFAEditText;
import com.pfa.pfaapp.customviews.PFATextInputLayout;
import com.pfa.pfaapp.interfaces.CNICTextWatcher;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class ForgotPinActivity extends BaseActivity implements HttpResponseCallback {

    PFAEditText phoneNumET, cnicNumET;
    PFATextInputLayout phoneNumETTIL, cnicETTIL;
    Button getCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pin);

        setTitle(getString(R.string.reset_pin), true);

        phoneNumETTIL = findViewById(R.id.phoneNumETTIL);
        phoneNumETTIL.setProperties(null);
        phoneNumET = findViewById(R.id.phoneNumET);

        cnicNumET = findViewById(R.id.cnicNumET);
        cnicETTIL = findViewById(R.id.cnicETTIL);
        cnicETTIL.setProperties(null);

        getCodeBtn = findViewById(R.id.getCodeBtn);

        sharedPrefUtils.applyFont(phoneNumET, AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(cnicNumET, AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(getCodeBtn, AppUtils.FONTS.HelveticaNeueMedium);


        cnicNumET.addTextChangedListener(new CNICTextWatcher(cnicNumET, String.valueOf(AppUtils.FIELD_TYPE.cnic), new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {

            }
        }, 0));


        cnicNumET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    cnicNumET.setHint("");
                } else {
                    cnicNumET.setHint(getString(R.string.cnic_number));
                    String cnicNum = cnicNumET.getText().toString().replaceAll("-", "");
                    if ((!cnicNum.isEmpty()) && cnicNum.length() < 13) {
                        cnicETTIL.setError(getString(R.string.invalid_cnic));
                    } else {
                        cnicETTIL.setError(null);
                    }
                }
            }
        });

        phoneNumET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String phoneNum = phoneNumET.getText().toString();

                phoneNumET.setHint(getString(R.string.mobile_number));

                if (hasFocus) {
                    phoneNumET.setHint("");
                    phoneNumETTIL.setError(null);

                } else {
                    if ((!phoneNum.startsWith("03")) || (phoneNum.length() < 11)) {
                        phoneNumETTIL.setError(getString(R.string.invalid_phone_num_msg));
                    } else {
                        phoneNumETTIL.setError(null);
                    }
                }
            }
        });
    }

    @Override
    public void onClickBackImgBtn(View view) {
        onBackPressed();
    }

    public void onClickGetCodeBtn(View view) {

        if (validateFields()) {
            HashMap<String, String> reqParams = new HashMap<>();

            String cnic = cnicNumET.getText().toString();
//            Replace (-) with empty space while sending cnic to server
            cnic = cnic.replaceAll("-", "");
            reqParams.put("cnic_number", cnic);

            reqParams.put("mobile_number", phoneNumET.getText().toString());

            httpService.forgetPin(reqParams, this);
        }
    }

    /**
     * Validating login params (cnic_number,mobile_number)
     */
    private boolean validateFields() {

        boolean isValid = true;
        String cnic = cnicNumET.getText().toString();
        cnic = cnic.replaceAll("-", "");
        if (cnic.isEmpty() || cnic.length() < 13) {
            cnicETTIL.setError(getString(R.string.invalid_cnic));
            isValid = false;
        }
        else
        {
            cnicETTIL.setError(null);
        }

        String phoneNum = phoneNumET.getText().toString();
        if ((!phoneNum.startsWith("03")) || (phoneNum.length() < 11)) {
            phoneNumETTIL.setError(getString(R.string.invalid_phone_num_msg));
            isValid = false;
        }
        else
        {
            phoneNumETTIL.setError(null);
        }
        return isValid;
    }


    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        if (response != null) {

            if (response.optBoolean("status")) {
                sharedPrefUtils.showMsgDialog(response.optString("message_code"), new SendMessageCallback() {
                    @Override
                    public void sendMsg(String message) {
                        sharedPrefUtils.startHomeActivity(LoginActivity.class, null);
                    }
                });

            } else {
                sharedPrefUtils.showMsgDialog(response.optString("message_code"), null);
            }

        } else {
            sharedPrefUtils.showMsgDialog("No data received from server", null);
        }
    }
}

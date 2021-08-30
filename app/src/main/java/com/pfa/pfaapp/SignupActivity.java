/*
 * Copyright (c) 2018. All Rights Reserved by Punjab Food Authority
 */

package com.pfa.pfaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.pfa.pfaapp.customviews.DateCustomDialog;
import com.pfa.pfaapp.customviews.PFATextInputLayout;
import com.pfa.pfaapp.httputils.ConfigHttpUtils;
import com.pfa.pfaapp.interfaces.CNICTextWatcher;
import com.pfa.pfaapp.interfaces.GetDateCallback;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.CustomDateUtils;

import org.json.JSONObject;

import java.util.HashMap;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_RESEND_API;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_SINGLE_TOP;
import static com.pfa.pfaapp.utils.AppConst.SIGNUP_REQ_PARAMS;
import static com.pfa.pfaapp.utils.AppConst.SP_CNIC;
import static com.pfa.pfaapp.utils.AppConst.SP_PHONE_NUM;
import static com.pfa.pfaapp.utils.AppConst.SP_SECURITY_CODE;

public class SignupActivity extends BaseActivity implements HttpResponseCallback {

    EditText cnicET, phoneNumET, nameET, emailET, dateET, alternatePhoneNumET;

    PFATextInputLayout nameETTIL, cnicETTIL, phoneNumETTIL, dateETTIL, alternatePhoneNumETTIL, emailETTIL;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initViews();
        setTitle(getString(R.string.register_new_account), true);

        bundle = getIntent().getExtras();

        nameETTIL.setProperties(null);
        cnicETTIL.setProperties(null);
        phoneNumETTIL.setProperties(null);
        dateETTIL.setProperties(null);
        alternatePhoneNumETTIL.setProperties(null);
        emailETTIL.setProperties(null);

        setInputFieldsFocus();

        new ConfigHttpUtils(this).fetchConfigData();

    }

    private void setInputFieldsFocus() {
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                EditText editText = (EditText) v;
                if (hasFocus) {
                    editText.setHint("");
                    removeErrorWarning(editText);
                } else {
                    editText.setHint(editText.getTag().toString());
                    removeErrorWarning(editText);
                }
            }
        };

        cnicET.setOnFocusChangeListener(onFocusChangeListener);
        phoneNumET.setOnFocusChangeListener(onFocusChangeListener);
        nameET.setOnFocusChangeListener(onFocusChangeListener);
        emailET.setOnFocusChangeListener(onFocusChangeListener);
        dateET.setOnFocusChangeListener(onFocusChangeListener);
        alternatePhoneNumET.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void removeErrorWarning(EditText editText) {
        switch (editText.getId()) {
            case R.id.cnicET:
                String cnicNum = editText.getText().toString().replaceAll("-", "");
                if ((!cnicNum.isEmpty()) && cnicNum.length() < 13) {
                    cnicETTIL.setError(getString(R.string.invalid_cnic));
                } else {
                    cnicETTIL.setError(null);
                }
                break;
            case R.id.phoneNumET:

                String phoneNum = phoneNumET.getText().toString();
                if ((!phoneNum.isEmpty()) && (!phoneNum.startsWith("03") || (phoneNum.length() < 11))) {
                    phoneNumETTIL.setError(getString(R.string.invalid_phone_num_msg));
                } else {
                    phoneNumETTIL.setError(null);
                }
                break;
            case R.id.nameET:
                nameETTIL.setError(null);
                break;
            case R.id.emailET:
                if ((!emailET.getText().toString().isEmpty()) && sharedPrefUtils.isInvalidEmail(emailET.getText().toString())) {
                    emailETTIL.setError(getString(R.string.invalid_email));
                } else {
                    emailETTIL.setError(null);
                }
                break;
            case R.id.dateET:
                dateETTIL.setError(null);
                break;
            case R.id.alternatePhoneNumET:
                String alternatePhoneNum = alternatePhoneNumET.getText().toString();
                if ((!alternatePhoneNum.isEmpty()) && (!alternatePhoneNum.startsWith("03") || (alternatePhoneNum.length() < 11))) {
                    alternatePhoneNumETTIL.setError(getString(R.string.invalid_phone_num_msg));
                } else {
                    alternatePhoneNumETTIL.setError(null);
                }
                break;
        }
    }

    private void initViews() {
        sharedPrefUtils.applyFont(findViewById(R.id.regNowTV), AppUtils.FONTS.HelveticaNeueMedium);
        sharedPrefUtils.applyFont(findViewById(R.id.cnicPhoneNUmTV), AppUtils.FONTS.HelveticaNeue);

        nameETTIL = findViewById(R.id.nameETTIL);
        cnicETTIL = findViewById(R.id.cnicETTIL);
        phoneNumETTIL = findViewById(R.id.phoneNumETTIL);

        if (!sharedPrefUtils.isEnglishLang()) {
            nameETTIL.setBackground(getResources().getDrawable(R.mipmap.ur_text_bg_star));
            cnicETTIL.setBackground(getResources().getDrawable(R.mipmap.ur_text_bg_star));
            phoneNumETTIL.setBackground(getResources().getDrawable(R.mipmap.ur_text_bg_star));
        }

        dateETTIL = findViewById(R.id.dateETTIL);
        alternatePhoneNumETTIL = findViewById(R.id.alternatePhoneNumETTIL);
        emailETTIL = findViewById(R.id.emailETTIL);


        cnicET = findViewById(R.id.cnicET);
        phoneNumET = findViewById(R.id.phoneNumET);
        alternatePhoneNumET = findViewById(R.id.alternatePhoneNumET);
        nameET = findViewById(R.id.nameET);
        emailET = findViewById(R.id.emailET);
        dateET = findViewById(R.id.dateET);


        sharedPrefUtils.applyFont(cnicET, AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(phoneNumET, AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(alternatePhoneNumET, AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(nameET, AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(emailET, AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(dateET, AppUtils.FONTS.HelveticaNeue);

        cnicET.addTextChangedListener(new CNICTextWatcher(cnicET, String.valueOf(AppUtils.FIELD_TYPE.cnic), new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {

            }
        }, 0));


        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateCustomDialog.showDatePickerDialog(SignupActivity.this, new GetDateCallback() {
                    @Override
                    public void onDateSelected(int day, int month, int year) {
                        dateET.setText((new CustomDateUtils().getDateString(day, month, year)));
                    }
                }, "past", null, null, dateET.getText().toString());
            }
        });

        sharedPrefUtils.applyFont((findViewById(R.id.signupBtn)), AppUtils.FONTS.HelveticaNeueMedium);
        sharedPrefUtils.applyFont((findViewById(R.id.existingAccountTV)), AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont((findViewById(R.id.loginBtn)), AppUtils.FONTS.HelveticaNeue);

    }

    HashMap<String, String> reqParams;

    public void onClickRegisterBtn(View view) {
        if (validateFields()) {

            reqParams = new HashMap<>();
            String cnic = cnicET.getText().toString();
//            Replace (-) with empty space while sending cnic to server
            cnic = cnic.replaceAll("-", "");
            reqParams.put("cnic_number", cnic);
            reqParams.put("mobile_number", phoneNumET.getText().toString());
            reqParams.put("name", nameET.getText().toString());
            reqParams.put("email", emailET.getText().toString());
            reqParams.put("dateOfBirth", dateET.getText().toString());
            reqParams.put("alternate_phone_num", alternatePhoneNumET.getText().toString());
            httpService.registerUser(reqParams, false, this);
        }
    }

    @Override
    public void onClickBackImgBtn(View view) {
        onBackPressed();
    }

    /**
     * Validating login params (cnic_number,mobile_number)
     */
    private boolean validateFields() {
        boolean isValid = true;
        String cnic = cnicET.getText().toString();
        cnic = cnic.replaceAll("-", "");
        if (cnic.isEmpty() || cnic.length() < 13) {
            cnicETTIL.setError(getString(R.string.invalid_cnic));
            isValid = false;
        }

        String phoneNum = phoneNumET.getText().toString();
        if ((!phoneNum.startsWith("03")) || (phoneNum.length() < 11)) {
            phoneNumETTIL.setError(getString(R.string.invalid_phone_num_msg));
            isValid = false;
        }

        String alternatePhoneNum = alternatePhoneNumET.getText().toString();
        if (!alternatePhoneNum.isEmpty()) {
            if (!alternatePhoneNum.startsWith("03") || alternatePhoneNum.length() < 11) {
                alternatePhoneNumETTIL.setError(getString(R.string.invalid_phone_num_msg));
                isValid = false;
            }
        }

        if (nameET.getText().toString().isEmpty()) {
            nameETTIL.setError(getString(R.string.required_field));
            isValid = false;
        }

        if ((!emailET.getText().toString().isEmpty()) && (sharedPrefUtils.isInvalidEmail(emailET.getText().toString()))) {
            emailETTIL.setError(getString(R.string.invalid_email));
            isValid = false;
        }

        return isValid;
    }


    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        if (response != null) {

            if (response.optBoolean("status")) {
                JSONObject dataObject = response.optJSONObject("data");
                sharedPrefUtils.saveSharedPrefValue(SP_SECURITY_CODE, dataObject.optString(SP_SECURITY_CODE));
                bundle.putString(SP_SECURITY_CODE, dataObject.optString(SP_SECURITY_CODE));
                bundle.putString(SP_CNIC, cnicET.getText().toString());
                bundle.putString(SP_PHONE_NUM, phoneNumET.getText().toString());
                bundle.putSerializable(SIGNUP_REQ_PARAMS, reqParams);

                if(dataObject.has(EXTRA_RESEND_API))
                {
                    bundle.putString(EXTRA_RESEND_API,dataObject.optString(EXTRA_RESEND_API));
                }

                bundle.putBoolean("isLogin", true);

                sharedPrefUtils.startNewActivity(VerifyActivity.class, bundle, false);

            } else {
                sharedPrefUtils.showMsgDialog(response.optString("message_code"), null);
            }

        } else {
            sharedPrefUtils.showMsgDialog("No data received from server", null);
        }
    }

    public void onClickLoginBtn(View view) {

        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_SINGLE_TOP, true);

        sharedPrefUtils.startNewActivity(LoginActivity.class, bundle, false);
    }
}

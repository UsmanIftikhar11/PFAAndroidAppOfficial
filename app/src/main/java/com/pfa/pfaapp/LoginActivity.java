package com.pfa.pfaapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.pfa.pfaapp.customviews.PFATextInputLayout;
import com.pfa.pfaapp.interfaces.CNICTextWatcher;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_PFA_MENU_ITEM;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_SINGLE_TOP;
import static com.pfa.pfaapp.utils.AppConst.MTO_WEB_URL;
import static com.pfa.pfaapp.utils.AppConst.SP_AUTH_TOKEN;
import static com.pfa.pfaapp.utils.AppConst.SP_CNIC;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;
import static com.pfa.pfaapp.utils.AppConst.SP_NEW_PIN_SMS_CODE;
import static com.pfa.pfaapp.utils.AppConst.SP_PHONE_NUM;
import static com.pfa.pfaapp.utils.AppConst.SP_PIN_CODE_MESSAGE;
import static com.pfa.pfaapp.utils.AppConst.SP_SECURITY_CODE;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;

public class LoginActivity extends BaseActivity implements HttpResponseCallback {

    EditText cnicET, pinNumET, phoneNumET;
    TextView forgotPinTV;
    PFATextInputLayout cnicETTIL, pinNumETTIL, phoneNumETTIL;

    String type;

    Bundle bundle;

    PFAMenuInfo pfaMenuInfo;

    boolean isStaff = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bundle = getIntent().getExtras();

        if (bundle != null) {
            pfaMenuInfo = (PFAMenuInfo) bundle.getSerializable(EXTRA_PFA_MENU_ITEM);
        }

        if (pfaMenuInfo != null && pfaMenuInfo.getMenuType().equalsIgnoreCase(String.valueOf(AppUtils.MENU_TYPE.login))) {
            isStaff = true;
        }
        initViews();

        cnicETTIL.setProperties(null);
        pinNumETTIL.setProperties(null);
        phoneNumETTIL.setProperties(null);

        if (isStaff) {
            findViewById(R.id.loginAsMTOBtn).setVisibility(View.VISIBLE);
            setTitle(getString(R.string.staff_login), true);

            if (bundle.containsKey("type"))
                type = bundle.getString("type");

            if (type != null && type.equalsIgnoreCase(String.valueOf(AppUtils.LOGIN_SETTING_TYPE.phone))) {
                phoneNumETTIL.setVisibility(View.VISIBLE);
                forgotPinTV.setVisibility(View.GONE);
            } else {
                type = String.valueOf(AppUtils.LOGIN_SETTING_TYPE.pin);
                pinNumETTIL.setVisibility(View.VISIBLE);
            }


        } else {
            setTitle(getString(R.string.user_login), true);

            type = String.valueOf(AppUtils.LOGIN_SETTING_TYPE.phone);
            phoneNumETTIL.setVisibility(View.VISIBLE);
            forgotPinTV.setVisibility(View.GONE);
        }

        sharedPrefUtils.applyFont(cnicET, AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(pinNumET, AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(phoneNumET, AppUtils.FONTS.HelveticaNeue);

    }

    @Override
    public void onClickBackImgBtn(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void initViews() {
        sharedPrefUtils.applyFont(findViewById(R.id.loginToAccountTV), AppUtils.FONTS.HelveticaNeueMedium);
        sharedPrefUtils.applyFont(findViewById(R.id.existingAccountTV), AppUtils.FONTS.HelveticaNeue);

        cnicETTIL = findViewById(R.id.cnicETTIL);
        pinNumETTIL = findViewById(R.id.pinNumETTIL);
        phoneNumETTIL = findViewById(R.id.phoneNumETTIL);

        if (!sharedPrefUtils.isEnglishLang()) {
            cnicETTIL.setBackground(getResources().getDrawable(R.mipmap.ur_text_bg_star));
            pinNumETTIL.setBackground(getResources().getDrawable(R.mipmap.ur_text_bg_star));
            phoneNumETTIL.setBackground(getResources().getDrawable(R.mipmap.ur_text_bg_star));
        }


        cnicET = findViewById(R.id.cnicET);
        sharedPrefUtils.applyFont(cnicET, AppUtils.FONTS.HelveticaNeue);

        cnicET.addTextChangedListener(new CNICTextWatcher(cnicET, String.valueOf(AppUtils.FIELD_TYPE.cnic), new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {

            }
        }, 0));

        pinNumET = findViewById(R.id.pinNumET);
        sharedPrefUtils.applyFont(pinNumET, AppUtils.FONTS.HelveticaNeue);

        phoneNumET = findViewById(R.id.phoneNumET);
        sharedPrefUtils.applyFont(phoneNumET, AppUtils.FONTS.HelveticaNeue);

        forgotPinTV = findViewById(R.id.forgotPinTV);
        sharedPrefUtils.applyFont(forgotPinTV, AppUtils.FONTS.HelveticaNeue);

        sharedPrefUtils.applyFont((findViewById(R.id.getCodeBtn)), AppUtils.FONTS.HelveticaNeueMedium);
        sharedPrefUtils.applyFont((findViewById(R.id.newRegTV)), AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont((findViewById(R.id.signupBTn)), AppUtils.FONTS.HelveticaNeue);

        phoneNumET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                phoneNumET.setHint(getString(R.string.phone_number));
                if (hasFocus) {
                    phoneNumET.setHint("");
                }
            }
        });

        cnicET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    cnicET.setHint("");
                } else {
                    cnicET.setHint(getString(R.string.cnic_number));
                    String cnicNum = cnicET.getText().toString().replaceAll("-", "");
                    if ((!cnicNum.isEmpty()) && cnicNum.length() < 13) {
                        cnicETTIL.setError(getString(R.string.invalid_cnic));
                    } else {
                        cnicETTIL.setError(null);
                    }
                }
            }
        });

        pinNumET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                pinNumET.setHint(getString(R.string.pin));
                String pinStr = pinNumET.getText().toString();
                if (hasFocus) {
                    pinNumET.setHint("");
                    if (pinStr.length() == 4)
                        pinNumETTIL.setError(null);
                } else {
                    if ((!pinStr.isEmpty()) && pinStr.length() < 4) {
                        pinNumETTIL.setError(getString(R.string.invalid_pin));
                    } else {
                        pinNumETTIL.setError(null);
                    }


                }
            }
        });
    }

    /**
     * Logoin User
     * Required params: cnic_number,mobile_number,type(value can be staff or client)
     * authenticateUser method of HttpService is called
     * Respones is received in onCompleteHttpResponse Method
     */
    public void onClickGetCodeBtn(View view) {

        if (validateFields()) {

            HashMap<String, String> reqParams = new HashMap<>();
            String cnic = cnicET.getText().toString();
//            Replace (-) with empty space while sending cnic to server
            cnic = cnic.replaceAll("-", "");
            reqParams.put("cnic_number", cnic);

            if (pinNumETTIL.getVisibility() == View.VISIBLE) {
                if (pinNumET.getText().toString().length() == 4)
                    pinNumETTIL.setError(null);

                reqParams.put("mobile_number", pinNumET.getText().toString());
            } else {
                reqParams.put("mobile_number", phoneNumET.getText().toString());
            }

            httpService.authenticateUser(reqParams, type, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPrefUtils.removeSharedPrefValue(SP_AUTH_TOKEN);
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
        } else {
            cnicETTIL.setError(null);
        }

        if (pinNumETTIL.getVisibility() == View.VISIBLE) {
            String pinNum = pinNumET.getText().toString();
            if ((pinNum.length() < 4)) {
                pinNumETTIL.setError(getString(R.string.invalid_pin));
                isValid = false;
            } else {
                pinNumETTIL.setError(null);
            }
        } else {
            String phoneNum = phoneNumET.getText().toString();
            if ((!phoneNum.startsWith("03")) || (phoneNum.length() < 11)) {
                phoneNumETTIL.setError(getString(R.string.invalid_phone_num_msg));
                isValid = false;
            } else {
                phoneNumETTIL.setError(null);
            }
        }
        return isValid;
    }

    /**
     * Http response received here and is in formatted json.
     * This is the callback method for HttpResponseCallback interface
     *
     * @param response   JSONObject is the response received
     * @param requestUrl String what url is called
     */
    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

//        {"status":true,"status_code":"200","data":{"staff_user_id":"21","staff_logged_in":true,"security_code":"541839","loginType":"staff/client"}}

        if (response != null) {

            if (response.optBoolean("status")) {
                JSONObject dataObject = response.optJSONObject("data");
                if (bundle == null)
                    bundle = new Bundle();

                bundle.putInt(SP_STAFF_ID, dataObject.optInt(SP_STAFF_ID, -1));

                bundle.putString(SP_LOGIN_TYPE, dataObject.optString(SP_LOGIN_TYPE));
                bundle.putString(AppConst.SP_CNIC, cnicET.getText().toString());
                bundle.putString(AppConst.SP_PHONE_NUM, phoneNumET.getText().toString());
                bundle.putBoolean("isLogin", true);
                bundle.putString(SP_PIN_CODE_MESSAGE, dataObject.optString(SP_PIN_CODE_MESSAGE));
                bundle.putBoolean(EXTRA_SINGLE_TOP, true);
                sharedPrefUtils.saveSharedPrefValue(SP_AUTH_TOKEN, dataObject.optString(SP_AUTH_TOKEN));

                if (String.valueOf(AppUtils.USER_LOGIN_TYPE.mto).equalsIgnoreCase((dataObject.optString(SP_LOGIN_TYPE)))) {
                    sharedPrefUtils.saveSharedPrefValue(MTO_WEB_URL, dataObject.optString(MTO_WEB_URL));
                }


                if (type.equalsIgnoreCase(String.valueOf(AppUtils.LOGIN_SETTING_TYPE.phone))) {

                    if ((dataObject.has(SP_SECURITY_CODE) && !dataObject.optString(SP_SECURITY_CODE).isEmpty()) || String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase((bundle.getString(SP_LOGIN_TYPE)))) {

                        String pinReceived = dataObject.optString(SP_SECURITY_CODE);
                        sharedPrefUtils.saveSharedPrefValue(SP_SECURITY_CODE, pinReceived);
//                        bundle.putString(SP_SECURITY_CODE, pinReceived);
                        bundle.putString("MyPin", pinReceived);
                        sharedPrefUtils.printLog("SecurityCode Received, LoginActivity ", "" + pinReceived);
                        sharedPrefUtils.startNewActivity(VerifyActivity.class, bundle, false);
                    } else {
                        sharedPrefUtils.saveSharedPrefValue(SP_NEW_PIN_SMS_CODE, dataObject.optString(SP_NEW_PIN_SMS_CODE));
//                        bundle.putString(SP_NEW_PIN_SMS_CODE, dataObject.optString(SP_NEW_PIN_SMS_CODE));
                        sharedPrefUtils.startNewActivity(VerifyActivity.class, bundle, false);
//                        sharedPrefUtils.startNewActivity(SetPinActivity.class, bundle, false);
                    }


                } else {

                    sharedPrefUtils.saveSharedPrefValue(SP_STAFF_ID, "" + bundle.getInt(SP_STAFF_ID));

                    if (dataObject.has(SP_SECURITY_CODE) && !dataObject.optString(SP_SECURITY_CODE).isEmpty()) {
                        String pinReceived = dataObject.optString(SP_SECURITY_CODE);
                        bundle.putString(SP_SECURITY_CODE, pinReceived);
                    }  //                        sharedPrefUtils.saveSharedPrefValue(SP_SECURITY_CODE, bundle.getString(SP_SECURITY_CODE));

                    sharedPrefUtils.saveSharedPrefValue(SP_CNIC, bundle.getString(SP_CNIC));
                    sharedPrefUtils.saveSharedPrefValue(SP_PHONE_NUM, bundle.getString(SP_PHONE_NUM));
                    sharedPrefUtils.saveSharedPrefValue(SP_LOGIN_TYPE, bundle.getString(SP_LOGIN_TYPE));

                    sharedPrefUtils.saveSharedPrefValue(SP_IS_LOGED_IN, SP_IS_LOGED_IN);

                    if (String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase((bundle.getString(SP_LOGIN_TYPE)))) {
                        sharedPrefUtils.startHomeActivity(FBOMainGridActivity.class, bundle);
                    } else {
                        sharedPrefUtils.startHomeActivity(PFADrawerActivity.class, bundle);
                    }
                }

            } else {
                sharedPrefUtils.showMsgDialog(response.optString("message_code").toUpperCase(), null);
            }

        } else {
            Log.d("loginResp" , "response = null" );
            sharedPrefUtils.showMsgDialog("No data received from server", null);
        }
    }

    public void onClickRegisterBtn(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_SINGLE_TOP, true);
        sharedPrefUtils.startNewActivity(SignupActivity.class, bundle, false);
    }

    public void onClickForgotPinBtn(View view) {
        sharedPrefUtils.startNewActivity(ForgotPinActivity.class, null, false);
    }

    public void onClickMTOLoginBtn(View view) {
        sharedPrefUtils.startHomeActivity(WebAppActivity.class,null);
    }
}

package com.pfa.pfaapp;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import static android.view.View.GONE;
import static com.pfa.pfaapp.utils.AppConst.*;

public class VerifyActivity extends BaseActivity implements HttpResponseCallback {

    EditText pinCodeET1, pinCodeET2, pinCodeET3, pinCodeET4, pinCodeET5, pinCodeET6;
    TextView timerTv;
    private Bundle bundle;
    PFAMenuInfo pfaMenuInfo;

    //    Timer Variables:
    private int timeSeconds = 1;
    Button manualVerify, forgotPinTV;

    boolean isLoginActivity = true;
    HashMap<String, String> reqParams;
    private String resendUrl;
    private String PinCodeMessage;
    private TextView tempPinTV;


    String securityCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        setTitle(getString(R.string.verify_mob_num), true);

        sharedPrefUtils.applyFont(findViewById(R.id.tempPinTV), AppUtils.FONTS.HelveticaNeue);

        timerTv = findViewById(R.id.timerTv);
        tempPinTV = findViewById(R.id.tempPinTV);
        sharedPrefUtils.applyFont(timerTv, AppUtils.FONTS.HelveticaNeueMedium);

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

        manualVerify = findViewById(R.id.manualVerify);
        sharedPrefUtils.applyFont(manualVerify, AppUtils.FONTS.HelveticaNeueMedium);

        forgotPinTV = findViewById(R.id.forgotPinTV);
        sharedPrefUtils.applyFont(forgotPinTV, AppUtils.FONTS.HelveticaNeueMedium);

        sharedPrefUtils.applyFont(findViewById(R.id.forgotPinTV), AppUtils.FONTS.HelveticaNeueMedium);

        pinCodeET1.addTextChangedListener(textWatcher);
        pinCodeET2.addTextChangedListener(textWatcher);
        pinCodeET3.addTextChangedListener(textWatcher);
        pinCodeET4.addTextChangedListener(textWatcher);
        pinCodeET5.addTextChangedListener(textWatcher);
        pinCodeET6.addTextChangedListener(textWatcher);

    }

    @Override
    public void onResume() {
        super.onResume();
        bundle = null;
        bundle = getIntent().getExtras();

        securityCode = "";

        if (bundle != null) {
//            securityCode = bundle.getString(SP_SECURITY_CODE);
//            securityCode = bundle.getString("MyPin");
            if (sharedPrefUtils.getSharedPrefValue(SP_NEW_PIN_SMS_CODE, "") != null) {
                securityCode = sharedPrefUtils.getSharedPrefValue(SP_NEW_PIN_SMS_CODE, "");
            } else {
                securityCode = sharedPrefUtils.getSharedPrefValue(SP_SECURITY_CODE, "");
            }
            sharedPrefUtils.printLog("PinCode Received, VerifyActivity ", "" + securityCode);
            if (bundle.containsKey(EXTRA_RESEND_API)) {
                forgotPinTV.setText(Html.fromHtml(getString(R.string.resend_pincode)));
                resendUrl = bundle.getString(EXTRA_RESEND_API);
                forgotPinTV.setVisibility(View.VISIBLE);
            }

            if (bundle.containsKey(SIGNUP_REQ_PARAMS)) {
                reqParams = (HashMap<String, String>) bundle.getSerializable(SIGNUP_REQ_PARAMS);
            }

            if (bundle.containsKey(SP_PIN_CODE_MESSAGE)) {
                PinCodeMessage = bundle.getString(SP_PIN_CODE_MESSAGE);
            }

            if (bundle.containsKey(EXTRA_PFA_MENU_ITEM))
                pfaMenuInfo = (PFAMenuInfo) bundle.getSerializable(EXTRA_PFA_MENU_ITEM);

            if (bundle.containsKey("isLogin") && bundle.getBoolean("isLogin")) {
                isLoginActivity = true;
            }
        }

        if (!PinCodeMessage.isEmpty())
            tempPinTV.setText(PinCodeMessage);
        else
            tempPinTV.setText(getResources().getString(R.string.enter_passcode));


        populateSecurityCode();
    }

    private void populateSecurityCode() {


        if (securityCode.length() == 6) {
//            if (!String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase((bundle.getString(SP_LOGIN_TYPE)))) {
                if (sharedPrefUtils.getSharedPrefValue(SP_NEW_PIN_SMS_CODE, "") == null)
                    forgotPinTV.setVisibility(View.VISIBLE);
//            }

            startTimer();
        }
    }

    private void showVerificationSuccessful() {

        sharedPrefUtils.showMsgDialog(getString(R.string.code_verified), new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {
                if (sharedPrefUtils.getSharedPrefValue(SP_NEW_PIN_SMS_CODE, "") != null) {
                    sharedPrefUtils.removeSharedPrefValue(SP_NEW_PIN_SMS_CODE);
                    sharedPrefUtils.startNewActivity(SetPinActivity.class, bundle, true);
                } else {
                    setVerification(bundle, pfaMenuInfo);
                }
            }
        });
    }

    @Override
    public void onClickBackImgBtn(View view) {
        onBackPressed();
    }

    public void onClickVerifyBtn(View view) {

        String verificationCode = getPinCode();

        if (verificationCode.isEmpty() || verificationCode.length() < 6) {
            sharedPrefUtils.showMsgDialog("Please enter verification code", null);
            return;
        }

        if (verificationCode.equals(securityCode)) {
            updateImageBg();

            if (reqParams == null) {
                /// show verification successful dialog
                showVerificationSuccessful();
                return;
            }

            httpService.registerUser(reqParams, true, new HttpResponseCallback() {
                @Override
                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

                    if (response != null) {
                        if (response.optBoolean("status")) {
                            JSONObject dataObject = response.optJSONObject("data");

                            bundle.putInt(SP_STAFF_ID, dataObject.optInt(SP_STAFF_ID, -1));
                            bundle.putString(SP_LOGIN_TYPE, dataObject.optString(SP_LOGIN_TYPE));
                            bundle.putSerializable(SIGNUP_REQ_PARAMS, reqParams);
                            /// show verification successful dialog
                            showVerificationSuccessful();
                        } else {
                            sharedPrefUtils.showMsgDialog(response.optString("message_code"), null);
                        }

                    } else {
                        sharedPrefUtils.showMsgDialog("Server error Occurred!", null);
                    }
                }
            });
        } else {
            sharedPrefUtils.showMsgDialog(getString(R.string.invalid_verification_code), null);
        }
    }

    public void resendCodeAPICall() {
        HashMap<String, String> reqParams = new HashMap<>();

        String cnic = bundle.getString(SP_CNIC);
        assert cnic != null;
        cnic = cnic.replaceAll("-", "");
        reqParams.put("cnic_number", cnic);
        reqParams.put("mobile_number", bundle.getString(SP_PHONE_NUM));

        if (bundle.containsKey(SIGNUP_REQ_PARAMS)) {
            httpService.fetchConfigData("account/resend_sms_code/" + bundle.getString(SP_PHONE_NUM), this);
        } else {
            httpService.authenticateUser(reqParams, String.valueOf(AppUtils.FIELD_TYPE.phone), this);
        }

    }

    private void updateImageBg() {
//        verifyIV.setImageResource(R.mipmap.mobile_bg_check);
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        if (response != null) {

            if (response.optBoolean("status")) {
                JSONObject dataObject = response.optJSONObject("data");
                bundle.putInt(SP_STAFF_ID, dataObject.optInt(SP_STAFF_ID, -1));
                bundle.putString(SP_SECURITY_CODE, dataObject.optString(SP_SECURITY_CODE));
                populateSecurityCode();

            } else {
                sharedPrefUtils.showMsgDialog(response.optString("message_code"), null);
            }

        } else {
            sharedPrefUtils.showMsgDialog("No data received from server", null);
        }
    }

    public void onClickChangeNumBtn(View view) {
        onBackPressed();
    }

    public void startTimer() {
        timeSeconds = 1;
        timerTv.setText(R.string.time_2_0_0);
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    Handler timerHandler = new Handler();

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timeSeconds -= 1;

            int seconds = timeSeconds % 60;
            int minutes = timeSeconds / 60;

            String localSeconds = "" + seconds;
            if (seconds < 10) {
                localSeconds = "0" + localSeconds;
            }

            timerTv.setText(String.format(Locale.getDefault(), "Resend in %d:%s", minutes, localSeconds));

            if (timeSeconds == 0) {
                timerTv.setText(R.string.resend_code);
                manualVerify.setVisibility(View.VISIBLE);
                timerTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startTimer();
                        manualVerify.setVisibility(GONE);

                        resendCodeAPICall();
                    }
                });
                timerHandler.removeCallbacks(timerRunnable);
            } else {
                timerHandler.postDelayed(timerRunnable, 1000);
            }
        }
    };

    //    TextWatcher for focusing to next EditText on entering text of pincode
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (getCurrentFocus() instanceof EditText) {
                EditText text = (EditText) getCurrentFocus();

                if (text != null && text.length() > 0) {
                    View next = text.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
                    if (next != null)
                        next.requestFocus();
                }
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
// for entering the back-space key to remove the pincode from EditText Fields.
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {

            if (getCurrentFocus() instanceof EditText) {
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
        }
        return super.dispatchKeyEvent(event);
    }

    public void onClickResetPinBtn(View view) {
        if (resendUrl == null)
            sharedPrefUtils.startNewActivity(VerifySMSActivity.class, bundle, true);
        else {
            httpService.getListsData(resendUrl, null, new HttpResponseCallback() {
                @Override
                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

                    if (response != null) {

                        if (response.optBoolean("status")) {
                            JSONObject dataJsonObject = response.optJSONObject("data");

                            sharedPrefUtils.printLog("VerifyPinCode==>", "" + (dataJsonObject.optString("security_code")));
                            securityCode = dataJsonObject.optString("security_code");

                            sharedPrefUtils.showMsgDialog("A security PIN Code has been sent to the Mobile Number provided via sms", null);
//                            {"status":true,"status_code":"REQUEST COMPLETED SUCCESSFULLY","data":{"security_code":"482017"}}

                        } else {
                            sharedPrefUtils.showMsgDialog(response.optString("message_code"), null);
                        }

                    } else {
                        sharedPrefUtils.showMsgDialog("No data received from server", null);
                    }
                }
            }, true);
        }
    }
}

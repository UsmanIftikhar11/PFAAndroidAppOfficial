package com.pfa.pfaapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;

import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;
import static com.pfa.pfaapp.utils.AppConst.SP_SECURITY_CODE;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;

public class SetPinActivity extends BaseActivity implements HttpResponseCallback {


    EditText pinCodeET1, pinCodeET2, pinCodeET3, pinCodeET4, pinCodeET5, pinCodeET6;
    EditText pinCodeET11, pinCodeET22, pinCodeET33, pinCodeET44, pinCodeET55, pinCodeET66;
    TextView cancelTVBtn;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);

        setTitle(getString(R.string.set_pin_code), true);
        sharedPrefUtils.applyFont(findViewById(R.id.setPinLblTV), AppUtils.FONTS.HelveticaNeue);

        cancelTVBtn = findViewById(R.id.cancelTVBtn);
        sharedPrefUtils.applyFont(cancelTVBtn, AppUtils.FONTS.HelveticaNeueMedium);

        sharedPrefUtils.applyFont(findViewById(R.id.setPinBtn), AppUtils.FONTS.HelveticaNeueMedium);

        sharedPrefUtils.applyFont(findViewById(R.id.confirmPinTV), AppUtils.FONTS.HelveticaNeueBold);
        sharedPrefUtils.applyFont(findViewById(R.id.pinCodeTV), AppUtils.FONTS.HelveticaNeueBold);

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

        //
        pinCodeET11 = findViewById(R.id.pinCodeET11);
        sharedPrefUtils.applyFont(pinCodeET11, AppUtils.FONTS.HelveticaNeueMedium);

        pinCodeET22 = findViewById(R.id.pinCodeET22);
        sharedPrefUtils.applyFont(pinCodeET22, AppUtils.FONTS.HelveticaNeueMedium);

        pinCodeET33 = findViewById(R.id.pinCodeET33);
        sharedPrefUtils.applyFont(pinCodeET33, AppUtils.FONTS.HelveticaNeueMedium);

        pinCodeET44 = findViewById(R.id.pinCodeET44);
        sharedPrefUtils.applyFont(pinCodeET44, AppUtils.FONTS.HelveticaNeueMedium);

        pinCodeET55 = findViewById(R.id.pinCodeET55);
        sharedPrefUtils.applyFont(pinCodeET55, AppUtils.FONTS.HelveticaNeueMedium);

        pinCodeET66 = findViewById(R.id.pinCodeET66);
        sharedPrefUtils.applyFont(pinCodeET66, AppUtils.FONTS.HelveticaNeueMedium);
        //
        bundle = getIntent().getExtras();

        pinCodeET1.addTextChangedListener(textWatcher);
        pinCodeET2.addTextChangedListener(textWatcher);
        pinCodeET3.addTextChangedListener(textWatcher);
        pinCodeET4.addTextChangedListener(textWatcher);
        pinCodeET5.addTextChangedListener(textWatcher);
        pinCodeET6.addTextChangedListener(textWatcher);

        //
        pinCodeET11.addTextChangedListener(textWatcher);
        pinCodeET22.addTextChangedListener(textWatcher);
        pinCodeET33.addTextChangedListener(textWatcher);
        pinCodeET44.addTextChangedListener(textWatcher);
        pinCodeET55.addTextChangedListener(textWatcher);
        pinCodeET66.addTextChangedListener(textWatcher);
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

    private String getConfirmPinCode() {
        String pin = pinCodeET11.getText().toString();
        pin += pinCodeET22.getText().toString();
        pin += pinCodeET33.getText().toString();
        pin += pinCodeET44.getText().toString();
        pin += pinCodeET55.getText().toString();
        pin += pinCodeET66.getText().toString();

        return pin;
    }

    public void onClickSetPinBtn(View view) {

        bundle.getBoolean("isLogin");

        String pin = getPinCode();
        String confirmPinCode = getConfirmPinCode();

        if (pin.length() == 6 && confirmPinCode.length() == 6 && (pin.equalsIgnoreCase(confirmPinCode))) {
            HashMap<String, String> reqParams = new HashMap<>();
            reqParams.put("staff_id", "" + (bundle.getInt(SP_STAFF_ID, -1)));

            reqParams.put("pin_code", pin);
            httpService.setPinCode(reqParams, bundle.getString(SP_LOGIN_TYPE), this);
        } else {
            sharedPrefUtils.showMsgDialog("PIN Code and Confirm PIN Code do not match!", null);
        }

    }

    private boolean isPinCode(View editText) {
        switch (editText.getId()) {
            case R.id.pinCodeET1:
            case R.id.pinCodeET2:
            case R.id.pinCodeET3:
            case R.id.pinCodeET4:
            case R.id.pinCodeET5:
            case R.id.pinCodeET6:
                return true;
            default:
                return false;

        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
            EditText text = (EditText) getCurrentFocus();

            assert text != null;
            String previousPin = isPinCode(text) ? getPinCode() : getConfirmPinCode();

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

    public void onClickCancelBtn(View view) {
        finish();
    }

    @Override
    public void onCompleteHttpResponse(final JSONObject response, String requestUrl) {
        if (response != null) {

            if (response.optBoolean("status")) {

                JSONObject dataObject = response.optJSONObject("data");
                bundle.putString(SP_SECURITY_CODE, dataObject.optString(SP_SECURITY_CODE));
                sharedPrefUtils.startNewActivity(PinSuccessActivity.class, bundle, false);
            } else {
                sharedPrefUtils.showMsgDialog(response.optString("message_code").toUpperCase(), null);
            }

        } else {
            sharedPrefUtils.showMsgDialog("No data received from server", null);
        }
    }
}

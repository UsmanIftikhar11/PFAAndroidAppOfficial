package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.text.Html;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.adapters.DropdownAdapter;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.CNICTextWatcher;
import com.pfa.pfaapp.interfaces.CheckUserCallback;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.PFATextWatcher;
import com.pfa.pfaapp.interfaces.PFAViewsCallbacks;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.pfa.pfaapp.utils.AppConst.CANCEL;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;
import static com.pfa.pfaapp.utils.AppConst.SP_VERIFICATION_CODE;

public class VerifyFBOLayout extends LinearLayout implements HttpResponseCallback {
    private PFAEditText verifyCodePFAET;
    public CNICEditText cnicET;
    public PFAEditText phoneNumET;
    PFAButton getCodeBtn;
    private String verificationCode;
    private int timeSeconds = 1;
    private PFAButton manualVerifyBtn;
    FormFieldInfo formFieldInfo;
    PFAViewsCallbacks pfaViewsCallbacks;

    LinearLayout showCodeFL;

    PFAViewsUtils sharedPrefUtils;
    String[] getCodeVals;
    public PFATextInputLayout cnicETTIL, verifyCodePFAETTIL, phoneNumETTIL;

    Button checkBtn;
    CheckUserCallback checkUserCallback;
    private WhichItemClicked whichItemClicked;
    boolean isFBO;


    public VerifyFBOLayout(Context mContext, FormFieldInfo formFieldInfo, PFAViewsCallbacks pfaViewsCallbacks, CheckUserCallback checkUserCallback) {
        super(mContext);
        this.formFieldInfo = formFieldInfo;
        this.pfaViewsCallbacks = pfaViewsCallbacks;
        this.checkUserCallback = checkUserCallback;
        init();
    }

    public VerifyFBOLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    boolean isAlreadyChecked;

    private void init() {
        isAlreadyChecked = false;
        inflate(getContext(), R.layout.verify_user_ll, this);
        sharedPrefUtils = new PFAViewsUtils(getContext());

        isFBO = sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, "") == null || String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase(sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, ""));


        cnicETTIL = findViewById(R.id.cnicETTIL);
        cnicETTIL.setProperties(null);

        phoneNumETTIL = findViewById(R.id.phoneNumETTIL);
        phoneNumETTIL.setProperties(null);

        verifyCodePFAETTIL = findViewById(R.id.verifyCodePFAETTIL);
        verifyCodePFAETTIL.setProperties(null);

        verifyCodePFAET = findViewById(R.id.verifyCodePFAET);
        phoneNumET = findViewById(R.id.phoneNumET);
        cnicET = findViewById(R.id.cnicET);
        cnicET.setTag("cnic_number");

        if (formFieldInfo.getDisable_fields() != null && formFieldInfo.getDisable_fields().size() > 0) {
            if (formFieldInfo.getDisable_fields().contains("cnic_number")) {
                cnicET.setInputType(InputType.TYPE_NULL);
                cnicETTIL.setClickable(false);
                cnicET.setEnabled(false);
                cnicET.setClickable(false);
                cnicET.setBackgroundColor(getContext().getResources().getColor(R.color.chat_list_footer_bg));
            }

            if (formFieldInfo.getDisable_fields().contains("mobile_number")) {
                phoneNumET.setInputType(InputType.TYPE_NULL);
                phoneNumETTIL.setClickable(false);
                phoneNumET.setClickable(false);
                phoneNumET.setEnabled(false);
                phoneNumET.setBackgroundColor(getContext().getResources().getColor(R.color.chat_list_footer_bg));
            }

        }

        checkBtn = findViewById(R.id.checkBtn);

        if (formFieldInfo.getValue() != null && (!formFieldInfo.getValue().isEmpty())) {
            getCodeVals = formFieldInfo.getValue().split(",");

            if (getCodeVals.length == 2) {
                AppConst.codeVerified = true;
                cnicET.setText(getCodeVals[0]);
                phoneNumET.setText(getCodeVals[1]);
            }
        }

        sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, "");
        sharedPrefUtils.applyFont(cnicET, AppUtils.FONTS.HelveticaNeueMedium);
        cnicET.addTextChangedListener(new CNICTextWatcher(cnicET, "cnic", new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {
                onInputChange();
            }
        }, cnicET.getText().toString().length()));

        cnicET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    cnicET.setHint("");
                    cnicETTIL.setError(null);
                } else {
                    cnicET.setHint(getContext().getString(R.string.cnic_number));
                    if ((!cnicET.getText().toString().isEmpty()) && (!sharedPrefUtils.validateCNIC(cnicET, false))) {
                        cnicETTIL.setError(getResources().getString(R.string.invalid_cnic));
                    } else {
                        cnicETTIL.setError(null);
                    }
                }
            }
        });

        phoneNumET.setTag("phonenumber");
        sharedPrefUtils.applyFont(phoneNumET, AppUtils.FONTS.HelveticaNeueMedium);

        phoneNumET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    phoneNumET.setHint("");
                    phoneNumETTIL.setError(null);
                } else {
                    phoneNumET.setHint(getContext().getString(R.string.mobile_number));
                    if ((!phoneNumET.getText().toString().isEmpty()) && (!sharedPrefUtils.validatePhoneNum(phoneNumET, false))) {
                        phoneNumETTIL.setError(getContext().getString(R.string.invalid_phone_num_msg));
                    } else {
                        phoneNumETTIL.setError(null);
                    }
                }
            }
        });

        getCodeBtn = findViewById(R.id.getCodeBtn);
        sharedPrefUtils.applyFont(getCodeBtn, AppUtils.FONTS.HelveticaNeueMedium);
        sharedPrefUtils.applyFont(checkBtn, AppUtils.FONTS.HelveticaNeueMedium);


        sharedPrefUtils.applyFont(verifyCodePFAET, AppUtils.FONTS.HelveticaNeueMedium);

        showCodeFL = findViewById(R.id.showCodeFL);

        verifyCodePFAET.addTextChangedListener(new PFATextWatcher(new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {
                if (message != null && (!message.isEmpty())) {
                    verificationCode = sharedPrefUtils.getSharedPrefValue(AppConst.SP_VERIFICATION_CODE, "");

                    if (verificationCode == null || (!verifyCodePFAET.getText().toString().equalsIgnoreCase(verificationCode))) {
                        AppConst.codeVerified = false;
                    } else if (verifyCodePFAET.getText().toString().equalsIgnoreCase(verificationCode)) {
                        hideViews();

                        AppConst.codeVerified = true;
                        manualVerifyBtn.setVisibility(GONE);
                        getCodeBtn.setVisibility(GONE);

                    }
                }
            }
        }));

        manualVerifyBtn = findViewById(R.id.manualVerifyBtn);
        sharedPrefUtils.applyFont(manualVerifyBtn, AppUtils.FONTS.HelveticaNeue);
        manualVerifyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                verificationCode = sharedPrefUtils.getSharedPrefValue(AppConst.SP_VERIFICATION_CODE, "");
                if (verificationCode != null) {
                    verifyCodePFAET.setText(verificationCode);
                }
            }
        });

        getCodeBtn.setOnClickListener(getCodeOnClickListener);

        checkBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPrefUtils.validateFields(cnicET, phoneNumET)) {
                    HashMap<String, String> reqParams = new HashMap<>();

                    String cnic = cnicET.getText().toString();
                    cnic = cnic.replaceAll("-", "");
                    reqParams.put("cnic_number", cnic);
                    reqParams.put("phonenumber", phoneNumET.getText().toString());
                    HttpService httpService = new HttpService(getContext());
                    httpService.checkExistingUser(reqParams, new HttpResponseCallback() {
                        @Override
                        public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

                            if (response != null && response.optBoolean("status")) {

                                final JSONArray jsonArray = response.optJSONArray("data");
                                if (jsonArray == null || jsonArray.length() == 0) {
                                    sharedPrefUtils.showMsgDialog("No user found with this CNIC and Phone Number", null);
                                } else {

                                    if (isAlreadyChecked) {
                                        //                                          this callback is returned in case the user taps OK
                                        checkUserCallback.getExistingUser(jsonArray);
                                        getCodeBtn.setVisibility(GONE);
                                        checkBtn.setVisibility(GONE);
                                    } else {
                                        sharedPrefUtils.showTwoBtnsMsgDialog("User already exists. Do you want to use existing information?", new SendMessageCallback() {
                                            @Override
                                            public void sendMsg(String message) {
                                                if (message.equalsIgnoreCase(CANCEL))
                                                    return;
//                                          this callback is returned in case the user taps OK
                                                checkUserCallback.getExistingUser(jsonArray);

                                                getCodeBtn.setVisibility(GONE);
                                                checkBtn.setVisibility(GONE);

                                            }
                                        });
                                    }

                                }
                            } else {
                                if (response != null) {

                                    if (response.optJSONArray("clients") != null && response.optJSONArray("clients").length() > 0) {
                                        showExistingBusinessDialog(response);
                                    } else {
                                        sharedPrefUtils.showMsgDialog("" + (response.optString("message_code")), null);
                                    }

                                } else {
                                    sharedPrefUtils.showMsgDialog(getContext().getString(R.string.server_error), null);
                                }


                            }
                        }
                    }, true);
                } else {
                    sharedPrefUtils.showMsgDialog("Please enter valid CNIC and Phone Number!", null);
                }
            }
        });

        addTextWatcher();
    }

    private void showExistingBusinessDialog(final JSONObject response) {

        boolean isPhone = false;
        ArrayList<String> businessArray = new Gson().fromJson(response.optJSONArray("clients").toString(), new TypeToken<ArrayList<String>>() {
        }.getType());

        DropdownAdapter adapter = new DropdownAdapter(getContext(), businessArray);

        String message = response.optString("message_code") + ".<br/> ";
        if (!response.optString("phonenumber").isEmpty()) {
            isPhone = true;
            message += "Existing Phone Number is:<br/> <b>" + response.optString("phonenumber") + "</b>";
        } else if (!response.optString("cnic_number").isEmpty()) {
            message += "Existing CNIC Number is:<br/> <b>" + response.optString("cnic_number") + "</b>";
        }

        message += "<br/><br/>Following businesses are registered.<br/>";

        final Dialog[] alertDialog = {new Dialog(getContext())};
        alertDialog[0].requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog[0].getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog[0].getWindow().setBackgroundDrawable(null);
        LayoutInflater li = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.existing_client_dialog, null);

        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView dialogmessage = view.findViewById(R.id.dialogmessage);
        dialogmessage.setText(Html.fromHtml(message));

        ListView clientsLV = view.findViewById(R.id.clientsLV);
        clientsLV.setAdapter(adapter);

        Button yesbtn = view.findViewById(R.id.yesbtn);


        final boolean finalIsPhone = isPhone;
        if (isPhone) {
            yesbtn.setText(R.string.use_this_phone_num);
        } else {
            yesbtn.setText(R.string.use_this_cnic_num);
        }
        yesbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (finalIsPhone) {
                    phoneNumET.setText(response.optString("phonenumber"));
                } else {
                    cnicET.setText(response.optString("cnic_number"));
                }

                alertDialog[0].dismiss();
                alertDialog[0] = null;
                isAlreadyChecked = true;
                checkBtn.performClick();
            }
        });

        Button noBtn = view.findViewById(R.id.noBtn);
        noBtn.setText(R.string.cancel);
        noBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cnicET.setText("");
                phoneNumET.setText("");

                alertDialog[0].dismiss();
                alertDialog[0] = null;
            }
        });

        sharedPrefUtils.applyFont(view.findViewById(R.id.dialogtitle), AppUtils.FONTS.HelveticaNeueBold);
        sharedPrefUtils.applyFont(dialogmessage, AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(yesbtn, AppUtils.FONTS.HelveticaNeueMedium);
        sharedPrefUtils.applyFont(noBtn, AppUtils.FONTS.HelveticaNeueMedium);

        alertDialog[0].setContentView(view);

        Window window = alertDialog[0].getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        alertDialog[0].show();

    }

    OnClickListener getCodeOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            getCodeBtn.setEnabled(false);
            getCodeBtn.setFocusable(false);
            getCodeBtn.setClickable(false);
            verifyCodePFAET.setText("");
            showTimer(null);
            manualVerifyBtn.setVisibility(GONE);
            getVerificationCode();
        }
    };

    private void getVerificationCode() {
        if (sharedPrefUtils.validateFields(cnicET, phoneNumET)) {
            HashMap<String, String> reqParams = new HashMap<>();

            String cnic = cnicET.getText().toString();
            cnic = cnic.replaceAll("-", "");
            reqParams.put("cnic_number", cnic);
            reqParams.put("phonenumber", phoneNumET.getText().toString());
            HttpService httpService = new HttpService(getContext());

            httpService.formSubmit(reqParams, null, formFieldInfo.getAPI_URL(), new HttpResponseCallback() {
                @Override
                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
//                                    {"status":true,"message_code":"REQUEST COMPLETED SUCCESSFULLY","data":{"status":true,"sms_code":520243,"msg":"done"}}
                    if (response != null && response.optBoolean("status")) {
                        JSONObject data = response.optJSONObject("data");
                        sharedPrefUtils.saveSharedPrefValue(SP_VERIFICATION_CODE, data.optString("sms_code"));

                        getCodeBtn.setEnabled(false);
                        getCodeBtn.setClickable(false);
                        showCodeFL.setVisibility(View.VISIBLE);

                    } else {

                        if (response != null)
                            sharedPrefUtils.showMsgDialog("" + (response.optString("message_code")), null);
                        else
                            sharedPrefUtils.showMsgDialog((getContext().getString(R.string.server_error)), null);
                    }
                }
            }, false, null);
        }
    }

    private void hideViews() {
        manualVerifyBtn.setVisibility(GONE);

        if (timerHandler != null)
            timerHandler.removeCallbacks(timerRunnable);
    }

    public void showTimer(WhichItemClicked whichItemClicked) {
        this.whichItemClicked = whichItemClicked;

        timeSeconds = 1;
        getCodeBtn.setText(R.string.time_2_0_0);
        timerHandler.postDelayed(timerRunnable, 1000);
    }


    public void stopTimer() {
        hideViews();
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

            getCodeBtn.setText(String.format(Locale.getDefault(), "Time %d:%s", minutes, localSeconds));

            if (timeSeconds == 0) {
                getCodeBtn.setText(R.string.get_code);
                if (!isFBO)
                    manualVerifyBtn.setVisibility(VISIBLE);
                getCodeBtn.setEnabled(true);
                getCodeBtn.setClickable(true);
                getCodeBtn.setFocusable(true);
                getCodeBtn.setOnClickListener(getCodeOnClickListener);
                timerHandler.removeCallbacks(timerRunnable);
            } else {
                timerHandler.postDelayed(timerRunnable, 1000);
            }
        }
    };


    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
    }

    private void addTextWatcher() {
        PFATextWatcher watcher = new PFATextWatcher(new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {
                onInputChange();
            }
        });
        phoneNumET.addTextChangedListener(watcher);
    }

    private void enableGetCode() {
        getCodeBtn.setEnabled(true);
        getCodeBtn.setFocusable(true);
        getCodeBtn.setClickable(true);
        getCodeBtn.setVisibility(VISIBLE);
        AppConst.codeVerified = false;
        getCodeBtn.setOnClickListener(getCodeOnClickListener);

        if (!isFBO)
            checkBtn.setVisibility(VISIBLE);

        getCodeBtn.setText(R.string.get_code);
    }

    private void onInputChange() {
        if (sharedPrefUtils.validateCNIC(cnicET, false) && sharedPrefUtils.validatePhoneNum(phoneNumET, false)) {
            if (getCodeVals != null && getCodeVals.length == 2) {
                if (cnicET.getText().toString().equalsIgnoreCase(getCodeVals[0]) && phoneNumET.getText().toString().equalsIgnoreCase(getCodeVals[1])) {
                    getCodeBtn.setEnabled(false);
                    getCodeBtn.setFocusable(false);
                    getCodeBtn.setClickable(false);
                    getCodeBtn.setVisibility(GONE);
                    checkBtn.setVisibility(GONE);
                    AppConst.codeVerified = true;
                } else {
                    enableGetCode();
                }
            } else {
                enableGetCode();
            }
        } else {
            AppConst.codeVerified = false;
            getCodeBtn.setVisibility(GONE);
            showCodeFL.setVisibility(GONE);
            checkBtn.setVisibility(GONE);
        }
        stopTimer();
    }

    public WhichItemClicked getWhichItemClicked() {
        return whichItemClicked;
    }
}

/*
 * Copyright (c) 2018. All Rights Reserved by Punjab Food Authority
 */

package com.pfa.pfaapp.httputils;

import android.util.Log;
import android.widget.LinearLayout;

import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.PFAButton;
import com.pfa.pfaapp.customviews.PFAViewsUtils;
import com.pfa.pfaapp.customviews.VerifyFBOLayout;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.pfa.pfaapp.utils.AppConst.APP_LATITUDE;
import static com.pfa.pfaapp.utils.AppConst.APP_LONGITUDE;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;
import static com.pfa.pfaapp.utils.AppConst.codeVerified;

public class PFAFormSubmitUtil extends PFAViewsUtils {
    private BaseActivity baseActivity;

    public PFAFormSubmitUtil(BaseActivity baseActivity) {
        super(baseActivity);
        this.baseActivity = baseActivity;
    }

    public void submitForm(final PFAButton button, HashMap<String, HashMap<String, Boolean>> sectionRequired, LinearLayout menuFragParentLL, final HttpResponseCallback callback, boolean showError) {

        VerifyFBOLayout verifyFBOLayout = menuFragParentLL.findViewWithTag("get_code_button");

        if (verifyFBOLayout != null) {
            if (!validateCNIC(verifyFBOLayout.cnicET, false)) {
                verifyFBOLayout.cnicETTIL.setError(mContext.getString(R.string.invalid_cnic));
            } else {
                verifyFBOLayout.cnicETTIL.setError(null);
            }

            if (!validatePhoneNum(verifyFBOLayout.phoneNumET, false)) {
                verifyFBOLayout.phoneNumETTIL.setError(mContext.getString(R.string.invalid_phone_num_msg));
            } else {
                verifyFBOLayout.phoneNumETTIL.setError(null);
            }
        }

        if (isFormDataValid(sectionRequired, menuFragParentLL, showError)) {
            HashMap<String, List<FormDataInfo>> formViewsData = getViewsData(menuFragParentLL, showError);
            final Map<String, File> filesMap = getFilesMap();

            final HashMap<String, String> reqParams = new HashMap<>();

            for (String key : formViewsData.keySet()) {

                List<FormDataInfo> formDataInfos = formViewsData.get(key);
                if (formDataInfos != null && formDataInfos.size() != 0) {
                    String formValues = "";
                    if (formDataInfos.size() == 1) {
                        formValues = formDataInfos.get(0).getKey();
                    } else {

                        for (int i = 0; i < formDataInfos.size(); i++) {
                            if (i == 0) {
                                formValues += formDataInfos.get(i).getKey();
                            } else {
                                formValues = String.format(Locale.getDefault(), "%s,%s", formValues, formDataInfos.get(i).getKey());
                            }
                        }
                    }
                    reqParams.put(key, formValues);
                }
            }

            if (verifyFBOLayout != null) {
                String cnic_number = verifyFBOLayout.cnicET.getText().toString();
                cnic_number = cnic_number.replaceAll("-", "");
                reqParams.put("cnic_number", cnic_number);
                reqParams.put("phonenumber", verifyFBOLayout.phoneNumET.getText().toString());
            }

            baseActivity.sharedPrefUtils.printLog("File Params=>", "Files=>" + filesMap.toString());

            if (reqParams.size() > 0 || filesMap.size() > 0) {

                if (baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LATITUDE, "") != null) {
                    reqParams.put(APP_LATITUDE, baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LATITUDE, ""));
                    reqParams.put(APP_LONGITUDE, baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LONGITUDE, ""));
                }
                if (baseActivity.sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID, "") != null) {
                    reqParams.put(SP_STAFF_ID, baseActivity.sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID, ""));
                    reqParams.put(SP_LOGIN_TYPE, baseActivity.sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, ""));
                }

                baseActivity.sharedPrefUtils.printLog("reqParams==>", "reqParams: " + reqParams.toString());

                if (filesMap.size() > 0) {
//                     in case of attachment rename the keys:
                    int attachmentCount = 0;

                    List<String> keys = new ArrayList<>(filesMap.keySet());
                    for (String key1 : keys) {
                        if (key1.startsWith("attachments")) {
                            if (filesMap.containsKey(key1)) {
                                File file = filesMap.get(key1);
                                filesMap.remove(key1);

                                assert file != null;
                                filesMap.put("attachments[" + attachmentCount + "]", file);
                                attachmentCount++;
                            }
                        }
                    }
                }

                if (verifyFBOLayout != null && (!codeVerified)) {
                    baseActivity.sharedPrefUtils.showMsgDialog(mContext.getResources().getString(R.string.verify_verification_code), null);
                    return;
                }

                if (button.getFormFieldInfo() != null && button.getFormFieldInfo().isShowBizConfirmMsg()) {
                    showTwoBtnsMsgDialog(mContext.getString(R.string.business_submit_prompt), new SendMessageCallback() {
                        @Override
                        public void sendMsg(String message) {
                            if (message != null && (!message.equalsIgnoreCase(AppConst.CANCEL)))
                                baseActivity.httpService.formSubmit(reqParams, filesMap, button.getButtonUrl(), callback, true, button.getFormFieldInfo().getAction());
                        }
                    });
                } else {
                    baseActivity.httpService.showProgressDialog(false);
                    baseActivity.httpService.formSubmit(reqParams, filesMap, button.getButtonUrl(), callback, true, button.getFormFieldInfo().getAction());
                }
            } else {
                baseActivity.sharedPrefUtils.showMsgDialog(mContext.getResources().getString(R.string.fill_atleast_one_field), null);
            }
        }
    }

    public boolean isFormDataValid(HashMap<String, HashMap<String, Boolean>> sectionRequired, LinearLayout menuFragParentLL, boolean showError) {
        boolean isValid = true;

        for (String sectionKey : sectionRequired.keySet()) {
            //fields value check in true false



            HashMap<String, Boolean> AllValues = sectionRequired.get(sectionKey);
            init(menuFragParentLL, AllValues);
            boolean allRequiredFields = isAllReqFieldsDone(showError);

            Log.d("formDataValid" , "allRequiredFields = " + allRequiredFields);

            if (isValid && (!allRequiredFields)) {
                isValid = false;
            }
        }

        Log.d("formDataValid" , "isValid = " + isValid);
        return isValid;
    }
}

package com.pfa.pfaapp.customviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.utils.SharedPrefUtils;
import com.samsung.android.sdk.pass.support.IFingerprintManagerProxy;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pfa.pfaapp.utils.AppConst.DISTRICT_TAG;
import static com.pfa.pfaapp.utils.AppConst.SP_VERIFICATION_CODE;
import static com.pfa.pfaapp.utils.AppConst.TOWN_TAG;

public class PFAViewsUtils extends SharedPrefUtils {
    private ViewGroup viewGroup;
    List<FormDataInfo> values;
    FormFieldInfo formFieldInfo;
    int count = 0;
    private HashMap<String, Boolean> reqViews;
    private List<View> viewList = new ArrayList<>();

    SharedPrefUtils sharedPrefUtils;
    private HashMap<String, List<FormDataInfo>> formViewsData = new HashMap<>();

    public PFAViewsUtils(Context mContext) {
        super(mContext);
    }

    public void init(ViewGroup viewGroup, HashMap<String, Boolean> reqViews) {

        this.viewGroup = viewGroup;
        this.reqViews = reqViews;

    }

    public void getVerificationCode(ViewGroup viewGroup, String API_URL, final View getCodeView, final VerifyFBOLayout verifyFBOLayout) {
        HttpService httpService = new HttpService(mContext);
        traversSubviews(viewGroup);

        PFAEditText cnicET = null, phoneNumET = null;

        for (View view : viewList) {
            if (view.getTag() != null) {

                if (view instanceof PFAEditText) {
                    PFAEditText pfaEditText = (PFAEditText) view;

                    if (pfaEditText.getFormFieldInfo() != null && pfaEditText.getFormFieldInfo().getField_type().equalsIgnoreCase("cnic")) {
                        cnicET = pfaEditText;
                    } else if (pfaEditText.getFormFieldInfo() != null && pfaEditText.getFormFieldInfo().getField_type().equalsIgnoreCase("phone")) {
                        phoneNumET = pfaEditText;
                    }

                    if (cnicET != null && phoneNumET != null) {

                        if (validateFields(cnicET, phoneNumET)) {
                            HashMap<String, String> reqParams = new HashMap<>();

                            String cnic = cnicET.getText().toString();
                            cnic = cnic.replaceAll("-", "");
                            reqParams.put("cnic_number", cnic);
                            reqParams.put("phonenumber", phoneNumET.getText().toString());
                            final PFAEditText finalPhoneNumET = phoneNumET;
                            final PFAEditText finalCnicET = cnicET;
                            httpService.formSubmit(reqParams, null, API_URL, new HttpResponseCallback() {
                                @Override
                                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

                                    if (response != null && response.optBoolean("status")) {
                                        JSONObject data = response.optJSONObject("data");
                                        saveSharedPrefValue(SP_VERIFICATION_CODE, data.optString("sms_code"));

                                        getCodeView.setVisibility(View.GONE);
                                        verifyFBOLayout.setVisibility(View.VISIBLE);
                                        verifyFBOLayout.showTimer(new WhichItemClicked() {
                                            @Override
                                            public void whichItemClicked(String id) {
                                                getCodeView.performClick();
                                            }

                                            @Override
                                            public void downloadInspection(String downloadUrl, int position) {

                                            }

                                            @Override
                                            public void deleteRecordAPICall(String deleteUrl, int position) {

                                            }
                                        });
                                        addTextWatcher(finalCnicET, finalPhoneNumET, getCodeView, verifyFBOLayout);

                                    } else {
                                        assert response != null;
                                        showMsgDialog("" + (response.optString("message_code")), null);

                                    }
                                }
                            }, true, null);
                        }
                        break;
                    }
                }
            }
        }
    }

    private void addTextWatcher(PFAEditText cnicET, PFAEditText phoneNumET, final View getCodeView, final VerifyFBOLayout verifyFBOLayout) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                getCodeView.setVisibility(View.VISIBLE);
                verifyFBOLayout.setVisibility(View.GONE);
                verifyFBOLayout.stopTimer();

            }
        };

        cnicET.addTextChangedListener(watcher);
        phoneNumET.addTextChangedListener(watcher);

    }

    boolean validateFields(PFAEditText cnicET, PFAEditText phoneNumET) {

        return validateCNIC(cnicET, true) && validatePhoneNum(phoneNumET, true);

    }

    protected boolean isAllReqFieldsDone(boolean showError) {

        boolean allFieldsValid = true;

        traversSubviews(viewGroup);

        for (View view : viewList) {

            if (view.getTag() != null) {
                String tagf = view.getTag().toString();

                if (reqViews.containsKey(view.getTag().toString()) && reqViews.get(view.getTag().toString())) {
                    if (view instanceof CheckBox) {
                        printLog("CheckBox", "selected CheckBox empty");
//                        pfaCheck

                    } else if (view instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = (PFADDACTV) view;
                        Log.d("checkViewName" , "name = " + viewGroup.findViewWithTag(""));
                        if (pfaddactv.getSelectedValues() == null || pfaddactv.getSelectedValues().size() == 0) {
                            allFieldsValid = false;
                            if (showError)
                                pfaddactv.getTextInputLayout().setError("Select " + pfaddactv.getHintValue());

                        }

                    } else if (view instanceof PFASearchACTV) {
                        PFASearchACTV pfaSearchACTV = (PFASearchACTV) view;
                        if (pfaSearchACTV.getPfaSearchInfo() == null)
                            allFieldsValid = false;
                    } else if (view instanceof CustomNetworkImageView) {
                        CustomNetworkImageView customNetworkImageView = (CustomNetworkImageView) view;
                        if (customNetworkImageView.getImageFile() == null && customNetworkImageView.getImgUrl() == null)
                            allFieldsValid = false;

                    } else if (view instanceof PFAButton) {
                        printLog("PFAButton", "selected PFAButton empty");

                    } else if (view instanceof PFALocationACTV && view.getVisibility() == View.VISIBLE) {
                        PFALocationACTV pfaLocationACTV = (PFALocationACTV) view;

                        if (pfaLocationACTV.getSelectedID() == -1) {

                            String tag = pfaLocationACTV.getTag().toString();

                            if (tag.equalsIgnoreCase(DISTRICT_TAG)) {
                                allFieldsValid = false;
                                pfaLocationACTV.getTextInputLayout().setError(mContext.getString(R.string.required_field));
                            }

                            if (tag.equalsIgnoreCase(TOWN_TAG)) {
                                allFieldsValid = false;
                                pfaLocationACTV.getTextInputLayout().setError(mContext.getString(R.string.required_field));
                            }
                        }

                    } else if (view instanceof PFAEditText) {

//
//                        SharedPreferences sharedPreferencesG = mContext.getSharedPreferences("pref", Context.MODE_PRIVATE);
//
//                        boolean Fake = sharedPreferencesG.getBoolean("IsNotRequired", false);
//                        if (Fake) {

                        Log.d("formDataValid", "instance of edittext created = ");

                        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(mContext);
                        if (sharedPrefUtils.getRLF("RequiredFalseField") != null) {

                            Log.d("formDataValid", "RequiredFalseField = " + "not null");

                            if (sharedPrefUtils.getRLF("RequiredFalseField").contains(view.getTag().toString())) {

                                validateEditText(false, view, showError);
                                Log.d("formDataValid", "RequiredFalseField = " + "here");
                            } else if (validateEditText(true, view, showError)) {
                                allFieldsValid = false;
                                Log.d("formDataValid", "RequiredFalseField = " + "here 1 ");
                            }
                        }

                        if (validateEditText(true, view, showError)) {
                            allFieldsValid = false;
                        }


                    } else if (view instanceof TextView) {
                        TextView editText = (TextView) view;
                        if (editText.getText().toString().isEmpty()) {
                            allFieldsValid = false;
                        }

                    } else if (view instanceof PFADDSpinner) {

                        PFADDSpinner spinner = (PFADDSpinner) view;
                        if (spinner.getSelectedValues() == null || spinner.getSelectedValues().size() == 0) {
                            allFieldsValid = false;
                        }

                    } else if (view instanceof PFAMultiSpinner) {

                        PFAMultiSpinner spinner = (PFAMultiSpinner) view;
                        if (spinner.getSelectedValues() == null || spinner.getSelectedValues().size() == 0) {
                            allFieldsValid = false;
                        }

                    } else if (view instanceof PFARadioGroup) {


//                           SharedPreferences sharedPreferencesG = PreferenceManager
//                                .getDefaultSharedPreferences(mContext);
                        SharedPreferences sharedPreferencesG = mContext.getSharedPreferences("pref", Context.MODE_PRIVATE);

                        boolean Fake = sharedPreferencesG.getBoolean("IsNotRequired", false);
                        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(mContext);


                        if (Fake && sharedPrefUtils.getRLF("RequiredFalseField").contains(sharedPrefUtils.getSharedPrefValue("SLUG", ""))) {
//                             if (Fake){

                            PFARadioGroup pfaRadioGroup = (PFARadioGroup) view;

                            pfaRadioGroup.setBackgroundResource(R.mipmap.text_bg);
                            pfaRadioGroup.setPadding(convertDpToPixel(10), convertDpToPixel(20), convertDpToPixel(10), convertDpToPixel(20));


                        } else {
//                        SharedPreferences sharedPreferencesf = PreferenceManager
//                                .getDefaultSharedPreferences(mContext);
//                        boolean Fake = sharedPreferencesf.getBoolean("IsNotRequired", false);
//
//
//                        if(Fake) {
//
//                            PFARadioGroup pfaRadioGroup = (PFARadioGroup) view;
//
//                            pfaRadioGroup.setBackgroundResource(R.mipmap.text_bg);
//                            pfaRadioGroup.setPadding(convertDpToPixel(10), convertDpToPixel(20), convertDpToPixel(10), convertDpToPixel(20));
//
//                        }else {
                            SharedPreferences sharedPreferences = PreferenceManager
                                    .getDefaultSharedPreferences(mContext);
                            boolean SR = sharedPreferences.getBoolean("singleRequiredKey", false);

                            if (SR) {

                                PFARadioGroup pfaRadioGroup = (PFARadioGroup) view;
                                if (values == null) {

                                    allFieldsValid = false;
                                    pfaRadioGroup.setBackgroundColor(mContext.getResources().getColor(R.color.checklist_error_color));
                                } else {
                                    int i = 0;

                                    if (i < values.size()) {
                                        if (!values.get(i).getValue().equalsIgnoreCase("Not Applicable")) {
                                            pfaRadioGroup.setBackgroundResource(R.mipmap.text_bg);

                                        }
                                        allFieldsValid = false;
                                        pfaRadioGroup.setBackgroundColor(mContext.getResources().getColor(R.color.checklist_error_color));
                                    }
//                               Toast.makeText(mContext, "D O N E", Toast.LENGTH_SHORT).show();
                                }
                                pfaRadioGroup.setPadding(convertDpToPixel(10), convertDpToPixel(20), convertDpToPixel(10), convertDpToPixel(20));

                            } else if (!SR) {

                                PFARadioGroup pfaRadioGroup = (PFARadioGroup) view;
                                if (pfaRadioGroup.getSelectedRB() == null) {
                                    allFieldsValid = false;
                                    pfaRadioGroup.setBackgroundColor(mContext.getResources().getColor(R.color.checklist_error_color));

                                } else {
                                    pfaRadioGroup.setBackgroundResource(R.mipmap.text_bg);
                                }
                                pfaRadioGroup.setPadding(convertDpToPixel(10), convertDpToPixel(20), convertDpToPixel(10), convertDpToPixel(20));

                            } else {

                            }
                        }
                    }
                } else {
                    if (view instanceof PFAEditText) {
                        if (validateEditText(false, view, showError)) {
                            allFieldsValid = false;
                        }
                    }
                }
            }
        }

        return allFieldsValid;
    }

    private boolean validateEditText(boolean isFieldReq, View view, boolean showError) {
        boolean allFieldsValid = true;
        PFAEditText pfaEditText = (PFAEditText) view;

//        SharedPreferences sharedPreferencesG = PreferenceManager
//                .getDefaultSharedPreferences(mContext);
        SharedPreferences sharedPreferencesG = mContext.getSharedPreferences("pref", Context.MODE_PRIVATE);

        boolean Fake = sharedPreferencesG.getBoolean("IsNotRequired", false);


        if (pfaEditText.getText().toString().isEmpty()) {
            if (isFieldReq)
                allFieldsValid = false;

            if (showError) {
                if (pfaEditText.textInputLayout != null && isFieldReq) {

                    Log.d("requiredFields", "required = " + true);
                    pfaEditText.textInputLayout.setError(mContext.getString(R.string.required_field));

                }
            }

        } else if (pfaEditText.getFormFieldInfo() != null && pfaEditText.getFormFieldInfo().getField_type().equalsIgnoreCase("cnic")) {
            if (!validateCNIC(pfaEditText, true)) {
                allFieldsValid = false;

                if (showError)
                    if (pfaEditText.textInputLayout != null)
                        pfaEditText.textInputLayout.setError(mContext.getString(R.string.invalid_cnic));
            }
        } else if (pfaEditText.getFormFieldInfo() != null && pfaEditText.getFormFieldInfo().getField_type().equalsIgnoreCase("phone")) {
            if (!validatePhoneNum(pfaEditText, true)) {
                if (showError)
                    if (pfaEditText.textInputLayout != null)
                        pfaEditText.textInputLayout.setError(mContext.getString(R.string.invalid_phone_num_msg));
                allFieldsValid = false;
            }
        } else if (pfaEditText.getFormFieldInfo() != null && pfaEditText.getFormFieldInfo().getField_type().equalsIgnoreCase("email")) {
            if (isInvalidEmail(pfaEditText.getText().toString())) {

                if (showError)
                    if (pfaEditText.textInputLayout != null)
                        pfaEditText.textInputLayout.setError(mContext.getString(R.string.invalid_email));
                allFieldsValid = false;
            }

        } else if (pfaEditText.getFormFieldInfo() != null && pfaEditText.getFormFieldInfo().getMin_limit() > 0) {
            if (pfaEditText.getText().toString().length() < pfaEditText.getFormFieldInfo().getMin_limit()) {

                if (showError)
                    if (pfaEditText.textInputLayout != null)
                        pfaEditText.textInputLayout.setError("Input field must have at least (" + pfaEditText.getFormFieldInfo().getMin_limit() + ") characters!");
                allFieldsValid = false;
            }
        }

        return !allFieldsValid;
    }

    private Map<String, File> filesMap = new HashMap<>();

    public HashMap<String, List<FormDataInfo>> getViewsData(ViewGroup parent, boolean showError) {
        viewGroup = parent;
        traversSubviews(parent);
        filesMap.clear();
        formViewsData.clear();
        for (View view : viewList) {
            addDataToFormViewsData(view, showError);
        }

        return formViewsData;
    }

    private void addDataToFormViewsData(View view, boolean showError) {
        if (view.getTag() != null && (!view.getTag().toString().isEmpty())) {

//            if (formFieldInfo.getRequired_false_fields().contains(view.getTag()))

            if (view instanceof PFALocationACTV) {
                PFALocationACTV pfaLocationACTV = (PFALocationACTV) view;
                if (pfaLocationACTV.getSelectedID() != -1) {

                    List<FormDataInfo> selectedValues = pfaLocationACTV.getSelectedValues();
                    if (selectedValues != null && selectedValues.size() > 0)
                        formViewsData.put(pfaLocationACTV.getTag().toString(), selectedValues);
                }

            } else if (view instanceof PFADDACTV) {
                PFADDACTV pfaDropdown = (PFADDACTV) view;
                if (pfaDropdown.getSelectedValues() != null && pfaDropdown.getSelectedValues().size() > 0) {

                    formViewsData.put(pfaDropdown.getTag().toString(), pfaDropdown.getSelectedValues());
                }

            } else if (view instanceof PFASearchACTV) {
                PFASearchACTV pfaSearchACTV = (PFASearchACTV) view;
                if (pfaSearchACTV.getPfaSearchInfo() != null) {

                    List<FormDataInfo> values = new ArrayList<>();
                    FormDataInfo formDataInfo = new FormDataInfo();
                    formDataInfo.setKey("" + (pfaSearchACTV.getPfaSearchInfo().getId()));
                    formDataInfo.setValue("" + (pfaSearchACTV.getPfaSearchInfo().getId()));
                    formDataInfo.setName(pfaSearchACTV.getTag().toString());
                    formDataInfo.setSelected(true);
                    values.add(formDataInfo);
                    formViewsData.put(pfaSearchACTV.getTag().toString(), values);
                }

            } else if (view instanceof PFACheckbox) {
                PFACheckbox checkbox = (PFACheckbox) view;

                List<FormDataInfo> values = new ArrayList<>();

                if (formViewsData.containsKey(checkbox.getTag().toString())) {
                    values = formViewsData.get(checkbox.getTag().toString());
                }

                if (checkbox.isChecked()) {
                    assert values != null;
                    if (!values.contains(checkbox.getFormDataInfo())) {
                        checkbox.getFormDataInfo().setSelected(true);
                        values.add(checkbox.getFormDataInfo());
                    }
                } else {
                    assert values != null;
                    checkbox.getFormDataInfo().setSelected(false);
                    values.remove(checkbox.getFormDataInfo());
                }

                if (values.size() > 0) {
                    formViewsData.put(checkbox.getTag().toString(), values);
                }

            } else if (view instanceof CustomNetworkImageView) {
                CustomNetworkImageView customNetworkImageView = (CustomNetworkImageView) view;

                if (customNetworkImageView.getImageFile() != null && (customNetworkImageView.getFormFieldInfo().isClickable()))
                    filesMap.put(customNetworkImageView.getTag().toString(), customNetworkImageView.getImageFile());

                ////////Local Image file path for Form ImageView
                if (customNetworkImageView.getImageFile() != null) {

                    List<FormDataInfo> values = new ArrayList<>();
                    FormDataInfo formDataInfo = new FormDataInfo();
                    formDataInfo.setKey("" + (customNetworkImageView.getImageFile().getAbsolutePath()));
                    formDataInfo.setValue("" + (customNetworkImageView.getImageFile().getAbsolutePath()));
                    formDataInfo.setName(customNetworkImageView.getTag().toString());
                    formDataInfo.setSelected(true);
                    values.add(formDataInfo);

                    formViewsData.put(customNetworkImageView.getTag().toString(), values);
                }


                if (customNetworkImageView.getFormFieldInfo().getData() != null && customNetworkImageView.getFormFieldInfo().getData().size() > 0) {
                    List<FormDataInfo> values = formViewsData.get(customNetworkImageView.getTag().toString());
                    for (FormDataInfo localFormDataInfo : customNetworkImageView.getFormFieldInfo().getData()) {

                        if (localFormDataInfo.getValue().startsWith("http")) {
                            if (values == null)
                                values = new ArrayList<>();
                            values.add(localFormDataInfo);
                        }
                    }
                    formViewsData.put(customNetworkImageView.getTag().toString(), values);
                }
                ////////End Local Image Path
//                }


            } else if (view instanceof PFAButton) {
                printLog("PFAButton", "PFAButton");

            } else if (view instanceof PFAEditText) {
                PFAEditText editText = (PFAEditText) view;

                if (!editText.getText().toString().isEmpty()) {
                    List<FormDataInfo> list = new ArrayList<>();
                    FormDataInfo formDataInfo = editText.getETData(showError);
                    list.add(formDataInfo);
                    if ((editText.getFormFieldInfo() == null) || (!editText.getFormFieldInfo().getField_type().equalsIgnoreCase(String.valueOf(FIELD_TYPE.abc))))
                        formViewsData.put(editText.getTag().toString(), list);
                }

            } else if (view instanceof PFASectionTV) {
                PFASectionTV editText = (PFASectionTV) view;
                if (!editText.getText().toString().isEmpty()) {
                    List<FormDataInfo> list = new ArrayList<>();
                    list.add(editText.formDataInfo);
                    formViewsData.put(editText.getTag().toString(), list);
                }

            } else if (view instanceof PFADDSpinner) {

                PFADDSpinner spinner = (PFADDSpinner) view;
                List<FormDataInfo> selectedValues = spinner.getSelectedValues();

                if (selectedValues.size() > 0) {
                    formViewsData.put(spinner.getTag().toString(), selectedValues);
                }

            } else if (view instanceof PFAMultiSpinner) {

                PFAMultiSpinner spinner = (PFAMultiSpinner) view;
                List<FormDataInfo> selectedValues = spinner.getSelectedValues();
                if (selectedValues.size() > 0) {
                    formViewsData.put(spinner.getTag().toString(), selectedValues);
                }

            } else if (view instanceof PFARadioGroup) {

                PFARadioGroup radioGroup = (PFARadioGroup) view;

                PFARadio radioButton = radioGroup.getSelectedRB();
                boolean allFieldsValid;
                if (radioButton != null) {
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(mContext);
                    boolean SR = sharedPreferences.getBoolean("singleRequiredKey", false);


                    if (SR && radioGroup.getSelectedRB().getFormDataInfo().getValue().equals("Not Applicable")) {

                        if (count == 0) {
                            allFieldsValid = false;
                            radioGroup.setBackgroundColor(mContext.getResources().getColor(R.color.checklist_error_color));
                        } else {
                            allFieldsValid = true;
                            radioGroup.setBackgroundResource(R.mipmap.text_bg);
                            radioGroup.setPadding(convertDpToPixel(10), convertDpToPixel(20), convertDpToPixel(10), convertDpToPixel(20));

                        }
                    } else if (SR && !radioGroup.getSelectedRB().getFormDataInfo().getValue().equals("Not Applicable")) {
                        count++;
                        allFieldsValid = true;
                        radioGroup.setBackgroundResource(R.mipmap.text_bg);
                        radioGroup.setPadding(convertDpToPixel(10), convertDpToPixel(20), convertDpToPixel(10), convertDpToPixel(20));

                    } else {

                    }
// It was commented
//                        values = new ArrayList<>();
//                        values.add(radioButton.getFormDataInfo());
//                        formViewsData.put(radioButton.getTag().toString(), values);
//


                }
            }
        }
    }

    protected Map<String, File> getFilesMap() {
        return filesMap;
    }

    /**
     * Method to travers all the subviews of viewgroup recursively
     *
     * @param parent {@link ViewGroup} it can be linearlayout, relativelayout, framelayout etc
     *               All the traversed values added into list @viewList
     */
    private void traversSubviews(ViewGroup parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if ((child instanceof ViewGroup && (!(child instanceof RadioGroup)) && (!(child instanceof PFADDSpinner)) && (!(child instanceof PFAMultiSpinner)))) {
                traversSubviews((ViewGroup) child);
                // DO SOMETHING WITH VIEWGROUP, AFTER CHILDREN HAS BEEN LOOPED
            } else {
                if (child != null && (child.getTag() != null) && (!child.getTag().toString().isEmpty())) {
                    viewList.add(child);
                }
            }
        }

    }


}

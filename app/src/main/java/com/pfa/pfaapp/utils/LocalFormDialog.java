package com.pfa.pfaapp.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.LocalFormDialogActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.CustomNetworkImageView;
import com.pfa.pfaapp.customviews.CustomViewCreate;
import com.pfa.pfaapp.customviews.PFAButton;
import com.pfa.pfaapp.customviews.PFASectionTV;
import com.pfa.pfaapp.customviews.VerifyFBOLayout;
import com.pfa.pfaapp.httputils.PFAFormSubmitUtil;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.PFAViewsCallbacks;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormSectionInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_DIALOG_ADD_ITEM_FORM_SECTION;
import static com.pfa.pfaapp.utils.AppConst.REQ_CODE_ADD_ITEM;

public class LocalFormDialog extends CustomDialogs implements PFAViewsCallbacks {
    private BaseActivity baseActivity;
    public CustomViewCreate customViewCreate;
    private HashMap<String, HashMap<String, Boolean>> sectionRequired = new HashMap<>();
    public ImageSelectionUtils imageSelectionUtils;

    public LocalFormDialog(BaseActivity baseActivity) {
        super(baseActivity);
        this.baseActivity = baseActivity;
        customViewCreate = new CustomViewCreate(baseActivity, this);

    }

    public LinearLayout menuFragParentLL;

    public void addFormItem(final FormSectionInfo formSectionInfo) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_DIALOG_ADD_ITEM_FORM_SECTION, formSectionInfo);

        baseActivity.sharedPrefUtils.startActivityForResult(baseActivity, LocalFormDialogActivity.class, bundle, REQ_CODE_ADD_ITEM);

//        This code is commented because LocalFormDialogActiivty is created as separate module for Adding items to list sections of Inspection

//        final Dialog[] alertDialog = {new Dialog(baseActivity)};
//        alertDialog[0].requestWindowFeature(Window.FEATURE_NO_TITLE);
//        alertDialog[0].getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        alertDialog[0].getWindow().setBackgroundDrawable(null);
//        LayoutInflater li = LayoutInflater.from(baseActivity);
//        @SuppressLint("InflateParams") final View view = li.inflate(R.layout.local_form_dialog, null);
//
//        menuFragParentLL = view.findViewById(R.id.menuFragParentLL);
//
//        customViewCreate.createViews(formSectionInfo, menuFragParentLL, sectionRequired, new PFAViewsCallbacks() {
//            @Override
//            public void showImagePickerDialog(CustomNetworkImageView view) {
//                imageSelectionUtils = new ImageSelectionUtils(baseActivity, view);
//                imageSelectionUtils.showImagePickerDialog(null, false,false);
//            }
//
//            @Override
//            public void onLabelViewClicked(PFASectionTV pfaSectionTV) {
//
//            }
//
//            @Override
//            public void onButtonCLicked(View view) {
//
//            }
//
//            @Override
//            public void onClickGetCodeBtn(View view, VerifyFBOLayout verifyFBOLayout) {
//
//            }
//
//            @Override
//            public void onDropdownItemSelected(final FormDataInfo formDataInfo) {
//                Log.e("LocalFormsDialog", "onDropdownItemSelected");
//                customViewCreate.onDDSelectedAPIUrl(formDataInfo,menuFragParentLL,sectionRequired,(ScrollView) view.findViewById(R.id.formDialogSV),null);
//
//            }
//        },false, (ScrollView) view.findViewById(R.id.formDialogSV));
//
//        Button yesbtn = view.findViewById(R.id.yesbtn);
//
//        yesbtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                HashMap<String, List<FormDataInfo>> formViewsData = pfaFormSubmitUtil.getViewsData(menuFragParentLL,true);
//
//                if (pfaFormSubmitUtil.isFormDataValid(sectionRequired, menuFragParentLL,true)) {
//                    if (formSectionInfo!=null && formSectionInfo.getFields() != null && formSectionInfo.getFields().size() > 0) {
//                        for (int j = 0; j < formSectionInfo.getFields().size(); j++) {
//
//                            if (formViewsData.containsKey(formSectionInfo.getFields().get(j).getField_name())) {
//                                if (formSectionInfo.getFields().get(j).getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.dropdown))) {
//                                    formSectionInfo.getFields().get(j).setDefault_value(formViewsData.get(formSectionInfo.getFields().get(j).getField_name()).get(0).getKey());
//                                } else if (formSectionInfo.getFields().get(j).getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.radiogroup))) {
//                                    formSectionInfo.getFields().get(j).setDefault_value(formViewsData.get(formSectionInfo.getFields().get(j).getField_name()).get(0).getKey());
//                                } else {
//
//                                    formSectionInfo.getFields().get(j).setData(formViewsData.get(formSectionInfo.getFields().get(j).getField_name()));
//                                }
//
//                            }
//                        }
//                        alertDialog[0].dismiss();
//                        alertDialog[0] = null;
//                        addFormSectionCallback.addFormSection(formSectionInfo);
//
//                    }
//                }
//
//                hideKeyBoard();
//            }
//        });
//
//        Button noBtn = view.findViewById(R.id.noBtn);
//
//        noBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                alertDialog[0].dismiss();
//                alertDialog[0] = null;
//                hideKeyBoard();
//            }
//        });
//
//        alertDialog[0].setContentView(view);
//
//        if (!baseActivity.isFinishing()) {
//            alertDialog[0].show();
//        }
//
//        alertDialog[0].setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                hideKeyBoard();
//            }
//        });
    }

    @Override
    public void showImagePickerDialog(CustomNetworkImageView view) {

    }

    @Override
    public void onLabelViewClicked(PFASectionTV pfaSectionTV) {

    }

    @Override
    public void onButtonCLicked(View view) {

    }

    @Override
    public void onClickGetCodeBtn(View view, VerifyFBOLayout verifyFBOLayout) {

    }

    @Override
    public void onDropdownItemSelected(FormDataInfo formDataInfo, String dataName) {

    }

    private void hideKeyBoard() {

        final View caller = baseActivity.getWindow().getDecorView();
        caller.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) caller.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(caller.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }


    public void addBusinessVisitDialog(JSONObject response) {

        final Dialog[] alertDialog = {new Dialog(baseActivity)};
        alertDialog[0].requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog[0].getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog[0].getWindow().setBackgroundDrawable(null);
        LayoutInflater li = LayoutInflater.from(baseActivity);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.business_visit_dialog, null);

        menuFragParentLL = view.findViewById(R.id.businessVisitLL);

        if (response != null && response.optBoolean("status")) {
            try {

                Type type = new TypeToken<List<FormSectionInfo>>() {
                }.getType();

                if (response.has("title")) {
                    baseActivity.setTitle(response.optString("title"), true);
                }
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (dataJsonObject.has("form")) {

                    JSONArray formJSONArray = dataJsonObject.getJSONArray("form");
                    List<FormSectionInfo> formSectionInfos = new GsonBuilder().create().fromJson(formJSONArray.toString(), type);

                    customViewCreate = new CustomViewCreate(mContext, new PFAViewsCallbacks() {
                        @Override
                        public void showImagePickerDialog(CustomNetworkImageView view) {
                            imageSelectionUtils = new ImageSelectionUtils(baseActivity, view);
                            imageSelectionUtils.showImagePickerDialog(null, false, false);
                        }

                        @Override
                        public void onLabelViewClicked(PFASectionTV pfaSectionTV) {

                        }

                        @Override
                        public void onButtonCLicked(View view) {
                            (new PFAFormSubmitUtil(baseActivity)).submitForm((PFAButton) view, sectionRequired, menuFragParentLL, new HttpResponseCallback() {
                                @Override
                                public void onCompleteHttpResponse(final JSONObject response, String requestUrl) {
                                    alertDialog[0].dismiss();
                                    alertDialog[0].cancel();
                                    baseActivity.sharedPrefUtils.showMsgDialog("" + (response.optString("message_code")), null);

                                }
                            }, true);
                        }

                        @Override
                        public void onClickGetCodeBtn(View view, VerifyFBOLayout verifyFBOLayout) {

                        }

                        @Override
                        public void onDropdownItemSelected(FormDataInfo formDataInfo, String dataName) {

                        }
                    });

                    for (FormSectionInfo formSectionInfo : formSectionInfos) {
                        customViewCreate.createViews(formSectionInfo, menuFragParentLL, sectionRequired, null, false, (ScrollView) view.findViewById(R.id.addBizDialogSV));
                    }

                }


            } catch (JSONException e) {
                baseActivity.sharedPrefUtils.printStackTrace(e);
            }
        }


        alertDialog[0].setContentView(view);

        if (!baseActivity.isFinishing()) {
            alertDialog[0].show();
        }

        alertDialog[0].setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideKeyBoard();
            }
        });
    }


    public void updateDropdownViewsData(Bundle bundle) {

        customViewCreate.updateDropdownViewsData(bundle, menuFragParentLL, sectionRequired);

//        if (bundle != null && bundle.containsKey(EXTRA_ACTV_TAG)) {
//            String actvTag = bundle.getString(EXTRA_ACTV_TAG);
//            int selectedPosition = bundle.getInt(SELECTED_POSITION, -1);
//
//            View pfaddactv = menuFragParentLL.findViewWithTag(actvTag);
//
//            if (pfaddactv instanceof PFADDACTV) {
//                PFADDACTV pfaddactv1 = (PFADDACTV) pfaddactv;
//                if (pfaddactv1.getTextInputLayout() != null) {
//                    pfaddactv1.getTextInputLayout().setError(null);
//                    pfaddactv1.clearFocus();
//                }
//            }
//        }
    }

}

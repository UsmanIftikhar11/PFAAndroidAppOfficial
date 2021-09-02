package com.pfa.pfaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pfa.pfaapp.customviews.CustomNetworkImageView;
import com.pfa.pfaapp.customviews.CustomViewCreate;
import com.pfa.pfaapp.customviews.PFASectionTV;
import com.pfa.pfaapp.customviews.VerifyFBOLayout;
import com.pfa.pfaapp.httputils.PFAFormSubmitUtil;
import com.pfa.pfaapp.interfaces.PFAViewsCallbacks;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormSectionInfo;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.ImageSelectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.pfa.pfaapp.utils.AppConst.CAPTURE_PHOTO;
import static com.pfa.pfaapp.utils.AppConst.CHOOSE_FROM_GALLERY;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_DIALOG_ADD_ITEM_FORM_SECTION;
import static com.pfa.pfaapp.utils.AppConst.RC_DROPDOWN;
import static com.pfa.pfaapp.utils.AppConst.RECORD_VIDEO;

public class LocalFormDialogActivity extends BaseActivity implements PFAViewsCallbacks {
    public CustomViewCreate customViewCreate;
    private HashMap<String, HashMap<String, Boolean>> sectionRequired = new HashMap<>();
    private PFAFormSubmitUtil pfaFormSubmitUtil;
    public ImageSelectionUtils imageSelectionUtils;
    public LinearLayout menuFragParentLL;
    FormSectionInfo formSectionInfo;
    Button yesbtn;
    ProgressBar saveProgressDialog;
    TextView txtProgress;

    List<FormSectionInfo> formSectionInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_form_dialog);

        formSectionInfos = new ArrayList<>();
        menuFragParentLL = findViewById(R.id.menuFragParentLL);
        customViewCreate = new CustomViewCreate(this, this);
        pfaFormSubmitUtil = new PFAFormSubmitUtil(this);

        formSectionInfo = (FormSectionInfo) getIntent().getSerializableExtra(EXTRA_DIALOG_ADD_ITEM_FORM_SECTION);
        formSectionInfos.add(formSectionInfo);
        setTitle(formSectionInfo.getSection_name(), true);

//                SharedPreferences sharedPreferences = PreferenceManager
//                        .getDefaultSharedPreferences(this);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("Section_Name", formSectionInfo.getSection_name());
//                editor.apply();

        customViewCreate.createViews(formSectionInfo, menuFragParentLL, sectionRequired, new PFAViewsCallbacks() {
            @Override
            public void showImagePickerDialog(CustomNetworkImageView view) {
                Log.d("imagePath" , "image selection utils local form dialog activity1");
                imageSelectionUtils = new ImageSelectionUtils(LocalFormDialogActivity.this, view);
                imageSelectionUtils.showImagePickerDialog(null, false, false);
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
            public void onDropdownItemSelected(final FormDataInfo formDataInfo, String dataName) {
                sharedPrefUtils.printLog("LocalFormsDialog", "onDropdownItemSelected");

                formSectionInfos.clear();
                formSectionInfos.add(formSectionInfo);
                customViewCreate.onDDSelectedAPIUrl(formDataInfo, menuFragParentLL, sectionRequired, (ScrollView) findViewById(R.id.formDialogSV), formSectionInfos);

            }
        }, false, (ScrollView) findViewById(R.id.formDialogSV));

         yesbtn = findViewById(R.id.yesbtn);
         saveProgressDialog = findViewById(R.id.saveProgressDialog);
         txtProgress = findViewById(R.id.txtProgress);

        yesbtn.setEnabled(true);
        yesbtn.setClickable(true);
        saveProgressDialog.setVisibility(View.GONE);
        txtProgress.setVisibility(View.GONE);

        yesbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d("yesButton" , "yes button clicked!!!");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        yesbtn.setEnabled(true);
                        yesbtn.setClickable(true);
                        saveProgressDialog.setVisibility(View.GONE);
                        txtProgress.setVisibility(View.GONE);
                    }
                } , 2000);

                yesbtn.setEnabled(false);
                yesbtn.setClickable(true);
                saveProgressDialog.setVisibility(View.VISIBLE);
                txtProgress.setVisibility(View.VISIBLE);

                HashMap<String, List<FormDataInfo>> formViewsData = pfaFormSubmitUtil.getViewsData(menuFragParentLL, true);

                if (formSectionInfos != null && formSectionInfos.size() > 0) {

//                    If challan Items are added then all fields of Challan item Section are added to top Section received
                    if (formSectionInfos.size() > 1) {
                        formSectionInfo.getFields().addAll(formSectionInfos.get(1).getFields());
                    }
                    if (pfaFormSubmitUtil.isFormDataValid(sectionRequired, menuFragParentLL, true)) {
                        if (formSectionInfo != null && formSectionInfo.getFields() != null && formSectionInfo.getFields().size() > 0) {
                            for (int j = 0; j < formSectionInfo.getFields().size(); j++) {

                                if (formViewsData.containsKey(formSectionInfo.getFields().get(j).getField_name())) {
                                    if (formSectionInfo.getFields().get(j).getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.dropdown))) {
                                        formSectionInfo.getFields().get(j).setDefault_value(formViewsData.get(formSectionInfo.getFields().get(j).getField_name()).get(0).getKey());
                                    } else if (formSectionInfo.getFields().get(j).getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.radiogroup))) {
                                        formSectionInfo.getFields().get(j).setDefault_value(formViewsData.get(formSectionInfo.getFields().get(j).getField_name()).get(0).getKey());
                                    } else {

                                        formSectionInfo.getFields().get(j).setData(formViewsData.get(formSectionInfo.getFields().get(j).getField_name()));
                                    }
                                }
                            }

                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_DIALOG_ADD_ITEM_FORM_SECTION, formSectionInfo);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }


            }
        });

        Button noBtn = findViewById(R.id.noBtn);

        noBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                hideKeyBoard();
                finish();
            }
        });


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

    public void onClickBackImgBtn(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void updateDropdownViewsData(Bundle bundle) {

        customViewCreate.updateDropdownViewsData(bundle, menuFragParentLL, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("imagePath" , "onActivityResult = " + "localFormDialogActivity");

        switch (requestCode) {
            case CAPTURE_PHOTO:
                Log.d("imagePath" , "local form dialog");
                imageSelectionUtils.chooseFromCameraImgPath(data, null);
                break;

            case CHOOSE_FROM_GALLERY:
                imageSelectionUtils.chooseFromGalleryImgPath(data, null);
                break;

            case RECORD_VIDEO:
                imageSelectionUtils.chooseFromGalleryImgPath(data, null);
                break;

            case RC_DROPDOWN:
                updateDropdownViewsData(data.getExtras());
                break;
        }

    }
}
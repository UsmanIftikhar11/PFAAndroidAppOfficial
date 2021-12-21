package com.pfa.pfaapp.customviews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.httputils.PFAFormSubmitUtil;
import com.pfa.pfaapp.interfaces.DDSelectedCallback;
import com.pfa.pfaapp.interfaces.LocalFormsCallback;
import com.pfa.pfaapp.interfaces.PFAViewsCallbacks;
import com.pfa.pfaapp.interfaces.VideoFileCallback;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.models.FormSectionInfo;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.ImageSelectionUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.pfa.pfaapp.utils.AddInspectionUtils.IS_FAKE;
import static com.pfa.pfaapp.utils.AppConst.CAPTURE_PHOTO;
import static com.pfa.pfaapp.utils.AppConst.CHOOSE_FROM_GALLERY;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_ACTV_TAG;
import static com.pfa.pfaapp.utils.AppConst.OTHER_FILES;
import static com.pfa.pfaapp.utils.AppConst.RC_DROPDOWN;
import static com.pfa.pfaapp.utils.AppConst.RECORD_VIDEO;
public class LocalFormLL extends LinearLayout implements PFAViewsCallbacks{
    private PFAMenuInfo pfaMenuInfo;
    private LinearLayout menuFragParentLL;
    private ImageSelectionUtils imageSelectionUtils;
    BaseActivity mContext;
    CustomViewCreate customViewCreate;
    PFAFormSubmitUtil pfaFormSubmitUtil;
    private HashMap<String, HashMap<String, Boolean>> sectionRequired = new HashMap<>();
    DDSelectedCallback ddSelectedCallback;
    FormFieldsHideShow formFieldsHideShow;
    ArrayList<String> Import_Required_false = new ArrayList<>();
    public LocalFormLL(Context mContext) {
        super(mContext);
    }

    public LocalFormLL(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalFormLL(PFAMenuInfo pfaMenuInfo, BaseActivity mContext, DDSelectedCallback ddSelectedCallback) {
        super(mContext);
        this.ddSelectedCallback = ddSelectedCallback;
        this.mContext = mContext;

        formFieldsHideShow = new FormFieldsHideShow(mContext);

        pfaFormSubmitUtil = new PFAFormSubmitUtil(mContext);
        updateLayout(pfaMenuInfo);

    }

    public void updateLayout(PFAMenuInfo pfaMenuInfo) {
        this.pfaMenuInfo = pfaMenuInfo;
        populateData();
    }

    private void populateData() {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        inflate(getContext(), R.layout.fragment_menu_item, this);
        menuFragParentLL = findViewById(R.id.menuFragParentLL);
        setTag("" + pfaMenuInfo.getSlug());
        setId(pfaMenuInfo.getMenuItemID());
        customViewCreate = new CustomViewCreate(getContext(), this);
        customViewCreate.setDDCallback(ddSelectedCallback);

        if (pfaMenuInfo.getData() != null && pfaMenuInfo.getData().getForm() != null) {
            List<FormSectionInfo> formSectionInfos = pfaMenuInfo.getData().getForm();

            for (FormSectionInfo formSectionInfo : formSectionInfos) {
                customViewCreate.createViews(formSectionInfo, menuFragParentLL, sectionRequired, new PFAViewsCallbacks() {
                    @Override
                    public void showImagePickerDialog(CustomNetworkImageView view) {
//                        mContext.sharedPrefUtils.showToast("CustomNetworkImageView clicked for LocalFormLL");
                        Log.d("imagePath" , "image selection utils Local From LL");
                        imageSelectionUtils = new ImageSelectionUtils(mContext, view);
                        imageSelectionUtils.showImagePickerDialog(null, false, false);
                    }

                    @Override
                    public void showFilePickerDialog(CustomNetworkImageView view) {
                        Log.d("imagePath" , "image selection utils menu form fragment");
                        imageSelectionUtils = new ImageSelectionUtils(mContext, view);
                        imageSelectionUtils.showFilePickerDialog(null, false, false);
                    }

                    @Override
                    public void onLabelViewClicked(PFASectionTV pfaSectionTV) {

                    }

                    @Override
                    public void onButtonCLicked(View view) {

                    }

                    @Override
                    public void onClickGetCodeBtn(View view, VerifyFBOLayout verifyFBOLayout ) {

                    }

                    @Override
                    public void onDropdownItemSelected(FormDataInfo formDataInfo, String dataName) {

                    }
                }, false, (ScrollView) findViewById(R.id.fragMenuItemSV));
            }
        }

        mContext.sharedPrefUtils.printLog("LocalFormLL child count", "Count=>" + menuFragParentLL.getChildCount());

    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("imagePath" , "onActivityResult = " + "local form LL");

        if (resultCode != RESULT_OK) {
            Log.d("imagePath" , "Result = " + RESULT_OK + "  result received = " + resultCode);
            Log.d("imagePath" , "Result = " + requestCode);
            customViewCreate.clearFocusOfAllViews(menuFragParentLL);
            return;
        }
        switch (requestCode) {

            case CAPTURE_PHOTO:
                Log.d("imagePath" , "requestCode = " + requestCode);
                Log.d("imagePath" , "local form LL");
                imageSelectionUtils.chooseFromCameraImgPath(data, null);
//                Log.d("imagePath" , "local form LL URI = " + data.getData().getPath());
                break;

            case OTHER_FILES:
                imageSelectionUtils.chooseFromFilePath(data, null);
                break;

            case CHOOSE_FROM_GALLERY:
                Log.d("imagePath" , "gallery requestCode = " + requestCode);
                Log.d("imagePath" , "gallery local form LL");
                imageSelectionUtils.chooseFromGalleryImgPath(data, null);
                break;
            case RECORD_VIDEO:
                imageSelectionUtils.chooseFromGalleryImgPath(data, null);
                break;

            case RC_DROPDOWN:
                if (customViewCreate != null) {
                    Bundle bundle = data.getExtras();
                    customViewCreate.updateDropdownViewsData(bundle, menuFragParentLL, sectionRequired);

                    if (bundle != null && bundle.containsKey(EXTRA_ACTV_TAG)) {
                        String actvTag = bundle.getString(EXTRA_ACTV_TAG);
                        View pfaddactv = menuFragParentLL.findViewWithTag(actvTag);
                        if (pfaddactv instanceof PFADDACTV) {
                            PFADDACTV pfaddactv1 = (PFADDACTV) pfaddactv;

//                            Check values to set them required fields in inspection forms/ no required
                            if (pfaddactv1.formFieldInfo.getField_name() != null && (pfaddactv1.formFieldInfo.getCheck_value() != null)
                                    && (pfaddactv1.formFieldInfo.getCheck_value().size() > 0)) {

                                if (pfaddactv1.getSelectedValues() != null && pfaddactv1.getSelectedValues().size() > 0) {
                                    boolean isFake = false;
                                    IS_FAKE = false;
//                                    Import_Required_false.addAll(pfaddactv1.formFieldInfo.getRequired_false_fields());

                                    SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(mContext);
                                    if (pfaddactv1.formFieldInfo.getRequired_false_fields() != null){
                                    sharedPrefUtils.saveRLF((ArrayList<String>) pfaddactv1.formFieldInfo.getRequired_false_fields());
                                    }

                                    if (pfaddactv1.formFieldInfo.getCheck_value().contains(pfaddactv1.getSelectedValues().get(0).getValue())) {
                                        isFake = true;
                                        formFieldsHideShow.setFieldsRequired(pfaddactv1.formFieldInfo.getRequired_false_fields(),pfaddactv1.formFieldInfo.getCheck_value(), false, sectionRequired, menuFragParentLL);
                                    } else {
                                        formFieldsHideShow.setFieldsRequired(pfaddactv1.formFieldInfo.getRequired_false_fields(),pfaddactv1.formFieldInfo.getCheck_value(), true, sectionRequired, menuFragParentLL);
                                    }

//                                    SharedPreferences sharedPreferences = PreferenceManager
//                                            .getDefaultSharedPreferences(mContext);
                                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("pref", Context.MODE_PRIVATE);

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("IsNotRequired", isFake);
                                    editor.apply();

                                    if (pfaddactv1.formFieldInfo.getField_name().equalsIgnoreCase("recommendation")) {
                                        IS_FAKE = isFake;
                                    }
                                }
                            }

//                            Check fields to set them required and visible
                            if (pfaddactv1.formFieldInfo.getField_name() != null && (pfaddactv1.formFieldInfo.getShow_check_value() != null)
                                    && (pfaddactv1.formFieldInfo.getShow_check_value().size() > 0)) {

                                if (pfaddactv1.getSelectedValues() != null && pfaddactv1.getSelectedValues().size() > 0) {
                                    if (pfaddactv1.formFieldInfo.getShow_check_value().contains(pfaddactv1.getSelectedValues().get(0).getValue())) {
                                        formFieldsHideShow.setFieldsRequiredAndVisible(pfaddactv1.formFieldInfo.getShow_hidden_false_fields(), true, sectionRequired, menuFragParentLL, pfaddactv1.getSelectedValues().get(0).getValue());
                                    } else {
                                        formFieldsHideShow.setFieldsRequiredAndVisible(pfaddactv1.formFieldInfo.getShow_hidden_false_fields(), false, sectionRequired, menuFragParentLL, pfaddactv1.getSelectedValues().get(0).getValue());
                                    }

                                }
                            }
//                            Check fields to set them required and visible end
                        }
                    }
                }
                break;
        }
    }


    public void getPfaMenuInfo(LocalFormsCallback callback, boolean showError) {

        HashMap<String, List<FormDataInfo>> formViewsData = pfaFormSubmitUtil.getViewsData(menuFragParentLL, showError);

        Map<String, File> filesMap = pfaFormSubmitUtil.getFilesMap();

        if (pfaMenuInfo.getData() != null && pfaMenuInfo.getData().getForm() != null && pfaMenuInfo.getData().getForm().size() > 0) {
            for (int i = 0; i < pfaMenuInfo.getData().getForm().size(); i++) {
                FormSectionInfo formSectionInfo = pfaMenuInfo.getData().getForm().get(i);


                if (formSectionInfo.getFields() != null && formSectionInfo.getFields().size() > 0) {
                    for (int j = 0; j < formSectionInfo.getFields().size(); j++) {
                        String tempFieldName = formSectionInfo.getFields().get(j).getField_name();
                        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(mContext);
                        sharedPrefUtils.getRLF("RequiredFalseField");
                        sharedPrefUtils.saveSharedPrefValue("SLUG",pfaMenuInfo.getSlug());

                        if (formViewsData.containsKey(tempFieldName)) {

                            if (formSectionInfo.getFields().get(j).getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.dropdown)) && formViewsData.get(tempFieldName).size() > 0) {
                                formSectionInfo.getFields().get(j).setDefault_value(formViewsData.get(tempFieldName).get(0).getKey());


                            } else if (formSectionInfo.getFields().get(j).getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.radiogroup)) && formViewsData.get(tempFieldName).size() > 0) {
                                formSectionInfo.getFields().get(j).setDefault_value(formViewsData.get(tempFieldName).get(0).getKey());
                            }
                            ///////////////

                            else if (formSectionInfo.getFields().get(j).getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.checkbox)) && formViewsData.get(tempFieldName).size() > 0) {

//                                getCheckedBoxes
//                                formSectionInfo.getFields().get(j).setDefault_value(formViewsData.get(tempFieldName).get(0).getKey());
                                List<FormDataInfo> localDataInfos = new ArrayList<>();
                                for(FormDataInfo formDataInfo: formViewsData.get(tempFieldName))
                                {
                                    if(formDataInfo.isSelected())
                                    {
                                        localDataInfos.add(formDataInfo);
                                    }
                                }


                                formSectionInfo.getFields().get(j).setData(localDataInfos);
                            }
                            /////////////////
                            else {
                                formSectionInfo.getFields().get(j).setData(formViewsData.get(tempFieldName));
                            }

                            if (sectionRequired.get(formSectionInfo.getSection_id()) != null && sectionRequired.get(formSectionInfo.getSection_id()).size() > 0) {

                                if (sectionRequired.get(formSectionInfo.getSection_id()).containsKey(tempFieldName) && sectionRequired.get(formSectionInfo.getSection_id()).get(tempFieldName)) {
                                    formSectionInfo.getFields().get(j).setRequired(true);
                                } else {
                                    formSectionInfo.getFields().get(j).setRequired(false);
                                }
                            } else {
                                formSectionInfo.getFields().get(j).setRequired(formSectionInfo.getFields().get(j).isRequired());
                            }
                        }

                    }
                }
                pfaMenuInfo.getData().getForm().set(i, formSectionInfo);
            }
        }

        if (filesMap != null && filesMap.size() > 0) {
            pfaMenuInfo.setFilesMap(filesMap);

        }

        mContext.sharedPrefUtils.printLog("LocalFormLL child count", "getPfaMenuInfo Count=>" + menuFragParentLL.getChildCount());

        //        only to show the error messages on fields
        if (!pfaFormSubmitUtil.isFormDataValid(sectionRequired, menuFragParentLL, showError)) {
            callback.getPFAMenuInfo(pfaMenuInfo, true);
        } else {
            callback.getPFAMenuInfo(pfaMenuInfo, false);
        }

    }

    @Override
    public void showImagePickerDialog(CustomNetworkImageView view) {
        mContext.sharedPrefUtils.printLog("ImagePicker", "Image Picker");
        Log.d("imagePath" , "image selection utils local form ll 2");
        imageSelectionUtils = new ImageSelectionUtils(mContext, view);
        imageSelectionUtils.showImagePickerDialog(new VideoFileCallback() {
            @Override
            public void onFileSelected(String files) {

            }

            @Override
            public void videoSelected() {

            }
        }, false, false);
    }

    @Override
    public void showFilePickerDialog(CustomNetworkImageView view) {
        Log.d("imagePath" , "image selection utils menu form fragment");
        imageSelectionUtils = new ImageSelectionUtils(mContext, view);
        imageSelectionUtils.showFilePickerDialog(new VideoFileCallback() {
            @Override
            public void onFileSelected(String files) {

            }

            @Override
            public void videoSelected() {

            }
        }, false, false);
    }

    @Override
    public void onLabelViewClicked(PFASectionTV pfaSectionTV) {

    }

    @Override
    public void onButtonCLicked(View view) {

    }

    @Override
    public void onClickGetCodeBtn(View view, VerifyFBOLayout verifyFBOLayout ) {

    }

    @Override
    public void onDropdownItemSelected(FormDataInfo formDataInfo, String dataName) {

    }
}

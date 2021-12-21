package com.pfa.pfaapp.customviews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.adapters.LocalGridAdapter;
import com.pfa.pfaapp.interfaces.VideoFileCallback;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
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
import static com.pfa.pfaapp.utils.AppConst.CAPTURE_PHOTO;
import static com.pfa.pfaapp.utils.AppConst.CHOOSE_FROM_GALLERY;
import static com.pfa.pfaapp.utils.AppConst.OTHER_FILES;
import static com.pfa.pfaapp.utils.AppConst.RECORD_VIDEO;

public class LocalGridLL extends LinearLayout implements WhichItemClicked {
    private PFAMenuInfo pfaMenuInfo;
    ImageButton addGridItemBtn;
    GridView menuGL;
    ImageSelectionUtils imageSelectionUtils;

    SharedPrefUtils sharedPrefUtils;

    LocalGridAdapter localGridAdapter;
    BaseActivity mContext;

    boolean isImage;
    LinearLayout sorry_iv;
    Button saveFormBtn;

    public LocalGridLL(Context mContext) {
        super(mContext);
    }

    public LocalGridLL(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalGridLL(PFAMenuInfo pfaMenuInfo, BaseActivity mContext) {
        super(mContext);
        this.mContext = mContext;
        setPfaMenuInfo(pfaMenuInfo);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.fragment_menu_grid, this);
        menuGL = findViewById(R.id.menuGL);
        menuGL.setNumColumns(3);
        addGridItemBtn = findViewById(R.id.addGridItemBtn);
        addGridItemBtn.setVisibility(VISIBLE);
        sorry_iv = findViewById(R.id.sorry_iv);

        sharedPrefUtils = new SharedPrefUtils(getContext());
        addGridItemBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                addGridItem(null);

            }
        });

        if (pfaMenuInfo != null && pfaMenuInfo.getData().getAdd_new() != null) {
            addGridItemBtn.setVisibility(VISIBLE);
        } else {
            addGridItemBtn.setVisibility(GONE);
        }

        if (pfaMenuInfo != null && pfaMenuInfo.getData() != null && pfaMenuInfo.getData().getForm() != null && pfaMenuInfo.getData().getForm().size() > 0 &&
                pfaMenuInfo.getData().getForm().get(0).getFields() != null) {

            if (pfaMenuInfo.getData().getForm().size() == 1) {
                if (pfaMenuInfo.getData().getForm().get(0).getFields() != null && pfaMenuInfo.getData().getForm().get(0).getFields().size() > 0 && pfaMenuInfo.getData().getForm().get(0).getFields().get(0).getData() != null
                        && pfaMenuInfo.getData().getForm().get(0).getFields().get(0).getData().size() > 0 &&
                        pfaMenuInfo.getData().getForm().get(0).getFields().get(0).getData().get(0).getValue().equals("")) {

                    pfaMenuInfo.getData().getForm().clear();

                    List<FormSectionInfo> formSectionInfos = new ArrayList<>();
                    pfaMenuInfo.getData().setForm(formSectionInfos);

                }
                List<FormSectionInfo> formSectionInfos = new ArrayList<>();

                if (pfaMenuInfo.getData().getForm() == null || pfaMenuInfo.getData().getForm().size() == 0 || pfaMenuInfo.getData().getForm().get(0).getFields().size() == 0)
                    pfaMenuInfo.getData().setForm(formSectionInfos);
            }

            localGridAdapter = new LocalGridAdapter(mContext, pfaMenuInfo.getData().getForm(), this);

        } else {
            localGridAdapter = new LocalGridAdapter(mContext, new ArrayList<FormSectionInfo>(), this);
        }

        menuGL.setAdapter(localGridAdapter);

        showHideSorryImage();
    }

    private void addGridItem(String filePath) {

        final FormSectionInfo formSectionInfo = new FormSectionInfo();


        final FormFieldInfo formFieldInfo = new FormFieldInfo();
        formFieldInfo.setField_type(String.valueOf(AppUtils.FIELD_TYPE.imageView));
        formFieldInfo.setData_type(String.valueOf(AppUtils.FIELD_TYPE.imageView));
        formFieldInfo.setField_name(String.valueOf(AppUtils.FIELD_TYPE.mediaFormField));

        if (pfaMenuInfo != null && pfaMenuInfo.getData() != null && pfaMenuInfo.getData().getForm() != null) {

            formSectionInfo.setSection_id("" + (pfaMenuInfo.getData().getForm().size()));
            formSectionInfo.setSection_name("section" + (pfaMenuInfo.getData().getForm().size()));

            formFieldInfo.setOrder(pfaMenuInfo.getData().getForm().size());
        } else {
            formSectionInfo.setSection_id("0");
            formSectionInfo.setSection_name("section0");
            formFieldInfo.setOrder(0);
        }

        CustomNetworkImageView customNetworkImageView = new CustomNetworkImageView(getContext());
        customNetworkImageView.setTag(String.valueOf(AppUtils.FIELD_TYPE.mediaFormField));


        VideoFileCallback videoFileCallback = new VideoFileCallback() {
            @Override
            public void onFileSelected(String files) {
                if (files != null) {

                    List<FormDataInfo> imageData = new ArrayList<>();
                    FormDataInfo formDataInfo = new FormDataInfo();
                    formDataInfo.setName(String.valueOf(AppUtils.FIELD_TYPE.mediaFormField));
                    formDataInfo.setValue(files);
                    formDataInfo.setKey(files);

                    imageData.add(formDataInfo);
                    formFieldInfo.setData(imageData);

                    List<FormFieldInfo> formFieldInfos = new ArrayList<>();
                    formFieldInfos.add(formFieldInfo);
                    formSectionInfo.setFields(formFieldInfos);

                    if (pfaMenuInfo.getData().getForm() == null) {
                        pfaMenuInfo.getData().setForm(new ArrayList<FormSectionInfo>());
                    }

                    pfaMenuInfo.getData().getForm().add(formSectionInfo);
                    if (localGridAdapter != null) {
                        localGridAdapter.updateAdapter(pfaMenuInfo.getData().getForm());
                    }

                    showHideSorryImage();
                    if (mContext != null && saveFormBtn != null) {
                        saveFormBtn.setEnabled(true);
                        saveFormBtn.setClickable(true);
                        saveFormBtn.setFocusable(true);
                    }
                }
            }

            @Override
            public void videoSelected() {

                if (mContext != null && saveFormBtn != null) {
                    saveFormBtn.setEnabled(false);
                    saveFormBtn.setClickable(false);
                    saveFormBtn.setFocusable(false);
                }
            }
        };

        if (filePath != null) {
            Log.d("imagePath" , "image selection utils local grid LL1");
            ImageSelectionUtils imageSelectionUtils = new ImageSelectionUtils((Activity) getContext(), customNetworkImageView);
            imageSelectionUtils.chooseMultipleImages(filePath, videoFileCallback);
        } else {
            Log.d("imagePath" , "image selection utils local grid LL2");
            imageSelectionUtils = new ImageSelectionUtils((Activity) getContext(), customNetworkImageView);
            imageSelectionUtils.showImagePickerDialog(videoFileCallback, true, true);
        }
    }

    private void showHideSorryImage() {
        if (pfaMenuInfo.getData().getForm() == null || pfaMenuInfo.getData().getForm().size() == 0)
            sorry_iv.setVisibility(VISIBLE);
        else sorry_iv.setVisibility(GONE);
    }


    public PFAMenuInfo getPfaMenuInfo() {
        Map<String, File> filesMap = new HashMap<>();

        if (pfaMenuInfo != null && pfaMenuInfo.getData() != null && pfaMenuInfo.getData().getForm() != null) {
            for (int i = 0; i < pfaMenuInfo.getData().getForm().size(); i++) {
                FormSectionInfo formSectionInfo = pfaMenuInfo.getData().getForm().get(i);

                if (formSectionInfo.getFields() != null && formSectionInfo.getFields().size() > 0 && formSectionInfo.getFields().get(0).getData() != null
                        && formSectionInfo.getFields().get(0).getData().size() > 0 &&
                        (!formSectionInfo.getFields().get(0).getData().get(0).getValue().isEmpty())) {
                    if (formSectionInfo.getFields().get(0).getData().get(0).getValue() != null && (!formSectionInfo.getFields().get(0).getData().get(0).getValue().isEmpty()) &&
                            (!formSectionInfo.getFields().get(0).getData().get(0).getValue().startsWith("https://")))
                        filesMap.put(pfaMenuInfo.getSlug() + "/media[" + i + "]", new File(formSectionInfo.getFields().get(0).getData().get(0).getValue()));
                }
            }
        }
        assert pfaMenuInfo != null;
        pfaMenuInfo.setFilesMap(filesMap);

        return pfaMenuInfo;
    }

    public void setPfaMenuInfo(PFAMenuInfo pfaMenuInfo) {
        this.pfaMenuInfo = pfaMenuInfo;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("imagePath" , "onActivityResult = " + "localGridLL");

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case CAPTURE_PHOTO:
                Log.d("imagePath" , "local form LL Grid");
                isImage = true;
                imageSelectionUtils.chooseFromCameraImgPath(data, null);
                break;

            case OTHER_FILES:
                imageSelectionUtils.chooseFromFilePath(data, null);
                break;

            case CHOOSE_FROM_GALLERY:

                if (data.getData() != null)
                    isImage = !data.getData().toString().contains("video");
                imageSelectionUtils.chooseFromGalleryImgPath(data, null);
                break;
            case RECORD_VIDEO:
                isImage = false;
                imageSelectionUtils.chooseFromGalleryImgPath(data, null);
                break;

            case Constants.REQUEST_CODE:
                final ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                final Handler handler = new Handler();

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (images != null && i < images.size()) {
                            addGridItem(images.get(i).path);
                            if (i < (images.size() - 1)) {
                                i++;
                                handler.postDelayed(runnable, 1000);
                            } else {
                                handler.removeCallbacks(runnable);
                            }
                        }
                    }
                };
                i = 0;
                handler.post(runnable);

                break;
        }
    }

    int i = 0;
    Runnable runnable;

    @Override
    public void whichItemClicked(String position) {
        if (pfaMenuInfo != null && pfaMenuInfo.getData() != null && pfaMenuInfo.getData().getForm() != null && pfaMenuInfo.getData().getForm().get(0).getFields() != null) {

            pfaMenuInfo.getData().getForm().remove(Integer.parseInt(position));//.get(0).getFields().remove(Integer.parseInt(position));
            if (localGridAdapter != null)
                localGridAdapter.updateAdapter(pfaMenuInfo.getData().getForm());
            showHideSorryImage();
        }
    }

    @Override
    public void downloadInspection(String downloadUrl, int position) {

    }

    @Override
    public void deleteRecordAPICall(String deleteUrl, int position) {

    }
}

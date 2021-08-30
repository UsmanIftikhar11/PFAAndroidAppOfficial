package com.pfa.pfaapp.customviews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pfa.pfaapp.AppController;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.ImageGalleryActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.adapters.PFATableAdapter;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.models.FormSectionInfo;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.printing.PrinterActivity;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.ImageSelectionUtils;
import com.pfa.pfaapp.utils.LocalFormDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.pfa.pfaapp.utils.AppConst.CAPTURE_PHOTO;
import static com.pfa.pfaapp.utils.AppConst.CHOOSE_FROM_GALLERY;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_DELETE_IMAGE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_DIALOG_ADD_ITEM_FORM_SECTION;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_DOWNLOAD_URL;
import static com.pfa.pfaapp.utils.AppConst.RC_DELETE_IMAGE;
import static com.pfa.pfaapp.utils.AppConst.RC_DROPDOWN;
import static com.pfa.pfaapp.utils.AppConst.RECORD_VIDEO;
import static com.pfa.pfaapp.utils.AppConst.REQ_CODE_ADD_ITEM;

public class LocalListLL extends LinearLayout implements WhichItemClicked {

    private PFAMenuInfo pfaMenuInfo;
    private boolean conducted_inspection;
    ListView localFormLV;
    PFATableAdapter localListAdapter;

    BaseActivity baseActivity;
    boolean isImage;

    JSONObject localSectionJSONObject;
    List<List<PFATableInfo>> pfaTableInfos = new ArrayList<>();

    ImageButton addNewBtn;
    LinearLayout sorry_iv;
    String add_newUrl;
    ImageButton print_icon;

    LocalFormDialog localFormDialog;

    CircularImageView addProofCNIV;
    ImageButton deleteProofImgBtn, addLLImgBtn;
    ImageView printbutton;


    ImageSelectionUtils imageSelectionUtils;

    //     form for adding new view to list
    FormSectionInfo tempFormSection;

    public LocalListLL(Context mContext) {
        super(mContext);
    }

    public LocalListLL(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalListLL(PFAMenuInfo pfaMenuInfo,boolean conducted_inspection, BaseActivity mContext) {
        super(mContext);
        this.baseActivity = mContext;
        setPfaMenuInfo(pfaMenuInfo);
        setboolean(conducted_inspection);
        init();
    }

    private void setboolean(boolean conducted_inspection) {
        this.conducted_inspection = conducted_inspection;

    }


    private void init() {
        inflate(getContext(), R.layout.local_form_list, this);
        setClickable(true);


        add_newUrl = pfaMenuInfo.getAPI_URL();
        addNewBtn = findViewById(R.id.addNewBtn);

        localFormLV = findViewById(R.id.localFormLV);
        sorry_iv = findViewById(R.id.sorry_iv);

        addLLImgBtn = findViewById(R.id.addLLImgBtn);
        printbutton = findViewById(R.id.printBtn);
        printbutton.setVisibility(GONE);

        addProofCNIV = findViewById(R.id.addProofCNIV);

        addProofCNIV.setTag(pfaMenuInfo.getSlug());

        deleteProofImgBtn = findViewById(R.id.deleteProofImgBtn);

        deleteProofImgBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addProofCNIV.setVisibility(GONE);
                addLLImgBtn.setImageResource(R.mipmap.proof_camera);
                addProofCNIV.setImageFile(null);
                addProofCNIV.setLocalImageBitmap(null);
                addLLImgBtn.setVisibility(VISIBLE);
            }
        });

        addProofCNIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_DOWNLOAD_URL, addProofCNIV.getImageFile().getAbsolutePath());
                bundle.putString(EXTRA_DELETE_IMAGE, EXTRA_DELETE_IMAGE);
                baseActivity.sharedPrefUtils.startActivityForResult(baseActivity, ImageGalleryActivity.class, bundle, RC_DELETE_IMAGE);
            }
        });

        addNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocalAddItemDialog();

            }
        });

        if (baseActivity.httpService.isNetworkDisconnected()) {

            if (pfaMenuInfo.getLocalSectionJSONObject() != null) {
                try {
                    localSectionJSONObject = new JSONObject(pfaMenuInfo.getLocalSectionJSONObject());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
            baseActivity.httpService.getListsData(pfaMenuInfo.getData().getAdd_new(), new HashMap<String, String>(), new HttpResponseCallback() {
                @Override
                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                    if (response != null && response.optBoolean("status")) {
                        try {
                            JSONObject daJsonObject = response.getJSONObject("data");
                            JSONArray formJsonArray = daJsonObject.getJSONArray("form");
                            if (formJsonArray.length() > 0) {
                                localSectionJSONObject = formJsonArray.getJSONObject(0);
                                if (localSectionJSONObject != null)
                                    pfaMenuInfo.setLocalSectionJSONObject1(localSectionJSONObject.toString());
                            }

                        } catch (JSONException e) {
                            baseActivity.sharedPrefUtils.printStackTrace(e);
                        }
                    }
                }
            }, false);
        }


        if (pfaMenuInfo.getData().getForm() != null) {

            if (pfaMenuInfo.getData().getAdd_proof() > 0) {

                addLLImgBtn.setVisibility(VISIBLE);

                addLLImgBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (addProofCNIV != null && addProofCNIV.getImageFile() != null) {
                            addProofCNIV.performClick();
                        } else {
                            imageSelectionUtils = new ImageSelectionUtils(baseActivity, addProofCNIV);
                            imageSelectionUtils.showImagePickerDialog(null, false, false);
                        }
                    }
                });
            }

//             Remove first empty section
            if (pfaMenuInfo.getData().getForm() != null && pfaMenuInfo.getData().getForm().size() == 1 && (pfaMenuInfo.getData().getForm().get(0).getFields() == null || pfaMenuInfo.getData().getForm().get(0).getFields().size() == 0)) {
                pfaMenuInfo.getData().getForm().clear();
            }
            populateList();
        }

        if (pfaMenuInfo.getMenuItemImg() != null && (!pfaMenuInfo.getMenuItemImg().isEmpty())) {

            if (pfaMenuInfo.getMenuItemImg().startsWith("http")) {

                // add image at footer of List
                LayoutInflater inflater = LayoutInflater.from(getContext());
                ViewGroup proof_list_footer = (ViewGroup) inflater.inflate(R.layout.proof_list_footer, localFormLV, false);

                localFormLV.addFooterView(proof_list_footer);
                CustomNetworkImageView proofNIV = proof_list_footer.findViewById(R.id.proofNIV);

                proofNIV.setImageUrl(pfaMenuInfo.getMenuItemImg(), AppController.getInstance().getImageLoader());

                proofNIV.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(EXTRA_DOWNLOAD_URL, pfaMenuInfo.getMenuItemImg());
                        baseActivity.sharedPrefUtils.startNewActivity(ImageGalleryActivity.class, bundle, false);
                    }
                });

            } else {
                AppConst.IMAGE_SELECTION_MAP.put(addProofCNIV.getTag().toString(), pfaMenuInfo.getMenuItemImg());
                addProofCNIV.setVisibility(VISIBLE);
                addLLImgBtn.setImageResource(0);

                addProofCNIV.setFileBitmap(pfaMenuInfo.getMenuItemImg());
            }
        }
    }


    private void populateList() {

        if (pfaTableInfos != null)
            pfaTableInfos.clear();
        Map<String, File> filesMap = new HashMap<>();
        if (pfaMenuInfo != null && pfaMenuInfo.getData() != null && pfaMenuInfo.getData().getForm() != null) {

            for (int secNum = 0; secNum < pfaMenuInfo.getData().getForm().size(); secNum++) {


                FormSectionInfo formSectionInfo = pfaMenuInfo.getData().getForm().get(secNum);
                List<PFATableInfo> tableInfos = new ArrayList<>();
                if (formSectionInfo.getFields() != null && formSectionInfo.getFields().size() > 0) {



                    int fieldNum = -1;
                    for (FormFieldInfo formFieldInfo : formSectionInfo.getFields()) {
                        PFATableInfo pfaTableInfo = new PFATableInfo();
                        pfaTableInfo.setField_type(formFieldInfo.getField_type());
                        pfaTableInfo.setOrder(formFieldInfo.getOrder());
                        pfaTableInfo.setValue(formFieldInfo.getValue());
                        pfaTableInfo.setPrintData(formFieldInfo.getPrintData());
                        pfaTableInfo.setPrintHtmlStr(formFieldInfo.getPrintHtmlStr());
                        pfaTableInfo.setShareHtmlStr(formFieldInfo.getShareHtmlStr());

                        pfaTableInfo.setAPI_URL(formFieldInfo.getAPI_URL());
                        pfaTableInfo.setField_name(formFieldInfo.getField_name());
                        pfaTableInfo.setInvisible(formFieldInfo.isInvisible());
                        pfaTableInfo.setFont_color(formFieldInfo.getFont_color());
                        pfaTableInfo.setFont_size(formFieldInfo.getFont_size());
                        pfaTableInfo.setFont_style(formFieldInfo.getFont_style());
                        pfaTableInfo.setDirection(formFieldInfo.getDirection());

                        if (formFieldInfo.getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.imageView))) {
                            if (formFieldInfo.getData() != null && formFieldInfo.getData().size() > 0) {
                                fieldNum++;
                                filesMap.put(pfaMenuInfo.getSlug() + "/media[" + secNum + "][" + fieldNum + "]", new File(formFieldInfo.getData().get(0).getKey()));
                                pfaTableInfo.setIcon(formFieldInfo.getData().get(0).getKey());
                            }

                        } else {
                            pfaTableInfo.setIcon(formFieldInfo.getIcon());
                        }
                        /////////////
                        if (formFieldInfo.getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.dropdown)) || formFieldInfo.getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.radiogroup))) {

                            String fieldType = formFieldInfo.getField_type();
                            if (fieldType.equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.dropdown))) {

                                for (int x = 0; x < formFieldInfo.getData().size(); x++) {
                                    if (formFieldInfo.getData().get(x).getKey().equalsIgnoreCase(formFieldInfo.getDefault_value())) {
                                        pfaTableInfo.setData("" + formFieldInfo.getData().get(x).getValue());
                                    }
                                }

                            } else
                                pfaTableInfo.setData(formFieldInfo.getDefault_value());

                        } else {
                            if (formFieldInfo.getData() != null && formFieldInfo.getData().size() > 0) {
                                pfaTableInfo.setData("" + formFieldInfo.getData().get(0).getKey());
                            }
                        }
                        tableInfos.add(pfaTableInfo);
                    }
                    pfaTableInfos.add(tableInfos);
                }
            }
            FormFieldInfo formFieldInfo = new FormFieldInfo();

            pfaMenuInfo.setPrintData(formFieldInfo.getPrintData());
            if (localListAdapter == null) {
                String sec_name = pfaMenuInfo.getMenuItemName();


//                String print = pfaMenuInfo.getData().getPrintData();
//                String share = pfaMenuInfo.getData().getShareHtmlStr();
                String print_data =  pfaMenuInfo.getData().getPrintData();
                localListAdapter = new PFATableAdapter(baseActivity,print_data,printbutton, sec_name, conducted_inspection,pfaTableInfos, pfaMenuInfo.getData().isShowDeleteIcon(), this);
                localFormLV.setAdapter(localListAdapter);
                localListAdapter.notifyDataSetChanged();
            } else {
                localListAdapter.updateAdapter(pfaTableInfos);
            }
        }



        if (pfaMenuInfo != null && pfaMenuInfo.getData() != null && pfaMenuInfo.getData().getAdd_new() != null) {
            pfaMenuInfo.setFilesMap(filesMap);

            if (pfaMenuInfo.getData().getItem_count() != 0 && (pfaMenuInfo.getData().getItem_count() == pfaTableInfos.size())) {
                addNewBtn.setVisibility(GONE);
            } else {
                addNewBtn.setVisibility(VISIBLE);
            }

        } else {
            addNewBtn.setVisibility(GONE);
        }

        if ((pfaMenuInfo.getMenuItemImg() == null || pfaMenuInfo.getMenuItemImg().isEmpty()) && (pfaTableInfos == null || pfaTableInfos.size() == 0))
            sorry_iv.setVisibility(VISIBLE);
        else sorry_iv.setVisibility(GONE);

        if (addProofCNIV.getVisibility() == VISIBLE) {
            if (pfaMenuInfo.getData().getProofImagePath() != null) {
                addProofCNIV.setFileBitmap(pfaMenuInfo.getData().getProofImagePath());
            }
        }
    }

    public PFAMenuInfo getPfaMenuInfo() {
        if (addProofCNIV.getVisibility() == VISIBLE) {
            if (addProofCNIV.getImageFile() != null) {
                pfaMenuInfo.getData().setProofImagePath(addProofCNIV.getImageFile().getAbsolutePath());
                pfaMenuInfo.setMenuItemImg(addProofCNIV.getImageFile().getAbsolutePath());
            }
        } else {
            pfaMenuInfo.getData().setProofImagePath(null);
        }
        return pfaMenuInfo;
    }

    public void setPfaMenuInfo(PFAMenuInfo pfaMenuInfo) {
        this.pfaMenuInfo = pfaMenuInfo;
    }

    @Override
    public void whichItemClicked(String position) {
        if (pfaMenuInfo != null && pfaMenuInfo.getData() != null && pfaMenuInfo.getData().getForm() != null && pfaMenuInfo.getData().getForm().get(0).getFields() != null) {

            pfaMenuInfo.getData().getForm().remove(Integer.parseInt(position));
            populateList();
        }
    }

    @Override
    public void downloadInspection(String downloadUrl, int position) {

    }

    @Override
    public void deleteRecordAPICall(String deleteUrl, int position) {

    }

    private void showLocalAddItemDialog() {

        localFormDialog = new LocalFormDialog(baseActivity);

        if (localSectionJSONObject != null) {
            tempFormSection = new Gson().fromJson(localSectionJSONObject.toString(), FormSectionInfo.class);

            tempFormSection.setSection_id("" + pfaMenuInfo.getData().getForm().size());

            if (tempFormSection.getFields().get(0).getData() != null && tempFormSection.getFields().get(0).getData().size() > 0) {
                tempFormSection.getFields().get(0).getData().clear();
            }

            localFormDialog.addFormItem(tempFormSection);
        } else {
            baseActivity.sharedPrefUtils.showMsgDialog("Add Item form empty!", null);
        }
    }

    public void addFormSection(FormSectionInfo formSectionInfo) {
        if (formSectionInfo != null) {
            pfaMenuInfo.getData().getForm().add(formSectionInfo);
            populateList();
            baseActivity.hideKeyBoard();
        }

        localFormDialog = null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        SendMessageCallback sendMessageCallback = new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {

                if (addProofCNIV.getImageFile() != null) {
                    addProofCNIV.setVisibility(VISIBLE);
                    addLLImgBtn.setImageResource(0);
                }
            }
        };
        switch (requestCode) {
            case CAPTURE_PHOTO:

                isImage = true;

                if (localFormDialog != null && localFormDialog.imageSelectionUtils != null)
                    localFormDialog.imageSelectionUtils.chooseFromCameraImgPath(data, sendMessageCallback);
                else if (imageSelectionUtils != null) {
                    imageSelectionUtils.chooseFromCameraImgPath(data, sendMessageCallback);
                }

                break;
            case CHOOSE_FROM_GALLERY:
                isImage = true;

                if (localFormDialog != null && localFormDialog.imageSelectionUtils != null)
                    localFormDialog.imageSelectionUtils.chooseFromGalleryImgPath(data, sendMessageCallback);
                else if (imageSelectionUtils != null) {
                    imageSelectionUtils.chooseFromGalleryImgPath(data, sendMessageCallback);
                }

                break;
            case RECORD_VIDEO:
                isImage = false;
                if (localFormDialog != null && localFormDialog.imageSelectionUtils != null)
                    localFormDialog.imageSelectionUtils.chooseFromGalleryImgPath(data, null);
                else if (imageSelectionUtils != null) {
                    imageSelectionUtils.chooseFromGalleryImgPath(data, null);
                }
                break;
            case RC_DELETE_IMAGE:
                if (resultCode == RESULT_OK)
                    deleteProofImgBtn.performClick();
                break;
            case RC_DROPDOWN:
                localFormDialog.updateDropdownViewsData(data.getExtras());
                break;
            case REQ_CODE_ADD_ITEM:
                if (data != null && data.getExtras() != null && data.getExtras().size() > 0) {
                    addFormSection((FormSectionInfo) data.getExtras().getSerializable(EXTRA_DIALOG_ADD_ITEM_FORM_SECTION));
                } else {
                    baseActivity.sharedPrefUtils.printLog("EXTRA_DIALOG_ADD_ITEM_FORM_SECTION", "EXTRA_DIALOG_ADD_ITEM_FORM_SECTION Null");
                }
                break;
            default:

                if (localFormDialog != null && data != null) {
                    localFormDialog.customViewCreate.updateDropdownViewsData(data.getExtras(), localFormDialog.menuFragParentLL, null);
                    break;
                }

        }

    }


}

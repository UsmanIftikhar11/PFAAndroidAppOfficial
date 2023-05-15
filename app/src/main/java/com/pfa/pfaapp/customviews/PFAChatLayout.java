package com.pfa.pfaapp.customviews;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.Nullable;

import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.adapters.ImagesGridAdapter;
import com.pfa.pfaapp.adapters.PFATableAdapter;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.PFAViewsCallbacks;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.models.FormSectionInfo;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;
import com.rey.material.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PFAChatLayout extends LinearLayout implements HttpResponseCallback, WhichItemClicked {

    private ListView msgsLV;
    private EditText msgET;
    private EditText inspectionET;
    private TextView chatTtlTV;
    private HttpService httpService;
    private SharedPrefUtils sharedPrefUtils;
    private PFATableAdapter pfaTableAdapter;
    List<List<PFATableInfo>> chatTableData = new ArrayList<>();
    private LinearLayout chatBoxLL;
    private String urlToCall;
    private JSONObject chat_sectionJsonObject;
    JSONObject status_sectionObject;
    LinearLayout statusSectionLL;
    FormSectionInfo formSectionInfo;
    public CustomViewCreate customViewCreate;
    RelativeLayout detailRL;
    FrameLayout chatDetailFL;

    ViewGroup chatHeaderLL;
    ViewGroup chatFooterLL;
    private boolean addComment;

    GridView imageGView;

//    FormDataInfo selectedStatusDataInfo;

    HashMap<String, FormDataInfo> selectedFormDataMap = new HashMap<>();
    List<PFATableInfo> imagesList;

    /**
     * Constructor:
     *
     * @param mContext               {@link Context}
     * @param chat_sectionJsonObject {@link JSONObject}
     * @param urlToCall              {@link String}
     */
    public PFAChatLayout(Context mContext, JSONObject chat_sectionJsonObject, String urlToCall, JSONObject status_sectionObject, RelativeLayout detailRL, List<PFATableInfo> imagesList) {
        super(mContext);
        this.detailRL = detailRL;
        this.chat_sectionJsonObject = chat_sectionJsonObject;
        this.status_sectionObject = status_sectionObject;
        this.imagesList = imagesList;
        if (status_sectionObject != null) {
            formSectionInfo = new Gson().fromJson(status_sectionObject.toString(), FormSectionInfo.class);
        }

//        if(forward_sectionObject!=null)
//        {
//            forward_sectionFormSectionInfo = new Gson().fromJson(forward_sectionObject.toString(), FormSectionInfo.class);
//        }

        this.urlToCall = urlToCall;
        init();
    }

    /**
     * this is a default constructor always required for custom view that we can add in xml layout otherwise it gives error for initialization with attributes param
     *
     * @param context {@link Context}
     * @param attrs   {@link AttributeSet}
     */
    public PFAChatLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Initialize the chat params
     */
    private void init() {
        selectedFormDataMap.clear();
        inflate(getContext(), R.layout.pfa_chat_ll, this);
        sharedPrefUtils = new SharedPrefUtils(getContext());
        httpService = new HttpService(getContext());

        msgsLV = findViewById(R.id.msgsLV);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        chatHeaderLL = (ViewGroup) inflater.inflate(R.layout.pfa_chat_header, msgsLV, false);

        chatTtlTV = chatHeaderLL.findViewById(R.id.chatTtlTV);
        chatDetailFL = chatHeaderLL.findViewById(R.id.chatDetailFL);

        chatDetailFL.addView(detailRL);
        imageGView = chatHeaderLL.findViewById(R.id.imageGView);

        if (imagesList != null && imagesList.size() > 0) {

//             set the height of images gridview based on the # of images. Imageview height is 100dp and in each row there can be shown only 3 images
            int gridCount = imagesList.size() / 3;
            if ((imagesList.size() % 3) > 0)
                gridCount += 1;
            int gridheight = sharedPrefUtils.convertDpToPixel(gridCount * 100);
            ViewGroup.LayoutParams layoutParams = imageGView.getLayoutParams();
            layoutParams.height = gridheight; //this is in pixels
            imageGView.setLayoutParams(layoutParams);


            ImagesGridAdapter imagesGridAdapter = new ImagesGridAdapter((BaseActivity) getContext(), imagesList);
            imageGView.setAdapter(imagesGridAdapter);
        } else
            imageGView.setVisibility(GONE);


        msgsLV.addHeaderView(chatHeaderLL, null, false);

        chatFooterLL = (ViewGroup) inflater.inflate(R.layout.pfa_chat_footer, msgsLV, false);

        chatBoxLL = chatFooterLL.findViewById(R.id.chatBoxLL);
        statusSectionLL = chatFooterLL.findViewById(R.id.statusSectionLL);

        msgET = chatFooterLL.findViewById(R.id.msgET);
        inspectionET = chatFooterLL.findViewById(R.id.inspectionET);
        sharedPrefUtils.applyFont(msgET, AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(inspectionET, AppUtils.FONTS.HelveticaNeue);

        inspectionET.setInputType(InputType.TYPE_CLASS_NUMBER);


        sharedPrefUtils.applyFont(chatTtlTV, AppUtils.FONTS.HelveticaNeueBold);

        if (!chat_sectionJsonObject.optBoolean("hide_footer")) {
            msgsLV.addFooterView(chatFooterLL, null, false);
        }

        (chatFooterLL.findViewById(R.id.sendBtn)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFormDataMap == null && formSectionInfo.getFields() != null && formSectionInfo.getFields().size() > 0) {
                    httpService.showMsgDialog("Please select the status!", null);
                    return;
                }

                if (!msgET.getText().toString().isEmpty() || !inspectionET.getText().toString().isEmpty() || (!("" + (selectedFormDataMap.get("forwarded_to") == null ? "" : selectedFormDataMap.get("forwarded_to").getValue())).equalsIgnoreCase("Operations"))) {
                    // Select the last row so it will scroll into view...
                    if (pfaTableAdapter.getCount() > 0)
                        msgsLV.setSelection(pfaTableAdapter.getCount() - 1);

                    HashMap<String, String> reqParams = new HashMap<>();
                    reqParams.put("message", msgET.getText().toString());
                    reqParams.put("inspection_id", inspectionET.getText().toString());

                    if (selectedFormDataMap.size() > 0) {
                        for (String key : selectedFormDataMap.keySet())
                            reqParams.put(key, "" + selectedFormDataMap.get(key).getKey());
                    }
//                    if (selectedStatusDataInfo != null)
//                        reqParams.put("status", "" + selectedStatusDataInfo.getKey());

                    HttpService httpService = new HttpService(getContext());

                    httpService.formSubmit(reqParams, null, chat_sectionJsonObject.optString("API_URL"), new HttpResponseCallback() {
                        @Override
                        public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                            if (response != null) {
                                if (response.optBoolean("status")) {
                                    Log.d("redirectOnListings" , "redirectOnListings = " + response.optBoolean("redirectOnListings"));
//                                    if ((!("" + (selectedFormDataMap.get("forwarded_to") == null ? "" : selectedFormDataMap.get("forwarded_to").getValue())).equalsIgnoreCase("Operations"))) {
                                    if (response.optBoolean("redirectOnListings")) {

                                        ((Activity) getContext()).finish();
                                        return;
                                    }
                                    msgET.setText("");
                                    inspectionET.setText("");
                                    callChatUrl();
                                }
                            }
                        }
                    }, true, null);


                } else {
                    msgET.setError("Please enter comment!");
                    inspectionET.setError("Please enter Inspection Id");
                }
            }
        });

        populateData();
    }

    private void callChatUrl() {
        httpService.fetchConfigData(urlToCall, this);
    }

    private void populateData() {

        if (chat_sectionJsonObject != null) {
            Type chatDataType = new TypeToken<List<List<PFATableInfo>>>() {
            }.getType();

            if (chat_sectionJsonObject.optBoolean("showCommentBox")) {
                chatBoxLL.setVisibility(VISIBLE);
            } else {
                chatBoxLL.setVisibility(GONE);
            }

            chatTtlTV.setText(chat_sectionJsonObject.optString("section_name"));
            if (chat_sectionJsonObject.has("fields")) {
                if (chatTableData != null)
                    chatTableData.clear();

                List<List<PFATableInfo>> chatTableDataLocal = new GsonBuilder().create().fromJson(chat_sectionJsonObject.optJSONArray("fields").toString(), chatDataType);
                chatTableData.addAll(chatTableDataLocal);

                if (pfaTableAdapter != null) {
                    pfaTableAdapter.updateAdapter(chatTableData);
                } else {
                    pfaTableAdapter = new PFATableAdapter((BaseActivity) getContext(), chatTableData, false, this);
                }

                msgsLV.setAdapter(pfaTableAdapter);
                pfaTableAdapter.notifyDataSetChanged();

                if (addComment)
                    msgsLV.setSelection(pfaTableAdapter.getCount() - 1);
                else
                    addComment = true;
            }
        } else {
            chatBoxLL.setVisibility(GONE);
        }

        if (formSectionInfo != null && formSectionInfo.getFields() != null && formSectionInfo.getFields().size() > 0) {
//             set the default value by default
            if (formSectionInfo.getFields().size() > 0) {

                for (FormFieldInfo formFieldInfo : formSectionInfo.getFields()) {
//                   FormFieldInfo formFieldInfo = formSectionInfo.getFields().get(0);
                    if (formFieldInfo != null && formFieldInfo.getData().size() > 0) {
                        for (int i = 0; i < formFieldInfo.getData().size(); i++) {

                            if (formFieldInfo.getDefault_value() != null && (!formFieldInfo.getDefault_value().isEmpty()) && formFieldInfo.getDefault_value().equalsIgnoreCase(formFieldInfo.getData().get(i).getValue())) {

                                selectedFormDataMap.put(formFieldInfo.getData().get(i).getName(), formFieldInfo.getData().get(i));
//                            selectedStatusDataInfo = formFieldInfo.getData().get(i);
                            }
                        }
                    }
                }
            }
            customViewCreate = new CustomViewCreate(getContext(), new PFAViewsCallbacks() {
                @Override
                public void showImagePickerDialog(CustomNetworkImageView view) {
                }
                @Override
                public void showFilePickerDialog(CustomNetworkImageView view) {
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

                    if (formDataInfo != null)
                        selectedFormDataMap.put(formDataInfo.getName(), formDataInfo);

                    if(dataName!=null && formDataInfo==null)
                        selectedFormDataMap.remove(dataName);
//                    selectedStatusDataInfo = formDataInfo;
                }
            });
            statusSectionLL.removeAllViews();

            customViewCreate.createViews(formSectionInfo, statusSectionLL, new HashMap<String, HashMap<String, Boolean>>(), null, false, null);

//            if(forward_sectionFormSectionInfo!=null)
//            {
//                customViewCreate.createViews(forward_sectionFormSectionInfo, statusSectionLL, new HashMap<String, HashMap<String, Boolean>>(), null, false, null);
//            }
        } else {
            statusSectionLL.removeAllViews();
        }
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        if (response != null && response.optBoolean("status")) {
            try {

//                selectedStatusDataInfo = null;
                selectedFormDataMap.clear();
                ///////////
                if (response.has("chat_section")) {
                    chat_sectionJsonObject = response.getJSONObject("chat_section");
                }

                if (response.has("status_section")) {
                    status_sectionObject = response.getJSONObject("status_section");
                    formSectionInfo = new Gson().fromJson(status_sectionObject.toString(), FormSectionInfo.class);
                }

                //                    Testing the chat view
                if (chat_sectionJsonObject != null || status_sectionObject != null) {
                    populateData();
                }

            } catch (JSONException e) {
                sharedPrefUtils.printStackTrace(e);
            }


        }
    }

    @Override
    public void whichItemClicked(String id) {
    }

    @Override
    public void downloadInspection(String downloadUrl, int position) {
    }

    @Override
    public void deleteRecordAPICall(String deleteUrl, int position) {

    }
}

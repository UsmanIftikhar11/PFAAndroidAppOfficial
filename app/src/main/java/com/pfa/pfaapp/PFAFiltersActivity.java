package com.pfa.pfaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.customviews.CustomNetworkImageView;
import com.pfa.pfaapp.customviews.CustomViewCreate;
import com.pfa.pfaapp.customviews.PFAButton;
import com.pfa.pfaapp.customviews.PFALocationACTV;
import com.pfa.pfaapp.customviews.PFASectionTV;
import com.pfa.pfaapp.customviews.VerifyFBOLayout;
import com.pfa.pfaapp.httputils.PFAFormSubmitUtil;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.PFAViewsCallbacks;
import com.pfa.pfaapp.models.AddressObjInfo;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.models.FormSectionInfo;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.ImageSelectionUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import static com.pfa.pfaapp.utils.AppConst.DO_REFRESH;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_ACTIVITY_TITLE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_FILTERS_DATA;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_FORM_SECTION_LIST;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_ITEM_COUNT;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_SEARCH_FRAGMENT;
import static com.pfa.pfaapp.utils.AppConst.RC_DROPDOWN;
import static com.pfa.pfaapp.utils.AppConst.SEARCH_DATA;
import static com.pfa.pfaapp.utils.AppConst.SUB_TOWN_TAG;
import static com.pfa.pfaapp.utils.AppConst.TOWN_TAG;

public class PFAFiltersActivity extends BaseActivity implements HttpResponseCallback, PFAViewsCallbacks {
    List<FormSectionInfo> formSectionInfos;
    Bundle bundle;
    CustomViewCreate customViewCreate;
    HashMap<String, List<FormDataInfo>> formViewsData = new HashMap<>(); // key is fieldname, values are list of all selected values
    private HashMap<String, HashMap<String, Boolean>> sectionRequired = new HashMap<>();
    LinearLayout filtersLL;
    ImageSelectionUtils imageSelectionUtils;
    HashMap<String, Boolean> reqViews = new HashMap<>();
    HashMap<String, List<FormDataInfo>> formFilteredData;
    List<List<PFATableInfo>> tableData;
    JSONArray jsonArrayTableData;
    String activityTitle;
    private String nextUrl;
    private String itemCount;
    private String searchFragment;

    PFAFormSubmitUtil pfaFormSubmitUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        setupWindowAnimations((ViewGroup) findViewById(R.id.viewGroup));

        filtersLL = findViewById(R.id.filtersLL);
        findViewById(R.id.clearFilterBtn).setVisibility(View.VISIBLE);

        pfaFormSubmitUtil = new PFAFormSubmitUtil(this);
        pfaFormSubmitUtil.init(filtersLL, reqViews);
        bundle = getIntent().getExtras();

        Log.d("onCreateActv", "PFAFiltersActivity");

        if (bundle != null) {
            setTitle(bundle.getString(EXTRA_ACTIVITY_TITLE), true);

            if (bundle.containsKey(EXTRA_FILTERS_DATA)) {
                formViewsData = (HashMap<String, List<FormDataInfo>>) bundle.getSerializable(EXTRA_FILTERS_DATA);
            }if (bundle.containsKey(EXTRA_SEARCH_FRAGMENT)) {
                searchFragment = bundle.getString(EXTRA_SEARCH_FRAGMENT);
            }

            if (bundle.containsKey(EXTRA_FORM_SECTION_LIST)) {
                formSectionInfos = (List<FormSectionInfo>) bundle.getSerializable(EXTRA_FORM_SECTION_LIST);
                createFiltersView();
            }
        }

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        getConfirmation();
//                    }
//                });
//            }
//        }, 1500);

    }

//    private void getConfirmation() {
//        String pincode = "";
//        String userId = "";
//        if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN) != null) {
//            userId = sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID);
//        }
//        pincode = sharedPrefUtils.getSharedPrefValue(SP_SECURITY_CODE);
//        httpService.getUserConfirmation(userId, pincode, new HttpResponseCallback() {
//            @Override
//            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
//                if (response!= null)
//                {
//                    try {
//                        String status = response.getString("status");
//                        if (status == "false"){
//                            if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN) != null) {
//                                sharedPrefUtils.logoutFromApp(httpService);
//                                Toast.makeText(PFAFiltersActivity.this, "Unauthentic User", Toast.LENGTH_SHORT).show();
//                            } else {
//                                sharedPrefUtils.startNewActivity(LoginActivity.class, null, false);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }

    private void createFiltersView() {
        customViewCreate = new CustomViewCreate(PFAFiltersActivity.this, PFAFiltersActivity.this, formViewsData);
        populateData();
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
    }

    private void populateData() {

        if (filtersLL != null && filtersLL.getChildCount() > 0) {
            filtersLL.removeAllViews();
        }

        customViewCreate.setSearchFilter(true);
//        if more than one section then do decision based on that accordingly

        if (formSectionInfos != null && formSectionInfos.size() > 0)
            for (FormSectionInfo formSectionInfo : formSectionInfos) {
                customViewCreate.createViews(formSectionInfo, filtersLL, sectionRequired, this, false, (ScrollView) findViewById(R.id.filtersSV));
            }
    }

    @Override
    public void showImagePickerDialog(CustomNetworkImageView view) {
        Log.d("imagePath", "image selection utils PFA filters activity");
        imageSelectionUtils = new ImageSelectionUtils(this, view);
        imageSelectionUtils.showImagePickerDialog(null, false, false);
    }

    @Override
    public void showFilePickerDialog(CustomNetworkImageView view) {
        Log.d("imagePath", "file selection utils menu form fragment");
        imageSelectionUtils = new ImageSelectionUtils(this, view);
        imageSelectionUtils.showFilePickerDialog(null, false, false);
    }

    @Override
    public void onLabelViewClicked(PFASectionTV pfaSectionTV) {
        sharedPrefUtils.showToast(pfaSectionTV.getText().toString());
    }

    @Override
    public void onButtonCLicked(View view) {

        pfaFormSubmitUtil.submitForm((PFAButton) view, sectionRequired, filtersLL, new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                DO_REFRESH = false;
                if (response != null && response.optBoolean("status")) {
                    Log.d("mapSearchData", "map pfa filter button click 1");
                    try {
                        Log.d("mapSearchData", "map pfa filter button click 2");
                        if (searchFragment.equals("map")){

//                            Type type = new TypeToken<JSONArray>() {}.getType();
                            JSONObject tableJsonObject = response.getJSONObject("table");

                            JSONArray formJSONArray = tableJsonObject.getJSONArray("tableData");
//                            jsonArrayTableData = new GsonBuilder().create().fromJson(formJSONArray.toString(), type);
                            jsonArrayTableData = formJSONArray;

                            if (tableJsonObject.has("title")) {
                                activityTitle = tableJsonObject.getString("title");
                            }

                            if (tableJsonObject.has("next_page")) {
                                nextUrl = tableJsonObject.optString("next_page");
                            }

                            if (tableJsonObject.has("itemCount"))
                                itemCount = tableJsonObject.optString("itemCount");
                            submitFilters();
                        } else {
                            Type type = new TypeToken<List<List<PFATableInfo>>>() {
                            }.getType();

                            JSONObject tableJsonObject = response.getJSONObject("table");

                            JSONArray formJSONArray = tableJsonObject.getJSONArray("tableData");
                            tableData = new GsonBuilder().create().fromJson(formJSONArray.toString(), type);

                            if (tableJsonObject.has("title")) {
                                activityTitle = tableJsonObject.getString("title");
                            }

                            if (tableJsonObject.has("next_page")) {
                                nextUrl = tableJsonObject.optString("next_page");
                            }

                            if (tableJsonObject.has("itemCount"))
                                itemCount = tableJsonObject.optString("itemCount");
                            submitFilters();
                        }

                    } catch (Exception e) {
                        Log.d("mapSearchData", "map pfa filter button click 2 exception = " + e.toString());
                        sharedPrefUtils.printStackTrace(e);
                    }
                } else {
                    assert response != null;
                    sharedPrefUtils.showMsgDialog(response.optString("message_code"), null);
                }
            }
        }, true);
    }

    @Override
    public void onClickGetCodeBtn(View view, VerifyFBOLayout verifyFBOLayout) {

    }

    @Override
    public void onDropdownItemSelected(FormDataInfo formDataInfo, String dataName) {

    }

    private void submitFilters() {
        Log.d("mapSearchData", "map pfa filter button click 3");
        if (formViewsData != null && formViewsData.size() > 0)
            formViewsData.clear();
        formFilteredData = pfaFormSubmitUtil.getViewsData(filtersLL, true);
        setActivityResult();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("imagePath", "onActivityResult = " + "PFAFiltersActivity");

        if (resultCode != RESULT_OK) {
            customViewCreate.clearFocusOfAllViews(filtersLL);
            return;
        }

        if (requestCode == RC_DROPDOWN) {
            customViewCreate.updateDropdownViewsData(data.getExtras(), filtersLL, sectionRequired);
        }
    }

    private void setActivityResult() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FILTERS_DATA, formFilteredData);

        if (searchFragment.equals("map")) {
            if (jsonArrayTableData != null && jsonArrayTableData.length() > 0)
                bundle.putString(SEARCH_DATA,  jsonArrayTableData.toString());
        } else {
            if (tableData != null && tableData.size() > 0)
                bundle.putSerializable(SEARCH_DATA, (Serializable) tableData);
        }
        if (activityTitle != null)
            bundle.putString("activityTitle", activityTitle);

        if (nextUrl != null) {
            bundle.putString(AppConst.EXTRA_NEXT_URL, nextUrl);
        }

        Log.d("mapSearchData", "map pfa drawer instance 4");

        bundle.putString(EXTRA_ITEM_COUNT, itemCount);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }


    public void onClickClearFilterBtn(View view) {
        if (formViewsData != null && formViewsData.size() > 0) {
            DO_REFRESH = true;
            formViewsData.clear();
        }
        if (formFilteredData != null && formFilteredData.size() > 0) {
            formFilteredData.clear();
        }
        if (tableData != null)
            tableData.clear();

        for (int i = 0; i < formSectionInfos.size(); i++) {
            FormSectionInfo formSectionInfo = formSectionInfos.get(i);

            if (formSectionInfo.getFields() != null && formSectionInfo.getFields().size() > 0) {
                List<FormFieldInfo> fieldInfos = formSectionInfo.getFields();
                if (fieldInfos != null && fieldInfos.size() > 0) {
                    for (int j = 0; j < fieldInfos.size(); j++) {
                        if (fieldInfos.get(j).getField_type().equalsIgnoreCase("location_fields")) {
                            AddressObjInfo addressObjInfo = fieldInfos.get(j).getDefault_locations();

                            PFALocationACTV town = filtersLL.findViewWithTag(TOWN_TAG);
                            PFALocationACTV subTown = filtersLL.findViewWithTag(SUB_TOWN_TAG);


                            if (town != null && town.getTextInputLayout() != null && town.getTextInputLayout().getVisibility() == View.VISIBLE) {
                                town.setText("");
                                addressObjInfo.setTown_id(0);
                                addressObjInfo.setTown_name("");
                            }

                            if (subTown != null && subTown.getTextInputLayout() != null && subTown.getTextInputLayout().getVisibility() == View.VISIBLE) {
                                subTown.setText("");
                                addressObjInfo.setSubtown_id(0);
                                addressObjInfo.setSubtown_name("");
                            }

                            fieldInfos.get(j).setDefault_locations(addressObjInfo);
                        } else {
                            fieldInfos.get(j).setDefault_value(null);

                            if (fieldInfos.get(j).getData() != null && fieldInfos.get(j).getData().size() > 0)
                                fieldInfos.get(j).getData().get(0).setKey("");
                        }
                    }

                    formSectionInfo.setFields(fieldInfos);
                }
            }
            formSectionInfos.set(i, formSectionInfo);
        }
        createFiltersView();
    }
}

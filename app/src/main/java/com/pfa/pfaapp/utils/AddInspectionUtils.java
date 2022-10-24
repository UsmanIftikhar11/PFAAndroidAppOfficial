package com.pfa.pfaapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.PFADetailActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.LocalFormLL;
import com.pfa.pfaapp.customviews.LocalGridLL;
import com.pfa.pfaapp.customviews.LocalListLL;
import com.pfa.pfaapp.customviews.PFASideMenuRB;
import com.pfa.pfaapp.httputils.LocalFormHttpUtils;
import com.pfa.pfaapp.interfaces.DDSelectedCallback;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.LocalFormsCallback;
import com.pfa.pfaapp.interfaces.RBClickCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
import com.pfa.pfaapp.localdbmodels.InspectionInfo;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.models.PFATableInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import static com.pfa.pfaapp.dbutils.DBQueriesUtil.TABLE_LOCAL_INSPECTIONS;
import static com.pfa.pfaapp.utils.AppConst.CANCEL;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_INSPECTION_DATA;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.SP_FCM_ID;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;

public class AddInspectionUtils {

    private String API_URL;
    private String inspection_id;
    private String inspection_alert;
    private List<PFAMenuInfo> pfaMenuInfos;
    private RadioGroup topbarRG;
    public int lastClicked = -1;
    public FrameLayout localFormsLL;
    private TextView inspection_alertTV;
    public Button saveFormBtn;
    private InspectionInfo inspectionInfo;
    SharedPrefUtils sharedPrefUtils;

    public String downloadUrl;

    public ImageButton downloadLocalImgBtn;

    private BaseActivity baseActivity;

    private DDSelectedCallback ddSelectedCallback;
    private RBClickCallback rbClickCallback;
    private View rootView;

    private HorizontalScrollView menubarHSV;
    public boolean isDraft;
    public static boolean IS_FAKE = false;
    public static boolean IS_FINE = false;
    boolean conducted_inspection;
    String addProduct = "Do you want to submit Inspection?";
    String addProductBackPressed = "Inspection is Incomplete. Do you want to:";

    public AddInspectionUtils(BaseActivity baseActivity, RBClickCallback rbClickCallback, DDSelectedCallback ddSelectedCallback, View rootView) {
        this.baseActivity = baseActivity;
        this.ddSelectedCallback = ddSelectedCallback;
        this.rbClickCallback = rbClickCallback;
        this.rootView = rootView;
        initViews();
    }


    private void initViews() {
        IS_FAKE = false;
        IS_FINE = false;
        if (rootView == null) {

            saveFormBtn = baseActivity.findViewById(R.id.saveFormBtn);
            inspection_alertTV = baseActivity.findViewById(R.id.inspection_alertTV);

            localFormsLL = baseActivity.findViewById(R.id.localFormsLL);
            topbarRG = baseActivity.findViewById(R.id.topbarRG);
            menubarHSV = baseActivity.findViewById(R.id.menubarHSV);
            baseActivity.sharedPrefUtils.applyFont(baseActivity.findViewById(R.id.saveFormBtn), AppUtils.FONTS.HelveticaNeueMedium);

        } else {
            menubarHSV = rootView.findViewById(R.id.menubarHSV);
            saveFormBtn = rootView.findViewById(R.id.saveFormBtn);
            inspection_alertTV = rootView.findViewById(R.id.inspection_alertTV);

            localFormsLL = rootView.findViewById(R.id.localFormsLL);
            topbarRG = rootView.findViewById(R.id.topbarRG);

            baseActivity.sharedPrefUtils.applyFont(rootView.findViewById(R.id.saveFormBtn), AppUtils.FONTS.HelveticaNeueMedium);
        }

        saveFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSaveFormBtn();
            }
        });
    }

    public void populateDraftInspection(Bundle bundle) {

        inspectionInfo = (InspectionInfo) bundle.getSerializable(EXTRA_INSPECTION_DATA);


//        SharedPreferences mPrefs = baseActivity.getSharedPreferences("inspec", baseActivity.MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = mPrefs.getString("myJson", "");
//
//
//            Type type = new TypeToken<List<InspectionInfo>>() {
//            }.getType();
//            inspectionInfo = gson.fromJson(json, type);


        if (inspectionInfo != null) {
            inspection_id = inspectionInfo.getInspectionID();
            API_URL = inspectionInfo.getAPI_URL();
            Type formSectionInfosType = new TypeToken<List<PFAMenuInfo>>() {
            }.getType();
            pfaMenuInfos = new GsonBuilder().create().fromJson(inspectionInfo.getMenuData(), formSectionInfosType);
            populateData();
        }
    }

    private void initInspectionInfo(final boolean showError) {
        if (pfaMenuInfos != null && pfaMenuInfos.size() > 0)
            pfaMenuInfos.clear();

        for (int i = 0; i < localFormsLL.getChildCount(); i++) {
            View childView = localFormsLL.getChildAt(i);
            if (childView instanceof LocalFormLL) {
                ((LocalFormLL) childView).getPfaMenuInfo(new LocalFormsCallback() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void getPFAMenuInfo(PFAMenuInfo pfaMenuInfo, boolean isError) {
                        pfaMenuInfos.add(pfaMenuInfo);
                        if (isError && showError) {
                            if (baseActivity.sharedPrefUtils.isEnglishLang()) {
                                ((RadioButton) topbarRG.findViewWithTag(pfaMenuInfo.getMenuItemName())).setTextColor(baseActivity.getResources().getColor(R.color.red));
                            } else {
                                ((RadioButton) topbarRG.findViewWithTag(pfaMenuInfo.getMenuItemNameUrdu())).setTextColor(baseActivity.getResources().getColor(R.color.red));
                            }

                        } else {
                            if (baseActivity.sharedPrefUtils.isEnglishLang()) {
                                ((RadioButton) topbarRG.findViewWithTag(pfaMenuInfo.getMenuItemName())).setTextColor(baseActivity.getResources().getColorStateList(R.drawable.tab_textcolor_selector));
                            } else {
                                ((RadioButton) topbarRG.findViewWithTag(pfaMenuInfo.getMenuItemNameUrdu())).setTextColor(baseActivity.getResources().getColorStateList(R.drawable.tab_textcolor_selector));
                            }

                        }
                    }
                }, showError);

            } else if (childView instanceof LocalListLL) {
                PFAMenuInfo pfaMenuInfo = ((LocalListLL) childView).getPfaMenuInfo();
                pfaMenuInfos.add(pfaMenuInfo);
            } else if (childView instanceof LocalGridLL) {
                PFAMenuInfo pfaMenuInfo = ((LocalGridLL) childView).getPfaMenuInfo();
                pfaMenuInfos.add(pfaMenuInfo);
            }
        }

        if (inspectionInfo == null) {
            inspectionInfo = new InspectionInfo();
        }

        inspectionInfo.setAPI_URL(API_URL);
        inspectionInfo.setMenuData(new Gson().toJson(pfaMenuInfos));
    }

    private void performInspAction(String action, boolean finish) {

        if (action.equalsIgnoreCase(String.valueOf(AppUtils.INSPECTION_ACTION.Complete))) {
            initInspectionInfo(true);

            new LocalFormHttpUtils(baseActivity.httpService, pfaMenuInfos, API_URL, inspection_id, baseActivity.sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID, "")).sendInspectionToServer(new HttpResponseCallback() {
                @Override
                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

                    String apiUrl = null;

//                    {"status":true,"message_code":"REQUEST COMPLETED SUCCESSFULLY","localMenu":"Only Checklist is updated."}
                    if (response != null && response.optBoolean("status")) {

                        JSONObject localMenuObject = response.optJSONObject("detailMenu");
                        if (localMenuObject != null) {
                            try {
                                JSONArray menus = localMenuObject.getJSONArray("menus");
                                apiUrl = menus.getJSONObject(0).optString("API_URL");
                                Log.d("productReg", "api url = " + menus.getJSONObject(0).optString("API_URL"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d("productReg", "message = " + response.optString("message_code"));
                        }

                        String finalApiUrl = apiUrl;
                        baseActivity.sharedPrefUtils.showMsgDialog(response.optString("message_code"), new SendMessageCallback() {
                            @Override
                            public void sendMsg(String message) {

                                if ( finalApiUrl!=null /*&& !finalApiUrl.isEmpty()*/){
                                    Bundle bundle = new Bundle();
                                    bundle.putString(EXTRA_URL_TO_CALL, finalApiUrl);
                                    baseActivity.sharedPrefUtils.startNewActivity(PFADetailActivity.class, bundle, true);
                                }
                                else {
                                    baseActivity.dbQueriesUtil.deleteTableRow(TABLE_LOCAL_INSPECTIONS, "inspectionID", inspection_id);
                                    if (baseActivity.isTaskRoot()) {
                                        baseActivity.onBackPressed();
                                    } else {
                                        AppConst.DO_REFRESH = true;
                                        baseActivity.finish();
                                    }
                                }
                            }
                        });
                    } else {
                        if (response != null && response.has("message_code"))
                            baseActivity.sharedPrefUtils.showMsgDialog(response.optString("message_code"), null);
                        else
                            baseActivity.sharedPrefUtils.showMsgDialog("Error Occurred" + response, null);
                    }
                }
            }, action);
        } else if (action.equalsIgnoreCase(String.valueOf(AppUtils.INSPECTION_ACTION.Draft))) {
            initInspectionInfo(false);
            saveDraft(finish);

        } else if (action.equalsIgnoreCase(String.valueOf(AppUtils.INSPECTION_ACTION.Exit))) {
            AppConst.DO_REFRESH = false;
            baseActivity.finish();
        }

    }

    private void onClickSaveFormBtn() {
        if (baseActivity.httpService.isNetworkDisconnected()) {
            baseActivity.sharedPrefUtils.showMsgDialog(baseActivity.getString(R.string.inspection_will_be_saved_draft), new SendMessageCallback() {
                @Override
                public void sendMsg(String message) {
                    initInspectionInfo(false);
                    saveDraft(false);
                }
            });

        } else {
            // Do API call to send local form data to server
/*
            new LocalFormHttpUtils(baseActivity.httpService, pfaMenuInfos, API_URL, inspection_id, baseActivity.sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID, "")).sendInspectionToServer(new HttpResponseCallback() {
                @Override
                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

                    String apiUrl = null;

//                    {"status":true,"message_code":"REQUEST COMPLETED SUCCESSFULLY","localMenu":"Only Checklist is updated."}
                    if (response != null && response.optBoolean("status")) {

                        JSONObject localMenuObject = response.optJSONObject("detailMenu");
                        if (localMenuObject != null) {
                            try {
                                JSONArray menus = localMenuObject.getJSONArray("menus");
                                apiUrl = menus.getJSONObject(0).optString("API_URL");
                                Log.d("productReg", "api url = " + menus.getJSONObject(0).optString("API_URL"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d("productReg", "message = " + response.optString("message_code"));
                        }

                        String finalApiUrl = apiUrl;
                        baseActivity.sharedPrefUtils.showMsgDialog(response.optString("message_code"), new SendMessageCallback() {
                            @Override
                            public void sendMsg(String message) {

                                if ( finalApiUrl!=null */
/*&& !finalApiUrl.isEmpty()*//*
){
                                    Bundle bundle = new Bundle();
                                    bundle.putString(EXTRA_URL_TO_CALL, finalApiUrl);
                                    baseActivity.sharedPrefUtils.startNewActivity(PFADetailActivity.class, bundle, true);
                                }
                                else {
                                    baseActivity.dbQueriesUtil.deleteTableRow(TABLE_LOCAL_INSPECTIONS, "inspectionID", inspection_id);
                                    if (baseActivity.isTaskRoot()) {
                                        baseActivity.onBackPressed();
                                    } else {
                                        AppConst.DO_REFRESH = true;
                                        baseActivity.finish();
                                    }
                                }
                            }
                        });
                    } else {
                        if (response != null && response.has("message_code"))
                            baseActivity.sharedPrefUtils.showMsgDialog(response.optString("message_code"), null);
                        else
                            baseActivity.sharedPrefUtils.showMsgDialog("Error Occurred" + response, null);
                    }
                }
            }, action);
*/

            baseActivity.sharedPrefUtils.showThreeBtnsMsgDialog( addProduct , new SendMessageCallback() {
                    @Override
                    public void sendMsg(String message) {
                        performInspAction(message, false);
                    }
                }, String.valueOf(AppUtils.INSPECTION_ACTION.Complete), (downloadLocalImgBtn.getVisibility() != View.VISIBLE && !isDraft));
        }
    }

    public void setSaveDraftData() {
        initInspectionInfo(false);
        if (inspectionInfo == null) {
            baseActivity.finish();
        } else {
            if (inspectionInfo.getInspectionID() != null && (!inspectionInfo.getInspectionID().isEmpty())) {

                baseActivity.sharedPrefUtils.showThreeBtnsMsgDialog(addProductBackPressed, new SendMessageCallback() {
                    @Override
                    public void sendMsg(String message) {
                        performInspAction(message, true);
                    }
                }, null, (downloadLocalImgBtn.getVisibility() != View.VISIBLE && !isDraft));
            } else {
                baseActivity.finish();
            }
        }
    }

    private void insertInspection() {
        baseActivity.dbQueriesUtil.insertUpdateLocalInspections(inspectionInfo, new WhichItemClicked() {
            @Override
            public void whichItemClicked(String message) {
                baseActivity.finish();
            }

            @Override
            public void downloadInspection(String downloadUrl, int position) {
            }

            @Override
            public void deleteRecordAPICall(String deleteUrl, int position) {

            }
        });
    }

    private void saveDraft(boolean isFinish) {
        if (inspectionInfo == null) {
            if (isFinish)
                baseActivity.finish();
        } else {
            JSONArray resultJsonArray = baseActivity.dbQueriesUtil.getSelectedTableValues(TABLE_LOCAL_INSPECTIONS, "inspectionID", inspectionInfo.getInspectionID());

            if (resultJsonArray != null && resultJsonArray.length() > 0) {
                insertInspection();
            } else {
                new LocalFormHttpUtils().downloadAndSaveInspection(baseActivity, downloadUrl, new SendMessageCallback() {
                    @Override
                    public void sendMsg(String message) {
                        if (!message.equalsIgnoreCase("error")) {
                            insertInspection();
                        }
                    }
                });
            }
        }
    }

    public void onCompleteHttpResponse(JSONObject response) {
        Log.d("onCreateActv" , "add inscpection util = onCompleteHttpResponse" );

        if (response != null && response.optBoolean("status")) {
            try {

                conducted_inspection = response.optBoolean("conducted_inspection");

                PFATableInfo pfaTableInfo = new PFATableInfo();

                JSONObject localMenuObject = response.optJSONObject("localMenu");

                try {
                    if (localMenuObject !=null && localMenuObject.has("before_submit_alert")) {
                        addProduct = localMenuObject.getString("before_submit_alert");
                        Log.d("isAddProduct" , "isAddProduct = " + localMenuObject.getString("before_submit_alert"));
                    }
                    else {
                        addProduct = "Do you want to submit Inspection?";
                        Log.d("isAddProduct" , "isAddProduct = " + addProduct);
                    }

                    if (localMenuObject !=null && localMenuObject.has("back_press_alert")) {
                        addProductBackPressed = localMenuObject.getString("back_press_alert");
                        Log.d("isAddProduct" , "isAddProductBack = " + localMenuObject.getString("back_press_alert"));
                    }
                    else {
                        addProductBackPressed = "Inspection is Incomplete. Do you want to:";
                        Log.d("isAddProduct" , "isAddProductBack = " + addProductBackPressed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                assert localMenuObject != null;
                API_URL = localMenuObject.optString("API_URL");
                Log.d("onCreateActv" , "add inscpection util = " + API_URL);
//                if (localMenuObject.has("inspection_id"))
                    inspection_id = localMenuObject.getString("inspection_id");
                Log.d("onCreateActv" , "add inscpection util inspection_id= " + inspection_id);
                Log.d("onCreateActv" , "add inscpection util conducted_inspection= " + conducted_inspection);

                baseActivity.getSharedPreferences("appPrefs", Context.MODE_PRIVATE).edit().putBoolean("conductedInspection", conducted_inspection).apply();
                baseActivity.getSharedPreferences("appPrefs", Context.MODE_PRIVATE).edit().putString("inspectionId", API_URL).apply();

                if (localMenuObject.has("download_url")) {
                    downloadUrl = localMenuObject.optString("download_url");
                }

                if (localMenuObject.has("inspection_alert"))
                    inspection_alert = localMenuObject.getString("inspection_alert");

                JSONArray menus = localMenuObject.getJSONArray("menus");

                Type type = new TypeToken<List<PFAMenuInfo>>() {
                }.getType();

                Log.d("localMenuData" , "menu = " + menus.toString());

                pfaMenuInfos = new GsonBuilder().create().fromJson(menus.toString(), type);
                if (inspectionInfo == null) {
                    inspectionInfo = new InspectionInfo();
                    inspectionInfo.setInspectionID(inspection_id);
                    inspectionInfo.setAPI_URL(API_URL);

                    if (localMenuObject.has("title")) {
                        inspectionInfo.setInspectionName(localMenuObject.getString("title"));
                    }

                    if (localMenuObject.has("local_add_newUrl")) {
                        inspectionInfo.setLocal_add_newUrl(localMenuObject.optString("local_add_newUrl"));

                        if (baseActivity.downloadInspImgBtn != null)
                            baseActivity.downloadInspImgBtn.setVisibility(View.VISIBLE);
                        else if (downloadLocalImgBtn != null)
                            downloadLocalImgBtn.setVisibility(View.VISIBLE);
                    }

                    inspectionInfo.setSaveData(localMenuObject.optBoolean("saveData"));

                    if (localMenuObject.has("draft_inspection") && localMenuObject.getJSONArray("draft_inspection").length() > 0) {
                        JSONArray draft_inspection = localMenuObject.getJSONArray("draft_inspection").getJSONArray(0);
                        inspectionInfo.setDraft_inspection(draft_inspection.toString());
                    }

                    inspectionInfo.setInspection_alert(inspection_alert);
                }
                populateData();

            } catch (JSONException e) {
                baseActivity.sharedPrefUtils.printStackTrace(e);
            }
        }
    }


    private void populateData() {
        populateHorizontalMenu();

        if (rootView == null)
            baseActivity.setTitle("" + (inspectionInfo.getInspectionName()), true);

        if (downloadUrl != null && (!downloadUrl.isEmpty())) {
            if (downloadLocalImgBtn != null)
                downloadLocalImgBtn.setVisibility(View.VISIBLE);
        }
    }

    private void populateHorizontalMenu() {
        menubarHSV.setVisibility(View.VISIBLE);
        if (inspectionInfo.getInspection_alert() != null && (!inspectionInfo.getInspection_alert().isEmpty())) {
            inspection_alertTV.setVisibility(View.VISIBLE);
            inspection_alertTV.setText(inspectionInfo.getInspection_alert());
            inspection_alertTV.setTextColor(baseActivity.getResources().getColor(R.color.maroon));
            baseActivity.sharedPrefUtils.applyFont(inspection_alertTV, AppUtils.FONTS.HelveticaNeueBold);
        }

        if (inspectionInfo != null && inspectionInfo.isSaveData())
            saveFormBtn.setVisibility(View.VISIBLE);
        else {
            saveFormBtn.setVisibility(View.GONE);
        }
        if (topbarRG != null && topbarRG.getChildCount() > 0) {
            topbarRG.removeAllViews();
        }
        if (localFormsLL != null && localFormsLL.getChildCount() > 0) {
            localFormsLL.removeAllViews();
        }

        new PFASideMenuRB(baseActivity, topbarRG, pfaMenuInfos, rbClickCallback, false);

        if (pfaMenuInfos != null && pfaMenuInfos.size() > 0) {
            for (PFAMenuInfo pfaMenuInfo : pfaMenuInfos) {

//            "form", "list", "googlemap", "profile","search"
                switch (pfaMenuInfo.getMenuType()) {

                    case "list":
                        localFormsLL.addView(new LocalListLL(pfaMenuInfo, conducted_inspection, baseActivity));
                        break;

                    case "dashboard":

                    case "grid":
                        LocalGridLL localGridLL = new LocalGridLL(pfaMenuInfo, baseActivity);
                        localFormsLL.addView(localGridLL);
                        break;

                    default:
                        LocalFormLL localFormLL4 = new LocalFormLL(pfaMenuInfo, baseActivity, ddSelectedCallback);
                        localFormsLL.addView(localFormLL4);
                        break;
                }

                if (localFormsLL.getChildCount() > 0) {
                    localFormsLL.getChildAt(localFormsLL.getChildCount() - 1).setVisibility(View.GONE);
                }
            }

            if (topbarRG.getChildCount() > 0)
                topbarRG.getChildAt(0).performClick();
        }
    }

    private void setChildVisibility() {
        for (int i = 0; i < localFormsLL.getChildCount(); i++) {
            if (i == lastClicked) {
                localFormsLL.getChildAt(i).setVisibility(View.VISIBLE);
            } else {
                localFormsLL.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }

    public void onClickRB(View targetView) {
        lastClicked = targetView.getId();
        setChildVisibility();
    }

    public void onClickDownloadAsDraftBtn(final SendMessageCallback callback) {

        initInspectionInfo(false);
        if (inspectionInfo != null) {

            if (inspectionInfo.getInspectionID() != null && (!inspectionInfo.getInspectionID().isEmpty())) {

                baseActivity.sharedPrefUtils.showTwoBtnsMsgDialog(baseActivity.getResources().getString(R.string.saveInspectionOfflineMsg), new SendMessageCallback() {
                    @Override
                    public void sendMsg(String message) {
                        if (message.equalsIgnoreCase(CANCEL)) {
                            if (callback != null)
                                callback.sendMsg(CANCEL);
                            return;
                        }

                        saveDraft(false);
                    }
                });
            } else baseActivity.finish();
        }
    }

    public void onDDDataSelected(FormDataInfo formDataInfo) {
        if (formDataInfo != null && formDataInfo.getAPI_URL() != null && (!formDataInfo.getAPI_URL().isEmpty())) {
            Log.d("createViewDropdown" , "AddInspectionUtils = " + formDataInfo.getAPI_URL());
            Log.d("createViewDropdown" , "AddInspectionUtils = " + formDataInfo);

            baseActivity.httpService.fetchConfigData(formDataInfo.getAPI_URL(), new HttpResponseCallback() {
                @Override
                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                    if (response != null) {
                        baseActivity.sharedPrefUtils.printLog("response==:>", "" + response.toString());
                        if (response.optBoolean("status")) {
                            try {
                                if (response.has("detailMenu")) {
                                    Type type = new TypeToken<List<PFAMenuInfo>>() {
                                    }.getType();
                                    JSONArray menusJsonArray = response.getJSONObject("detailMenu").getJSONArray("menus");
                                    List<PFAMenuInfo> testPFMenuInfos = new GsonBuilder().create().fromJson(menusJsonArray.toString(), type);

                                    if (testPFMenuInfos != null && testPFMenuInfos.size() > 0) {
                                        if (pfaMenuInfos != null && pfaMenuInfos.size() > 0) {
                                            for (int i = 0; i < pfaMenuInfos.size(); i++) {
                                                if (pfaMenuInfos.get(i).getSlug().equalsIgnoreCase(testPFMenuInfos.get(0).getSlug())) {
                                                    pfaMenuInfos.set(i, testPFMenuInfos.get(0));

                                                    View view = localFormsLL.getChildAt(i);

                                                    if (view instanceof LocalFormLL) {
                                                        ((LocalFormLL) localFormsLL.getChildAt(i)).updateLayout(pfaMenuInfos.get(i));
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                baseActivity.sharedPrefUtils.printStackTrace(e);
                            }
                        }
                    }
                }
            });
        }
    }
}

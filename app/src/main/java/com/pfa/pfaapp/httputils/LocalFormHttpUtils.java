package com.pfa.pfaapp.httputils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
import com.pfa.pfaapp.localdbmodels.InspectionInfo;
import com.pfa.pfaapp.models.AddressObjInfo;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormSectionInfo;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.models.SearchBizInspInfo;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.pfa.pfaapp.dbutils.DBQueriesUtil.TABLE_LOCAL_INSPECTIONS;
import static com.pfa.pfaapp.utils.AddInspectionUtils.IS_FAKE;
import static com.pfa.pfaapp.utils.AddInspectionUtils.IS_FINE;
import static com.pfa.pfaapp.utils.AppConst.DISTRICT_TAG;
import static com.pfa.pfaapp.utils.AppConst.DIVISION_TAG;
import static com.pfa.pfaapp.utils.AppConst.DO_REFRESH;
import static com.pfa.pfaapp.utils.AppConst.INSPECTION_ID;
import static com.pfa.pfaapp.utils.AppConst.REGION_TAG;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;
import static com.pfa.pfaapp.utils.AppConst.SUB_TOWN_TAG;
import static com.pfa.pfaapp.utils.AppConst.TOWN_TAG;

public class LocalFormHttpUtils {
    private HttpService httpService;
    private List<PFAMenuInfo> pfaMenuInfos;
    private String API_URL;
    private String inspectionID;
    private String staffID;
    private boolean containsFineImage = false;
    public LocalFormHttpUtils() { }

    /**
     * formDataInfo  sendInspectionToServer is used to create the request Param to be sent to server
     * JSON format to be sent to server:
     * [{"menuSlug":"overview","formData":[{"business_name":"safasdf","cnic_number":"32432423","phonenumber":"03243423432",
     * "inspection_type":"saf asdf","alternate_phonenumber":""},{"next_inspection_date":"","description":""}]}]
     */
    public LocalFormHttpUtils(HttpService httpService, List<PFAMenuInfo> pfaMenuInfos, String API_URL, String inspectionID, String staffID) {
        this.httpService = httpService;
        this.pfaMenuInfos = pfaMenuInfos;
        this.API_URL = API_URL;
        this.inspectionID = inspectionID;
        this.staffID = staffID;
        Log.d("svaeInspection" , "url = " + API_URL);
    }

    public void sendInspectionToServer(final HttpResponseCallback httpResponseCallback, String action) {

        final HashMap<String, String> reqParams = new HashMap<>();

//         Files based on Tab sections. Key is Tab Slug and files is the file to be sent to server
        final Map<String, File> filesMap = new HashMap<>();

        // Inspection Menus JSON Array that will contain all the sections of all menus as sub array
        JSONArray reqJSONArray = new JSONArray();

        boolean isDataValid = true;
        boolean isFineNotAdded = true;

        // start traversing the Menu Infos to be shown for Inspection. PFAMenuInfo is the tab section of inspection
        for (PFAMenuInfo pfaMenuInfo : pfaMenuInfos) {
            JSONObject pfaMenuTabJSONObj = new JSONObject();
            JSONArray menuSectionsJSONArray = new JSONArray();
            try {
                if (pfaMenuInfo.getData() != null && pfaMenuInfo.getData().getProofImagePath() != null) {
                    filesMap.put(pfaMenuInfo.getSlug() + "/proof", new File(pfaMenuInfo.getData().getProofImagePath()));
                }

                if (pfaMenuInfo.getData() != null && pfaMenuInfo.getData().getForm() != null && pfaMenuInfo.getData().getForm().size() > 0) {

//                     get All sections of Tab (Tab is PFAMenuInfo)
                    List<FormSectionInfo> formSectionInfos = pfaMenuInfo.getData().getForm();
                    for (int formSec = 0; formSec < formSectionInfos.size(); formSec++) {
                        FormSectionInfo formSectionInfo = formSectionInfos.get(formSec);

                        // All the key value pairs of Section Data fields
                        JSONObject formDataKeyValuesJSONObj = new JSONObject();
                        if (formSectionInfo.getFields() != null && formSectionInfo.getFields().size() > 0) {

                            for (int x = 0; x < formSectionInfo.getFields().size(); x++) {

                                if (formSectionInfo.getFields().get(x).getField_name().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.mediaFormField))) {

                                    if (formSectionInfo.getFields().get(x).getData() != null && formSectionInfo.getFields().get(x).getData().size() > 0) {
                                        filesMap.putAll(pfaMenuInfo.getFilesMap());
                                    }
//                                        Check if image field is required and image is not selected to validate the data of form

                                } else if (formSectionInfo.getFields().get(x).getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.imageView))) {


                                    if (pfaMenuInfo.getFilesMap() != null && pfaMenuInfo.getFilesMap().size() > 0) {
                                        if (pfaMenuInfo.getFilesMap().containsKey(formSectionInfo.getFields().get(x).getField_name())) {
                                            filesMap.put(formSectionInfo.getFields().get(x).getField_name(), pfaMenuInfo.getFilesMap().get(formSectionInfo.getFields().get(x).getField_name()));
                                        } else {
                                            filesMap.putAll(pfaMenuInfo.getFilesMap());
                                        }

                                    }

                                    //// commented above code and added here to check all the required images
//                                    Check if image field is required and image is not selected to validate the data of form
                                    if (formSectionInfo.getFields().get(x).isRequired()) {

                                        if (formSectionInfo.getFields().get(x).getIcon() != null && (!formSectionInfo.getFields().get(x).getIcon().startsWith("https://"))) {
                                            if ((!pfaMenuInfo.getFilesMap().containsKey(formSectionInfo.getFields().get(x).getField_name())) ||
                                                    (pfaMenuInfo.getFilesMap().get(formSectionInfo.getFields().get(x).getField_name())) == null) {
                                                Log.e("Name: " + formSectionInfo.getFields().get(x).getField_name(), "Value = empty.. .is just check");

                                                isDataValid = false;
                                            }
                                        }
                                    }


                                } else if (formSectionInfo.getFields().get(x).getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.radiogroup))) {
                                    formDataKeyValuesJSONObj.put(formSectionInfo.getFields().get(x).getField_name(), formSectionInfo.getFields().get(x).getDefault_value());

//                                    check if the radiogroup field is required and no data is selected for data validation of form
                                    if (formSectionInfo.getFields().get(x).isRequired() && (formSectionInfo.getFields().get(x).getDefault_value() == null || (formSectionInfo.getFields().get(x).getDefault_value().isEmpty()))) {
                                        Log.e("Name: " + formSectionInfo.getFields().get(x).getField_name(), "Value =" + formSectionInfo.getFields().get(x).getDefault_value());

                                        isDataValid = false;
                                    }

                                }

                                else if (formSectionInfo.getFields().get(x).getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.dropdown))) {
                                    formDataKeyValuesJSONObj.put(formSectionInfo.getFields().get(x).getField_name(), formSectionInfo.getFields().get(x).getDefault_value());

//                                    check if the dropdown field is required and no data is selected for data validation of form
                                    if (formSectionInfo.getFields().get(x).isRequired() && (formSectionInfo.getFields().get(x).getDefault_value() == null || (formSectionInfo.getFields().get(x).getDefault_value().isEmpty()))) {
                                        Log.e("Name: " + formSectionInfo.getFields().get(x).getField_name(), "Value =" + formSectionInfo.getFields().get(x).getDefault_value());

                                        isDataValid = false;
                                    }

                                }
                                ///////////
                                else if (formSectionInfo.getFields().get(x).getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.checkbox))) {

                                    List<String> localDataInfos = new ArrayList<>();
                                    for(FormDataInfo formDataInfo: formSectionInfo.getFields().get(x).getData())
                                    {
                                        if(formDataInfo.isSelected())
                                        {
                                            localDataInfos.add(formDataInfo.getKey());
                                        }
                                    }
                                    formDataKeyValuesJSONObj.put(formSectionInfo.getFields().get(x).getField_name(),localDataInfos);


                                }
                                ///////
                                else if (formSectionInfo.getFields().get(x).getField_type().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.location_fields))) {
                                    if (formSectionInfo.getFields().get(x).getDefault_locations() != null) {
                                        AddressObjInfo addressObjInfo = formSectionInfo.getFields().get(x).getDefault_locations();

                                        if (addressObjInfo.getRegion_id() != 0)
                                            formDataKeyValuesJSONObj.put(REGION_TAG, "" + addressObjInfo.getRegion_id());
                                        if (addressObjInfo.getDivision_id() != 0)
                                            formDataKeyValuesJSONObj.put(DIVISION_TAG, "" + addressObjInfo.getDivision_id());
                                        if (addressObjInfo.getDistrict_id() != 0)
                                            formDataKeyValuesJSONObj.put(DISTRICT_TAG, "" + addressObjInfo.getDistrict_id());
                                        if (addressObjInfo.getTown_id() != 0)
                                            formDataKeyValuesJSONObj.put(TOWN_TAG, "" + addressObjInfo.getTown_id());
                                        if (addressObjInfo.getSubtown_id() != 0)
                                            formDataKeyValuesJSONObj.put(SUB_TOWN_TAG, "" + addressObjInfo.getSubtown_id());
                                    }

                                } else {

                                    List<FormDataInfo> formDataInfos = formSectionInfo.getFields().get(x).getData();
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

                                        if (formSectionInfo.getFields().get(x).isRequired() && formValues.isEmpty()) {

                                            Log.e("Name: " + formSectionInfo.getFields().get(x).getField_name(), "Value =" + formSectionInfo.getFields().get(x).getDefault_value());

                                            isDataValid = false;
                                        }

                                        formDataKeyValuesJSONObj.put(formSectionInfo.getFields().get(x).getField_name(), formValues);
                                    }
                                }
                            }
                            if (formDataKeyValuesJSONObj.length() > 0) {
//                                 Add key value pairs json into sections array
                                menuSectionsJSONArray.put(formDataKeyValuesJSONObj);
                            }
                        }
                    }
                }

                pfaMenuTabJSONObj.put("menuSlug", pfaMenuInfo.getSlug());
//                 Add sections array of Menu into request json object as formData
                pfaMenuTabJSONObj.put("formData", menuSectionsJSONArray);

//                Check if the image of fine is added to FileMap
                if (filesMap.size() > 0) {
                    for (String key : filesMap.keySet()) {

                        if (key.startsWith("fine-challans")) {
                            containsFineImage = true;
                        }
                    }
                }

//                FileMap for fine Image  End
                if (pfaMenuInfo.getSlug().equalsIgnoreCase("fine-challans") && menuSectionsJSONArray.length() > 0) {
                    isFineNotAdded = false;

                }

                reqJSONArray.put(pfaMenuTabJSONObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        reqParams.put("inspectionMenuData", reqJSONArray.toString());
        reqParams.put("inspection_id", INSPECTION_ID == null ? inspectionID : INSPECTION_ID);
        reqParams.put(SP_STAFF_ID, staffID);

//        Log.e("LocalFormHttpParams", "" + reqParams.toString());

        httpService.printLog("reqJSONArray.toString()", "" + (reqJSONArray.toString()));


        if (IS_FAKE) {
            isDataValid = true;
        }


        if (IS_FINE && isFineNotAdded && (!IS_FAKE)) {
            httpService.showMsgDialog("Please add Challan Detail  in \"Fine Section\" before submitting inspection!", "Warning!", null);
            return;
        }


//        if (containsFineImage && isFineNotAdded){
        if ((IS_FINE &&containsFineImage == false  && isFineNotAdded == false)) {
            httpService.showMsgDialog("Please add Challan Detail  in \"Fine Section\" before submitting inspection!", "Warning!", null);

            return;
        }



        if(action.equalsIgnoreCase(String.valueOf(AppUtils.INSPECTION_ACTION.Complete))) {
            if (httpService.getContext() != null && isDataValid) {
                httpService.formSubmit(reqParams, filesMap, API_URL, httpResponseCallback, true, null);
            }
        }

    }

    public void downloadAndSaveInspection(final BaseActivity baseActivity, final String downloadUrl, final SendMessageCallback sendMessageCallback) {

        JSONArray allRecords = baseActivity.dbQueriesUtil.selectAllFromTable(TABLE_LOCAL_INSPECTIONS);

       SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(baseActivity);
       String Limit = sharedPrefUtils.getSharedPrefValue("Draft_MAx_Limit","0");
        if (allRecords != null && Limit != null && allRecords.length() > Integer.parseInt(Limit)) {
            new SharedPrefUtils(baseActivity).showMsgDialog("Sorry! Local Inspection Limit (more than"+ Integer.parseInt(Limit)+"records) consumed ", "Inspection Limit!", null);
            return;
        }

        baseActivity.httpService.getListsData(downloadUrl, new HashMap<String, String>(), new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                if (response != null && response.optBoolean("status")) {
                    try {

                        JSONObject localMenuObject = response.optJSONObject("localMenu");

                        JSONArray menus = localMenuObject.getJSONArray("menus");

                        Type type = new TypeToken<List<PFAMenuInfo>>() {
                        }.getType();

                        List<PFAMenuInfo> pfaMenuInfos = new GsonBuilder().create().fromJson(menus.toString(), type);
                        InspectionInfo inspectionInfo = new InspectionInfo();
                        inspectionInfo.setInspectionID(localMenuObject.optString("inspection_id"));
                        inspectionInfo.setAPI_URL(localMenuObject.optString("API_URL"));

                        if (localMenuObject.has("draft_inspection") && localMenuObject.getJSONArray("draft_inspection").length() > 0) {
                            JSONArray draft_inspection = localMenuObject.getJSONArray("draft_inspection").getJSONArray(0);

                            inspectionInfo.setDraft_inspection(draft_inspection.toString());
                        } else {
                            baseActivity.sharedPrefUtils.showMsgDialog("Inspection is already saved as draft", new SendMessageCallback() {
                                @Override
                                public void sendMsg(String message) {

                                    sendMessageCallback.sendMsg("error");
                                }
                            });

                            return;
                        }

                        if (localMenuObject.has("inspection_alert"))
                            inspectionInfo.setInspection_alert(localMenuObject.optString("inspection_alert"));

                        inspectionInfo.setMenuData(new Gson().toJson(pfaMenuInfos));
                        inspectionInfo.setInspectionName(localMenuObject.optString("title"));
                        String inspectionTime = baseActivity.httpService.getFutureExpiryTime();
                        inspectionInfo.setInsert_time(inspectionTime);
                        inspectionInfo.setLocal_add_newUrl(downloadUrl);
                        inspectionInfo.setSaveData(localMenuObject.optBoolean("saveData"));

                        baseActivity.dbQueriesUtil.insertUpdateLocalInspections(inspectionInfo, new WhichItemClicked() {
                            @Override
                            public void whichItemClicked(String msg) {
                                if (msg != null && (!msg.isEmpty())) {
                                    baseActivity.sharedPrefUtils.showMsgDialog(msg, new SendMessageCallback() {
                                        @Override
                                        public void sendMsg(String message) {

                                            JSONArray jsonArray = baseActivity.dbQueriesUtil.selectAllFromTable(TABLE_LOCAL_INSPECTIONS);
                                            if (jsonArray != null) {
                                                AppConst.draftsRadioButton.setText(String.format(Locale.getDefault(), "Drafts\n( %d )", jsonArray.length()));
                                                DO_REFRESH = true;
                                            }
                                            sendMessageCallback.sendMsg("");
                                        }
                                    });
                                }
                            }

                            @Override
                            public void downloadInspection(String downloadUrl, int position) {

                            }

                            @Override
                            public void deleteRecordAPICall(String deleteUrl, int position) {

                            }
                        });

                    } catch (JSONException e) {
                        baseActivity.sharedPrefUtils.printStackTrace(e);
                    }
                } else
                    baseActivity.sharedPrefUtils.showMsgDialog("Response=>" + response, null);
            }
        }, true);
    }
}

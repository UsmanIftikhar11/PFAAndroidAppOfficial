package com.pfa.pfaapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.FBOMainGridActivity;
import com.pfa.pfaapp.PFADetailActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.CustomNetworkImageView;
import com.pfa.pfaapp.customviews.CustomViewCreate;
import com.pfa.pfaapp.customviews.PFAButton;
import com.pfa.pfaapp.customviews.PFADetailMenu;
import com.pfa.pfaapp.customviews.PFASectionTV;
import com.pfa.pfaapp.customviews.PFAViewsUtils;
import com.pfa.pfaapp.customviews.VerifyFBOLayout;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.httputils.PFAFormSubmitUtil;
import com.pfa.pfaapp.interfaces.DDSelectedCallback;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.PFAViewsCallbacks;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormSectionInfo;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.ImageSelectionUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.pfa.pfaapp.utils.AppConst.CAPTURE_PHOTO;
import static com.pfa.pfaapp.utils.AppConst.CHOOSE_FROM_GALLERY;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_ACTIVITY_TITLE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_BIZ_FORM_DATA;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_DETAIL_MENU;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.OTHER_FILES;
import static com.pfa.pfaapp.utils.AppConst.RC_DROPDOWN;
import static com.pfa.pfaapp.utils.AppConst.RECORD_VIDEO;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;
import static com.pfa.pfaapp.utils.AppConst.codeVerified;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class MenuFormFragment extends Fragment implements HttpResponseCallback, PFAViewsCallbacks, DDSelectedCallback {

    @SuppressLint("StaticFieldLeak")
    public LinearLayout menuFragParentLL;
    private ScrollView fragMenuItemSV;
    public HttpService httpService;

    public SharedPrefUtils sharedPrefUtils;


    private List<FormSectionInfo> formSectionInfos;

    private CustomViewCreate customViewCreate;
    private ImageSelectionUtils imageSelectionUtils;
    private HashMap<String, HashMap<String, Boolean>> sectionRequired = new HashMap<>();

    private BaseActivity baseActivity;
    private String urlToCall;

    private List<String> columnTags = new ArrayList<>();
    private PFADetailMenu pfaDetailMenu;

    public MenuFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pfaMenuInfo Parameter 1.
     * @return A new instance of fragment MenuFormFragment.
     */

    public static MenuFormFragment newInstance(PFAMenuInfo pfaMenuInfo, JSONObject data) {
        MenuFormFragment fragment = new MenuFormFragment();
        Bundle args = new Bundle();
        if (pfaMenuInfo != null && pfaMenuInfo.getAPI_URL() != null) {
            args.putString(EXTRA_URL_TO_CALL, pfaMenuInfo.getAPI_URL());
        }
        if (data != null) {
            args.putString(EXTRA_BIZ_FORM_DATA, data.toString());
        }
        fragment.setArguments(args);
        return fragment;
    }

    public static MenuFormFragment newInstance(Bundle args) {
        MenuFormFragment fragment = new MenuFormFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rotView = inflater.inflate(R.layout.fragment_menu_item, container, false);
        menuFragParentLL = rotView.findViewById(R.id.menuFragParentLL);
        fragMenuItemSV = rotView.findViewById(R.id.fragMenuItemSV);

        Log.d("onCreateActv" , "MenuFormsFragment");
        initFragment();

        return rotView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initFragment() {
        baseActivity = (BaseActivity) getActivity();
        pfaDetailMenu = new PFADetailMenu(getContext());

        if (getArguments() != null) {
            Log.d("menuFormFragData" , "getArguments != null");
            if (getArguments().containsKey(EXTRA_URL_TO_CALL)) {
                Log.d("menuFormFragData" , "EXTRA_URL_TO_CALL");
                urlToCall = getArguments().getString(EXTRA_URL_TO_CALL);
            }

            if (getArguments().containsKey(EXTRA_JSON_STR_RESPONSE)) {
                try {
                    onCompleteHttpResponse(new JSONObject(getArguments().getString(EXTRA_JSON_STR_RESPONSE)), urlToCall);
                    Log.d("menuFormFragData" , "EXTRA_JSON_STR_RESPONE");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (getArguments().containsKey(EXTRA_BIZ_FORM_DATA)) {
                try {
                    Log.d("menuFormFragData" , "EXTRA_BIZ_FORM_DATA");
                    JSONObject dataJsonObject = new JSONObject(getArguments().getString(EXTRA_BIZ_FORM_DATA));
                    JSONArray formJSONArray = dataJsonObject.getJSONArray("fields");
                    populateFieldsData(formJSONArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.d("menuFormFragData" , "refreshData");
                refreshData();
            }
        } /*else
            refreshData();*/
    }

    private void populateFieldsData(JSONArray formJSONArray) {
        Type type = new TypeToken<List<PFATableInfo>>() {
        }.getType();

        List<PFATableInfo> tableData = new GsonBuilder().create().fromJson(formJSONArray.toString(), type);

        if (tableData != null && tableData.size() > 0) {
            pfaDetailMenu.createDetailViews(tableData, columnTags, menuFragParentLL, MenuFormFragment.this);
        }
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        if (response != null && response.optBoolean("status")) {
            try {
                Type type = new TypeToken<List<FormSectionInfo>>() {
                }.getType();

                if (response.has("title")) {
                    baseActivity.setTitle(baseActivity.sharedPrefUtils.isEnglishLang()?response.optString("title"):response.optString("titleUrdu"), true);
                }
                JSONObject dataJsonObject = response.getJSONObject("data");

                if (dataJsonObject.has("form")) {
                    Log.d("enfrocementData" , "data = " + response);
                    JSONArray formJSONArray = dataJsonObject.getJSONArray("form");
                    formSectionInfos = new GsonBuilder().create().fromJson(formJSONArray.toString(), type);

                    customViewCreate = new CustomViewCreate(getActivity(), MenuFormFragment.this);
                    customViewCreate.setDDCallback(MenuFormFragment.this);

                    populateData();
                } else if (dataJsonObject.has("fields")) {

                    JSONArray formJSONArray = dataJsonObject.getJSONArray("fields");
                    populateFieldsData(formJSONArray);

                }
            } catch (JSONException e) {
                baseActivity.sharedPrefUtils.printStackTrace(e);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("imagePath" , "onActivityResult = " + "MenuFormFragment");

        if (resultCode != RESULT_OK) {
            if (customViewCreate!=null)
                customViewCreate.clearFocusOfAllViews(menuFragParentLL);
            return;
        }

//        if(data != null) {
            switch (requestCode) {
                case CAPTURE_PHOTO:
                    Log.d("imagePath", "menu form fragment");
                    imageSelectionUtils.chooseFromCameraImgPath(data, null);
                    break;

                case CHOOSE_FROM_GALLERY:

                case RECORD_VIDEO:
                    imageSelectionUtils.chooseFromGalleryImgPath(data, null);
                    break;

                case RC_DROPDOWN:
                    Log.d("DDPathCheck", "menu form fragment");
                    customViewCreate.updateDropdownViewsData(data.getExtras(), menuFragParentLL, sectionRequired);
                    break;

                case OTHER_FILES:
                    imageSelectionUtils.chooseFromFilePath(data, null);
                    break;
            }
//        }
    }

    private void populateData() {
//        if more than one section then do decision based on that accordingly
        menuFragParentLL.removeAllViews();
        for (FormSectionInfo formSectionInfo : formSectionInfos) {
            customViewCreate.createViews(formSectionInfo, menuFragParentLL, sectionRequired, null, false, fragMenuItemSV , getActivity());
        }
    }

    @Override
    public void showImagePickerDialog(CustomNetworkImageView view) {
        Log.d("imagePath" , "image selection utils menu form fragment");
        imageSelectionUtils = new ImageSelectionUtils(baseActivity, view);
        imageSelectionUtils.showImagePickerDialog(null, false, false);
    }

    @Override
    public void showFilePickerDialog(CustomNetworkImageView view) {
        Log.d("imagePath" , "image selection utils menu form fragment");
        imageSelectionUtils = new ImageSelectionUtils(baseActivity, view);
        imageSelectionUtils.showFilePickerDialog(null, false, false);
    }

    @Override
    public void onLabelViewClicked(PFASectionTV pfaSectionTV) {
        baseActivity.sharedPrefUtils.showToast(pfaSectionTV.getText().toString());
    }

    @Override
    public void onButtonCLicked(View view) {
        switch (view.getTag().toString()) {
            case "get_code_button":
                onClickGetCodeBtn(view, null );

                break;
            case "submit":

                (new PFAFormSubmitUtil((BaseActivity) getActivity())).submitForm((PFAButton) view, sectionRequired, menuFragParentLL, new HttpResponseCallback() {
                    @Override
                    public void onCompleteHttpResponse(final JSONObject response, String requestUrl) {

                        if (response != null && response.optBoolean("status")) {
                            if (response.has("detailMenu")) {
                                codeVerified = false;
                                /////////////// If single menu then do API call for it
                                Type type = new TypeToken<List<PFAMenuInfo>>() {
                                }.getType();
                                JSONArray menusJsonArray = response.optJSONObject("detailMenu").optJSONArray("menus");
                                List<PFAMenuInfo> pfaMenuInfos = new GsonBuilder().create().fromJson(menusJsonArray.toString(), type);

                                if (pfaMenuInfos != null && pfaMenuInfos.size() == 1) {
                                    if (pfaMenuInfos.get(0).getAPI_URL().contains("fbo_product_detail")){
                                        baseActivity.getSharedPreferences("appPrefs" , Context.MODE_PRIVATE).edit().putBoolean("CheckListUpdated" , true).apply();
                                    } else
                                        baseActivity.getSharedPreferences("appPrefs" , Context.MODE_PRIVATE).edit().putBoolean("CheckListUpdated" , false).apply();
//                                    if (pfaMenuInfos.get(0).getBack_API_URL().contains("fbo_product_listings")){
//                                        baseActivity.getSharedPreferences("appPrefs" , Context.MODE_PRIVATE).edit().putString("BackUrl" , pfaMenuInfos.get(0).getBack_API_URL()).apply();
//                                    }
//                                    else {

                                        final Bundle bundle = new Bundle();
                                        bundle.putString(EXTRA_URL_TO_CALL, pfaMenuInfos.get(0).getAPI_URL());

                                        baseActivity.httpService.getListsData(pfaMenuInfos.get(0).getAPI_URL(), new HashMap<String, String>(), new HttpResponseCallback() {
                                            @Override
                                            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                                if (response != null)
                                                    bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                                                Log.d("submitCheckList" , "here");
                                                baseActivity.sharedPrefUtils.startNewActivity(PFADetailActivity.class, bundle, true);
                                            }
                                        }, false);
//                                    }
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(EXTRA_DETAIL_MENU, response.toString());
                                    baseActivity.sharedPrefUtils.startNewActivity(PFADetailActivity.class, bundle, true);
                                }
                            } else {
                                if (baseActivity.isTaskRoot()) {
                                    if (String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase((baseActivity.sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, "")))) {
                                        baseActivity.sharedPrefUtils.startHomeActivity(FBOMainGridActivity.class, null);
                                    }

                                } else {
                                    Intent intent = new Intent();
                                    AppConst.DO_REFRESH = true;
                                    baseActivity.setResult(RESULT_OK, intent);
                                    baseActivity.finish();
                                }


                            }
                        } else {
                            if (response != null)
                                baseActivity.sharedPrefUtils.showMsgDialog("" + (response.optString("message_code").toUpperCase()), null);
                            else
                                baseActivity.sharedPrefUtils.showMsgDialog("" + null, null);
                        }
                    }
                }, true);

                break;

            case "button":
                PFAButton button = (PFAButton) view;

                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_URL_TO_CALL, "" + (button.getButtonUrl()));
                if (button.getFormFieldInfo() != null)
                    bundle.putString(EXTRA_ACTIVITY_TITLE, baseActivity.sharedPrefUtils.isEnglishLang()?button.getFormFieldInfo().getValue():button.getFormFieldInfo().getValueUrdu());
                else
                    bundle.putString(EXTRA_ACTIVITY_TITLE, baseActivity.sharedPrefUtils.isEnglishLang()?button.getPFATableInfo().getValue():button.getPFATableInfo().getValueUrdu());
                baseActivity.sharedPrefUtils.startNewActivity(PFADetailActivity.class, bundle, false);
                break;
        }
    }

    @Override
    public void onClickGetCodeBtn(View view, VerifyFBOLayout verifyFBOLayout ) {
        PFAViewsUtils pfaViewsUtils = new PFAViewsUtils(baseActivity);

        PFAButton get_code_button = (PFAButton) view;
        pfaViewsUtils.getVerificationCode(menuFragParentLL, get_code_button.getButtonUrl(), view, verifyFBOLayout);
        Log.d("onCreateActv" , "MenuFormsFragment" + "onClickGetCodeBtn123");
    }

    @Override
    public void onDropdownItemSelected(FormDataInfo formDataInfo, String dataName) {
    }

    private void refreshData() {
        if (urlToCall != null) {
            baseActivity.httpService.getListsData(urlToCall, new HashMap<String, String>(), MenuFormFragment.this, true);
        }
    }

    @Override
    public void onDDDataSelected(final FormDataInfo formDataInfo) {
        baseActivity.sharedPrefUtils.printLog("MenuFormFragment", "onDDDataSelected");

        customViewCreate.onDDSelectedAPIUrl(formDataInfo, menuFragParentLL, sectionRequired, fragMenuItemSV, formSectionInfos);

    }
}

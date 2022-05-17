package com.pfa.pfaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.customviews.PFAChatLayout;
import com.pfa.pfaapp.customviews.PFAListItem;
import com.pfa.pfaapp.customviews.PFASideMenuRB;
import com.pfa.pfaapp.fragments.*;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.RBClickCallback;
import com.pfa.pfaapp.localdbmodels.InspectionInfo;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.pfa.pfaapp.utils.AppConst.*;

public class PFADetailActivity extends BaseActivity implements HttpResponseCallback, RBClickCallback {

    private int lastClicked = -1;

    RadioGroup topbarRG;
    FrameLayout detailSectionsFL;
    List<Fragment> menuItemFragments = new ArrayList<>();

    HorizontalScrollView menubarHSV;

    //     Detail view (non editable)
    List<PFATableInfo> tableData;
    List<PFAMenuInfo> pfaMenuInfos;

    private PFAListItem pfaListItem;
    private List<String> columnTags = new ArrayList<>();
    RelativeLayout detailRL;
    ScrollView detailDataSV;
    String urlToCall;
    LinearLayout chatLL;
    PFAChatLayout pfaChatLayout;

    ImageView print_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pfadetail);
        setupWindowAnimations((ViewGroup) findViewById(R.id.viewGroup));


        MenuMapFragment.businessLatLng = null;

        Log.d("onCreateActv" , "PFADetailActivity");

        chatLL = findViewById(R.id.chatLL);

        topbarRG = findViewById(R.id.topbarRG);
        detailSectionsFL = findViewById(R.id.detailSectionsFL);
        menubarHSV = findViewById(R.id.menubarHSV);
        detailDataSV = findViewById(R.id.detailDataSV);

        pfaListItem = new PFAListItem(this);

        getIntentData();

        startLocation();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
       sharedPrefUtils.hideProgressDialog();
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(EXTRA_URL_TO_CALL)) {
            urlToCall = getIntent().getStringExtra(EXTRA_URL_TO_CALL);
            Log.d("enfrocementData" , "urltocall = " + urlToCall);
            Log.d("enfrocementData2312" , "urltocall = " + urlToCall);

            if (urlToCall != null) {
                if (getIntent().hasExtra(EXTRA_JSON_STR_RESPONSE)) {
                    String responseStr = getIntent().getStringExtra(EXTRA_JSON_STR_RESPONSE);
                    try {
                        JSONObject responseJSONObject = new JSONObject(responseStr);
                        onCompleteHttpResponse(responseJSONObject, urlToCall);
                        Log.d("BusinessDetailsMenu", "PFADetailActivity = " );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    httpService.getListsData(urlToCall, new HashMap<String, String>(), this, true);

                }

            }
        } else if (bundle != null && bundle.containsKey(EXTRA_DETAIL_MENU)) {
            try {
                JSONObject response = new JSONObject((bundle.getString(EXTRA_DETAIL_MENU)));
                onCompleteHttpResponse(response, "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClickBackImgBtn(View view) {
        if (isTaskRoot()) {
            if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") == null)
                finish();
            else {
                if (String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase((sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, "")))) {
                    sharedPrefUtils.startNewActivity(FBOMainGridActivity.class, null, true);
                } else {
                    sharedPrefUtils.startNewActivity(PFADrawerActivity.class, null, true);
                }
            }

        } else {
            super.onClickBackImgBtn(view);
        }
    }

    @Override
    public void onBackPressed() {


        Log.d("submitCheckList" , "here1");
        if (isTaskRoot()) {
            if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") == null)
                finish();
            else {
                if (String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase((sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, "")))) {
                    sharedPrefUtils.startNewActivity(FBOMainGridActivity.class, null, true);
                    Log.d("submitCheckList" , "here2");
                } else {
                    Log.d("submitCheckList" , "here3");
                    sharedPrefUtils.startNewActivity(PFADrawerActivity.class, null, true);
                }
            }
        } else {
            Log.d("submitCheckList" , "here4");
            boolean checkListUpdated = getSharedPreferences("appPrefs" , Context.MODE_PRIVATE).getBoolean("CheckListUpdated" , false);
//            String BackUrl = getSharedPreferences("appPrefs" , Context.MODE_PRIVATE).getString("BackUrl" , null);

            if (checkListUpdated) {
                Bundle bundle = null;
                final List<PFAMenuInfo> pfaMenuInfos;
                int inspecPos = getSharedPreferences("appPrefs1", Context.MODE_PRIVATE).getInt("inspecPos1", 0);
                String title = getSharedPreferences("appPrefs1", Context.MODE_PRIVATE).getString("EXTRA_ACTIVITY_TITLE", "");

                SharedPreferences appSharedPrefs = getSharedPreferences("AppPref1", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = appSharedPrefs.getString("inspec1", "");
                Type type = new TypeToken<List<PFAMenuInfo>>() {
                }.getType();
//        inspectionInfo = new GsonBuilder().create().fromJson(json, type);
                pfaMenuInfos = gson.fromJson(json, type);

                Log.d("BundleData", "inspecPos = " + inspecPos);

                bundle = new Bundle();
                if (pfaMenuInfos != null) {
                    bundle.putSerializable(EXTRA_PFA_MENU_ITEM, pfaMenuInfos.get(inspecPos));
                    bundle.putString(EXTRA_ACTIVITY_TITLE, title);
                }
                    sharedPrefUtils.startNewActivity(MapsActivity.class, bundle, false);

//                getSharedPreferences("appPrefs" , Context.MODE_PRIVATE).edit().putBoolean("CheckListUpdated" , false).apply();
            }
            else
                finish();
//            if (String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase((sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, "")))) {
//                sharedPrefUtils.startNewActivity(FBOMainGridActivity.class, null, true);
//                Log.d("submitCheckList" , "here2");
//            } else {
//                Log.d("submitCheckList" , "here3");
//                sharedPrefUtils.startNewActivity(PFADrawerActivity.class, null, true);
//            }
        }
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

        if (response != null && response.optBoolean("status")) {
            try {
                Type type = new TypeToken<List<PFATableInfo>>() {
                }.getType();

                if (response.has("data")) {
                    Log.d("enfrocementData" , "dada = " + response);
                    JSONArray formJSONArray = response.getJSONArray("data");

                    tableData = new GsonBuilder().create().fromJson(formJSONArray.toString(), type);

                    if (tableData != null && tableData.size() > 0) {
                        detailRL = pfaListItem.createViews(tableData, columnTags, true);
                    }
                    menubarHSV.setVisibility(View.GONE);
                }

                if (response.has("title")) {
                    setTitle(sharedPrefUtils.isEnglishLang() ? response.optString("title") : response.optString("titleUrdu"), true);
                }
                if (response.has("detailMenu")) {
                    type = new TypeToken<List<PFAMenuInfo>>() {
                    }.getType();
                    JSONArray menusJsonArray = response.getJSONObject("detailMenu").getJSONArray("menus");
                    Log.d("enfrocementData" , "menus = " + response.getJSONObject("detailMenu").getJSONArray("menus"));
                    pfaMenuInfos = new GsonBuilder().create().fromJson(menusJsonArray.toString(), type);

                    if (pfaMenuInfos != null && pfaMenuInfos.size() > 0) {
                        populateHorizontalMenu(menusJsonArray);
                    }
                    if (response.getJSONObject("detailMenu").has("title")) {
                        setTitle(sharedPrefUtils.isEnglishLang() ? response.getJSONObject("detailMenu").getString("title") : response.getJSONObject("detailMenu").getString("titleUrdu"), true);
                    }
                }

                JSONObject chat_sectionJsonObject = null;
                if (response.has("chat_section")) {
                    chat_sectionJsonObject = response.getJSONObject("chat_section");
                }
                JSONObject status_sectionObject = null;
                if (response.has("status_section")) {
                    status_sectionObject = response.getJSONObject("status_section");
                }
                //                    Testing the chat view
                if (chat_sectionJsonObject != null || status_sectionObject != null) {

                    if (response.has("images_section")) {
                        if (response.getJSONObject("images_section").has("fields")) {
                            List<PFATableInfo> imagesList = new GsonBuilder().create().fromJson(response.getJSONObject("images_section").optJSONArray("fields").toString(), type);
                            pfaChatLayout = new PFAChatLayout(PFADetailActivity.this, chat_sectionJsonObject, urlToCall, status_sectionObject, detailRL, imagesList);
                        }
                    } else {
                        pfaChatLayout = new PFAChatLayout(PFADetailActivity.this, chat_sectionJsonObject, urlToCall, status_sectionObject, detailRL, null);
                    }

                    chatLL.removeAllViews();
                    chatLL.addView(pfaChatLayout);
                } else {
                    if (detailDataSV != null && detailRL != null)
                        detailDataSV.addView(detailRL);
                }
            } catch (JSONException e) {
                sharedPrefUtils.printStackTrace(e);
            }
        } else {
            if (response == null) {
                sharedPrefUtils.showMsgDialog("No data received from server", null);
            } else {
                sharedPrefUtils.showMsgDialog("" + (response.optString("message_code")), null);
            }
        }
    }

    private void populateHorizontalMenu(JSONArray menusJsonArray) {
        menubarHSV.setVisibility(View.VISIBLE);
        detailSectionsFL.setVisibility(View.VISIBLE);
        new PFASideMenuRB(this, topbarRG, pfaMenuInfos, this, false);

        if (pfaMenuInfos.size() == 1) {
            if (pfaMenuInfos.get(0).getMenuItemName() == null || pfaMenuInfos.get(0).getMenuItemName().isEmpty()) {
                menubarHSV.setVisibility(View.GONE);
            }
        }

        if (pfaMenuInfos != null && pfaMenuInfos.size() > 0) {
            for (int i = 0; i < pfaMenuInfos.size(); i++) {
                PFAMenuInfo pfaMenuInfo = pfaMenuInfos.get(i);
                Fragment menuItemFragment;

                Log.d("enfrocementData" , "type = " + pfaMenuInfo.getMenuType());

//            "form", "list", "googlemap", "profile","search"
                switch (pfaMenuInfo.getMenuType()) {
                    case "form":

                        Log.d("enfrocementData" , "type = form");

                        if (menusJsonArray.optJSONObject(i).has("data")) {
                            menuItemFragment = MenuFormFragment.newInstance(pfaMenuInfo, menusJsonArray.optJSONObject(i).optJSONObject("data"));
                        } else {
                            menuItemFragment = MenuFormFragment.newInstance(pfaMenuInfo, null);
                        }

                        break;
                    case "list":
                        if (pfaMenuInfo.getMenuItemName().equalsIgnoreCase("De-Seize")) {
//                            try {
//                                JSONObject deseizeJSONObject = sharedPrefUtils.getJSONFromAssetFile("de_seize_data.json");
//                                menusJsonArray.getJSONObject(i).put("table", deseizeJSONObject);
//                                menuItemFragment = MenuListFragment.newInstance(pfaMenuInfo, false, false, false, deseizeJSONObject);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                            Log.d("checkListUpdated" , "pfaDetailActv = MenuListFragment1");
                            menuItemFragment = MenuListFragment.newInstance(pfaMenuInfo, false, false, false, menusJsonArray.optJSONObject(i).optJSONObject("table"));
                        } else if (menusJsonArray.optJSONObject(i).has("table")) {
                            Log.d("checkListUpdated" , "pfaDetailActv = MenuListFragment2");
                            menuItemFragment = MenuListFragment.newInstance(pfaMenuInfo, false, false, false, menusJsonArray.optJSONObject(i).optJSONObject("table"));
                        } else {
                            Log.d("checkListUpdated" , "pfaDetailActv = MenuListFragment3");
                            menuItemFragment = MenuListFragment.newInstance(pfaMenuInfo, false, false, false, null);
                        }

                        break;
                    case "googlemap":
                        menuItemFragment = MenuMapFragment.newInstance(pfaMenuInfo, null);
                        break;
                    case "dashboard":
                    case "grid":
                        menuItemFragment = MenuGridFragment.newInstance(pfaMenuInfo);
                        break;

                    default:
                        Log.d("enfrocementData" , "type = other");
                        menuItemFragment = MenuFormFragment.newInstance(pfaMenuInfo, null);
                        break;
                }

                if (menuItemFragment != null)
                    menuItemFragments.add(menuItemFragment);
            }
            lastClicked = 0;

            populatePager();

            if (topbarRG.getChildCount() > 0)
                topbarRG.getChildAt(0).performClick();
        }
    }

    private void replaceFragment() {

        if (fragmentManager == null)
            fragmentManager = getSupportFragmentManager();


        for (int i = 0; i < menuItemFragments.size(); i++) {
            try {
                fragmentManager.beginTransaction().hide(menuItemFragments.get(i)).commit();

            } catch (Exception e) {
                fragmentManager.beginTransaction().hide(menuItemFragments.get(i)).commitAllowingStateLoss();
            }
        }
        try {
            if (lastClicked < menuItemFragments.size())

                fragmentManager.beginTransaction()
                        .show(menuItemFragments.get(lastClicked))
                        .commit();

        } catch (Exception e) {
            if (lastClicked < menuItemFragments.size())
                fragmentManager.beginTransaction()
                        .show(menuItemFragments.get(lastClicked))
                        .commitAllowingStateLoss();
        }
    }

    FragmentManager fragmentManager;

    private void addAllFragments() {
        {
            if (fragmentManager == null)
                fragmentManager = getSupportFragmentManager();

            for (int i = 0; i < menuItemFragments.size(); i++) {
                try {
                    fragmentManager.beginTransaction()
                            .add(R.id.detailSectionsFL, menuItemFragments.get(i))
                            .commit();

                    fragmentManager.beginTransaction().hide(menuItemFragments.get(i)).commit();

                } catch (Exception e) {
                    fragmentManager.beginTransaction()
                            .add(R.id.detailSectionsFL, menuItemFragments.get(i))
                            .commitAllowingStateLoss();

                    fragmentManager.beginTransaction().hide(menuItemFragments.get(i)).commitAllowingStateLoss();
                }
            }
        }
    }

    private void populatePager() {
        addAllFragments();

    }

    @Override
    public void onClickRB(View targetView) {
        lastClicked = targetView.getId();
        replaceFragment();

        topbarRG.clearCheck();
        ((RadioButton) targetView).setChecked(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("imagePath" , "onActivityResult = " + "PFADetailsActivity");

        if (lastClicked >= 0) {
            menuItemFragments.get(lastClicked).onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == RC_DROPDOWN) {
            if (data != null && data.getExtras() != null) {

                AppConst.DO_REFRESH = true;
                pfaChatLayout.customViewCreate.updateDropdownViewsData(data.getExtras(), pfaChatLayout, null);
            }
        }
    }

}

/*
 * Copyright (c) 2018. All Rights Reserved by Punjab Food Authority
 */

package com.pfa.pfaapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.PFASideMenuRB;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.ListDataFetchedInterface;
import com.pfa.pfaapp.interfaces.RBClickCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.utils.AppConst;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_MENU_ITEM_NAME;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.SP_INSPECTIONS_MENU;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabbedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabbedFragment extends Fragment implements HttpResponseCallback, RBClickCallback {
    private int lastClicked = -1;

    private RadioGroup topbarRG;
    private FrameLayout detailSectionsCVP;
    private List<Fragment> menuItemFragments = new ArrayList<>();

    private HorizontalScrollView menubarHSV;
    private BaseActivity baseActivity;
    private String urlToCall;
    private List<PFAMenuInfo> pfaMenuInfos;
    private boolean isDrawer;
    private FragmentManager fragmentManager;

    private boolean isInspectionsTabBar;

    public TabbedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pfaMenuInfo Parameter 1.
     * @return A new instance of fragment TabbedFragment.
     */
    public static TabbedFragment newInstance(PFAMenuInfo pfaMenuInfo, boolean isDrawer) {
        TabbedFragment fragment = new TabbedFragment();
        Bundle args = new Bundle();
        if (pfaMenuInfo != null && pfaMenuInfo.getAPI_URL() != null) {
            args.putString(EXTRA_URL_TO_CALL, pfaMenuInfo.getAPI_URL());
            args.putString(EXTRA_MENU_ITEM_NAME, pfaMenuInfo.getMenuItemName());
        }
        args.putBoolean("isDrawer", isDrawer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);
        topbarRG = rootView.findViewById(R.id.topbarRG);
        detailSectionsCVP = rootView.findViewById(R.id.detailSectionsCVP);
        menubarHSV = rootView.findViewById(R.id.menubarHSV);

        Log.d("onCreateActv" , "TabbedFragment");

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        baseActivity = (BaseActivity) getActivity();
        if (getArguments() != null) {
            urlToCall = getArguments().getString(EXTRA_URL_TO_CALL);
            isDrawer = getArguments().getBoolean("isDrawer");

            if (getArguments().containsKey(EXTRA_MENU_ITEM_NAME)) {
                isInspectionsTabBar = true;
            }
            refreshData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lastClicked >= 0) {
            menuItemFragments.get(lastClicked).onResume();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("imagePath" , "onActivityResult = " + "TabbedFragment");

        if (lastClicked >= 0) {
            menuItemFragments.get(lastClicked).onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        if (response != null && response.optBoolean("status")) {
            try {
                if (response.has("data")) {

                    JSONObject dataJsonObject = response.optJSONObject("data");
                    if (dataJsonObject.has("detailMenu")) {
                        setMenus(dataJsonObject.getJSONObject("detailMenu").getJSONArray("menus"));

                    } else if (dataJsonObject.has("menus")) {
                        setMenus(dataJsonObject.getJSONArray("menus"));
                    }
                }

            } catch (JSONException e) {
                baseActivity.sharedPrefUtils.printStackTrace(e);
            }
        }
    }

    private void setMenus(JSONArray menusJsonArray) {
        Type type = new TypeToken<List<PFAMenuInfo>>() {
        }.getType();

        if (isInspectionsTabBar) {
            baseActivity.sharedPrefUtils.saveSharedPrefValue(SP_INSPECTIONS_MENU, menusJsonArray.toString());
        }

        pfaMenuInfos = new GsonBuilder().create().fromJson(menusJsonArray.toString(), type);

        if (pfaMenuInfos != null && pfaMenuInfos.size() > 0) {
            populateHorizontalMenu();
        }
    }

    @Override
    public void onClickRB(View targetView) {
        lastClicked = targetView.getId();
        replaceFragment();
        topbarRG.clearCheck();
        ((RadioButton) targetView).setChecked(true);

        if (menuItemFragments.get(lastClicked) instanceof MenuListFragment) {
            if (((MenuListFragment) menuItemFragments.get(lastClicked)).isResetData()) {
                baseActivity.removeFilter();

                ((MenuListFragment) (menuItemFragments.get(lastClicked))).doAPICall();
            }

        } else if (menuItemFragments.get(lastClicked) instanceof DraftsFragment) {
            ((DraftsFragment) (menuItemFragments.get(lastClicked))).populateData();
        }

        hideShowFilters();
    }

    private void hideShowFilters() {
        baseActivity.searchFilterFL.setVisibility(View.GONE);
        if (menuItemFragments != null && lastClicked < menuItemFragments.size() && menuItemFragments.get(lastClicked) instanceof MenuListFragment) {
            if (((MenuListFragment) menuItemFragments.get(lastClicked)).showFilter) {

                baseActivity.filterCountTV.setText("");
                baseActivity.filterCountTV.setVisibility(View.GONE);
                baseActivity.searchFilterFL.setVisibility(View.VISIBLE);
            }
        }
    }

    public Fragment getCurrentFragment() {
        return menuItemFragments.get(lastClicked);
    }


    private void addAllFragments() {

        if (fragmentManager == null)
            fragmentManager = baseActivity.getSupportFragmentManager();

        for (int i = 0; i < menuItemFragments.size(); i++) {
            if(detailSectionsCVP!=null)
            {
                try {
                    fragmentManager.beginTransaction().add(R.id.detailSectionsCVP, menuItemFragments.get(i)).commit();
                    fragmentManager.beginTransaction().hide(menuItemFragments.get(i)).commit();

                } catch (Exception e) {
                    fragmentManager.beginTransaction()
                            .add(R.id.detailSectionsCVP, menuItemFragments.get(i))
                            .commitAllowingStateLoss();

                    fragmentManager.beginTransaction().hide(menuItemFragments.get(i)).commitAllowingStateLoss();
                }
            }

        }

    }


    private void replaceFragment() {

        if (fragmentManager == null)
            fragmentManager = baseActivity.getSupportFragmentManager();

        for (int i = 0; i < menuItemFragments.size(); i++) {
            try {
                fragmentManager.beginTransaction().hide(menuItemFragments.get(i)).commit();

            } catch (Exception e) {
                fragmentManager.beginTransaction().hide(menuItemFragments.get(i)).commitAllowingStateLoss();
            }
        }

        if (menuItemFragments.size() > 0 && lastClicked < menuItemFragments.size()) {
            try {
                fragmentManager.beginTransaction().show(menuItemFragments.get(lastClicked)).commit();

            } catch (Exception e) {
                baseActivity.sharedPrefUtils.printStackTrace(e);
                fragmentManager.beginTransaction().show(menuItemFragments.get(lastClicked)).commitAllowingStateLoss();
            }
        }
    }

    private void clearViews() {
        menubarHSV.setVisibility(View.GONE);
        if (menuItemFragments != null && menuItemFragments.size() > 0) {
            menuItemFragments.clear();
        }
        if (topbarRG != null && topbarRG.getChildCount() > 0) {
            topbarRG.removeAllViews();
        }
    }

    private void populateHorizontalMenu() {
        clearViews();
        menubarHSV.setVisibility(View.VISIBLE);
        detailSectionsCVP.setVisibility(View.VISIBLE);

        if (pfaMenuInfos != null && pfaMenuInfos.size() > 0) {
            if (baseActivity.httpService.isNetworkDisconnected()) {
                if (pfaMenuInfos.get(0).getMenuItemName().contains("Draft")) {
                    for (int i = 0; i < pfaMenuInfos.size(); i++) {
                        if (i != 0)
                            pfaMenuInfos.get(i).setMenuItemName("");
                    }
                }
            }

            new PFASideMenuRB(getContext(), topbarRG, pfaMenuInfos, this, false);

            topbarRG.setVisibility(View.VISIBLE);
            for (final PFAMenuInfo pfaMenuInfo : pfaMenuInfos) {

                Fragment menuItemFragment;

                switch (pfaMenuInfo.getMenuType()) {
                    case "list":
                        menuItemFragment = MenuListFragment.newInstance(pfaMenuInfo, false, isDrawer, isDrawer, null);
                        ((MenuListFragment) menuItemFragment).setFetchDataInterface(new ListDataFetchedInterface() {
                            @Override
                            public void listDataFetched() {
                                hideShowFilters();
                            }
                        });
                        ((MenuListFragment) menuItemFragment).setSendMessageCallback(new SendMessageCallback() {
                            @Override
                            public void sendMsg(String message) {
                                if (message != null && (!message.isEmpty())) {
                                    RadioButton radioButton = topbarRG.findViewWithTag(pfaMenuInfo.getMenuItemName());
                                    if (radioButton != null) {
                                        radioButton.setText(message);
                                    }
                                }
                            }
                        });

                        break;
                    case "googlemap":
                        menuItemFragment = MenuMapFragment.newInstance(pfaMenuInfo, null);
                        break;
                    case "dashboard":
                    case "grid":
                        menuItemFragment = MenuGridFragment.newInstance(pfaMenuInfo);
                        break;
                    case "draft":
                        AppConst.draftsRadioButton = topbarRG.findViewWithTag(pfaMenuInfo.getMenuItemName());
                        menuItemFragment = DraftsFragment.newInstance(pfaMenuInfo, new SendMessageCallback() {
                            @Override
                            public void sendMsg(String message) {
                                AppConst.draftsRadioButton.setText(message);
                            }
                        });
                        break;
                    default:
                        menuItemFragment = MenuFormFragment.newInstance(pfaMenuInfo, null);
                        break;
                }

                if (menuItemFragment != null)
                    menuItemFragments.add(menuItemFragment);
            }


            populatePager();
            baseActivity.sharedPrefUtils.printLog("topbarRG", topbarRG == null ? "null" : "topbarRG Size=>" + topbarRG.getChildCount());
            if (topbarRG.getChildCount() > 0) {
                topbarRG.getChildAt(0).performClick();
            }
        }
    }

    private void populatePager() {
        addAllFragments();
    }

    public void refreshData() {
        if (urlToCall != null) {

            if (pfaMenuInfos == null || pfaMenuInfos.size() == 0) {
                if (baseActivity.sharedPrefUtils.getSharedPrefValue(SP_INSPECTIONS_MENU, "") == null) {
                    baseActivity.httpService.getListsData(urlToCall, new HashMap<String, String>(), TabbedFragment.this, false);
                } else if (isInspectionsTabBar) {
                    String menuJSONStr = baseActivity.sharedPrefUtils.getSharedPrefValue(SP_INSPECTIONS_MENU, "");
                    try {
                        setMenus(new JSONArray(menuJSONStr));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                if (menuItemFragments != null && menuItemFragments.size() > 0)
                    for (int i = 0; i < menuItemFragments.size(); i++)
                        if (menuItemFragments.get(i) instanceof MenuListFragment) {
                            ((MenuListFragment) menuItemFragments.get(i)).onRefreshListener.onRefresh();
                        }
            }
        }
    }

}

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CiTabbedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class CiTabbedFragment extends Fragment implements HttpResponseCallback, RBClickCallback {
    private int lastClicked = -1;

    private RadioGroup topbarRG;
    private FrameLayout detailSectionsCICVP;
    private List<Fragment> menuItemFragments = new ArrayList<>();

    private HorizontalScrollView menubarHSV;
    private BaseActivity baseActivity;
    private String urlToCall;
    private List<PFAMenuInfo> pfaMenuInfos;
    private boolean isDrawer;
    private boolean isInspectionsTabBar;

    private FragmentManager fragmentManager;

    public CiTabbedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pfaMenuInfo Parameter 1.
     * @return A new instance of fragment CiTabbedFragment.
     */
    public static CiTabbedFragment newInstance(PFAMenuInfo pfaMenuInfo, boolean isDrawer) {
        CiTabbedFragment fragment = new CiTabbedFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_ci_tabbed, container, false);
        topbarRG = rootView.findViewById(R.id.topbarCIRG);
        detailSectionsCICVP = rootView.findViewById(R.id.detailSectionsCICVP);
        menubarHSV = rootView.findViewById(R.id.menubarCIHSV);
        Log.d("onCreateActv" , "CiTabbedFragment");
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
            getCurrentFragment().onResume();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("imagePath" , "onActivityResult = " + "CiTabbedFragment");

        if (lastClicked >= 0) {
            getCurrentFragment().onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        if (response != null) {
            if (response.optBoolean("status")) {
                try {
                    if (response.has("data")) {
                        JSONObject dataJsonObject = response.optJSONObject("data");
                        if (dataJsonObject.has("detailMenu")) {
                            setMenus(dataJsonObject.getJSONObject("detailMenu").getJSONArray("menus"));

                        } else if (dataJsonObject.has("menus")) {
                            setMenus(dataJsonObject.getJSONArray("menus"));
                            Log.d(":cittabbed" , "enforcement menu");
                        }
                    }

                } catch (JSONException e) {
                    baseActivity.sharedPrefUtils.printStackTrace(e);
                }
            }
        }
    }

    private void setMenus(JSONArray menusJsonArray) {
        Type type = new TypeToken<List<PFAMenuInfo>>() {
        }.getType();

        if (menuItemFragments != null && menuItemFragments.size() > 0) {
            menuItemFragments.clear();
        }
        pfaMenuInfos = new GsonBuilder().create().fromJson(menusJsonArray.toString(), type);

        if (pfaMenuInfos != null && pfaMenuInfos.size() > 0) {
            populateHorizontalMenu();
        }
    }


    @Override
    public void onClickRB(View targetView) {
        lastClicked = targetView.getId();
        try {
            replaceFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
        topbarRG.clearCheck();
        ((RadioButton) targetView).setChecked(true);

        try {
            if (getCurrentFragment() instanceof MenuListFragment) {
                if (((MenuListFragment) getCurrentFragment()).isResetData()) {
                    baseActivity.removeFilter();

                    ((MenuListFragment) getCurrentFragment()).doAPICall();
                }

            } else if (getCurrentFragment() instanceof DraftsFragment) {
                ((DraftsFragment) (getCurrentFragment())).populateData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        hideShowFilters();
    }

    private void hideShowFilters() {
        baseActivity.searchFilterFL.setVisibility(View.GONE);
        if (lastClicked > -1 && menuItemFragments != null && lastClicked < menuItemFragments.size() && menuItemFragments.get(lastClicked) instanceof MenuListFragment) {
            if (((MenuListFragment) getCurrentFragment()).showFilter) {

                baseActivity.filterCountTV.setText("");
                baseActivity.filterCountTV.setVisibility(View.GONE);
                baseActivity.searchFilterFL.setVisibility(View.VISIBLE);
            }
        }
    }

    public Fragment getCurrentFragment() {
//        if (lastClicked < menuItemFragments.size() && menuItemFragments.get(lastClicked) != null)
            return menuItemFragments.get(lastClicked);
//        else
//            return menuItemFragments.get(lastClicked-1);
    }


    private void addAllFragments() {

        //////////////
        if (fragmentManager == null)
            fragmentManager = getFragmentManager();


        if (fragmentManager != null) {
            for (int i = 0; i < menuItemFragments.size(); i++) {
                if (detailSectionsCICVP != null) {
                    try {
                        fragmentManager.beginTransaction().add(detailSectionsCICVP.getId(), menuItemFragments.get(i)).commit();
                        fragmentManager.beginTransaction().hide(menuItemFragments.get(i)).commit();

                    } catch (Exception e) {
                        fragmentManager.beginTransaction().add(detailSectionsCICVP.getId(), menuItemFragments.get(i)).commitAllowingStateLoss();
                        fragmentManager.beginTransaction().hide(menuItemFragments.get(i)).commitAllowingStateLoss();
                    }
                }
            }
        }

        ///////////////

    }

    private void replaceFragment() {

        if (fragmentManager == null)
            fragmentManager = getFragmentManager();

        for (int i = 0; i < menuItemFragments.size(); i++) {
            try {
//                assert fragmentManager != null;
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
        if (detailSectionsCICVP != null)
            detailSectionsCICVP.setVisibility(View.VISIBLE);

        if (pfaMenuInfos != null && pfaMenuInfos.size() > 0) {
            if (baseActivity.httpService.isNetworkDisconnected()) {
                if (pfaMenuInfos.get(0).getMenuItemName().contains("Draft")) {
                    for (int i = 0; i < pfaMenuInfos.size(); i++) {
                        if (i != 0)
                            pfaMenuInfos.get(i).setMenuItemName("");
                    }
                }
            }

            new PFASideMenuRB(baseActivity, topbarRG, pfaMenuInfos, this, false);

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

                if (menuItemFragment != null) {
                    menuItemFragments.add(menuItemFragment);
                }
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

            if (isInspectionsTabBar) {
                lastClicked = -1;
                baseActivity.httpService.getListsData(urlToCall, new HashMap<String, String>(), CiTabbedFragment.this, false);
            } else {
                if (menuItemFragments != null && menuItemFragments.size() > 0)
                    for (int i = 0; i < menuItemFragments.size(); i++)
                        if (menuItemFragments.get(i) instanceof MenuListFragment) {
                            ((MenuListFragment) menuItemFragments.get(i)).onRefreshListener.onRefresh();
                        }

            }

        }
    }

}

package com.pfa.pfaapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.LocalFormsActivity;
import com.pfa.pfaapp.PFAAddNewActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.adapters.PFATableAdapter;
import com.pfa.pfaapp.customviews.CustomNetworkImageView;
import com.pfa.pfaapp.customviews.CustomViewCreate;
import com.pfa.pfaapp.customviews.PFASectionTV;
import com.pfa.pfaapp.customviews.PullAndLoadListView;
import com.pfa.pfaapp.customviews.VerifyFBOLayout;
import com.pfa.pfaapp.httputils.LocalFormHttpUtils;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.ListDataFetchedInterface;
import com.pfa.pfaapp.interfaces.PFAViewsCallbacks;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormSectionInfo;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_BIZ_FORM_DATA;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_FILTERS_DATA;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_ITEM_COUNT;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.RC_ACTIVITY;
import static com.pfa.pfaapp.utils.AppConst.RC_DROPDOWN;
import static com.pfa.pfaapp.utils.AppConst.RC_REFRESH_LIST;
import static com.pfa.pfaapp.utils.AppConst.SEARCH_DATA;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuListFragment extends Fragment implements HttpResponseCallback, WhichItemClicked {

    private PullAndLoadListView menuTableLV;
    private LinearLayout sorry_iv;

    private LinearLayout searchLL, newsSearchLL;
    private BaseActivity baseActivity;
    private PFATableAdapter pfaTableAdapter;
    private List<List<PFATableInfo>> tableData;
    public HashMap<String, List<FormDataInfo>> formFilteredData = new HashMap<>();
    private HashMap<String, HashMap<String, Boolean>> sectionRequired = new HashMap<>();
    private boolean fetchDataInProgress;
    private String nextUrl;
    private String add_newUrl = null, local_add_newUrl = null;
    private ImageButton addNewBtn;
    private String urlToCall;
    public List<FormSectionInfo> formSectionInfos;
    private boolean isDrawer;
    private boolean addHeader = true;

    private int lastScrolledPosition = 0;

    public boolean showFilter = false;
    private boolean resetData;

    private ListDataFetchedInterface listDataFetchedInterface;
    private SendMessageCallback sendMessageCallback;
    private View clickableWrapperView;
    private boolean showProgress;

    private ImageButton deseizeAllBtn;
    private CustomViewCreate customViewCreate;
    private boolean isDeseize;

    public MenuListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pfaMenuInfo Parameter 1.
     * @return A new instance of fragment MenuListFragment.
     */

    public static MenuListFragment newInstance(PFAMenuInfo pfaMenuInfo, boolean showSearch, boolean isDrawer, boolean showProgressDialog, JSONObject data) {

        MenuListFragment fragment = new MenuListFragment();
        Bundle args = new Bundle();
        if (pfaMenuInfo != null && pfaMenuInfo.getAPI_URL() != null) {
            args.putString(EXTRA_URL_TO_CALL, pfaMenuInfo.getAPI_URL());
            args.putString("MENU_NAME", "" + pfaMenuInfo.getMenuItemName());
            args.putSerializable("PFAMenuInfo", pfaMenuInfo);
        }

        if (pfaMenuInfo != null && pfaMenuInfo.getDeseize_ALL_API_URL() != null && (!pfaMenuInfo.getDeseize_ALL_API_URL().isEmpty())) {
            args.putString("Deseize_ALL_API_URL", pfaMenuInfo.getDeseize_ALL_API_URL());
        }
        args.putBoolean("isDrawer", isDrawer);
        args.putBoolean("showSearch", showSearch);
        args.putBoolean("showProgress", showProgressDialog);

        if (data != null) {
            args.putString(EXTRA_BIZ_FORM_DATA, data.toString());
        }

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(getActivity());

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_menu_table, container, false);
        menuTableLV = rootView.findViewById(R.id.menuTableLV);

        clickableWrapperView = rootView.findViewById(R.id.clickableWrapperView);

        searchLL = rootView.findViewById(R.id.searchLL);
        newsSearchLL = rootView.findViewById(R.id.newsSearchLL);

        sorry_iv = rootView.findViewById(R.id.sorry_iv12);

        addNewBtn = rootView.findViewById(R.id.addNewBtn);
        sharedPrefUtils.applyFont(addNewBtn, AppUtils.FONTS.HelveticaNeue);

        deseizeAllBtn = rootView.findViewById(R.id.deseizeAllBtn);

        Log.d("onCreateActv" , "MenuListFragment");

        menuTableLV.setOnRefreshListener(onRefreshListener);
        clickableWrapperView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                This is used to stop listView clicks while refreshing
            }
        });

        return rootView;
    }

    public PullAndLoadListView.OnRefreshListener onRefreshListener = new PullAndLoadListView.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Log.d("onResfreshListener" , "refresh 1 ");
            clickableWrapperView.setVisibility(View.VISIBLE);
            if (fetchDataInProgress) {
                Log.d("onResfreshListener" , "refresh 2 ");
                endRefresh();
                return;
            }
            Log.d("onResfreshListener" , "refresh 3 ");
            lastScrolledPosition = 0;
            clickableWrapperView.setVisibility(View.VISIBLE);
//            Log.e("On Refresh Called", "On refresh called");
            baseActivity.removeFilter();
            populateListMain();
        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        baseActivity = (BaseActivity) getActivity();
//        baseActivity.filterCountTV.setText(String.format(Locale.getDefault(), "%d", formFilteredData.size())+"1");
//        baseActivity.filterCountTV.setVisibility(View.VISIBLE);
        Log.d("onActivityCreated" , "onActivityCreated 1 ");
        populateListMain();
    }

    public void setFetchDataInterface(ListDataFetchedInterface listDataFetchedInterface) {
        this.listDataFetchedInterface = listDataFetchedInterface;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onActivityCreated" , "onActivityCreated 2 ");
        if (AppConst.DO_REFRESH || AppConst.BIZ_LOC_UPDATED) {

            if (AppConst.DO_REFRESH) {
                AppConst.DO_REFRESH = false;
            } else {
                if (urlToCall != null && urlToCall.contains("client/nearestBusinesses"))
                    AppConst.BIZ_LOC_UPDATED = false;
                else
                    return;
            }
            Log.d("onActivityCreated" , "onActivityCreated 3 ");
            baseActivity.removeFilter();
            populateListMain();
        }
    }

    private void populateListMain() {
        Log.d("onActivityCreated" , "onActivityCreated 4 ");
        if (tableData != null && tableData.size() > 0)
            tableData.clear();

        if (getArguments() != null) {
            urlToCall = getArguments().getString(EXTRA_URL_TO_CALL);
            assert getArguments() != null;
            isDrawer = getArguments().getBoolean("isDrawer");

            showProgress = getArguments().getBoolean("showProgress");

        } else {
            baseActivity.removeFilter();
        }

        if (getArguments() != null && getArguments().containsKey(EXTRA_BIZ_FORM_DATA)) {
            try {
                JSONObject dataJsonObject = new JSONObject(getArguments().getString(EXTRA_BIZ_FORM_DATA));

                populateTableData(dataJsonObject, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (urlToCall != null) {
            doAPICall();
        }

        addNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (baseActivity.httpService.isNetworkDisconnected())
                    return;

                addNewBtn.setClickable(false);
                addNewBtn.setEnabled(false);
                if (add_newUrl != null) {

                    final Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_URL_TO_CALL, add_newUrl);

                    baseActivity.httpService.getListsData(add_newUrl, new HashMap<String, String>(), new HttpResponseCallback() {
                        @Override
                        public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                            if (response != null)
                                bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                            Log.d("getListData" , "menuListFragment = 1" );
                            baseActivity.sharedPrefUtils.startActivityForResult(baseActivity, PFAAddNewActivity.class, bundle, RC_REFRESH_LIST);

                            addNewBtn.setEnabled(true);
                            addNewBtn.setClickable(true);
                        }
                    }, true);

                } else if (local_add_newUrl != null) {
                    final Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_URL_TO_CALL, local_add_newUrl);

                    baseActivity.httpService.getListsData(local_add_newUrl, new HashMap<String, String>(), new HttpResponseCallback() {
                        @Override
                        public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                            if (response != null)
                                bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                            Log.d("getListData" , "menuListFragment = 2" );
                            baseActivity.sharedPrefUtils.startActivityForResult(baseActivity, LocalFormsActivity.class, bundle, RC_REFRESH_LIST);

                            addNewBtn.setEnabled(true);
                            addNewBtn.setClickable(true);

                        }
                    }, true);
                } else {

                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addNewBtn.setEnabled(true);
                            addNewBtn.setClickable(true);
                        }
                    }, 2000);
                }
            }
        });
    }

    private void createSearchFilterView() {
        customViewCreate = new CustomViewCreate(getActivity(), new PFAViewsCallbacks() {
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

                if (formDataInfo == null) {
                    onRefreshListener.onRefresh();
                } else {
                    fetchDataInProgress = false;
                    HashMap<String, String> searchParams = new HashMap<>();
                    if (formDataInfo.getKey() != null && (!formDataInfo.getKey().isEmpty())) {

                        searchParams.put(formDataInfo.getName(), formDataInfo.getKey());
                    }
                    baseActivity.httpService.getListsData(formSectionInfos.get(0).getFields().get(0).getAPI_URL(), searchParams, MenuListFragment.this, true);
                    Log.d("getListData" , "menuListFragment = 3" );
                }
            }
        }, formFilteredData);
        populateSearchFilter();
    }

    private void populateSearchFilter() {
//        if more than one section then do decision based on that accordingly
        newsSearchLL.removeAllViews();
        for (FormSectionInfo formSectionInfo : formSectionInfos) {
            if (customViewCreate == null)
                createSearchFilterView();
            customViewCreate.createViews(formSectionInfo, newsSearchLL, sectionRequired, null, false, null);
        }
    }

    private void updateSearch() {
        fetchDataInProgress = true;
        baseActivity.httpService.getListsData(nextUrl, new HashMap<String, String>(), MenuListFragment.this, false);
        Log.d("getListData" , "menuListFragment = 4" );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("imagePath" , "onActivityResult = " + "MenuListFragment");

        if (requestCode == RC_ACTIVITY && data != null) {
            Bundle bundle = data.getExtras();

            assert bundle != null;
            formFilteredData = (HashMap<String, List<FormDataInfo>>) bundle.getSerializable(EXTRA_FILTERS_DATA);
            if (formFilteredData != null && formFilteredData.size() > 0) {
                baseActivity.sharedPrefUtils.printLog("Data=>", "" + (formFilteredData.toString()));
                baseActivity.filterCountTV.setText(String.format(Locale.getDefault(), "%d", formFilteredData.size()));
                baseActivity.filterCountTV.setVisibility(View.VISIBLE);
                String filterStr = "";

                for (String key : formFilteredData.keySet()) {
                    List<FormDataInfo> filterDataInfos = formFilteredData.get(key);
                    if (filterDataInfos != null && filterDataInfos.size() > 0) {
                        if (filterStr.equalsIgnoreCase("")) {
                            filterStr = String.format("%s%s", filterStr, filterDataInfos.get(0).getValue());
                        } else {
                            filterStr += ", " + filterDataInfos.get(0).getValue();
                        }
                    }
                }
            } else {
                nextUrl = null;
                baseActivity.removeFilter();
            }

            if (bundle.containsKey("activityTitle")){
                baseActivity.setTitle(bundle.getString("activityTitle" , "Businesses") , true);
            }

            if (bundle.containsKey(SEARCH_DATA)) {
                if (bundle.containsKey(EXTRA_ITEM_COUNT) && sendMessageCallback != null) {
                    sendMessageCallback.sendMsg(bundle.getString(EXTRA_ITEM_COUNT));
                }
                tableData = (List<List<PFATableInfo>>) bundle.getSerializable(SEARCH_DATA);
                if (bundle.containsKey(AppConst.EXTRA_NEXT_URL)) {
                    nextUrl = bundle.getString(AppConst.EXTRA_NEXT_URL);
                } else nextUrl = null;

                setResetData(true);
                setAdapterData();


            } else {
                if (nextUrl != null) {
                    nextUrl = null;
                    populateListMain();
                }
            }
        } else if (requestCode == RC_REFRESH_LIST && AppConst.DO_REFRESH) {
            AppConst.DO_REFRESH = false;
            populateListMain();
        } else if (requestCode == RC_DROPDOWN) {
            if (data != null)
                customViewCreate.updateDropdownViewsData(data.getExtras(), newsSearchLL, sectionRequired);
        }

    }



    private void setAdapterData() {
        if (tableData == null)
            tableData = new ArrayList<>();
//        sortTableData(tableData);
        isDeseize = false;

        assert getArguments() != null;
        if (getArguments().containsKey("PFAMenuInfo")) {
            PFAMenuInfo pfaMenuInfo = (PFAMenuInfo) getArguments().getSerializable("PFAMenuInfo");
            assert pfaMenuInfo != null;
            if (pfaMenuInfo.getSlug() != null && pfaMenuInfo.getSlug().equalsIgnoreCase("De-Seize")) {
                isDeseize = true;
                deseizeAllBtn.setVisibility(View.VISIBLE);
                deseizeAllBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        baseActivity.sharedPrefUtils.showTwoBtnsMsgDialog(getString(R.string.deseize_all_msg), new SendMessageCallback() {
                            @Override
                            public void sendMsg(String message) {
                                if ((!message.isEmpty()) && message.equalsIgnoreCase(AppConst.CANCEL))
                                    return;

                                baseActivity.httpService.getListsData(getArguments().getString("Deseize_ALL_API_URL"), new HashMap<String, String>(), new HttpResponseCallback() {
                                    @Override
                                    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                        if (response != null && response.optBoolean("status")) {

                                            Log.d("getListData" , "menuListFragment = 5" );
                                            baseActivity.sharedPrefUtils.showMsgDialog(response.optString("message_code"), new SendMessageCallback() {
                                                @Override
                                                public void sendMsg(String message) {
                                                    doAPICall();

                                                }
                                            });

                                        } else {
                                            onRefreshListener.onRefresh();
                                        }
                                    }
                                }, true);


                            }
                        });
                    }
                });
            }
        }

        if (isDeseize) {
            if (tableData == null || tableData.size() == 0) {
                deseizeAllBtn.setVisibility(View.GONE);
            } else {
                deseizeAllBtn.setVisibility(View.VISIBLE);
            }
            pfaTableAdapter = new PFATableAdapter(baseActivity, tableData, this, true);
        } else {
            pfaTableAdapter = new PFATableAdapter(baseActivity, tableData, false, this);
        }
        menuTableLV.setAdapter(pfaTableAdapter);


        if (lastScrolledPosition > 0 && lastScrolledPosition < tableData.size()) {
            menuTableLV.setSelection(lastScrolledPosition);
        }

        setNoDataFound();

        menuTableLV.setOnGetMoreListener(new PullAndLoadListView.OnGetMoreListener() {
            @Override
            public void onGetMore(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                clickableWrapperView.setVisibility(View.VISIBLE);

                if (firstVisibleItem == 0) {
                    menuTableLV.setSelection(lastScrolledPosition);
                }

                if (firstVisibleItem != 0)
                    lastScrolledPosition = firstVisibleItem;// + visibleItemCount + 10;
                if (fetchDataInProgress) {
                    endRefresh();
                    return;
                }

                if (nextUrl == null || nextUrl.isEmpty()) {
                    endRefresh();
                    return;
                }
                updateSearch();
            }
        });

        pfaTableAdapter.notifyDataSetChanged();


    }

    private void setNoDataFound() {
        if (tableData == null || tableData.size() == 0) {
            menuTableLV.setVisibility(View.GONE);
            sorry_iv.setVisibility(View.VISIBLE);
            if ((baseActivity.findViewById(R.id.sorry_iv)) != null)
                (baseActivity.findViewById(R.id.sorry_iv)).setVisibility(View.VISIBLE);

        } else {
            menuTableLV.setVisibility(View.VISIBLE);
            sorry_iv.setVisibility(View.GONE);

        }
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        Log.d("getListData" , "menuListFragment = 10" );
        fetchDataInProgress = false;
        endRefresh();
        sorry_iv.setVisibility(View.GONE);
        if (response != null)
            if (response.optBoolean("status")) {
                try {
                    JSONObject tableJsonObject = response.getJSONObject("table");
                    Log.d("AfterSearch" , "menuListFragment after search= 1 " );
                    populateTableData(tableJsonObject, requestUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                nextUrl = "";
                setNoDataFound();
            }
    }

    private void endRefresh() {
        clickableWrapperView.setVisibility(View.GONE);
        menuTableLV.refreshComplete();
        menuTableLV.getMoreComplete();
    }

    void doAPICall() {

        if (tableData != null)
            tableData.clear();
        if (urlToCall != null) {

            if (!showProgress) {
                lastScrolledPosition = 0;
            }
            setResetData(false);
            sorry_iv.setVisibility(View.GONE);

            baseActivity.httpService.getListsData(urlToCall, new HashMap<String, String>(), MenuListFragment.this, showProgress);
            Log.d("getListData" , "menuListFragment = 6" );
        }
    }

    @Override
    public void whichItemClicked(final String id) {
//         in case of isDeseize=true
        if (id == null)
            return;
        if (isDeseize) {
            baseActivity.sharedPrefUtils.showTwoBtnsMsgDialog(getString(R.string.delete_deseize_msg), new SendMessageCallback() {
                @Override
                public void sendMsg(String message) {
                    if ((!message.isEmpty()) && message.equalsIgnoreCase(AppConst.CANCEL))
                        return;

                    baseActivity.httpService.getListsData(tableData.get(Integer.parseInt(id)).get(0).getAPI_URL(), new HashMap<String, String>(), new HttpResponseCallback() {
                        @Override
                        public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                            if (response != null && response.optBoolean("status")) {

                                Log.d("getListData" , "menuListFragment = 7" );
                                baseActivity.sharedPrefUtils.showMsgDialog(response.optString("message_code"), new SendMessageCallback() {
                                    @Override
                                    public void sendMsg(String message) {
                                        doAPICall();

                                    }
                                });

                            } else {
                                onRefreshListener.onRefresh();
                            }
                        }
                    }, true);
                }
            });
        }
    }


    @Override
    public void downloadInspection(String downloadUrl, final int position) {
        new LocalFormHttpUtils().downloadAndSaveInspection(baseActivity, downloadUrl, new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {

                if (tableData != null && position < tableData.size()) {
                    tableData.remove(position);
                    if (pfaTableAdapter != null)
                        pfaTableAdapter.updateAdapter(tableData);
                }
//commented doAPICall because no refresh of data is required after saving the inspection as draft from list
//                doAPICall(true);
            }
        });
    }

    @Override
    public void deleteRecordAPICall(String deleteUrl, int position) {

        baseActivity.httpService.getListsData(deleteUrl, new HashMap<String, String>(), new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                if (response != null && response.optBoolean("status")) {

                    Log.d("getListData" , "menuListFragment = 8" );
                    baseActivity.sharedPrefUtils.showMsgDialog(response.optString("message_code"), new SendMessageCallback() {
                        @Override
                        public void sendMsg(String message) {
                            doAPICall();

                        }
                    });

                }
            }
        }, showProgress);
    }

    boolean isResetData() {
        return resetData;
    }

    private void setResetData(boolean resetData) {
        this.resetData = resetData;
    }

    private void populateTableData(JSONObject tableJsonObject, String requestUrl) {
        try {
            Type type = new TypeToken<List<List<PFATableInfo>>>() {
            }.getType();

            String itemCount = tableJsonObject.optString("itemCount");
            if (sendMessageCallback != null) {
                sendMessageCallback.sendMsg(itemCount);
            }
            JSONArray formJSONArray = tableJsonObject.getJSONArray("tableData");

            if ((tableData == null || tableData.size() == 0)) {
                tableData = new GsonBuilder().create().fromJson(formJSONArray.toString(), type);
            } else {

                if (requestUrl != null) {
                    if (requestUrl.contains("?search=") && (!requestUrl.contains("page="))) {
                        tableData.clear();
                    }
                    if (requestUrl.contains("latestNews")) {
                        tableData.clear();
                    }
                }

                List<List<PFATableInfo>> testTableData = new GsonBuilder().create().fromJson(formJSONArray.toString(), type);
                tableData.addAll(testTableData);
            }

            setAdapterData();

            nextUrl = tableJsonObject.optString("next_page");

             if (tableJsonObject.has("max_draft_limit")){
              SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(baseActivity);
              sharedPrefUtils.saveSharedPrefValue("Draft_MAx_Limit",tableJsonObject.optString("max_draft_limit"));
             }

            if (tableJsonObject.has("add_new")) {
                add_newUrl = tableJsonObject.getString("add_new");

                if (!add_newUrl.isEmpty()) {
                    addNewBtn.setVisibility(View.VISIBLE);
                }
            }

            if (tableJsonObject.has("local_add_newUrl")) {
                local_add_newUrl = tableJsonObject.getString("local_add_newUrl");

                if (!local_add_newUrl.isEmpty()) {
                    addNewBtn.setVisibility(View.VISIBLE);
                }
            }

            Log.d("AfterSearch" , "menuListFragment after search= 2 " );
            if (tableJsonObject.has("title")) {
//                if (!isDrawer) {
                Log.d("AfterSearch" , "menuListFragment after search= 3 " );
                    baseActivity.setTitle(baseActivity.sharedPrefUtils.isEnglishLang() ? tableJsonObject.getString("title") : tableJsonObject.optString("titleUrdu"), true);
//                }
                // Add a header to the ListView
                if (addHeader && isDrawer) {
                    addHeader = false;
                }
            }

            if (tableJsonObject.has("search_filters_count")){
                String search_filters_count = tableJsonObject.getString("search_filters_count");
                baseActivity.searchFilterFL.setVisibility(View.VISIBLE);
                baseActivity.filterCountTV.setVisibility(View.VISIBLE);
                baseActivity.filterCountTV.setText(search_filters_count);
            }

            if (tableJsonObject.has("search_filters")) {
                JSONObject search_filtersObject = tableJsonObject.getJSONObject("search_filters");

                if (search_filtersObject.has("form")) {
                    Type formSectionInfosType = new TypeToken<List<FormSectionInfo>>() {
                    }.getType();


                    JSONArray formSectionJArray = search_filtersObject.getJSONArray("form");
                    formSectionInfos = new GsonBuilder().create().fromJson(formSectionJArray.toString(), formSectionInfosType);

//                    if (!itemCount.isEmpty()){
//                        baseActivity.searchFilterFL.setVisibility(View.VISIBLE);
//                        baseActivity.filterCountTV.setText("1");
//                        baseActivity.filterCountTV.setVisibility(View.VISIBLE);
//                    }

                    if (formSectionInfos != null && formSectionInfos.size() > 0) {
                        showFilter = true;
                        if (listDataFetchedInterface != null)
                            listDataFetchedInterface.listDataFetched();
                    }
                }

            } else if (tableJsonObject.has("news_search_filters")) {
                searchLL.setVisibility(View.VISIBLE);
                JSONObject search_filtersObject = tableJsonObject.getJSONObject("news_search_filters");
                newsSearchLL.setVisibility(View.VISIBLE);
                if (search_filtersObject.has("form")) {
                    Type formSectionInfosType = new TypeToken<List<FormSectionInfo>>() {
                    }.getType();

                    JSONArray formSectionJArray = search_filtersObject.getJSONArray("form");
                    formSectionInfos = new GsonBuilder().create().fromJson(formSectionJArray.toString(), formSectionInfosType);

                    if (formSectionInfos != null && formSectionInfos.size() > 0) {
                        createSearchFilterView();
                    }
                }

            } else if (tableJsonObject.has("general_search")) {
                searchLL.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            baseActivity.sharedPrefUtils.printStackTrace(e);
        }
    }

    void setSendMessageCallback(SendMessageCallback sendMessageCallback) {
        this.sendMessageCallback = sendMessageCallback;
    }
}

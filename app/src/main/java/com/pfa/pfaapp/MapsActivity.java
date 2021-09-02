package com.pfa.pfaapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.adapters.PFATableAdapter;
import com.pfa.pfaapp.fragments.MenuListFragment;
import com.pfa.pfaapp.fragments.MenuMapFragment;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.MapMoveCallback;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
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

import static com.pfa.pfaapp.utils.AppConst.APP_LATITUDE;
import static com.pfa.pfaapp.utils.AppConst.APP_LONGITUDE;
import static com.pfa.pfaapp.utils.AppConst.BUSINESS_LOCATION_FIELD;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_ACTIVITY_TITLE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_LATLNG_STR;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_PFA_MENU_ITEM;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;

public class MapsActivity extends BaseActivity implements HttpResponseCallback, WhichItemClicked {

    String urlToCall;
    PFATableAdapter pfaTableAdapter;
    List<List<PFATableInfo>> tableData;

    ListView mapListLV;
    TextView headingTV;
    Bundle bundle;
    Button setBizLocaBtn;

    LatLng locationToSend;

    TextView tapAnywhereMsgTV;

    RelativeLayout mapListRL;

    PFAMenuInfo pfaMenuInfo;
    private boolean isMap = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        startLocation();
        mapListLV = findViewById(R.id.mapListLV);
        headingTV = findViewById(R.id.headingTV);
        headingTV.setVisibility(View.GONE);
        setBizLocaBtn = findViewById(R.id.setBizLocaBtn);
        tapAnywhereMsgTV = findViewById(R.id.tapAnywhereMsgTV);
        mapListRL = findViewById(R.id.mapListRL);

        sharedPrefUtils.applyStyle(String.valueOf(AppUtils.FONT_STYLE.medium), String.valueOf(AppUtils.FONT_SIZE.l), getResources().getString(R.string.text_light_grey), headingTV);
        sharedPrefUtils.applyFont(setBizLocaBtn, AppUtils.FONTS.HelveticaNeueMedium);

        sharedPrefUtils.applyFont(tapAnywhereMsgTV, AppUtils.FONTS.HelveticaNeue);

        bundle = getIntent().getExtras();
        assert bundle != null;
        String title = bundle.getString(EXTRA_ACTIVITY_TITLE);

        if (title != null && (!title.isEmpty())) {
            findViewById(R.id.ttl_bar).setVisibility(View.VISIBLE);
            setTitle(title, true);
        } else {
            findViewById(R.id.mapBackBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.ttl_bar).setVisibility(View.GONE);
        }

        ArrayList<String> latLng = new ArrayList<>();
        if (getIntent().getExtras().containsKey(EXTRA_URL_TO_CALL)) {

            urlToCall = bundle.getString(EXTRA_URL_TO_CALL);

            if (bundle.containsKey(BUSINESS_LOCATION_FIELD)) {
                sharedPrefUtils.printLog("BUSINESS_LOCATION_FIELD", "" + bundle.getString(BUSINESS_LOCATION_FIELD));

                latLng.add(bundle.getString(BUSINESS_LOCATION_FIELD));

                mapListLV.setVisibility(View.GONE);
                setBizLocaBtn.setVisibility(View.VISIBLE);
                tapAnywhereMsgTV.setVisibility(View.VISIBLE);
            } else {
                doAPICall();
            }

        } else {

            if (bundle.containsKey(EXTRA_PFA_MENU_ITEM)) {

                isMap = false;
                pfaMenuInfo = (PFAMenuInfo) bundle.getSerializable(EXTRA_PFA_MENU_ITEM);

                addMenuListFragment();
            }

            if (bundle.containsKey(EXTRA_LATLNG_STR)) {
                sharedPrefUtils.printLog("EXTRA_LATLNG_STR", bundle.getString(EXTRA_LATLNG_STR));

                mapListLV.setVisibility(View.GONE);
                latLng.add(bundle.getString(EXTRA_LATLNG_STR));
            }
        }

        addMapView(latLng);


    }

    MenuListFragment menuListFragment;

    private void addMenuListFragment() {
        mapListLV.setVisibility(View.GONE);
        menuListFragment = MenuListFragment.newInstance(pfaMenuInfo, false, false, true, null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mapFL, menuListFragment);
        transaction.addToBackStack(null).commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.d("imagePath" , "onActivityResult = " + "MapsActivity");

        if (menuListFragment != null) {
            menuListFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void doAPICall() {
        if (urlToCall != null) {

            HashMap<String, String> reqParams = new HashMap<>();

            if (sharedPrefUtils.getSharedPrefValue(AppConst.APP_LATITUDE, "") != null) {
                reqParams.put(APP_LATITUDE, sharedPrefUtils.getSharedPrefValue(APP_LATITUDE, ""));
                reqParams.put(APP_LONGITUDE, sharedPrefUtils.getSharedPrefValue(APP_LONGITUDE, ""));
                httpService.getListsData(urlToCall, reqParams, this, true);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doAPICall();
                    }
                }, 1000);
            }

        }
    }

    MenuMapFragment menuMapFragment;

    private void addMapView(ArrayList<String> latLng) {
        if (!isMap)
            return;
        menuMapFragment = MenuMapFragment.newInstance(null, latLng, new MapMoveCallback() {
            @Override
            public void onMoveMap(LatLng latLng) {
                if (latLng != null) {
                    locationToSend = latLng;

                    if (bundle.containsKey(BUSINESS_LOCATION_FIELD)) {
                        menuMapFragment.addClickedLocation(latLng);
                    }
                }
            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mapFL, menuMapFragment);
        transaction.addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        if (response != null)
            if (response.optBoolean("status")) {
           //     Toast.makeText(this, "This One", Toast.LENGTH_SHORT).show();
                try {
                    Type type = new TypeToken<List<List<PFATableInfo>>>() {
                    }.getType();

                    JSONObject tableJsonObject = response.getJSONObject("table");

                    JSONArray formJSONArray = tableJsonObject.getJSONArray("tableData");
                    tableData = new GsonBuilder().create().fromJson(formJSONArray.toString(), type);

                    setAdapterData();

                    if (tableJsonObject.has("title")) {
                        headingTV.setText(tableJsonObject.getString("title"));
                    }

                } catch (JSONException e) {
                    sharedPrefUtils.printStackTrace(e);
                }
            } else {

                sharedPrefUtils.printLog("No Maps data", "No maps data");
            }
    }

    private void setAdapterData() {
        if (tableData == null)
            tableData = new ArrayList<>();
        pfaTableAdapter = new PFATableAdapter(this, tableData, false, this);
        mapListLV.setAdapter(pfaTableAdapter);
        pfaTableAdapter.notifyDataSetChanged();

        if (isMap)
            setMapData();
    }

    private void setMapData() {

        final ArrayList<String> mapData = new ArrayList<>();

        for (List<PFATableInfo> pfaTableInfos : tableData) {
            if (pfaTableInfos != null && pfaTableInfos.size() > 0) {
                for (int i = 0; i < pfaTableInfos.size(); i++) {
                    if (pfaTableInfos.get(i).getField_name().equalsIgnoreCase("lat_lng")) {
                        mapData.add(pfaTableInfos.get(i).getData());
                    }
                }
            }
        }

        getCurrentLoction(mapData);

    }

    /**
     * keep on waiting for location, and then animate map to current location
     *
     * @param mapData ArrayList<String> of location names [name,lat,lng]
     */
    private void getCurrentLoction(final ArrayList<String> mapData) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPrefUtils.getSharedPrefValue(APP_LATITUDE, "") != null) {
                    ((MenuMapFragment) getSupportFragmentManager().getFragments().get(0)).setMapPins(mapData);
                } else {
                    getCurrentLoction(mapData);
                }
            }
        }, 1000);
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

    public void onClickSetBizLocationBtn(View view) {

        if (locationToSend != null) {
            HashMap<String, String> reqParams = new HashMap<>();
            reqParams.put("lat", "" + locationToSend.latitude);
            reqParams.put("lng", "" + locationToSend.longitude);
            httpService.showProgressDialog(false);

            httpService.formSubmit(reqParams, null, urlToCall, new HttpResponseCallback() {
                @Override
                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
//                    {"status":true,"message_code":"Business Location Updated Successfully!"}

                    if (response != null)
                        if (response.optBoolean("status")) {
                            AppConst.BIZ_LOC_UPDATED = true;
                            MenuMapFragment.businessLatLng = locationToSend;

                            finish();

                        } else {
                            sharedPrefUtils.showMsgDialog(response.optString("Server error occurred!"), null);
                        }

                }
            }, true, "post");
        } else {
            sharedPrefUtils.showMsgDialog("Please locate and tap on your business location!", null);
        }
    }

    public void onClickMapBackBtn(View view) {
        onBackPressed();
    }
}

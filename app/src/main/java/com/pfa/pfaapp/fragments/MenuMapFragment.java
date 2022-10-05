package com.pfa.pfaapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.collections.MarkerManager;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.PFAAddNewActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.ListDataFetchedInterface;
import com.pfa.pfaapp.interfaces.MapMoveCallback;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormSectionInfo;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.utils.AppConst;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.pfa.pfaapp.utils.AppConst.APP_LATITUDE;
import static com.pfa.pfaapp.utils.AppConst.APP_LONGITUDE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_BIZ_FORM_DATA;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_FILTERS_DATA;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_ITEM_COUNT;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.RC_ACTIVITY;
import static com.pfa.pfaapp.utils.AppConst.RC_DROPDOWN;
import static com.pfa.pfaapp.utils.AppConst.RC_REFRESH_LIST;
import static com.pfa.pfaapp.utils.AppConst.SEARCH_DATA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuMapFragment extends Fragment implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener , GoogleMap.OnInfoWindowClickListener , HttpResponseCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_LAT_LNG = "ARG_LAT_LNG";
    private ArrayList<String> latLngArray;

    private GoogleMap theGoogleMap;
    private ImageButton viewMapPathBtn;

    private BaseActivity baseActivity;
    private LatLng currentLatLng;
    public static LatLng businessLatLng;
    private static MapMoveCallback mapMoveCallback;

    private LatLng currentLocLatLng;
    private Handler mapReadyHandler;
    private Runnable mapReadyRunnable;

    private LatLngBounds.Builder builder;
    private final float DEFAULT_ZOOM = 15.5f;
    private String[] latLngStr;
    private Runnable locationRunnable;
    private TextView txtMapDetail;
    private int counter = 0;
    String urlToCall;
    private LinearLayout layoutMapButton;
    private ClusterManager<MyItem> clusterManager;


//    DistanceCalculator distanceCalculator;

    public MenuMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pfaMenuInfo Parameter 1.
     * @return A new instance of fragment MenuListFragment.
     */
    public static MenuMapFragment newInstance(PFAMenuInfo pfaMenuInfo, ArrayList<String> latLng, MapMoveCallback mapMoveCallback1) {
        businessLatLng = null;
        mapMoveCallback = mapMoveCallback1;
        MenuMapFragment fragment = new MenuMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, pfaMenuInfo);

        args.putStringArrayList(ARG_LAT_LNG, latLng);

        if (pfaMenuInfo != null && pfaMenuInfo.getAPI_URL() != null) {
            args.putString(EXTRA_URL_TO_CALL, pfaMenuInfo.getAPI_URL());
            args.putString("MENU_NAME", "" + pfaMenuInfo.getMenuItemName());
            args.putSerializable("PFAMenuInfo", pfaMenuInfo);
        }

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_menu_map, container, false);
        viewMapPathBtn = rootView.findViewById(R.id.viewMapPathBtn);
        txtMapDetail = rootView.findViewById(R.id.txtMapDetail);
        layoutMapButton = rootView.findViewById(R.id.layoutMapButton);
        viewMapPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGoogleMap();
            }
        });

        Log.d("onCreateActv", "MenuMapFragment");


        return rootView;
    }

    private void viewGoogleMap() {
        if (businessLatLng != null && currentLatLng != null) {
            // Create a Uri from an intent string. Use the result to create an Intent.
//            Uri gmmIntentUri = Uri.parse("geo:" + businessLatLng.latitude + "," + businessLatLng.longitude+"?z=18.0");

            Log.d("MapFragment", "viewGoogleMap");
            String uri = String.format(Locale.getDefault(), "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", currentLatLng.latitude, currentLatLng.longitude, businessLatLng.latitude, businessLatLng.longitude);

//            if (!uri.isEmpty()) {
            try {
                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
                Log.d("MapFragment", "viewGoogleMap1");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("MapFragment", "viewGoogleMap2 = " + e.toString());
            }
//            }


        }
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        baseActivity = (BaseActivity) getActivity();
//        distanceCalculator = new DistanceCalculator();

        latLngArray = new ArrayList<>();
        Bundle args = getArguments();
        assert args != null;
        if (args.containsKey(ARG_LAT_LNG)) {
            Log.d("latLngFromMap" , "here enforcement map lat lng = " + args.getStringArrayList(ARG_LAT_LNG));
            latLngArray = args.getStringArrayList(ARG_LAT_LNG);

            if (latLngArray != null && latLngArray.size() == 1) {
                viewMapPathBtn.setVisibility(View.VISIBLE);
            }
        }
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);


        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        baseActivity.startLocation();
    }


    @Override
    public void onResume() {
        super.onResume();
//        Log.e("MenuMapFragment", "MenuMapFragment onResume called " + businessLatLng);

        if (businessLatLng != null) {
            setBusinessPin();
        }


    }

    private void doAPiCall() {
        assert getArguments() != null;
        urlToCall = getArguments().getString(EXTRA_URL_TO_CALL);
        if (urlToCall != null) {
            baseActivity.httpService.getListsData(urlToCall, new HashMap<String, String>(), MenuMapFragment.this, true);
            Log.d("getListData", "MenuMapFragment = 6b");
        }
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        theGoogleMap = googleMap;

        mapReadyHandler = new Handler();
        mapReadyRunnable = new Runnable() {
            @Override
            public void run() {
                if (baseActivity.sharedPrefUtils.getSharedPrefValue(AppConst.APP_LATITUDE, "") != null) {
                    if (currentLocLatLng == null) {
                        String latStr = baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LATITUDE, "");
                        String lngStr = baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LONGITUDE, "");
                        currentLocLatLng = new LatLng(Double.parseDouble(latStr), Double.parseDouble(lngStr));
                    }

                    if (latLngArray != null && (latLngArray.size() != 1)) {
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLocLatLng,
                                14.5f);
                        googleMap.moveCamera(update);
                    }
                    mapReadyHandler.removeCallbacks(mapReadyRunnable);
                } else {
                    mapReadyHandler.postDelayed(mapReadyRunnable, 1000);
                }
            }
        };
        mapReadyHandler.postDelayed(mapReadyRunnable, 200);


        Log.d("latLngFromMap" , "onMapReady");
        setMapPins(null);

        doAPiCall();

        theGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mapMoveCallback != null)
                    mapMoveCallback.onMoveMap(latLng);
            }
        });

    }

    public void setMapPins(ArrayList<String> mapPinsArray) {
        Log.d("latLngFromMap" , "here enforcement map 1");
        if (mapPinsArray != null)
            latLngArray = mapPinsArray;

        if (latLngArray == null) {
            latLngArray = new ArrayList<>();
        }

        if (theGoogleMap != null) {
            theGoogleMap.clear();
        }

        Log.d("latLngFromMap" , "here enforcement map 11 lat array size = " + latLngArray.size());

        builder = new LatLngBounds.Builder();

        if (latLngArray.size() > 0) {
            Log.d("latLngFromMap" , "here enforcement map 2");
            for (String latLngExtra : latLngArray) {
                if (latLngExtra != null) {
                    Log.d("latLngFromMap" , "here enforcement map 3");
                    latLngStr = latLngExtra.split(",");

                    if (latLngStr.length == 3) {
                        Log.d("latLngFromMap" , "here enforcement map 4");

                        if (latLngArray != null && latLngArray.size() == 1) {
                            if (businessLatLng == null)
                                businessLatLng = new LatLng(Double.parseDouble(latLngStr[1]), Double.parseDouble(latLngStr[2]));
                            Log.d("latLngFromMap" , "here enforcement map 5");
                            setBusinessPin();
                        } else {
                            LatLng businessLatLng = new LatLng(Double.parseDouble(latLngStr[1]), Double.parseDouble(latLngStr[2]));
                            theGoogleMap.addMarker(new MarkerOptions().position(businessLatLng).title(latLngStr[0]));
                        }
                    }
                }
            }
        }

        setUpMap();
        if (baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LATITUDE, "") != null) {
            currentLatLng = new LatLng(Double.parseDouble(baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LATITUDE, "")), Double.parseDouble(baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LONGITUDE, "")));
            if (latLngArray.size() == 0 || (latLngArray.size() == 1 && (businessLatLng == null || businessLatLng.latitude == 0 || businessLatLng.longitude == 0))) {
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM);
                theGoogleMap.moveCamera(update);
            }
        }
    }

/*

    public void setMapPins(ArrayList<String> mapPinsArray, boolean isHttp) {
        Log.d("latLngFromMap" , "here enforcement map 12");
        if (mapPinsArray != null)
            latLngArray = mapPinsArray;

        if (latLngArray == null) {
            latLngArray = new ArrayList<>();
        }

        if (theGoogleMap != null) {
            theGoogleMap.clear();
        }

        builder = new LatLngBounds.Builder();

        if (latLngArray.size() > 0) {
            Log.d("latLngFromMap" , "here enforcement map 5");
            for (String latLngExtra : latLngArray) {
                if (latLngExtra != null) {
                    latLngStr = latLngExtra.split(",");
                    Log.d("latLngFromMap" , "here enforcement map 4");

                    if (latLngStr.length == 3) {

                        if (isHttp) {
                            Log.d("latLngFromMap" , "here enforcement map1");
                            if (latLngArray != null && latLngArray.size() == 1) {
                                if (businessLatLng == null)
                                    businessLatLng = new LatLng(Double.parseDouble(latLngStr[1]), Double.parseDouble(latLngStr[2]));
                                setBusinessPin();
                            } else {
                                LatLng businessLatLng = new LatLng(Double.parseDouble(latLngStr[1]), Double.parseDouble(latLngStr[2]));
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(businessLatLng);
                                markerOptions.title(latLngStr[0]);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));


                                theGoogleMap.addMarker(new MarkerOptions().position(businessLatLng).title(latLngStr[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

//                                theGoogleMap.addMarker(new MarkerOptions().position(businessLatLng).title(latLngStr[0]).snippet(""));
//                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(businessLatLng, 10);
//                                theGoogleMap.animateCamera(cameraUpdate);
                            }
                        } else {
                            if (latLngArray != null && latLngArray.size() == 1) {
                                if (businessLatLng == null)
                                    businessLatLng = new LatLng(Double.parseDouble(latLngStr[1]), Double.parseDouble(latLngStr[2]));
                                Log.d("latLngFromMap" , "here enforcement map 2");
                                setBusinessPin();
                            } else {
                                LatLng businessLatLng = new LatLng(Double.parseDouble(latLngStr[1]), Double.parseDouble(latLngStr[2]));
                                theGoogleMap.addMarker(new MarkerOptions().position(businessLatLng).title(latLngStr[0]));


                                Log.d("latLngFromMap" , "here enforcement map 3");
//                                theGoogleMap.addMarker(new MarkerOptions().position(businessLatLng).title(latLngStr[0]).snippet(""));
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(businessLatLng, 10);
                                theGoogleMap.animateCamera(cameraUpdate);
                            }
                        }
                    }
                }
            }
        }

        setUpMap();
        if (baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LATITUDE, "") != null) {
            currentLatLng = new LatLng(Double.parseDouble(baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LATITUDE, "")), Double.parseDouble(baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LONGITUDE, "")));
            if (latLngArray.size() == 0 || (latLngArray.size() == 1 && (businessLatLng == null || businessLatLng.latitude == 0 || businessLatLng.longitude == 0))) {
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM);
                theGoogleMap.moveCamera(update);
            }
        }
    }
*/

    public void setMultiMapPins(ArrayList<String> markerTitle, ArrayList<String> markerLat, ArrayList<String> markerLng, ArrayList<String> markerColor, ArrayList<String> markerContent, ArrayList<String> markerAPIUrl, boolean isHttp) {

        if (theGoogleMap != null) {
            theGoogleMap.clear();
        }

        builder = new LatLngBounds.Builder();
        myMarkerAPIUrl = markerAPIUrl;

        for (int i = 0; i < markerTitle.size(); i++) {

            LatLng businessLatLng = new LatLng(Double.parseDouble(markerLat.get(i)), Double.parseDouble(markerLng.get(i)));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(businessLatLng);
            markerOptions.title(markerTitle.get(i));
            if (markerColor.get(i).equals("0"))
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            else if (markerColor.get(i).equals("1"))
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            markerOptions.snippet(markerContent.get(i));



            theGoogleMap.setOnMarkerClickListener(this);
            theGoogleMap.setOnInfoWindowClickListener(this);
            myMarker = theGoogleMap.addMarker(markerOptions);
//            theGoogleMap.addMarker(new MarkerOptions().position(businessLatLng).title(latLngStr[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            if (i == 0) {
//                theGoogleMap.addMarker(new MarkerOptions().position(businessLatLng).title(latLngStr[0]).snippet(""));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(businessLatLng, 15f);
                theGoogleMap.animateCamera(cameraUpdate);
            }


        }


//        setUpClusterer();

        setUpMap();
        /*if (baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LATITUDE, "") != null) {
            currentLatLng = new LatLng(Double.parseDouble(baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LATITUDE, "")), Double.parseDouble(baseActivity.sharedPrefUtils.getSharedPrefValue(APP_LONGITUDE, "")));
            if (latLngArray.size() == 0 || (latLngArray.size() == 1 && (businessLatLng == null || businessLatLng.latitude == 0 || businessLatLng.longitude == 0))) {
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM);
                theGoogleMap.moveCamera(update);
            }
        }*/
    }

    private Marker myMarker;
    private ArrayList<String> myMarkerAPIUrl = new ArrayList<>();
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        /*Log.d("markerClick" , "marker id = " + marker.getId());
        Log.d("markerClick" , "marker position = " + marker.getPosition());
        Log.d("markerClick" , "marker title = " + marker.getTitle());
        String index = marker.getId();
        String APIUrl = myMarkerAPIUrl.get(Integer.parseInt(index.substring(1)));
        Bundle bundle = new Bundle();
        baseActivity.httpService.getListsData(APIUrl, new HashMap<String, String>(), new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                if (response != null)
                    bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());

                Log.d("viewCreated" , "pfa table adapter new activity 3");
                baseActivity.sharedPrefUtils.startActivityForResult(baseActivity, PFAAddNewActivity.class, bundle, RC_REFRESH_LIST);
            }
        }, true);
        Log.d("markerClick" , "marker original title = " + myMarkerAPIUrl.get(Integer.parseInt(index.substring(1))));
        Log.d("markerClick" , "marker tag = " + marker.getTag());

        marker.showInfoWindow();*/

//        if (marker  == myMarker){
//            Log.d("markerClick" , "title = " + myMarkerTitle);
//            Toast.makeText(getActivity(), "title = " + myMarkerTitle, Toast.LENGTH_LONG).show();
//        }
        return false;
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Log.d("markerClick" , "marker id = " + marker.getId());
        Log.d("markerClick" , "marker position = " + marker.getPosition());
        Log.d("markerClick" , "marker title = " + marker.getTitle());
        String index = marker.getId();
        String APIUrl = myMarkerAPIUrl.get(Integer.parseInt(index.substring(1)));
        Bundle bundle = new Bundle();
        baseActivity.httpService.getListsData(APIUrl, new HashMap<String, String>(), new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                if (response != null)
                    bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());

                Log.d("viewCreated" , "pfa table adapter new activity 3");
                baseActivity.sharedPrefUtils.startActivityForResult(baseActivity, PFAAddNewActivity.class, bundle, RC_REFRESH_LIST);
            }
        }, true);
        Log.d("markerClick" , "marker original title = " + myMarkerAPIUrl.get(Integer.parseInt(index.substring(1))));
        Log.d("markerClick" , "marker tag = " + marker.getTag());
    }

    private void setBusinessPin() {
        Log.d("latLngFromMap" , "here enforcement map business pin");
        if (theGoogleMap == null)
            return;

        if (latLngArray != null && latLngArray.size() == 1) {
            theGoogleMap.clear();

            if (latLngStr != null && latLngStr.length > 0)
                theGoogleMap.addMarker(new MarkerOptions().position(businessLatLng).title(latLngStr[0]));

            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(businessLatLng, DEFAULT_ZOOM);
            theGoogleMap.moveCamera(update);
            builder.include(businessLatLng);
        }
    }


    private void setUpMap() {
        final Handler locHandler = new Handler();
        locationRunnable = new Runnable() {
            @Override
            public void run() {
                if (theGoogleMap == null)
                    return;
                if (baseActivity == null)
                    return;
                theGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                if (ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    if (ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                theGoogleMap.setMyLocationEnabled(true);
                theGoogleMap.setTrafficEnabled(true);
                theGoogleMap.setIndoorEnabled(true);
                theGoogleMap.setBuildingsEnabled(true);
                theGoogleMap.getUiSettings().setZoomControlsEnabled(false);

               /* theGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        Log.d("markerClick" , "markler clickkkkkkclickkkkkkclickkkkkk");
                        return true;
                    }
                });*/

                if (currentLocLatLng != null) {
                    locHandler.removeCallbacks(locationRunnable);
                } else {
                    locHandler.postDelayed(locationRunnable, 1000);
                }
            }
        };
        locHandler.postDelayed(locationRunnable, 1000);

    }

    public static MenuMapFragment newInstance(PFAMenuInfo pfaMenuInfo, ArrayList<String> latLng) {
        return newInstance(pfaMenuInfo, latLng, null);
    }/*

    public static MenuMapFragment newInstance(PFAMenuInfo pfaMenuInfo, ArrayList<String> latLng) {
        MenuMapFragment fragment = new MenuMapFragment();
        Bundle args = new Bundle();
        if (pfaMenuInfo != null && pfaMenuInfo.getAPI_URL() != null) {
            args.putString(EXTRA_URL_TO_CALL, pfaMenuInfo.getAPI_URL());
            args.putString("MENU_NAME", "" + pfaMenuInfo.getMenuItemName());
            args.putSerializable("PFAMenuInfo", pfaMenuInfo);
        }

        fragment.setArguments(args);
        return fragment;
    }*/

    public void addClickedLocation(LatLng latLng) {
        if (theGoogleMap != null) {
            theGoogleMap.clear();
            if (latLngArray != null)
                latLngArray.clear();
            setUpMap();
            theGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
        }
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        Log.d("latLngFromMap" , "onCompleteHttpResponse");

        if (response.optBoolean("status")) {
            try {
                JSONObject tableJsonObject = response.getJSONObject("table");
                JSONArray jsonArray = tableJsonObject.getJSONArray("tableData");
                populateMap(jsonArray);


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

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("AfterSearch12", "menuMapFragment after search = " + e);
            }
        }
    }

    private void populateMap(JSONArray jsonArray) {
        JSONObject tableJsonObject1;
        ArrayList<String> markerTitle = new ArrayList();
        ArrayList<String> markerLat = new ArrayList();
        ArrayList<String> markerLng = new ArrayList();
        ArrayList<String> markerColor = new ArrayList();
        ArrayList<String> markerContent = new ArrayList();
        ArrayList<String> markerAPIUrl = new ArrayList();

        try {

            tableJsonObject1 = jsonArray.getJSONArray(0).getJSONObject(0);
            JSONArray mapButtonArray = tableJsonObject1.getJSONArray("map_buttons");

            Log.d("AfterSearch12", "menuMapFragment after search= 1 ");
            for (int i = 0; i < tableJsonObject1.getJSONArray("data").length(); i++) {
                JSONObject jsonObject = tableJsonObject1.getJSONArray("data").getJSONObject(i);
                markerTitle.add(jsonObject.getString("title"));
                markerLat.add(jsonObject.getString("latitude"));
                markerLng.add(jsonObject.getString("longitude"));
                markerColor.add(jsonObject.getString("color"));
                markerContent.add(jsonObject.getString("content"));
                markerAPIUrl.add(jsonObject.getString("API_URL"));
            }

            layoutMapButton.removeAllViews();

            for (int i = 0 ; i < mapButtonArray.length() ; i++){
                TextView textView = new TextView(getActivity());
                android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 30,0, 0);
                textView.setLayoutParams(params);
                textView.setPadding(14,14,14,14);

                JSONObject jsonObject = mapButtonArray.getJSONObject(i);
                textView.setBackgroundResource(R.drawable.textview_border_transparent);
                GradientDrawable drawable = (GradientDrawable) textView.getBackground();
                drawable.setColor(Color.parseColor(jsonObject.getString("background_color")));
                textView.setText(jsonObject.getString("label"));
                textView.setTextColor(Color.parseColor(jsonObject.getString("font_color")));
                layoutMapButton.addView(textView);
            }
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (tableJsonObject1.getBoolean("enable_clusters"))
                            setUpClusterer(markerTitle, markerLat, markerLng, markerColor, markerContent, markerAPIUrl, true);
                        else
                            setMultiMapPins(markerTitle, markerLat, markerLng, markerColor, markerContent, markerAPIUrl, true);
                    } catch (JSONException e) {
                        setMultiMapPins(markerTitle, markerLat, markerLng, markerColor, markerContent, markerAPIUrl, true);
                        e.printStackTrace();
                    }

                }
            });

            if (!tableJsonObject1.getString("map_heading").isEmpty()) {
                txtMapDetail.setVisibility(View.VISIBLE);
                String map_headingTxt = String.format(tableJsonObject1.getString("map_heading").replace("\\n", System.lineSeparator()));
                txtMapDetail.setText(map_headingTxt);
            } else
                txtMapDetail.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("mapSearchData" , "map fragment activity result");

        if (requestCode == RC_ACTIVITY && data != null) {
            Log.d("mapSearchData" , "map fragment activity result data not null");
            Bundle bundle = data.getExtras();

            assert bundle != null;
            formFilteredData = (HashMap<String, List<FormDataInfo>>) bundle.getSerializable(EXTRA_FILTERS_DATA);
            if (formFilteredData != null && formFilteredData.size() > 0) {
                Log.d("mapSearchData" , "map fragment activity result form filter > 0");
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
                urlToCall = null;
                baseActivity.removeFilter();
            }

            if (bundle.containsKey("activityTitle")){
                baseActivity.setTitle(bundle.getString("activityTitle" , "Businesses") , true);
            }

            if (bundle.containsKey(SEARCH_DATA)) {
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(bundle.getString(SEARCH_DATA));
                    populateMap(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /*if (bundle.containsKey(EXTRA_ITEM_COUNT) && sendMessageCallback != null) {
                    sendMessageCallback.sendMsg(bundle.getString(EXTRA_ITEM_COUNT));
                }*/

//                tableData = (List<List<PFATableInfo>>) bundle.getSerializable(SEARCH_DATA);

//                if (bundle.containsKey(AppConst.EXTRA_NEXT_URL)) {
//                    urlToCall = bundle.getString(AppConst.EXTRA_NEXT_URL);
//                    doAPiCall();
//                } else urlToCall = null;

//                setResetData(true);
//                setAdapterData();


            } /*else {
                if (urlToCall != null) {
                    urlToCall = null;
                    doAPiCall();
                }
            }*/
        }/* else if (requestCode == RC_REFRESH_LIST && AppConst.DO_REFRESH) {
            AppConst.DO_REFRESH = false;
            populateListMain();
        } else if (requestCode == RC_DROPDOWN) {
            if (data != null)
                customViewCreate.updateDropdownViewsData(data.getExtras(), newsSearchLL, sectionRequired);
        }*/

    }

    private void setUpClusterer(ArrayList<String> markerTitle, ArrayList<String> markerLat, ArrayList<String> markerLng, ArrayList<String> markerColor, ArrayList<String> markerContent, ArrayList<String> markerAPIUrl, boolean isHttp) {
        // Position the map.
        theGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(markerLat.get(0)), Double.parseDouble(markerLng.get(0))), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager(getActivity(), theGoogleMap);
        myMarkerAPIUrl = new ArrayList<>();
        myMarkerAPIUrl = markerAPIUrl;

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        clusterManager = new ClusterManager<MyItem>(getActivity(), theGoogleMap, new MarkerManager(theGoogleMap){
            @Override
            public boolean onMarkerClick(Marker marker) {
                //here will get the clicked marker
                /*Log.d("clusterMarker" , "marker id = " + marker.getId());
                Log.d("clusterMarker" , "marker position = " + marker.getPosition());
                Log.d("clusterMarker" , "marker title = " + marker.getTitle());
                String index = String.valueOf(marker.getPosition().latitude);
                int index1 = 0;
                for (int i = 0 ; i<markerLat.size() ; i++){
                    if (index.equals(markerLat.get(i))){
                        index1 = i;
                    }
                        
                }
                String APIUrl = myMarkerAPIUrl.get(index1);
                Bundle bundle = new Bundle();
                baseActivity.httpService.getListsData(APIUrl, new HashMap<String, String>(), new HttpResponseCallback() {
                    @Override
                    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                        if (response != null)
                            bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());

                        Log.d("viewCreated" , "pfa table adapter new activity 3");
                        baseActivity.sharedPrefUtils.startActivityForResult(baseActivity, PFAAddNewActivity.class, bundle, RC_REFRESH_LIST);
                    }
                }, true);
                Log.d("clusterMarker" , "marker original title = " + myMarkerAPIUrl.get(index1));
                Log.d("clusterMarker" , "marker tag = " + marker.getTag());*/
                return super.onMarkerClick(marker);
            }

            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d("clusterMarker" , "marker id = " + marker.getId());
                Log.d("clusterMarker" , "marker position = " + marker.getPosition());
                Log.d("clusterMarker" , "marker title = " + marker.getTitle());
                String markerLati = String.valueOf(marker.getPosition().latitude);
                String markerTitlee = String.valueOf(marker.getTitle());
                int index = 0 , titleIndex = 0;
                for (int i = 0 ; i<markerLat.size() ; i++){
                    if (markerLati.equals(markerLat.get(i))){
                        index = i;
                    }

                }
                for (int i = 0 ; i<markerTitle.size() ; i++){
                    if (markerTitlee.equals(markerTitle.get(i))){
                        titleIndex = i;
                    }

                }
                if (index == titleIndex) {
                    String APIUrl = myMarkerAPIUrl.get(index);
                    Bundle bundle = new Bundle();
                    baseActivity.httpService.getListsData(APIUrl, new HashMap<String, String>(), new HttpResponseCallback() {
                        @Override
                        public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                            if (response != null)
                                bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());

                            Log.d("viewCreated", "pfa table adapter new activity 3");
                            baseActivity.sharedPrefUtils.startActivityForResult(baseActivity, PFAAddNewActivity.class, bundle, RC_REFRESH_LIST);
                        }
                    }, true);
                }
                Log.d("clusterMarker" , "marker original title = " + myMarkerAPIUrl.get(index));
                Log.d("clusterMarker" , "marker tag = " + marker.getTag());
                super.onInfoWindowClick(marker);
            }
        });
        theGoogleMap.setOnCameraIdleListener(clusterManager);
        theGoogleMap.setOnMarkerClickListener(clusterManager);
        theGoogleMap.setOnInfoWindowClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems(markerTitle , markerLat , markerLng , markerColor , markerContent);
    }

    private void addItems(ArrayList<String> markerTitle, ArrayList<String> markerLat, ArrayList<String> markerLng, ArrayList<String> markerColor, ArrayList<String> markerContent) {

        // Set some lat/lng coordinates to start with.
//        double lat = 51.5145160;
//        double lng = -0.1270060;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < markerLat.size(); i++) {
//            double offset = i / 60d;
//            lat = lat + offset;
//            lng = lng + offset;
            MyItem offsetItem = new MyItem(Double.parseDouble(markerLat.get(i)), Double.parseDouble(markerLng.get(i)), markerTitle.get(i), markerContent.get(i));
            clusterManager.addItem(offsetItem);
        }
//        setUpMap();
    }


    private ListDataFetchedInterface listDataFetchedInterface;
    public List<FormSectionInfo> formSectionInfos;
    public boolean showFilter = false;
    public HashMap<String, List<FormDataInfo>> formFilteredData = new HashMap<>();

    public void setFetchDataInterface(ListDataFetchedInterface listDataFetchedInterface) {
        this.listDataFetchedInterface = listDataFetchedInterface;
    }
}

package com.pfa.pfaapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.interfaces.MapMoveCallback;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.utils.AppConst;

import java.util.ArrayList;
import java.util.Locale;

import static com.pfa.pfaapp.utils.AppConst.APP_LATITUDE;
import static com.pfa.pfaapp.utils.AppConst.APP_LONGITUDE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuMapFragment extends Fragment implements OnMapReadyCallback {
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
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_menu_map, container, false);
        viewMapPathBtn = rootView.findViewById(R.id.viewMapPathBtn);
        viewMapPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGoogleMap();
            }
        });
        return rootView;
    }

    private void viewGoogleMap() {
        if (businessLatLng != null) {
            // Create a Uri from an intent string. Use the result to create an Intent.
//            Uri gmmIntentUri = Uri.parse("geo:" + businessLatLng.latitude + "," + businessLatLng.longitude+"?z=18.0");

            String uri = String.format(Locale.getDefault(), "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", currentLatLng.latitude, currentLatLng.longitude, businessLatLng.latitude, businessLatLng.longitude);

            try {
                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }


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

        setMapPins(null);

        theGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mapMoveCallback != null)
                    mapMoveCallback.onMoveMap(latLng);
            }
        });

    }



    public void setMapPins(ArrayList<String> mapPinsArray) {
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
            for (String latLngExtra : latLngArray) {
                if (latLngExtra != null) {
                    latLngStr = latLngExtra.split(",");

                    if (latLngStr.length == 3) {

                        if (latLngArray != null && latLngArray.size() == 1) {
                            if (businessLatLng == null)
                                businessLatLng = new LatLng(Double.parseDouble(latLngStr[1]), Double.parseDouble(latLngStr[2]));
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

    private void setBusinessPin() {
        if (theGoogleMap == null)
            return;

        if (latLngArray.size() == 1) {
            theGoogleMap.clear();

            if (latLngStr != null && latLngStr.length > 0)
                theGoogleMap.addMarker(new MarkerOptions().position(businessLatLng).title(latLngStr[0]));

            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(businessLatLng,
                    DEFAULT_ZOOM);
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
    }

    public void addClickedLocation(LatLng latLng) {
        if (theGoogleMap != null) {
            theGoogleMap.clear();
            if (latLngArray != null)
                latLngArray.clear();
            setUpMap();
            theGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
        }
    }

}

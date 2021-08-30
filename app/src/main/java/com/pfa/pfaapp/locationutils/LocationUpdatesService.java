/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pfa.pfaapp.locationutils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import static com.pfa.pfaapp.utils.AppConst.APP_LATITUDE;
import static com.pfa.pfaapp.utils.AppConst.APP_LONGITUDE;

/**
 * A bound and started service that is promoted to a foreground service when location updates have
 * been requested and all clients unbind.
 * <p>
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 * <p>
 * This sample show how to use a long-running service for location updates. When an activity is
 * bound to this service, frequent location updates are permitted. When the activity is removed
 * from the foreground, the service promotes itself to a foreground service, and location updates
 * continue. When the activity comes back to the foreground, the foreground service stops, and the
 * notification associated with that service is removed.
 */
public class LocationUpdatesService extends Service {

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /*
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
//    private boolean mChangingConfiguration = false;

    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderClient}.
     */
    private LocationRequest mLocationRequest;
    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    private static boolean isReturn;

    public LocationUpdatesService() {
    }

    public void requestLocationUpdates(Context mContext) {
//        Log.i(TAG, "Requesting location updates");
        try {
            isReturn = false;
            mContext.startService(new Intent(mContext, LocationUpdatesService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendLocationToFirebase() {

        //            String userId = (new SharedPrefUtils(getApplicationContext())).getSharedPrefValue(SP_STAFF_ID);

        //            firebase code is commented because  project's database has been disabled due to exceeding its GB stored limit
        //            if (userId != null) {
        //                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        //                DatabaseReference locationCloudEndPoint = mDatabase.child("locationCloudEndPoint");
        //
        //                FirebaseTackInfo locationInfo = new FirebaseTackInfo();
        //                locationInfo.setLatitude(mLocation.getLatitude());
        //                locationInfo.setLongitude(mLocation.getLongitude());
        //
        ////                Log.e("UserId", "UserId=>" + userId);
        //                locationInfo.setUserId(Integer.parseInt(userId));
        //
        //                locationInfo.setTimeStamp(getCurreentTimeStamp());
        //
        //                String key = locationCloudEndPoint.push().getKey();
        //                locationInfo.setStaffId(key);
        //                locationCloudEndPoint.child(key).setValue(locationInfo).addOnFailureListener(new OnFailureListener() {
        //                    @Override
        //                    public void onFailure(@NonNull Exception e) {
        //                        Log.e(TAG, e.getLocalizedMessage());
        //                    }
        //                });
        //            }
    }

    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (isReturn) {
                    if (mFusedLocationClient != null && mLocationCallback != null) {
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                        stopSelf();
                        return;
                    }
                }
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i(TAG, "Service started");

        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void stopLocationService() {
//        this is the check that will disable the location update in LocationCallback (onCreate method)
        isReturn = true;
        stopSelf();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
//        Log.i(TAG, "in onBind()");
        return null;
    }


    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
        stopLocationService();
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (!task.isSuccessful() || task.getResult() == null) {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
//        Log.i(TAG, "New location: " + location);

        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(getApplicationContext());

        sharedPrefUtils.saveSharedPrefValue(APP_LATITUDE, "" + location.getLatitude());
        sharedPrefUtils.saveSharedPrefValue(APP_LONGITUDE, "" + location.getLongitude());

        sendLocationToFirebase();
    }

    /**
     * Sets the location request parameters.
     */
    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

//    private String getCurreentTimeStamp()
//    {
//        Calendar c = Calendar.getInstance();
////        System.out.println("Current time => "+c.getTime());
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
//        return df.format(c.getTime());
//    }
}

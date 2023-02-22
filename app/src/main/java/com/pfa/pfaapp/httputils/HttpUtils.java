package com.pfa.pfaapp.httputils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.security.ProviderInstaller;
import com.pfa.pfaapp.AppController;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.utils.HttpsTrustManager;
import com.pfa.pfaapp.utils.ScalingUtilities;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.X509TrustManager;

import static com.android.volley.Request.Method;
import static com.pfa.pfaapp.utils.AppConst.APP_LATITUDE;
import static com.pfa.pfaapp.utils.AppConst.APP_LONGITUDE;

import static com.pfa.pfaapp.utils.AppConst.APP_TOWN;
import static com.pfa.pfaapp.utils.AppConst.SP_APP_AUTH_TOKEN;
import static com.pfa.pfaapp.utils.AppConst.SP_AUTH_TOKEN;
import static com.pfa.pfaapp.utils.AppConst.SP_FCM_ID;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;
import static com.pfa.pfaapp.SplashActivity.BaseUrl;

import androidx.annotation.NonNull;

/**
 * HttpUtils->SharedPrefUtils->AppUtils->CustomDialogs
 */
class HttpUtils extends ScalingUtilities implements LocationListener /*implements X509TrustManager*/ {
    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};
     ProgressDialog progressDialog;
     Context context;

    HttpUtils(Context mContext) {
        super(mContext);
        context = mContext;
    }

    public boolean isNetworkDisconnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo == null) || !netInfo.isConnected();
    }

    void httpGet(String requestUrl, HashMap<String, String> httpParams, final HttpResponseCallback callback, boolean showProgress) {

//        To solve the issue of creation of multiple records , add "DO_POST" keyword in GET URL.
        if (requestUrl.contains("DO_POST")) {
            httpPost(requestUrl, httpParams, callback, showProgress);
            return;
        }

        if (isNetworkDisconnected()) {
            showMsgDialog(mContext.getResources().getString(R.string.no_internet_connection), null);
            return;
        }
        if (requestUrl.contains("&HTTP_CURRENT_")) {
            int firstIndex = requestUrl.indexOf("&HTTP_CURRENT_");
            requestUrl = requestUrl.substring(0, firstIndex);
        }

        if (requestUrl.endsWith("null"))
            return;
        if (showProgress)
            showProgressDialog(false);

        final StringBuilder url = new StringBuilder(requestUrl);
        if (httpParams != null) {
            for (String key : httpParams.keySet()) {

                try {
                    if (url.toString().contains("?")) {
                        url.append("&").append(key).append("=").append(URLEncoder.encode(httpParams.get(key), "UTF-8"));
                    } else {
                        url.append("?").append(key).append("=").append(URLEncoder.encode(httpParams.get(key), "UTF-8"));
                    }

                } catch (UnsupportedEncodingException e) {
                    printStackTrace(e);
                }
            }
        }

        if (getSharedPrefValue(SP_STAFF_ID, "") != null && getSharedPrefValue(APP_LATITUDE, "") != null) {

            /*double lat = Double.parseDouble(getSharedPrefValue(APP_LATITUDE , ""));
            double lng = Double.parseDouble(getSharedPrefValue(APP_LONGITUDE , ""));

            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(29.7956, 72.8634, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String cityName = addresses.get(0).getAddressLine(0);
            String stateName = String.valueOf(addresses);
            String countryName = addresses.get(0).getLocality();

            Log.d("CurrentTown" , "city = " + cityName);
            Log.d("CurrentTown" , "state = " + stateName);
            Log.d("CurrentTown" , "country = " + countryName);*/

            try {
                if (url.toString().contains("?")) {
                    url.append("&").append("HTTP_CURRENT_LAT").append("=").append(URLEncoder.encode(getSharedPrefValue(APP_LATITUDE, ""), "UTF-8"));
                } else {
                    url.append("?").append("HTTP_CURRENT_LAT").append("=").append(URLEncoder.encode(getSharedPrefValue(APP_LATITUDE, ""), "UTF-8"));
                }
                url.append("&").append("HTTP_CURRENT_LNG").append("=").append(URLEncoder.encode(getSharedPrefValue(APP_LONGITUDE, ""), "UTF-8"));
                url.append("&").append(SP_STAFF_ID).append("=").append(URLEncoder.encode(getSharedPrefValue(SP_STAFF_ID, ""), "UTF-8"));
//              url.append("&").append(SP_AUTH_TOKEN).append("=").append(URLEncoder.encode(getSharedPrefValue(SP_AUTH_TOKEN), "UTF-8"));
                url.append("&").append(SP_FCM_ID).append("=").append(URLEncoder.encode(getSharedPrefValue(SP_FCM_ID, ""), "UTF-8"));
            //////////////THIISSS
                if (!SP_APP_AUTH_TOKEN.isEmpty())
                url.append("&").append(SP_APP_AUTH_TOKEN).append("=").append(URLEncoder.encode(getSharedPrefValue(SP_APP_AUTH_TOKEN, ""), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        printLog("Request Url=>", "" + url.toString());
//        HttpsTrustManager.allowAllSSL();
        updateAndroidSecurityProvider();
        String finalRequestUrl = requestUrl;
        StringRequest stringRequest = new StringRequest(Method.GET, url.toString().replaceAll(" ", "%20"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                printLog("Response:", "   => " + response);
                hideProgressDialog();
                if (callback != null) {
                    try {
                        if (response != null) {
                            JSONObject jsonObject = new JSONObject(response);
                            /////////
                            if (jsonObject.optBoolean("invalid_user")) {
                                Log.d("invalidUser" , "invalid_user 1");
                                invalidUserLogout(jsonObject.optString("message_code"));
                            } else {
                                callback.onCompleteHttpResponse(jsonObject, url.toString());
                            }
                            //////////

                        }
                    } catch (JSONException e) {
                        callback.onCompleteHttpResponse(null, url.toString());
                        printStackTrace(e);
                    }
                }
//                showMsgDialog("Http Error: Status Code=> " + response, null);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {

                printStackTrace(volleyError);
                hideProgressDialog();

                if (volleyError instanceof TimeoutError) {
                    Log.d("volleyErrorMsg" , "msg 1= " + "TimeoutError");
//                    DecodeError(context , volleyError);
                    //For example your timeout is 3 seconds but the operation takes longer
                }

                else if (volleyError instanceof ServerError) {
                    //error in server
                    Log.d("volleyErrorMsg" , "msg 1= " + "ServerError" + volleyError);
                    volleyError.printStackTrace();
//                    DecodeError(context , volleyError);
                }

                else if (volleyError instanceof NetworkError) {
                    Log.d("volleyErrorMsg" , "msg 1= " + "NetworkError");
//                    DecodeError(context , volleyError);
                    //network is disconnect
                }

                else if (volleyError instanceof ParseError) {
                    //for cant convert data
                    Log.d("volleyErrorMsg" , "msg 1= " + "ParseError");
//                    DecodeError(context , volleyError);
                }

                else {
                    Log.d("volleyErrorMsg" , "msg 1= " + "Other");
//                    DecodeError(context , volleyError);
                    //other error
                }


                ////////
                if (finalRequestUrl.equals("https://app.pfa.gop.pk/api/BaseURL/GetBaseURL?applicationName=cellpfagop")){
                    try {
                        JSONObject obj = new JSONObject(loadJSONFromAsset());
                        callback.onCompleteHttpResponse(obj , null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("currentApiVersion", "httpUtils= 1");
                }
                else {
                    Log.d("currentApiVersion", "httpUtils= 2");
                    if (volleyError.networkResponse != null) {
                        showMsgDialog("Some Error Occurred, Please Try Again!1" /*+ "\n" + url.toString()*/, null);
                    } else {
                        showMsgDialog("Please Check Your Internet Connection and Try Again!1", null);
                    }
                }


            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                if (getSharedPrefValue(SP_STAFF_ID, "") != null && getSharedPrefValue(APP_LATITUDE, "") != null) {

                    headers.put("HTTP-CURRENT-LAT", getSharedPrefValue(APP_LATITUDE, ""));
                    headers.put("HTTP-CURRENT-LNG", getSharedPrefValue(APP_LONGITUDE, ""));
//                    headers.put("HTTP-CURRENT-TOWN", getSharedPrefValue(APP_TOWN, ""));
                    headers.put("HTTP-USER-ID", getSharedPrefValue(SP_STAFF_ID, ""));
//                    headers.put("APP-AUTH-TOKEN",getSharedPrefValue(SP_APP_AUTH_TOKEN,""));

                    if (getSharedPrefValue(SP_APP_AUTH_TOKEN, "") != null) {
                        headers.put("APP-AUTH-TOKEN", getSharedPrefValue(SP_APP_AUTH_TOKEN, ""));
                    }
                    else {
                        Toast.makeText(mContext, "APP-AUTH-TOKEN", Toast.LENGTH_SHORT).show();
                    }

                    if (getSharedPrefValue(SP_AUTH_TOKEN, "") != null) {
                        headers.put("AUTH-TOKEN", getSharedPrefValue(SP_AUTH_TOKEN, ""));
                    }
                }

                return headers;
            }

            @Override
            public Priority getPriority() {
                return Priority.HIGH;//super.getPriority();
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 100000;
            }

            @Override
            public int getCurrentRetryCount() {
                return DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
            }

            @Override
            public void retry(VolleyError error) {
                printStackTrace(error);
            }
        });

        // Add Request to Queue
        stringRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(stringRequest, "" + requestUrl);
    }

    private HashMap<String, String> params;

    void httpPost(final String requestUrl, HashMap<String, String> httpParams, final HttpResponseCallback callback, final boolean showProgress) {

        this.params = httpParams;


        if (isNetworkDisconnected()) {
            showMsgDialog(mContext.getResources().getString(R.string.no_internet_connection), null);
            return;
        }

        if (showProgress)
            showProgressDialog(false);

//        final String TAG = "HttpPOSTRequest";
        printLog("Request URL:=> ", "" + requestUrl);

// waiting response msg
//        if(!((Activity) context).isFinishing()) {
            //show dialog
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Submitting please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
//        }


//        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Method.POST, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();
                printLog("Response ", "  Response=>  " + response);
                if (callback != null) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();

                        JSONObject jsonObject = new JSONObject(response);
                        /////////
                        if (jsonObject.optBoolean("invalid_user")) {
                            Log.d("invalidUser" , "invalid_user 2");
                            invalidUserLogout(jsonObject.optString("message_code"));

                        } else {
                            callback.onCompleteHttpResponse(jsonObject, requestUrl);
                        }
                        //////////

                    } catch (JSONException e) {
                        callback.onCompleteHttpResponse(null, requestUrl);
                        printStackTrace(e);
                    }
                }

//                showMsgDialog("Http Error: Status Code=> " + response, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                printStackTrace(volleyError);
//                Log.d("volleyErrorMsg" , "msg = " + volleyError.getMessage().toString());

                if (volleyError instanceof TimeoutError) {
                    Log.d("volleyErrorMsg" , "msg = " + "TimeoutError");
                    //For example your timeout is 3 seconds but the operation takes longer
                }

                else if (volleyError instanceof ServerError) {
                    //error in server
                    Log.d("volleyErrorMsg" , "msg = " + "ServerError" + volleyError);
                    volleyError.printStackTrace();
//                    DecodeError(context , volleyError);
                }

                else if (volleyError instanceof NetworkError) {
                    Log.d("volleyErrorMsg" , "msg = " + "NetworkError");
                    //network is disconnect
                }

                else if (volleyError instanceof ParseError) {
                    //for cant convert data
                    Log.d("volleyErrorMsg" , "msg = " + "ParseError");
                }

                else {
                    Log.d("volleyErrorMsg" , "msg = " + "Other");
                    //other error
                }

                if (showProgress)
                    hideProgressDialog();

                if (volleyError.networkResponse != null) {
//                    showMsgDialog("Http Error: Status Code=> " + volleyError.networkResponse.statusCode + " Message: " + volleyError.getMessage(), null);
                    showMsgDialog("Some Error Occurred, Please Try Again!2", null);
                } else {
                    showMsgDialog("Please Check Your Internet Connection and Try Again!2", null);
                }

                if (callback != null)
                    callback.onCompleteHttpResponse(null, requestUrl);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
            /*@Override
            public String getBodyContentType()
            {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }*/

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//                headers.put("Content-Type", "application/json; charset=UTF-8");
                if (getSharedPrefValue(SP_STAFF_ID, "") != null && getSharedPrefValue(APP_LATITUDE, "") != null) {

                    /*double lat = Double.parseDouble(getSharedPrefValue(APP_LATITUDE , ""));
                    double lng = Double.parseDouble(getSharedPrefValue(APP_LONGITUDE , ""));

                    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String cityName = addresses.get(0).getAddressLine(0);
                    String stateName = addresses.get(0).getAddressLine(1);
                    String countryName = addresses.get(0).getAddressLine(2);

                    Log.d("CurrentTown" , "city1 = " + cityName);
                    Log.d("CurrentTown" , "state1 = " + stateName);
                    Log.d("CurrentTown" , "country1 = " + countryName);*/

                    Log.d("getHeaders" , "SP_STAFF_ID = " + "not null");

                    headers.put("HTTP-CURRENT-LAT", getSharedPrefValue(APP_LATITUDE, ""));
                    headers.put("HTTP-CURRENT-LNG", getSharedPrefValue(APP_LONGITUDE, ""));
                    headers.put("HTTP-USER-ID", getSharedPrefValue(SP_STAFF_ID, ""));
//                    6.put("APP-AUTH-TOKEN",getSharedPrefValue(SP_APP_AUTH_TOKEN,""));

                    if (getSharedPrefValue(SP_APP_AUTH_TOKEN, "") != null) {
                        headers.put("APP-AUTH-TOKEN", getSharedPrefValue(SP_APP_AUTH_TOKEN, ""));
                        Log.d("getHeaders" , "SP_APP_AUTH_TOKEN = " + "not null");
                    }

                    if (getSharedPrefValue(SP_AUTH_TOKEN, "") != null) {
                        headers.put("AUTH-TOKEN", getSharedPrefValue(SP_AUTH_TOKEN, ""));
                        Log.d("getHeaders" , "SP_AUTH_TOKEN = " + "not null");

                    }
                }

                return headers;
            }

            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }
        };

//        //Volley does retry for you if you have specified the policy.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        // Add Request to queue
        stringRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(stringRequest, "" + requestUrl);
    }
    /*@Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        printLog("checkClientTrusted =>", "X509Certificate authType " + authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        printLog("checkServerTrusted =>", "X509Certificate authType " + authType);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return _AcceptedIssuers;
    }*/

    void httpMultipartAPICall(final String requestUrl, Map<String, String> params, Map<String, File> fileParams,
                              final HttpResponseCallback callback, boolean showProgress) {

        if(((Activity)mContext).isFinishing())
            return;

//         Picture
        //mimeType:@"image/jpeg"
        final String TAG = "HttpMultipartRequest";

        if (isNetworkDisconnected()) {
            showMsgDialog(mContext.getResources().getString(R.string.no_internet_connection), null);
            return;
        }

//        if (showProgress)
//            showProgressDialog(false);

//        if(!((Activity) context).isFinishing()) {
            //show dialog
        if (showProgress) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Submitting please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
//        }

        MultipartRequest multipartRequest = new MultipartRequest(requestUrl, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {

                if (showProgress)
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

                printStackTrace(volleyError);
                hideProgressDialog();

                if (volleyError instanceof TimeoutError) {
                    Log.d("volleyErrorMsg3" , "msg = " + "TimeoutError");
                    //For example your timeout is 3 seconds but the operation takes longer
                }

                else if (volleyError instanceof ServerError) {
                    //error in server
                    Log.d("volleyErrorMsg3" , "msg = " + "ServerError" + volleyError);
                    volleyError.printStackTrace();
//                    DecodeError(context , volleyError);
                }

                else if (volleyError instanceof NetworkError) {
                    Log.d("volleyErrorMsg3" , "msg = " + "NetworkError");
                    //network is disconnect
                }

                else if (volleyError instanceof ParseError) {
                    //for cant convert data
                    Log.d("volleyErrorMsg3" , "msg = " + "ParseError");
                }

                else {
                    Log.d("volleyErrorMsg3" , "msg = " + "Other");
                    //other error
                }

                if (volleyError.networkResponse != null) {
                    showMsgDialog("Some Error Occurred, Please Try Again!3", null);
                } else {
                    showMsgDialog("Please Check Your Internet Connection and Try Again!3", null);
                }
            }
        }, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                printLog(TAG, "" + response);
                hideProgressDialog();
                if (showProgress)
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("invalid_user")) {

                        Log.d("invalidUser" , "invalid_user 3");
                        invalidUserLogout(jsonObject.optString("message_code"));
                    } else {
                        callback.onCompleteHttpResponse(jsonObject, requestUrl);
                    }
                    //////////
                } catch (JSONException e) {
                    callback.onCompleteHttpResponse(null, requestUrl);
                    printStackTrace(e);
                }

            }
        }, fileParams, params) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "multipart/form-data; charset=UTF-8");
                if (getSharedPrefValue(SP_STAFF_ID, "") != null && getSharedPrefValue(APP_LATITUDE, "") != null) {

                    headers.put("HTTP-CURRENT-LAT", getSharedPrefValue(APP_LATITUDE, ""));
                    headers.put("HTTP-CURRENT-LNG", getSharedPrefValue(APP_LONGITUDE, ""));
                    headers.put("HTTP-USER-ID", getSharedPrefValue(SP_STAFF_ID, ""));

                    if (getSharedPrefValue(SP_APP_AUTH_TOKEN, "") != null) {
                        headers.put("APP-AUTH-TOKEN", getSharedPrefValue(SP_APP_AUTH_TOKEN, ""));
                    }
                    else {
                        Toast.makeText(mContext, "APP-AUTH-TOKEN", Toast.LENGTH_SHORT).show();
                    }

                    if (getSharedPrefValue(SP_AUTH_TOKEN, "") != null) {
                        headers.put("AUTH-TOKEN", getSharedPrefValue(SP_AUTH_TOKEN, ""));
                    }
                }

                return headers;
            }

            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }

            @Override
            public byte[] getBody() {
                return super.getBody();
            }

            @Override
            public String getBodyContentType() {
                return super.getBodyContentType();
            }
        };

        //Volley does retry for you if you have specified the policy.
        multipartRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 100000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) {
                printStackTrace(error);
            }
        });

        // Add Request to queue
        multipartRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(multipartRequest, "" + requestUrl);
    }

    private void invalidUserLogout(String message_code) {
        showInvalidUserDialog(message_code, new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {

                removeSharedPrefValue(SP_AUTH_TOKEN);
                removeSharedPrefValue(SP_APP_AUTH_TOKEN);

                if (message.equals("")) {
                    HttpService httpService = new HttpService(mContext);
                    logoutFromApp(httpService);
                }

            }
        });
    }

    private static void DecodeError(Context context, VolleyError error) {
        Log.d("volleyErrorMsg" , "DecodeError = ");
        NetworkResponse response = error.networkResponse;
        try {
            Log.d("volleyErrorMsg" , "try = ");
            String res = null;
            if (response !=null)
            res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
            // Now you can use any deserializer to make sense of data
//            JSONObject obj = new JSONObject(res);
//            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show();
//            Log.d("volleyErrorMsg" , "msg = " + obj.getString("message"));
            Log.d("volleyErrorMsg" , "res = " + res);

        } catch (UnsupportedEncodingException e1) {
            Log.d("volleyErrorMsg" , "UnsupportedEncodingException = ");
            // Couldn't properly decode data to string
            e1.printStackTrace();
        } /*catch (JSONException e2) {
            Log.d("volleyErrorMsg" , "JSONException = ");
            e2.printStackTrace();
        }*/
    }

    private void updateAndroidSecurityProvider() { try { ProviderInstaller.installIfNeeded(mContext); } catch (Exception e) { e.getMessage(); } }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();


    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("baseurl.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}

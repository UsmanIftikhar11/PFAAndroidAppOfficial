package com.pfa.pfaapp.httputils;

import android.content.Context;
import android.util.Log;

import com.pfa.pfaapp.interfaces.HttpResponseCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.pfa.pfaapp.SplashActivity.BaseUrl;
import static com.pfa.pfaapp.httputils.ConfigHttpUtils.MAIN_MENU_POSTFIX;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;

/**
 * HttpService->HttpUtils->SharedPrefUtils->AppUtils->CustomDialogs
 */

public class HttpService extends HttpUtils {
    //    Dev API
//    private static final String BASE_URL = "https://cell.pfa.gop.pk/dev/api/";
//    private static final String BASE_URL = "http://192.168.1.129/api/";
//    private static final String BASE_URL = "http://182.176.112.99/pfa/api/";
    private static final String BASE_URL = "https://cell.pfa.gop.pk/dev/api/";
//    private static final String BASE_URL = "https://test.pfa.gop.pk/api/";
//    private static final String BASE_URL = "http://182.176.112.99:8087/api/";

    //    Live APIhttps:
//    private static final String BASE_URL = BaseUrl;
//    private static final String BASE_URL = "https://cell.pfa.gop.pk/api/";
//    private static final String BASE_URL = "https://cellpfa.chimpstudio.co.uk/api/";

//    private static final String BASE_URL = "http://54.39.33.105/api/";


    public Context getContext() {
        return mContext;
    }

    public HttpService(Context mContext) {
        super(mContext);
    }

    public void authenticateUser(HashMap<String, String> httpParams, String type, HttpResponseCallback callback) {
        httpPost(BaseUrl + "account/login/" + type, httpParams, callback, true);
        Log.d("loginResp" , "url = " + BaseUrl + "account/login/" + type);
    }

    public void registerUser(HashMap<String, String> reqParams, boolean isUserVerified, HttpResponseCallback callback) {
        httpPost(isUserVerified ? BaseUrl + "account/registerAuth/1" : BaseUrl + "account/registerAuth", reqParams, callback, true);
    }

    public void forgetPin(HashMap<String, String> httpParams, HttpResponseCallback callback) {
        httpPost(BaseUrl + "account/forgetPin", httpParams, callback, true);
    }

    public void getLoginSettings(HttpResponseCallback callback) {
        httpGet(BaseUrl + "account/settings", new HashMap<String, String>(), callback, true);
    }

    public void getUserInfo(String userId, String fcmId, HttpResponseCallback callback, boolean showProgress) {
        httpGet(BaseUrl + "account/users/" + userId + "/" + fcmId + "/" + getSharedPrefValue(SP_LOGIN_TYPE, ""), new HashMap<String, String>(), callback, showProgress);
    }

    public void getMainMenu(HttpResponseCallback callback, String userID) {
        httpGet(BaseUrl + (MAIN_MENU_POSTFIX) + userID, new HashMap<String, String>(), callback, false);
    }


    public void getSideMenu(String userId, String userType, HttpResponseCallback callback) {
        httpGet(BaseUrl + "menu/" + userId + "/" + userType, new HashMap<String, String>(), callback, true);
    }

    public void getUserConfirmation(String userId, String pincode, HttpResponseCallback callback) {
        httpGet(BaseUrl + "account/user_authentication/" + userId + "/" + pincode, new HashMap<String, String>(), callback, true);
    }


    public void fetchConfigData(String suffix, HttpResponseCallback callback) {
        getListsData(suffix, new HashMap<String, String>(), callback, true);
    }

    public void getListsData(String suffix, HashMap<String, String> params, HttpResponseCallback callback, boolean showProgress) {
        httpGet(BaseUrl + suffix, params, callback, showProgress);
        Log.d("getListData" , "suffix = " + suffix);
    }

    public void getListsData1(String suffix, HashMap<String, String> params, HttpResponseCallback callback, boolean showProgress) {
        httpGet( suffix, params, callback, showProgress);
        Log.d("getListData" , "suffix = " + suffix);
    }

    public void formSubmit(HashMap<String, String> httpParams, Map<String, File> fileParams, String suffix, HttpResponseCallback callback, boolean showProgress, String actionType) {
        if (fileParams != null && fileParams.size() > 0) {
            httpMultipartAPICall(BaseUrl + suffix, httpParams, fileParams, callback,showProgress);
            Log.d("getListData" , "formSubmit 1= " + suffix);
        } else {
            if (actionType != null && actionType.equals("get")) {
                Log.d("getListData" , "formSubmit 2= " + suffix);
                httpGet(BaseUrl + suffix, httpParams, callback, showProgress);
            } else {
                Log.d("getListData" , "formSubmit 3= " + suffix);
                httpPost(BaseUrl + suffix, httpParams, callback, showProgress);
            }
        }
    }

    public void updateToken(HashMap<String, String> httpParams) {
        httpPost(BaseUrl + "account/tokenUpdate", httpParams, null, false);
    }

    public void sendEmergencyMessage(HashMap<String, String> httpParams, HttpResponseCallback callback) {
        httpPost(BaseUrl + "account/sendEmergencyMessage", httpParams, callback, false);
    }

    public void logout(HashMap<String, String> httpParams, HttpResponseCallback callback) {
        Log.d("invalidUser" , "httpPost = " + "logout");
        httpPost(BaseUrl + "account/tokenDelete", httpParams, callback, true);
    }

    public void logoutFromAll(HashMap<String, String> httpParams, HttpResponseCallback callback) {
        httpPost(BaseUrl + "account/logout_from_all_devices", httpParams, callback, true);
    }

    public void deleteDraftInspection(String inspectionID, HttpResponseCallback callback, boolean showProgress) {
        httpGet(BaseUrl + "inspections/download_inspection_show_back/" + inspectionID, new HashMap<String, String>(), callback, showProgress);
    }

    public void checkExistingUser(HashMap<String, String> params, HttpResponseCallback callback, boolean showProgress) {
        httpGet(BaseUrl + "account/check_existing_user", params, callback, showProgress);
    }

    public void checkExistingBusiness(String suffix , HashMap<String, String> params, HttpResponseCallback callback, boolean showProgress) {
        httpGet(BaseUrl + suffix, params, callback, showProgress);
    }

    public void setPinCode(HashMap<String, String> httpParams, String type, HttpResponseCallback callback) {
        httpPost(BaseUrl + "account/setpincode/" + type, httpParams, callback, true);
    }
}

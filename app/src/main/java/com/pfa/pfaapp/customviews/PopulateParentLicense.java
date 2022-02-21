package com.pfa.pfaapp.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.CheckUserCallback;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PopulateParentLicense extends LinearLayout implements HttpResponseCallback {

    PFAViewsUtils sharedPrefUtils;
    CheckUserCallback checkUserCallback;
    String suffix;
    String business_category_val;
    Context mContext;


    public PopulateParentLicense(Context mContext, String revisedLicesneUrl , String business_category_val , CheckUserCallback checkUserCallback ) {
        super(mContext);
        this.checkUserCallback = checkUserCallback;
        this.business_category_val = business_category_val;
        this.suffix = revisedLicesneUrl;

        this.mContext = mContext;
        init();
    }

    public PopulateParentLicense(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        sharedPrefUtils = new PFAViewsUtils(getContext());

        if (suffix != null)
            suffix = suffix.substring(0, suffix.lastIndexOf('/'));
        HashMap<String, String> reqParams = new HashMap<>();

        Log.e("createViewDropdown" , "new Url 1 = " + suffix);
        Log.e("createViewDropdown" , "val 1= " + business_category_val);
//        Log.e("createViewDropdown" , "business_id = " + id);

        reqParams.put("selected_business_category" , business_category_val);

        HttpService httpService = new HttpService(mContext);

        httpService.checkExistingBusiness(suffix , reqParams, new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

                if (response != null && response.optBoolean("status")) {

                    final JSONArray jsonArray = response.optJSONArray("data");
                    if (jsonArray == null || jsonArray.length() == 0) {
                        Log.d("revisedLicense" , "null liscenese");
                        sharedPrefUtils.showMsgDialog("Error Getting Data from the Server", null);
                    } else {
                        Log.d("revisedLicense" , "not null liscenese  " + jsonArray);
                        checkUserCallback.getExistingBusiness(jsonArray , "");
                    }
                } else
                    sharedPrefUtils.showMsgDialog("No Data Received from the Server", null);
            }
        }, true);
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
    }

}

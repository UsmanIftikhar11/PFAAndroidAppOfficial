package com.pfa.pfaapp.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.pfa.pfaapp.PrinterCommands;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.CheckUserCallback;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.WhichItemClicked;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class VerifyFBOBusiness extends LinearLayout implements HttpResponseCallback {

    PFAViewsUtils sharedPrefUtils;
    CheckUserCallback checkUserCallback;
    String id , suffix;
    Context mContext;


    public VerifyFBOBusiness(Context mContext, String id , String suffix , CheckUserCallback checkUserCallback ) {
        super(mContext);
        this.checkUserCallback = checkUserCallback;
        this.id = id;
        this.suffix = suffix;

        this.mContext = mContext;
        Log.e("VerifyFBOBusiness" , "VerifyFBOBusiness = created" );
        init();
    }

    public VerifyFBOBusiness(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        sharedPrefUtils = new PFAViewsUtils(getContext());

        if (suffix != null)
            suffix = suffix.substring(0, suffix.lastIndexOf('/'));
        HashMap<String, String> reqParams = new HashMap<>();

        Log.e("createViewDropdown" , "new Url = " + suffix);
        Log.e("createViewDropdown" , "business_id = " + id);

        reqParams.put("business_id", id);
        HttpService httpService = new HttpService(mContext);

        httpService.checkExistingBusiness(suffix , reqParams, new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

                if (response != null && response.optBoolean("status")) {

                    final JSONArray jsonArray = response.optJSONArray("data");
                    if (jsonArray == null || jsonArray.length() == 0) {
                        sharedPrefUtils.showMsgDialog("There is no emergency information against this business", null);
                    } else {
                        checkUserCallback.getExistingBusiness(jsonArray , "");
                    }
                }
            }
        }, true);
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
    }

}

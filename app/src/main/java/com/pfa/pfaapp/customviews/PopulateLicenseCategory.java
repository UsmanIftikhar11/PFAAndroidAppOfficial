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

public class PopulateLicenseCategory extends LinearLayout implements HttpResponseCallback {

    PFAViewsUtils sharedPrefUtils;
    CheckUserCallback checkUserCallback;
    String suffix;
    String parent_license_type;
    String parent_license_type_key;
    Context mContext;
    ArrayList<String> retailer_val = new ArrayList<>();
    ArrayList<String> retailer_val_key = new ArrayList<>();
    String revisedLicesneUrl;


    public PopulateLicenseCategory(Context mContext, String revisedLicesneUrl , ArrayList<String> retailer_val , ArrayList<String> retailer_val_key , String parent_license_type , String parent_license_type_key , CheckUserCallback checkUserCallback ) {
        super(mContext);
        this.checkUserCallback = checkUserCallback;
        this.revisedLicesneUrl = revisedLicesneUrl;
        this.parent_license_type = parent_license_type;
        this.parent_license_type_key = parent_license_type_key;
        this.retailer_val = retailer_val;
        this.retailer_val_key = retailer_val_key;
//        this.suffix = "Client/get_license_type/";
        this.suffix = revisedLicesneUrl;

        this.mContext = mContext;
        init();
    }

    public PopulateLicenseCategory(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        sharedPrefUtils = new PFAViewsUtils(getContext());

        if (suffix != null)
            suffix = suffix.substring(0, suffix.lastIndexOf('/'));
        HashMap<String, String> reqParams = new HashMap<>();

        Log.e("createViewDropdown" , "new Url = " + suffix);
//        Log.e("createViewDropdown" , "business_id = " + id);

        if (parent_license_type.equals("Retailer")) {
            Log.d("liscentype" , "Retailer PopulateLicenseCategory");
            reqParams.put("parent_license_type" , "Retailer");
            reqParams.put(retailer_val_key.get(0), retailer_val.get(0));
            reqParams.put(retailer_val_key.get(1), retailer_val.get(1));
            reqParams.put(retailer_val_key.get(2), retailer_val.get(2));
            reqParams.put(retailer_val_key.get(3), retailer_val.get(3));
            reqParams.put(retailer_val_key.get(4), retailer_val.get(4));
            reqParams.put(retailer_val_key.get(5), retailer_val.get(5));

        } else if (parent_license_type.equals("Manufacturer")) {
            Log.d("liscentype" , "Manufacturer PopulateLicenseCategory");
            reqParams.put("parent_license_type" , "Manufacturer");
            reqParams.put(retailer_val_key.get(0), retailer_val.get(0));
            reqParams.put(retailer_val_key.get(1), retailer_val.get(1));
            reqParams.put(retailer_val_key.get(2), retailer_val.get(2));
            reqParams.put(retailer_val_key.get(3), retailer_val.get(3));
            reqParams.put(retailer_val_key.get(4), retailer_val.get(4));
            reqParams.put(retailer_val_key.get(5), retailer_val.get(5));
            reqParams.put(retailer_val_key.get(6), retailer_val.get(6));
            reqParams.put(retailer_val_key.get(7), retailer_val.get(7));
            reqParams.put(retailer_val_key.get(8), retailer_val.get(8));
        } else if (parent_license_type.equals("Restaurants")) {
            Log.d("liscentype" , "Restaurants PopulateLicenseCategory");
            Log.d("liscentype" , "Restaurants PopulateLicenseCategory suffix = " + suffix);
            reqParams.put("parent_license_type" , "Restaurants");
            reqParams.put(retailer_val_key.get(0), retailer_val.get(0));
            reqParams.put(retailer_val_key.get(1), retailer_val.get(1));
            reqParams.put(retailer_val_key.get(2), retailer_val.get(2));
            reqParams.put(retailer_val_key.get(3), retailer_val.get(3));
            reqParams.put(retailer_val_key.get(4), retailer_val.get(4));
            reqParams.put(retailer_val_key.get(5), retailer_val.get(5));
            reqParams.put(retailer_val_key.get(6), retailer_val.get(6));
            reqParams.put(retailer_val_key.get(7), retailer_val.get(7));
            reqParams.put(retailer_val_key.get(8), retailer_val.get(8));
        } else if (parent_license_type.equals("E-Commerce")) {
            Log.d("liscentype" , "E-Commerce PopulateLicenseCategory");
            reqParams.put("parent_license_type" , "E-Commerce");
            reqParams.put(retailer_val_key.get(0), retailer_val.get(0));
            reqParams.put(retailer_val_key.get(1), retailer_val.get(1));
            reqParams.put(retailer_val_key.get(2), retailer_val.get(2));
            reqParams.put(retailer_val_key.get(3), retailer_val.get(3));
            reqParams.put(retailer_val_key.get(4), retailer_val.get(4));
        }

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
                        checkUserCallback.getExistingBusiness(jsonArray , response.optString("confirmMsg"));
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

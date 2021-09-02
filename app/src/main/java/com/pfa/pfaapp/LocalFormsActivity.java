/*
 * Copyright (c) 2018. All Rights Reserved by Punjab Food Authority
 */

package com.pfa.pfaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.pfa.pfaapp.customviews.LocalFormLL;
import com.pfa.pfaapp.customviews.LocalGridLL;
import com.pfa.pfaapp.customviews.LocalListLL;
import com.pfa.pfaapp.fragments.MenuMapFragment;
import com.pfa.pfaapp.interfaces.DDSelectedCallback;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.RBClickCallback;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.utils.AddInspectionUtils;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_DOWNLOAD_URL;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_INSPECTION_DATA;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;

public class LocalFormsActivity extends BaseActivity implements HttpResponseCallback, RBClickCallback, DDSelectedCallback {

    boolean allowBackPress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_forms);
        MenuMapFragment.businessLatLng = null;
        AppConst.INSPECTION_ID = null;

        addInspectionUtils = new AddInspectionUtils(this, this, this, null);

        addInspectionUtils.downloadLocalImgBtn = findViewById(R.id.downloadLocalImgBtn);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_INSPECTION_DATA)) {
            addInspectionUtils.populateDraftInspection(getIntent().getExtras());
            if (getIntent().getExtras().containsKey("isDraft")) {
                addInspectionUtils.isDraft = true;
            } else {
                addInspectionUtils.downloadLocalImgBtn.setVisibility(View.VISIBLE);
            }
        } else {
            String fetchFormUrl = getIntent().getExtras().getString(EXTRA_URL_TO_CALL);

            if (getIntent().getExtras().containsKey(EXTRA_DOWNLOAD_URL)) {
                addInspectionUtils.downloadUrl = getIntent().getExtras().getString(EXTRA_DOWNLOAD_URL);
            }

            if (fetchFormUrl != null) {
                if (getIntent().hasExtra(EXTRA_JSON_STR_RESPONSE)) {
                    String responseStr = getIntent().getStringExtra(EXTRA_JSON_STR_RESPONSE);

                    try {
                        JSONObject responseJSONObject = new JSONObject(responseStr);
                        onCompleteHttpResponse(responseJSONObject, fetchFormUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        addInspectionUtils.downloadLocalImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInspectionUtils.onClickDownloadAsDraftBtn(null);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                allowBackPress = true;
            }
        }, 300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("imagePath" , "onActivityResult = " + "localFormActivity");

        if (addInspectionUtils.lastClicked >= 0) {
            if (addInspectionUtils.localFormsLL.getChildAt(addInspectionUtils.lastClicked) instanceof LocalFormLL)
                ((LocalFormLL) addInspectionUtils.localFormsLL.getChildAt(addInspectionUtils.lastClicked)).onActivityResult(requestCode, resultCode, data);

            if (addInspectionUtils.localFormsLL.getChildAt(addInspectionUtils.lastClicked) instanceof LocalGridLL)
                ((LocalGridLL) addInspectionUtils.localFormsLL.getChildAt(addInspectionUtils.lastClicked)).onActivityResult(requestCode, resultCode, data);

            if (addInspectionUtils.localFormsLL.getChildAt(addInspectionUtils.lastClicked) instanceof LocalListLL)
                ((LocalListLL) addInspectionUtils.localFormsLL.getChildAt(addInspectionUtils.lastClicked)).onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onClickBackImgBtn(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (addInspectionUtils.saveFormBtn.getVisibility() == View.GONE) {

            if (isTaskRoot()) {
                if (!String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase(sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, ""))) {
                    sharedPrefUtils.startHomeActivity(PFADrawerActivity.class, null);
                    return;
                }
            }

            finish();
        } else {
            addInspectionUtils.setSaveDraftData();
        }
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        addInspectionUtils.onCompleteHttpResponse(response);
    }

    @Override
    public void onClickRB(View targetView) {
        addInspectionUtils.onClickRB(targetView);
    }

    @Override
    public void onDDDataSelected(FormDataInfo formDataInfo) {
        addInspectionUtils.onDDDataSelected(formDataInfo);
    }


}

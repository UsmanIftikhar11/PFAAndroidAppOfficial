/*
 * Copyright (c) 2018. All Rights Reserved by Punjab Food Authority
 */

package com.pfa.pfaapp;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pfa.pfaapp.adapters.PFAGridAdapter;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.appcompat.app.AlertDialog;

import static com.pfa.pfaapp.utils.AppConst.SP_APP_LANG;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;
import static com.pfa.pfaapp.utils.AppConst.SP_MAIN_MENU;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;
import static com.pfa.pfaapp.utils.AppConst.SP_USER_INFO;
import static com.pfa.pfaapp.utils.AppUtils.APP_LANGUAGE;

public class FBOMainGridActivity extends BaseActivity implements HttpResponseCallback {

    GridView fboMainGV;
    List<PFAMenuInfo> pfaMenuInfos;
    PFAGridAdapter pfaGridAdapter;

    ImageButton logoutImgBtn;

    RadioGroup langRG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbomain_grid);

        checkPermission();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            checkManageStorage();
//        }
        Log.d("onCreateActv" , "FBOMAINGRIDActivity");
        initViews();
        setTitle("", false);
        (findViewById(R.id.ttl_bar)).setBackgroundColor(getResources().getColor(R.color.transparent));

        if (sharedPrefUtils.getMainMenu() == null) {
            getMainMenu();
        } else {
            populateGridData();
        }


    }


    private void initViews() {

        sharedPrefUtils.applyFont(findViewById(R.id.englisRB),AppUtils.FONTS.HelveticaNeue);
        sharedPrefUtils.applyFont(findViewById(R.id.urduRB),AppUtils.FONTS.HelveticaNeue);

        langRG = findViewById(R.id.langRG);
        fboMainGV = findViewById(R.id.fboMainGV);
        logoutImgBtn = findViewById(R.id.logoutImgBtn);
        logoutImgBtn.setVisibility(View.VISIBLE);


        if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") == null) {
            logoutImgBtn.setImageResource(sharedPrefUtils.isEnglishLang()?R.mipmap.login:R.mipmap.ur_login);
        }
        else
        {
            registerScreenReceiver();
            logoutImgBtn.setImageResource(sharedPrefUtils.isEnglishLang()?R.mipmap.logout:R.mipmap.ur_logout);
        }

        if (sharedPrefUtils.isEnglishLang()) {
            ((RadioButton) findViewById(R.id.englisRB)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.urduRB)).setChecked(true);
        }
        langRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.englisRB:
                        sharedPrefUtils.saveSharedPrefValue(SP_APP_LANG, String.valueOf(APP_LANGUAGE.en));
                        updateLocale();
                        sharedPrefUtils.restartActivitySelf(FBOMainGridActivity.this);
                        break;
                    case R.id.urduRB:
                        sharedPrefUtils.saveSharedPrefValue(SP_APP_LANG, String.valueOf(APP_LANGUAGE.ur));
                        updateLocale();
                        sharedPrefUtils.restartActivitySelf(FBOMainGridActivity.this);
                        break;
                }
            }
        });
    }

    private void getMainMenu() {
        String userId = "";
        if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {
            userId = sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID, "");
            userId = "/" + userId + "/" + sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, "");

            if (sharedPrefUtils.getSharedPrefValue(SP_USER_INFO, "") == null) {
                fetchUserInfo(null, false);
                updateConfigData();
            }
        }

        httpService.getMainMenu(this, userId);

    }



    @Override
    public void onBackPressed() {
        sharedPrefUtils.showExitDialog();
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

        if (response != null) {
            if (response.optBoolean("status")) {

                try {
                    JSONObject jsonObject = response.getJSONObject("data");

                    JSONArray formJSONArray = jsonObject.getJSONArray("menus");
                    sharedPrefUtils.saveSharedPrefValue(SP_MAIN_MENU, formJSONArray.toString());

                    populateGridData();

                } catch (JSONException e) {
                    sharedPrefUtils.printStackTrace(e);
                }
            }
        }
    }

    private void populateGridData() {
        pfaMenuInfos = sharedPrefUtils.getMainMenu();
        if (pfaMenuInfos != null && pfaMenuInfos.size() > 0) {
            pfaGridAdapter = new PFAGridAdapter(FBOMainGridActivity.this, pfaMenuInfos);
            fboMainGV.setAdapter(pfaGridAdapter);
        }

        logoutImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(FBOMainGridActivity.this);
//                builder.setTitle("Log out");

                String[] options = {"Log Out","Log Out from All Devices"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {
                                    sharedPrefUtils.logoutFromApp(httpService);
                                } else {
                                    sharedPrefUtils.startNewActivity(LoginActivity.class, null, false);
                                }
                                break;
                            case 1:

                                if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {
                                    sharedPrefUtils.logoutFromAllDevices(httpService);
                                } else {
                                    sharedPrefUtils.startNewActivity(LoginActivity.class, null, false);
                                }
                                break;
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
    }

}

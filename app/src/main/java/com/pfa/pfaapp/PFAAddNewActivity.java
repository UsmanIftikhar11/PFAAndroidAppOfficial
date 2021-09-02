package com.pfa.pfaapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pfa.pfaapp.fragments.MenuFormFragment;
import com.pfa.pfaapp.fragments.MenuMapFragment;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.utils.AppUtils;

import org.json.JSONObject;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;
import static com.pfa.pfaapp.utils.AppConst.SP_USER_INFO;

public class PFAAddNewActivity extends BaseActivity {

    FrameLayout addNewComplainFL;
    MenuFormFragment menuFormFragment;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pfaadd_new);
        setupWindowAnimations((ViewGroup) findViewById(R.id.viewGroup));

        MenuMapFragment.businessLatLng = null;
        addNewComplainFL = findViewById(R.id.addNewComplainFL);
        bundle = getIntent().getExtras();

        if (sharedPrefUtils.getSharedPrefValue(SP_USER_INFO, "") == null) {
            fetchUserInfo(new HttpResponseCallback() {
                @Override
                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                    showAddNewFragment();
                }
            }, false);
            updateConfigData();

            if (bundle.containsKey(EXTRA_JSON_STR_RESPONSE)) {
                showAddNewFragment();
            }
        } else {
            if (bundle.containsKey("extraFetchConfig"))
                updateConfigData();
            showAddNewFragment();
        }
    }

    private void showAddNewFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        menuFormFragment = MenuFormFragment.newInstance(bundle);
        fragmentTransaction.add(R.id.addNewComplainFL, menuFormFragment, null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("imagePath" , "onActivityResult = " + "PFAAddNewActivity");

        if (menuFormFragment != null)
            menuFormFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        String loginType = sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, "");
        if (loginType != null && loginType.equalsIgnoreCase(String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo))) {
            sharedPrefUtils.startHomeActivity(FBOMainGridActivity.class, null);
        } else {
            super.onBackPressed();
        }
    }
}

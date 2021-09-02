package com.pfa.pfaapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.LocalFormLL;
import com.pfa.pfaapp.customviews.LocalGridLL;
import com.pfa.pfaapp.customviews.LocalListLL;
import com.pfa.pfaapp.interfaces.DDSelectedCallback;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.RBClickCallback;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.utils.AddInspectionUtils;

import org.json.JSONObject;

import java.util.HashMap;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;

public class LocalTabbedFragment extends Fragment implements HttpResponseCallback, RBClickCallback, DDSelectedCallback {

    private BaseActivity baseActivity;
    private String urlToCall;

    public LocalTabbedFragment() {
        // Required empty public constructor
    }

    public static LocalTabbedFragment newInstance(PFAMenuInfo pfaMenuInfo, boolean isDrawer) {
        LocalTabbedFragment fragment = new LocalTabbedFragment();
        Bundle args = new Bundle();
        if (pfaMenuInfo != null && pfaMenuInfo.getAPI_URL() != null) {
            args.putString(EXTRA_URL_TO_CALL, pfaMenuInfo.getAPI_URL());
        }
        args.putBoolean("isDrawer", isDrawer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_tabbed, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        baseActivity = (BaseActivity) getActivity();
        assert baseActivity != null;
        baseActivity.addInspectionUtils = new AddInspectionUtils(baseActivity, this, this, getView());

        if (getArguments() != null) {
            urlToCall = getArguments().getString(EXTRA_URL_TO_CALL);
            baseActivity.addInspectionUtils.downloadUrl = urlToCall;

//            boolean isDrawer = getArguments().getBoolean("isDrawer");
            refreshData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("imagePath" , "onActivityResult = " + "localTabbedFragment");

        if (baseActivity.addInspectionUtils.lastClicked >= 0) {
            if (baseActivity.addInspectionUtils.localFormsLL.getChildAt(baseActivity.addInspectionUtils.lastClicked) instanceof LocalFormLL)
                ((LocalFormLL) baseActivity.addInspectionUtils.localFormsLL.getChildAt(baseActivity.addInspectionUtils.lastClicked)).onActivityResult(requestCode, resultCode, data);
            if (baseActivity.addInspectionUtils.localFormsLL.getChildAt(baseActivity.addInspectionUtils.lastClicked) instanceof LocalGridLL)
                ((LocalGridLL) baseActivity.addInspectionUtils.localFormsLL.getChildAt(baseActivity.addInspectionUtils.lastClicked)).onActivityResult(requestCode, resultCode, data);
            if (baseActivity.addInspectionUtils.localFormsLL.getChildAt(baseActivity.addInspectionUtils.lastClicked) instanceof LocalListLL)
                ((LocalListLL) baseActivity.addInspectionUtils.localFormsLL.getChildAt(baseActivity.addInspectionUtils.lastClicked)).onActivityResult(requestCode, resultCode, data);
        }
    }

    private void refreshData() {
        if (urlToCall != null) {
            baseActivity.httpService.getListsData(urlToCall, new HashMap<String, String>(), LocalTabbedFragment.this, true);
        }
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        baseActivity.addInspectionUtils.onCompleteHttpResponse(response);
    }

    @Override
    public void onClickRB(View targetView) {
        baseActivity.addInspectionUtils.onClickRB(targetView);
    }

    @Override
    public void onDDDataSelected(FormDataInfo formDataInfo) {
        baseActivity.addInspectionUtils.onDDDataSelected(formDataInfo);
    }
}

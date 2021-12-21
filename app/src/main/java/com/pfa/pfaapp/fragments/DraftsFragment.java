package com.pfa.pfaapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.LocalFormsActivity;
import com.pfa.pfaapp.LoginActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.adapters.InspectionAdapter;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.localdbmodels.InspectionInfo;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.pfa.pfaapp.dbutils.DBQueriesUtil.TABLE_LOCAL_INSPECTIONS;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_SECURITY_CODE;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link DraftsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DraftsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private BaseActivity baseActivity;
    //    Commented because it is not used at the moment but we can uncomment when required pfaMenuInfo
    private PFAMenuInfo pfaMenuInfo;
    private ListView draftsLV;
    private LinearLayout sorry_iv;
    private static SendMessageCallback sendMessageCallback1;
    private List<InspectionInfo> inspectionInfos;
    private InspectionAdapter inspectionAdapter;

    private ImageButton addNewBtn;
    private SharedPrefUtils sharedPrefUtils;
    private HttpService httpService;

    public DraftsFragment() {
        // Required empty public constructor
    }


    public static DraftsFragment newInstance(PFAMenuInfo pfaMenuInfo, SendMessageCallback sendMessageCallback) {

        sendMessageCallback1 = sendMessageCallback;

        DraftsFragment fragment = new DraftsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, pfaMenuInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("onCreateActv" , "DraftsFragment");
        return inflater.inflate(R.layout.fragment_drafts, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            pfaMenuInfo = (PFAMenuInfo) getArguments().getSerializable(ARG_PARAM1);
        }
        baseActivity = (BaseActivity) getActivity();

        View rootView = getView();
        if (rootView != null) {
            draftsLV = rootView.findViewById(R.id.draftsLV);
            sorry_iv = rootView.findViewById(R.id.sorry_iv11);
            addNewBtn = rootView.findViewById(R.id.addNewBtn);

            if (pfaMenuInfo != null && pfaMenuInfo.getAPI_URL() != null && (!pfaMenuInfo.getAPI_URL().isEmpty())) {
                addNewBtn.setVisibility(View.VISIBLE);
                addNewBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(baseActivity.httpService.isNetworkDisconnected())
                            return;

                        addNewBtn.setClickable(false);
                        addNewBtn.setEnabled(false);
                        final Bundle bundle = new Bundle();
                        bundle.putString(EXTRA_URL_TO_CALL, pfaMenuInfo.getAPI_URL());

                        baseActivity.httpService.getListsData(pfaMenuInfo.getAPI_URL(), new HashMap<String, String>(), new HttpResponseCallback() {
                            @Override
                            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                if (response != null)
                                    bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                                baseActivity.sharedPrefUtils.startNewActivity(LocalFormsActivity.class, bundle, false);

                                addNewBtn.setEnabled(true);
                                addNewBtn.setClickable(true);
                            }
                        }, true);
                    }
                });
            }
        }
        populateData();
//
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        getConfirmation();
//                    }
//                });
//            }
//        }, 1500);
    }


//    private void getConfirmation() {
//        String pincode = "";
//        String userId = "";
//        if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN) != null) {
//            userId = sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID);
//        }
//        pincode = sharedPrefUtils.getSharedPrefValue(SP_SECURITY_CODE);
//        httpService.getUserConfirmation(userId, pincode, new HttpResponseCallback() {
//            @Override
//            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
//                if (response!= null)
//                {
//                    try {
//                        String status = response.getString("status");
//                        if (status == "false"){
//                            if (sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN) != null) {
//                                sharedPrefUtils.logoutFromApp(httpService);
//                                Toast.makeText(requireContext(), "Unauthentic User", Toast.LENGTH_SHORT).show();
//
//                            } else {
//                                sharedPrefUtils.startNewActivity(LoginActivity.class, null, false);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }

    @Override
    public void onResume() {
        super.onResume();
        populateData();
    }

    public void populateData() {

        if (baseActivity == null)
            return;

        final List<List<PFATableInfo>> data = new ArrayList<>();

        JSONArray inspectionJSONArray = baseActivity.dbQueriesUtil.selectAllFromTable(TABLE_LOCAL_INSPECTIONS);

        if (inspectionInfos != null && inspectionInfos.size() > 0)
            inspectionInfos.clear();
        if (inspectionJSONArray != null && inspectionJSONArray.length() > 0) {
            Type formSectionInfosType = new TypeToken<List<InspectionInfo>>() {
            }.getType();
            inspectionInfos = new GsonBuilder().create().fromJson(inspectionJSONArray.toString(), formSectionInfosType);

//             Create List<List<PFATableInfo>> data by getting the draft_inspection String and convert it to
//            List<PFATableInfo> to make the data for draft..

            if (inspectionInfos != null && inspectionInfos.size() > 0) {

                Type draftType = new TypeToken<List<PFATableInfo>>() {
                }.getType();

                for (InspectionInfo inspectionInfo : inspectionInfos) {
                    if (inspectionInfo != null && inspectionInfo.getDraft_inspection() != null) {
                        List<PFATableInfo> pfaTableInfos = new GsonBuilder().create().fromJson(inspectionInfo.getDraft_inspection(), draftType);
                        data.add(pfaTableInfos);
                    }
                }
            }

            inspectionAdapter = new InspectionAdapter(baseActivity, inspectionInfos, data, new SendMessageCallback() {
                @Override
                public void sendMsg(final String position) {
                    baseActivity.httpService.deleteDraftInspection(inspectionInfos.get(Integer.parseInt(position)).getInspectionID(), new HttpResponseCallback() {
                        @Override
                        public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                            if (response != null && response.optBoolean("status")) {
                                int i = baseActivity.dbQueriesUtil.deleteTableRow(TABLE_LOCAL_INSPECTIONS, "inspectionID", inspectionInfos.get(Integer.parseInt(position)).getInspectionID());

                                if (i > 0) {
                                    inspectionInfos.remove(Integer.parseInt(position));
                                    data.remove(Integer.parseInt(position));
                                    inspectionAdapter.notifyDataSetChanged();

                                    populateData();
                                }

                            } else {
                                if (response != null)
                                    baseActivity.sharedPrefUtils.showMsgDialog("" + (response.optString("message_code")), null);
                                else
                                    baseActivity.sharedPrefUtils.showMsgDialog("response => " + null, null);

                            }
                        }
                    }, true);


                }
            });
            draftsLV.setAdapter(inspectionAdapter);


            if (sendMessageCallback1 != null) {
                sendMessageCallback1.sendMsg("Drafts\n( " + inspectionInfos.size() + " )");
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (inspectionInfos == null || inspectionInfos.size() == 0) {
                    if (AppConst.draftsRadioButton != null)
                        AppConst.draftsRadioButton.setText("Drafts\n( 0 )");
                    sorry_iv.setVisibility(View.VISIBLE);
                } else {
                    if (AppConst.draftsRadioButton != null)
                        AppConst.draftsRadioButton.setText(String.format(Locale.getDefault(), "Drafts\n( %d )", inspectionInfos.size()));
                    sorry_iv.setVisibility(View.GONE);
                }
            }
        }, 1000);
    }

}

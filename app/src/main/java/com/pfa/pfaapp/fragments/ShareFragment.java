package com.pfa.pfaapp.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.LoginActivity;
import com.pfa.pfaapp.PFADrawerActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.DateCustomDialog;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.GetDateCallback;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.utils.CustomDateUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_SECURITY_CODE;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShareFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShareFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";

    private PFAMenuInfo pfaMenuInfo;

    private TextView startDateET;
    private TextView endDateET;
    private Button yesbtn;

    private BaseActivity baseActivity;
    private HttpService httpService;
    private SharedPrefUtils sharedPrefUtils;

    public ShareFragment() {
        // Required empty public constructor
    }

    public static ShareFragment newInstance(PFAMenuInfo pfaMenuInfo) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, pfaMenuInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pfaMenuInfo = (PFAMenuInfo) getArguments().getSerializable(ARG_PARAM1);
        }
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_share, container, false);

//        TextView dialogtitle = rootView.findViewById(R.id.dialogtitle);
//        TextView startDateLblTV = rootView.findViewById(R.id.startDateLblTV);
        startDateET = rootView.findViewById(R.id.startDateET);
//        TextView endDateLblTV = rootView.findViewById(R.id.endDateLblTV);
        endDateET = rootView.findViewById(R.id.endDateET);
        yesbtn = rootView.findViewById(R.id.yesbtn);

        Log.d("onCreateActv" , "ShareFragment");

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        baseActivity = (BaseActivity) getActivity();

        yesbtn.setOnClickListener(this);
        startDateET.setOnClickListener(this);
        endDateET.setOnClickListener(this);


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
//    }`
    @Override
    public void onClick(View v) {

        if (v == startDateET || v == endDateET) {
            final TextView pfaDateET = (TextView) v;
            DateCustomDialog.showDatePickerDialog(baseActivity, new GetDateCallback() {
                @Override
                public void onDateSelected(int day, int month, int year) {
                    if (day == -1 || month == -1 || year == -1) {
                        pfaDateET.setText("");
                    } else
                        pfaDateET.setText((new CustomDateUtils().getDateString(day, month, year)));

                }
            }, "all", null, null, pfaDateET.getText().toString());
        } else if (v == yesbtn) {
            if (startDateET.getText().toString().isEmpty() || endDateET.getText().toString().isEmpty()) {
                baseActivity.sharedPrefUtils.showMsgDialog("Please select Start and End Date", null);
                return;
            }

            HashMap<String, String> reqParams = new HashMap<>();
            reqParams.put("start_date", startDateET.getText().toString());
            reqParams.put("end_date", endDateET.getText().toString());

            ////

            baseActivity.httpService.getListsData(pfaMenuInfo.getAPI_URL(), reqParams, new HttpResponseCallback() {
                @Override
                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

                    if (response != null){
                        if (response.optBoolean("status")) {
                            baseActivity.sharedPrefUtils.shareOnWhatsApp(response.optString("shareHtmlStr"));

                        } else {
                            baseActivity.sharedPrefUtils.showMsgDialog(response.optString("message_code"), null);
                        }
                    }
                }
            }, true);

            //////

        }

    }
}

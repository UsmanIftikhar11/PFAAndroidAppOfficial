package com.pfa.pfaapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.adapters.DropdownAdapter;
import com.pfa.pfaapp.customviews.PFAEditText;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.models.PFASearchInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_FILTERS_DATA;
import static com.pfa.pfaapp.utils.AppConst.PFA_SEARCH_TAG;

public class PFASearchActivity extends BaseActivity {

    PFAEditText searchPFAET;
    ListView pfaSearchLV;

    FormFieldInfo formFieldInfo;
    DropdownAdapter dropdownAdapter;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pfasearch);

        bundle = getIntent().getExtras();

        assert bundle != null;
        formFieldInfo = (FormFieldInfo) bundle.getSerializable(EXTRA_FILTERS_DATA);
        assert formFieldInfo != null;
        setTitle(formFieldInfo.getValue(), true);
        initViews();

        Log.d("onCreateActv" , "PFASearchActivity");
    }

    private void initViews() {
        searchPFAET = findViewById(R.id.searchPFAET);
        pfaSearchLV = findViewById(R.id.pfaSearchLV);

        setTextChangers();
    }

    private void setActivityResult(PFASearchInfo pfaSearchInfo) {
        Intent intent = new Intent();
        if (bundle == null)
            bundle = new Bundle();
        bundle.putSerializable(PFA_SEARCH_TAG, pfaSearchInfo);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setSearchData(final List<PFASearchInfo> pfaSearchInfos) {
        List<String> listItemNames = new ArrayList<>();
        if (pfaSearchInfos != null && pfaSearchInfos.size() > 0) {
            for (PFASearchInfo pfaSearchInfo : pfaSearchInfos) {

                listItemNames.add(pfaSearchInfo.getFull_name() + ((pfaSearchInfo.getCnic_number() == null || pfaSearchInfo.getCnic_number().isEmpty()) ? "" : " / " + pfaSearchInfo.getCnic_number()));
            }

            dropdownAdapter = new DropdownAdapter(this, listItemNames);
            setAdapters(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    setActivityResult(pfaSearchInfos.get(position));
                }
            });

        }
    }

    private void setAdapters(AdapterView.OnItemClickListener clickListener) {
        pfaSearchLV.setAdapter(dropdownAdapter);
        pfaSearchLV.setSelection(0);
        pfaSearchLV.setOnItemClickListener(clickListener);
    }

    private void removeWatcher() {
        handler.removeCallbacks(searchTimeRunnable);
        searchPFAET.removeTextChangedListener(watcher);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setTextChangers();
            }
        }, 200);
    }

    private void setTextChangers() {
        searchPFAET.setOnKeyListener(keyListener);
        searchPFAET.addTextChangedListener(watcher);
    }

    View.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            try {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    removeWatcher();
                } else {
                    searchPFAET.addTextChangedListener(watcher);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    };

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            handler.removeCallbacks(searchTimeRunnable);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (searchPFAET.getText().toString().length() >= 3) {
                handler.postDelayed(searchTimeRunnable, 200);
            } else {
                handler.removeCallbacks(searchTimeRunnable);
            }
        }
    };
    ////////// Auto Search API Management

    Handler handler = new Handler();
    Runnable searchTimeRunnable = new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> reqParams = new HashMap<>();
            String searchText = searchPFAET.getText().toString();
            if (searchText.contains("/")) {
                int firstIndex = searchText.indexOf("/");
                searchText = searchText.substring(0, firstIndex);
            }
            reqParams.put("keyword", searchText.trim());
            (new HttpService(PFASearchActivity.this)).getListsData("" + formFieldInfo.getAPI_URL(), reqParams, new HttpResponseCallback() {
                @Override
                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                    ////////////
                    if (response != null)
                        if (response.optBoolean("status")) {
                            try {
                                Type type = new TypeToken<List<PFASearchInfo>>() {
                                }.getType();

                                JSONArray data = response.getJSONArray("data");
                                List<PFASearchInfo> testTableData = new GsonBuilder().create().fromJson(data.toString(), type);
                                setSearchData(testTableData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                }
            }, false);
        }
    };

}

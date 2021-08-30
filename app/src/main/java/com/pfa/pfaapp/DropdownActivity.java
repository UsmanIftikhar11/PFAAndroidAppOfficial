package com.pfa.pfaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.pfa.pfaapp.adapters.SearchDDAdapter;
import com.pfa.pfaapp.customviews.PFAEditText;
import com.pfa.pfaapp.interfaces.PFATextWatcher;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_DROPDOWN_NAME;
import static com.pfa.pfaapp.utils.AppConst.SEARCH_DATA;
import static com.pfa.pfaapp.utils.AppConst.SELECTED_POSITION;

public class DropdownActivity extends BaseActivity {

    Bundle bundle;
    SearchDDAdapter searchDDAdapter;
    List<String> data = new ArrayList<>();
    private int selectedPos = 0;
    PFAEditText searchDropdownPFAET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropdown);

        searchDropdownPFAET = findViewById(R.id.searchDropdownPFAET);
        sharedPrefUtils.applyFont(searchDropdownPFAET, AppUtils.FONTS.HelveticaNeue);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            setTitle(bundle.getString(EXTRA_DROPDOWN_NAME), true);
            data = bundle.getStringArrayList(SEARCH_DATA);

            if (data != null && data.size() > 10)
                searchDropdownPFAET.setVisibility(View.VISIBLE);

        }
        searchDDAdapter = new SearchDDAdapter(this, data, new SendMessageCallback() {
            @Override
            public void sendMsg(String position) {
                selectedPos = Integer.parseInt(position);
                setActivityResult();
            }
        });
        ListView searchLV = findViewById(R.id.searchLV);
        searchLV.setAdapter(searchDDAdapter);

        searchDropdownPFAET.addTextChangedListener(new PFATextWatcher(new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {
                if (searchDDAdapter != null) {
                    searchDDAdapter.getFilter().filter(message);
                }
            }
        }));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onClickBackImgBtn(View view) {
        onBackPressed();
    }

    private void setActivityResult() {
        Intent intent = new Intent();
        if (bundle == null)
            bundle = new Bundle();
        bundle.putInt(SELECTED_POSITION, selectedPos);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}

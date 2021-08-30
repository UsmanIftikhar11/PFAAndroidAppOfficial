package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.adapters.DropdownAdapter;
import com.pfa.pfaapp.interfaces.PFAViewsCallbacks;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("ViewConstructor")
public class PFADDSpinner extends androidx.appcompat.widget.AppCompatSpinner {
    private FormFieldInfo formFieldInfo;
    private Context context;

    List<FormDataInfo> formDataInfos;
    List<String> dropdownArray = new ArrayList<>();
    DropdownAdapter businessCatACTAdapter;
    private List<FormDataInfo> selectedValues = new ArrayList<>();
    HashMap<String, List<FormDataInfo>> formViewsData;
    PFAViewsCallbacks pfaViewsCallbacks;
    private boolean doNotRefresh = true;

    AppUtils appUtils;

    // You could also just apply your default style if none is given
    public PFADDSpinner(Context context, FormFieldInfo formFieldInfo) {
        super(context, null, R.style.spinnerTheme);
        this.formFieldInfo = formFieldInfo;
        this.context = context;
        appUtils = new AppUtils(context);
        setProperties();
        setFocusable(false);
    }

    /*in case of multi selection dropdown, it returns the List of all selected */
    public List<FormDataInfo> getSelectedValues() {
        return selectedValues;
    }

    public FormFieldInfo getFormFieldInfo() {
        return formFieldInfo;
    }

    @SuppressLint("RtlHardcoded")
    private void setProperties() {

        if (formFieldInfo.isRequired()) {
            setBackgroundDrawable(context.getResources().getDrawable(appUtils.isEnglishLang() ? R.mipmap.spinner_required : R.mipmap.ur_spinner_required));
        } else {
            setBackgroundDrawable(context.getResources().getDrawable(appUtils.isEnglishLang() ? R.mipmap.spinner_bg : R.mipmap.ur_spinner_bg));
        }
        setPadding((int) context.getResources().getDimension(R.dimen.spinner_left_padding), 0, (int) context.getResources().getDimension(R.dimen.form_right_padding), 0);

        setTag(formFieldInfo.getField_name());

        setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT | Gravity.START);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.form_text_field_height));
        params.setMargins(0, (int) context.getResources().getDimension(R.dimen.form_top_margin), 0, 0);
        setLayoutParams(params);

        populateData();

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (formFieldInfo.isInvisible()) {
            setVisibility(GONE);
        }
        if (formFieldInfo.isNotEditable()) {
            setFocusable(false);
            setClickable(false);
            setEnabled(false);
        }
    }

    //
    private void populateData() {
        doNotRefresh = true;
        if (formFieldInfo == null || formFieldInfo.getData().size() != 1) {
            assert formFieldInfo != null;
            formDataInfos = formFieldInfo.getData();
            for (int i = 0; i < formDataInfos.size(); i++) {
                dropdownArray.add(formDataInfos.get(i).getValue());
            }

            businessCatACTAdapter = new DropdownAdapter(context, dropdownArray);
            this.setAdapter(businessCatACTAdapter);

        }
        for (int i = 0; i < dropdownArray.size(); i++) {
            if (formDataInfos.get(i).getKey().equalsIgnoreCase(formFieldInfo.getDefault_value()) || dropdownArray.get(i).equalsIgnoreCase(formFieldInfo.getDefault_value())) {
                selectedValues.add(formFieldInfo.getData().get(i));
                setSelection(i);
            } else if (formViewsData != null && formViewsData.containsKey(getTag().toString())) {
                if (formViewsData.get(getTag().toString()).get(0).getValue().equalsIgnoreCase(dropdownArray.get(i))) {
                    selectedValues.add(formFieldInfo.getData().get(i));
                    setSelection(i);
                }
            }
        }

        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValues.clear();

                if (position != 0)
                    selectedValues.add(formDataInfos.get(position));

                if (doNotRefresh) {
                    doNotRefresh = false;
                } else {
                    if (pfaViewsCallbacks != null)
                        pfaViewsCallbacks.onDropdownItemSelected(formDataInfos.get(position),formDataInfos.get(position).getName());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}

package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.interfaces.RGSelectCallback;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import java.util.HashMap;
import java.util.List;

@SuppressLint("ViewConstructor")
public class PFARadioGroup extends RadioGroup {
    private FormFieldInfo formFieldInfo;
    private Context context;
    private HashMap<String, List<FormDataInfo>> formViewsData;
    RGSelectCallback rgSelectCallback;
    AppUtils appUtils;

    SharedPrefUtils sharedPrefUtils;

    public PFARadioGroup(Context context, FormFieldInfo formFieldInfo, HashMap<String, List<FormDataInfo>> formViewsData, RGSelectCallback rgSelectCallback) {
        super(context);
        appUtils = new AppUtils(context);
        this.formViewsData = formViewsData;
        this.context = context;
        this.rgSelectCallback = rgSelectCallback;
        this.formFieldInfo = formFieldInfo;

        setTag(formFieldInfo.getField_name());

        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (formFieldInfo.isHorizontal()) {
            setOrientation(HORIZONTAL);

        } else {
            setOrientation(VERTICAL);
        }

        setLayoutParams(layoutParams);
        setBackground(context.getResources().getDrawable(R.mipmap.text_bg));
        setPadding(appUtils.convertDpToPixel(10), appUtils.convertDpToPixel(20), appUtils.convertDpToPixel(10), appUtils.convertDpToPixel(20));
        addRadioButtons();

        if (formFieldInfo.isNotEditable()) {
            setClickable(false);
            setEnabled(false);
            setFocusable(false);
        }
    }

    private void addRadioButtons() {
        if (formFieldInfo.getData() != null && formFieldInfo.getData().size() > 0) {

            for (FormDataInfo formDataInfo : formFieldInfo.getData()) {
                PFARadio radioButton = new PFARadio(context);
                radioButton.setTag(formFieldInfo.getField_name());
                radioButton.setText(appUtils.isEnglishLang() ? formDataInfo.getValue() : formDataInfo.getValueUrdu());
                radioButton.setFormDataInfo(formDataInfo);
                radioButton.setButtonDrawable(context.getResources().getDrawable(R.drawable.lang_rb_selector));
                radioButton.setCompoundDrawablePadding(100);
                radioButton.setChecked(false);

                if (appUtils.isEnglishLang())
                    radioButton.setPadding(20, 0, 0, 0);
                else
                    radioButton.setPadding(0, 0, 20, 0);

                LayoutParams layoutParams;
                if (formFieldInfo.isHorizontal()) {
                    layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                } else {
                    layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                }

                if (formFieldInfo.isHorizontal() && this.getChildCount() > 0) {
                    layoutParams.leftMargin = appUtils.convertDpToPixel(25);
                } else if (!formFieldInfo.isHorizontal() && this.getChildCount() > 0) {
                    layoutParams.topMargin = appUtils.convertDpToPixel(10);
                }
                radioButton.setTextColor(context.getResources().getColor(R.color.black));
                radioButton.setLayoutParams(layoutParams);


                appUtils.applyFont(radioButton, AppUtils.FONTS.HelveticaNeue);
                appUtils.applyStyle(formFieldInfo.getFont_style(), formFieldInfo.getFont_size(), formFieldInfo.getFont_color(), radioButton);

                addView(radioButton);

                if (formViewsData != null && formViewsData.containsKey(radioButton.getTag().toString())) {
                    if (formViewsData.get(radioButton.getTag().toString()).contains(formDataInfo)) {
                        radioButton.setChecked(true);
                    }
                } else if (formFieldInfo.getDefault_value() != null &&(!formFieldInfo.getDefault_value().isEmpty()) && (formDataInfo.getKey().equalsIgnoreCase(formFieldInfo.getDefault_value()) || formDataInfo.getValue().equalsIgnoreCase(formFieldInfo.getDefault_value()) || formDataInfo.getValueUrdu().equalsIgnoreCase(formFieldInfo.getDefault_value()))) {
                    radioButton.setChecked(true);
                }

                if (formFieldInfo.isNotEditable()) {
                    radioButton.setClickable(false);
                    radioButton.setEnabled(false);
                    radioButton.setFocusable(false);
                }
            }
        }

        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rgSelectCallback != null)
                    rgSelectCallback.onCheckedChanged(group, checkedId);
            }
        });
    }

    public PFARadio getSelectedRB() {
        for (int i = 0; i < getChildCount(); i++) {
            PFARadio radioButton = (PFARadio) getChildAt(i);
//            if (formFieldInfo.getDefault_value().equalsIgnoreCase("Not Applicable")) {
                if (radioButton.isChecked()) {
                    // saving singlerequire value start
                    boolean singleRequired = formFieldInfo.isSingle_required();

                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("singleRequiredKey", singleRequired);
                    editor.apply();
                    //end
                    formFieldInfo.setDefault_value(radioButton.getFormDataInfo().getKey());
                    return radioButton;
                }
            }
        return null;
    }

    public FormFieldInfo getFormFieldInfo() {
        return formFieldInfo;
    }
}

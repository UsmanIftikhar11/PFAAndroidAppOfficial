package com.pfa.pfaapp.customviews;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class PFACheckboxGroup {
    private FormFieldInfo formFieldInfo;
    private Context context;
    private HashMap<String, List<FormDataInfo>> formViewsData;

    PFACheckboxGroup(Context context, FormFieldInfo formFieldInfo, HashMap<String, List<FormDataInfo>> formViewsData) {
        this.formViewsData = formViewsData;
        this.context = context;
        this.formFieldInfo = formFieldInfo;
    }

    LinearLayout getCheckboxLL() {

        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setTag(formFieldInfo.getField_name());
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setBackgroundResource(R.drawable.grey_box);

        linearLayout.setPadding(50, 50, 50, 50);

        if (formFieldInfo.isHorizontal())
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        else
            linearLayout.setOrientation(LinearLayout.VERTICAL);

        if (this.formFieldInfo != null && this.formFieldInfo.getData() != null && this.formFieldInfo.getData().size() > 0) {
            for (FormDataInfo formDataInfo : formFieldInfo.getData()) {

                PFACheckbox checkBox = new PFACheckbox(context);
                checkBox.setTag(formFieldInfo.getField_name());
                checkBox.setFormDataInfo(formDataInfo);

                LinearLayout.LayoutParams layoutParams;


                if (formFieldInfo.isHorizontal()) {

                    layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    checkBox.setButtonDrawable(context.getResources().getDrawable(R.drawable.lang_rb_selector));
                    if (linearLayout.getChildCount() > 0)
                        layoutParams.leftMargin = 20;
                } else {
                    layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    checkBox.setButtonDrawable(null);
                    if (sharedPrefUtils.isEnglishLang()) {
                        checkBox.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.lang_rb_selector), null);
                    } else {
                        checkBox.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.lang_rb_selector), null, null, null);
                    }
                    checkBox.setCompoundDrawablePadding(10);

                    if (linearLayout.getChildCount() > 0)
                        layoutParams.topMargin = 10;
                }
                checkBox.setLayoutParams(layoutParams);
                checkBox.setText(sharedPrefUtils.isEnglishLang() ? formDataInfo.getValue() : formDataInfo.getValueUrdu());
                checkBox.setPadding(10, 0, 0, 0);
                checkBox.setTextColor(context.getResources().getColor(R.color.black));

                if (formViewsData != null && formViewsData.containsKey(checkBox.getTag().toString())) {
                    if (formViewsData.get(checkBox.getTag().toString()).contains(formDataInfo)) {
                        checkBox.setChecked(true);
                    }
                }

                sharedPrefUtils.applyFont(checkBox, AppUtils.FONTS.HelveticaNeue);
                sharedPrefUtils.applyStyle(formFieldInfo.getFont_style(), formFieldInfo.getFont_size(), formFieldInfo.getFont_color(), checkBox);
                linearLayout.addView(checkBox);
            }
        }
        linearLayout.setLayoutParams(llParams);
        return linearLayout;
    }

    public List<PFACheckbox> getCheckedBoxes(LinearLayout linearLayout) {
        List<PFACheckbox> checkedBoxes = new ArrayList<>();

        if (linearLayout != null && linearLayout.getChildCount() > 0) {
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                if (((CheckBox) linearLayout.getChildAt(i)).isChecked()) {
                    checkedBoxes.add((PFACheckbox) linearLayout.getChildAt(i));
                }
            }
        }
        return checkedBoxes;
    }

}

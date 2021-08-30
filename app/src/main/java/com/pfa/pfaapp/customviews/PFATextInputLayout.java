package com.pfa.pfaapp.customviews;

import android.content.Context;
import android.util.AttributeSet;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.custominputlayout.CustomTextInputLayout;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.utils.SharedPrefUtils;

public class PFATextInputLayout extends CustomTextInputLayout {
    SharedPrefUtils sharedPrefUtils;
    public PFATextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PFATextInputLayout(Context mContext, FormFieldInfo fieldInfo) {
        super(mContext);

        setProperties(fieldInfo);
    }

    public void setProperties(FormFieldInfo fieldInfo) {
        if(fieldInfo!=null) {
            sharedPrefUtils= new SharedPrefUtils(getContext());
            setHint(sharedPrefUtils.isEnglishLang()?fieldInfo.getValue():fieldInfo.getValueUrdu());
        }

        setHintTextAppearance(R.style.textInputHintStyle);
        setHintEnabled(true);
        setHintAnimationEnabled(true);
        setErrorEnabled(true);
        setFocusable(true);
    }
}

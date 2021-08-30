package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pfa.pfaapp.PFASearchActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.models.PFASearchInfo;
import com.pfa.pfaapp.utils.AppUtils;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_ACTV_TAG;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_FILTERS_DATA;
import static com.pfa.pfaapp.utils.AppConst.RC_DROPDOWN;

public class PFASearchACTV extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    private PFASearchInfo pfaSearchInfo;
    private FormFieldInfo formFieldInfo;
    AppUtils appUtils;

    @SuppressLint("RtlHardcoded")
    public PFASearchACTV(Context context, AttributeSet attrs) {
        super(context, attrs);
        setThreshold(3);//will start working from first character

        appUtils = new AppUtils(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.form_text_field_height));
        setGravity(Gravity.TOP | Gravity.LEFT | Gravity.START);
        params.setMargins(0, (int) context.getResources().getDimension(R.dimen.form_left_padding), 0, 0);
        setLayoutParams(params);
    }

    public PFASearchACTV(Context context) {
        super(context, null);
    }


    public void setTextWatcher(FormFieldInfo fieldInfo) {
        this.formFieldInfo = fieldInfo;
        setTag(formFieldInfo.getField_name());
        setHint(formFieldInfo.getValue());
        setFocusable(false);

        setClicListner();


    }

    public PFASearchInfo getPfaSearchInfo() {
        return pfaSearchInfo;
    }

    public void setPfaSearchInfo(PFASearchInfo pfaSearchInfo) {
        this.pfaSearchInfo = pfaSearchInfo;
    }

    private boolean isClicked = false;

    private void setClicListner() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked) {
                    return;
                } else {
                    isClicked = true;
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isClicked = false;

                    }
                }, 300);
                startDropDownActivity();
            }
        });
    }

    public void startDropDownActivity() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FILTERS_DATA, formFieldInfo);
        bundle.putString(EXTRA_ACTV_TAG,formFieldInfo.getField_name());

        appUtils.startActivityForResult((Activity) getContext(), PFASearchActivity.class, bundle, RC_DROPDOWN);
    }


}

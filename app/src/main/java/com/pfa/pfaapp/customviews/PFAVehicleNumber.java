package com.pfa.pfaapp.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.utils.AppUtils;

public class PFAVehicleNumber extends ConstraintLayout {

    public FormFieldInfo formFieldInfo;
    AppUtils appUtils;
    public PFATextInputLayout textInputLayout;


    public PFAVehicleNumber(@NonNull Context context) {
        super(context);
    }

    public PFAVehicleNumber(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        appUtils = new AppUtils(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) context.getResources().getDimension(R.dimen.form_left_padding), 0, 0);
        setLayoutParams(params);
    }

    public PFATextInputLayout getTextInputLayout() {
        return textInputLayout;
    }

    public void setTextInputLayout(PFATextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }
}

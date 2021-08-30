package com.pfa.pfaapp.customviews;

import android.content.Context;

import com.pfa.pfaapp.models.FormDataInfo;

public class PFACheckbox extends androidx.appcompat.widget.AppCompatCheckBox {
    private FormDataInfo formDataInfo;
    public PFACheckbox(Context context) {
        super(context);
    }

    public FormDataInfo getFormDataInfo() {
        return formDataInfo;
    }

    public void setFormDataInfo(FormDataInfo formDataInfo) {
        this.formDataInfo = formDataInfo;
    }
}

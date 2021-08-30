package com.pfa.pfaapp.customviews;

import android.content.Context;

import com.pfa.pfaapp.models.FormDataInfo;

public class PFARadio extends androidx.appcompat.widget.AppCompatRadioButton {
    private FormDataInfo formDataInfo;
    public PFARadio(Context context) {
        super(context);
    }

    public FormDataInfo getFormDataInfo() {
        return formDataInfo;
    }

    public void setFormDataInfo(FormDataInfo formDataInfo) {
        this.formDataInfo = formDataInfo;
    }
}

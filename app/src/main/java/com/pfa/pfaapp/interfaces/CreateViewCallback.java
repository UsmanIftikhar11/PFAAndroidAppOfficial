package com.pfa.pfaapp.interfaces;

import android.widget.LinearLayout;

import com.pfa.pfaapp.models.FormSectionInfo;

public interface CreateViewCallback {
    void createAddView(final FormSectionInfo formSectionInfo, final LinearLayout parentView);
}
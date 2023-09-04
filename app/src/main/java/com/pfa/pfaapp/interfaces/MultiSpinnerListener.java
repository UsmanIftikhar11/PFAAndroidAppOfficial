package com.pfa.pfaapp.interfaces;

import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormFieldInfo;

import java.util.List;

public interface MultiSpinnerListener {
    void onItemsSelected(boolean[] selected, FormFieldInfo formFieldInfo , List<FormDataInfo> selectedValues);
}
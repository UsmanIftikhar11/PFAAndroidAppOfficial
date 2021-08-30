package com.pfa.pfaapp.interfaces;

import com.pfa.pfaapp.models.FormFieldInfo;

public interface MultiSpinnerListener {
    void onItemsSelected(boolean[] selected, FormFieldInfo formFieldInfo);
}
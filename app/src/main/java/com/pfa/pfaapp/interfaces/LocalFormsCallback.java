package com.pfa.pfaapp.interfaces;

import com.pfa.pfaapp.models.PFAMenuInfo;

public interface LocalFormsCallback {
    void getPFAMenuInfo(PFAMenuInfo pfaMenuInfo, boolean isError);
}

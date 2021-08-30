package com.pfa.pfaapp.interfaces;

import android.view.View;

import com.pfa.pfaapp.customviews.CustomNetworkImageView;
import com.pfa.pfaapp.customviews.PFASectionTV;
import com.pfa.pfaapp.customviews.VerifyFBOLayout;
import com.pfa.pfaapp.models.FormDataInfo;

public interface PFAViewsCallbacks {
    void showImagePickerDialog(CustomNetworkImageView view);
    void onLabelViewClicked(PFASectionTV pfaSectionTV);
    void onButtonCLicked(View view);
    void onClickGetCodeBtn(View view, VerifyFBOLayout verifyFBOLayout);
    void onDropdownItemSelected(FormDataInfo formDataInfo, String dataName);

}

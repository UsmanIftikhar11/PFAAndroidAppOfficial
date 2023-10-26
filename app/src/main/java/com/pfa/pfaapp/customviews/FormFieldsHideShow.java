package com.pfa.pfaapp.customviews;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.models.ShowHiddenFalseFields;
import com.pfa.pfaapp.utils.AppUtils;

import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.constraintlayout.widget.ConstraintLayout;

public class FormFieldsHideShow {
    Context mContext;

    public FormFieldsHideShow(Context mContext) {
        this.mContext = mContext;
    }

    public void setFieldsRequired(List<String> required_false_fields, List<String> checkvalues, boolean isRequired, HashMap<String, HashMap<String, Boolean>> sectionRequired, LinearLayout menuFragParentLL) {
        if (sectionRequired != null && sectionRequired.size() > 0) {
            for (String sectionKey : sectionRequired.keySet())
                if (sectionRequired.get(sectionKey) != null && sectionRequired.get(sectionKey).size() > 0)
                    if (required_false_fields != null && required_false_fields.size() > 0)
                        for (String tag : required_false_fields) {
                            if (isRequired) {
                                if (menuFragParentLL.findViewWithTag(tag) instanceof PFAEditText) {

                                    Log.d("setFieldsRequired", "fields = " + tag);
                                    ((PFAEditText) menuFragParentLL.findViewWithTag(tag)).getFormFieldInfo().setRequired(true);

                                } else if (menuFragParentLL.findViewWithTag(tag) instanceof PFADDACTV) {
                                    ((PFADDACTV) menuFragParentLL.findViewWithTag(tag)).formFieldInfo.setRequired(true);
                                }
                                sectionRequired.get(sectionKey).put(tag, true);
                            } else {
                                if (menuFragParentLL.findViewWithTag(tag) instanceof PFAEditText) {
                                    ((PFAEditText) menuFragParentLL.findViewWithTag(tag)).getFormFieldInfo().setRequired(false);
                                } else if (menuFragParentLL.findViewWithTag(tag) instanceof PFADDACTV) {
                                    ((PFADDACTV) menuFragParentLL.findViewWithTag(tag)).formFieldInfo.setRequired(false);
                                }


                                sectionRequired.get(sectionKey).remove(tag);
                            }
                        }
        }
    }

    private void showHideReqFields(boolean isShow, List<String> required_false_fields, LinearLayout menuFragParentLL,
                                   HashMap<String, HashMap<String, Boolean>> sectionRequired, boolean isReq) {

        AppUtils appUtils = new AppUtils(mContext);

        Log.d("checkReqFieldss", "fields = " + required_false_fields);
        if (required_false_fields == null || required_false_fields.size() == 0)
            return;
        for (String sectionReqFieldKey : required_false_fields) {

            Log.d("vehicleNUmbercheck", "views = " + sectionReqFieldKey);

            if (menuFragParentLL.findViewWithTag(sectionReqFieldKey) instanceof PFAEditText) {
                PFAEditText reqPFAET = menuFragParentLL.findViewWithTag(sectionReqFieldKey);
//                boolean isRequired = mContext.getSharedPreferences("fieldsPrefs", Context.MODE_PRIVATE).getBoolean(reqPFAET.getFormFieldInfo().getField_name(), false);
//                if (isRequired) {
//                    reqPFAET.getFormFieldInfo().setRequired(false);
//                } else {
                    reqPFAET.getFormFieldInfo().setRequired(isShow);
//                }


                Log.d("vehicleNUmbercheck", "views EditText = " + sectionReqFieldKey);
                reqPFAET.getFormFieldInfo().setInvisible(!isShow);
                reqPFAET.textInputLayout.setVisibility(isShow ? VISIBLE : GONE);
                reqPFAET.addTextInputLayout(reqPFAET.textInputLayout);
                Log.d("parentLicCat1", "Rendering Units parent dd onChange 222 req = " + reqPFAET.getFormFieldInfo().isRequired());
                Log.d("parentLicCat1", "Rendering Units parent dd onChange 222 req = " + reqPFAET.getTag());

                if (!isShow) {
                    reqPFAET.setText("");
                }

            }
            /*else if (sectionReqFieldKey.equals("vehicle_number")) {
//            } else if (menuFragParentLL.findViewWithTag(sectionReqFieldKey) instanceof PFAVehicleNumber) {
                Log.d("vehicleNUmbercheck", "vehicleNUmbercheck = here");
                CustomViewCreate.setVehicleNumberReqInfo();
                ConstraintLayout pfaddLL = menuFragParentLL.findViewById(R.id.clVehicleNumber);
                boolean isRequire = mContext.getSharedPreferences("fieldsPrefs" , Context.MODE_PRIVATE).getBoolean("vehicle_number_req" , true);
                if (isShow) {
                    pfaddLL.setVisibility(GONE);
                    if (isRequire)
                        mContext.getSharedPreferences("fieldsPrefs" , Context.MODE_PRIVATE).edit().putBoolean("vehicle_number_req" , true).apply();
                    else
                        mContext.getSharedPreferences("fieldsPrefs" , Context.MODE_PRIVATE).edit().putBoolean("vehicle_number_req" , false).apply();
                }
                else {
                    pfaddLL.setVisibility(VISIBLE);
                    mContext.getSharedPreferences("fieldsPrefs" , Context.MODE_PRIVATE).edit().putBoolean("vehicle_number_req" , false).apply();
                }

            } */
            else if (menuFragParentLL.findViewWithTag(sectionReqFieldKey) instanceof PFASearchACTV) {
                FrameLayout autoSearchLayout = menuFragParentLL.findViewWithTag(sectionReqFieldKey + "_parent");

                final PFASearchACTV autoSearchPFAET = autoSearchLayout.findViewById(R.id.pfaSearchACTV);
                PFATextInputLayout textInputLayout = autoSearchLayout.findViewById(R.id.pfaSearchTIL);

                autoSearchLayout.setVisibility(isShow ? VISIBLE : GONE);
                autoSearchPFAET.formFieldInfo.setRequired(isShow);
                //////
                if (isShow) {

                    if (textInputLayout != null) {
                        textInputLayout.setBackground(mContext.getResources().getDrawable(appUtils.isEnglishLang() ? R.mipmap.text_bg_star : R.mipmap.ur_text_bg_star));
                        textInputLayout.setGravity(Gravity.CENTER_VERTICAL);
                    } else {
                        autoSearchLayout.setBackground(mContext.getResources().getDrawable(appUtils.isEnglishLang() ? R.mipmap.text_bg_star : R.mipmap.ur_text_bg_star));
                    }
                } else {
                    autoSearchPFAET.setText("");
                    if (textInputLayout != null) {
                        textInputLayout.setBackground(mContext.getResources().getDrawable(R.mipmap.text_bg));
                        textInputLayout.setGravity(Gravity.CENTER_VERTICAL);
                    } else {
                        autoSearchLayout.setBackground(mContext.getResources().getDrawable(R.mipmap.text_bg));
                    }
                }
                //////


            } else if (menuFragParentLL.findViewWithTag(sectionReqFieldKey) instanceof CustomNetworkImageView) {
                LinearLayout img_attachment_ll = menuFragParentLL.findViewWithTag(sectionReqFieldKey + "_parent");

                if (img_attachment_ll != null) {
                    final CustomNetworkImageView attachmentCNIV = img_attachment_ll.findViewById(R.id.attachmentCNIV);
                    TextView selectImgTV = img_attachment_ll.findViewById(R.id.selectImgTV);

                    selectImgTV.setText(isShow ? Html.fromHtml("<b><font color=\"#EB5757\">" + " *</font> </b> Select") : mContext.getString(R.string.select_image));

                    attachmentCNIV.getFormFieldInfo().setRequired(isShow);
                    img_attachment_ll.setVisibility(isShow ? VISIBLE : GONE);
                    attachmentCNIV.setVisibility(isShow ? VISIBLE : GONE);

                    if (!isShow) {
                        attachmentCNIV.setImageFile(null);
                        attachmentCNIV.setLocalImageBitmap(null);
                    }
                }
            } else if (menuFragParentLL.findViewWithTag(sectionReqFieldKey) instanceof PFADDACTV) {
                FrameLayout pfaddLL = menuFragParentLL.findViewWithTag(sectionReqFieldKey + "01");
//                PFADDACTV pfaddLL = menuFragParentLL.findViewWithTag(sectionReqFieldKey);

                Log.d("checkReqFieldss", "here = " + sectionReqFieldKey);
                Log.d("checkReqFieldss", "here1 = " + pfaddLL.getTag().toString());
                Log.d("checkReqFieldss", "show = " + isShow);
                if (pfaddLL != null) {
                    Log.d("checkReqFieldss", "here 2");
                    PFADDACTV pfa_dd_actv = pfaddLL.findViewById(R.id.pfa_dd_actv);

                    pfa_dd_actv.getFormFieldInfo().setRequired(isShow);
                    pfa_dd_actv.getFormFieldInfo().setInvisible(!isShow);
                    pfa_dd_actv.formFieldInfo.setRequired(isShow);
                    pfa_dd_actv.showHideDropDownVisibility(true);
                    pfa_dd_actv.setRequired(isShow);
                    pfaddLL.setVisibility(VISIBLE);
                    pfa_dd_actv.getTextInputLayout().setVisibility(isShow ? VISIBLE : GONE);
                    pfa_dd_actv.setTextInputLayout(pfa_dd_actv.getTextInputLayout());
                }
            }

            for (String sectionKey : sectionRequired.keySet()) {
                if (sectionRequired.get(sectionKey) != null && sectionRequired.get(sectionKey).size() > 0 && sectionRequired.get(sectionKey).containsKey(sectionReqFieldKey))
                    sectionRequired.get(sectionKey).put(sectionReqFieldKey, isShow);
            }

        }
    }

    private void showHideMultiReqFields(List<String> fieldsToShow, List<String> fieldsToHide, LinearLayout menuFragParentLL,
                                        HashMap<String, HashMap<String, Boolean>> sectionRequired, boolean isReq) {

        AppUtils appUtils = new AppUtils(mContext);

        Log.d("checkReqFieldss", "fields = " + fieldsToShow);
        Log.d("checkReqFieldss", "views = " + fieldsToHide);
        if (fieldsToShow == null || fieldsToShow.size() == 0 ) {
            return;
        } else {
            for (int i = 0; i < fieldsToShow.size(); i++) {
                if (menuFragParentLL.findViewWithTag(fieldsToShow.get(i)) instanceof PFAEditText) {
                    PFAEditText reqPFAET = menuFragParentLL.findViewWithTag(fieldsToShow.get(i));
                    boolean isRequired = mContext.getSharedPreferences("fieldsPrefs", Context.MODE_PRIVATE).getBoolean(reqPFAET.getFormFieldInfo().getField_name(), false);
                    if (isRequired) {
                        reqPFAET.getFormFieldInfo().setRequired(false);
                    } else {
                        reqPFAET.getFormFieldInfo().setRequired(true);
                    }

                    reqPFAET.getFormFieldInfo().setInvisible(false);
                    reqPFAET.textInputLayout.setVisibility(VISIBLE);
                    reqPFAET.addTextInputLayout(reqPFAET.textInputLayout);
                    Log.d("parentLicCat1", "Rendering Units parent dd onChange 222 req = " + reqPFAET.getFormFieldInfo().isRequired());
                    Log.d("parentLicCat1", "Rendering Units parent dd onChange 222 req = " + reqPFAET.getTag());

                    reqPFAET.setText("");

                }
                for (String sectionKey : sectionRequired.keySet()) {
                    if (sectionRequired.get(sectionKey) != null && sectionRequired.get(sectionKey).size() > 0 && sectionRequired.get(sectionKey).containsKey(fieldsToShow.get(i)))
                        sectionRequired.get(sectionKey).put(fieldsToShow.get(i), true);
                }
            }
        }

        if (fieldsToHide == null || fieldsToHide.size() == 0 ) {
            return;
        } else {
            for (int i = 0; i < fieldsToHide.size(); i++) {
                if (menuFragParentLL.findViewWithTag(fieldsToHide.get(i)) instanceof PFAEditText) {
                    PFAEditText reqPFAET = menuFragParentLL.findViewWithTag(fieldsToHide.get(i));
                    reqPFAET.getFormFieldInfo().setRequired(false);

                    reqPFAET.getFormFieldInfo().setInvisible(true);
                    reqPFAET.textInputLayout.setVisibility(GONE);
                    reqPFAET.addTextInputLayout(reqPFAET.textInputLayout);
                    Log.d("parentLicCat1", "Rendering Units parent dd onChange 222 req = " + reqPFAET.getFormFieldInfo().isRequired());
                    Log.d("parentLicCat1", "Rendering Units parent dd onChange 222 req = " + reqPFAET.getTag());

                    reqPFAET.setText("");

                }
                for (String sectionKey : sectionRequired.keySet()) {
                    if (sectionRequired.get(sectionKey) != null && sectionRequired.get(sectionKey).size() > 0 && sectionRequired.get(sectionKey).containsKey(fieldsToHide.get(i)))
                        sectionRequired.get(sectionKey).put(fieldsToHide.get(i), true);
                }
            }
        }

    }

    public void setFieldsRequiredAndVisible(List<ShowHiddenFalseFields> required_false_fields, boolean isReq, HashMap<String, HashMap<String, Boolean>> sectionRequired, LinearLayout menuFragParentLL, String checkValue) {

        List<String> checkViews = null;

        Log.d("parentLicCat", "Rendering Units parent dd onChange 222");
        for (ShowHiddenFalseFields showHiddenFalseField : required_false_fields) {
            if (showHiddenFalseField.getCheckKey().equals(checkValue)) {
                checkViews = showHiddenFalseField.getCheckViews();
            } else {
                Log.d("parentLicCat1", "Rendering Units parent dd onChange 222 111 = " + isReq);
                showHideReqFields(false, showHiddenFalseField.getCheckViews(), menuFragParentLL, sectionRequired, isReq);
            }
        }

        if (checkViews != null && checkViews.size() > 0) {
            Log.d("parentLicCat1", "Rendering Units parent dd onChange 222 222 = " + isReq);
            showHideReqFields(true, checkViews, menuFragParentLL, sectionRequired, isReq);
        }

    }

    void setMultiFieldsRequiredAndVisible(List<String> fieldsToShow, boolean isReq, HashMap<String, HashMap<String, Boolean>> sectionRequired, LinearLayout menuFragParentLL, List<String> fieldsToHide) {

        showHideMultiReqFields(fieldsToShow, fieldsToHide, menuFragParentLL, sectionRequired, isReq);


    }

}

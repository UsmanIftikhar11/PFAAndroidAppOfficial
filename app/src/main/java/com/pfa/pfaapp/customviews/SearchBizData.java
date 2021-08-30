package com.pfa.pfaapp.customviews;

import android.content.Context;
import android.os.Build;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.interfaces.BizLocCallback;
import com.pfa.pfaapp.interfaces.PFAViewsCallbacks;
import com.pfa.pfaapp.models.FormFieldInfo;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import static android.view.View.FOCUSABLE;
import static android.view.View.GONE;
import static android.view.View.NOT_FOCUSABLE;
import static android.view.View.VISIBLE;
import static com.pfa.pfaapp.utils.AppConst.SUB_TOWN_TAG;

class SearchBizData extends CustomAddLabelUtils {
    JSONObject SEARCH_BIZ_JSON_OBJ;

    PFALocationACTV regionACTV, divisionACTV, districtACTV, townACTV, subTownACTV;
    PFATextInputLayout regionACTIL, divisionACTIL, districtACTIL, townACTIL, sub_townACTIL;

    SearchBizData(Context mContext) {
        super(mContext);
    }

    void setSearchBizData(boolean searchFormData, View parentView, PFAViewsCallbacks pfaViewsCallbacks, BizLocCallback bizLocCallback) {
        if (SEARCH_BIZ_JSON_OBJ == null)
            return;

        Type strType = new TypeToken<List<String>>() {
        }.getType();
        List<String> hideShowList = new Gson().fromJson(SEARCH_BIZ_JSON_OBJ.optJSONArray("show_hide_fields").toString(), strType);

        for (Iterator<String> it = SEARCH_BIZ_JSON_OBJ.keys(); it.hasNext(); ) {
            String key = it.next();
            if (parentView.findViewWithTag(key) instanceof PFAEditText) {
                PFAEditText pfaEditText = parentView.findViewWithTag(key);
                if (searchFormData) {
                    if (!SEARCH_BIZ_JSON_OBJ.optString(key).isEmpty()) {
                        pfaEditText.setNotEditable(true);
                        pfaEditText.setText(SEARCH_BIZ_JSON_OBJ.optString(key));
                    }

                } else {
                    pfaEditText.setNotEditable(false);
                    pfaEditText.setText("");

                }
            } else if (parentView.findViewWithTag(key) instanceof PFALocationACTV) {
                bizLocCallback.setSearchBizLoc(key, searchFormData);
            } else if (parentView.findViewWithTag(key) instanceof PFADDACTV) {

                PFADDACTV pfaddactv = parentView.findViewWithTag(key);
                if (searchFormData) {
                    pfaddactv.setBizSelectedDDId(SEARCH_BIZ_JSON_OBJ.optString(key), pfaViewsCallbacks);

                } else {
                    pfaddactv.setText("");
                    pfaddactv.setSelectedValues(null);
                }
            }
        }

//        hide show business selected views start
        if (hideShowList != null && hideShowList.size() > 0) {
            for (String key : hideShowList) {
                View view = parentView.findViewWithTag(key);
                View hideShowView = parentView.findViewWithTag(key + "01");

                if (hideShowView != null) {
                    if (searchFormData)
                        hideShowView.setVisibility(GONE);
                    else
                        hideShowView.setVisibility(VISIBLE);

                } else if (view != null) {
                    if (searchFormData)
                        view.setVisibility(GONE);
                    else
                        view.setVisibility(VISIBLE);
                }
            }
        }
//        hide show business selected views end

        if (searchFormData) {
            showInspectionList(SEARCH_BIZ_JSON_OBJ);
        } else {
            SEARCH_BIZ_JSON_OBJ = null;
            if (hideShowList != null)
                hideShowList.clear();
        }
    }

    void setBizLocationEnabled(boolean isDisabled, PFALocationACTV pfaLocationACTV) {
        if (isDisabled) {
            pfaLocationACTV.setEnabled(false);
            pfaLocationACTV.setKeyListener(null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                pfaLocationACTV.setFocusable(NOT_FOCUSABLE);
            } else {
                pfaLocationACTV.setFocusable(false);
            }
        } else {
            pfaLocationACTV.setEnabled(true);
            pfaLocationACTV.setKeyListener(null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                pfaLocationACTV.setFocusable(FOCUSABLE);
            } else {
                pfaLocationACTV.setFocusable(true);
            }
            pfaLocationACTV.setFocusableInTouchMode(true);

            pfaLocationACTV.setText("");
            pfaLocationACTV.setSelectedID(-1);

        }
    }


    private void disableKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputManager != null;
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    void setSpinnerFonts(FormFieldInfo formFieldInfo, AppCompatAutoCompleteTextView autoCompleteTextView, PFATextInputLayout textInputLayout) {

        applyFont(autoCompleteTextView, FONTS.HelveticaNeueMedium);
        applyStyle(formFieldInfo.getFont_style(), formFieldInfo.getFont_size(), formFieldInfo.getFont_color(), autoCompleteTextView);
        disableKeyboard(autoCompleteTextView);

        autoCompleteTextView.setBackgroundResource(R.color.transparent);

        String autoCTVTag = autoCompleteTextView.getTag().toString();

        if (formFieldInfo.isRequired() && (!autoCTVTag.equals(SUB_TOWN_TAG))) {
            if (textInputLayout == null)
                autoCompleteTextView.setBackgroundDrawable(mContext.getResources().getDrawable(isEnglishLang()?R.mipmap.spinner_required:R.mipmap.ur_spinner_required));
            else {
                textInputLayout.setBackground(mContext.getResources().getDrawable(isEnglishLang()?R.mipmap.spinner_required:R.mipmap.ur_spinner_required));

            }
        } else {
            if (textInputLayout == null)
                autoCompleteTextView.setBackgroundDrawable(mContext.getResources().getDrawable(isEnglishLang()?R.mipmap.spinner_bg:R.mipmap.ur_spinner_bg));
            else {
                textInputLayout.setBackground(mContext.getResources().getDrawable(isEnglishLang()?R.mipmap.spinner_bg:R.mipmap.ur_spinner_bg));
            }
        }
    }


}

package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.pfa.pfaapp.DropdownActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.interfaces.DDSelectedCallback;
import com.pfa.pfaapp.interfaces.PFAViewsCallbacks;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.pfa.pfaapp.utils.AppConst.CANCEL;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_ACTV_TAG;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_DROPDOWN_NAME;
import static com.pfa.pfaapp.utils.AppConst.RC_DROPDOWN;
import static com.pfa.pfaapp.utils.AppConst.SEARCH_DATA;

public class PFADDACTV extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    private List<String> listItemNames = new ArrayList<>();
    FormDataInfo formDataInfo;

    AppUtils appUtils;
    private Context mContext;
    private List<FormDataInfo> selectedValues = new ArrayList<>();

    String dropdownName;
    WhichItemClicked whichItemClicked;
    FormDataInfo filterDataInfo;
    public FormFieldInfo formFieldInfo;
    HashMap<String, List<FormDataInfo>> formFilteredData;

    private FrameLayout pfaddLL;
    private PFATextInputLayout textInputLayout;
    private DDSelectedCallback DDCallback;
    String action;
    @SuppressLint("RtlHardcoded")
    public PFADDACTV(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        //
        setKeyListener(null);
        setFocusable(false);
        setFocusableInTouchMode(true);
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        appUtils = new AppUtils(context);
        setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT | Gravity.START);
    }

    public PFADDACTV(Context context) {
        super(context);
    }

    public void setProperties(WhichItemClicked whichItemClicked, final FormFieldInfo formFieldInfo, HashMap<String, List<FormDataInfo>> formFilteredData) {
        this.whichItemClicked = whichItemClicked;
        this.formFieldInfo = formFieldInfo;
        this.formFilteredData = formFilteredData;

        dropdownName = appUtils.isEnglishLang() ? ("Select " + formFieldInfo.getValue()) : (formFieldInfo.getValueUrdu() + " منتخب کریں ");
        setTag(formFieldInfo.getField_name());
        setHintTextColor(mContext.getResources().getColor(R.color.hint_color));

        if (formFieldInfo.isNotEditable()) {
            setEnabled(false);
            setClickable(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setFocusable(NOT_FOCUSABLE);
            } else {
                setFocusable(false);
            }
        }

        setKeyListener(null);
        setFocusable(false);
        setClicListner();
    }

    public String getHintValue() {
        if (formFieldInfo != null)
            return appUtils.isEnglishLang() ? formFieldInfo.getValue() : formFieldInfo.getValueUrdu();
        return "";
    }

    public void populateData() {

        if (formFilteredData != null && formFilteredData.get(getTag().toString()) != null && formFilteredData.get(getTag().toString()).size() > 0) {
            filterDataInfo = formFilteredData.get(getTag().toString()).get(0);
        }

        listItemNames.clear();
        if (formFieldInfo == null || formFieldInfo.getData().size() != 0) {
            assert formFieldInfo != null;
            List<FormDataInfo> formDataInfos = formFieldInfo.getData();
            for (int i = 0; i < formDataInfos.size(); i++) {
                listItemNames.add(appUtils.isEnglishLang() ? formDataInfos.get(i).getValue() : formDataInfos.get(i).getValueUrdu());

                if (formFieldInfo.getDefault_value() != null && (!formFieldInfo.getDefault_value().isEmpty()) && (formFieldInfo.getDefault_value().equalsIgnoreCase(formDataInfos.get(i).getValue()) || formFieldInfo.getDefault_value().equalsIgnoreCase(formDataInfos.get(i).getKey()))) {

                    filterDataInfo = formFieldInfo.getData().get(i);
                }
            }
        }

        setSelectedPosition(filterDataInfo, false);

        if (listItemNames != null && listItemNames.size() > 0) {
            setSelection(0);
        }
    }

    public void setBizSelectedDDId(String dataId, PFAViewsCallbacks pfaViewsCallbacks) {
        if (formFieldInfo == null || formFieldInfo.getData().size() != 0) {
            assert formFieldInfo != null;
            List<FormDataInfo> formDataInfos = formFieldInfo.getData();
            if (formDataInfos != null) {
                for (int i = 0; i < formDataInfos.size(); i++) {
                    if (formDataInfos.get(i).getKey().equalsIgnoreCase(dataId)) {
                        filterDataInfo = formFieldInfo.getData().get(i);
                        setDropdownSelection(i, pfaViewsCallbacks);
                        break;
                    }
                }
            }
        }
    }

    public void setDropdownSelection(int position, PFAViewsCallbacks pfaViewsCallbacks) {
        selectedValues = new ArrayList<>();

        if (position > -1 && position < formFieldInfo.getData().size()) {
//            FormDataInfo formDataInfo;
            formDataInfo = new FormDataInfo();
            formDataInfo = formFieldInfo.getData().get(position);
             action = formDataInfo.getValue();
             //*****************
//            SharedPreferences sharedPreferences = PreferenceManager.
//            getDefaultSharedPreferences(mContext);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("defaultVAL", action);
//            editor.apply();
            SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(mContext);
            sharedPrefUtils.setAction(action);
            //*********************
            formDataInfo.setSelected(true);
            setSelectedPosition(formDataInfo, true);

            if (pfaViewsCallbacks != null)
                pfaViewsCallbacks.onDropdownItemSelected(formDataInfo, formDataInfo.getName());
        } else {
            if (getText().toString().isEmpty()) {
                textInputLayout.setError(null);
                setHint(appUtils.isEnglishLang() ? formFieldInfo.getValue() : formFieldInfo.getValueUrdu());
                setText("");
                clearFocus();
            }
        }

        clearFocus();
    }

    private void setSelectedPosition(final FormDataInfo filterDataInfo, final boolean showConfirmMsg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setShowSoftInputOnFocus(false);
        } else {
            setFocusable(false);
        }

        if (showConfirmMsg && filterDataInfo != null && filterDataInfo.getAPI_URL() != null && (!filterDataInfo.getAPI_URL().isEmpty())) {

            if (filterDataInfo.getAPI_URL().contains("inspections/get_checklist")) {

//                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
//                        mContext);
//
//// Setting Dialog Title
//                alertDialog2.setTitle("Confirm...");
//
//// Setting Dialog Message
//                alertDialog2.setMessage("Would you like to update Checklist?");
//
//                alertDialog2.setPositiveButton("YES",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                ActionCall();
//                            }
//                        });
//
//                alertDialog2.setNegativeButton("NO",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                dialog.cancel();
//                            }
//                        });
//
//                alertDialog2.show();
                // dialog box
//                appUtils.showTwoBtnsMsgDialog("Would you like to update Checklist?", new SendMessageCallback() {
//                    @Override
//                    public void sendMsg(String message) {
//                        if (message.equalsIgnoreCase(CANCEL))
//                            return;
//                        YesButtonActionCall();
//
//                    }
//                });
                setFilterInfo(filterDataInfo, true);

            } else {
                setFilterInfo(filterDataInfo, true);
            }
        } else {
            setFilterInfo(filterDataInfo, showConfirmMsg);
        }

        if (getText().toString().isEmpty())
            clearFocus();
    }

    private void YesButtonActionCall() {
        setFilterInfo(filterDataInfo, true);

    }

    private void setFilterInfo(FormDataInfo filterDataInfo, boolean showConfirmMsg) {
        if (filterDataInfo != null)
            for (int i = 0; i < listItemNames.size(); i++) {
                if (filterDataInfo.getValue().equalsIgnoreCase(listItemNames.get(i)) || filterDataInfo.getValueUrdu().equalsIgnoreCase(listItemNames.get(i)) || filterDataInfo.getKey().equalsIgnoreCase(listItemNames.get(i))) {
                    setText(Html.fromHtml(listItemNames.get(i)));
                    formFieldInfo.setDefault_value("" + filterDataInfo.getKey());
                    selectedValues.add(filterDataInfo);
                    whichItemClicked.whichItemClicked(filterDataInfo.getKey());
                }
            }

        if (showConfirmMsg && DDCallback != null)
            DDCallback.onDDDataSelected(filterDataInfo);
        clearFocus();
    }

    private boolean isClicked = false;

    private void setClicListner() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked) {
                    return;
                } else {
                    isClicked = true;
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isClicked = false;

                    }
                }, 300);
                startDropDownActivity();
            }
        });
    }

    public void setSelectedValues(List<FormDataInfo> selectedValues) {
        if (selectedValues == null) {
            formFieldInfo.setDefault_value("");
        }
        if (selectedValues == null && this.selectedValues != null)
            this.selectedValues = new ArrayList<>();
        else
            this.selectedValues = selectedValues;
    }

    public List<FormDataInfo> getSelectedValues() {
        return selectedValues;
    }

    public void startDropDownActivity() {

//        Log.e("formFieldInfo.getData11", "Form Field info size==" + formFieldInfo.getData().size());
        Intent intent = new Intent(mContext, DropdownActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_DROPDOWN_NAME, dropdownName);
        bundle.putStringArrayList(SEARCH_DATA, (ArrayList<String>) listItemNames);
        bundle.putString(EXTRA_ACTV_TAG, getTag().toString());
        intent.putExtras(bundle);
        ((Activity) mContext).startActivityForResult(intent, RC_DROPDOWN);
    }

    public FrameLayout getPfaddLL() {
        return pfaddLL;
    }

    public void setPfaddLL(FrameLayout pfaddLL) {
        this.pfaddLL = pfaddLL;
    }

    public PFATextInputLayout getTextInputLayout() {
        return textInputLayout;
    }

    public void setTextInputLayout(PFATextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    public void setDDCallback(DDSelectedCallback DDCallback) {
        this.DDCallback = DDCallback;
    }
}

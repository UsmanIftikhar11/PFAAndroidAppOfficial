package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.interfaces.MultiSpinnerListener;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PFAMultiSpinner extends androidx.appcompat.widget.AppCompatSpinner implements OnMultiChoiceClickListener, OnCancelListener {

    private List<String> items = new ArrayList<>();
    private boolean[] selected;
    private String defaultText = "";
    private String spinnerTitle = "";
    private MultiSpinnerListener listener;
    private FormFieldInfo formFieldInfo;

    private List<FormDataInfo> selectedValues = new ArrayList<>();
    HashMap<String, List<FormDataInfo>> formViewsData;

    public PFAMultiSpinner(Context context, FormFieldInfo formFieldInfo, MultiSpinnerListener listener, HashMap<String, List<FormDataInfo>> formViewsData) {
        super(context);
        this.formViewsData = formViewsData;
        AppUtils appUtils = new AppUtils(context);
        this.formFieldInfo = formFieldInfo;
        defaultText = formFieldInfo.getValue();

        if (formFieldInfo.isRequired()) {
            setBackgroundDrawable(context.getResources().getDrawable(appUtils.isEnglishLang() ? R.mipmap.spinner_required : R.mipmap.ur_spinner_required));
        } else {
            setBackgroundDrawable(context.getResources().getDrawable(appUtils.isEnglishLang() ? R.mipmap.spinner_bg : R.mipmap.ur_spinner_bg));
        }
        setPopupBackgroundResource(R.color.white);

        setTag(formFieldInfo.getField_name());
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(appUtils.convertDpToPixel(5), appUtils.convertDpToPixel(5), appUtils.convertDpToPixel(10), appUtils.convertDpToPixel(5));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, appUtils.convertDpToPixel(40));
        params.setMargins(0, appUtils.convertDpToPixel(10), 0, 0);
        setLayoutParams(params);

        setItems(listener);

        setCheckedValues();

        setOnKeyListener(null);

        if (formFieldInfo.isInvisible()) {
            setVisibility(GONE);
        }
        if (formFieldInfo.isNotEditable()) {
            setFocusable(false);
            setClickable(false);
            setEnabled(false);
        }
    }

    private void setCheckedValues() {
        if (formViewsData != null && formViewsData.containsKey(getTag().toString())) {
            List<FormDataInfo> selectedValues = formViewsData.get(getTag().toString());
            for (int i = 0; i < items.size(); i++) {
                if (selectedValues.contains(formFieldInfo.getData().get(i))) {
                    selected[i] = true;
                }
            }
            onCancel(null);
        }
    }

    public PFAMultiSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        @SuppressLint("CustomViewStyleable") TypedArray a = arg0.obtainStyledAttributes(arg1, R.styleable.MultiSpinnerSearch);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.MultiSpinnerSearch_hintText) {
                spinnerTitle = a.getString(attr);
            }
        }
        a.recycle();
    }

    public PFAMultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        selected[which] = isChecked;
    }

    public List<FormDataInfo> getSelectedValues() {
        return selectedValues;
    }

    @Override
    public void onCancel(DialogInterface dialog1) {
        // refresh text on spinner
        StringBuilder spinnerBuffer = new StringBuilder();
        selectedValues.clear();
        for (int i = 0; i < items.size(); i++) {
            if (selected[i]) {
                spinnerBuffer.append(items.get(i));
                spinnerBuffer.append(", ");
                selectedValues.add(formFieldInfo.getData().get(i));
            }
        }

        String spinnerText = spinnerBuffer.toString();
        if (spinnerText.length() > 2) {
            spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
        } else {
            spinnerText = defaultText;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.textview_for_spinner, new String[]{spinnerText});
        setAdapter(adapter);
        if (selected.length > 0) {
            listener.onItemsSelected(selected, formFieldInfo);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(spinnerTitle);
        builder.setMultiChoiceItems(items.toArray(new CharSequence[0]), selected, this);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    /**
     * Sets items to this spinner.
     * <p>
     * //     * @param formFieldInfo A TreeMap where the keys are the values to display in the spinner
     * //     *                      and the value the initial selected state of the key.
     *
     * @param listener A MultiSpinnerListener.
     */
    private void setItems(MultiSpinnerListener listener) {


        if (formFieldInfo.getData() != null && formFieldInfo.getData().size() > 0) {

            this.listener = listener;

            selected = new boolean[formFieldInfo.getData().size()];
            for (int i = 0; i < formFieldInfo.getData().size(); i++) {
                selected[i] = formFieldInfo.getData().get(i).isSelected();
                items.add(formFieldInfo.getData().get(i).getValue());
            }

            // all text on the spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.textview_for_spinner, new String[]{defaultText});
            setAdapter(adapter);

            // Set Spinner Text
            onCancel(null);
        }
    }
}
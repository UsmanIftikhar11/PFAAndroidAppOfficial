package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

@SuppressLint("ViewConstructor")
public class PFAButton extends androidx.appcompat.widget.AppCompatButton {
    private FormFieldInfo formFieldInfo;
    private PFATableInfo pfaTableInfo;
    private Context mContext;
    AppUtils appUtils;

    public PFAButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.appUtils = new AppUtils(context);
    }
    @SuppressLint("RestrictedApi")
    public PFAButton(Context context, FormFieldInfo formFieldInfo, int buttonStyle) {
        super(new ContextThemeWrapper(context, buttonStyle), null, buttonStyle);

        appUtils= new AppUtils(context);

        this.formFieldInfo = formFieldInfo;
        this.mContext = context;
        setFormFieldBtnProperties();
        setGravity(Gravity.CENTER);
    }

    @SuppressLint("RestrictedApi")
    public PFAButton(Context context, PFATableInfo fieldInfo, int buttonStyle) {
        super(new ContextThemeWrapper(context, buttonStyle), null, buttonStyle);
        appUtils= new SharedPrefUtils(context);
        this.pfaTableInfo = fieldInfo;
        this.mContext = context;
        setTableBtnProperties();
        setGravity(Gravity.CENTER);
    }

    private void setFormFieldBtnProperties() {
        setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.button_green));
        setPadding((int) mContext.getResources().getDimension(R.dimen.form_left_padding),0 , (int) mContext.getResources().getDimension(R.dimen.form_right_padding), 0);
        setCompoundDrawablePadding(appUtils.convertDpToPixel(7));
        setTag(formFieldInfo.getData_type());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.form_text_field_height));
        params.setMargins(0, (int) mContext.getResources().getDimension(R.dimen.form_top_margin), 0, 0);
        setLayoutParams(params);
        setText(appUtils.isEnglishLang()?formFieldInfo.getValue():formFieldInfo.getValueUrdu());
        appUtils.applyFont(this, AppUtils.FONTS.HelveticaNeueBold);
        appUtils.applyStyle(formFieldInfo.getFont_style(),formFieldInfo.getFont_size(),formFieldInfo.getFont_color(),this);
    }

    private void setTableBtnProperties() {
        setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.button_green_rounded));
        setPadding((int) mContext.getResources().getDimension(R.dimen.form_left_padding),0 , (int) mContext.getResources().getDimension(R.dimen.form_right_padding), 0);
        setCompoundDrawablePadding(appUtils.convertDpToPixel(7));
        setTag(pfaTableInfo.getField_type());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.form_text_field_height));
        params.setMargins(0, (int) mContext.getResources().getDimension(R.dimen.form_left_padding), 0, (int) mContext.getResources().getDimension(R.dimen.form_left_padding));
        setLayoutParams(params);
        setText(appUtils.isEnglishLang()?pfaTableInfo.getValue():pfaTableInfo.getValueUrdu());
        appUtils.applyFont(this, AppUtils.FONTS.HelveticaNeueBold);
        appUtils.applyStyle(pfaTableInfo.getFont_style(),pfaTableInfo.getFont_size(),pfaTableInfo.getFont_color(),this);
    }

    public String getButtonUrl() {
        return formFieldInfo==null?pfaTableInfo.getAPI_URL():formFieldInfo.getAPI_URL();
    }

    public FormFieldInfo getFormFieldInfo() {
        return formFieldInfo;
    }

    public PFATableInfo getPFATableInfo() {
        return pfaTableInfo;
    }

}

package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.LinearLayout;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.utils.AppUtils;

@SuppressLint("ViewConstructor")
public class PFASectionTV extends androidx.appcompat.widget.AppCompatTextView {
    private Context context;
    public FormDataInfo formDataInfo;
    AppUtils appUtils;
    public PFASectionTV(Context context,String tag, String valueStr) {
        super(context);
        this.context=context;
        appUtils= new AppUtils(context);
        formDataInfo = new FormDataInfo();
        if(tag!=null && (!tag.isEmpty())) {
            setTag(tag);
            formDataInfo.setKey(valueStr);
        }

        setText(valueStr);
        formDataInfo.setName(valueStr);

        appUtils.applyFont(this, AppUtils.FONTS.HelveticaNeue);

    }

    public void setHeadingTextStyle(boolean isHeading)
    {
        if(isHeading) {
            setTextAppearance(context, R.style.inner_tv_17_sp_bold);
            appUtils.applyFont(this, AppUtils.FONTS.HelveticaNeueBold);
        }
        else
        {
            appUtils.applyFont(this, AppUtils.FONTS.HelveticaNeueMedium);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) context.getResources().getDimension(R.dimen.form_top_margin), (int) context.getResources().getDimension(R.dimen.form_top_margin),0, 0);

        setLayoutParams(params);
    }
    public void setSmallTextStyle()
    {
        setTextAppearance(context, R.style.inner_tv_15_sp);
        appUtils.applyFont(this, AppUtils.FONTS.HelveticaNeueMedium);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,appUtils.convertDpToPixel(15),0,0);
        setLayoutParams(params);
    }
}

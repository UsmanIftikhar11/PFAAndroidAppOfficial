package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.httputils.ImageHttpUtils;
import com.pfa.pfaapp.interfaces.ImageCallback;
import com.pfa.pfaapp.interfaces.RBClickCallback;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.utils.AppUtils;

import java.util.List;

public class PFASideMenuRB {

    @SuppressLint("InflateParams")
    public PFASideMenuRB(final Context mContext, RadioGroup radioGroup, List<PFAMenuInfo> pfaMenuInfos, final RBClickCallback callbacks, final boolean isVertical) {
        final AppUtils appUtils = new AppUtils(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);

        Log.d("NavDrawerClick" , "PFASideMenuRB");

        boolean showMenuBar = false;

        for (int i = 0; i < pfaMenuInfos.size(); i++) {
            Log.d("NavDrawerClick" , "PFASideMenuRB size = " + pfaMenuInfos.size());
            Log.d("NavDrawerClick" , "PFASideMenuRB name = " + pfaMenuInfos.get(i).getMenuItemName());
            final RadioButton radioButton;
            LinearLayout.LayoutParams params;
            if (isVertical) {
                radioButton = (RadioButton) inflater.inflate(R.layout.pfa_sidemenu_rb, null);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.form_text_field_height));
                Log.d("NavDrawerClick" , "PFASideMenuRB vertical");
            } else {
                radioButton = (RadioButton) inflater.inflate(R.layout.pfa_tab_rb, null);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) mContext.getResources().getDimension(R.dimen.height_60_dp));
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                radioButton.setAllCaps(true);
                radioButton.setGravity(Gravity.CENTER);
                Log.d("NavDrawerClick" , "PFASideMenuRB horizontal");
            }
            if (i > 0 && isVertical) {
                params.setMargins(0, (int) mContext.getResources().getDimension(R.dimen.vertical_margin_1), 0, 0);
            }

            radioButton.setLayoutParams(params);
            radioButton.setText(appUtils.isEnglishLang() ? pfaMenuInfos.get(i).getMenuItemName() : pfaMenuInfos.get(i).getMenuItemNameUrdu());

            radioButton.setId(pfaMenuInfos.get(i).getMenuItemOrder());
            radioButton.setTag(pfaMenuInfos.get(i).getMenuItemName());
            appUtils.applyFont(radioButton, AppUtils.FONTS.HelveticaNeueBold);

            if (pfaMenuInfos.get(i).getBg_color() != null) {
                radioButton.setBackgroundColor(appUtils.colorFromHexDecimal(pfaMenuInfos.get(i).getBg_color()));
            }
            if (i == 0)
                radioButton.setChecked(true);
            else
                radioButton.setChecked(false);

            if (!pfaMenuInfos.get(i).getMenuItemImg().equals("") && isVertical)
                new ImageHttpUtils(mContext, pfaMenuInfos.get(i).getMenuItemImg(), new ImageCallback() {
                    @Override
                    public void onBitmapDownloaded(Bitmap bitmap) {
                        {
                            Drawable drawable = new BitmapDrawable(mContext.getResources(), Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true));
                            if (appUtils.isEnglishLang()) {
                                radioButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                            } else {
                                radioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                            }
                        }
                    }
                });

            int finalI = i;
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    callbacks.onClickCallUrl(pfaMenuInfos.get(view.getId()).getAPI_URL());
                    callbacks.onClickRB(view);

                    Log.d("CiTabbedDrawerClick", "view id = " + view.getTag());
                    Log.d("CiTabbedDrawerClick", "view id i= " + finalI);
                    Log.d("CiTabbedDrawerClick", "view id url= " + pfaMenuInfos.get(view.getId()).getAPI_URL());

                    Log.d("NavDrawerClick" , "PFASideMenuRB1 click = " /*+ view.getTag().toString()*/);

                }
            });
            if (pfaMenuInfos.get(i).getMenuItemName() == null || pfaMenuInfos.get(i).getMenuItemName().isEmpty()) {
                radioButton.setVisibility(View.GONE);
            } else {
                showMenuBar = true;
            }
            radioGroup.addView(radioButton);
        }

        if (!showMenuBar)
            radioGroup.setVisibility(View.GONE);
    }

    public PFASideMenuRB(final Context mContext, RadioGroup radioGroup, List<PFAMenuInfo> pfaMenuInfos, final RBClickCallback callbacks) {
        this(mContext, radioGroup, pfaMenuInfos, callbacks, true);
    }
}

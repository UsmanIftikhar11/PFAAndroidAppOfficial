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

import java.util.ArrayList;
import java.util.List;

public class PFASideMenuRB {

    public static int itemId;
//    List<Integer> indexList = new ArrayList<>() 1;

    @SuppressLint("InflateParams")
    public PFASideMenuRB(final Context mContext, RadioGroup radioGroup, List<PFAMenuInfo> pfaMenuInfos, final RBClickCallback callbacks, final boolean isVertical) {
        final AppUtils appUtils = new AppUtils(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);

        Log.d("NavDrawerClick", "PFASideMenuRB");

        boolean showMenuBar = false;

        for (int i = 0; i < pfaMenuInfos.size(); i++) {
            Log.d("NavDrawerClick", "PFASideMenuRB size = " + pfaMenuInfos.size());
            Log.d("NavDrawerClick", "PFASideMenuRB name = " + pfaMenuInfos.get(i).getMenuItemName());
            Log.d("NavDrawerClick", "PFASideMenuRB id = " + pfaMenuInfos.get(i).getMenuItemID());

            if (pfaMenuInfos.get(i).getMenuItemName().equals("Announcements")) {
                itemId = pfaMenuInfos.get(i).getMenuItemID();
            }

            final RadioButton radioButton;
            LinearLayout.LayoutParams params;
            if (isVertical) {
                radioButton = (RadioButton) inflater.inflate(R.layout.pfa_sidemenu_rb, null);
                /*if(pfaMenuInfos.get(i).getMenuItemName().startsWith("20"))
                    radioButton.setVisibility(View.GONE); 2*/
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                Log.d("NavDrawerClick", "PFASideMenuRB vertical");
            } else {
                radioButton = (RadioButton) inflater.inflate(R.layout.pfa_tab_rb, null);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) mContext.getResources().getDimension(R.dimen.height_60_dp));
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                radioButton.setAllCaps(true);
                radioButton.setGravity(Gravity.CENTER);
                Log.d("NavDrawerClick", "PFASideMenuRB horizontal");
            }
            if (i > 0 && isVertical) {
                /*if (pfaMenuInfos.get(i).getMenuItemName().startsWith("20"))
                    params.setMargins((int) mContext.getResources().getDimension(R.dimen.side_menu), (int) mContext.getResources().getDimension(R.dimen.vertical_margin_1), 0, 0);
                else 3*/
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

                    /*if (pfaMenuInfos.get(view.getId()).getMenuItemName().contains("2018"))
                        callbacks.onClickCallUrl("enforcements/conducted_enforcements/57/1/2018/");
                    else if (pfaMenuInfos.get(view.getId()).getMenuItemName().contains("2019"))
                        callbacks.onClickCallUrl("enforcements/conducted_enforcements/57/1/2019/");
                    else if (pfaMenuInfos.get(view.getId()).getMenuItemName().contains("2020"))
                        callbacks.onClickCallUrl("enforcements/conducted_enforcements/57/1/2020/");
                    else if (pfaMenuInfos.get(view.getId()).getMenuItemName().contains("2021"))
                        callbacks.onClickCallUrl("enforcements/conducted_enforcements/57/1/2021/");
                    else if (pfaMenuInfos.get(view.getId()).getMenuItemName().contains("2022"))
                        callbacks.onClickCallUrl("enforcements/conducted_enforcements/57/1/2022/");*/
//                    callbacks.onClickCallUrl(pfaMenuInfos.get(view.getId()).getAPI_URL());

                    /*if (view.getTag().equals("Enforcements")){
                            Log.d("EnforcementsClick", "list size = " + indexList.size());
                            for (int i = 0; i < indexList.size(); i++){
                                Log.d("EnforcementsClick", "view id = here");
                                if (radioGroup.getChildAt(indexList.get(i)).getVisibility() == View.GONE){
                                    Log.d("EnforcementsClick", "view id = " + view.getTag());
                                    radioGroup.getChildAt(indexList.get(i)).setVisibility(View.VISIBLE);
                                }
                                else {
                                    radioGroup.getChildAt(indexList.get(i)).setVisibility(View.GONE);
                                }
                            }
                    } else { 4*/
                        if (pfaMenuInfos.get(view.getId()).getClickable_URL() != null)
                            callbacks.onClickCallUrl(pfaMenuInfos.get(view.getId()).getClickable_URL());
//                    else
//                        callbacks.onClickCallUrl(pfaMenuInfos.get(view.getId()).getAPI_URL());
                        callbacks.onClickRB(view);
//                    } 5

                    Log.d("CiTabbedDrawerClick", "view id = " + view.getTag());
                    Log.d("CiTabbedDrawerClick", "view id i= " + finalI);
                    Log.d("CiTabbedDrawerClick", "view id i= " + pfaMenuInfos.get(view.getId()).getMenuItemName());
                    Log.d("CiTabbedDrawerClick", "view id url= " + pfaMenuInfos.get(view.getId()).getAPI_URL());
                    Log.d("CiTabbedDrawerClick", "view id clickable url= " + pfaMenuInfos.get(view.getId()).getClickable_URL());

                    Log.d("NavDrawerClick", "PFASideMenuRB1 click = " /*+ view.getTag().toString()*/);

                }
            });
            if (pfaMenuInfos.get(i).getMenuItemName() == null || pfaMenuInfos.get(i).getMenuItemName().isEmpty()) {
                radioButton.setVisibility(View.GONE);
            } else {
                showMenuBar = true;
            }

            /*if (pfaMenuInfos.get(i).getMenuItemName().startsWith("20"))
                indexList.add(i); 6*/
            radioGroup.addView(radioButton);

            /*if (pfaMenuInfos.get(i).getMenuItemName().equals("Enforcements")){
                LinearLayout.LayoutParams params1;
                params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.leftMargin = (int) mContext.getResources().getDimension(R.dimen.side_menu);
                RadioButton radioButton1;
                radioButton1 = (RadioButton) inflater.inflate(R.layout.pfa_sidemenu_rb_child, null);
                radioButton1.setLayoutParams(params1);
                radioButton1.setText("2018");
//                radioButton1.setId(pfaMenuInfos.get(i).getMenuItemOrder()-3+4);
//                radioButton1.setTag(pfaMenuInfos.get(i).getMenuItemName()+"2017");
//                pfaMenuInfos.get(i).setAPI_URL("enforcements/conducted_enforcements/411/1/2019");
                appUtils.applyFont(radioButton1, AppUtils.FONTS.HelveticaNeueBold);
                if (pfaMenuInfos.get(i).getBg_color() != null) {
                    radioButton1.setBackgroundColor(appUtils.colorFromHexDecimal(pfaMenuInfos.get(i).getBg_color()));
                }
                radioButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callbacks.onClickRB(v);
                    }
                });
                radioGroup.addView(radioButton1);
            }*/
        }

        if (!showMenuBar)
            radioGroup.setVisibility(View.GONE);
    }

    public PFASideMenuRB(final Context mContext, RadioGroup radioGroup, List<PFAMenuInfo> pfaMenuInfos, final RBClickCallback callbacks) {
        this(mContext, radioGroup, pfaMenuInfos, callbacks, true);
    }
}

/*
 * Copyright (c) 2018. All Rights Reserved by Punjab Food Authority
 */

package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pfa.pfaapp.AppController;
import com.pfa.pfaapp.ImageGalleryActivity;
import com.pfa.pfaapp.PFADetailActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.fragments.MenuMapFragment;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.PFAViewsCallbacks;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static android.view.View.GONE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_ACTIVITY_TITLE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_DOWNLOAD_URL;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;

public class PFADetailMenu extends SharedPrefUtils {

    private Context mContext;

    public PFADetailMenu(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    public void createDetailViews(List<PFATableInfo> fields, List<String> columnTags, LinearLayout fpfaListItemParentLL, final PFAViewsCallbacks pfaViewsCallbacks) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        Collections.sort(fields, new Comparator<PFATableInfo>() {
            @Override
            public int compare(PFATableInfo o1, PFATableInfo o2) {
                int order1 = o1.getOrder();
                int order2 = o2.getOrder();

                return compareInts(order1, order2);
            }
        });

        // Linear layout for images of business detail or any other detail view
        LinearLayout imagesLL = null;

        for (final PFATableInfo fieldInfo : fields) {

            columnTags.add(fieldInfo.getField_name());

            switch (fieldInfo.getField_type()) {
                case "button":
                    Log.d("viewCreated", "PFADetailMenu button");
                    final PFAButton button = new PFAButton(mContext, fieldInfo, R.style.white_15_sp);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("viewCreated", "PFADetailMenu buttonClick");
                            pfaViewsCallbacks.onButtonCLicked(view);
                        }
                    });

                    fpfaListItemParentLL.addView(button);
                    if (fieldInfo.isInvisible()) {
                        button.setVisibility(GONE);
                    }
                    break;

                case "heading":
                    @SuppressLint("InflateParams") RelativeLayout pfa_detail_heading = (RelativeLayout) inflater.inflate(R.layout.pfa_detail_heading, null, false);

                    Log.d("viewCreated", "PFADetailMenu heading");
                    RelativeLayout.LayoutParams rlLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(65));
                    pfa_detail_heading.setLayoutParams(rlLayoutParams);

                    TextView headingLblTV = pfa_detail_heading.findViewById(R.id.lblTV);
                    headingLblTV.setText(fieldInfo.getValue());
                    applyFont(headingLblTV, FONTS.HelveticaNeueBold);

                    if (fieldInfo.getClickable_text() != null && (!fieldInfo.getClickable_text().isEmpty())) {
                        final TextView clickableTV = pfa_detail_heading.findViewById(R.id.clickableTV);
                        clickableTV.setText(fieldInfo.getClickable_text());
                        applyStyle(fieldInfo.getFont_style(), "m", fieldInfo.getFont_color(), clickableTV);

                        if (fieldInfo.getClickable_text().equalsIgnoreCase("Edit")) {
                            clickableTV.setTextSize(COMPLEX_UNIT_SP, 17);
                        }


                        clickableTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("viewCreated", "PFADetailMenu headingClick");
                                final Bundle bundle = new Bundle();
                                bundle.putString(EXTRA_URL_TO_CALL, "" + fieldInfo.getAPI_URL());
                                bundle.putString(EXTRA_ACTIVITY_TITLE, isEnglishLang()?fieldInfo.getValue():fieldInfo.getValueUrdu());
//                                startNewActivity(PFADetailActivity.class, bundle, false);

                                /////////////
                                HttpService httpService = new HttpService(mContext);

                                httpService.getListsData(fieldInfo.getAPI_URL(), new HashMap<String, String>(), new HttpResponseCallback() {
                                    @Override
                                    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                        if (response != null)
                                            bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                                        startNewActivity(PFADetailActivity.class, bundle, false);
                                    }
                                }, true);
                                /////////////
                            }
                        });
                    }
                    fpfaListItemParentLL.addView(pfa_detail_heading);

                    if (fieldInfo.isInvisible()) {
                        pfa_detail_heading.setVisibility(GONE);
                    }

                    break;
                case "text":
                    @SuppressLint("InflateParams") LinearLayout subviewLL = (LinearLayout) inflater.inflate(R.layout.pfa_detail_subview, null, false);

                    Log.d("viewCreated", "PFADetailMenu text");
                    LinearLayout.LayoutParams subViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    subviewLL.setMinimumHeight(convertDpToPixel(55));
                    subviewLL.setLayoutParams(subViewLayoutParams);

                    TextView lblTV = subviewLL.findViewById(R.id.lblTV);

                    TextView subviewTV = subviewLL.findViewById(R.id.subviewTV);

                    if (fieldInfo.getValue() == null || fieldInfo.getValue().isEmpty()) {
                        lblTV.setVisibility(View.GONE);
                    } else {
                        lblTV.setVisibility(View.VISIBLE);
                    }
                    lblTV.setText(String.format(Locale.getDefault(), "%s: ", (isEnglishLang()?fieldInfo.getValue():fieldInfo.getValueUrdu())));

                    applyFont(lblTV, FONTS.HelveticaNeueMedium);

                    subviewTV.setText(Html.fromHtml(isEnglishLang()?fieldInfo.getData():fieldInfo.getDataUrdu()));

                    subviewTV.setTag(fieldInfo.getField_name());
                    applyFont(subviewTV, FONTS.HelveticaNeueBold);

//                    Linkify.addLinks(subviewTV, Linkify.ALL);
                    subviewTV.setLinksClickable(false);
                    fpfaListItemParentLL.addView(subviewLL);

                    applyStyle(fieldInfo.getFont_style(), fieldInfo.getFont_size(), fieldInfo.getFont_color(), subviewTV);

                    if (fieldInfo.isInvisible()) {
                        subviewLL.setVisibility(GONE);
                    }

                    break;

                case "phone":
                    @SuppressLint("InflateParams") LinearLayout subviewLL1 = (LinearLayout) inflater.inflate(R.layout.pfa_detail_subview, null, false);

                    Log.d("viewCreated", "PFADetailMenu phone");
                    LinearLayout.LayoutParams subViewLayoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(55));
                    subviewLL1.setLayoutParams(subViewLayoutParams1);

                    TextView lblTV1 = subviewLL1.findViewById(R.id.lblTV);

                    TextView subviewTV1 = subviewLL1.findViewById(R.id.subviewTV);

                    if (fieldInfo.getValue() == null || fieldInfo.getValue().isEmpty()) {
                        lblTV1.setVisibility(View.GONE);
                    } else {
                        lblTV1.setVisibility(View.VISIBLE);
                    }
                    lblTV1.setText(String.format(Locale.getDefault(), "%s: ", (isEnglishLang()?fieldInfo.getValue():fieldInfo.getValueUrdu())));

                    applyFont(lblTV1, FONTS.HelveticaNeueMedium);

                    subviewTV1.setText(Html.fromHtml(isEnglishLang()?fieldInfo.getData():fieldInfo.getDataUrdu()));

                    subviewTV1.setTag(fieldInfo.getField_name());
                    applyFont(subviewTV1, FONTS.HelveticaNeueBold);

                    subviewTV1.setLinksClickable(false);
                    fpfaListItemParentLL.addView(subviewLL1);

                    applyStyle(fieldInfo.getFont_style(), fieldInfo.getFont_size(), fieldInfo.getFont_color(), subviewTV1);

                    subviewTV1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doPhoneCall(fieldInfo.getData());
                        }
                    });

                    if (fieldInfo.isInvisible()) {
                        subviewLL1.setVisibility(GONE);
                    }
                    break;

                case "imageView":

                    Log.d("viewCreated", "PFADetailMenu imageView");
                    if (!fieldInfo.getData().equals("")) {

                        if (imagesLL == null) {
                            imagesLL = new LinearLayout(mContext);
                            imagesLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            imagesLL.setOrientation(LinearLayout.HORIZONTAL);
                            imagesLL.setBackground(mContext.getResources().getDrawable(R.mipmap.text_bg));
                        }

                        @SuppressLint("InflateParams") LinearLayout img_attachment_ll = (LinearLayout) inflater.inflate(R.layout.img_detail_ll, null, false);

                        (img_attachment_ll.findViewById(R.id.selectImgTV)).setVisibility(View.GONE);
                        TextView attachmentLblTV = img_attachment_ll.findViewById(R.id.attachmentLblTV);
                        final CustomNetworkImageView attachmentCNIV = img_attachment_ll.findViewById(R.id.attachmentCNIV);
                        attachmentCNIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imagesLL.addView(img_attachment_ll);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                        params.setMargins(0, (int) mContext.getResources().getDimension(R.dimen.form_top_margin), 0, 0);
                        img_attachment_ll.setLayoutParams(params);

                        attachmentCNIV.setTag(fieldInfo.getField_name());
                        attachmentLblTV.setText(isEnglishLang()?fieldInfo.getValue():fieldInfo.getValueUrdu());
                        attachmentLblTV.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                        if ((fieldInfo.getData() != null) && (!fieldInfo.getData().equals(""))) {
                            attachmentCNIV.setImageUrl(fieldInfo.getData(), AppController.getInstance().getImageLoader());

                            img_attachment_ll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("imageCheck" , "fieldInfo = "  + fieldInfo.getField_type());
                                    if (fieldInfo.getData() != null && (!fieldInfo.getData().isEmpty())) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString(EXTRA_DOWNLOAD_URL, fieldInfo.getData());
                                        startNewActivity(ImageGalleryActivity.class, bundle, false);
                                    }
                                }
                            });

                        } else {
                            attachmentCNIV.setDrawable(R.mipmap.no_img);
                        }
                        if (fieldInfo.isInvisible()) {
                            img_attachment_ll.setVisibility(GONE);
                        }
                    }

                    break;
                case "googlemap":
                    @SuppressLint("InflateParams") RelativeLayout map_detail_ll = (RelativeLayout) inflater.inflate(R.layout.map_detail_ll, null, false);

                    Log.d("viewCreated", "PFADetailMenu map");
                    LinearLayout.LayoutParams map_detail_llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(202));
                    map_detail_ll.setLayoutParams(map_detail_llParams);

                    fpfaListItemParentLL.addView(map_detail_ll);
                    if (!fieldInfo.getData().equals("")) {

                        ArrayList<String> latLngs = new ArrayList<>();
                        latLngs.add(fieldInfo.getData());

                        MenuMapFragment menuItemFragment = MenuMapFragment.newInstance(null, latLngs);
                        replaceFragment(menuItemFragment);
                    }

                    if (fieldInfo.isInvisible()) {
                        map_detail_ll.setVisibility(GONE);
                    }
                    break;
            }
        }

        if (imagesLL != null && imagesLL.getChildCount()>0) {
            fpfaListItemParentLL.addView(imagesLL);

        }
    }

}

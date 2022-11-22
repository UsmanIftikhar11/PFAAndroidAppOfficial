package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputType;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pfa.pfaapp.DownloadLicenseActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.httputils.ImageHttpUtils;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.ImageCallback;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;



import static android.view.View.GONE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;

import org.json.JSONObject;

public class PFAListItem extends SharedPrefUtils {

    public PFAListItem(Context mContext) {
        super(mContext);
    }

    public RelativeLayout createViews(List<PFATableInfo> fields, List<String> columnTags, boolean isDetail) {
        Log.d("onCreateActv" , "PFAListItem 1");
        return createViews(fields, columnTags, true, isDetail);
    }

    @SuppressLint("InflateParams")
    public RelativeLayout createViews(List<PFATableInfo> fields, List<String> columnTags, boolean autoLink, boolean isDetail) {
        Log.d("onCreateActv" , "PFAListItem 2");

        LayoutInflater inflater = LayoutInflater.from(mContext);
        RelativeLayout parentView = (RelativeLayout) inflater.inflate(R.layout.pfa_list_item, null, false);



        LinearLayout fpfaListItemParentLL = parentView.findViewById(R.id.fpfaListItemParentLL);
        SimpleDraweeView pfaListCNIVLeft = parentView.findViewById(R.id.pfaListCNIVLeft);
        SimpleDraweeView pfaListCNIVRight = parentView.findViewById(R.id.pfaListCNIVRight);



        Collections.sort(fields, new Comparator<PFATableInfo>() {
            @Override
            public int compare(PFATableInfo o1, PFATableInfo o2) {
                int order1 = o1.getOrder();
                int order2 = o2.getOrder();

                return compareInts(order1, order2);
            }
        });

        for (final PFATableInfo fieldInfo : fields) {

            columnTags.add(fieldInfo.getField_name());

            String fieldType = fieldInfo.getField_type();

            if (fieldType.equalsIgnoreCase("text") || fieldType.equalsIgnoreCase("numeric") || fieldType.equalsIgnoreCase("textarea") ||
                    fieldType.equalsIgnoreCase("phone") || fieldType.equalsIgnoreCase("dropdown")) {
                Log.d("PFAListItemView" , "PFAListItem 1");

                LinearLayout subviewLL;
                if (isDetail) {
                    subviewLL = (LinearLayout) inflater.inflate(R.layout.pfa_detail_item, null, false);
                } else {
                    subviewLL = (LinearLayout) inflater.inflate(R.layout.pfa_tbl_subview, null, false);
                }
                TextView lblTV = subviewLL.findViewById(R.id.lblTV);
                applyFont(lblTV, FONTS.HelveticaNeue);

                final TextView subviewTV = subviewLL.findViewById(R.id.subviewTV);


                if (fieldInfo.getValue() == null || fieldInfo.getValue().isEmpty()) {
                    lblTV.setVisibility(View.GONE);
                }
                lblTV.setText(String.format(Locale.getDefault(), "%s: ", (isEnglishLang()?fieldInfo.getValue():fieldInfo.getValueUrdu())));
                subviewTV.setText(Html.fromHtml("" + (isEnglishLang()?fieldInfo.getData():fieldInfo.getDataUrdu())));

                Log.d("PFAListItemView" , "PFAListItem value = " + fieldInfo.getValue());
                Log.d("PFAListItemView" , "PFAListItem Data = " + fieldInfo.getData());

                subviewTV.setTag(fieldInfo.getField_name());
                if (autoLink) {
                    Linkify.addLinks(subviewTV, Linkify.EMAIL_ADDRESSES);
                    subviewTV.setLinksClickable(false);
                }
                fpfaListItemParentLL.addView(subviewLL);

                if (fieldInfo.isInvisible()) {
                    subviewLL.setVisibility(View.GONE);
                }

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) subviewLL.getLayoutParams();

                applyFont(subviewTV, FONTS.HelveticaNeue);

                if (fieldInfo.getData().equals("Download")) {
                    subviewTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Bundle bundle = new Bundle();
                            HttpService httpService = new HttpService(mContext);

                            bundle.putString(EXTRA_URL_TO_CALL, "" + fieldInfo.getAPI_URL());
                            httpService.getListsData(fieldInfo.getAPI_URL(), new HashMap<String, String>(), new HttpResponseCallback() {
                                @Override
                                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                    if (response != null)
                                        bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                                    startNewActivity(DownloadLicenseActivity.class, bundle, false);
                                }
                            }, true);
                        }
                    });
                }

                applyStyle(fieldInfo.getFont_style(), fieldInfo.getFont_size(), fieldInfo.getFont_color(), subviewTV);

                if (fieldType.equalsIgnoreCase("phone")) {
                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }

                if (fieldInfo.getMargin_top() != null && (!fieldInfo.getMargin_top().isEmpty())) {
                    if (params != null) {
                        params.setMargins(0, Integer.parseInt(fieldInfo.getMargin_top()), 0, 0);
                        subviewLL.setLayoutParams(params);
                    }
                }

                if (fieldInfo.getField_name().equalsIgnoreCase("business_visit")||
                        fieldInfo.getField_name().equalsIgnoreCase(String.valueOf(FIELD_TYPE.local_add_newUrl))) {

                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(convertDpToPixel(7), convertDpToPixel(10), 0, convertDpToPixel(0));
                    subviewLL.setPadding(0, convertDpToPixel(3), 0, convertDpToPixel(3));
                    subviewLL.setLayoutParams(params);

                    subviewLL.setBackgroundResource(R.color.green_btn_color);
                    subviewTV.setTextColor(mContext.getResources().getColor(R.color.white));
                }

                if (fieldInfo.getField_name().equalsIgnoreCase(String.valueOf(FIELD_TYPE.submit_category_button))){
                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(convertDpToPixel(7), convertDpToPixel(10), 0, convertDpToPixel(0));
                    subviewLL.setLayoutParams(params);
                    subviewLL.setBackgroundResource(R.color.green_btn_color);

                    subviewTV.setPadding(0, convertDpToPixel(10), 0, convertDpToPixel(10));
                    subviewTV.setTextColor(mContext.getResources().getColor(R.color.white));
                }

                // download and set drawable left on edit text field
                if (fieldInfo.getIcon() != null && !fieldInfo.getIcon().equals("") && !fieldInfo.getIcon().equalsIgnoreCase("http://www.jazzcash.com.pk/assets/uploads/2016/05/new-icon-set-CNIC.png"))
                    new ImageHttpUtils(mContext, fieldInfo.getIcon(), new ImageCallback() {
                        @Override
                        public void onBitmapDownloaded(Bitmap bitmap) {
                            Drawable drawableLeft = new BitmapDrawable(mContext.getResources(), Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true));
                            if(isEnglishLang())
                            {
                                subviewTV.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
                            }
                            else
                            {
                                subviewTV.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableLeft, null);
                            }

                            subviewTV.setCompoundDrawablePadding(convertDpToPixel(7));

                        }
                    });

            } else if (fieldType.equalsIgnoreCase("imageView")) {

                if (fieldInfo.getData() != null && (!fieldInfo.getData().equals(""))) {

                    if (fieldInfo.getDirection() != null && fieldInfo.getDirection().equalsIgnoreCase(String.valueOf(DIRECTION.left))) {
                        if (fieldInfo.getShape() != null && fieldInfo.getShape().equalsIgnoreCase(String.valueOf(IMAGE_SHAPE.circle))) {
                            pfaListCNIVLeft.getHierarchy().setRoundingParams(getRoundParams());
                        }
                        pfaListCNIVLeft.setVisibility(View.VISIBLE);
                        pfaListCNIVLeft.setTag(fieldInfo.getField_name());
                        pfaListCNIVLeft.setImageURI(fieldInfo.getData());

                        if (fieldInfo.isNotClickable()) {
                            pfaListCNIVLeft.setEnabled(false);
                            pfaListCNIVLeft.setClickable(false);
                            pfaListCNIVLeft.setFocusable(false);
                        }

                    }else if (fieldInfo.getDirection() != null && fieldInfo.getDirection().equalsIgnoreCase(String.valueOf(DIRECTION.clearfix))){


                        SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ImgPath", fieldInfo.getData());
                editor.putString("ImgTag",fieldInfo.getField_name());
                editor.apply();


                    } else {

                        if (fieldInfo.getShape() != null && fieldInfo.getShape().equalsIgnoreCase(String.valueOf(IMAGE_SHAPE.circle))) {
                            pfaListCNIVRight.getHierarchy().setRoundingParams(getRoundParams());
                        }

                        pfaListCNIVRight.setVisibility(View.VISIBLE);
                        pfaListCNIVRight.setTag(fieldInfo.getField_name());

                        if (fieldInfo.getData().startsWith("http")) {
                            pfaListCNIVRight.setImageURI(fieldInfo.getData());
                        } else {
                            File file = new File(fieldInfo.getData());
                            pfaListCNIVRight.setImageURI(Uri.fromFile(file));
                        }

                        if (fieldInfo.isNotClickable()) {
                            pfaListCNIVRight.setEnabled(false);
                            pfaListCNIVRight.setClickable(false);
                            pfaListCNIVRight.setFocusable(false);
                        }
                    }
                } else {
                    pfaListCNIVLeft.setVisibility(View.GONE);
                    pfaListCNIVRight.setVisibility(View.GONE);
                }
            } else if (fieldType.equalsIgnoreCase("abc") || fieldType.equalsIgnoreCase("abc_phone")) {

                Log.d("PFAListItemView" , "PFAListItem 2");
                pfaListCNIVLeft.setVisibility(View.GONE);
                pfaListCNIVRight.setVisibility(View.GONE);

                fpfaListItemParentLL.setPadding(convertDpToPixel(0), convertDpToPixel(0), convertDpToPixel(0), convertDpToPixel(0));


                LinearLayout subviewLL = (LinearLayout) inflater.inflate(R.layout.pfa_form_edittext, null, false);
                LinearLayout.LayoutParams subViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                if (fieldInfo.getField_name().equalsIgnoreCase("recipient_cnic")){
                    LinearLayout imagView = subviewLL.findViewById(R.id.clearfix);
                    SimpleDraweeView img1 = imagView.findViewById(R.id.rightS);

                    SharedPreferences sharedPreferences  = PreferenceManager
                            .getDefaultSharedPreferences(mContext);
                            String path = sharedPreferences.getString("ImgPath", "");
                            String tag = sharedPreferences.getString("ImgTag", "");
                    Glide.with(mContext).load(path).into(img1);

                    img1.setImageURI(path);
                            img1.setTag(tag);

                }else {
                    LinearLayout imagView = subviewLL.findViewById(R.id.clearfix);
                    imagView.setVisibility(GONE);
                }


                subviewLL.setLayoutParams(subViewLayoutParams);
                subViewLayoutParams.setMargins(0, 0, 0, 0);

                PFAEditText abcET = subviewLL.findViewById(R.id.abcET);  //new PFAEditText(mContext, fieldInfo, formFilteredData);

                if (isDetail) {
                    abcET.setLayoutParams(subViewLayoutParams);
                    abcET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    abcET.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);

                } else {
                    abcET.setRawInputType(InputType.TYPE_CLASS_TEXT);
                    subviewLL.setMinimumHeight(convertDpToPixel(60));
                    abcET.setMaxLines(3);
                }


                abcET.setText(Html.fromHtml("" + (isEnglishLang()?fieldInfo.getData():fieldInfo.getDataUrdu())));
                abcET.setFocusable(false);

                if (fieldInfo.isInvisible()) {
                    subviewLL.setVisibility(GONE);
                }

                if (fieldType.equalsIgnoreCase("abc_phone")) {
                    abcET.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                    abcET.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doPhoneCall(fieldInfo.getData());
                        }
                    });
                } else {
                    abcET.setClickable(false);
                    abcET.setEnabled(false);
                }

                applyStyle(fieldInfo.getFont_style(), fieldInfo.getFont_size(), fieldInfo.getFont_color(), abcET);

                TextView lblTV = subviewLL.findViewById(R.id.lblTV);
                lblTV.setText(isEnglishLang()?fieldInfo.getValue():fieldInfo.getValueUrdu());

                fpfaListItemParentLL.addView(subviewLL);
            }
        }
        return parentView;
    }

    private RoundingParams getRoundParams() {
        int color = mContext.getResources().getColor(R.color.cfcfcf);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setBorder(color, 1.0f);
        roundingParams.setRoundAsCircle(true);
        return roundingParams;
    }
}
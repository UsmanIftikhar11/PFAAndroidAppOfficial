package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.AppController;
import com.pfa.pfaapp.ImageGalleryActivity;
import com.pfa.pfaapp.MapsActivity;
import com.pfa.pfaapp.PFADetailActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.fragments.MenuMapFragment;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.BizLocCallback;
import com.pfa.pfaapp.interfaces.CheckUserCallback;
import com.pfa.pfaapp.interfaces.CreateViewCallback;
import com.pfa.pfaapp.interfaces.DDSelectedCallback;
import com.pfa.pfaapp.interfaces.GetDateCallback;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.MultiSpinnerListener;
import com.pfa.pfaapp.interfaces.PFATextWatcher;
import com.pfa.pfaapp.interfaces.PFAViewsCallbacks;
import com.pfa.pfaapp.interfaces.RGSelectCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
import com.pfa.pfaapp.localdbmodels.DistrictInfo;
import com.pfa.pfaapp.localdbmodels.DivisionInfo;
import com.pfa.pfaapp.localdbmodels.RegionInfo;
import com.pfa.pfaapp.localdbmodels.SubTownInfo;
import com.pfa.pfaapp.localdbmodels.TownInfo;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.models.FormSectionInfo;
import com.pfa.pfaapp.models.PFASearchInfo;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.CustomDateUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.pfa.pfaapp.utils.AddInspectionUtils.IS_FINE;
import static com.pfa.pfaapp.utils.AppConst.BUSINESS_LOCATION_FIELD;
import static com.pfa.pfaapp.utils.AppConst.DD_BIZ_SIZE;
import static com.pfa.pfaapp.utils.AppConst.DD_FOOD_LAB_TEST;
import static com.pfa.pfaapp.utils.AppConst.DD_STATUS;
import static com.pfa.pfaapp.utils.AppConst.DD_STATUS_EP;
import static com.pfa.pfaapp.utils.AppConst.DD_STATUS_FINE;
import static com.pfa.pfaapp.utils.AppConst.DD_STATUS_SEAL;
import static com.pfa.pfaapp.utils.AppConst.DISTRICT_TAG;
import static com.pfa.pfaapp.utils.AppConst.DIVISION_TAG;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_ACTIVITY_TITLE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_ACTV_TAG;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_DOWNLOAD_URL;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.INSPECTION_ID;
import static com.pfa.pfaapp.utils.AppConst.PFA_SEARCH_TAG;
import static com.pfa.pfaapp.utils.AppConst.REGION_TAG;
import static com.pfa.pfaapp.utils.AppConst.SELECTED_POSITION;
import static com.pfa.pfaapp.utils.AppConst.SUB_TOWN_TAG;
import static com.pfa.pfaapp.utils.AppConst.TOWN_TAG;

/**
 * This is the class to create all the views based on form data received.
 * It also validates
 */
public class CustomViewCreate extends SearchBizData implements BizLocCallback {
    private PFAViewsCallbacks pfaViewsCallbacks;
    private HashMap<String, List<FormDataInfo>> formFilteredData;

    private List<RegionInfo> regionInfos = null;
    private List<DivisionInfo> divisionInfos = null;
    private List<DistrictInfo> districtInfos = null;
    private List<TownInfo> townInfos = null;
    private List<SubTownInfo> subTownInfos = null;

    SharedPreferences sharedPreferences;
    String  defaultValue = null;

    private boolean setLocationListener;
    private DDSelectedCallback DDCallback;

    private ScrollView mainScrollView;
    private boolean isSearchFilter;

    private PFADDACTV pfaddBizSizeACTV;
    private List<FormSectionInfo> foodLabSections;

    private FormFieldsHideShow formFieldsHideShow;

    /**
     * @param mContext          {@link Context}
     * @param pfaViewsCallbacks PFAViewsCallbacks interface
     */
    public CustomViewCreate(Context mContext, PFAViewsCallbacks pfaViewsCallbacks) {
        this(mContext, pfaViewsCallbacks, null);
    }

    /**
     * @param mContext          Context
     * @param pfaViewsCallbacks PFAViewsCallbacks interface
     * @param formFilteredData  FormDataInfo
     */
    public CustomViewCreate(Context mContext, PFAViewsCallbacks pfaViewsCallbacks, HashMap<String, List<FormDataInfo>> formFilteredData) {
        super(mContext);
        this.pfaViewsCallbacks = pfaViewsCallbacks;
        this.formFilteredData = formFilteredData;


        formFieldsHideShow = new FormFieldsHideShow(mContext);
    }

    /**
     * This is the method that actually creates the views.
     * It always needs the section info, parent view(linearlayout in which all the views created dynamically added) and what the sections required
     *
     * @param formSectionInfo FormSectionInfo
     * @param parentView      {@link LinearLayout}
     * @param sectionRequired HashMap<String, HashMap<String, Boolean>>
     */
    @SuppressLint({"ClickableViewAccessibility", "RtlHardcoded"})
    public void createViews(final FormSectionInfo formSectionInfo, final LinearLayout parentView, final HashMap<String, HashMap<String, Boolean>> sectionRequired, final PFAViewsCallbacks pfaViewsCallbacks2, boolean setLocationListener, final ScrollView mainScrollView) {

        this.setLocationListener = setLocationListener;
        this.mainScrollView = mainScrollView;
        if (pfaViewsCallbacks2 != null)
            this.pfaViewsCallbacks = pfaViewsCallbacks2;
        final LayoutInflater inflater = LayoutInflater.from(mContext);

        if (!formSectionInfo.getSection_name().equals("")) {
            PFASectionTV pfaSectionTV = new PFASectionTV(mContext, null, isEnglishLang() ? formSectionInfo.getSection_name() : formSectionInfo.getSection_nameUrdu());
            pfaSectionTV.setHeadingTextStyle(true);
            parentView.addView(pfaSectionTV);
        }

//        sharedPreferences  = PreferenceManager
//                .getDefaultSharedPreferences(mContext);
//        defaultValue = sharedPreferences.getString("defaultVAL", "");

        final HashMap<String, Boolean> fieldsReq = new HashMap<>();

//All the fields needed to be created. They are sorted on the bases of order param of FormFieldInfo
        final List<FormFieldInfo> fields = formSectionInfo.getFields();
//        Sorting based on order param of FormFieldInfo
        Collections.sort(fields, new Comparator<FormFieldInfo>() {
            @Override
            public int compare(FormFieldInfo o1, FormFieldInfo o2) {
                int order1 = o1.getOrder();
                int order2 = o2.getOrder();

                return AppUtils.compareInts(order1, order2);
            }
        });


        final LinearLayout addDynamicSubItem = new LinearLayout(mContext);
        LinearLayout.LayoutParams dynamicSubItemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addDynamicSubItem.setOrientation(LinearLayout.VERTICAL);
        addDynamicSubItem.setLayoutParams(dynamicSubItemParams);

        if (formSectionInfo.getAdd_new() != null) {
            addDynamicSubItem.setTag(formSectionInfo.getAdd_new());
            setAddDynamicItemJSONObj(formSectionInfo.getAdd_new(), null, new CreateViewCallback() {
                @Override
                public void createAddView(FormSectionInfo formSectionInfo, LinearLayout parentView) {
                    createViews(formSectionInfo, parentView, sectionRequired, pfaViewsCallbacks2, false, null);
                }
            });
        }

//        If attachment is  application_image|| application_cnic_image || application_business_image, then all these images are added to horizontal linear layout
        LinearLayout imageLayout = new LinearLayout(mContext);
        ViewGroup.LayoutParams imagesParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageLayout.setOrientation(LinearLayout.HORIZONTAL);
        imageLayout.setLayoutParams(imagesParams);
//        attachment HORIZONTAL Linear layout end

//         Create and Add the views to parent layout (@parentView)
        for (int fieldCount = 0; fieldCount < fields.size(); fieldCount++) {
            final FormFieldInfo fieldInfo = fields.get(fieldCount);
            PFASectionTV pfaSectionTV = new PFASectionTV(mContext, null, isEnglishLang() ? fieldInfo.getValue() : fieldInfo.getValueUrdu());
            pfaSectionTV.setSmallTextStyle();

//             if button, location_fields or get_code_button then do not add the label
            if (!fieldInfo.getField_type().equals("button") && (!fieldInfo.getField_type().equalsIgnoreCase("location_fields"))
                    && (!fieldInfo.getField_type().equalsIgnoreCase("get_code_button"))) {
                String txt = pfaSectionTV.getText().toString();
                if (fieldInfo.isRequired()) {
                    txt = "<font color='red'>*</font> " + txt;
                    pfaSectionTV.setHeadingTextStyle(false);
                }

                pfaSectionTV.setText(Html.fromHtml(txt));
            }

            if (fieldInfo.getValue() == null || fieldInfo.getValue().isEmpty()) {
                pfaSectionTV.setVisibility(GONE);
            }

            applyFont(pfaSectionTV, FONTS.HelveticaNeueMedium);
            fieldsReq.put(fieldInfo.getField_name(), fieldInfo.isRequired());

            PFATextInputLayout textInputLayout = new PFATextInputLayout(mContext, fieldInfo);

//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(72));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textInputLayout.setMinimumHeight(convertDpToPixel(72));
            params.setMargins(0, convertDpToPixel(10), 0, 0);
            if (!isEnglishLang())
                textInputLayout.setPadding(0, 0, convertDpToPixel(20), 0);
            textInputLayout.setLayoutParams(params);

//                get_code_button, text, textarea, numeric, cnic ,phone, email, dropdown , radiogroup, checkbox, label, date, imageView,  button,  autoSearch, location_fields
            switch (fieldInfo.getField_type()) {
                case "heading":
                    createViewHeading(parentView, inflater, fieldInfo);
                    break;

                case "get_code_button":
                    createViewGetCodeButton(parentView, fieldInfo);
                    break;

                case "googlemap":
                    createGoogleMap(parentView, inflater, fieldInfo);
                    break;

                case "radiogroup":
                    createRadioGroup(pfaSectionTV, fieldInfo, parentView, fieldsReq);
                    break;

                case "checkbox":
                    parentView.addView(pfaSectionTV);
                    PFACheckboxGroup pfaCheckboxGroup = new PFACheckboxGroup(mContext, fieldInfo, formFilteredData);
                    parentView.addView(pfaCheckboxGroup.getCheckboxLL());
                    if (fieldInfo.isInvisible()) {
                        pfaSectionTV.setVisibility(GONE);
                        pfaCheckboxGroup.getCheckboxLL().setVisibility(GONE);
                    }
                    break;

                case "label":
                    pfaSectionTV = new PFASectionTV(mContext, fieldInfo.getField_name(), isEnglishLang() ? fieldInfo.getValue() : fieldInfo.getValueUrdu());
                    pfaSectionTV.setSmallTextStyle();
                    parentView.addView(pfaSectionTV);

                    if (fieldInfo.isClickable()) {
                        pfaSectionTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (pfaViewsCallbacks != null)
                                    pfaViewsCallbacks.onLabelViewClicked((PFASectionTV) v);
                            }
                        });
                    }

                    if (fieldInfo.isInvisible()) {
                        pfaSectionTV.setVisibility(GONE);
                    }
                    break;

                case "imageView":
                    createViewImageView(parentView, inflater, formSectionInfo, fieldInfo, imageLayout, fields, sectionRequired, pfaViewsCallbacks2, fieldCount);
                    break;

                case "button":
                    createGenButton(formSectionInfo, fieldInfo, parentView, imageLayout, addDynamicSubItem, sectionRequired, pfaViewsCallbacks2);
                    break;

                case "autoSearch":
                    createAutoSearchView(parentView, inflater, fieldInfo);
                    break;

                case "dropdown":
                    createViewDropdown(fieldInfo, parentView, inflater);

                    if (fieldInfo.getField_name().equalsIgnoreCase(DD_FOOD_LAB_TEST)) {
                        final LinearLayout foodLabTestsLL = new LinearLayout(mContext);
                        foodLabTestsLL.setTag(DD_FOOD_LAB_TEST + "LL");

                        dynamicSubItemParams.setMargins(convertDpToPixel(7), convertDpToPixel(7), convertDpToPixel(7), convertDpToPixel(7));
                        foodLabTestsLL.setLayoutParams(dynamicSubItemParams);

                        parentView.addView(foodLabTestsLL);

                        if (foodLabSections != null) {
                            setFoodLabViews(parentView, foodLabSections, sectionRequired, mainScrollView);
                        }
                    }

                    break;

                case "location_fields":
                    createViewLocationFields(fieldInfo, params, parentView, inflater, fieldsReq);
                    break;

                case "abc":
                    createViewABC(fieldInfo, parentView, inflater);
                    break;

                default:
//                    for all edittext fields this function creates views
                    createViewEditText(parentView, fieldInfo, textInputLayout);
                    break;
            }
        }
        sectionRequired.put(formSectionInfo.getSection_id(), fieldsReq);
    }
//plus
    @SuppressLint("RtlHardcoded")
    private void createGenButton(final FormSectionInfo formSectionInfo, final FormFieldInfo fieldInfo, LinearLayout parentView, LinearLayout imageLayout, final LinearLayout addDynamicSubItem, final HashMap<String, HashMap<String, Boolean>> sectionRequired, final PFAViewsCallbacks pfaViewsCallbacks2) {
        if (fieldInfo.getField_name().equalsIgnoreCase("submit")) {
            if (imageLayout.getChildCount() > 0)
                parentView.addView(imageLayout);

            if (formSectionInfo.getAdd_new() != null) {
                parentView.addView(addDynamicSubItem);

                ImageButton imageButton = new ImageButton(mContext);
                imageButton.setImageResource(R.mipmap.add_new);
                imageButton.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));

                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                btnParams.gravity = Gravity.RIGHT | Gravity.END;
                imageButton.setLayoutParams(btnParams);

                parentView.addView(imageButton);

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (add_label_application_form == null) {

                            setAddDynamicItemJSONObj(formSectionInfo.getAdd_new(), addDynamicSubItem, new CreateViewCallback() {
                                @Override
                                public void createAddView(FormSectionInfo formSectionInfo, LinearLayout parentView) {
                                    createViews(formSectionInfo, parentView, sectionRequired, pfaViewsCallbacks2, false, null);
                                }
                            });

                        } else {
                            setViewForms(addDynamicSubItem);
                        }
                    }
                });
            }
        }
//plus2
        final PFAButton button = new PFAButton(mContext, fieldInfo, R.style.white_15_sp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pfaViewsCallbacks != null)
                    pfaViewsCallbacks.onButtonCLicked(view);
            }
        });

        parentView.addView(button);
        if (fieldInfo.isInvisible()) {
            button.setVisibility(GONE);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void createGoogleMap(LinearLayout parentView, LayoutInflater inflater, FormFieldInfo fieldInfo) {
        @SuppressLint("InflateParams") RelativeLayout map_detail_ll = (RelativeLayout) inflater.inflate(R.layout.map_detail_ll, null, false);

        ImageView transparent_image = map_detail_ll.findViewById(R.id.transparent_image);

        LinearLayout.LayoutParams map_detail_llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(250));
        map_detail_ll.setLayoutParams(map_detail_llParams);

        parentView.addView(map_detail_ll);
        if (fieldInfo.getData() != null && fieldInfo.getData().size() > 0) {

            ArrayList<String> latLngs = new ArrayList<>();
            latLngs.add(fieldInfo.getData().get(0).getKey());

            MenuMapFragment menuItemFragment = MenuMapFragment.newInstance(null, latLngs);
            replaceFragment(menuItemFragment);
        }

        if (fieldInfo.isInvisible()) {
            map_detail_ll.setVisibility(GONE);
        }

        transparent_image.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        if (mainScrollView != null)
                            mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        if (mainScrollView != null)
                            mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (mainScrollView != null)
                            mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

    }

    private void createRadioGroup(final PFASectionTV pfaSectionTV, final FormFieldInfo fieldInfo, final LinearLayout parentView, final HashMap<String, Boolean> fieldsReq) {
        // reset string to remove star
        pfaSectionTV.setHeadingTextStyle(false);
        pfaSectionTV.setText(Html.fromHtml(isEnglishLang() ? fieldInfo.getValue() : fieldInfo.getValueUrdu()));
        pfaSectionTV.setPadding(convertDpToPixel(0), convertDpToPixel(10), isEnglishLang() ? convertDpToPixel(0) : convertDpToPixel(20), convertDpToPixel(0));
        parentView.addView(pfaSectionTV);
        PFARadioGroup radioGroup = new PFARadioGroup(mContext, fieldInfo, formFilteredData, new RGSelectCallback() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                PFARadioGroup pfaRadioGroup = (PFARadioGroup) group;

                if (pfaRadioGroup.getTag().toString().equalsIgnoreCase("complain_department")) {
                    PFAEditText business_name = parentView.findViewWithTag("business_name");
                    PFAEditText business_address = parentView.findViewWithTag("business_address");
                    PFADDACTV complain_category = parentView.findViewWithTag("complain_category");

                    PFATextInputLayout business_nameTIL = parentView.findViewWithTag("business_name01");
                    PFATextInputLayout business_addressTIL = parentView.findViewWithTag("business_address01");
                    FrameLayout complain_categoryFL = parentView.findViewWithTag("complain_category01");

                    if (complain_categoryFL != null) {
                        PFATextInputLayout textInputLayout = complain_categoryFL.findViewById(R.id.pfaAdd_IPL);
                        textInputLayout.setBackground(mContext.getResources().getDrawable(isEnglishLang() ? R.mipmap.spinner_required : R.mipmap.ur_spinner_required));
                    }

                    business_nameTIL.setBackground(mContext.getResources().getDrawable(isEnglishLang() ? R.mipmap.text_bg_star : R.mipmap.ur_text_bg_star));
                    business_addressTIL.setBackground(mContext.getResources().getDrawable(isEnglishLang() ? R.mipmap.text_bg_star : R.mipmap.ur_text_bg_star));

                    if (pfaRadioGroup.getSelectedRB().getFormDataInfo().getKey().equalsIgnoreCase("business")) {
                        business_nameTIL.setVisibility(VISIBLE);
                        business_addressTIL.setVisibility(VISIBLE);
                        if (complain_categoryFL != null)
                            complain_categoryFL.setVisibility(VISIBLE);

                        business_name.getFormFieldInfo().setRequired(true);
                        business_address.getFormFieldInfo().setRequired(true);


                        fieldsReq.put(business_name.getTag().toString(), true);
                        fieldsReq.put(business_address.getTag().toString(), true);

                        if (complain_category != null) {
                            complain_category.formFieldInfo.setRequired(true);
                            fieldsReq.put(complain_category.getTag().toString(), true);
                        }

                    } else {
                        business_nameTIL.setVisibility(GONE);
                        business_addressTIL.setVisibility(GONE);
                        if (complain_categoryFL != null)
                            complain_categoryFL.setVisibility(GONE);

                        business_name.getFormFieldInfo().setRequired(false);
                        business_address.getFormFieldInfo().setRequired(false);


                        fieldsReq.remove(business_name.getTag().toString());
                        fieldsReq.remove(business_address.getTag().toString());
                        if (complain_category != null) {
                            complain_category.formFieldInfo.setRequired(false);
                            fieldsReq.remove(complain_category.getTag().toString());
                        }
                    }
                }
            }
        });
        parentView.addView(radioGroup);

        if (fieldInfo.isInvisible()) {
            pfaSectionTV.setVisibility(GONE);
            radioGroup.setVisibility(GONE);
        }
    }


    private void createViewHeading(final LinearLayout parentView, LayoutInflater inflater, final FormFieldInfo fieldInfo) {
        @SuppressLint("InflateParams") RelativeLayout pfa_detail_heading = (RelativeLayout) inflater.inflate(R.layout.pfa_detail_heading, null, false);

        RelativeLayout.LayoutParams rlLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pfa_detail_heading.setMinimumHeight(convertDpToPixel(65));
        pfa_detail_heading.setLayoutParams(rlLayoutParams);

        TextView headingLblTV = pfa_detail_heading.findViewById(R.id.lblTV);
        headingLblTV.setText(Html.fromHtml(isEnglishLang() ? fieldInfo.getValue() : fieldInfo.getValueUrdu()));
        applyFont(headingLblTV, FONTS.HelveticaNeueBold);


        if (fieldInfo.getClickable_text() != null && (!fieldInfo.getClickable_text().isEmpty())) {
            final TextView clickableTV = pfa_detail_heading.findViewById(R.id.clickableTV);
            clickableTV.setText(Html.fromHtml(isEnglishLang() ? fieldInfo.getClickable_text() : fieldInfo.getClickable_textUrdu()));
            applyStyle(fieldInfo.getFont_style(), "m", fieldInfo.getFont_color(), clickableTV);
            if (fieldInfo.getClickable_text().equalsIgnoreCase("Edit")) {
                clickableTV.setTextSize(COMPLEX_UNIT_SP, 17);
            }

            clickableTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_URL_TO_CALL, "" + fieldInfo.getAPI_URL());
                    if (fieldInfo.getField_name().equalsIgnoreCase(BUSINESS_LOCATION_FIELD)) {
                        bundle.putString(BUSINESS_LOCATION_FIELD, fieldInfo.getMap_info());
                        startNewActivity(MapsActivity.class, bundle, false);
                    } else {
//                        startNewActivity(PFADetailActivity.class, bundle, false);
                        /////////////
                        HttpService httpService = new HttpService(mContext);

                        httpService.getListsData(fieldInfo.getAPI_URL(), new HashMap<String, String>(), new HttpResponseCallback() {
                            @Override
                            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                if(response!=null)
                                {
                                    bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                                    startNewActivity(PFADetailActivity.class, bundle, false);
                                }
                                else
                                {
                                    showMsgDialog("No data received from server", null);
                                }

                            }
                        }, true);
                        /////////////

                    }
                }
            });
        }
        parentView.addView(pfa_detail_heading);

        if (fieldInfo.isInvisible()) {
            pfa_detail_heading.setVisibility(GONE);
        }
    }

    private void createViewGetCodeButton(final LinearLayout parentView, final FormFieldInfo fieldInfo) {
        AppConst.codeVerified = false;
        final VerifyFBOLayout verifyFBOLayout = new VerifyFBOLayout(mContext, fieldInfo, pfaViewsCallbacks, new CheckUserCallback() {
            @Override
            public void getExistingUser(JSONArray jsonArray) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);

                    if (parentView.findViewWithTag(jsonObject.optString("key")) != null && parentView.findViewWithTag(jsonObject.optString("key")) instanceof PFAEditText) {
                        PFAEditText pfaEditText = parentView.findViewWithTag(jsonObject.optString("key"));
                        pfaEditText.setText(Html.fromHtml(jsonObject.optString("value")));

                        if (!jsonObject.optBoolean("is_editable")) {
                            pfaEditText.setEnabled(false);
                            pfaEditText.setClickable(false);
                            pfaEditText.setFocusable(false);

                            pfaEditText.setBackgroundColor(mContext.getResources().getColor(R.color.chat_list_footer_bg));

                        }
                    }
                }

                AppConst.codeVerified = true;
            }
        });
        verifyFBOLayout.setTag(fieldInfo.getField_name());

        parentView.addView(verifyFBOLayout);

        if (fieldInfo.isInvisible()) {
            verifyFBOLayout.setVisibility(GONE);
        }

    }

    private void setSearchOption(PFAEditText pfaEditText, final ViewGroup parentView, boolean isSearchFilter) {
        if (!isSearchFilter)
            return;
        pfaEditText.setSearchOption(new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {
                if (parentView.findViewWithTag("submit") != null)
                    parentView.findViewWithTag("submit").performClick();

            }
        });
    }

    private void createViewEditText(final LinearLayout parentView, final FormFieldInfo fieldInfo, PFATextInputLayout textInputLayout) {

        textInputLayout.setTag(fieldInfo.getField_name() + "01");

        switch (fieldInfo.getField_type()) {
            case "text":
            case "searchkeytext":
            case "cnic":

                PFAEditText pfaEditText = new PFAEditText(mContext, fieldInfo, formFilteredData);
                pfaEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

                textInputLayout.addView(pfaEditText);
                parentView.addView(textInputLayout);

                pfaEditText.addTextInputLayout(textInputLayout);

                if (fieldInfo.isInvisible()) {
                    textInputLayout.setVisibility(GONE);
                }

                setSearchOption(pfaEditText, parentView, isSearchFilter);
                break;

            case "textarea":

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, convertDpToPixel(10), 0, 0);
                textInputLayout.setLayoutParams(params);

                final PFAEditText pfa1EditText = new PFAEditText(mContext, fieldInfo, formFilteredData);
                pfa1EditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

                textInputLayout.addView(pfa1EditText);
                parentView.addView(textInputLayout);

                pfa1EditText.addTextInputLayout(textInputLayout);

                if (fieldInfo.isInvisible()) {
                    textInputLayout.setVisibility(GONE);
                } else {
                    textInputLayout.setVisibility(VISIBLE);
                }

                setSearchOption(pfa1EditText, parentView, isSearchFilter);

                break;
            case "numeric":
                PFAEditText pfaEditText2 = new PFAEditText(mContext, fieldInfo, formFilteredData);
                pfaEditText2.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                pfaEditText2.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

                textInputLayout.addView(pfaEditText2);
                parentView.addView(textInputLayout);

                pfaEditText2.addTextInputLayout(textInputLayout);

                if (fieldInfo.isInvisible()) {
                    textInputLayout.setVisibility(GONE);
                }

                setSearchOption(pfaEditText2, parentView, isSearchFilter);

                break;
            //                pfaEditText3.setKeyListener(DigitsKeyListener.getInstance("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-"));
//                pfaEditText3.setKeyListener(DigitsKeyListener.getInstance("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"));


            case "phone":
                PFAEditText pfaEditText4 = new PFAEditText(mContext, fieldInfo, formFilteredData);
                pfaEditText4.setRawInputType(InputType.TYPE_CLASS_PHONE);
                pfaEditText4.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

                textInputLayout.addView(pfaEditText4);
                parentView.addView(textInputLayout);

                if (fieldInfo.isClickable()) {
                    pfaEditText4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (fieldInfo.getData() != null && fieldInfo.getData().size() > 0)
                                doPhoneCall(fieldInfo.getData().get(0).getValue());
                        }
                    });
                }

                pfaEditText4.addTextInputLayout(textInputLayout);

                if (fieldInfo.isInvisible()) {
                    textInputLayout.setVisibility(GONE);
                }
                setSearchOption(pfaEditText4, parentView, isSearchFilter);
                break;
            case "email":
                PFAEditText pfaEditText5 = new PFAEditText(mContext, fieldInfo, formFilteredData);
                pfaEditText5.setRawInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                textInputLayout.addView(pfaEditText5);
                parentView.addView(textInputLayout);

                pfaEditText5.addTextInputLayout(textInputLayout);

                if (fieldInfo.isInvisible()) {
                    textInputLayout.setVisibility(GONE);
                }
                setSearchOption(pfaEditText5, parentView, isSearchFilter);
                break;
            case "date":
                final PFAEditText pfaDateET = new PFAEditText(mContext, fieldInfo, formFilteredData);
                pfaDateET.setRawInputType(InputType.TYPE_CLASS_TEXT);
                pfaDateET.setKeyListener(null);
                pfaDateET.setFocusable(false);

                final PFATextInputLayout finalTextInputLayout = textInputLayout;
                pfaDateET.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DateCustomDialog.showDatePickerDialog(mContext, new GetDateCallback() {
                            @Override
                            public void onDateSelected(int day, int month, int year) {
                                if (day == -1 || month == -1 || year == -1) {
                                    pfaDateET.setText("");
                                } else
                                    pfaDateET.setText((new CustomDateUtils().getDateString(day, month, year)));

                                finalTextInputLayout.setError(null);
                                pfaDateET.clearFocus();
                            }
                        }, fieldInfo.getCalendarType(), fieldInfo.getDate_from(), fieldInfo.getDate_to(), pfaDateET.getText().toString());
                    }
                });

                textInputLayout.addView(pfaDateET);
                parentView.addView(textInputLayout);

                pfaDateET.addTextInputLayout(textInputLayout);

                if (fieldInfo.isInvisible()) {
                    textInputLayout.setVisibility(GONE);
                }

                setSearchOption(pfaDateET, parentView, isSearchFilter);
                break;
        }
    }

    private void createAutoSearchView(final LinearLayout parentView, LayoutInflater inflater, final FormFieldInfo fieldInfo) {
        @SuppressLint("InflateParams") FrameLayout autoSearchLayout = (FrameLayout) inflater.inflate(R.layout.pfa_search_actv, null, false);

        FrameLayout.LayoutParams searchFLParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        searchFLParams.setMargins(0, convertDpToPixel(10), 0, 0);
        autoSearchLayout.setLayoutParams(searchFLParams);

        PFATextInputLayout textInputLayout = autoSearchLayout.findViewById(R.id.pfaSearchTIL);
        textInputLayout.setProperties(fieldInfo);

        final ImageButton autoSearchClearBtn = autoSearchLayout.findViewById(R.id.clearImgBtn);

        final PFASearchACTV autoSearchPFAET = autoSearchLayout.findViewById(R.id.pfaSearchACTV);
        textInputLayout.setProperties(fieldInfo);
        autoSearchPFAET.setTag(fieldInfo.getField_name());

        setSpinnerFonts(fieldInfo, autoSearchPFAET, textInputLayout);
        parentView.addView(autoSearchLayout);
        autoSearchLayout.setTag(fieldInfo.getField_name() + "_parent");

        if (fieldInfo.getData() != null && fieldInfo.getData().size() > 0) {
            autoSearchPFAET.setText(Html.fromHtml(fieldInfo.getData().get(0).getValue()));
        }
        autoSearchPFAET.setTextWatcher(fieldInfo);

        if (fieldInfo.isInvisible()) {
            autoSearchLayout.setVisibility(GONE);
        }

        autoSearchPFAET.addTextChangedListener(new PFATextWatcher(new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {
                if (message != null && message.length() > 0) {
                    autoSearchClearBtn.setVisibility(VISIBLE);
                }
            }
        }));
        autoSearchClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoSearchClearBtn.setVisibility(GONE);
                autoSearchPFAET.setText("");
                autoSearchPFAET.setPfaSearchInfo(null);
                INSPECTION_ID = null;

                if (pfaViewsCallbacks != null)
                    pfaViewsCallbacks.onDropdownItemSelected(null, null);
                autoSearchPFAET.clearFocus();

                setSearchBizData(false, parentView, pfaViewsCallbacks, CustomViewCreate.this);
            }
        });

//        if (fieldInfo.isNotEditable() || fieldInfo.isHide_clear()) {
//            autoSearchClearBtn.setVisibility(GONE);
//        }

    }

    private void createViewImageView(final LinearLayout parentView, LayoutInflater inflater, final FormSectionInfo formSectionInfo, final FormFieldInfo fieldInfo, LinearLayout imageLayout, final List<FormFieldInfo> fields, final HashMap<String, HashMap<String, Boolean>> sectionRequired, final PFAViewsCallbacks pfaViewsCallbacks2, int fieldCount) {


        @SuppressLint("InflateParams") final LinearLayout img_attachment_ll = (LinearLayout) inflater.inflate(R.layout.img_attachment_ll, null, false);

        img_attachment_ll.setTag(fieldInfo.getField_name() + "_parent");

        TextView attachmentLblTV = img_attachment_ll.findViewById(R.id.attachmentLblTV);
        TextView selectImgTV = img_attachment_ll.findViewById(R.id.selectImgTV);

        applyFont(attachmentLblTV, FONTS.HelveticaNeue);
        applyFont(selectImgTV, FONTS.HelveticaNeue);

        final CustomNetworkImageView attachmentCNIV = img_attachment_ll.findViewById(R.id.attachmentCNIV);
        attachmentCNIV.setFormFieldInfo(fieldInfo);

        ImageButton addMoreImgBtn = img_attachment_ll.findViewById(R.id.addMoreImgBtn);
        final ImageButton deleteImgBtn = img_attachment_ll.findViewById(R.id.deleteImgBtn);

        if (fieldInfo.getField_name().equalsIgnoreCase("application_image") || fieldInfo.getField_name().equalsIgnoreCase("application_cnic_image") ||
                fieldInfo.getField_name().equalsIgnoreCase("application_business_image")) {
            LinearLayout.LayoutParams imgLLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imgLLParams.weight = 1;
            img_attachment_ll.setLayoutParams(imgLLParams);

            imageLayout.addView(img_attachment_ll);
        } else {
            parentView.addView(img_attachment_ll);
        }


        attachmentCNIV.setTag(fieldInfo.getField_name());

        if (fieldInfo.isClickable()) {
            if (fieldInfo.isRequired()) {
                selectImgTV.setText(Html.fromHtml("<b><font color=\"#EB5757\">" + " *</font> </b> Select"));
            } else {
                selectImgTV.setText(mContext.getString(R.string.select_image));
            }

            attachmentLblTV.setText(Html.fromHtml(isEnglishLang() ? fieldInfo.getValue() : fieldInfo.getValueUrdu()));
        } else {
            attachmentLblTV.setText("Attachment");
            img_attachment_ll.findViewById(R.id.selectImgLblFL).setVisibility(GONE);
        }


        if (fieldInfo.isNotEditable()) {
            deleteImgBtn.setVisibility(GONE);
        }
        if ((fieldInfo.getData() != null && fieldInfo.getData().size() > 0) && (!fieldInfo.getData().get(0).getValue().equals(""))) {
            if (fieldInfo.getData().get(0).getValue().startsWith("http")) {
                attachmentCNIV.setImageUrl(fieldInfo.getData().get(0).getValue(), AppController.getInstance().getImageLoader());
            } else {
                deleteImgBtn.setVisibility(VISIBLE);
                attachmentCNIV.setFileBitmap(fieldInfo.getData().get(0).getValue());
            }
        } else {
            attachmentCNIV.setDrawable(R.mipmap.no_img);
        }

        if (fieldInfo.isInvisible()) {
            img_attachment_ll.setVisibility(GONE);
        }


        deleteImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentCNIV.setImageFile(null);
                attachmentCNIV.setLocalImageBitmap(null);
            }
        });


        printLog("fieldInfo.isAdd_more", "" + (fieldInfo.isAdd_more()));
        if (fieldInfo.isAdd_more()) {
            addMoreImgBtn.setVisibility(VISIBLE);
            addMoreImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PFAViewsUtils pfaViewsUtils = new PFAViewsUtils(mContext);
                    HashMap<String, List<FormDataInfo>> pfaViewsData = pfaViewsUtils.getViewsData(parentView, false);

                    for (int x = 0; x < fields.size(); x++) {
                        String fieldType = fields.get(x).getField_type();

                        if (fieldType.equalsIgnoreCase("radioGroup") || fieldType.equalsIgnoreCase("dropdown")) {
                            if (pfaViewsData.get(fields.get(x).getField_name()) != null && pfaViewsData.get(fields.get(x).getField_name()).size() > 0)
                                fields.get(x).setDefault_value(pfaViewsData.get(fields.get(x).getField_name()).get(0).getKey());
                        } else if (fieldType.equalsIgnoreCase("location_fields")) {

                            fields.get(x).setData(pfaViewsData.get(fields.get(x).getField_name()));
                            fields.get(x).setRequired(true);

                        } else {
                            fields.get(x).setData(pfaViewsData.get(fields.get(x).getField_name()));
                        }
                    }

                    FormFieldInfo addMoreFieldInfo = copyFormFieldInfo(fieldInfo, fields.size());

                    fields.add(addMoreFieldInfo);

                    parentView.removeAllViews();
                    formSectionInfo.setFields(fields);

                    createViews(formSectionInfo, parentView, sectionRequired, pfaViewsCallbacks2, true, mainScrollView);

                }
            });
        }

        if (fieldInfo.isDeleteImg()) {
            if (isEnglishLang()) {
                attachmentLblTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.delete, 0);
            } else {
                attachmentLblTV.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.delete, 0, 0, 0);
            }

            final int finalFieldCount = fieldCount;
            attachmentLblTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PFAViewsUtils pfaViewsUtils = new PFAViewsUtils(mContext);
                    HashMap<String, List<FormDataInfo>> pfaViewsData = pfaViewsUtils.getViewsData(parentView, false);
                    for (int x = 0; x < fields.size(); x++) {
                        String fieldType = fields.get(x).getField_type();

                        if (fieldType.equalsIgnoreCase(String.valueOf(FIELD_TYPE.radiogroup)) || fieldType.equalsIgnoreCase(String.valueOf(FIELD_TYPE.dropdown))) {
                            if (pfaViewsData.get(fields.get(x).getField_name()) != null && pfaViewsData.get(fields.get(x).getField_name()).size() > 0)
                                fields.get(x).setDefault_value(pfaViewsData.get(fields.get(x).getField_name()).get(0).getKey());
                        } else if (fieldType.equalsIgnoreCase(String.valueOf(FIELD_TYPE.location_fields))) {

                            fields.get(x).setData(pfaViewsData.get(fields.get(x).getField_name()));
                            fields.get(x).setRequired(true);

                        } else {
                            fields.get(x).setData(pfaViewsData.get(fields.get(x).getField_name()));
                        }
                    }

                    fields.remove(finalFieldCount);

                    List<FormFieldInfo> tempFields = new ArrayList<>(fields);
                    parentView.removeAllViews();

                    formSectionInfo.setFields(tempFields);
                    createViews(formSectionInfo, parentView, sectionRequired, pfaViewsCallbacks2, true, mainScrollView);
                }
            });
        }

        if (fieldInfo.isClickable()) {
            attachmentCNIV.setDeleteImgBtn(deleteImgBtn);
            attachmentCNIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pfaViewsCallbacks != null)
                        pfaViewsCallbacks.showImagePickerDialog(attachmentCNIV);
                }
            });
        } else {
            attachmentCNIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fieldInfo.getIcon() != null && (!fieldInfo.getIcon().isEmpty())) {
                        Bundle bundle = new Bundle();
                        bundle.putString(EXTRA_DOWNLOAD_URL, fieldInfo.getIcon());
                        startNewActivity(ImageGalleryActivity.class, bundle, false);
                    }
                }
            });

        }
    }

    private void createViewDropdown(final FormFieldInfo fieldInfo, final LinearLayout parentView, LayoutInflater inflater) {
        if (fieldInfo.isMultiple()) {
            PFAMultiSpinner pfaMultiSpinner = new PFAMultiSpinner(mContext, fieldInfo, new MultiSpinnerListener() {
                @Override
                public void onItemsSelected(boolean[] selected, FormFieldInfo formFieldInfo) {
                }
            }, formFilteredData);
            parentView.addView(pfaMultiSpinner);

        } else {
            @SuppressLint("InflateParams") FrameLayout pfaddLL = (FrameLayout) inflater.inflate(R.layout.pfa_dropdown_actv, null, false);

            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.setMargins(0, convertDpToPixel(10), 0, 0);
            pfaddLL.setLayoutParams(params1);

            pfaddLL.setTag((fieldInfo.getField_name()) + "01");

            PFATextInputLayout textInputLayout = pfaddLL.findViewById(R.id.pfaAdd_IPL);
            textInputLayout.setProperties(fieldInfo);

            final ImageButton clearImgBtn = pfaddLL.findViewById(R.id.clearImgBtn);

            final PFADDACTV pfa_dd_actv = pfaddLL.findViewById(R.id.pfa_dd_actv);
            pfa_dd_actv.setText("");

            pfa_dd_actv.setHint(isEnglishLang() ? fieldInfo.getValue() : fieldInfo.getValueUrdu());
            pfa_dd_actv.setDDCallback(DDCallback);
            pfa_dd_actv.setProperties(new WhichItemClicked() {
                @Override
                public void whichItemClicked(String id) {
                    printLog("createViewDropdown", "whichItemClicked=>" + id);
                }

                @Override
                public void downloadInspection(String downloadUrl, int position) {
                    printLog("createViewDropdown", "downloadInspection  position=> " + downloadUrl + " position=> " + position);

                }

                @Override
                public void deleteRecordAPICall(String deleteUrl, int position) {

                }
            }, fieldInfo, formFilteredData);
            pfa_dd_actv.setPfaddLL(pfaddLL);

            parentView.addView(pfaddLL);

            pfa_dd_actv.populateData();
            setSpinnerFonts(fieldInfo, pfa_dd_actv, textInputLayout);

            if (fieldInfo.isInvisible()) {
                pfaddLL.setVisibility(GONE);
            }

            pfa_dd_actv.addTextChangedListener(new PFATextWatcher(new SendMessageCallback() {
                @Override
                public void sendMsg(String message) {
                    if (message != null && message.length() > 0 && pfa_dd_actv.getVisibility() == VISIBLE && (!fieldInfo.isNotEditable())) {

                        if (!fieldInfo.isHide_clear())
                            clearImgBtn.setVisibility(VISIBLE);
                    }

                    pfa_dd_actv.clearFocus();
                }
            }));


            clearImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearImgBtn.setVisibility(GONE);
                    pfa_dd_actv.setText("");
                    pfa_dd_actv.setSelectedValues(null);

                    if (pfaViewsCallbacks != null)
                        pfaViewsCallbacks.onDropdownItemSelected(null, pfa_dd_actv.formFieldInfo.getField_name());
                    pfa_dd_actv.clearFocus();
                    clearFocusOfAllViews(parentView);

                    if (pfa_dd_actv.formFieldInfo.getField_name() != null && (pfa_dd_actv.formFieldInfo.getField_name().equalsIgnoreCase(DD_STATUS))) {

                        if (pfaddBizSizeACTV == null && parentView.findViewWithTag(DD_BIZ_SIZE) != null) {
                            pfaddBizSizeACTV = parentView.findViewWithTag(DD_BIZ_SIZE);
                        }

                        if (pfaddBizSizeACTV != null) {
                            setPFABizSizeACTVStatus(false, GONE);
                        }
                    }

                }
            });

            pfa_dd_actv.setTextInputLayout(textInputLayout);

            if (!pfa_dd_actv.getText().toString().isEmpty()) {
                clearImgBtn.setVisibility(VISIBLE);
            }

            if (fieldInfo.isNotEditable() || fieldInfo.isHide_clear()) {
                clearImgBtn.setVisibility(GONE);
            }
        }

    }

    private void createViewLocationFields(FormFieldInfo fieldInfo, LinearLayout.LayoutParams params, final LinearLayout parentView, LayoutInflater inflater, HashMap<String, Boolean> fieldsReq) {
        if (fieldInfo.getDefault_locations() != null) {

            regionInfos = getRegionsList();
            divisionInfos = getDivision("" + fieldInfo.getDefault_locations().getRegion_id());
            districtInfos = getDistrictInfos("" + fieldInfo.getDefault_locations().getDivision_id());
            townInfos = fieldInfo.getDefault_locations().isShow_all_town() ? getTownInfos(null) : getTownInfos("" + fieldInfo.getDefault_locations().getDistrict_id());
            subTownInfos = getSubTownInfos("" + fieldInfo.getDefault_locations().getTown_id());

            if (!setLocationListener) {
                List<String> whichLocationToHide = new ArrayList<>();
                if (fieldInfo.getDefault_locations().getRegion_id() > 0) {
                    whichLocationToHide.add(REGION_TAG);
                }
                if (fieldInfo.getDefault_locations().getDivision_id() > 0) {
                    whichLocationToHide.add(DIVISION_TAG);
                }
                if (fieldInfo.getDefault_locations().getDistrict_id() > 0) {
                    whichLocationToHide.add(DISTRICT_TAG);
                }
                if (fieldInfo.getDefault_locations().getTown_id() > 0) {
                    if (!fieldInfo.getDefault_locations().isShow_town())
                        whichLocationToHide.add(TOWN_TAG);
                }

                if (fieldInfo.getDefault_locations().getSubtown_id() > 0) {
                    if (!fieldInfo.getDefault_locations().isShow_town())
                        whichLocationToHide.add(SUB_TOWN_TAG);
                }
                fieldInfo.setWhichLocationToHide(whichLocationToHide);
            }

        }

//        if location attribute is needed then its already created pfa_location_include layout
//        is created and it is inflated.
//        pfa_location_include contains AutoCompleteTextViews and they also have their adapters for
//        which location (district / town) is selected
        @SuppressLint("InflateParams") LinearLayout pfa_location_include = (LinearLayout) inflater.inflate(R.layout.pfa_location_include, null, false);
        params.setMargins(0, convertDpToPixel(10), 0, 0);
        regionACTIL = pfa_location_include.findViewById(R.id.regionACTIL);
        regionACTIL.setProperties(null);

        regionACTIL.setLayoutParams(params);

        divisionACTIL = pfa_location_include.findViewById(R.id.divisionACTIL);
        divisionACTIL.setProperties(null);
        divisionACTIL.setLayoutParams(params);

        districtACTIL = pfa_location_include.findViewById(R.id.districtACTIL);
        districtACTIL.setProperties(null);
        districtACTIL.setLayoutParams(params);

        townACTIL = pfa_location_include.findViewById(R.id.townACTIL);
        townACTIL.setProperties(null);
        townACTIL.setLayoutParams(params);

        sub_townACTIL = pfa_location_include.findViewById(R.id.sub_townACTIL);
        sub_townACTIL.setProperties(null);
        sub_townACTIL.setLayoutParams(params);

        regionACTV = pfa_location_include.findViewById(R.id.regionACTV);
        regionACTV.setTag(REGION_TAG);
        setSpinnerFonts(fieldInfo, regionACTV, regionACTIL);
        regionACTV.setTextInputLayout(regionACTIL);

        divisionACTV = pfa_location_include.findViewById(R.id.divisionACTV);
        divisionACTV.setTag(DIVISION_TAG);
        setSpinnerFonts(fieldInfo, divisionACTV, divisionACTIL);
        divisionACTV.setTextInputLayout(divisionACTIL);

        districtACTV = pfa_location_include.findViewById(R.id.districtACTV);
        districtACTV.setTag(DISTRICT_TAG);
        setSpinnerFonts(fieldInfo, districtACTV, districtACTIL);
        districtACTV.setTextInputLayout(districtACTIL);
        if (fieldInfo.getDefault_locations().getDistrict_id() != 0) {
            districtACTV.setSelectedID(fieldInfo.getDefault_locations().getDistrict_id());
        }

        townACTV = pfa_location_include.findViewById(R.id.townACTV);
        townACTV.setTag(TOWN_TAG);
        setSpinnerFonts(fieldInfo, townACTV, townACTIL);
        townACTV.setTextInputLayout(townACTIL);
        if (fieldInfo.getDefault_locations().getTown_id() != 0) {
            townACTV.setSelectedID(fieldInfo.getDefault_locations().getTown_id());
        }

        subTownACTV = pfa_location_include.findViewById(R.id.subTownACTV);
        subTownACTV.setTag(SUB_TOWN_TAG);
        setSpinnerFonts(fieldInfo, subTownACTV, sub_townACTIL);
        subTownACTV.setTextInputLayout(sub_townACTIL);

        if (fieldInfo.getDefault_locations().getSubtown_id() != 0) {
            subTownACTV.setSelectedID(fieldInfo.getDefault_locations().getSubtown_id());
        }

        hideAndShowLocationViews(fieldInfo, fieldsReq); //setLocationListener

        parentView.addView(pfa_location_include);

        if (fieldInfo.isInvisible()) {
            pfa_location_include.setVisibility(GONE);
        }

    }

    private void createViewABC(final FormFieldInfo fieldInfo, final LinearLayout parentView, LayoutInflater inflater) {
        @SuppressLint("InflateParams")
        LinearLayout subviewLL = (LinearLayout) inflater.inflate(R.layout.pfa_form_edittext, null, false);
        LinearLayout.LayoutParams subViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subviewLL.setLayoutParams(subViewLayoutParams);

        LinearLayout imagView = subviewLL.findViewById(R.id.clearfix);
//        SimpleDraweeView img1 = imagView.findViewById(R.id.rightS);
        imagView.setVisibility(GONE);

        PFAEditText abcET = subviewLL.findViewById(R.id.abcET);  //new PFAEditText(mContext, fieldInfo, formFilteredData);
        abcET.setRawInputType(InputType.TYPE_CLASS_TEXT);
        abcET.setData(mContext, fieldInfo, formFilteredData);

        TextView lblTV = subviewLL.findViewById(R.id.lblTV);
        lblTV.setText(Html.fromHtml(isEnglishLang() ? fieldInfo.getValue() : fieldInfo.getValueUrdu()));
        parentView.addView(subviewLL);

        if (fieldInfo.isInvisible()) {
            subviewLL.setVisibility(GONE);
        }

        if (fieldInfo.isClickable() && fieldInfo.getField_name().contains("phonenumber")) {
            abcET.setClickable(true);
            abcET.setEnabled(true);
            abcET.setFocusable(false);
            abcET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fieldInfo.getData() != null && fieldInfo.getData().size() > 0)
                        doPhoneCall(fieldInfo.getData().get(0).getValue());
                }
            });
        } else if (fieldInfo.isClickable() && fieldInfo.getAPI_URL() != null && (!fieldInfo.getAPI_URL().isEmpty())) {

            // open business detail

            abcET.setClickable(true);
            abcET.setEnabled(true);
            abcET.setFocusable(false);
            abcET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_URL_TO_CALL, fieldInfo.getAPI_URL());
                    bundle.putString(EXTRA_ACTIVITY_TITLE, isEnglishLang() ? fieldInfo.getValue() : fieldInfo.getValueUrdu());

                    HttpService httpService = new HttpService(mContext);

                    httpService.getListsData(fieldInfo.getAPI_URL(), new HashMap<String, String>(), new HttpResponseCallback() {
                        @Override
                        public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                            bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                            startNewActivity(PFADetailActivity.class, bundle, false);
                        }
                    }, true);
                }
            });

        }
    }

    private FormFieldInfo copyFormFieldInfo(FormFieldInfo fieldInfo, int size) {

        FormFieldInfo addMoreFieldInfo = new FormFieldInfo();

        addMoreFieldInfo.setField_name(fieldInfo.getField_name() + "" + size);
        addMoreFieldInfo.setAdd_more(false);

        addMoreFieldInfo.setField_type(fieldInfo.getField_type());
        addMoreFieldInfo.setData_type(fieldInfo.getData_type());
        addMoreFieldInfo.setOrder(fieldInfo.getOrder());
        addMoreFieldInfo.setIcon(fieldInfo.getIcon());
        addMoreFieldInfo.setPlaceholder(fieldInfo.getPlaceholder());
        addMoreFieldInfo.setLimit(fieldInfo.getLimit());
        addMoreFieldInfo.setRequired(fieldInfo.isRequired());
        addMoreFieldInfo.setMultiple(fieldInfo.isMultiple());
        addMoreFieldInfo.setValue(fieldInfo.getValue());
        addMoreFieldInfo.setValueUrdu(fieldInfo.getValueUrdu());
        addMoreFieldInfo.setDefault_value(fieldInfo.getDefault_value());
        addMoreFieldInfo.setClickable(fieldInfo.isClickable());
        addMoreFieldInfo.setHorizontal(fieldInfo.isHorizontal());
        addMoreFieldInfo.setAPI_URL(fieldInfo.getAPI_URL());
        addMoreFieldInfo.setAction(fieldInfo.getAction());
        addMoreFieldInfo.setNotEditable(fieldInfo.isNotEditable());
        addMoreFieldInfo.setInvisible(fieldInfo.isInvisible());
        addMoreFieldInfo.setDirection(fieldInfo.getDirection());
        addMoreFieldInfo.setClickable_text(fieldInfo.getClickable_text());
        addMoreFieldInfo.setDefault_locations(fieldInfo.getDefault_locations());
        addMoreFieldInfo.setDeleteImg(true);
//        addMoreFieldInfo.setData(fieldInfo.getData());
        return addMoreFieldInfo;
    }

    private void setRegionACTV(final FormFieldInfo fieldInfo) {

        FormDataInfo formDataInfo = null;

        if (formFilteredData != null && formFilteredData.get(regionACTV.getTag().toString()) != null && formFilteredData.get(regionACTV.getTag().toString()).size() > 0) {
            formDataInfo = formFilteredData.get(regionACTV.getTag().toString()).get(0);
        }

        if (fieldInfo.getDefault_locations().getRegion_id() == 0) {
            regionACTV.setItemClickCallback(new WhichItemClicked() {
                @Override
                public void whichItemClicked(String id) {
                    divisionACTV.setText("");
                    districtACTV.setText("");
                    townACTV.setText("");
                    subTownACTV.setText("");

                    divisionInfos = getDivision(id);
                    if (divisionInfos != null && divisionInfos.size() > 0) {
                        divisionACTIL.setVisibility(View.VISIBLE);
                    }

                    fieldInfo.getDefault_locations().setRegion_id(Integer.parseInt(id));
                    setDivisionACTV(fieldInfo);
                }

                @Override
                public void downloadInspection(String downloadUrl, int position) {

                }

                @Override
                public void deleteRecordAPICall(String deleteUrl, int position) {

                }
            }, formDataInfo, fieldInfo);
        }

        regionACTV.setRegionData(regionInfos);
    }

    private void setDivisionACTV(final FormFieldInfo fieldInfo) {
        FormDataInfo formDataInfo = null;
        if (formFilteredData != null && formFilteredData.get(divisionACTV.getTag().toString()) != null && formFilteredData.get(divisionACTV.getTag().toString()).size() > 0) {
            formDataInfo = formFilteredData.get(divisionACTV.getTag().toString()).get(0);
        }

        if (fieldInfo.getDefault_locations().getDivision_id() == 0) {
            divisionACTV.setItemClickCallback(new WhichItemClicked() {
                @Override
                public void whichItemClicked(String id) {
                    districtACTV.setText("");
                    townACTV.setText("");
                    subTownACTV.setText("");
                    districtInfos = getDistrictInfos(id);

                    if (districtInfos != null && districtInfos.size() > 0) {
                        districtACTIL.setVisibility(View.VISIBLE);
                    }
                    fieldInfo.getDefault_locations().setDivision_id(Integer.parseInt(id));
                    setDistrictACTV(fieldInfo);
                }

                @Override
                public void downloadInspection(String downloadUrl, int position) {

                }

                @Override
                public void deleteRecordAPICall(String deleteUrl, int position) {

                }
            }, formDataInfo, fieldInfo);
        }
        divisionACTV.setDivisionData(divisionInfos);
    }

    private void setDistrictACTV(final FormFieldInfo fieldInfo) {
        FormDataInfo formDataInfo = null;
        if (formFilteredData != null && formFilteredData.get(districtACTV.getTag().toString()) != null && formFilteredData.get(districtACTV.getTag().toString()).size() > 0) {
            formDataInfo = formFilteredData.get(districtACTV.getTag().toString()).get(0);
        }

        if (fieldInfo.getDefault_locations().getDistrict_id() == 0 || setLocationListener) {

            List<DistrictInfo> tempDistrictInfos = getDistrictByDistrictID("" + fieldInfo.getDefault_locations().getDistrict_id());

            if (tempDistrictInfos != null && tempDistrictInfos.size() > 0) {
                fieldInfo.getDefault_locations().setDistrict_name(tempDistrictInfos.get(0).getDistrict_name());
                fieldInfo.getDefault_locations().setDistrict_nameUrdu(tempDistrictInfos.get(0).getDistrict_nameUrdu());
                fieldInfo.getDefault_locations().setDistrict_id(tempDistrictInfos.get(0).getDistrict_id());

                districtACTV.setFormFieldInfo(fieldInfo);
                districtACTV.setText(Html.fromHtml(isEnglishLang() ? tempDistrictInfos.get(0).getDistrict_name() : tempDistrictInfos.get(0).getDistrict_nameUrdu()));

                if (fieldInfo.getDefault_locations().getTown_id() != 0) {
                    setTownsACTV(fieldInfo);
                }
            }

            districtACTV.setItemClickCallback(new WhichItemClicked() {
                @Override
                public void whichItemClicked(String id) {
                    townACTV.setText("");
                    subTownACTV.setText("");

                    townInfos = fieldInfo.getDefault_locations().isShow_all_town() ? getTownInfos(null) : getTownInfos(id);

                    if (townInfos != null && townInfos.size() > 0) {
                        townACTIL.setVisibility(View.VISIBLE);
                    }
                    fieldInfo.getDefault_locations().setDistrict_id(Integer.parseInt(id));
                    setTownsACTV(fieldInfo);
                }

                @Override
                public void downloadInspection(String downloadUrl, int position) {

                }

                @Override
                public void deleteRecordAPICall(String deleteUrl, int position) {

                }
            }, formDataInfo, fieldInfo);
        }
        districtACTV.setDistrictsData(districtInfos);
    }

    private void setTownsACTV(final FormFieldInfo fieldInfo) {
        fieldInfo.setRequired(true);
        FormDataInfo formDataInfo = null;
        if (formFilteredData != null && formFilteredData.get(townACTV.getTag().toString()) != null && formFilteredData.get(townACTV.getTag().toString()).size() > 0) {
            formDataInfo = formFilteredData.get(townACTV.getTag().toString()).get(0);
        }

        if (fieldInfo.getDefault_locations().getTown_id() == 0 || setLocationListener || fieldInfo.getDefault_locations().isShow_town()) {

            List<TownInfo> temTowns = getTownsByTownId("" + fieldInfo.getDefault_locations().getTown_id());

            if (temTowns != null && temTowns.size() > 0) {
                fieldInfo.getDefault_locations().setTown_name(temTowns.get(0).getTown_name());
                fieldInfo.getDefault_locations().setTown_nameUrdu(temTowns.get(0).getTown_nameUrdu());
                fieldInfo.getDefault_locations().setTown_id(temTowns.get(0).getTown_id());

                townACTV.setFormFieldInfo(fieldInfo);
                townACTV.setText(Html.fromHtml(isEnglishLang() ? temTowns.get(0).getTown_name() : temTowns.get(0).getTown_nameUrdu()));

                if (fieldInfo.getDefault_locations().getTown_id() != 0) {
                    setSubTownsACTV(fieldInfo);
                }
            }

            townACTV.setItemClickCallback(new WhichItemClicked() {
                @Override
                public void whichItemClicked(String id) {
                    subTownACTV.setText("");
                    subTownInfos = getSubTownInfos(id);
                    if (subTownInfos != null && subTownInfos.size() > 0) {
                        sub_townACTIL.setVisibility(View.VISIBLE);
                    }
                    fieldInfo.getDefault_locations().setTown_id(Integer.parseInt(id));
                    setSubTownsACTV(fieldInfo);
                }

                @Override
                public void downloadInspection(String downloadUrl, int position) {

                }

                @Override
                public void deleteRecordAPICall(String deleteUrl, int position) {

                }
            }, formDataInfo, fieldInfo);
        }
        townACTV.setTowns(townInfos);
    }

    private void setSubTownsACTV(final FormFieldInfo fieldInfo) {
        fieldInfo.setRequired(false);
        FormDataInfo formDataInfo = null;
        if (formFilteredData != null && formFilteredData.get(subTownACTV.getTag().toString()) != null && formFilteredData.get(subTownACTV.getTag().toString()).size() > 0) {
            formDataInfo = formFilteredData.get(subTownACTV.getTag().toString()).get(0);
        }

        if ((subTownInfos == null || subTownInfos.size() == 0)) {
            sub_townACTIL.setVisibility(GONE);
        } else {
            sub_townACTIL.setVisibility(View.VISIBLE);
        }

        ///////////////
        if (fieldInfo.getDefault_locations().getSubtown_id() == 0 || setLocationListener) {
            List<SubTownInfo> tempSubtowns = getSubtownBySubtownId("" + fieldInfo.getDefault_locations().getSubtown_id());

            if (tempSubtowns != null && tempSubtowns.size() > 0) {
                fieldInfo.getDefault_locations().setSubtown_name(tempSubtowns.get(0).getSubtown_name());
                fieldInfo.getDefault_locations().setSubtown_nameUrdu(tempSubtowns.get(0).getSubtown_nameUrdu());
                fieldInfo.getDefault_locations().setSubtown_id(tempSubtowns.get(0).getSubtown_id());
                subTownACTV.setFormFieldInfo(fieldInfo);
                subTownACTV.setText(Html.fromHtml(isEnglishLang() ? tempSubtowns.get(0).getSubtown_name() : tempSubtowns.get(0).getSubtown_nameUrdu()));

                sub_townACTIL.setVisibility(VISIBLE);
            }

            //////////
            subTownACTV.setItemClickCallback(new WhichItemClicked() {
                @Override
                public void whichItemClicked(String id) {
                    fieldInfo.getDefault_locations().setSubtown_id(Integer.parseInt(id));
                }

                @Override
                public void downloadInspection(String downloadUrl, int position) {

                }

                @Override
                public void deleteRecordAPICall(String deleteUrl, int position) {

                }
            }, formDataInfo, fieldInfo);
        }

        subTownACTV.setSubTowns(subTownInfos);
    }

    private void hideAndShowLocationViews(FormFieldInfo fieldInfo, HashMap<String, Boolean> fieldsReq) {

        if (fieldInfo.getDefault_locations() != null) {

            fieldsReq.put(REGION_TAG, fieldInfo.isRequired());
            setRegionACTV(fieldInfo);

            fieldsReq.put(DIVISION_TAG, fieldInfo.isRequired());
            setDivisionACTV(fieldInfo);

            fieldsReq.put(DISTRICT_TAG, fieldInfo.isRequired());
            setDistrictACTV(fieldInfo);

            fieldsReq.put(TOWN_TAG, fieldInfo.isRequired());
            setTownsACTV(fieldInfo);

            fieldsReq.put(SUB_TOWN_TAG, false);
            setSubTownsACTV(fieldInfo);

            regionACTIL.setVisibility(GONE);
            divisionACTIL.setVisibility(GONE);
            districtACTIL.setVisibility(GONE);
            townACTIL.setVisibility(GONE);
            sub_townACTIL.setVisibility(GONE);

            if (setLocationListener) {
                List<String> whichLocationToHide = fieldInfo.getWhichLocationToHide();
                if (whichLocationToHide != null && whichLocationToHide.size() > 0) {
                    if (!whichLocationToHide.contains(REGION_TAG)) {

                        if (fieldInfo.getDefault_locations().getRegion_id() != 0) {
                            regionACTIL.setVisibility(VISIBLE);
                            divisionACTIL.setVisibility(VISIBLE);
                        }
                    }

                    if (!whichLocationToHide.contains(DIVISION_TAG)) {
                        if (fieldInfo.getDefault_locations().getDivision_id() != 0) {
                            divisionACTIL.setVisibility(VISIBLE);
                            districtACTIL.setVisibility(VISIBLE);
                        }
                    }

                    if (!whichLocationToHide.contains(DISTRICT_TAG)) {
                        if (fieldInfo.getDefault_locations().getDistrict_id() != 0) {
                            districtACTIL.setVisibility(VISIBLE);
                            if (fieldInfo.getDefault_locations().isShow_town())
                                townACTIL.setVisibility(VISIBLE);
                        }
                    }

                    if (!whichLocationToHide.contains(TOWN_TAG)) {
                        if (fieldInfo.getDefault_locations().isShow_town())
                            townACTIL.setVisibility(VISIBLE);
                        if (fieldInfo.getDefault_locations().getTown_id() != 0) {
                            sub_townACTIL.setVisibility(VISIBLE);
                        }
                    }
                    if (!whichLocationToHide.contains(SUB_TOWN_TAG)) {
                        if (fieldInfo.getDefault_locations().getSubtown_id() != 0)
                            sub_townACTIL.setVisibility(VISIBLE);
                    }
                }

            } else if (fieldInfo.getDefault_locations().getRegion_id() == 0 && fieldInfo.getDefault_locations().getDivision_id() == 0) {
                districtACTIL.setVisibility(View.VISIBLE);

                if (fieldInfo.getDefault_locations().getTown_id() != 0 || fieldInfo.getDefault_locations().isShow_town()) {
                    townACTIL.setVisibility(VISIBLE);
                    sub_townACTIL.setVisibility(VISIBLE);
                }
                if (fieldInfo.getDefault_locations().getSubtown_id() != 0)
                    sub_townACTIL.setVisibility(VISIBLE);

            } else {

                if (fieldInfo.getDefault_locations().getRegion_id() == 0) {
                    regionACTIL.setVisibility(View.VISIBLE);
                    return;
                }
                if (fieldInfo.getDefault_locations().getDivision_id() == 0) {
                    divisionACTIL.setVisibility(View.VISIBLE);
                    return;
                }
                if (fieldInfo.getDefault_locations().getDistrict_id() == 0) {
                    districtACTIL.setVisibility(View.VISIBLE);
                    return;
                }
                if (fieldInfo.getDefault_locations().getTown_id() == 0 || fieldInfo.getDefault_locations().isShow_town()) {
                    townACTIL.setVisibility(View.VISIBLE);
                    if (!fieldInfo.getDefault_locations().isShow_town())
                        return;
                }

                if (fieldInfo.getDefault_locations().getTown_id() != 0 || fieldInfo.getDefault_locations().isShow_town()) {
                    sub_townACTIL.setVisibility(View.VISIBLE);
                }

                if (fieldInfo.getDefault_locations().getSubtown_id() == 0) {
                    if (subTownInfos != null && subTownInfos.size() > 0)
                        sub_townACTIL.setVisibility(View.VISIBLE);
                }
            }
        }

    }


    private void setPFABizSizeACTVStatus(boolean isReq, int visibilty) {
        if (pfaddBizSizeACTV == null)
            return;
        pfaddBizSizeACTV.formFieldInfo.setInvisible(!isReq);
        pfaddBizSizeACTV.formFieldInfo.setRequired(isReq);
        pfaddBizSizeACTV.setVisibility(visibilty);
        pfaddBizSizeACTV.getPfaddLL().setVisibility(visibilty);

        pfaddBizSizeACTV.setText("");
        pfaddBizSizeACTV.setSelectedValues(null);

    }


    public void setDDCallback(DDSelectedCallback DDCallback) {
        this.DDCallback = DDCallback;
    }

    @Override
    public void setSearchBizLoc(String key, boolean searchFormData) {
        switch (key) {
            case REGION_TAG:
                if (searchFormData) {
                    List<RegionInfo> subTownInfos = getRegionByRegionID(SEARCH_BIZ_JSON_OBJ.optString(key));
                    regionACTV.setSearchRegionID(SEARCH_BIZ_JSON_OBJ.optInt(key));
                    if (subTownInfos != null && subTownInfos.size() > 0) {
                        regionACTV.setText(Html.fromHtml(subTownInfos.get(0).getRegion_name()));
                        setBizLocationEnabled(true, regionACTV);
                    }
                } else {
                    setBizLocationEnabled(false, regionACTV);
                }
                break;

            case DIVISION_TAG:
                if (searchFormData) {
                    List<DivisionInfo> subTownInfos = getDivisionByDivisionID(SEARCH_BIZ_JSON_OBJ.optString(key));
                    divisionACTV.setSearchDivID(SEARCH_BIZ_JSON_OBJ.optInt(key));
                    if (subTownInfos != null && subTownInfos.size() > 0) {
                        divisionACTV.setText(Html.fromHtml(isEnglishLang() ? subTownInfos.get(0).getDivision_name() : subTownInfos.get(0).getDivision_nameUrdu()));
                        setBizLocationEnabled(true, divisionACTV);
                    }
                } else {
                    setBizLocationEnabled(false, divisionACTV);
                }
                break;

            case DISTRICT_TAG:
                if (searchFormData) {
                    List<DistrictInfo> subTownInfos = getDistrictByDistrictID(SEARCH_BIZ_JSON_OBJ.optString(key));
                    districtACTV.setSearchDistID(SEARCH_BIZ_JSON_OBJ.optInt(key));
                    if (subTownInfos != null && subTownInfos.size() > 0) {
                        districtACTV.setText(Html.fromHtml(isEnglishLang() ? subTownInfos.get(0).getDistrict_name() : subTownInfos.get(0).getDistrict_nameUrdu()));
                        setBizLocationEnabled(true, districtACTV);
                    }
                } else {
                    setBizLocationEnabled(false, districtACTV);
                }
                break;

            case TOWN_TAG:
                if (searchFormData) {
                    List<TownInfo> subTownInfos = getTownsByTownId(SEARCH_BIZ_JSON_OBJ.optString(key));
                    townACTV.setSearchTownID(SEARCH_BIZ_JSON_OBJ.optInt(key));
                    if (subTownInfos != null && subTownInfos.size() > 0) {
                        townACTV.setText(Html.fromHtml(isEnglishLang() ? subTownInfos.get(0).getTown_name() : subTownInfos.get(0).getTown_nameUrdu()));
                        setBizLocationEnabled(true, townACTV);
                    }
                } else {
                    setBizLocationEnabled(false, townACTV);
                }
                break;

            case SUB_TOWN_TAG:
                if (searchFormData) {
                    List<SubTownInfo> subTownInfos = getSubtownBySubtownId(SEARCH_BIZ_JSON_OBJ.optString(key));
                    subTownACTV.setSearchSubTownID(SEARCH_BIZ_JSON_OBJ.optInt(key));
                    if (subTownInfos != null && subTownInfos.size() > 0) {
                        subTownACTV.setText(Html.fromHtml(isEnglishLang() ? subTownInfos.get(0).getSubtown_name() : subTownInfos.get(0).getSubtown_nameUrdu()));
                        setBizLocationEnabled(true, subTownACTV);
                    }
                } else {
                    setBizLocationEnabled(false, subTownACTV);
                }
                break;
        }
    }

    public void setSearchFilter(boolean searchFilter) {
        isSearchFilter = searchFilter;
    }

    public void updateDropdownViewsData(Bundle bundle, final LinearLayout parentView, HashMap<String, HashMap<String, Boolean>> sectionRequired) {

        if (bundle != null && bundle.containsKey(EXTRA_ACTV_TAG)) {
            String actvTag = bundle.getString(EXTRA_ACTV_TAG);
            int selectedPosition = bundle.getInt(SELECTED_POSITION, -1);

            assert actvTag != null;
            switch (actvTag) {
                case REGION_TAG:
                    regionACTV.setRegionDropdownSelection(selectedPosition);
                    break;

                case DIVISION_TAG:
                    divisionACTV.setDivisionDropdownSelection(selectedPosition);
                    break;

                case DISTRICT_TAG:
                    districtACTV.setDistrictDropdownSelection(selectedPosition);
                    break;

                case TOWN_TAG:
                    townACTV.setTownsDropdownSelection(selectedPosition);
                    break;

                case SUB_TOWN_TAG:
                    subTownACTV.setSubTownsDropdownSelection(selectedPosition);
                    break;

                default:
                    View pfaddactv = parentView.findViewWithTag(actvTag);

                    if (pfaddactv instanceof PFASearchACTV) {
                        if (bundle.containsKey(PFA_SEARCH_TAG)) {
                            PFASearchACTV pfaSearchACTV = (PFASearchACTV) pfaddactv;//parentView.findViewWithTag(actvTag);
                            PFASearchInfo pfaSearchInfo = (PFASearchInfo) bundle.getSerializable(PFA_SEARCH_TAG);
                            if (pfaSearchInfo != null) {
                                pfaSearchACTV.setPfaSearchInfo(pfaSearchInfo);
                                pfaSearchACTV.setText(String.format(Locale.getDefault(), "%s%s", pfaSearchInfo.getFull_name(), (pfaSearchInfo.getCnic_number() == null || pfaSearchInfo.getCnic_number().isEmpty()) ? "" : " / " + pfaSearchInfo.getCnic_number()));

                                HttpService httpService = new HttpService(mContext);
                                httpService.getListsData(pfaSearchInfo.getAPI_URL(), new HashMap<String, String>(), new HttpResponseCallback() {
                                    @Override
                                    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                        if (response != null) {
                                            if (response.optBoolean("status")) {
                                                SEARCH_BIZ_JSON_OBJ = response.optJSONObject("data");
                                                setSearchBizData(true, parentView, pfaViewsCallbacks, CustomViewCreate.this);
                                            }
                                        }
                                    }
                                }, true);
                            }
                        }
                    } else if (pfaddactv instanceof PFADDACTV) {
                        PFADDACTV pfaddactv1 = (PFADDACTV) pfaddactv;
                        if (pfaddactv1.getTextInputLayout() != null) {
                            pfaddactv1.getTextInputLayout().setError(null);
                            pfaddactv1.clearFocus();
                        }
                        ((PFADDACTV) pfaddactv).setDropdownSelection(selectedPosition, pfaViewsCallbacks);
                        pfaddBizSizeACTV = parentView.findViewWithTag(DD_BIZ_SIZE);  //business_size

                        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(mContext);
                       defaultValue =  sharedPrefUtils.getAction();

                        if (pfaddactv1.formFieldInfo.getField_name() != null && (pfaddactv1.formFieldInfo.getField_name().equalsIgnoreCase(DD_STATUS))) {
                            IS_FINE = false;

                            //*********************

//                            String defaultValue = null;

//                            if (pfaddactv1.formFieldInfo != null && pfaddactv1.formFieldInfo.getData() != null && pfaddactv1.formFieldInfo.getData().size() > 0) {
//                                for (FormDataInfo formDataInfo : pfaddactv1.formFieldInfo.getData()) {
//
//                                    if (formDataInfo != null && formDataInfo.getKey().equalsIgnoreCase(pfaddactv1.formFieldInfo.getDefault_value()))
//                                        defaultValue = formDataInfo.getValue();
//                                }
//                            }

//                               String  defaultValue = null;

//                            sharedPreferences  = PreferenceManager
//                                    .getDefaultSharedPreferences(mContext);
//                            defaultValue = sharedPreferences.getString("defaultVAL", "");
//
//                            //*********************

                            if (pfaddBizSizeACTV != null) {

                                if (defaultValue != null && (defaultValue.equalsIgnoreCase(DD_STATUS_EP) || defaultValue.equalsIgnoreCase(DD_STATUS_SEAL))) {

                                    setPFABizSizeACTVStatus(true, VISIBLE);
                                    //////////////////// Added Mandatory background for dropdown
                                    if (pfaddBizSizeACTV.getTextInputLayout() == null) {
                                        pfaddBizSizeACTV.setBackgroundDrawable(mContext.getResources().getDrawable(isEnglishLang() ? R.mipmap.spinner_required : R.mipmap.ur_spinner_required));
                                    } else {
                                        pfaddBizSizeACTV.getTextInputLayout().setBackground(mContext.getResources().getDrawable(isEnglishLang() ? R.mipmap.spinner_required : R.mipmap.ur_spinner_required));
                                    }

                                } else {
                                    setPFABizSizeACTVStatus(false, GONE);
                                }

                                pfaddBizSizeACTV.clearFocus();
                            }

//                            1: Mobile inspection:  if select action taken ( Fine / Seal Business ). Mobile number and cnic number will be required else optional.
//                            2: Mobile inspection:  if select action taken fine. then validate fine tab and force fso to add challan details in case he submit empty.
                            if (defaultValue != null && (defaultValue.equalsIgnoreCase(DD_STATUS_FINE) || defaultValue.equalsIgnoreCase(DD_STATUS_SEAL))) {

                                if (defaultValue.equalsIgnoreCase(DD_STATUS_FINE)) {
                                    IS_FINE = true;
                                }

                            }
                        }

                        ////////// Show Check Value Data Set

//                            Check fields to set them required and visible
                        if (pfaddactv1.formFieldInfo != null && pfaddactv1.formFieldInfo.getField_name() != null && (pfaddactv1.formFieldInfo.getShow_check_value() != null)
                                && (pfaddactv1.formFieldInfo.getShow_check_value().size() > 0)) {

                            if (pfaddactv1.getSelectedValues() != null && pfaddactv1.getSelectedValues().size() > 0) {
                                if (pfaddactv1.formFieldInfo.getShow_check_value().contains(pfaddactv1.getSelectedValues().get(0).getValue())) {
                                    formFieldsHideShow.setFieldsRequiredAndVisible(pfaddactv1.formFieldInfo.getShow_hidden_false_fields(), true, sectionRequired, parentView, pfaddactv1.getSelectedValues().get(0).getValue());
                                } else {
                                    formFieldsHideShow.setFieldsRequiredAndVisible(pfaddactv1.formFieldInfo.getShow_hidden_false_fields(), false, sectionRequired, parentView, pfaddactv1.getSelectedValues().get(0).getValue());
                                }
//                                String value = pfaddactv1.getSelectedValues().get(0).getValue();
//
//                                if (value != null && (value.equalsIgnoreCase(DD_STATUS_EP) || value.equalsIgnoreCase(DD_STATUS_SEAL))) {
//
//                                    if (pfaddactv1.formFieldInfo.getShow_check_value().contains((DD_STATUS_EP))) {
//
//                                        setPFABizSizeACTVStatus(true, VISIBLE);
//                                        //////////////////// Added Mandatory background for dropdown
//                                        if (pfaddBizSizeACTV.getTextInputLayout() == null) {
//                                            pfaddBizSizeACTV.setBackgroundDrawable(mContext.getResources().getDrawable(isEnglishLang() ? R.mipmap.spinner_required : R.mipmap.ur_spinner_required));
//                                        } else {
//                                            pfaddBizSizeACTV.getTextInputLayout().setBackground(mContext.getResources().getDrawable(isEnglishLang() ? R.mipmap.spinner_required : R.mipmap.ur_spinner_required));
//                                        }
//                                        ///////////////
//                                    } else {
//                                        setPFABizSizeACTVStatus(false, GONE);
//                                    }
//                                }
                            }
                        }
//                            Check fields to set them required and visible end
                        ////////// Show Check Value Data Set End
                    }
                    break;
            }
        }
    }


    private void setFoodLabViews(final LinearLayout menuFragParentLL, List<FormSectionInfo> formSectionInfos, final HashMap<String, HashMap<String, Boolean>> sectionRequired, final ScrollView fragMenuItemSV) {
        if (menuFragParentLL.findViewWithTag(DD_FOOD_LAB_TEST + "LL") != null) {
            LinearLayout ddFoodLabTestLL = menuFragParentLL.findViewWithTag(DD_FOOD_LAB_TEST + "LL");
            ddFoodLabTestLL.removeAllViews();
            for (FormSectionInfo formSectionInfo : formSectionInfos) {
                createViews(formSectionInfo, ddFoodLabTestLL, sectionRequired, null, false, fragMenuItemSV);
            }
        }

    }

    /**
     * If dropdown is selected [having API URL], so the data returned from API_URL response is added to form sections
     *
     * @param formDataInfo     FormDataInfo data actually populated in views
     * @param menuFragParentLL LinearLayout parent linear layout inside ScrollView with vertical orientation in which new sections will be appended
     * @param sectionRequired  this is used for form validation. If any of the field required is missed to enter data, form cannot be submitted
     * @param fragMenuItemSV   As the data can be long so it should be scrollable.. That;s why fragMenuItemSV[ScrollView] is parent scrollview
     * @param formSectionInfos in case of multiple form sections, the received new sections are appended in that list
     */
    public void onDDSelectedAPIUrl(final FormDataInfo formDataInfo, final LinearLayout menuFragParentLL, final HashMap<String, HashMap<String, Boolean>> sectionRequired, final ScrollView fragMenuItemSV, final List<FormSectionInfo> formSectionInfos) {

        if (formDataInfo != null && formDataInfo.getAPI_URL() != null && (!formDataInfo.getAPI_URL().isEmpty())) {
            HttpService httpService = new HttpService(mContext);
            /////////////
            if (formDataInfo.getName() != null && (formDataInfo.getName().equalsIgnoreCase(DD_FOOD_LAB_TEST))) {

                httpService.getListsData(formDataInfo.getAPI_URL(), new HashMap<String, String>(), new HttpResponseCallback() {
                    @Override
                    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                        if (response != null) {
                            if (response.optBoolean("status")) {
                                try {
                                    Type type = new TypeToken<List<FormSectionInfo>>() {
                                    }.getType();

                                    JSONObject dataJsonObject = response.getJSONObject("data");
                                    if (dataJsonObject.has("form")) {

                                        JSONArray formJSONArray = dataJsonObject.getJSONArray("form");
                                        foodLabSections = new GsonBuilder().create().fromJson(formJSONArray.toString(), type);
//                                        if more than one section then do decision based on that accordingly
                                        if (formSectionInfos != null)
                                            formSectionInfos.addAll(foodLabSections);
                                        setFoodLabViews(menuFragParentLL, foodLabSections, sectionRequired, fragMenuItemSV);
//                                        if (menuFragParentLL.findViewWithTag(DD_FOOD_LAB_TEST + "LL") != null) {
//                                            LinearLayout ddFoodLabTestLL = menuFragParentLL.findViewWithTag(DD_FOOD_LAB_TEST + "LL");
//                                            ddFoodLabTestLL.removeAllViews();
//                                            for (FormSectionInfo formSectionInfo : foodLabSections) {
//                                                createViews(formSectionInfo, ddFoodLabTestLL, sectionRequired, null, false, fragMenuItemSV);
//                                            }
//                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, true);

            } else {
                httpService.fetchConfigData(formDataInfo.getAPI_URL(), new HttpResponseCallback() {
                    @Override
                    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                        if (response.optBoolean("status")) {
                            Type type = new TypeToken<List<FormDataInfo>>() {
                            }.getType();

                            List<FormDataInfo> formDataInfos = new GsonBuilder().create().fromJson(response.optJSONArray("data").toString(), type);

                            if (formDataInfo.getAppend_to() != null && (!formDataInfo.getAppend_to().isEmpty())) {
                                PFADDACTV pfaddactv = menuFragParentLL.findViewWithTag(formDataInfo.getAppend_to());

                                if (pfaddactv != null) {
                                    if (pfaddactv.formFieldInfo != null) {

                                        //// Remove Previous Data
                                        if (pfaddactv.getSelectedValues() != null && pfaddactv.getSelectedValues().size() > 0) {
                                            if (pfaddactv.formFieldInfo != null && pfaddactv.formFieldInfo.getData() != null)
                                                pfaddactv.formFieldInfo.getData().clear();
                                            pfaddactv.getSelectedValues().clear();
                                            pfaddactv.setText("");
                                            pfaddactv.setSelectedValues(null);
                                        }
                                        pfaddactv.formFieldInfo.setData(formDataInfos);
                                        pfaddactv.populateData();
                                    }
                                }
                            }
                        }
                    }
                });
            }
            ////////////
        }
    }
}

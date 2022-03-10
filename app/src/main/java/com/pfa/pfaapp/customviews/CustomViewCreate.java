package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.AppController;
import com.pfa.pfaapp.ImageGalleryActivity;
import com.pfa.pfaapp.MapsActivity;
import com.pfa.pfaapp.PFADetailActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.SplashActivity;
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
import com.pfa.pfaapp.utils.DownloadFileManager;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

import androidx.appcompat.app.AlertDialog;

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
    private String dropDownId = "0";
    private String localBusinessDropDownUrl = "";
    private String revisedBusinessCatDropDownUrl = "";

    SharedPreferences sharedPreferences;
    String defaultValue = null;

    private boolean setLocationListener;
    private DDSelectedCallback DDCallback;

    private ScrollView mainScrollView;
    private boolean isSearchFilter;

    private PFADDACTV pfaddBizSizeACTV;
    private List<FormSectionInfo> foodLabSections;

    private FormFieldsHideShow formFieldsHideShow;
    PFAViewsUtils pfaViewsUtils;
    private SharedPrefUtils sharedPrefUtils;
    private FormSectionInfo formSectionInfo;
    private ArrayList<LinearLayout> linerList = new ArrayList<LinearLayout>();
    ArrayList<String> fieldTypesList = new ArrayList<>();
    ArrayList<PFATextInputLayout> textInputLayoutList = new ArrayList<>();
    private ArrayList<TextView> inputTextList = new ArrayList<>();
    private PFATextInputLayout sampleBrandName;
    private FrameLayout sampleBrandNameDD;
    private TextView textView_name;
    private PFAEditText editTextName;
    private PFADDACTV product_categoryDD;
    private boolean firstTime = true;
    String parentLicenseVal , revisedLicesneUrl , confirmRevisedCatMsg , parentBusinessCatUrl;
    String revBusinessCategoryURL;

    private String retailer_investment, retailer_avg_sale, retailer_rent, retailer_employees, retailer_location, retailer_bills, parent_license_type = "Retailer";
    private String retailer_investment_key, retailer_avg_sale_key, retailer_rent_key, retailer_employees_key, retailer_location_key, retailer_bills_key, parent_license_type_key = "ParentLicenseType";

    private String manufacturer_investment, manufacturer_inventory, manufacturer_units_produced, manufacturer_sales, manufacturer_rent, manufacturer_employees, manufacturer_machines, manufacturer_products, manufacturer_bills;
    private String manufacturer_investment_key, manufacturer_inventory_key, manufacturer_units_produced_key, manufacturer_sales_key, manufacturer_rent_key, manufacturer_employees_key, manufacturer_machines_key, manufacturer_products_key, manufacturer_bills_key;

    private String restaurant_investment, restaurant_inventory, restaurant_sales, restaurant_rent, restaurant_employees, restaurant_seating_capacity, restaurant_location, restaurant_menu_range, restaurant_bills;
    private String restaurant_investment_key, restaurant_inventory_key, restaurant_sales_key, restaurant_rent_key, restaurant_employees_key, restaurant_seating_capacity_key, restaurant_location_key, restaurant_menu_range_key, restaurant_bills_key;

    private String e_commerce_investment, e_commerce_sales, e_commerce_employees, e_commerce_riders, e_commerce_registered_fbos;
    private String e_commerce_investment_key, e_commerce_sales_key, e_commerce_employees_key, e_commerce_riders_key, e_commerce_registered_fbos_key;

    /**
     * @param mContext          {@link Context}
     * @param pfaViewsCallbacks PFAViewsCallbacks interface
     */

    public CustomViewCreate(Context mContext, PFAViewsCallbacks pfaViewsCallbacks) {
        this(mContext, pfaViewsCallbacks, null);
        sharedPrefUtils = new SharedPrefUtils(mContext);
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

        pfaViewsUtils = new PFAViewsUtils(mContext);


        formFieldsHideShow = new FormFieldsHideShow(mContext);
        sharedPrefUtils = new SharedPrefUtils(mContext);
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
        this.formSectionInfo = formSectionInfo;

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
                    Log.d("viewCreated", "heading");
                    createViewHeading(parentView, inflater, fieldInfo);
                    break;

                case "get_code_button":
                    Log.d("viewCreated", "get_code_button");
                    createViewGetCodeButton(parentView, fieldInfo);
                    break;

                case "get_business_details":
                    Log.d("viewCreated", "get_business_details");
                    createViewGetBusinessDetails(parentView, fieldInfo);
                    break;

                case "googlemap":
                    Log.d("viewCreated", "googlemap");
                    createGoogleMap(parentView, inflater, fieldInfo);
                    break;

                case "radiogroup":
                    Log.d("viewCreated", "radiogroup");
                    createRadioGroup(pfaSectionTV, fieldInfo, parentView, fieldsReq);
                    break;

                case "checkbox":
                    Log.d("viewCreated", "checkbox");
                    parentView.addView(pfaSectionTV);
                    PFACheckboxGroup pfaCheckboxGroup = new PFACheckboxGroup(mContext, fieldInfo, formFilteredData);
                    parentView.addView(pfaCheckboxGroup.getCheckboxLL());
                    if (fieldInfo.isInvisible()) {
                        pfaSectionTV.setVisibility(GONE);
                        pfaCheckboxGroup.getCheckboxLL().setVisibility(GONE);
                    }
                    break;

                case "label":
                    Log.d("viewCreated", "label");
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
                    Log.d("viewCreated", "imageView");
                    createViewImageView(parentView, inflater, formSectionInfo, fieldInfo, imageLayout, fields, sectionRequired, pfaViewsCallbacks2, fieldCount);
                    break;


                case "fileView":
                    Log.d("viewCreated", "fileView");
                    createViewFileView(parentView, inflater, formSectionInfo, fieldInfo, imageLayout, fields, sectionRequired, pfaViewsCallbacks2, fieldCount);
                    break;

/*
                case "multipleInputFields":
                    createMultipleInputFields(parentView, inflater, formSectionInfo, fieldInfo, imageLayout, fields, sectionRequired, pfaViewsCallbacks2, fieldCount);
                    break;*/

                case "button":
                    Log.d("viewCreated", "button");
                    createGenButton(formSectionInfo, fieldInfo, parentView, imageLayout, addDynamicSubItem, sectionRequired, pfaViewsCallbacks2);
                    break;

                case "autoSearch":
                    Log.d("viewCreated", "autoSearch");
                    createAutoSearchView(parentView, inflater, fieldInfo);
                    break;

                case "dropdown":
                    Log.d("viewCreated", "dropDown = " + fieldInfo.getField_name());
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
                    Log.d("viewCreated", "location");
                    createViewLocationFields(fieldInfo, params, parentView, inflater, fieldsReq);
                    break;

                case "abc":
                    Log.d("viewCreated", "abc");
                    createViewABC(fieldInfo, parentView, inflater);
                    break;

                default:
//                    for all edittext fields this function creates views
                    Log.d("viewCreated", "edittext");
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
                Log.d("enfrocementData2312", "button clicked = ");

                Log.d("submitButtonCLick", "button clicked action = " + button.getFormFieldInfo().getAction());
                if (confirmRevisedCatMsg != null && !confirmRevisedCatMsg.isEmpty()){
                    String confirmMsg = String.format(confirmRevisedCatMsg.replace("\\n", System.lineSeparator()));
//                    String confirmMsg = confirmRevisedCatMsg.substring(0 , confirmRevisedCatMsg.lastIndexOf("!"));
//                    String confirmMsg1 = confirmRevisedCatMsg.substring(confirmRevisedCatMsg.lastIndexOf("Bu") , confirmRevisedCatMsg.lastIndexOf(")"));
                    AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setTitle("Revise Business License Category")
                    .setMessage(confirmMsg)

                    .setPositiveButton(android.R.string.yes, (dialog1, which) -> {
                        if (pfaViewsCallbacks != null) {
                            pfaViewsCallbacks.onButtonCLicked(view);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
                    TextView textView = dialog.findViewById(android.R.id.message);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mContext.getResources().getDimension(R.dimen._5sdp));
                }else {
                    if (pfaViewsCallbacks != null)
                        pfaViewsCallbacks.onButtonCLicked(view);
                }

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
        TextView clickableTV = pfa_detail_heading.findViewById(R.id.clickableTV);
        headingLblTV.setText(Html.fromHtml(isEnglishLang() ? fieldInfo.getValue() : fieldInfo.getValueUrdu()));
        applyFont(headingLblTV, FONTS.HelveticaNeueBold);

        if (!fieldInfo.isBottom_hr_line()) {
            pfa_detail_heading.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            clickableTV.setVisibility(GONE);
        } else {
            pfa_detail_heading.setBackground(mContext.getResources().getDrawable(R.mipmap.text_bg));
            clickableTV.setVisibility(VISIBLE);
        }


        if (fieldInfo.getClickable_text() != null && (!fieldInfo.getClickable_text().isEmpty())) {


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
                                if (response != null) {
                                    bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                                    startNewActivity(PFADetailActivity.class, bundle, false);
                                } else {
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
        Log.d("createViewGetCodeButton", "createViewGetCodeButton");
        final VerifyFBOLayout verifyFBOLayout = new VerifyFBOLayout(mContext, fieldInfo, pfaViewsCallbacks, new CheckUserCallback() {
            @Override
            public void getExistingUser(JSONArray jsonArray) {
                Log.d("createViewGetCodeButton", "createViewGetCodeButton1");
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
                    if (parentView.findViewWithTag(jsonObject.optString("key")) != null && parentView.findViewWithTag(jsonObject.optString("key")) instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag(jsonObject.optString("key"));
                        pfaddactv.setText(Html.fromHtml(jsonObject.optString("value")));

                        if (!jsonObject.optBoolean("is_editable")) {
                            pfaddactv.setEnabled(false);
                            pfaddactv.setClickable(false);
                            pfaddactv.setFocusable(false);

                            pfaddactv.setBackgroundColor(mContext.getResources().getColor(R.color.chat_list_footer_bg));

                        }
                    }
                }

                AppConst.codeVerified = true;
            }

            @Override
            public void getExistingBusiness(JSONArray jsonArray , String msg) {

            }
        });
        verifyFBOLayout.setTag(fieldInfo.getField_name());

        parentView.addView(verifyFBOLayout);

        if (fieldInfo.isInvisible()) {
            verifyFBOLayout.setVisibility(GONE);
        }

    }


    private void createViewGetBusinessDetails(final LinearLayout parentView, final FormFieldInfo fieldInfo) {
        AppConst.codeVerified = false;
        /*final VerifyFBOBusiness verifyFBOLayout = new VerifyFBOBusiness(mContext, fieldInfo, pfaViewsCallbacks, new CheckUserCallback() {
            @Override
            public void getExistingUser(JSONArray jsonArray) {

            }

            @Override
            public void getExistingBusiness(JSONArray jsonArray) {
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
                    if (parentView.findViewWithTag(jsonObject.optString("key")) != null && parentView.findViewWithTag(jsonObject.optString("key")) instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag(jsonObject.optString("key"));
                        pfaddactv.setText(Html.fromHtml(jsonObject.optString("value")));

                        if (!jsonObject.optBoolean("is_editable")) {
                            pfaddactv.setEnabled(false);
                            pfaddactv.setClickable(false);
                            pfaddactv.setFocusable(false);

                            pfaddactv.setBackgroundColor(mContext.getResources().getColor(R.color.chat_list_footer_bg));

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
        }*/

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

    public String getEditTextName() {
        String etText = null;
        if (editTextName != null) {
            etText = Objects.requireNonNull(editTextName.getText()).toString();
//            editTextName.setRequired(true);
//            editTextName.setError("");
//            editTextName.setCompoundDrawables(mContext.getResources().getDrawable(R.mipmap.text_bg_star),null,null,null);
        }
        return etText;
    }

    public String getProduct_categoryDD() {
        String etText = null;
        if (product_categoryDD != null) {
            etText = Objects.requireNonNull(product_categoryDD.getText()).toString();
//            product_categoryDD.setRequired(true);
        }
        return etText;
    }

    private void createViewEditText(final LinearLayout parentView, final FormFieldInfo fieldInfo, PFATextInputLayout textInputLayout) {

        textInputLayout.setTag(fieldInfo.getField_name() + "01");

        fieldTypesList.add(fieldInfo.getField_type());
        if (fieldInfo.getField_name().equals("applicant_name") || fieldInfo.getField_name().equals("cnic") || fieldInfo.getField_name().equals("designation_Position") ||
                fieldInfo.getField_name().equals("food_business_name") || fieldInfo.getField_name().equals("office_address") || fieldInfo.getField_name().equals("manufacturing_unit_address") ||
                fieldInfo.getField_name().equals("cell_number")) {

//            textInputLayoutList.add(textInputLayout);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(convertDpToPixel(20), convertDpToPixel(8), 0, 0);

            TextView textView = new TextView(mContext);
            textView.setText("*required");
            textView.setTextColor(mContext.getResources().getColor(R.color.maroon1));
            textView.setLayoutParams(params);
//                textView.setTextSize(mContext.getResources().getDimension(R.dimen._2sdp));
            textView.setPadding(0, 0, 0, 0);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
//                textView.setTypeface(null, Typeface.BOLD);
//            textView.setVisibility(View.INVISIBLE);

            parentView.addView(textView);
            inputTextList.add(textView);

        }
        if (fieldInfo.getField_name().equals("name")) {

            Log.d("enfrocedData", "name text created");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(convertDpToPixel(20), convertDpToPixel(8), 0, 0);

            textView_name = new TextView(mContext);
            textView_name.setText("*required");
            textView_name.setTextColor(mContext.getResources().getColor(R.color.maroon1));
            textView_name.setLayoutParams(params);
//            textView.setTranslationY(70f);
            textView_name.bringToFront();

            parentView.addView(textView_name);

        }
        switch (fieldInfo.getField_type()) {
            case "text":
            case "searchkeytext":
            case "cnic":

                PFAEditText pfaEditText = new PFAEditText(mContext, fieldInfo, formFilteredData);
                pfaEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

                Log.d("enfrocedData", "text");
                textInputLayout.addView(pfaEditText);
                if (fieldInfo.getField_name().equals("revised_license_type")) {
                    pfaEditText.setClickable(false);
                    pfaEditText.setEnabled(false);
                    pfaEditText.setFocusable(false);
//                    textInputLayout.setBackgroundColor(mContext.getResources().getColor(R.color.text_light_grey));
                    pfaEditText.setBackgroundColor(mContext.getResources().getColor(R.color.text_light_grey));
                    revisedLicesneUrl = fieldInfo.getAPI_URL();
                }
                parentView.addView(textInputLayout);

                pfaEditText.addTextInputLayout(textInputLayout);

                if (fieldInfo.isInvisible()) {
                    textInputLayout.setVisibility(GONE);
                }

                setSearchOption(pfaEditText, parentView, isSearchFilter);

                if (fieldInfo.getField_name().equals("revised_license_type")) {


                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, convertDpToPixel(15), 0, 0);

                    TextView textView = new TextView(mContext);
                    textView.setText("Calculate License Category");
                    textView.setTextColor(mContext.getResources().getColor(R.color.white));
                    textView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                    textView.setLayoutParams(params);
                    textView.setTextSize(mContext.getResources().getDimension(R.dimen._5sdp));
                    textView.setPadding(0, 30, 0, 30);
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    textView.setTypeface(null, Typeface.BOLD);

                    parentView.addView(textView);

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            ArrayList<String> retailer_val = new ArrayList<>();
                            ArrayList<String> retailer_val_key = new ArrayList<>();

                            if (parent_license_type.equals("Retailer")) {
                                Log.d("liscentype", "Retailer");
                                if (retailer_avg_sale != null && retailer_bills != null && retailer_employees != null &&
                                        retailer_investment != null && retailer_location != null && retailer_rent != null) {

                                    Log.d("liscentype", "Retailer field not null");
                                    retailer_val.clear();
                                    retailer_val_key.clear();

                                    retailer_val.add(retailer_investment);
                                    retailer_val.add(retailer_avg_sale);
                                    retailer_val.add(retailer_rent);
                                    retailer_val.add(retailer_employees);
                                    retailer_val.add(retailer_location);
                                    retailer_val.add(retailer_bills);

                                    retailer_val_key.add(retailer_investment_key);
                                    retailer_val_key.add(retailer_avg_sale_key);
                                    retailer_val_key.add(retailer_rent_key);
                                    retailer_val_key.add(retailer_employees_key);
                                    retailer_val_key.add(retailer_location_key);
                                    retailer_val_key.add(retailer_bills_key);

                                    new PopulateLicenseCategory(mContext, revisedLicesneUrl , retailer_val, retailer_val_key, parent_license_type, parent_license_type_key, new CheckUserCallback() {
                                        @Override
                                        public void getExistingUser(JSONArray jsonArray) {
                                        }

                                        @Override
                                        public void getExistingBusiness(JSONArray jsonArray , String msg) {
                                            Log.d("liscentype", "Retailer field repsone");
                                            confirmRevisedCatMsg = msg;
                                            for (int i = 0; i < jsonArray.length(); i++) {

                                                if (i == 1) {
                                                    JSONObject jsonObject = jsonArray.optJSONObject(i);

                                                    if (parentView.findViewWithTag(jsonObject.optString("key")) instanceof PFAEditText) {
                                                        PFAEditText pfaEditText = parentView.findViewWithTag(jsonObject.optString("key"));
//                                                    pfaEditText.setText((CharSequence) pfaEditText.formFieldInfo.getData().get(42));
                                                        try {
                                                            pfaEditText.setText(jsonObject.getString("value"));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                        Log.d("liscentype", "Retailer respones not null");
                                                    }
                                                }

                                            }
                                        }
                                    });
                                } else
                                    sharedPrefUtils.showMsgDialog("Please fill above retailer assessment dropdowns", null);
                            } else if (parent_license_type.equals("Manufacturer")) {
                                Log.d("liscentype", "Manufacturer");
                                if (manufacturer_investment != null && manufacturer_inventory != null && manufacturer_units_produced != null &&
                                        manufacturer_sales != null && manufacturer_rent != null && manufacturer_employees != null &&
                                        manufacturer_machines != null && manufacturer_products != null && manufacturer_bills != null) {

                                    Log.d("liscentype", "Manufacturer fields not null");
                                    retailer_val.clear();
                                    retailer_val_key.clear();

                                    retailer_val.add(manufacturer_investment);
                                    retailer_val.add(manufacturer_inventory);
                                    retailer_val.add(manufacturer_units_produced);
                                    retailer_val.add(manufacturer_sales);
                                    retailer_val.add(manufacturer_rent);
                                    retailer_val.add(manufacturer_employees);
                                    retailer_val.add(manufacturer_machines);
                                    retailer_val.add(manufacturer_products);
                                    retailer_val.add(manufacturer_bills);

                                    retailer_val_key.add(manufacturer_investment_key);
                                    retailer_val_key.add(manufacturer_inventory_key);
                                    retailer_val_key.add(manufacturer_units_produced_key);
                                    retailer_val_key.add(manufacturer_sales_key);
                                    retailer_val_key.add(manufacturer_rent_key);
                                    retailer_val_key.add(manufacturer_employees_key);
                                    retailer_val_key.add(manufacturer_machines_key);
                                    retailer_val_key.add(manufacturer_products_key);
                                    retailer_val_key.add(manufacturer_bills_key);

                                    new PopulateLicenseCategory(mContext, revisedLicesneUrl , retailer_val, retailer_val_key, parent_license_type, parent_license_type_key, new CheckUserCallback() {
                                        @Override
                                        public void getExistingUser(JSONArray jsonArray) {
                                        }

                                        @Override
                                        public void getExistingBusiness(JSONArray jsonArray , String msg) {
                                            Log.d("liscentype", "Manufacturer field response");
                                            confirmRevisedCatMsg = msg;
                                            for (int i = 0; i < jsonArray.length(); i++) {

                                                JSONObject jsonObject = jsonArray.optJSONObject(i);

                                                if (parentView.findViewWithTag(jsonObject.optString("key")) instanceof PFAEditText) {
                                                    PFAEditText pfaEditText = parentView.findViewWithTag(jsonObject.optString("key"));
                                                    try {
                                                        pfaEditText.setText(jsonObject.getString("value"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Log.d("liscentype", "Manufacturer respone not null");
                                                }

                                            }
                                        }
                                    });
                                } else
                                    sharedPrefUtils.showMsgDialog("Please fill above manufacturer assessment dropdowns", null);
                            } else if (parent_license_type.equals("Restaurants")) {
                                Log.d("liscentype", "Restaurants");
                                if (restaurant_investment != null && restaurant_inventory != null && restaurant_sales != null &&
                                        restaurant_rent != null && restaurant_employees != null && restaurant_seating_capacity != null &&
                                        restaurant_location != null && restaurant_menu_range != null && restaurant_bills != null) {


                                    Log.d("liscentype", "Restaurants fields not null");
                                    retailer_val.clear();
                                    retailer_val_key.clear();

                                    retailer_val.add(restaurant_investment);
                                    retailer_val.add(restaurant_inventory);
                                    retailer_val.add(restaurant_sales);
                                    retailer_val.add(restaurant_rent);
                                    retailer_val.add(restaurant_employees);
                                    retailer_val.add(restaurant_seating_capacity);
                                    retailer_val.add(restaurant_location);
                                    retailer_val.add(restaurant_menu_range);
                                    retailer_val.add(restaurant_bills);

                                    retailer_val_key.add(restaurant_investment_key);
                                    retailer_val_key.add(restaurant_inventory_key);
                                    retailer_val_key.add(restaurant_sales_key);
                                    retailer_val_key.add(restaurant_rent_key);
                                    retailer_val_key.add(restaurant_employees_key);
                                    retailer_val_key.add(restaurant_seating_capacity_key);
                                    retailer_val_key.add(restaurant_location_key);
                                    retailer_val_key.add(restaurant_menu_range_key);
                                    retailer_val_key.add(restaurant_bills_key);

                                    new PopulateLicenseCategory(mContext, revisedLicesneUrl , retailer_val, retailer_val_key, parent_license_type, parent_license_type_key, new CheckUserCallback() {
                                        @Override
                                        public void getExistingUser(JSONArray jsonArray) {
                                        }

                                        @Override
                                        public void getExistingBusiness(JSONArray jsonArray , String msg) {
                                            Log.d("liscentype", "Restaurants fields response");
                                            confirmRevisedCatMsg = msg;
                                            for (int i = 0; i < jsonArray.length(); i++) {

                                                JSONObject jsonObject = jsonArray.optJSONObject(i);

                                                if (parentView.findViewWithTag(jsonObject.optString("key")) instanceof PFAEditText) {
                                                    PFAEditText pfaEditText = parentView.findViewWithTag(jsonObject.optString("key"));
                                                    try {
                                                        pfaEditText.setText(jsonObject.getString("value"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Log.d("liscentype", "Restaurants response not null");
                                                }

                                            }
                                        }
                                    });
                                } else
                                    sharedPrefUtils.showMsgDialog("Please fill above restaurants assessment dropdowns", null);
                            } else if (parent_license_type.equals("E-Commerce")) {
                                Log.d("liscentype", "Restaurants");
                                if (e_commerce_investment != null && e_commerce_sales != null && e_commerce_employees != null &&
                                        e_commerce_riders != null && e_commerce_registered_fbos != null) {


                                    Log.d("liscentype", "Restaurants fields not null");
                                    retailer_val.clear();
                                    retailer_val_key.clear();

                                    retailer_val.add(e_commerce_investment);
                                    retailer_val.add(e_commerce_sales);
                                    retailer_val.add(e_commerce_employees);
                                    retailer_val.add(e_commerce_riders);
                                    retailer_val.add(e_commerce_registered_fbos);

                                    retailer_val_key.add(e_commerce_investment_key);
                                    retailer_val_key.add(e_commerce_sales_key);
                                    retailer_val_key.add(e_commerce_employees_key);
                                    retailer_val_key.add(e_commerce_riders_key);
                                    retailer_val_key.add(e_commerce_registered_fbos_key);

                                    new PopulateLicenseCategory(mContext, revisedLicesneUrl , retailer_val, retailer_val_key, parent_license_type, parent_license_type_key, new CheckUserCallback() {
                                        @Override
                                        public void getExistingUser(JSONArray jsonArray) {
                                        }

                                        @Override
                                        public void getExistingBusiness(JSONArray jsonArray , String msg) {
                                            Log.d("liscentype", "Restaurants fields response");
                                            confirmRevisedCatMsg = msg;
                                            for (int i = 0; i < jsonArray.length(); i++) {

                                                JSONObject jsonObject = jsonArray.optJSONObject(i);

                                                if (parentView.findViewWithTag(jsonObject.optString("key")) instanceof PFAEditText) {
                                                    PFAEditText pfaEditText = parentView.findViewWithTag(jsonObject.optString("key"));
                                                    try {
                                                        pfaEditText.setText(jsonObject.getString("value"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Log.d("liscentype", "Restaurants response not null");
                                                }

                                            }
                                        }
                                    });
                                } else
                                    sharedPrefUtils.showMsgDialog("Please fill above E-Commerce assessment dropdowns", null);
                            }

                        }
                    });

                }
                if (fieldInfo.getField_name().equals("name")) {
                    sampleBrandName = textInputLayout;
                    editTextName = pfaEditText;
                }

                pfaEditText.addTextInputLayout(textInputLayout);

                if (fieldInfo.isInvisible()) {
                    textInputLayout.setVisibility(GONE);
                }

                setSearchOption(pfaEditText, parentView, isSearchFilter);
                if (fieldInfo.getField_name().equals("name")) {

//                    Log.d("yesButton", "name text = " + pfaEditText.getText().toString());
//                    mContext.getSharedPreferences("appPrefs", Context.MODE_PRIVATE).edit().putString("InputTextAdded", pfaEditText.getText().toString()).apply();
                }
                break;

            case "textarea":

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, convertDpToPixel(10), 0, 0);
                textInputLayout.setLayoutParams(params);

                Log.d("enfrocedData", "textArea");

                final PFAEditText pfa1EditText = new PFAEditText(mContext, fieldInfo, formFilteredData);
                pfa1EditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

                textInputLayout.addView(pfa1EditText);
                parentView.addView(textInputLayout);
//                textInputLayoutList.add(textInputLayout);

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

                Log.d("enfrocedData", "numeric");

                textInputLayout.addView(pfaEditText2);
                parentView.addView(textInputLayout);
//                textInputLayoutList.add(textInputLayout);

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

                Log.d("enfrocedData", "phone");

                textInputLayout.addView(pfaEditText4);
                parentView.addView(textInputLayout);
//                textInputLayoutList.add(textInputLayout);

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

                Log.d("enfrocedData", "email");

                textInputLayout.addView(pfaEditText5);
                parentView.addView(textInputLayout);
//                textInputLayoutList.add(textInputLayout);

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

                Log.d("enfrocedData", "date");

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

//        if (fieldInfo.getField_name().equals("applicant_name") || fieldInfo.getField_name().equals("cnic") || fieldInfo.getField_name().equals("designation_Position") ||
//                fieldInfo.getField_name().equals("food_business_name") || fieldInfo.getField_name().equals("office_address") || fieldInfo.getField_name().equals("manufacturing_unit_address") ||
//                fieldInfo.getField_name().equals("landline_phone") || fieldInfo.getField_name().equals("cell_number") || fieldInfo.getField_name().equals("fax") ||
//                fieldInfo.getField_name().equals("email")) {
//
//            textInputLayoutList.add(textInputLayout);
//
//        }

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

        if (fieldInfo.getField_name().equalsIgnoreCase("application_image") ||
                fieldInfo.getField_name().equalsIgnoreCase("application_cnic_image") ||
                fieldInfo.getField_name().equalsIgnoreCase("application_cnic_back_image") ||
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


        deleteImgBtn.setOnClickListener(v -> {
            attachmentCNIV.setImageFile(null);
            attachmentCNIV.setLocalImageBitmap(null);
        });


        printLog("fieldInfo.isAdd_more", "" + (fieldInfo.isAdd_more()));
        if (fieldInfo.isAdd_more()) {
            addMoreImgBtn.setVisibility(VISIBLE);
            addMoreImgBtn.setOnClickListener(v -> {

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

    private void createViewFileView(final LinearLayout parentView, LayoutInflater inflater, final FormSectionInfo formSectionInfo, final FormFieldInfo fieldInfo, LinearLayout imageLayout, final List<FormFieldInfo> fields, final HashMap<String, HashMap<String, Boolean>> sectionRequired, final PFAViewsCallbacks pfaViewsCallbacks2, int fieldCount) {

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
        final ImageButton downloadImgBtn = img_attachment_ll.findViewById(R.id.downloadImgBtn);

        if (fieldInfo.getField_name().equalsIgnoreCase("application_image") ||
                fieldInfo.getField_name().equalsIgnoreCase("application_cnic_image") ||
                fieldInfo.getField_name().equalsIgnoreCase("application_cnic_back_image") ||
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

//            if ((fieldInfo.getIcon().startsWith("http")))
//                attachmentLblTV.setText(Html.fromHtml(isEnglishLang() ? fieldInfo.getValue() : fieldInfo.getValueUrdu()));
//            else
//                attachmentLblTV.setText("Attachment");
        } else {
//            if ((fieldInfo.getIcon().startsWith("http")))
//                attachmentLblTV.setText(Html.fromHtml(isEnglishLang() ? fieldInfo.getValue() : fieldInfo.getValueUrdu()));
//            else
//                attachmentLblTV.setText("Attachment");
            img_attachment_ll.findViewById(R.id.selectImgLblFL).setVisibility(GONE);
        }

        if ((fieldInfo.getValue() != null))
            attachmentLblTV.setText(Html.fromHtml(isEnglishLang() ? fieldInfo.getValue() : fieldInfo.getValueUrdu()));
        else
            attachmentLblTV.setText("Attachment");

        if (fieldInfo.isNotEditable()) {
            deleteImgBtn.setVisibility(GONE);
        }

//        if (fieldInfo.isClickable() && fieldInfo.getData() != null)
//            deleteImgBtn.setVisibility(GONE);

        if ((fieldInfo.getIcon().startsWith("http")) && fieldInfo.isClickable()) {

            Log.d("iconVal", "icon = " + fieldInfo.getIcon());
            if (fieldInfo.getIcon().endsWith("pdf"))
                attachmentCNIV.setDrawable(R.drawable.ic_pdf);
            else if (fieldInfo.getIcon().endsWith("docx"))
                attachmentCNIV.setDrawable(R.drawable.ic_docx);
            else
                attachmentCNIV.setImageUrl(fieldInfo.getIcon(), AppController.getInstance().getImageLoader());
            deleteImgBtn.setVisibility(VISIBLE);
            downloadImgBtn.setVisibility(VISIBLE);

        } else if ((fieldInfo.getIcon().startsWith("http")) && !fieldInfo.isClickable()) {
            Log.d("iconVal", "icon = " + fieldInfo.getIcon());
            if (fieldInfo.getIcon().endsWith("pdf"))
                attachmentCNIV.setDrawable(R.drawable.ic_pdf);
            else if (fieldInfo.getIcon().endsWith("docx"))
                attachmentCNIV.setDrawable(R.drawable.ic_docx);
            else
                attachmentCNIV.setImageUrl(fieldInfo.getIcon(), AppController.getInstance().getImageLoader());
            deleteImgBtn.setVisibility(GONE);
            downloadImgBtn.setVisibility(VISIBLE);
        }
        /*if ((fieldInfo.getData() != null && fieldInfo.getData().size() > 0) && (!fieldInfo.getData().get(0).getValue().equals(""))) {
            if (fieldInfo.getData().get(0).getValue().startsWith("http")) {
                attachmentCNIV.setImageUrl(fieldInfo.getData().get(0).getValue(), AppController.getInstance().getImageLoader());
            } else {
                deleteImgBtn.setVisibility(VISIBLE);
                attachmentCNIV.setFileBitmap(fieldInfo.getData().get(0).getValue());
            }
        }*/
        else {
            attachmentCNIV.setDrawable(R.mipmap.no_img);
            deleteImgBtn.setVisibility(GONE);
            downloadImgBtn.setVisibility(GONE);
        }

       /* if (fieldInfo.getIcon().endsWith("pdf") && fieldInfo.isClickable()){
            Log.d("pdfFilePath" , "path = " + fieldInfo.getIcon());
            attachmentCNIV.setDrawable(R.drawable.ic_pdf);
            deleteImgBtn.setVisibility(VISIBLE);
            downloadImgBtn.setVisibility(VISIBLE);
        } else {
            attachmentCNIV.setDrawable(R.drawable.ic_pdf);
            deleteImgBtn.setVisibility(GONE);
            downloadImgBtn.setVisibility(VISIBLE);
        }*/

       /* if (fieldInfo.getIcon().endsWith("docx") && !fieldInfo.isClickable()){
            Log.d("pdfFilePath" , "path = " + fieldInfo.getIcon());
            attachmentCNIV.setDrawable(R.drawable.ic_docx);
            deleteImgBtn.setVisibility(VISIBLE);
            downloadImgBtn.setVisibility(VISIBLE);
        }
        else {
            attachmentCNIV.setDrawable(R.drawable.ic_docx);
            deleteImgBtn.setVisibility(GONE);
            downloadImgBtn.setVisibility(VISIBLE);
        }*/

        /* else if (fieldInfo.getIcon()!=null){
            attachmentCNIV.setImageUrl(fieldInfo.getIcon() , AppController.getInstance().getImageLoader());
        }*/

        if (fieldInfo.isInvisible()) {
            img_attachment_ll.setVisibility(GONE);
        }

        deleteImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentCNIV.setImageFile(null);
                attachmentCNIV.setLocalImageBitmap(null);
                attachmentCNIV.setDrawable(R.mipmap.no_img);
                deleteImgBtn.setVisibility(GONE);
                downloadImgBtn.setVisibility(GONE);
            }
        });

        downloadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldInfo.getIcon().startsWith("http")) {
//                    Toast.makeText(mContext, "Download started", Toast.LENGTH_SHORT).show();
//                    downloadFile(fieldInfo.getIcon());
                    downloadImgBtn.setVisibility(GONE);

                    //         get the file name to be downloaded from url
                    final String outputFile = URLUtil.guessFileName(fieldInfo.getIcon(), null, null);

//        Create the folder for downloads
                    String rootDir = Environment.getExternalStorageDirectory()
                            + File.separator + "Download";
//                + File.separator + "PFA_Mobile_Downloads";
                    File rootFile = new File(rootDir);

                    if (!rootFile.exists())
                        rootFile.mkdir();

//        End create folder

                    if (sharedPrefUtils.isVideoFile(fieldInfo.getIcon())) {
                        DownloadFileManager.downloadVideo(fieldInfo.getIcon(), new File(rootFile, outputFile), pbListener, mContext);
                    } else {
                        DownloadFileManager.downloadImage(fieldInfo.getIcon(), new File(rootFile, outputFile), pbListener, mContext);
                    }

                }
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
            if (deleteImgBtn != null)
                attachmentCNIV.setDeleteImgBtn(deleteImgBtn);
            else
                Log.d("deleteIMageBtn", "dlete button null");
            attachmentCNIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pfaViewsCallbacks != null)
                        pfaViewsCallbacks.showFilePickerDialog(attachmentCNIV);
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

/*

    private void createMultipleInputFields(final LinearLayout parentView, LayoutInflater inflater, final FormSectionInfo formSectionInfo, final FormFieldInfo fieldInfo, LinearLayout imageLayout, final List<FormFieldInfo> fields, final HashMap<String, HashMap<String, Boolean>> sectionRequired, final PFAViewsCallbacks pfaViewsCallbacks2, int fieldCount) {


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

        if (fieldInfo.getField_name().equalsIgnoreCase("application_image") ||
                fieldInfo.getField_name().equalsIgnoreCase("application_cnic_image") ||
                fieldInfo.getField_name().equalsIgnoreCase("application_cnic_back_image") ||
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

*/

    private void createViewDropdown(final FormFieldInfo fieldInfo, final LinearLayout parentView, LayoutInflater inflater) {
        Log.d("checkDropDown", "drop down opened");
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
            params1.setMargins(0, 0, 0, 0);
            pfaddLL.setLayoutParams(params1);

            pfaddLL.setTag((fieldInfo.getField_name()) + "01");

            PFATextInputLayout textInputLayout = pfaddLL.findViewById(R.id.pfaAdd_IPL);
            TextView txtPopulateLicense = pfaddLL.findViewById(R.id.txtPopulateLicense);
            textInputLayout.setProperties(fieldInfo);


            final ImageButton clearImgBtn = pfaddLL.findViewById(R.id.clearImgBtn);

            final PFADDACTV pfa_dd_actv = pfaddLL.findViewById(R.id.pfa_dd_actv);
            pfa_dd_actv.setText("");

            pfa_dd_actv.setHint(isEnglishLang() ? fieldInfo.getValue() : fieldInfo.getValueUrdu());
            pfa_dd_actv.setDDCallback(DDCallback);

            if (fieldInfo.getField_name().equals("revised_license_type")) {
                txtPopulateLicense.setVisibility(VISIBLE);
                pfa_dd_actv.setEnabled(false);

            } else {
                pfa_dd_actv.setEnabled(true);
                txtPopulateLicense.setVisibility(GONE);
            }

            if (fieldInfo.getField_name().equals("local_business"))
                localBusinessDropDownUrl = fieldInfo.getAPI_URL();
            if (fieldInfo.getField_name().equals("product_category")) {
                product_categoryDD = pfa_dd_actv;
                Log.d("dropDOwnCat", "here 1");
            }
            if (fieldInfo.getField_name().equals("revise_business_category"))
                revisedBusinessCatDropDownUrl = fieldInfo.getAPI_URL();
            if (fieldInfo.getField_name().equals("parent_business_category"))
                parentBusinessCatUrl = fieldInfo.getAPI_URL();

            if (fieldInfo.getField_name().equals("parent_license_type")) {
                parentLicenseVal = fieldInfo.getDefault_value();
                printLog("parentLicenseVal", "whichItemClicked=>");
            }
            if (fieldInfo.getField_name().equals("revise_business_category")){
                revBusinessCategoryURL = fieldInfo.getAPI_URL();
            }

            if (fieldInfo.getField_name().equals("clientid")) {

                pfa_dd_actv.setProperties(new WhichItemClicked() {
                    @Override
                    public void whichItemClicked(String id) {
                        printLog("createViewDropdown", "whichItemClicked=>" + id);
                        Log.d("createDropDoewn", "id = " + id);

//                        if (fieldInfo.getField_name().equals("clientid")) {
                        if (id.equals("others")) {

                            Log.d("createDropDoewn", "id = others");
                            if (parentView.findViewWithTag("local_business") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("local_business");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }

                            if (parentView.findViewWithTag("business_name") instanceof PFAEditText) {

                                for (int i = 0; i < linerList.size(); i++) {
                                    LinearLayout linearLayout = linerList.get(i);
                                    linearLayout.setVisibility(GONE);
                                }
//                                for (int i = 0; i < textInputLayoutList.size(); i++) {
//                                    PFATextInputLayout pfaTextInputLayout = textInputLayoutList.get(i);
//                                    parentView.addView(pfaTextInputLayout);
//                                }
                            }

//                            if (parentView.findViewWithTag("applicant_name") instanceof PFAEditText) {
//
//
//                            }

                            if (parentView.findViewWithTag("applicant_name") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("applicant_name");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("cnic") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("cnic");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("designation_Position") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("designation_Position");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("food_business_name") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("food_business_name");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("provinces") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("provinces");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("office_address") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("office_address");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturing_unit_address") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("manufacturing_unit_address");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("landline_phone") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("landline_phone");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("cell_number") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("cell_number");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("fax") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("fax");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("email") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("email");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            for (int i = 0; i < inputTextList.size(); i++) {
                                TextView textView = inputTextList.get(i);
                                textView.setVisibility(VISIBLE);
                            }

                        } else {

                            Log.d("createDropDoewn", "id = else");
                            if (parentView.findViewWithTag("local_business") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("local_business");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                                Log.d("createDropDoewn", "id = else 1");
                            } else
                                Log.d("createDropDoewn", "id = else else");

                            if (parentView.findViewWithTag("business_name") instanceof PFAEditText) {

                                for (int i = 0; i < linerList.size(); i++) {
                                    LinearLayout linearLayout = linerList.get(i);
                                    linearLayout.setVisibility(VISIBLE);
                                }
//                                for (int i = 0; i < textInputLayoutList.size(); i++) {
//                                    PFATextInputLayout pfaTextInputLayout = textInputLayoutList.get(i);
//                                    parentView.removeView(pfaTextInputLayout);
//                                }
                            }

//                            if (parentView.findViewWithTag("applicant_name") instanceof PFAEditText) {


//                            }

                            if (parentView.findViewWithTag("applicant_name") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("applicant_name");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("cnic") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("cnic");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("designation_Position") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("designation_Position");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("food_business_name") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("food_business_name");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("provinces") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("provinces");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("office_address") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("office_address");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturing_unit_address") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("manufacturing_unit_address");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("landline_phone") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("landline_phone");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("cell_number") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("cell_number");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("fax") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("fax");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("email") instanceof PFAEditText) {
                                PFAEditText pfaddactv = parentView.findViewWithTag("email");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            for (int i = 0; i < inputTextList.size(); i++) {
                                TextView textView = inputTextList.get(i);
                                textView.setVisibility(GONE);
                            }
                        }
//                        }

                        final VerifyFBOBusiness verifyFBOLayout = new VerifyFBOBusiness(mContext, id, fieldInfo.getAPI_URL(), new CheckUserCallback() {
                            @Override
                            public void getExistingUser(JSONArray jsonArray) {

                            }

                            @Override
                            public void getExistingBusiness(JSONArray jsonArray , String msg) {
                                Log.d("createViewGetCodeButton", "createViewGetCodeButton1 dropDOwn");
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.optJSONObject(i);

//                                    if (parentView.findViewWithTag(jsonObject.optString("key")) instanceof PFAEditText) {
//                                        PFAEditText pfaEditText = parentView.findViewWithTag(jsonObject.optString("key"));
//                                        pfaEditText.setText(null);
//                                        Log.d("createViewGetCodeButton", "createViewGetCodeButton1 set text null");
//                                    }


                                    if (parentView.findViewWithTag(jsonObject.optString("key")) != null && parentView.findViewWithTag(jsonObject.optString("key")) instanceof PFAEditText) {
                                        PFAEditText pfaEditText = parentView.findViewWithTag(jsonObject.optString("key"));
                                        pfaEditText.setText(Html.fromHtml(jsonObject.optString("value")));

                                        Log.d("createViewGetDDVal", "editext value = " + Html.fromHtml(jsonObject.optString("value")));
                                        Log.d("createViewGetDDValues", "editext name = " + jsonObject.optString("key"));
                                        Log.d("createViewGetDDValues", "editext value = " + Html.fromHtml(jsonObject.optString("value")));

                                        if (!jsonObject.optBoolean("is_editable")) {
                                            pfaEditText.setEnabled(false);
                                            pfaEditText.setClickable(false);
                                            pfaEditText.setFocusable(false);

                                            pfaEditText.setBackgroundColor(mContext.getResources().getColor(R.color.chat_list_footer_bg));

                                        } else {
                                            pfaEditText.setFocusable(true);
                                            pfaEditText.setFocusableInTouchMode(true);
                                            pfaEditText.setEnabled(true);
                                            pfaEditText.setClickable(true);

                                            pfaEditText.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                                        }

//                                        if (jsonObject.optBoolean("required")){
//                                            pfaEditText.setRequired(true);
//                                        } else
//                                            pfaEditText.setRequired(false);
                                    }

                                }
                            }
                        });
                        verifyFBOLayout.setTag(fieldInfo.getField_name());

                        parentView.addView(verifyFBOLayout);

                        if (fieldInfo.isInvisible()) {
                            verifyFBOLayout.setVisibility(GONE);
                        }

                        printLog("createViewDropdown", "whichItemClicked=>" + fieldInfo.getAPI_URL());

                    }

                    @Override
                    public void downloadInspection(String downloadUrl, int position) {
                        printLog("createViewDropdown", "downloadInspection  position=> " + downloadUrl + " position=> " + position);

                    }

                    @Override
                    public void deleteRecordAPICall(String deleteUrl, int position) {

                    }
                }, fieldInfo, formFilteredData);
            }

            /*else if (fieldInfo.getField_name().equals("local_business")) {

                pfa_dd_actv.setProperties(new WhichItemClicked() {
                    @Override
                    public void whichItemClicked(String id) {
                        printLog("createViewDropdown", "whichItemClicked=>" + id);
                        Log.d("createDropDoewn", "API_URL() = " + fieldInfo.getAPI_URL());

                        final VerifyFBOBusiness verifyFBOLayout = new VerifyFBOBusiness(mContext, id, fieldInfo.getAPI_URL(), new CheckUserCallback() {
                            @Override
                            public void getExistingUser(JSONArray jsonArray) {

                            }

                            @Override
                            public void getExistingBusiness(JSONArray jsonArray) {
                                Log.d("createViewGetCodeButton", "createViewGetCodeButton1 dropDOwn");
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.optJSONObject(i);

                                    if (parentView.findViewWithTag(jsonObject.optString("key")) != null && parentView.findViewWithTag(jsonObject.optString("key")) instanceof PFAEditText) {
                                        PFAEditText pfaEditText = parentView.findViewWithTag(jsonObject.optString("key"));
                                        pfaEditText.setText(Html.fromHtml(jsonObject.optString("value")));

                                        Log.d("createViewGetCodeButton", "createViewGetCodeButton1 set received text");

                                        if (!jsonObject.optBoolean("is_editable")) {
                                            pfaEditText.setEnabled(false);
                                            pfaEditText.setClickable(false);
                                            pfaEditText.setFocusable(false);

                                            pfaEditText.setBackgroundColor(mContext.getResources().getColor(R.color.chat_list_footer_bg));

                                        } else {
                                            pfaEditText.setFocusable(true);
                                            pfaEditText.setFocusableInTouchMode(true);
                                            pfaEditText.setEnabled(true);
                                            pfaEditText.setClickable(true);

                                            pfaEditText.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                                        }
                                    }

                                }
                            }
                        });
                        verifyFBOLayout.setTag(fieldInfo.getField_name());

                        parentView.addView(verifyFBOLayout);

                        if (fieldInfo.isInvisible()) {
                            verifyFBOLayout.setVisibility(GONE);
                        }

                        printLog("createViewDropdown", "whichItemClicked=>" + fieldInfo.getAPI_URL());

                    }

                    @Override
                    public void downloadInspection(String downloadUrl, int position) {
                        printLog("createViewDropdown", "downloadInspection  position=> " + downloadUrl + " position=> " + position);

                    }

                    @Override
                    public void deleteRecordAPICall(String deleteUrl, int position) {

                    }
                }, fieldInfo, formFilteredData);
            }*/

            else {
                Log.d("checkDD", "id = not others");
                new Handler().postDelayed(() -> {
                    if (parentView.findViewWithTag("local_business") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("local_business");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                        Log.d("checkDD", "id = local_business");
                    }

//                    for (int i = 0; i < textInputLayoutList.size(); i++) {
//                        PFATextInputLayout pfaTextInputLayout = textInputLayoutList.get(i);
//                        parentView.removeView(pfaTextInputLayout);
//                    }

                    if (parentView.findViewWithTag("applicant_name") instanceof PFAEditText) {
                        PFAEditText pfaddactv0 = parentView.findViewWithTag("applicant_name");
                        pfaddactv0.setVisibility(GONE);
                        pfaddactv0.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("cnic") instanceof PFAEditText) {
                        PFAEditText pfaddactv1 = parentView.findViewWithTag("cnic");
                        pfaddactv1.setVisibility(GONE);
                        pfaddactv1.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("designation_Position") instanceof PFAEditText) {
                        PFAEditText pfaddactv2 = parentView.findViewWithTag("designation_Position");
                        pfaddactv2.setVisibility(GONE);
                        pfaddactv2.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("food_business_name") instanceof PFAEditText) {
                        PFAEditText pfaddactv3 = parentView.findViewWithTag("food_business_name");
                        pfaddactv3.setVisibility(GONE);
                        pfaddactv3.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("provinces") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("provinces");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("office_address") instanceof PFAEditText) {
                        PFAEditText pfaddactv4 = parentView.findViewWithTag("office_address");
                        pfaddactv4.setVisibility(GONE);
                        pfaddactv4.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("manufacturing_unit_address") instanceof PFAEditText) {
                        PFAEditText pfaddactv4 = parentView.findViewWithTag("manufacturing_unit_address");
                        pfaddactv4.setVisibility(GONE);
                        pfaddactv4.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("landline_phone") instanceof PFAEditText) {
                        PFAEditText pfaddactv5 = parentView.findViewWithTag("landline_phone");
                        pfaddactv5.setVisibility(GONE);
                        pfaddactv5.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("cell_number") instanceof PFAEditText) {
                        PFAEditText pfaddactv6 = parentView.findViewWithTag("cell_number");
                        pfaddactv6.setVisibility(GONE);
                        pfaddactv6.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("fax") instanceof PFAEditText) {
                        PFAEditText pfaddactv7 = parentView.findViewWithTag("fax");
                        pfaddactv7.setVisibility(GONE);
                        pfaddactv7.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("email") instanceof PFAEditText) {
                        PFAEditText pfaddactv8 = parentView.findViewWithTag("email");
                        pfaddactv8.setVisibility(GONE);
                        pfaddactv8.showHideDropDown(false);
                    }
//                    if (inputTextList!=null)
                    for (int i = 0; i < inputTextList.size(); i++) {
                        TextView textView = inputTextList.get(i);
                        textView.setVisibility(GONE);
                    }


                }, 300);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (fieldInfo.getField_name().equals("labs_branches")) {
                            if (dropDownId.equals("11")) {

                                if (parentView.findViewWithTag("product_name_dropdown") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("product_name_dropdown");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                    pfaddactv.setRequired(true);
                                    if (pfaddactv.getText().toString().equals("Add New")) {
                                        Log.d("dropDOwnCat", "value 123 1= ");
                                        if (parentView.findViewWithTag("product_category") instanceof PFADDACTV) {
                                            PFADDACTV pfaddactv1 = parentView.findViewWithTag("product_category");
                                            pfaddactv1.setVisibility(VISIBLE);
                                            pfaddactv1.showHideDropDown(true);
                                            pfaddactv1.setRequired(true);
                                        }
                                        if (parentView.findViewWithTag("name") instanceof PFAEditText) {
                                            PFAEditText pfaddactv1 = parentView.findViewWithTag("name");
                                            pfaddactv1.setVisibility(VISIBLE);
                                            pfaddactv1.showHideDropDown(true);
                                            pfaddactv1.setRequired(true);
                                        }
                                    }
                                }

                                if (parentView.findViewWithTag("product_category") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("product_category");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                    pfaddactv.setRequired(false);
                                }
                                if (parentView.findViewWithTag("name") instanceof PFAEditText) {
                                    PFAEditText pfaddactv = parentView.findViewWithTag("name");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                    pfaddactv.setRequired(false);
                                }
                                mContext.getSharedPreferences("appPrefs", Context.MODE_PRIVATE).edit().putBoolean("InputVisible", false).apply();
//                                if (textView_name!=null)
                                textView_name.setVisibility(GONE);
//                                parentView.removeView(sampleBrandName);

                            } else {

                                if (parentView.findViewWithTag("product_name_dropdown") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("product_name_dropdown");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                    pfaddactv.setRequired(false);
                                }

                                if (parentView.findViewWithTag("product_category") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("product_category");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                    pfaddactv.setRequired(true);
                                }
                                if (parentView.findViewWithTag("name") instanceof PFAEditText) {
                                    PFAEditText pfaddactv = parentView.findViewWithTag("name");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                    pfaddactv.setRequired(true);
                                }
                                mContext.getSharedPreferences("appPrefs", Context.MODE_PRIVATE).edit().putBoolean("InputVisible", true).apply();
//                                if (textView_name!=null)
                                textView_name.setVisibility(GONE);
//                                parentView.removeView(sampleBrandNameDD);
                            }
                        }
                    }
                }, 300);

              /*  new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (parentLicenseVal != null && !parentLicenseVal.isEmpty()){
                            Log.d("parentLicenseVal", "parentLicenseVal = " + parentLicenseVal);
                            if (parentLicenseVal.equals("Retailer")){
                                Log.d("parentLicenseVal", "parentLicenseVal Retailer = ");
                            if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_bills_empty") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills_empty");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                        }
                            else if (parentLicenseVal.equals("Manufacturer")){
                            if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_bills_empty") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills_empty");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                        }
                            else if (parentLicenseVal.equals("Restaurants")){
                            if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_bills_empty") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills_empty");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                        }
                            else if (parentLicenseVal.equals("E-Commerce")){
                            if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_bills_empty") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills_empty");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(true);
                            }
                        }
                        }
                    }
                } , 500);*/
/*

                if (fieldInfo.getField_name().equals("manufacturer_investment") ||
                        fieldInfo.getField_name().equals("manufacturer_inventory") ||
                        fieldInfo.getField_name().equals("manufacturer_units_produced") ||
                        fieldInfo.getField_name().equals("manufacturer_sales") ||
                        fieldInfo.getField_name().equals("manufacturer_rent") ||
                        fieldInfo.getField_name().equals("manufacturer_employees") ||
                        fieldInfo.getField_name().equals("manufacturer_machines") ||
                        fieldInfo.getField_name().equals("manufacturer_products") ||
                        fieldInfo.getField_name().equals("manufacturer_bills") ||
                        fieldInfo.getField_name().equals("restaurant_investment") ||
                        fieldInfo.getField_name().equals("restaurant_inventory") ||
                        fieldInfo.getField_name().equals("restaurant_sales") ||
                        fieldInfo.getField_name().equals("restaurant_rent") ||
                        fieldInfo.getField_name().equals("restaurant_employees") ||
                        fieldInfo.getField_name().equals("restaurant_seating_capacity") ||
                        fieldInfo.getField_name().equals("restaurant_location") ||
                        fieldInfo.getField_name().equals("restaurant_menu_range") ||
                        fieldInfo.getField_name().equals("restaurant_bills") ||
                        fieldInfo.getField_name().equals("e_commerce_investment") ||
                        fieldInfo.getField_name().equals("e_commerce_sales") ||
                        fieldInfo.getField_name().equals("e_commerce_employees") ||
                        fieldInfo.getField_name().equals("e_commerce_riders") ||
                        fieldInfo.getField_name().equals("e_commerce_registered_fbos") ||
                        fieldInfo.getField_name().equals("restaurant_bills_empty")) {
                    if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("restaurant_bills_empty") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills_empty");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                    if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                        PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                        pfaddactv.setVisibility(GONE);
                        pfaddactv.showHideDropDown(false);
                    }
                }
*/

                pfa_dd_actv.setProperties(new WhichItemClicked() {
                    @Override
                    public void whichItemClicked(String id) {
                        printLog("createViewDropdown", "whichItemClicked123=>" + id);
                        Log.d("createViewDropdown", "value 123 = " + fieldInfo.getField_name());

                        dropDownId = id;

                        if (fieldInfo.getField_name().equals("labs_branches")) {
                            if (!firstTime) {
//                                    if (textView_name != null)
                                textView_name.setVisibility(GONE);
                                if (Objects.requireNonNull(editTextName.getText()).toString() != null && !editTextName.getText().toString().isEmpty())
                                    editTextName.setText("");
                                if (product_categoryDD.getText() != null /*&& !product_categoryDD.getText().toString().isEmpty()*/) {
                                    product_categoryDD.setText("");
                                    product_categoryDD.setSelectedValues(null);

                                    if (pfaViewsCallbacks != null)
                                        pfaViewsCallbacks.onDropdownItemSelected(null, product_categoryDD.formFieldInfo.getField_name());
                                    product_categoryDD.clearFocus();
                                    clearFocusOfAllViews(parentView);
                                    Log.d("dropDOwnCat", "here");
                                }
                            }
                            firstTime = false;
                            if (id.equals("11")) {

                                if (parentView.findViewWithTag("product_name_dropdown") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("product_name_dropdown");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                    pfaddactv.setRequired(true);
                                    if (pfaddactv.getText().toString().equals("Add New")) {
                                        Log.d("dropDOwnCat", "value 123 = ");
                                        if (parentView.findViewWithTag("product_category") instanceof PFADDACTV) {
                                            PFADDACTV pfaddactv1 = parentView.findViewWithTag("product_category");
                                            pfaddactv1.setVisibility(VISIBLE);
                                            pfaddactv1.showHideDropDown(true);
                                            pfaddactv1.setRequired(true);
                                        }
                                        if (parentView.findViewWithTag("name") instanceof PFAEditText) {
                                            PFAEditText pfaddactv1 = parentView.findViewWithTag("name");
                                            pfaddactv1.setVisibility(VISIBLE);
                                            pfaddactv1.showHideDropDown(true);
                                            pfaddactv1.setRequired(true);
                                        }
                                    }
                                }

                                if (parentView.findViewWithTag("product_category") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("product_category");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                    pfaddactv.setRequired(false);
                                }
                                if (parentView.findViewWithTag("name") instanceof PFAEditText) {
                                    PFAEditText pfaddactv = parentView.findViewWithTag("name");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                    pfaddactv.setRequired(false);
                                }
                                mContext.getSharedPreferences("appPrefs", Context.MODE_PRIVATE).edit().putBoolean("InputVisible", false).apply();
//                                if (textView_name!=null)
                                textView_name.setVisibility(GONE);

                                //                                parentView.removeView(sampleBrandName);
//                                parentView.addView(sampleBrandNameDD , 2);

                            } else {


                                if (parentView.findViewWithTag("product_name_dropdown") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("product_name_dropdown");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                    pfaddactv.setRequired(false);
                                }

                                if (parentView.findViewWithTag("product_category") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("product_category");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                    pfaddactv.setRequired(true);
                                }
                                if (parentView.findViewWithTag("name") instanceof PFAEditText) {
                                    PFAEditText pfaddactv = parentView.findViewWithTag("name");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                    pfaddactv.setRequired(true);
                                }
                                mContext.getSharedPreferences("appPrefs", Context.MODE_PRIVATE).edit().putBoolean("InputVisible", true).apply();
//                                if (textView_name!=null)
                                textView_name.setVisibility(GONE);
//                                parentView.removeView(sampleBrandNameDD);
//                                parentView.addView(sampleBrandName , 2);

                            }
                        }
                        else if (fieldInfo.getField_name().equals("product_name_dropdown")) {
                            if (!firstTime) {
//                                    if (textView_name != null)
                                textView_name.setVisibility(GONE);
                                if (Objects.requireNonNull(editTextName.getText()).toString() != null && !editTextName.getText().toString().isEmpty())
                                    editTextName.setText("");
                                if (product_categoryDD.getText() != null /*&& !product_categoryDD.getText().toString().isEmpty()*/) {
                                    product_categoryDD.setText("");
                                    product_categoryDD.setSelectedValues(null);

                                    if (pfaViewsCallbacks != null)
                                        pfaViewsCallbacks.onDropdownItemSelected(null, product_categoryDD.formFieldInfo.getField_name());
                                    product_categoryDD.clearFocus();
                                    clearFocusOfAllViews(parentView);
                                    Log.d("dropDOwnCat", "here");
                                }
                            }
                            if (id.equals("add_new")) {
                                if (parentView.findViewWithTag("product_category") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("product_category");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                    pfaddactv.setRequired(true);
                                }
                                if (parentView.findViewWithTag("name") instanceof PFAEditText) {
                                    PFAEditText pfaddactv = parentView.findViewWithTag("name");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                    pfaddactv.setRequired(true);
                                }
                                mContext.getSharedPreferences("appPrefs", Context.MODE_PRIVATE).edit().putBoolean("InputVisible", true).apply();

                                firstTime = false;
                            } else {
                                if (parentView.findViewWithTag("product_category") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("product_category");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                    pfaddactv.setRequired(false);
                                }
                                if (parentView.findViewWithTag("name") instanceof PFAEditText) {
                                    PFAEditText pfaddactv = parentView.findViewWithTag("name");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                    pfaddactv.setRequired(false);
                                }
                                mContext.getSharedPreferences("appPrefs", Context.MODE_PRIVATE).edit().putBoolean("InputVisible", false).apply();
                                if (!firstTime) {
//                                    if (textView_name != null)
                                    textView_name.setVisibility(GONE);

                                }
                                firstTime = false;
                            }
                        }
                        else if (fieldInfo.getField_name().equals("local_business")) {
                            /*final VerifyFBOBusiness verifyFBOLayout =*/

                            if (id.equals("add_new")) {
                                for (int i = 0; i < inputTextList.size(); i++) {
                                    TextView textView = inputTextList.get(i);
                                    textView.setVisibility(VISIBLE);
                                }
                            } else {
                                for (int i = 0; i < inputTextList.size(); i++) {
                                    TextView textView = inputTextList.get(i);
                                    textView.setVisibility(GONE);
                                }
                            }

                            new VerifyFBOBusiness(mContext, dropDownId, localBusinessDropDownUrl, new CheckUserCallback() {
                                @Override
                                public void getExistingUser(JSONArray jsonArray) {

                                }

                                @Override
                                public void getExistingBusiness(JSONArray jsonArray , String msg) {
                                    Log.d("createViewGetCodeButton", "localBusinessDropDownUrl = " + localBusinessDropDownUrl);
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonObject = jsonArray.optJSONObject(i);

                                        if (parentView.findViewWithTag(jsonObject.optString("key")) != null && parentView.findViewWithTag(jsonObject.optString("key")) instanceof PFAEditText) {
                                            PFAEditText pfaEditText = parentView.findViewWithTag(jsonObject.optString("key"));
                                            pfaEditText.setText(Html.fromHtml(jsonObject.optString("value")));

                                            Log.d("createViewGetCodeButton", "createViewGetCodeButton1 set received text");

                                            if (!jsonObject.optBoolean("is_editable")) {
                                                pfaEditText.setEnabled(false);
                                                pfaEditText.setClickable(false);
                                                pfaEditText.setFocusable(false);

                                                pfaEditText.setBackgroundColor(mContext.getResources().getColor(R.color.chat_list_footer_bg));

                                            } else {
                                                pfaEditText.setFocusable(true);
                                                pfaEditText.setFocusableInTouchMode(true);
                                                pfaEditText.setEnabled(true);
                                                pfaEditText.setClickable(true);

                                                pfaEditText.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                                            }
                                        }
                                        if (parentView.findViewWithTag(jsonObject.optString("key")) != null && parentView.findViewWithTag(jsonObject.optString("key")) instanceof PFADDACTV) {
                                            PFADDACTV pfaddactv = parentView.findViewWithTag(jsonObject.optString("key"));
                                            pfaddactv.setText(Html.fromHtml(jsonObject.optString("value")));

                                            if (!jsonObject.optBoolean("is_editable")) {
                                                pfaddactv.setEnabled(false);
                                                pfaddactv.setClickable(false);
                                                pfaddactv.setFocusable(false);

                                                pfaddactv.setBackgroundColor(mContext.getResources().getColor(R.color.chat_list_footer_bg));

                                            }
                                        }

                                    }
                                }
                            });
                        }
                        else if (fieldInfo.getField_name().equals("parent_business_category") && parentBusinessCatUrl != null){
                            new PopulateParentLicense(mContext, parentBusinessCatUrl , id, new CheckUserCallback() {
                                @Override
                                public void getExistingUser(JSONArray jsonArray) {
                                }

                                @Override
                                public void getExistingBusiness(JSONArray jsonArray , String msg) {
                                    Log.d("liscentype", "business category field repsone");
                                    List<String> listItemNames = new ArrayList<>();
                                    JSONObject jsonObject = null;
                                    List<FormDataInfo> data = new ArrayList<>();
                                    FormDataInfo formDataInfo = new FormDataInfo();
                                    if (parentView.findViewWithTag("business_category") instanceof PFADDACTV){
                                        PFADDACTV pfaddactv = parentView.findViewWithTag("business_category");
                                        pfaddactv.setText("");
                                        pfaddactv.setSelectedValues(null);

                                        if (pfaViewsCallbacks != null)
                                            pfaViewsCallbacks.onDropdownItemSelected(null, pfaddactv.formFieldInfo.getField_name());
                                        pfaddactv.clearFocus();
                                        clearFocusOfAllViews(parentView);

                                        if (pfaddactv.formFieldInfo.getField_name() != null && (pfaddactv.formFieldInfo.getField_name().equalsIgnoreCase(DD_STATUS))) {

                                            if (pfaddBizSizeACTV == null && parentView.findViewWithTag(DD_BIZ_SIZE) != null) {
                                                pfaddBizSizeACTV = parentView.findViewWithTag(DD_BIZ_SIZE);
                                            }

                                            if (pfaddBizSizeACTV != null) {
                                                setPFABizSizeACTVStatus(false, GONE);
                                            }
                                        }
                                    }


                                        Type type = new TypeToken<List<FormDataInfo>>() {
                                        }.getType();
                                        data = new GsonBuilder().create().fromJson(jsonArray.toString(), type);
//                                                formDataInfo.setName(jsonObject.getString("name"));
//                                                formDataInfo.setKey(jsonObject.getString("key"));
//                                                formDataInfo.setValue(jsonObject.getString("value"));
//                                                data.set(i , formDataInfo);
//                                        Log.d("BusinessCattype", "array i = " + i);

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        jsonObject = jsonArray.optJSONObject(i);


                                        if (parentView.findViewWithTag(jsonObject.optString("name")) instanceof PFADDACTV) {

                                            try {
                                            listItemNames.add(jsonObject.getString("value"));
                                             } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                        }
                                    }
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("business_category");
//                                    data.add(formDataInfo);
                                    if (parentView.findViewWithTag("business_category") instanceof PFADDACTV){
                                        pfaddactv.formFieldInfo.setData(data);
                                    Log.d("BusinessCattype", "data size = " + data.size());
                                    }
                                    pfaddactv.listItemNames.clear();
                                    pfaddactv.listItemNames = listItemNames;
                                }
                            });
                        }

                        if (id.equals("Retailer")) {
                            parent_license_type = "Retailer";
                            parent_license_type_key = "ParentLicenseType";
                            Log.d("liscentype", "Retailer dd onChange");
                            if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);

                            }
                            if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                Log.d("restaurantBills", "on retailer changed");
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                        }
                        if (id.equals("Manufacturer")) {
                            parent_license_type = "Manufacturer";
                            parent_license_type_key = "ParentLicenseType";
                            Log.d("liscentype", "Manufacturer dd onChange");
                            if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                        }
                        if (id.equals("Restaurants")) {
                            parent_license_type = "Restaurants";
                            parent_license_type_key = "ParentLicenseType";
                            Log.d("liscentype", "Restaurants dd onChange");
                            if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                                pfa_dd_actv.formFieldInfo.setInvisible(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                        }
                        if (id.equals("E-Commerce")) {
                            parent_license_type = "E-Commerce";
                            parent_license_type_key = "ParentLicenseType";
                            Log.d("liscentype", "E-Commerce dd onChange");
                            if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                pfaddactv.setVisibility(GONE);
                                pfaddactv.showHideDropDown(false);
                            }
                            if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                            if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                pfaddactv.setVisibility(VISIBLE);
                                pfaddactv.showHideDropDown(true);
                            }
                        }

                        if (fieldInfo.getField_name().equals("revise_business_category")){
                            new PopulateParentLicense(mContext, revBusinessCategoryURL , id, new CheckUserCallback() {
                                @Override
                                public void getExistingUser(JSONArray jsonArray) {
                                }

                                @Override
                                public void getExistingBusiness(JSONArray jsonArray , String msg) {
                                    Log.d("liscentype", "business category field repsone");
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                            JSONObject jsonObject = jsonArray.optJSONObject(i);

                                            if (parentView.findViewWithTag(jsonObject.optString("key")) instanceof PFADDACTV) {
                                                PFADDACTV pfaEditText = parentView.findViewWithTag(jsonObject.optString("key"));
                                                try {
                                                    pfaEditText.setText(jsonObject.getString("value"));
                                                    if (!jsonObject.optBoolean("is_editable")) {
                                                        pfaEditText.setEnabled(false);
                                                        pfaEditText.setClickable(false);
                                                        pfaEditText.setFocusable(false);

                                                        pfaEditText.setBackgroundColor(mContext.getResources().getColor(R.color.chat_list_footer_bg));

                                                    }else {
                                                        pfaEditText.setEnabled(true);
                                                        pfaEditText.setClickable(true);
                                                        pfaEditText.setFocusable(true);

                                                        pfaEditText.setBackgroundColor(mContext.getResources().getColor(R.color.white));

                                                    }

                                                    switch (jsonObject.getString("value")) {
                                                        case "Retailer":
                                                            parent_license_type = "Retailer";
                                                            parent_license_type_key = "ParentLicenseType";
                                                            Log.d("liscentype", "Retailer dd onChange");
                                                            if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);

                                                            }
                                                            if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                                                Log.d("restaurantBills", "on retailer changed");
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            break;
                                                        case "Manufacturer":
                                                            parent_license_type = "Manufacturer";
                                                            parent_license_type_key = "ParentLicenseType";
                                                            Log.d("liscentype", "Manufacturer dd onChange");
                                                            if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            break;
                                                        case "Restaurants":
                                                            parent_license_type = "Restaurants";
                                                            parent_license_type_key = "ParentLicenseType";
                                                            Log.d("liscentype", "Restaurants dd onChange");
                                                            if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                                pfa_dd_actv.formFieldInfo.setInvisible(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            break;
                                                        case "E-Commerce":
                                                            parent_license_type = "E-Commerce";
                                                            parent_license_type_key = "ParentLicenseType";
                                                            Log.d("liscentype", "E-Commerce dd onChange");
                                                            if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                                                pfaddactv.setVisibility(GONE);
                                                                pfaddactv.showHideDropDown(false);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                                                PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                                                pfaddactv.setVisibility(VISIBLE);
                                                                pfaddactv.showHideDropDown(true);
                                                            }
                                                            break;
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                Log.d("liscentype", "Retailer respones not null");
                                            }


                                    }
                                }
                            });
                        }

                        if (fieldInfo.getField_name().equals("retailer_investment")) {
                            retailer_investment_key = "retailer_investment";
                            retailer_investment = id;
                            Log.d("reatilaerFiled", "key = " + retailer_investment_key);
                            Log.d("reatilaerFiled", "value = " + retailer_investment);
                        }
                        if (fieldInfo.getField_name().equals("retailer_avg_sale")) {
                            retailer_avg_sale_key = "retailer_avg_sale";
                            retailer_avg_sale = id;
                            Log.d("reatilaerFiled", "key = " + retailer_avg_sale_key);
                            Log.d("reatilaerFiled", "value = " + retailer_avg_sale);
                        }
                        if (fieldInfo.getField_name().equals("retailer_rent")) {
                            retailer_rent_key = "retailer_rent";
                            retailer_rent = id;
                            Log.d("reatilaerFiled", "key = " + retailer_rent_key);
                            Log.d("reatilaerFiled", "value = " + retailer_rent);
                        }
                        if (fieldInfo.getField_name().equals("retailer_employees")) {
                            retailer_employees_key = "retailer_employees";
                            retailer_employees = id;
                            Log.d("reatilaerFiled", "key = " + retailer_employees_key);
                            Log.d("reatilaerFiled", "value = " + retailer_employees);
                        }
                        if (fieldInfo.getField_name().equals("retailer_location")) {
                            retailer_location_key = "retailer_location";
                            retailer_location = id;
                            Log.d("reatilaerFiled", "key = " + retailer_location_key);
                            Log.d("reatilaerFiled", "value = " + retailer_location);
                        }
                        if (fieldInfo.getField_name().equals("retailer_bills")) {
                            retailer_bills_key = "retailer_bills";
                            retailer_bills = id;
                            Log.d("reatilaerFiled", "key = " + retailer_bills_key);
                            Log.d("reatilaerFiled", "value = " + retailer_bills);
                        }

                        if (fieldInfo.getField_name().equals("manufacturer_investment")) {
                            manufacturer_investment_key = "manufacturer_investment";
                            manufacturer_investment = id;
                        }
                        if (fieldInfo.getField_name().equals("manufacturer_inventory")) {
                            manufacturer_inventory_key = "manufacturer_inventory";
                            manufacturer_inventory = id;
                        }
                        if (fieldInfo.getField_name().equals("manufacturer_units_produced")) {
                            manufacturer_units_produced_key = "manufacturer_units_produced";
                            manufacturer_units_produced = id;
                        }
                        if (fieldInfo.getField_name().equals("manufacturer_sales")) {
                            manufacturer_sales_key = "manufacturer_sales";
                            manufacturer_sales = id;
                        }
                        if (fieldInfo.getField_name().equals("manufacturer_rent")) {
                            manufacturer_rent_key = "manufacturer_rent";
                            manufacturer_rent = id;
                        }
                        if (fieldInfo.getField_name().equals("manufacturer_employees")) {
                            manufacturer_employees_key = "manufacturer_employees";
                            manufacturer_employees = id;
                        }
                        if (fieldInfo.getField_name().equals("manufacturer_machines")) {
                            manufacturer_machines_key = "manufacturer_machines";
                            manufacturer_machines = id;
                        }
                        if (fieldInfo.getField_name().equals("manufacturer_products")) {
                            manufacturer_products_key = "manufacturer_products";
                            manufacturer_products = id;
                        }
                        if (fieldInfo.getField_name().equals("manufacturer_bills")) {
                            manufacturer_bills_key = "manufacturer_bills";
                            manufacturer_bills = id;
                        }

                        if (fieldInfo.getField_name().equals("restaurant_investment")) {
                            restaurant_investment_key = "restaurant_investment";
                            restaurant_investment = id;
                        }
                        if (fieldInfo.getField_name().equals("restaurant_inventory")) {
                            restaurant_inventory_key = "restaurant_inventory";
                            restaurant_inventory = id;
                        }
                        if (fieldInfo.getField_name().equals("restaurant_sales")) {
                            restaurant_sales_key = "restaurant_sales";
                            restaurant_sales = id;
                        }
                        if (fieldInfo.getField_name().equals("restaurant_rent")) {
                            restaurant_rent_key = "restaurant_rent";
                            restaurant_rent = id;
                        }
                        if (fieldInfo.getField_name().equals("restaurant_employees")) {
                            restaurant_employees_key = "restaurant_employees";
                            restaurant_employees = id;
                        }
                        if (fieldInfo.getField_name().equals("restaurant_seating_capacity")) {
                            restaurant_seating_capacity_key = "restaurant_seating_capacity";
                            restaurant_seating_capacity = id;
                        }
                        if (fieldInfo.getField_name().equals("restaurant_location")) {
                            restaurant_location_key = "restaurant_location";
                            restaurant_location = id;
                        }
                        if (fieldInfo.getField_name().equals("restaurant_menu_range")) {
                            restaurant_menu_range_key = "restaurant_menu_range";
                            restaurant_menu_range = id;
                        }
                        if (fieldInfo.getField_name().equals("restaurant_bills")) {
                            restaurant_bills_key = "restaurant_bills";
                            restaurant_bills = id;
                        }
                        if (fieldInfo.getField_name().equals("e_commerce_investment")) {
                            e_commerce_investment_key = "e_commerce_investment";
                            e_commerce_investment = id;
                        }
                        if (fieldInfo.getField_name().equals("e_commerce_sales")) {
                            e_commerce_sales_key = "e_commerce_sales";
                            e_commerce_sales = id;
                        }
                        if (fieldInfo.getField_name().equals("e_commerce_employees")) {
                            e_commerce_employees_key = "e_commerce_employees";
                            e_commerce_employees = id;
                        }
                        if (fieldInfo.getField_name().equals("e_commerce_riders")) {
                            e_commerce_riders_key = "e_commerce_riders";
                            e_commerce_riders = id;
                        }
                        if (fieldInfo.getField_name().equals("e_commerce_registered_fbos")) {
                            e_commerce_registered_fbos_key = "e_commerce_registered_fbos";
                            e_commerce_registered_fbos = id;
                        }
                    }

                    @Override
                    public void downloadInspection(String downloadUrl, int position) {
                        printLog("createViewDropdown", "downloadInspection  position=> " + downloadUrl + " position=> " + position);

                    }

                    @Override
                    public void deleteRecordAPICall(String deleteUrl, int position) {

                    }
                }, fieldInfo, formFilteredData);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (dropDownId != null && !dropDownId.isEmpty()) {
                            Log.d("parentLicenseVal", "parentLicenseVal = " + parentLicenseVal);
                            if (dropDownId.equals("Retailer")) {
                                Log.d("parentLicenseVal", "parentLicenseVal Retailer = ");
                                if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_bills_empty") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills_empty");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                            }
                            else if (dropDownId.equals("Manufacturer")) {
                                if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_bills_empty") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills_empty");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                            }
                            else if (dropDownId.equals("Restaurants")) {
                                if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("restaurant_bills_empty") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills_empty");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                            }
                            else if (dropDownId.equals("E-Commerce")) {
                                if (parentView.findViewWithTag("retailer_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_investment");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_avg_sale") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_avg_sale");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_rent") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_rent");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_employees");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_location") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_location");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("retailer_bills") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("retailer_bills");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_investment");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_inventory") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_inventory");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_units_produced") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_units_produced");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_sales") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_sales");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_rent") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_rent");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_employees");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_machines") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_machines");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_products") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_products");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("manufacturer_bills") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("manufacturer_bills");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_investment");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_inventory") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_inventory");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_sales") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_sales");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_rent") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_rent");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_employees");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_seating_capacity") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_seating_capacity");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_location") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_location");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_menu_range") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_menu_range");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_bills") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("restaurant_bills_empty") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("restaurant_bills_empty");
                                    pfaddactv.setVisibility(GONE);
                                    pfaddactv.showHideDropDown(false);
                                }
                                if (parentView.findViewWithTag("e_commerce_investment") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_investment");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("e_commerce_sales") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_sales");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("e_commerce_employees") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_employees");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("e_commerce_riders") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_riders");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                                if (parentView.findViewWithTag("e_commerce_registered_fbos") instanceof PFADDACTV) {
                                    PFADDACTV pfaddactv = parentView.findViewWithTag("e_commerce_registered_fbos");
                                    pfaddactv.setVisibility(VISIBLE);
                                    pfaddactv.showHideDropDown(true);
                                }
                            }
                        }
                    }
                }, 300);
            }
            pfa_dd_actv.setPfaddLL(pfaddLL);

            parentView.addView(pfaddLL);
            if (fieldInfo.getField_name().equals("product_name_dropdown"))
                sampleBrandNameDD = pfaddLL;

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

    private void createViewLocationFields(FormFieldInfo fieldInfo, LinearLayout.LayoutParams
            params, final LinearLayout parentView, LayoutInflater
                                                  inflater, HashMap<String, Boolean> fieldsReq) {
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


    private void createViewABC(final FormFieldInfo fieldInfo,
                               final LinearLayout parentView, LayoutInflater inflater) {
        @SuppressLint("InflateParams")
        LinearLayout subviewLL = (LinearLayout) inflater.inflate(R.layout.pfa_form_edittext, null, false);
        LinearLayout.LayoutParams subViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subviewLL.setLayoutParams(subViewLayoutParams);

        Log.d("enfrocedData", "createViewABC");


        linerList.add(subviewLL);

        LinearLayout textView = subviewLL.findViewById(R.id.textView);

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
                            if (response != null)
                                bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());

                            startNewActivity(PFADetailActivity.class, bundle, false);
                        }
                    }, true);
                }
            });

        }
    }

    private LinearLayout createViewABC1(final FormFieldInfo fieldInfo,
                                        final LinearLayout parentView, LayoutInflater inflater) {
        @SuppressLint("InflateParams")
        LinearLayout subviewLL = (LinearLayout) inflater.inflate(R.layout.pfa_form_edittext, null, false);
        LinearLayout.LayoutParams subViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subviewLL.setLayoutParams(subViewLayoutParams);

        Log.d("enfrocedData", "createViewABC");

        LinearLayout textView = subviewLL.findViewById(R.id.textView);


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
                            if (response != null)
                                bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());

                            startNewActivity(PFADetailActivity.class, bundle, false);
                        }
                    }, true);
                }
            });

        }
        return subviewLL;
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

    private void hideAndShowLocationViews(FormFieldInfo
                                                  fieldInfo, HashMap<String, Boolean> fieldsReq) {

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

    public void updateDropdownViewsData(Bundle bundle, final LinearLayout parentView, HashMap<
            String, HashMap<String, Boolean>> sectionRequired) {

        if (bundle != null && bundle.containsKey(EXTRA_ACTV_TAG)) {
            String actvTag = bundle.getString(EXTRA_ACTV_TAG);
            int selectedPosition = bundle.getInt(SELECTED_POSITION, -1);

            Log.d("formSectionName", "selectedPos=  " + selectedPosition);
            Log.d("formSectionName", "EXTRA_ACTV_TAG=  " + EXTRA_ACTV_TAG);

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

                        if (actvTag.equals("retailer_investment") || actvTag.equals("retailer_avg_sale") || actvTag.equals("retailer_rent") ||
                                actvTag.equals("retailer_employees") || actvTag.equals("retailer_location") || actvTag.equals("retailer_bills") ||
                                actvTag.equals("manufacturer_investment") || actvTag.equals("manufacturer_inventory") || actvTag.equals("manufacturer_units_produced") ||
                                actvTag.equals("manufacturer_sales") || actvTag.equals("manufacturer_rent") || actvTag.equals("manufacturer_employees") ||
                                actvTag.equals("manufacturer_machines") || actvTag.equals("manufacturer_products") || actvTag.equals("manufacturer_bills") ||
                                actvTag.equals("restaurant_investment") || actvTag.equals("restaurant_inventory") || actvTag.equals("restaurant_sales") ||
                                actvTag.equals("restaurant_rent") || actvTag.equals("restaurant_employees") || actvTag.equals("restaurant_seating_capacity") ||
                                actvTag.equals("restaurant_location") || actvTag.equals("restaurant_menu_range") || actvTag.equals("restaurant_bills") ||
                                actvTag.equals("parent_license_type") || actvTag.equals("e_commerce_investment") || actvTag.equals("e_commerce_sales") ||
                                actvTag.equals("e_commerce_employees") || actvTag.equals("e_commerce_riders") || actvTag.equals("e_commerce_registered_fbos")
                                || actvTag.equals("revise_business_category")) {
                            PFAEditText pfaddactv2 = parentView.findViewWithTag("revised_license_type");
                            pfaddactv2.setText("");
                            confirmRevisedCatMsg = "";
//                            pfaddactv2.setSelectedValues(null);
//
//                            if (pfaViewsCallbacks != null)
//                                pfaViewsCallbacks.onDropdownItemSelected(null, pfaddactv2.formFieldInfo.getField_name());
                            pfaddactv2.clearFocus();
                            clearFocusOfAllViews(parentView);
                        }

                        ((PFADDACTV) pfaddactv).setDropdownSelection(selectedPosition, pfaViewsCallbacks);
                        pfaddBizSizeACTV = parentView.findViewWithTag(DD_BIZ_SIZE);  //business_size

                        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(mContext);
                        defaultValue = sharedPrefUtils.getAction();

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


    private void setFoodLabViews(final LinearLayout menuFragParentLL, List<
            FormSectionInfo> formSectionInfos,
                                 final HashMap<String, HashMap<String, Boolean>> sectionRequired,
                                 final ScrollView fragMenuItemSV) {
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

    public void onDDSelectedAPIUrl(final FormDataInfo formDataInfo,
                                   final LinearLayout menuFragParentLL,
                                   final HashMap<String, HashMap<String, Boolean>> sectionRequired,
                                   final ScrollView fragMenuItemSV, final List<FormSectionInfo> formSectionInfos) {

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
                        if (response != null) {
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
                    }
                });
            }
            ////////////
        }
    }

    public void downloadFile(String url) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //your codes here
        new Handler().post(() -> {
            try {
                String filename = url.substring(url.lastIndexOf('/') + 1);
                String filePath = Environment.getExternalStorageDirectory() + "/" + "Download" + "/" + filename;
                URL u = new URL(url);
                InputStream is = u.openStream();

                DataInputStream dis = new DataInputStream(is);

                byte[] buffer = new byte[1024];
                int length;

                FileOutputStream fos = null;

                if (url.endsWith("jpg"))
                    fos = new FileOutputStream(new File(filePath));
                else if (url.endsWith("jpeg"))
                    fos = new FileOutputStream(new File(filePath));
                else if (url.endsWith("png"))
                    fos = new FileOutputStream(new File(filePath));
                else if (url.endsWith("gif"))
                    fos = new FileOutputStream(new File(filePath));
                else if (url.endsWith("pdf"))
                    fos = new FileOutputStream(new File(filePath));
                else if (url.endsWith("docx"))
                    fos = new FileOutputStream(new File(filePath));

                while ((length = dis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }

                Toast.makeText(mContext, "File Downloaded Successfully..." + "\n" + filePath, Toast.LENGTH_LONG).show();
//                viewFile(filePath);

            } catch (MalformedURLException mue) {
                Log.e("SYNC getUpdate", "malformed url error", mue);
            } catch (IOException ioe) {
                Log.e("SYNC getUpdate", "io error", ioe);
            } catch (SecurityException se) {
                Log.e("SYNC getUpdate", "security error", se);
            }
        });

    }

    public void viewFile(String path) {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = null;
        if (path.endsWith("png"))
            mimeType = "png";
        if (path.endsWith("jpg"))
            mimeType = "jpg";
        if (path.endsWith("jpeg"))
            mimeType = "jpeg";
        if (path.endsWith("gif"))
            mimeType = "gif";
        if (path.endsWith("pdf"))
            mimeType = "pdf";
        if (path.endsWith("docx"))
            mimeType = "docx";

        newIntent.setDataAndType(Uri.fromFile(new File(path)), "*/*");
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mContext.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    DownloadFileManager.OnDownloadListener pbListener = new DownloadFileManager.OnDownloadListener() {
        @Override
        public void onStart() {

            sharedPrefUtils.showProgressDialog(false);

        }

        @Override
        public void onSetMax(int max) {

        }

        @Override
        public void onProgress(int current) {

        }

        @Override
        public void onFinishDownload() {
            sharedPrefUtils.hideProgressDialog();
        }

        @Override
        public void onResponse(boolean isSuccess, String path) {
            sharedPrefUtils.hideProgressDialog();
            if (isSuccess) {
                sharedPrefUtils.showMsgDialog("File downloaded Successfully" + "\n" + path, null);
//                Toast.makeText(ImageGalleryActivity.this, ""+path, Toast.LENGTH_LONG).show();
            } else {
                sharedPrefUtils.showMsgDialog("File downloading Failed!!", null);
            }
        }
    };
}

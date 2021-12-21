package com.pfa.pfaapp.utils;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.widget.RadioButton;

import java.util.HashMap;

public class AppConst {

    public static HashMap<String, String> IMAGE_SELECTION_MAP = new HashMap<>();
    static HashMap<String, String> VIDEO_SELECTION_MAP = new HashMap<>();
    //    General global values
    public static final String EMPTY_JSON_STRING = "";

    public static final int CAPTURE_PHOTO = 0;
    public static final int CHOOSE_FROM_GALLERY = 1;
    public static final int RECORD_VIDEO = 2;
    static final int MULTIPLE_IMAGES = 3;
    public static final int OTHER_FILES = 4;

    public static final int REQ_CODE_ADD_ITEM = 10;//  EXTRA_DIALOG_ADD_ITEM_FORM_SECTION


    public static final String BUSINESS_LOCATION_FIELD = "business_location";
    public static final String REGION_TAG = "region";
    public static final String DIVISION_TAG = "division";
    public static final String DISTRICT_TAG = "district";
    public static final String TOWN_TAG = "town";
    public static final String SUB_TOWN_TAG = "subtown";
    public static final String PFA_SEARCH_TAG = "autoSearch";

    public static final String DD_STATUS = "status";
    public static final String DD_BIZ_SIZE = "business_size";
    public static final String DD_STATUS_EP = "Emergency Prohibition",
            DD_STATUS_SEAL = "Business Seal",
            DD_STATUS_FINE = "Fine Action";


    public static final String DD_FOOD_LAB_TEST = "food_lab_tests";


    public static final String APP_LATITUDE = "latitude";
    public static final String APP_LONGITUDE = "longitude";
    public static boolean codeVerified = false;


    //    Shared preference values
    public static final String SP_CNIC = "SP_CNIC";
    public static final String SP_PHONE_NUM = "SP_PHONE_NUM";
    public static final String SP_SECURITY_CODE = "security_code";
    public static final String SP_NEW_PIN_SMS_CODE = "newPinSMSCode";
    public static final String SP_STAFF_ID = "user_id";
    public static final String SP_AUTH_TOKEN = "auth_token";
//    public static final String AUTH_APP_TOKEN= "PFAqVOIYfqs:PUT1bGDNXx-8JELLbelRQcb9EN9srz";

    public static final String SP_IS_LOGED_IN = "SP_IS_LOGED_IN";
    public static final String SP_USER_INFO = "SP_USER_INFO";
    public static final String SP_LAST_FETCH_TIME = "SP_LAST_FETCH_TIME";
    public static final String SP_VERIFICATION_CODE = "SP_VERIFICATION_CODE";
    public static final String SP_PIN_CODE_MESSAGE = "PinCodeMessage";
    public static final String SP_LOGIN_TYPE = "loginType";
    public static final String MTO_WEB_URL="MTO_WEB_URL";
    public static final String SP_FCM_ID = "fcmId";
    public static final String SP_APP_AUTH_TOKEN = "AUTH_APP_TOKEN";
    public static final String SP_MAIN_MENU = "SP_MAIN_MENU";
    public static final String SP_DRAWER_MENU = "SP_DRAWER_MENU";
    public static final String SP_INSPECTIONS_MENU = "Inspections";
    public static final String SP_APP_LANG = "SP_APP_LANG";
    public static final String SP_IS_DELETE_DB_DELETED = "SP_IS_DELETE_DB_DELETED";


    //    Extras (values to be passed through intent/bundle among activities & services
    public static final String EXTRA_DROPDOWN_NAME = "EXTRA_DROPDOWN_NAME";
    public static final String EXTRA_ACTV_TAG = "EXTRA_ACTV_TAG";
    public static final String SELECTED_POSITION = "SELECTED_POSITION";
    public static final String SEARCH_DATA = "SEARCH_DATA";
    public static final String SIGNUP_REQ_PARAMS = "SIGNUP_REQ_PARAMS";

    public static final String EXTRA_FILTERS_DATA = "EXTRA_FILTERS_DATA";
    public static final String EXTRA_URL_TO_CALL = "EXTRA_URL_TO_CALL";
    public static final String EXTRA_JSON_STR_RESPONSE = "EXTRA_JSON_STR_RESPONSE";
    public static final String EXTRA_DOWNLOAD_URL = "EXTRA_DOWNLOAD_URL";
    public static final String EXTRA_MENU_ITEM_NAME = "EXTRA_MENU_ITEM_NAME";

    public static final String EXTRA_FORM_SECTION_LIST = "EXTRA_FORM_SECTION_LIST";
    public static final String EXTRA_PFA_MENU_ITEM = "EXTRA_PFA_MENU_ITEM";
    public static final String EXTRA_BIZ_FORM_DATA = "EXTRA_BIZ_FORM_DATA";

    public static final String EXTRA_ACTIVITY_TITLE = "EXTRA_ACTIVITY_TITLE";

    public static final String EXTRA_NEXT_URL = "nextUrl";
    public static final String EXTRA_DETAIL_MENU = "EXTRA_DETAIL_MENU";
    public static final String EXTRA_INSPECTION_DATA = "EXTRA_INSPECTION_DATA";
    public static final String EXTRA_LATLNG_STR = "EXTRA_LATLNG_STR";
    public static final String EXTRA_STARTING_ACTIVITY = "EXTRA_STARTING_ACTIVITY";
    public static final String EXTRA_SINGLE_TOP = "EXTRA_SINGLE_TOP";
    //    public static final String EXTRA_VIDEO_FILE_PATH = "EXTRA_VIDEO_FILE_PATH";
    public static final String EXTRA_DELETE_IMAGE = "EXTRA_DELETE_IMAGE";
    public static final String EXTRA_ITEM_COUNT = "itemCount";
    public static final String EXTRA_RESEND_API = "resend_API";

    public static final String EXTRA_FP_ACTION = "EXTRA_FP_ACTION";
    public static final String EXTRA_DIALOG_ADD_ITEM_FORM_SECTION = "EXTRA_DIALOG_ADD_ITEM_FORM_SECTION";

    //    Request Codes for search filters ( for onActivityResult. RC=Request Code)
    public static final int RC_ACTIVITY = 9000, RC_REFRESH_LIST = 10000, RC_DROPDOWN = 12000, RC_DELETE_IMAGE = 101;
    public static final String FP_SIGNUP = "FP_SIGNUP", FP_LOGIN = "FP_LOGIN";
    static Uri mImageCaptureUri;

    @SuppressLint("StaticFieldLeak")
    public static RadioButton draftsRadioButton;

    public static boolean DO_REFRESH = false;
    public static boolean BIZ_LOC_UPDATED = false;

    public static final String CANCEL = "CANCEL";

    public static String INSPECTION_ID;

}

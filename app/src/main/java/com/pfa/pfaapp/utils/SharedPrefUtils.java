package com.pfa.pfaapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.FBOMainGridActivity;
import com.pfa.pfaapp.customviews.PFADDSpinner;
import com.pfa.pfaapp.customviews.PFAMultiSpinner;
import com.pfa.pfaapp.dbutils.DBQueriesUtil;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.localdbmodels.DistrictInfo;
import com.pfa.pfaapp.localdbmodels.DivisionInfo;
import com.pfa.pfaapp.localdbmodels.RegionInfo;
import com.pfa.pfaapp.localdbmodels.SubTownInfo;
import com.pfa.pfaapp.localdbmodels.TownInfo;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.models.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.pfa.pfaapp.utils.AppConst.SP_DRAWER_MENU;
import static com.pfa.pfaapp.utils.AppConst.SP_FCM_ID;
import static com.pfa.pfaapp.utils.AppConst.SP_MAIN_MENU;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;
import static com.pfa.pfaapp.utils.AppConst.SP_USER_INFO;

/**
 * SharedPrefUtils->AppUtils->CustomDialogs
 */
public class SharedPrefUtils extends AppUtils {

    private static final String PREFS_NAME = "pref";
    private static final String KEY_CAMERA_P= "CameraKey";
    private DBQueriesUtil dbQueriesUtil;
//    private final String sp_usr = "sp_user";

    public SharedPrefUtils(Context mContext) {
        super(mContext);
        dbQueriesUtil = new DBQueriesUtil(mContext);
    }




    /**
     * get the shared preference value with given key:
     * if value exist for given key return that value otherwise return null
     *
     * @param key String
     * @param s
     * @return String
     */
    public String getSharedPrefValue(String key, String s) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, null);
    }

    public Boolean getBoolean(String key, Boolean s) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return settings.getBoolean(key, false);
    }

//require false field list stored here
    public void saveRLF(ArrayList<String> list) {
        SharedPreferences RFF = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = RFF.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("RequireFalseFieldList", json);
        editor.apply();
    }

    public void setAction(String value){
        SharedPreferences action = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = action.edit();
        editor.putString("ActionValue", value);
        editor.apply();
    }

        public String getAction(){
        SharedPreferences action = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String actionValur  = action.getString("ActionValue", "");
        return actionValur;
    }

    public ArrayList<String> getRLF(String key){
        ArrayList<String> ImportRFF = new ArrayList<>();
        Gson gson = new Gson();
        SharedPreferences RFF2 = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String jsonPreferences = RFF2.getString("RequireFalseFieldList", "");

        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ImportRFF = gson.fromJson(jsonPreferences, type);

        return ImportRFF;
    }


//    public void printAllSP() {
//        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        printLog("SharedPref==>, ",""+(settings.getAll()));
//    }

    /**
     * Saves the shared preference value with given key
     *
     * @param key             String
     * @param sharedPrefValue String
     */
    @SuppressLint("ApplySharedPref")
    public void saveSharedPrefValue(String key, String sharedPrefValue) {
        try {
            SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, sharedPrefValue);
            editor.apply();
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    public void saveBoolean(String key, Boolean sharedPrefValue) {
        try {
            SharedPreferences settings =PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
            final SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(key, sharedPrefValue);
            editor.apply();
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    public void saveSingleRequired(boolean singleRequired) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("singleRequiredKey", singleRequired);
        editor.apply();
    }



    /*
     * removes only the shared preference value with given key
     * */
    public void removeSharedPrefValue(String key) {
        try {
            SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(key);
            editor.apply();
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    /*
     * Clears all the shared preference values except Main Menu opening on start of app
     * */
    @SuppressLint("ApplySharedPref")
    private void clearSharedPref() {
        try {
            String fcdId = getSharedPrefValue(SP_FCM_ID, "");
//            String mainMenu = getSharedPrefValue(SP_MAIN_MENU);
            removeSharedPrefValue(AppConst.SP_USER_INFO);
            SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.apply();

            saveSharedPrefValue(SP_FCM_ID, fcdId);
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    /**
     * get the saved user in shared preferences
     *
     * @return UserInfo
     */
    public UserInfo getUserInfo() {
        String userInfoStr = getSharedPrefValue(SP_USER_INFO, "");
        if (userInfoStr == null || (userInfoStr.trim().isEmpty())) {
            return null;
        }
        return new Gson().fromJson(userInfoStr, UserInfo.class);
    }

    /**
     * get the list of all regions saved in SQLite Database
     *
     * @return List<RegionInfo>
     */
    protected List<RegionInfo> getRegionsList() {
        Type type = new TypeToken<List<RegionInfo>>() {
        }.getType();

        List<RegionInfo> regionInfos = new GsonBuilder().create().fromJson(dbQueriesUtil.selectAllFromTable(DBQueriesUtil.TABLE_REGION).toString(), type);
        Collections.sort(regionInfos, new Comparator<RegionInfo>() {
            @Override
            public int compare(RegionInfo o1, RegionInfo o2) {
                return o1.getRegion_name().compareToIgnoreCase(o2.getRegion_name());

            }
        });

        return regionInfos;
    }

    protected List<RegionInfo> getRegionByRegionID(String region_id) {
        Type type = new TypeToken<List<RegionInfo>>() {
        }.getType();

        JSONArray result = dbQueriesUtil.getSelectedTableValues(DBQueriesUtil.TABLE_REGION, "region_id", region_id);

        List<RegionInfo> regionInfos = new GsonBuilder().create().fromJson(result.toString(), type);
        Collections.sort(regionInfos, new Comparator<RegionInfo>() {
            @Override
            public int compare(RegionInfo o1, RegionInfo o2) {
                return o1.getRegion_name().compareToIgnoreCase(o2.getRegion_name());

            }
        });

        return regionInfos;

    }


    public void savePermissionStatus(String status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CAMERA_P, status);
        editor.apply();
    }


    /**
     * get the list of all divisions for given region id available in SQLite Database
     *
     * @param regionId String
     * @return List<DivisionInfo>
     */

    protected List<DivisionInfo> getDivision(String regionId) {
        Type type = new TypeToken<List<DivisionInfo>>() {
        }.getType();

        JSONArray result;
        if (regionId == null || regionId.equalsIgnoreCase("0")) {
            result = dbQueriesUtil.selectAllFromTable(DBQueriesUtil.TABLE_DIVISION);
        } else {
            result = dbQueriesUtil.getSelectedTableValues(DBQueriesUtil.TABLE_DIVISION, "region_id", regionId);
        }

        List<DivisionInfo> divisionsInfos = new GsonBuilder().create().fromJson(result.toString(), type);
        Collections.sort(divisionsInfos, new Comparator<DivisionInfo>() {
            @Override
            public int compare(DivisionInfo o1, DivisionInfo o2) {
                return o1.getDivision_name().compareToIgnoreCase(o2.getDivision_name());
            }
        });

        return divisionsInfos;
    }

    protected List<DivisionInfo> getDivisionByDivisionID(String division_id) {
        Type type = new TypeToken<List<DivisionInfo>>() {
        }.getType();

        JSONArray result = dbQueriesUtil.getSelectedTableValues(DBQueriesUtil.TABLE_DIVISION, "division_id", division_id);

        List<DivisionInfo> divisionsInfos = new GsonBuilder().create().fromJson(result.toString(), type);
        Collections.sort(divisionsInfos, new Comparator<DivisionInfo>() {
            @Override
            public int compare(DivisionInfo o1, DivisionInfo o2) {
                return o1.getDivision_name().compareToIgnoreCase(o2.getDivision_name());
            }
        });

        return divisionsInfos;
    }

    /**
     * get the list of all DistrictInfo for given region id available in SQLite Database
     *
     * @param divisionId String
     * @return List<DistrictInfo>
     */
    protected List<DistrictInfo> getDistrictInfos(String divisionId) {
        Type type = new TypeToken<List<DistrictInfo>>() {
        }.getType();
        JSONArray result;
        if (divisionId == null || divisionId.equalsIgnoreCase("0")) {
            result = dbQueriesUtil.selectAllFromTable(dbQueriesUtil.TABLE_DISTRICT);
        } else {
            result = dbQueriesUtil.getSelectedTableValues(dbQueriesUtil.TABLE_DISTRICT, "division_id", divisionId);
        }

        List<DistrictInfo> districtInfos = new GsonBuilder().create().fromJson(result.toString(), type);
        Collections.sort(districtInfos, new Comparator<DistrictInfo>() {
            @Override
            public int compare(DistrictInfo o1, DistrictInfo o2) {
                return o1.getDistrict_name().compareToIgnoreCase(o2.getDistrict_name());

            }
        });

        return districtInfos;
    }


    protected List<DistrictInfo> getDistrictByDistrictID(String districtID) {
        Type type = new TypeToken<List<DistrictInfo>>() {
        }.getType();
        JSONArray result;
        result = dbQueriesUtil.getSelectedTableValues(dbQueriesUtil.TABLE_DISTRICT, "district_id", districtID);
        List<DistrictInfo> districtInfos = new GsonBuilder().create().fromJson(result.toString(), type);
        Collections.sort(districtInfos, new Comparator<DistrictInfo>() {
            @Override
            public int compare(DistrictInfo o1, DistrictInfo o2) {
                return o1.getDistrict_name().compareToIgnoreCase(o2.getDistrict_name());

            }
        });

        return districtInfos;
    }

    /**
     * get the list of all TownInfo for given region id available in SQLite Database
     *
     * @param districtId String
     * @return List<TownInfo>
     */
    protected List<TownInfo> getTownInfos(String districtId) {
        Type type = new TypeToken<List<TownInfo>>() {
        }.getType();
        JSONArray result;
        if (districtId == null || districtId.equalsIgnoreCase("0")) {
            result = dbQueriesUtil.selectAllFromTable(dbQueriesUtil.TABLE_TOWN);
        } else {
            result = dbQueriesUtil.getSelectedTableValues(dbQueriesUtil.TABLE_TOWN, "district_id", districtId);
        }

        List<TownInfo> townInfos = new GsonBuilder().create().fromJson(result.toString(), type);
        Collections.sort(townInfos, new Comparator<TownInfo>() {
            @Override
            public int compare(TownInfo o1, TownInfo o2) {
                return o1.getTown_name().compareToIgnoreCase(o2.getTown_name());

            }
        });

        return townInfos;
    }


    protected List<TownInfo> getTownsByTownId(String town_id) {
        Type type = new TypeToken<List<TownInfo>>() {
        }.getType();
        JSONArray result;

        result = dbQueriesUtil.getSelectedTableValues(dbQueriesUtil.TABLE_TOWN, "town_id", town_id);

        List<TownInfo> townInfos = new GsonBuilder().create().fromJson(result.toString(), type);
        Collections.sort(townInfos, new Comparator<TownInfo>() {
            @Override
            public int compare(TownInfo o1, TownInfo o2) {
                return o1.getTown_name().compareToIgnoreCase(o2.getTown_name());

            }
        });

        return townInfos;
    }

    /**
     * get the list of all TownInfo for given region id available in SQLite Database
     *
     * @param townId String
     * @return List<TownInfo>
     */
    protected List<SubTownInfo> getSubTownInfos(String townId) {
        Type type = new TypeToken<List<SubTownInfo>>() {
        }.getType();
        JSONArray result;
        if (townId == null || townId.equalsIgnoreCase("0")) {
            result = dbQueriesUtil.selectAllFromTable(dbQueriesUtil.TABLE_SUB_TOWN);
        } else {
            result = dbQueriesUtil.getSelectedTableValues(dbQueriesUtil.TABLE_SUB_TOWN, "town_id", townId);
        }

        List<SubTownInfo> subTownInfos = new GsonBuilder().create().fromJson(result.toString(), type);
        Collections.sort(subTownInfos, new Comparator<SubTownInfo>() {
            @Override
            public int compare(SubTownInfo o1, SubTownInfo o2) {
                return o1.getSubtown_name().compareToIgnoreCase(o2.getSubtown_name());

            }
        });

        return subTownInfos;
    }

    protected List<SubTownInfo> getSubtownBySubtownId(String subtown_id) {
        Type type = new TypeToken<List<SubTownInfo>>() {
        }.getType();
        JSONArray result;

        result = dbQueriesUtil.getSelectedTableValues(dbQueriesUtil.TABLE_SUB_TOWN, "subtown_id", subtown_id);

        List<SubTownInfo> subTownInfos = new GsonBuilder().create().fromJson(result.toString(), type);
        Collections.sort(subTownInfos, new Comparator<SubTownInfo>() {
            @Override
            public int compare(SubTownInfo o1, SubTownInfo o2) {
                return o1.getSubtown_name().compareToIgnoreCase(o2.getSubtown_name());

            }
        });

        return subTownInfos;
    }


    private static AlertDialog.Builder builder1;

    /*
     * Update application (in case installed app version is less than the version available in playstore
     * */
    public void showUpdateAppDialog(final String packageStr) {
        if (builder1 != null)
            return;
        builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("New Version of App is available in Playstore. Please update.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        dialog.dismiss();
                        builder1 = null;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=" + packageStr));
                        mContext.startActivity(intent);
                        ((Activity) mContext).finish();

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dialog.dismiss();
                        builder1 = null;
                        ((Activity) mContext).finish();
                    }
                });

        if (!((Activity) mContext).isFinishing()) {
            AlertDialog alert11 = builder1.create();
            alert11.setCancelable(false);
            alert11.show();
        }

    }

    public List<PFAMenuInfo> getMainMenu() {
        List<PFAMenuInfo> pfaMenuInfos = null;
        if (getSharedPrefValue(SP_MAIN_MENU, "") != null) {
            Type type = new TypeToken<List<PFAMenuInfo>>() {
            }.getType();

            pfaMenuInfos = new GsonBuilder().create().fromJson(getSharedPrefValue(SP_MAIN_MENU, ""), type);
        }

        return pfaMenuInfos;
    }



    public List<PFAMenuInfo> getDrawerMenu() {
        List<PFAMenuInfo> pfaMenuInfos = null;
        if (getSharedPrefValue(SP_DRAWER_MENU, "") != null) {
            Type type = new TypeToken<List<PFAMenuInfo>>() {
            }.getType();

            pfaMenuInfos = new GsonBuilder().create().fromJson(getSharedPrefValue(SP_DRAWER_MENU, ""), type);

//             Add fingerprint
//            if (addFPMenu) {
//                PFAMenuInfo pfaMenuInfo = new PFAMenuInfo(pfaMenuInfos.get(pfaMenuInfos.size() - 1));
//                pfaMenuInfo.setMenuType("fingerPrint");
//                pfaMenuInfo.setMenuItemName("Add Fingerprint");
//                pfaMenuInfo.setMenuItemID(pfaMenuInfos.size());
//                pfaMenuInfo.setMenuItemOrder(pfaMenuInfos.size() - 1);
//                pfaMenuInfo.setSlug("fingerPrint");
//                pfaMenuInfo.setMenuItemImg("https://raw.githubusercontent.com/ModernPGP/icons/master/keys/icon-fingerprint.png");
//
//                pfaMenuInfos.add(pfaMenuInfo);
//            }
        }

        return pfaMenuInfos;
    }

    public void logoutFromApp(HttpService httpService) {
        HashMap<String, String> reqParams = new HashMap<>();
        reqParams.put("fcmID", getSharedPrefValue(SP_FCM_ID, ""));
        reqParams.put("staffID", getSharedPrefValue(SP_STAFF_ID, ""));
        Log.d("invalidUser" , "logoutFromApp 1");

        httpService.logout(reqParams, new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                if (response != null) {
                    Log.d("invalidUser" , "logoutFromApp response =2 ");
                    if (response.optBoolean("status")) {
                        try {
                            Log.d("invalidUser" , "logoutFromApp response = " + response.getBoolean("status"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        clearSharedPref();
                        startHomeActivity(FBOMainGridActivity.class, null);
                        dbQueriesUtil.deleteRecordsOfAllTable();

                    } else {
                        showMsgDialog("Logout Failed!", null);
                    }
                } else
                    Log.d("invalidUser" , "logoutFromApp response = null ");
            }
        });
    }

    public void logoutFromAllDevices(HttpService httpService) {
        HashMap<String, String> reqParams = new HashMap<>();
        reqParams.put("fcmID", getSharedPrefValue(SP_FCM_ID, ""));
        reqParams.put("staffID", getSharedPrefValue(SP_STAFF_ID, ""));

        httpService.logoutFromAll(reqParams, new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                if (response != null) {
                    if (response.optBoolean("status")) {
                        clearSharedPref();
                        startHomeActivity(FBOMainGridActivity.class, null);
                        dbQueriesUtil.deleteRecordsOfAllTable();

                    } else {
                        showMsgDialog("Logout Failed!", null);
                    }
                }
            }
        });
    }

    public void clearFocusOfAllViews(ViewGroup parent) {
        if (parent != null && parent.getChildCount() > 0) {
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                if ((child instanceof ViewGroup && (!(child instanceof RadioGroup)) && (!(child instanceof PFADDSpinner)) && (!(child instanceof PFAMultiSpinner)))) {
                    clearFocusOfAllViews((ViewGroup) child);
                    // DO SOMETHING WITH VIEWGROUP, AFTER CHILDREN HAS BEEN LOOPED
                } else {
                    if (child != null) {
                        child.clearFocus();
                    }
                }
            }
        }

    }

    public void restartActivitySelf(Activity activity) {
        Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);

    }


}

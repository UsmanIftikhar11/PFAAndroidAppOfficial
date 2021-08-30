package com.pfa.pfaapp.httputils;

import android.content.Context;

import com.pfa.pfaapp.dbutils.DBQueriesUtil;
import com.pfa.pfaapp.dbutils.DbHelper;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.pfa.pfaapp.utils.AppConst.SP_DRAWER_MENU;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_DELETE_DB_DELETED;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_LAST_FETCH_TIME;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;
import static com.pfa.pfaapp.utils.AppConst.SP_MAIN_MENU;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;

public class ConfigHttpUtils extends HttpService implements HttpResponseCallback {

    //    private final String client_locations = "account/location_fields";
    private final String client_locations = "client/get_locations";
    static final String MAIN_MENU_POSTFIX = "menu/main_menu";
    private DBQueriesUtil dbQueriesUtil;

    public ConfigHttpUtils(Context mContext) {
        super(mContext);
        dbQueriesUtil = new DBQueriesUtil(mContext);
    }

    public void fetchConfigData() {
        resetDB();
        HashMap<String, String> locParams = new HashMap<>();
        getListsData(client_locations, locParams, this, false);

        if (getSharedPrefValue(SP_LAST_FETCH_TIME, "") != null) {
            long lastUpdateTime = Long.parseLong(getSharedPrefValue(SP_LAST_FETCH_TIME, ""));
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime < (5 * 60 * 1000)) {  // 5 minutes as test
//                6 * 60 * 60 * 1000 this is 6 hours time
                return;
            }
        }

        String userId = "";
        if (getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {
            userId = getSharedPrefValue(SP_STAFF_ID, "");
            userId = "/" + userId;
        }



        getMainMenu(this, userId);
        if (getDrawerMenu() == null)
            getSideMenu("" + getSharedPrefValue(SP_STAFF_ID, ""), getSharedPrefValue(SP_LOGIN_TYPE, ""), this);

    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        if (response != null) {
            if (response.optBoolean("status")) {
                if (requestUrl.contains(client_locations)) {
                    try {
                        JSONObject dataJsonObject = response.getJSONObject("data");

                        if (dataJsonObject.has("region")) {
                            dbQueriesUtil.insertUpdateRegions(dataJsonObject.optJSONArray("region"));
                        }
                        if (dataJsonObject.has("division")) {
                            dbQueriesUtil.insertUpdateDivisions(dataJsonObject.optJSONArray("division"));
                        }
                        if (dataJsonObject.has("district")) {
                            dbQueriesUtil.insertUpdateDistricts(dataJsonObject.optJSONArray("district"));
                        }
                        if (dataJsonObject.has("town")) {
                            dbQueriesUtil.insertUpdateTowns(dataJsonObject.optJSONArray("town"));
                        }
                        if (dataJsonObject.has("subtown")) {
                            dbQueriesUtil.insertUpdateSubTowns(dataJsonObject.optJSONArray("subtown"));
                        }

                    } catch (JSONException e) {
                        printStackTrace(e);
                    }

                }

                String licence_type = "client/license_type";
                if (requestUrl.endsWith(licence_type)) {

                    dbQueriesUtil.insertUpdateLicenseTypes(response.optJSONArray("data"));
                }

                String business_categories = "client/business_categories";
                if (requestUrl.endsWith(business_categories)) {
                    dbQueriesUtil.insertUpdateBusinessCat(response.optJSONArray("data"));
                }

                if (requestUrl.contains(MAIN_MENU_POSTFIX)) {
                    try {
                        JSONObject jsonObject = response.getJSONObject("data");

                        JSONArray formJSONArray = jsonObject.getJSONArray("menus");
                        saveSharedPrefValue(SP_MAIN_MENU, formJSONArray.toString());

                    } catch (JSONException e) {
                        printStackTrace(e);
                    }
                } else if (requestUrl.contains("/api/menu/")) {
                    try {
                        JSONObject jsonObject = response.getJSONObject("data");

                        JSONArray formJSONArray = jsonObject.getJSONArray("menus");

                        saveSharedPrefValue(SP_DRAWER_MENU, formJSONArray.toString());

                    } catch (JSONException e) {
                        printStackTrace(e);
                    }
                }

                //        update last update time
                saveSharedPrefValue(SP_LAST_FETCH_TIME, "" + System.currentTimeMillis());
            }
        }
    }


    private void resetDB() {
        String sp_deleteDb = getSharedPrefValue(SP_IS_DELETE_DB_DELETED, "");
        if (sp_deleteDb == null) {
            DbHelper.getInstance(mContext).getWritableDatabase();
            saveSharedPrefValue(SP_IS_DELETE_DB_DELETED, SP_IS_DELETE_DB_DELETED);
        }
    }

}

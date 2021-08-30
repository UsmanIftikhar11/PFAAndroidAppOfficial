package com.pfa.pfaapp.dbutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
import com.pfa.pfaapp.localdbmodels.InspectionInfo;
import com.pfa.pfaapp.utils.AppConst;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class DBQueriesUtil {

    public static final String TABLE_REGION = "Region";
    public static final String TABLE_DIVISION = "Division";
    public final String TABLE_DISTRICT = "District";
    public final String TABLE_TOWN = "Town";
    public final String TABLE_SUB_TOWN = "SubTown";
    private final String TABLE_LICENSE_TYPE = "LicenseTypeInfo";
    private final String TABLE_BUSINESS_CAT = "BusinessCatInfo";
    public static final String TABLE_LOCAL_INSPECTIONS = "LocalInspections";
    private Context mContext;

    public DBQueriesUtil(Context mContext) {
        this.mContext = mContext;
    }

    public void insertUpdateRegions(JSONArray jsonArray) {
        String TABLE_REGION = "Region";
        insertUpdatePullData(TABLE_REGION, "region_id", jsonArray);
    }

    public void insertUpdateDivisions(JSONArray jsonArray) {
        String TABLE_DIVISION = "Division";
        insertUpdatePullData(TABLE_DIVISION, "division_id", jsonArray);
    }

    public void insertUpdateDistricts(JSONArray jsonArray) {
        insertUpdatePullData(TABLE_DISTRICT, "district_id", jsonArray);
    }

    public void insertUpdateTowns(JSONArray jsonArray) {
        insertUpdatePullData(TABLE_TOWN, "town_id", jsonArray);
    }

    public void insertUpdateSubTowns(JSONArray jsonArray) {
        insertUpdatePullData(TABLE_SUB_TOWN, "subtown_id", jsonArray);
    }

    public void insertUpdateLicenseTypes(JSONArray jsonArray) {
        insertUpdatePullData(TABLE_LICENSE_TYPE, "id", jsonArray);
    }

    public void insertUpdateBusinessCat(JSONArray jsonArray) {
        insertUpdatePullData(TABLE_BUSINESS_CAT, "id", jsonArray);
    }

    public void insertUpdateLocalInspections(InspectionInfo inspectionInfo, WhichItemClicked whichItemClicked) {
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(inspectionInfo));


            ContentValues values = new ContentValues();
            Iterator<?> keysIterator = jsonObject.keys();
            while (keysIterator.hasNext()) {
                String columnName = (String) keysIterator.next();

                values.put(columnName, jsonObject.optString(columnName));
            }
            if (inspectionInfo.getInspectionID() != null) {

                JSONArray resultJsonArray = getSelectedTableValues(TABLE_LOCAL_INSPECTIONS, "inspectionID", inspectionInfo.getInspectionID());

                if (resultJsonArray != null && resultJsonArray.length() > 0) {
                    String[] args = new String[]{"" + inspectionInfo.getInspectionID()};
                    int numOfRowsAffected = DbHelper.getInstance(mContext).dataBase.update(TABLE_LOCAL_INSPECTIONS, values, "inspectionID=?", args);

                    if (numOfRowsAffected > 0) {
                        whichItemClicked.whichItemClicked("Inspection Updated!");
                    }

                } else {
                    long insertedId = DbHelper.getInstance(mContext).dataBase.insert(TABLE_LOCAL_INSPECTIONS, "", values);
                    Log.e("Insert ID: ", "" + insertedId);
                    whichItemClicked.whichItemClicked("Inspection Saved as Draft Locally");
                }
            } else {
                whichItemClicked.whichItemClicked("");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void insertUpdatePullData(String tableName, String tablePK, JSONArray jsonRecordsArray) {
//        Log.e("Pull Table Name", "PullTable= > " + tableName);
        StringBuilder idsValuesString = new StringBuilder();
        List<String> totalRecordsIds = new ArrayList<>();
        List<String> tableColumnNames = new ArrayList<>();
        List<String> recordsIdsForUpdate = new ArrayList<>();
        StringBuilder queryColumnNames = new StringBuilder();
        LinkedHashMap<String, String> tableRecordMap = new LinkedHashMap<>();
        LinkedHashMap<String, String> updateRecordMap = new LinkedHashMap<>();

        // // make the record of table as comma separated and make two list, 1
        // for keys and 2 for values (comma separated colum values)

        try {
            int sizeJsonRecordsArray = jsonRecordsArray.length();
            if (sizeJsonRecordsArray > 0) {
                if (jsonRecordsArray.getJSONObject(0) != null) {
                    Iterator<?> keysIterator = jsonRecordsArray.getJSONObject(0).keys();
                    Object columnName;
                    while (keysIterator.hasNext()) {
                        columnName = keysIterator.next();
                        if (!columnName.equals("success")) {
                            if (queryColumnNames.toString().equals("")) {
                                queryColumnNames = new StringBuilder("" + columnName + "");
                            } else {
                                queryColumnNames.append(",").append(columnName);
                            }
                            tableColumnNames.add(columnName + "");
                        }
                    }
                } else {
                    return;
                }
                int numOfRecordsTobeInserted = 0;
                JSONObject json;
                StringBuilder tableRecord;
                StringBuilder tableUpdateRecord;
                String rowId;
                int sizeTableColumnNames;
                String columnName;
                String columnValue;

                for (int index = 0; index < sizeJsonRecordsArray; index++) {
                    json = jsonRecordsArray.getJSONObject(index);
                    if (json != null) {
                        tableRecord = new StringBuilder();
                        tableUpdateRecord = new StringBuilder();

                        // IDs data
                        rowId = json.optString(tablePK);
                        totalRecordsIds.add(rowId);

                        if (idsValuesString.toString().equals("")) {
                            idsValuesString.append("'").append(rowId).append("'");
                        } else {
                            idsValuesString.append(",'").append(rowId).append("'");
                        }
                        numOfRecordsTobeInserted++;

                        sizeTableColumnNames = tableColumnNames.size();
                        for (int i = 0; i < sizeTableColumnNames; i++) {
                            columnName = tableColumnNames.get(i);
                            columnValue = json.optString(columnName);
                            columnValue = DatabaseUtils.sqlEscapeString(((columnValue != null && !columnValue.isEmpty()) ? columnValue : AppConst.EMPTY_JSON_STRING));

                            if (i == 0) {
                                if (numOfRecordsTobeInserted == 1) {
                                    tableRecord = new StringBuilder("\nSELECT " + columnValue);
                                } else {

                                    tableRecord = new StringBuilder("\nUNION SELECT " + columnValue);
                                }
                                if (numOfRecordsTobeInserted == 500) {
                                    numOfRecordsTobeInserted = 0;
                                }
                                tableUpdateRecord = new StringBuilder(columnName + "=" + columnValue);
                            } else {
                                tableRecord.append(",").append(columnValue);
                                tableUpdateRecord.append(",").append(columnName).append("=").append(columnValue);
                            }
                        }

                        tableRecordMap.put(rowId, tableRecord.toString());
                        updateRecordMap.put(rowId, tableUpdateRecord.toString());
                    }
                }

                // ////////////////////
                int sizeTotalRecordsIds = totalRecordsIds.size();
                String employeeSelectQuery;
                if (sizeTotalRecordsIds > 0) {
                    employeeSelectQuery = "SELECT " + tablePK + " FROM " + tableName + " WHERE " + tablePK + " IN (" + idsValuesString + ")";

                    Cursor cursor = DbHelper.getInstance(mContext).dataBase.rawQuery(employeeSelectQuery, null);

                    if (cursor.moveToFirst()) {
                        do {
                            recordsIdsForUpdate.add(cursor.getString(0));
                        }
                        while (cursor.moveToNext());
                    }
                    if (!cursor.isClosed()) {
                        cursor.close();
                    }

                    String updateQuery;
                    for (String updateRecordId : recordsIdsForUpdate) {
                        updateQuery = "UPDATE " + tableName + " SET " + updateRecordMap.get(updateRecordId) + " WHERE " + tablePK + "='" + updateRecordId + "'";

                        // Log.e("updateQuery ", "" + updateQuery);
                        DbHelper.getInstance(mContext).dataBase.execSQL(updateQuery);

                        totalRecordsIds.remove(updateRecordId);
                        tableRecordMap.remove(updateRecordId);
                    }

                    String columnSelectionStr = "INSERT INTO " + tableName + "(" + queryColumnNames + ")\n ";
                    String insertQuery;// columnSelectionStr;
                    StringBuilder stringBuilderInsertQuery = new StringBuilder();
                    numOfRecordsTobeInserted = 0;
                    int sizeTableRecordMap = tableRecordMap.size();
                    if (sizeTableRecordMap > 0) {
                        for (int i = 0; i < sizeTableRecordMap; i++) {

                            numOfRecordsTobeInserted++;
                            stringBuilderInsertQuery.append(tableRecordMap.get(totalRecordsIds.get(i)));

                            if (numOfRecordsTobeInserted == 500) {
                                // Log.e("insertQuery ", "" + insertQuery);
                                insertQuery = stringBuilderInsertQuery.toString();
                                if (insertQuery.startsWith("\nUNION")) {
                                    insertQuery = insertQuery.substring(7);
                                }
                                insertQuery = columnSelectionStr + " " + insertQuery;
                                DbHelper.getInstance(mContext).dataBase.execSQL(insertQuery);
                                stringBuilderInsertQuery = new StringBuilder();
                                numOfRecordsTobeInserted = 0;
                            } else if (i == sizeTableRecordMap - 1) {
                                if (numOfRecordsTobeInserted > 0) {
                                    insertQuery = stringBuilderInsertQuery.toString();
                                    if (insertQuery.startsWith("\nUNION")) {
                                        insertQuery = insertQuery.substring(7);
                                    }

                                    insertQuery = columnSelectionStr + " " + insertQuery;
                                    DbHelper.getInstance(mContext).dataBase.execSQL(insertQuery);
                                    stringBuilderInsertQuery = new StringBuilder();
                                    numOfRecordsTobeInserted = 0;
                                }
                            }

                        }
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray selectAllFromTable(String tableName) {
        String selectQuery = "SELECT * FROM " + tableName;
        return selectQuery(selectQuery);
    }

    public JSONArray getSelectedTableValues(String tableName, String idKey, String idValue) {
        String selectQuery = "SELECT * FROM " + tableName + " WHERE " + idKey + "=" + idValue;
        return selectQuery(selectQuery);
    }

    private JSONArray selectQuery(String selectQuery) {
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = DbHelper.getInstance(mContext).dataBase.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                jsonArray.put(cursorToJSONObject(cursor));
            }
            while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }

        return jsonArray;
    }

    private JSONObject cursorToJSONObject(Cursor cursor) {
        JSONObject jsonObject = new JSONObject();
        String[] allColumNames = cursor.getColumnNames();

        try {
            for (String allColumName : allColumNames) {
                jsonObject.put(allColumName, cursor.getString(cursor.getColumnIndexOrThrow(allColumName)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void deleteAllRecordsOfTable(String tableName) {
        DbHelper.getInstance(mContext).dataBase.delete(tableName, "1", null);
    }

    public int deleteTableRow(String tableName, String keyid, String rowid) {

        Log.e("deleteAllRecordsOfTable", "Deleted Specific rows of table with name: " + tableName);
        return DbHelper.getInstance(mContext).dataBase.delete(tableName, keyid + "='" + rowid + "'", null);
    }


    public void deleteExpiredInspections() {
        JSONArray inspectionJSONArray = selectAllFromTable(TABLE_LOCAL_INSPECTIONS);
        Type formSectionInfosType = new TypeToken<List<InspectionInfo>>() {
        }.getType();
        List<InspectionInfo> inspectionInfos = new GsonBuilder().create().fromJson(inspectionJSONArray.toString(), formSectionInfosType);

        if (inspectionInfos != null && inspectionInfos.size() > 0) {
            for (InspectionInfo inspectionInfo : inspectionInfos) {

                boolean isDelete = isDeleteDraft(inspectionInfo.getInsert_time());
                if (isDelete) {
                    deleteTableRow(TABLE_LOCAL_INSPECTIONS, "inspectionID", inspectionInfo.getInspectionID());
                }
            }

        }
    }

//    public int updateData(String tablename, String keyUpdateRow, String dataRowStr, String keyidRow, String idRow) {
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(keyUpdateRow, dataRowStr); //These Fields should be your String values of actual column names
//        int updateRow = DbHelper.getInstance(mContext).dataBase.update(tablename, contentValues, keyidRow + "='" + idRow + "'", null);
//        Log.e("Update Row", "Update Table of row " + tablename);
//
//        return updateRow;
//    }

//    public int deleteSpecficValueofColumn(String tableName, String keyid, String rowid, String keyid2, String rowid2) {
//
//        int deleteResult = DbHelper.getInstance(mContext).dataBase.delete(tableName, "'" + keyid + "'='" + rowid + "' AND '" + keyid2 + "' = '" + rowid2 + "'", null);
//        Log.e("deleteAllRecordsOfTable", "Deleted Specific rows of table with name: " + tableName);
//        return deleteResult;
//    }


    public void deleteRecordsOfAllTable() {
        deleteAllRecordsOfTable(TABLE_REGION);
        deleteAllRecordsOfTable(TABLE_DIVISION);
        deleteAllRecordsOfTable(TABLE_DISTRICT);
        deleteAllRecordsOfTable(TABLE_TOWN);
        deleteAllRecordsOfTable(TABLE_SUB_TOWN);
        deleteAllRecordsOfTable(TABLE_LICENSE_TYPE);
        deleteAllRecordsOfTable(TABLE_BUSINESS_CAT);
//        deleteAllRecordsOfTable(TABLE_LOCAL_INSPECTIONS);
    }

    private boolean isDeleteDraft(String expiryDateStr) {
        Calendar cal = Calendar.getInstance(); // creates calendar
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault());

        try {
            Date expiryDate = formatter.parse(expiryDateStr);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(expiryDate);

            return cal.after(cal2);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


}

package com.pfa.pfaapp.localdbmodels;

import java.io.Serializable;

public class InspectionInfo implements Serializable {
    private String inspectionID;
    private String inspectionName;
    private String API_URL;
    private String menuData;  // json data saved in db for inspectionName
    private String draft_inspection;
    private String inspection_alert;
    private String local_add_newUrl;
    private String insert_time;
    private boolean saveData;


    public String getInspectionName() {
        return inspectionName;
    }

    public void setInspectionName(String inspectionName) {
        this.inspectionName = inspectionName;
    }

    public String getAPI_URL() {
        return API_URL;
    }

    public void setAPI_URL(String API_URL) {
        this.API_URL = API_URL;
    }

    public String getMenuData() {
        return menuData;
    }

    public void setMenuData(String menuData) {
        this.menuData = menuData;
    }

    public String getInspectionID() {
        return inspectionID;
    }

    public void setInspectionID(String inspectionID) {
        this.inspectionID = inspectionID;
    }

    public String getInspection_alert() {
        return inspection_alert;
    }

    public void setInspection_alert(String inspection_alert) {
        if(inspection_alert==null)
            inspection_alert="";
        this.inspection_alert = inspection_alert;
    }

    public String getDraft_inspection() {
        return draft_inspection;
    }

    public void setDraft_inspection(String draft_inspection) {
        this.draft_inspection = draft_inspection;
    }

    public String getLocal_add_newUrl() {
        return local_add_newUrl;
    }

    public void setLocal_add_newUrl(String local_add_newUrl) {
        this.local_add_newUrl = local_add_newUrl;
    }

    public String getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(String insert_time) {
        this.insert_time = insert_time;
    }

    public boolean isSaveData() {
        return saveData;
    }

    public void setSaveData(boolean saveData) {
        this.saveData = saveData;
    }

}

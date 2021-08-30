package com.pfa.pfaapp.models;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PFAMenuInfo implements Serializable {
    private String menuType;
    private String menuItemName;
    private String menuItemNameUrdu;
    private int menuItemID;
    private int menuItemOrder;
    private String menuItemImg;
    private String API_URL;
    private String Deseize_ALL_API_URL;
    private String bg_color;
    private String slug;
    private LocalMenuDataInfo data;
    private Map<String, File> filesMap;
    private Map<String, String> filePaths;
    private String localSectionJSONObject = "";
    private String printData;
    //limit for offline downloading
    public String max_draft_limit;

    public PFAMenuInfo(PFAMenuInfo pfaMenuInfo) {
        this.menuType = pfaMenuInfo.menuType;
        this.menuItemName = pfaMenuInfo.menuItemName;
        this.menuItemNameUrdu = pfaMenuInfo.menuItemNameUrdu;
        this.menuItemID = pfaMenuInfo.menuItemID;
        this.menuItemOrder = pfaMenuInfo.menuItemOrder;
        this.menuItemImg = pfaMenuInfo.menuItemImg;
        this.API_URL = pfaMenuInfo.API_URL;
        this.bg_color = pfaMenuInfo.bg_color;
        this.slug = pfaMenuInfo.slug;
        this.data = pfaMenuInfo.data;
        this.filesMap = pfaMenuInfo.filesMap;
        this.filePaths = pfaMenuInfo.filePaths;
        this.localSectionJSONObject = pfaMenuInfo.localSectionJSONObject;
        this.Deseize_ALL_API_URL = pfaMenuInfo.Deseize_ALL_API_URL;
        this.printData = pfaMenuInfo.printData;
        this.max_draft_limit = pfaMenuInfo.max_draft_limit;

    }


    public String getMax_draft_limit() {
        return max_draft_limit;
    }

    public void setMax_draft_limit(String max_draft_limit) {
        this.max_draft_limit = max_draft_limit;
    }

    public String getPrintData() {
        return printData;
    }

    public void setPrintData(String printData) {
        this.printData = printData;
    }





    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public int getMenuItemID() {
        return menuItemID;
    }

    public void setMenuItemID(int menuItemID) {
        this.menuItemID = menuItemID;
    }

    public int getMenuItemOrder() {
        return menuItemOrder;
    }

    public void setMenuItemOrder(int menuItemOrder) {
        this.menuItemOrder = menuItemOrder;
    }

    public String getMenuItemImg() {
        return menuItemImg;
    }

    public void setMenuItemImg(String menuItemImg) {
        this.menuItemImg = menuItemImg;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getAPI_URL() {
        return API_URL;
    }

    public void setAPI_URL(String API_URL) {
        this.API_URL = API_URL;
    }

    public String getBg_color() {
        return bg_color;
    }

    public void setBg_color(String bg_color) {
        this.bg_color = bg_color;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public LocalMenuDataInfo getData() {
        return data;
    }

    public void setData(LocalMenuDataInfo data) {
        this.data = data;
    }

    public Map<String, File> getFilesMap() {
        if(filesMap==null)
            filesMap = new HashMap<>();
        return filesMap;
    }

    public void setFilesMap(Map<String, File> filesMap) {
        this.filesMap = filesMap;
    }

    public String getLocalSectionJSONObject() {
        return localSectionJSONObject;
    }

    public void setLocalSectionJSONObject1(String localSectionJSONObject) {
        this.localSectionJSONObject = localSectionJSONObject;
    }

    public Map<String, String> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(Map<String, String> filePaths) {
        this.filePaths = filePaths;
    }

    public String getMenuItemNameUrdu() {
        return menuItemNameUrdu;
    }

    public void setMenuItemNameUrdu(String menuItemNameUrdu) {
        this.menuItemNameUrdu = menuItemNameUrdu;
    }

    public String getDeseize_ALL_API_URL() {
        return Deseize_ALL_API_URL;
    }

    public void setDeseize_ALL_API_URL(String deseize_ALL_API_URL) {
        Deseize_ALL_API_URL = deseize_ALL_API_URL;
    }
}

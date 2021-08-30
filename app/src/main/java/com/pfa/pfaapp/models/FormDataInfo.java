package com.pfa.pfaapp.models;

import java.io.Serializable;

public class FormDataInfo implements Serializable {
    private String name;//": "designation",
    private String value;//":
    private String valueUrdu;
    private boolean isSelected; // this is only value which will be used in case of multi select dropdown
    private String key;
    private String API_URL;
    private String append_to;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAPI_URL() {
        return API_URL;
    }

    public void setAPI_URL(String API_URL) {
        this.API_URL = API_URL;
    }

    public String getAppend_to() {
        return append_to;
    }

    public void setAppend_to(String append_to) {
        this.append_to = append_to;
    }

    public String getValueUrdu() {
        if(valueUrdu==null)
            valueUrdu="";
        return valueUrdu;
    }

    public void setValueUrdu(String valueUrdu) {
        this.valueUrdu = valueUrdu;
    }
}

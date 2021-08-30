package com.pfa.pfaapp.models;

import java.io.Serializable;

public class PFASearchInfo implements Serializable {
    private int id;//: "1",
    private String full_name;//: "Qasim Ali ",
    private String cnic_number;//: "3520251182489",
    private String phonenumber;//: "03334447250",
    private String API_URL;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getCnic_number() {
        return cnic_number;
    }

    public void setCnic_number(String cnic_number) {
        this.cnic_number = cnic_number;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAPI_URL() {
        return API_URL;
    }

    public void setAPI_URL(String API_URL) {
        this.API_URL = API_URL;
    }
}

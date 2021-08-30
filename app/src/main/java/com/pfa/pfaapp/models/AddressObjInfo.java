package com.pfa.pfaapp.models;

import java.io.Serializable;

public class AddressObjInfo implements Serializable {
    private int region_id;//: "4",
    private String region_name;//: "Central",

    private int division_id;//: "0",
    private String division_name;//: "",
    private String division_nameUrdu;//: "",

    private int district_id;//: "15",
    private String district_name;//: "Lahore",
    private String district_nameUrdu;//: "Lahore",

    private int town_id;//: "71",
    private String town_name;//: "Allama Iqbal Town",
    private String town_nameUrdu;//: "Allama Iqbal Town",

    private int subtown_id;//: "0",
    private String subtown_name;//: "",
    private String subtown_nameUrdu;//: "",

    private String address;//: "209 ravi block mian fazal e haq road",
    private boolean show_town;
    private boolean show_all_town;

    public int getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(int district_id) {
        this.district_id = district_id;
    }

    public String getDistrict_name() {
        return district_name;
    }

    public void setDistrict_name(String district_name) {
        this.district_name = district_name;
    }

    public int getTown_id() {
        return town_id;
    }

    public void setTown_id(int town_id) {
        this.town_id = town_id;
    }

    public String getTown_name() {
        return town_name;
    }

    public void setTown_name(String town_name) {
        this.town_name = town_name;
    }

    public int getSubtown_id() {
        return subtown_id;
    }

    public void setSubtown_id(int subtown_id) {
        this.subtown_id = subtown_id;
    }

    public String getSubtown_name() {
        return subtown_name;
    }

    public void setSubtown_name(String subtown_name) {
        this.subtown_name = subtown_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRegion_id() {
        return region_id;
    }

    public void setRegion_id(int region_id) {
        this.region_id = region_id;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }

    public int getDivision_id() {
        return division_id;
    }

    public void setDivision_id(int division_id) {
        this.division_id = division_id;
    }

    public String getDivision_name() {
        return division_name;
    }

    public void setDivision_name(String division_name) {
        this.division_name = division_name;
    }

    public boolean isShow_town() {
        return show_town;
    }

    public void setShow_town(boolean show_town) {
        this.show_town = show_town;
    }

    public boolean isShow_all_town() {
        return show_all_town;
    }

    public void setShow_all_town(boolean show_all_town) {
        this.show_all_town = show_all_town;
    }


    public String getDivision_nameUrdu() {
        return division_nameUrdu;
    }

    public void setDivision_nameUrdu(String division_nameUrdu) {
        this.division_nameUrdu = division_nameUrdu;
    }

    public String getDistrict_nameUrdu() {
        return district_nameUrdu;
    }

    public void setDistrict_nameUrdu(String district_nameUrdu) {
        this.district_nameUrdu = district_nameUrdu;
    }

    public String getTown_nameUrdu() {
        return town_nameUrdu;
    }

    public void setTown_nameUrdu(String town_nameUrdu) {
        this.town_nameUrdu = town_nameUrdu;
    }

    public String getSubtown_nameUrdu() {
        return subtown_nameUrdu;
    }

    public void setSubtown_nameUrdu(String subtown_nameUrdu) {
        this.subtown_nameUrdu = subtown_nameUrdu;
    }
}

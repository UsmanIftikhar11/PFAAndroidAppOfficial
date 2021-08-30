package com.pfa.pfaapp.localdbmodels;

import java.io.Serializable;

public class DistrictInfo implements Serializable {

    //    https://cell.pfa.gop.pk/test/api/client/get_locations
    private int division_id;//: "3",
    private int district_id;//: "4",
    private String district_name;//: "Attock",
    private String district_nameUrdu;

    public int getDivision_id() {
        return division_id;
    }

    public void setDivision_id(int division_id) {
        this.division_id = division_id;
    }

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

    public String getDistrict_nameUrdu() {
        return district_nameUrdu;
    }

    public void setDistrict_nameUrdu(String district_nameUrdu) {
        this.district_nameUrdu = district_nameUrdu;
    }
}



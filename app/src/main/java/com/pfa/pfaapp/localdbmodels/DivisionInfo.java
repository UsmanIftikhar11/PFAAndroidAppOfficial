package com.pfa.pfaapp.localdbmodels;

import java.io.Serializable;

public class DivisionInfo implements Serializable {
    private int region_id;//: "2",
    private int division_id;//: "3",
    private String division_name;//: "Rawalpindi",
    private String division_nameUrdu;

    public int getRegion_id() {
        return region_id;
    }

    public void setRegion_id(int region_id) {
        this.region_id = region_id;
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

    public String getDivision_nameUrdu() {
        return division_nameUrdu;
    }

    public void setDivision_nameUrdu(String division_nameUrdu) {
        this.division_nameUrdu = division_nameUrdu;
    }
}

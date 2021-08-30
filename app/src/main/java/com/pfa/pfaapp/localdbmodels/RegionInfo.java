package com.pfa.pfaapp.localdbmodels;

import java.io.Serializable;

public class RegionInfo implements Serializable{
    private int province_id;//: "1",
    private int region_id;//: "2",
    private String region_name;//: "North"

    public int getProvince_id() {
        return province_id;
    }

    public void setProvince_id(int province_id) {
        this.province_id = province_id;
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
}

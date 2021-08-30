package com.pfa.pfaapp.localdbmodels;

import java.io.Serializable;

public class TownInfo implements Serializable {
    private int town_id;//": "5",
    private String district_id;//": "3",
    private String town_name;//": "Potohar Town"
    private String town_nameUrdu;

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    public String getTown_name() {
        return town_name;
    }

    public void setTown_name(String town_name) {
        this.town_name = town_name;
    }

    public int getTown_id() {
        return town_id;
    }

    public void setTown_id(int town_id) {
        this.town_id = town_id;
    }

    public String getTown_nameUrdu() {
        return town_nameUrdu;
    }

    public void setTown_nameUrdu(String town_nameUrdu) {
        this.town_nameUrdu = town_nameUrdu;
    }
}

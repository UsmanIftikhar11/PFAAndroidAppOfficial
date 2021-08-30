package com.pfa.pfaapp.localdbmodels;

import java.io.Serializable;

public class SubTownInfo implements Serializable {
    private int subtown_id;//": "5",
    private String town_id;//": "3",
    private String subtown_name;//": "Potohar Town"
    private String subtown_nameUrdu;

    public int getSubtown_id() {
        return subtown_id;
    }

    public void setSubtown_id(int subtown_id) {
        this.subtown_id = subtown_id;
    }

    public String getTown_id() {
        return town_id;
    }

    public void setTown_id(String town_id) {
        this.town_id = town_id;
    }

    public String getSubtown_name() {
        return subtown_name;
    }

    public void setSubtown_name(String subtown_name) {
        this.subtown_name = subtown_name;
    }

    public String getSubtown_nameUrdu() {
        return subtown_nameUrdu;
    }

    public void setSubtown_nameUrdu(String subtown_nameUrdu) {
        this.subtown_nameUrdu = subtown_nameUrdu;
    }
}

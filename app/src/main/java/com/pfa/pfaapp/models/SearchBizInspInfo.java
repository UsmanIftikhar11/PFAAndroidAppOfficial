package com.pfa.pfaapp.models;

public class SearchBizInspInfo {
    private int inspection_id;//: "43693",
    private String start_date;//: "2018-02-16"
    private String inspection_type;
    private String API_URL;
    private String max_draft_limit;


    public String getMax_draft_limit() {
        return max_draft_limit;
    }

    public void setMax_draft_limit(String max_draft_limit) {
        this.max_draft_limit = max_draft_limit;
    }

    public int getInspection_id() {
        return inspection_id;
    }

    public void setInspection_id(int inspection_id) {
        this.inspection_id = inspection_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getInspection_type() {
        return inspection_type;
    }

    public void setInspection_type(String inspection_type) {
        this.inspection_type = inspection_type;
    }

    public String getAPI_URL() {
        return API_URL;
    }

    public void setAPI_URL(String API_URL) {
        this.API_URL = API_URL;
    }
}

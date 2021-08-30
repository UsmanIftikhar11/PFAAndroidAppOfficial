package com.pfa.pfaapp.models;

import java.io.Serializable;
import java.util.List;

public class ShowHiddenFalseFields implements Serializable {
    private String checkKey;
    private List<String> checkViews;


    public String getCheckKey() {
        return checkKey;
    }

    public void setCheckKey(String checkKey) {
        this.checkKey = checkKey;
    }

    public List<String> getCheckViews() {
        return checkViews;
    }

    public void setCheckViews(List<String> checkViews) {
        this.checkViews = checkViews;
    }
}

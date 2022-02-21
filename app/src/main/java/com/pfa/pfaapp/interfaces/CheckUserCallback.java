package com.pfa.pfaapp.interfaces;

import org.json.JSONArray;

public interface CheckUserCallback {
    void getExistingUser(JSONArray jsonArray);
    void getExistingBusiness(JSONArray jsonArray , String msg);

}

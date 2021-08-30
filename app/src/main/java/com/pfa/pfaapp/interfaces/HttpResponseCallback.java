package com.pfa.pfaapp.interfaces;

import org.json.JSONObject;

public interface HttpResponseCallback {

    void onCompleteHttpResponse(JSONObject response, String requestUrl);
}

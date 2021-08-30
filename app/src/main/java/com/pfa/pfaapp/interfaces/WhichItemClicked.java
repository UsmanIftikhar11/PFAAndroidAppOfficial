package com.pfa.pfaapp.interfaces;

public interface WhichItemClicked {
    void whichItemClicked(String id);
    void downloadInspection(String downloadUrl, int position);
    void deleteRecordAPICall(String deleteUrl, int position);
}

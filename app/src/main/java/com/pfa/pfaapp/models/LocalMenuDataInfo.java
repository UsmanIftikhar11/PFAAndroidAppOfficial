package com.pfa.pfaapp.models;

import java.util.List;

public class LocalMenuDataInfo {
    private String add_new;
    private List<FormSectionInfo> form;
    private int item_count;
    private boolean showDeleteIcon;
    private int add_proof;
    private String proofImagePath;
    private String printData;

    public List<FormSectionInfo> getForm() {
        return form;
    }

    public void setForm(List<FormSectionInfo> form) {
        this.form = form;
    }

    public String getAdd_new() {
        return add_new;
    }

    public void setAdd_new(String add_new) {
        this.add_new = add_new;
    }

    public int getItem_count() {
        return item_count;
    }

    public void setItem_count(int item_count) {
        this.item_count = item_count;
    }


    public boolean isShowDeleteIcon() {
        return showDeleteIcon;
    }

    public void setShowDeleteIcon(boolean showDeleteIcon) {
        this.showDeleteIcon = showDeleteIcon;
    }

    public int getAdd_proof() {
        return add_proof;
    }

    public void setAdd_proof(int add_proof) {
        this.add_proof = add_proof;
    }

    public String getProofImagePath() {
        return proofImagePath;
    }

    public void setProofImagePath(String proofImagePath) {
        this.proofImagePath = proofImagePath;
    }

    public String getPrintData() {
        return printData;
    }

    public void setPrintData(String printData) {
        this.printData = printData;
    }
}

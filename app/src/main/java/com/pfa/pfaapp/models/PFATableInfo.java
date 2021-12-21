package com.pfa.pfaapp.models;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.net.URL;

public class PFATableInfo implements Serializable {
    private String field_name;
    private String field_link;
    private String field_type;//": "text",
    private int order;//": 1,
    private String value;//": "",
    private String valueUrdu;
    private String icon;//": "",
    private String data;//": "Swift Beef Company Recalls Beef Stew Products Due ",
    private String dataUrdu;
    private String API_URL;//": "LatestNews\/news\/5"
    private String status_color;
    private boolean invisible;
    private String font_size;
    private String font_style;
    private String font_color;
    private String direction;
    private String margin_top;
    private String lat_lng;
    private String local_add_newUrl;
    private String download_url;
    private boolean show_download_btn;
    private String clickable_text;
    private String license_number;
    private String cell_bg_color;
    private String shape;
    private boolean isNotClickable;
    private String shareHtmlStr;
    private String delete_url;
    //PRINTING MODULE INFO
    private String barcode_url;
    private String printHtmlStr;
    private String receipt_logo;

    @SerializedName("printData")
    private String printData;

    @SerializedName("conducted_inspection")
    private boolean conducted_inspection;

    private String max_draft_limit;


    public String getField_link() {
        return field_link;
    }

    public void setField_link(String field_link) {
        this.field_link = field_link;
    }

    public boolean getConducted_inspection() {
        return conducted_inspection;
    }

    public String getMax_draft_limit() {
        return max_draft_limit;
    }

    public void setConducted_inspection(boolean conducted_inspection) {
        this.conducted_inspection = conducted_inspection;
    }

    public String getPrintData() {
        return printData;
    }
//
    public void setPrintData(String printData) {
        this.printData = printData;
    }

    public String getField_type() {
        return field_type;
    }

    public void setField_type(String field_type) {
        this.field_type = field_type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getData() {
        if (data == null)
            data = "";
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAPI_URL() {
        return API_URL;
    }

    public void setAPI_URL(String API_URL) {
        this.API_URL = API_URL;
    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public String getStatus_color() {
        return status_color;
    }

    public void setStatus_color(String status_color) {
        this.status_color = status_color;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public String getFont_size() {
        return font_size;
    }

    public void setFont_size(String font_size) {
        this.font_size = font_size;
    }

    public String getFont_style() {
        return font_style;
    }

    public void setFont_style(String font_style) {
        this.font_style = font_style;
    }

    public String getFont_color() {
        return font_color;
    }

    public void setFont_color(String font_color) {
        this.font_color = font_color;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getMargin_top() {
        return margin_top;
    }

    public void setMargin_top(String margin_top) {
        this.margin_top = margin_top;
    }

    public String getLat_lng() {
        return lat_lng;
    }

    public void setLat_lng(String lat_lng) {
        this.lat_lng = lat_lng;
    }

    public String getLocal_add_newUrl() {
        return local_add_newUrl;
    }

    public void setLocal_add_newUrl(String local_add_newUrl) {
        this.local_add_newUrl = local_add_newUrl;
    }

    public String getClickable_text() {
        return clickable_text;
    }

    public void setClickable_text(String clickable_text) {
        this.clickable_text = clickable_text;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getLicense_number() {
        return license_number;
    }

    public void setLicense_number(String license_number) {
        this.license_number = license_number;
    }

    public boolean isShow_download_btn() {
        return show_download_btn;
    }

    public void setShow_download_btn(boolean show_download_btn) {
        this.show_download_btn = show_download_btn;
    }

    public String getCell_bg_color() {
        return cell_bg_color;
    }

    public void setCell_bg_color(String cell_bg_color) {
        this.cell_bg_color = cell_bg_color;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public boolean isNotClickable() {
        return isNotClickable;
    }

    public void setNotClickable(boolean notClickable) {
        isNotClickable = notClickable;
    }

    public String getShareHtmlStr() {
        return shareHtmlStr;
    }

    public void setShareHtmlStr(String shareHtmlStr) {
        this.shareHtmlStr = shareHtmlStr;
    }

    public String getDataUrdu() {
        if (dataUrdu == null)
            dataUrdu = "";
        return dataUrdu;
    }

    public void setDataUrdu(String dataUrdu) {
        this.dataUrdu = dataUrdu;
    }

    public String getValueUrdu() {
        return valueUrdu;
    }

    public void setValueUrdu(String valueUrdu) {
        this.valueUrdu = valueUrdu;
    }

    public String getDelete_url() {
        return delete_url;
    }

    public void setDelete_url(String delete_url) {
        this.delete_url = delete_url;
    }

    public String getPrintHtmlStr() {
        return printHtmlStr;
    }

    public void setPrintHtmlStr(String printHtmlStr) {
        this.printHtmlStr = printHtmlStr;
    }
// generate gtter and setter


    public String getBarcode_url() {
        return barcode_url;
    }

    public void setBarcode_url(String barcode_url) {
        this.barcode_url = barcode_url;
    }

    public String getReceipt_logo() {
        return receipt_logo;
    }

    public void setReceipt_logo(String receipt_logo) {
        this.receipt_logo = receipt_logo;
    }
}

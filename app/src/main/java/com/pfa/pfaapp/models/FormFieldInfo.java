package com.pfa.pfaapp.models;

import java.io.Serializable;
import java.util.List;

public class FormFieldInfo implements Serializable {
    private String field_name;//": "designation",
    private String field_type;//": "dropdown",
    private String data_type;//": "text",
    private int order;//": 0,
    private String icon;//' => 'icon.png',
    private String placeholder;//' => ''
    private int limit;//' => '',
    private boolean required;//":true,
    private boolean single_required;
    private boolean multiple;//":true,
    private String value;//":"Designation",
    private String valueUrdu;
    private String default_value;//":"Owner",
    private boolean bottom_hr_line = true;//":"heading bottom line",
    private boolean clickable;
    private boolean horizontal;
    private List<FormDataInfo> data;
    private String API_URL;
    private String DOWNLOAD_URL;
    private String action;
    private boolean isNotEditable;
    private String calendarType;
    private boolean invisible;
    private String font_size;
    private String font_style;
    private String font_color;
    private String direction;
    private String clickable_text;
    private String clickable_textUrdu;
    private AddressObjInfo default_locations;
    private boolean add_more;
    private boolean deleteImg;
    private int min_limit;
    private int max_limit;
    private String map_info; // on in case of set business location
    private String date_from;
    private String date_to;
    private boolean showBizConfirmMsg;
    private boolean hide_clear;
    ///////////////////
    private String printData;
    private String shareHtmlStr;
    private String printHtmlStr;


    private List<String> whichLocationToHide;

    private List<String> check_value;
    private List<String> required_false_fields;

    private List<String> show_check_value;
    private List<ShowHiddenFalseFields> show_hidden_false_fields;

    private List<String> disable_fields;
    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public String getField_type() {
        return field_type;
    }

    public void setField_type(String field_type) {
        this.field_type = field_type;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefault_value() {
        return default_value;
    }

    public void setDefault_value(String default_value) {
        this.default_value = default_value;
    }

    public boolean isBottom_hr_line() {
        return bottom_hr_line;
    }

    public void setBottom_hr_line(boolean bottom_hr_line) {
        this.bottom_hr_line = bottom_hr_line;
    }

    public List<FormDataInfo> getData() {
        return data;
    }

    public void setData(List<FormDataInfo> data) {
        this.data = data;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public String getAPI_URL() {
        return API_URL;
    }

    public void setAPI_URL(String API_URL) {
        this.API_URL = API_URL;
    }

    public String getDOWNLOAD_URL() {
        return DOWNLOAD_URL;
    }

    public void setDOWNLOAD_URL(String DOWNLOAD_URL) {
        this.DOWNLOAD_URL = DOWNLOAD_URL;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isNotEditable() {
        return isNotEditable;
    }

    public void setNotEditable(boolean notEditable) {
        isNotEditable = notEditable;
    }

    public String getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
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

    public AddressObjInfo getDefault_locations() {
        return default_locations;
    }

    public void setDefault_locations(AddressObjInfo default_locations) {
        this.default_locations = default_locations;
    }

    public String getClickable_text() {
        return clickable_text;
    }

    public void setClickable_text(String clickable_text) {
        this.clickable_text = clickable_text;
    }

    public boolean isAdd_more() {
        return add_more;
    }

    public void setAdd_more(boolean add_more) {
        this.add_more = add_more;
    }


    public boolean isDeleteImg() {
        return deleteImg;
    }

    public void setDeleteImg(boolean deleteImg) {
        this.deleteImg = deleteImg;
    }

    public int getMin_limit() {
        return min_limit;
    }

    public void setMin_limit(int min_limit) {
        this.min_limit = min_limit;
    }

    public int getMax_limit() {
        return max_limit;
    }

    public void setMax_limit(int max_limit) {
        this.max_limit = max_limit;
    }

    public String getMap_info() {
        return map_info;
    }

    public void setMap_info(String map_info) {
        this.map_info = map_info;
    }

    public List<String> getWhichLocationToHide() {
        return whichLocationToHide;
    }

    public void setWhichLocationToHide(List<String> whichLocationToHide) {
        this.whichLocationToHide = whichLocationToHide;
    }

    public String getDate_from() {
        return date_from;
    }

    public void setDate_from(String date_from) {
        this.date_from = date_from;
    }

    public String getDate_to() {
        return date_to;
    }

    public void setDate_to(String date_to) {
        this.date_to = date_to;
    }

    public boolean isShowBizConfirmMsg() {
        return showBizConfirmMsg;
    }

    public void setShowBizConfirmMsg(boolean showBizConfirmMsg) {
        this.showBizConfirmMsg = showBizConfirmMsg;
    }

    public boolean isHide_clear() {
        return hide_clear;
    }

    public void setHide_clear(boolean hide_clear) {
        this.hide_clear = hide_clear;
    }


    public List<String> getRequired_false_fields() {
        return required_false_fields;
    }

    public void setRequired_false_fields(List<String> required_false_fields) {
        this.required_false_fields = required_false_fields;
    }

    public List<String> getCheck_value() {
        return check_value;
    }

    public void setCheck_value(List<String> check_value) {
        this.check_value = check_value;
    }

    public String getValueUrdu() {
        if(valueUrdu==null)
            valueUrdu="";
        return valueUrdu;
    }

    public void setValueUrdu(String valueUrdu) {
        this.valueUrdu = valueUrdu;
    }

    public String getClickable_textUrdu() {
        return clickable_textUrdu;
    }

    public void setClickable_textUrdu(String clickable_textUrdu) {
        this.clickable_textUrdu = clickable_textUrdu;
    }

    public List<String> getShow_check_value() {
        return show_check_value;
    }

    public void setShow_check_value(List<String> show_check_value) {
        this.show_check_value = show_check_value;
    }

    public List<ShowHiddenFalseFields> getShow_hidden_false_fields() {
        return show_hidden_false_fields;
    }

    public void setShow_hidden_false_fields(List<ShowHiddenFalseFields> show_hidden_false_fields) {
        this.show_hidden_false_fields = show_hidden_false_fields;
    }

    public List<String> getDisable_fields() {
        return disable_fields;
    }

    public void setDisable_fields(List<String> disable_fields) {
        this.disable_fields = disable_fields;

    }

    public boolean isSingle_required() {
        return single_required;
    }

    public void setSingle_required(boolean single_required) {
        this.single_required = single_required;
    }

    public String getPrintData() {
        return printData;
    }

    public void setPrintData(String printData) {
        this.printData = printData;
    }

    public String getShareHtmlStr() {
        return shareHtmlStr;
    }

    public void setShareHtmlStr(String shareHtmlStr) {
        this.shareHtmlStr = shareHtmlStr;
    }

    public String getPrintHtmlStr() {
        return printHtmlStr;
    }

    public void setPrintHtmlStr(String printHtmlStr) {
        this.printHtmlStr = printHtmlStr;
    }
}

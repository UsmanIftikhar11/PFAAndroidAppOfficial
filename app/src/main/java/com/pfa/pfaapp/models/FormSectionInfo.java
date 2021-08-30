package com.pfa.pfaapp.models;

import java.io.Serializable;
import java.util.List;

public class FormSectionInfo implements Serializable {
    private String section_id;//": "",
    private String section_name;//":"",
    private String section_nameUrdu;
    private String add_new;


    private List<FormFieldInfo> fields;

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    public List<FormFieldInfo> getFields() {
        return fields;
    }

    public void setFields(List<FormFieldInfo> fields) {
        this.fields = fields;
    }


    public String getAdd_new() {
        return add_new;
    }

    public void setAdd_new(String add_new) {
        this.add_new = add_new;
    }

    public String getSection_nameUrdu() {
        return section_nameUrdu;
    }

    public void setSection_nameUrdu(String section_nameUrdu) {
        this.section_nameUrdu = section_nameUrdu;
    }


}

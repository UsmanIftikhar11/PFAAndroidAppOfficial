package com.pfa.pfaapp.models;

import java.io.Serializable;

public class UserInfo implements Serializable {
    // Staff Params
    private int user_id;//": "21",
    private String email;//": "test2@test.com",
    private String firstname;//":"Test",
    private String lastname;//":"Test last",
    private String scale;//":"0",
    private String cnic_number;//":"8888888888888",
    private String phonenumber;//":"88888888888",
    private String qualification;//":"Intermediate",
    private String type;//":"Permanent",
    private String profile_image;//":null,
    private String full_path;
    private AddressObjInfo address_obj;
    //Client Login Extra Params
    private double latitude;//: "",
    private double longitude;//: "",
    private String position;//: "owner",
    private String company;//: "shah jee super store",
    private String date_of_birth;//: "0000-00-00",
    private String alternate_phonenumber;//: "",
    private String location;//: "",
    private String category_name;//: "Super Stores",
    private String category_id;//: "78",
    private String application_image;//: "",
    private String application_cnic_image;//: "",
    private String application_business_image;//: "",
    private String license_number;//: "LHR/A21802-1043406",
    private String dateend;//: "2023-02-01",


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getCnic_number() {
        return cnic_number;
    }

    public void setCnic_number(String cnic_number) {
        this.cnic_number = cnic_number;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }


    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }


    public AddressObjInfo getAddress_obj() {
        return address_obj;
    }

    public void setAddress_obj(AddressObjInfo address_obj) {
        this.address_obj = address_obj;
    }


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFull_path() {
        return full_path;
    }

    public void setFull_path(String full_path) {
        this.full_path = full_path;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getAlternate_phonenumber() {
        return alternate_phonenumber;
    }

    public void setAlternate_phonenumber(String alternate_phonenumber) {
        this.alternate_phonenumber = alternate_phonenumber;
    }

    public String getApplication_business_image() {
        return application_business_image;
    }

    public void setApplication_business_image(String application_business_image) {
        this.application_business_image = application_business_image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getApplication_image() {
        return application_image;
    }

    public void setApplication_image(String application_image) {
        this.application_image = application_image;
    }

    public String getApplication_cnic_image() {
        return application_cnic_image;
    }

    public void setApplication_cnic_image(String application_cnic_image) {
        this.application_cnic_image = application_cnic_image;
    }

    public String getLicense_number() {
        return license_number;
    }

    public void setLicense_number(String license_number) {
        this.license_number = license_number;
    }

    public String getDateend() {
        return dateend;
    }

    public void setDateend(String dateend) {
        this.dateend = dateend;
    }
}

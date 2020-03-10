package com.example.shidan.ges.model;

public class User {


    private String worker_id = "";
    private String worker_type = "";
    private String fname = "" ;
    private String midname = "";
    private String lname = "";
    private String email = "";
    private String phone = "";
    private String address = "";
    private int postcode = 0;
    private String state = "";
    private String country = "";
    private String supervisor_id = "";

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;

    public User()
    {}
    public User(String worker_id, String worker_type, String fname, String midname, String lname, String email, String phone, String address, int postcode, String state, String country) {
        this.worker_id = worker_id;
        this.worker_type = worker_type;
        this.fname = fname;
        this.midname = midname;
        this.lname = lname;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.postcode = postcode;
        this.state = state;
        this.country = country;
    }
    public User(String worker_id, String worker_type, String fname, String midname, String lname, String email, String phone, String address, int postcode, String state, String country, String supervisor_id) {
        this.worker_id = worker_id;
        this.worker_type = worker_type;
        this.fname = fname;
        this.midname = midname;
        this.lname = lname;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.postcode = postcode;
        this.state = state;
        this.country = country;
        this.supervisor_id = supervisor_id;
    }

    public String getWorker_id() {
        return worker_id;
    }

    public void setWorker_id(String worker_id) {
        this.worker_id = worker_id;
    }

    public String getWorker_type() {
        return worker_type;
    }

    public void setWorker_type(String worker_type) {
        this.worker_type = worker_type;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMidname() {
        return midname;
    }

    public void setMidname(String midname) {
        this.midname = midname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSupervisor_id() {
        return supervisor_id;
    }

    public void setSupervisor_id(String supervisor_id) {
        this.supervisor_id = supervisor_id;
    }


}

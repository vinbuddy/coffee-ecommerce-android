package com.coffee.app.model;

public class Store {
    private int id;
    private String store_name;
    private String address;
    private String city;
    private String google_map_location;
    private String open_time;
    private String close_time;
    private String district;
    private String ward;
    private String image;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStoreName() {
        return store_name;
    }

    public void setStoreName(String store_name) {
        this.store_name = store_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGoogleMapLocation() {
        return google_map_location;
    }

    public void setGoogleMapLocation(String google_map_location) {
        this.google_map_location = google_map_location;
    }

    public String getOpenTime() {
        return open_time;
    }

    public void setOpenTime(String open_time) {
        this.open_time = open_time;
    }

    public String getCloseTime() {
        return close_time;
    }

    public void setCloseTime(String close_time) {
        this.close_time = close_time;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
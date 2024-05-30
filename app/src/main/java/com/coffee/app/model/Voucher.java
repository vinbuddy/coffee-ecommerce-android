package com.coffee.app.model;

import java.time.DateTimeException;

public class Voucher {
    int id;
    String voucher_name;
    String description;
    String start_date;
    String end_date;
    String image;
    double discount_price;
    String discount_type;
    double min_order_price;

    public Voucher() {
    }

    public Voucher(int id, String voucher_name, String description, String start_date, String end_date, String image, double discount_price, String discount_type, double min_order_price) {
        this.id = id;
        this.voucher_name = voucher_name;
        this.description = description;
        this.start_date = start_date;
        this.end_date = end_date;
        this.image = image;
        this.discount_price = discount_price;
        this.discount_type = discount_type;
        this.min_order_price = min_order_price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVoucher_name() {
        return voucher_name;
    }

    public void setVoucher_name(String voucher_name) {
        this.voucher_name = voucher_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public void setDiscount_type(String discount_type) {
        this.discount_type = discount_type;
    }

    public double getMin_order_price() {
        return min_order_price;
    }

    public void setMin_order_price(double min_order_price) {
        this.min_order_price = min_order_price;
    }

    public double getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(double discount_price) {
        this.discount_price = discount_price;
    }
}

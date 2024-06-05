package com.coffee.app.model;

import java.util.ArrayList;

public class Order {
    private String id;
    private String user_id;
    private double total_payment;
    private String payment_method;
    private String order_status;
    private String order_type;
    private String order_date;
    private String order_note;
    private double shipping_cost;
    private String receiver_name;
    private String phone_number;
    private String address;
    private int store_id;
    private int voucher_id;
    private String voucher_name;
    private String store_name;
    private ArrayList<Cart> order_items;
    private boolean is_reviewed;

    private String user_name;
    private String email;
    private String avatar;

    // Constructors
    public Order() {
    }

    public Order(String id, String user_id, double total_payment, String payment_method, String order_status,
                 String order_type, String order_date, String order_note, double shipping_cost, String receiver_name,
                 String phone_number, String address, int store_id, int voucher_id, String voucher_name, String store_name,
                 ArrayList<Cart> order_items, boolean is_reviewed, String user_name, String email, String avatar) {
        this.id = id;
        this.user_id = user_id;
        this.total_payment = total_payment;
        this.payment_method = payment_method;
        this.order_status = order_status;
        this.order_type = order_type;
        this.order_date = order_date;
        this.order_note = order_note;
        this.shipping_cost = shipping_cost;
        this.receiver_name = receiver_name;
        this.phone_number = phone_number;
        this.address = address;
        this.store_id = store_id;
        this.voucher_id = voucher_id;
        this.voucher_name = voucher_name;
        this.store_name = store_name;
        this.order_items = order_items;
        this.is_reviewed = is_reviewed;
        this.user_name = user_name;
        this.email = email;
        this.avatar = avatar;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public double getTotalPayment() {
        return total_payment;
    }

    public void setTotalPayment(double total_payment) {
        this.total_payment = total_payment;
    }

    public String getPaymentMethod() {
        return payment_method;
    }

    public void setPaymentMethod(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getOrderStatus() {
        return order_status;
    }

    public void setOrderStatus(String order_status) {
        this.order_status = order_status;
    }

    public String getOrderType() {
        return order_type;
    }

    public void setOrderType(String order_type) {
        this.order_type = order_type;
    }

    public String getOrderDate() {
        return order_date;
    }

    public void setOrderDate(String order_date) {
        this.order_date = order_date;
    }

    public String getOrderNote() {
        return order_note;
    }

    public void setOrderNote(String order_note) {
        this.order_note = order_note;
    }

    public double getShippingCost() {
        return shipping_cost;
    }

    public void setShippingCost(double shipping_cost) {
        this.shipping_cost = shipping_cost;
    }

    public String getReceiverName() {
        return receiver_name;
    }

    public void setReceiverName(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStoreId() {
        return store_id;
    }

    public void setStoreId(int store_id) {
        this.store_id = store_id;
    }

    public int getVoucherId() {
        return voucher_id;
    }

    public void setVoucherId(int voucher_id) {
        this.voucher_id = voucher_id;
    }

    public String getVoucherName() {
        return voucher_name;
    }

    public void setVoucherName(String voucher_name) {
        this.voucher_name = voucher_name;
    }

    public String getStoreName() {
        return store_name;
    }

    public void setStoreName(String store_name) {
        this.store_name = store_name;
    }

    public ArrayList<Cart> getOrderItems() {
        return order_items;
    }

    public void setOrderItems(ArrayList<Cart> order_items) {
        this.order_items = order_items;
    }

    public boolean isReviewed() {
        return is_reviewed;
    }

    public void setReviewed(boolean is_reviewed) {
        this.is_reviewed = is_reviewed;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

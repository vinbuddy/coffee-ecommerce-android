package com.coffee.app.model;

import java.util.ArrayList;

public class Order {
    private String id;
    private String userId;
    private double totalPayment;
    private String paymentMethod;
    private String orderStatus;
    private String orderType;
    private String orderDate;
    private String orderNote;
    private double shippingCost;
    private String receiverName;
    private String phoneNumber;
    private String address;
    private int storeId;
    private int voucherId;
    private String voucherName;
    private String storeName;
    private ArrayList<Cart> orderItems;
    private boolean isReviewed;

    // Getters and Setters

    // Constructors
    public Order() {
    }

    public Order(String id, String userId, double totalPayment, String paymentMethod, String orderStatus,
                 String orderType, String orderDate, String orderNote, double shippingCost, String receiverName,
                 String phoneNumber, String address, int storeId, int voucherId, String voucherName, String storeName,
                 ArrayList<Cart> orderItems, boolean isReviewed) {
        this.id = id;
        this.userId = userId;
        this.totalPayment = totalPayment;
        this.paymentMethod = paymentMethod;
        this.orderStatus = orderStatus;
        this.orderType = orderType;
        this.orderDate = orderDate;
        this.orderNote = orderNote;
        this.shippingCost = shippingCost;
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.storeId = storeId;
        this.voucherId = voucherId;
        this.voucherName = voucherName;
        this.storeName = storeName;
        this.orderItems = orderItems;
        this.isReviewed = isReviewed;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public String getVoucherName() {
        return voucherName;
    }

    public void setVoucherName(String voucherName) {
        this.voucherName = voucherName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public ArrayList<Cart> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<Cart> orderItems) {
        this.orderItems = orderItems;
    }

    public boolean isReviewed() {
        return isReviewed;
    }

    public void setReviewed(boolean reviewed) {
        isReviewed = reviewed;
    }
}

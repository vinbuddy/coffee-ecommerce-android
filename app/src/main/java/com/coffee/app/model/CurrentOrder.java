package com.coffee.app.model;

import java.util.ArrayList;
import java.util.Map;

public class CurrentOrder {
    private String orderId;
    private String userId;

    private ArrayList<OrderStatus> statuses;

    private boolean isCompleted;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<OrderStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<OrderStatus> statuses) {
        this.statuses = statuses;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public CurrentOrder() {
    }

    public CurrentOrder(String orderId, String userId, ArrayList<OrderStatus> statuses, boolean isCompleted) {
        this.orderId = orderId;
        this.userId = userId;
        this.statuses = statuses;
        this.isCompleted = isCompleted;
    }

    public static class OrderStatus {
        private String status;
        private String time;

        public  OrderStatus() {

        }

        public OrderStatus(String status, String time) {
            this.status = status;
            this.time = time;
        }



        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }


    public static class InitCurrentOrder extends CurrentOrder {
        private Map<String, Map<String, String>> statusMap;

        public InitCurrentOrder() {
            super();
        }

        public InitCurrentOrder(String orderId, String userId, ArrayList<OrderStatus> statuses, boolean isCompleted, Map<String, Map<String, String>> statusMap) {
            super(orderId, userId, statuses, isCompleted);
            this.statusMap = statusMap;
        }

        public Map<String, Map<String, String>> getStatusMap() {
            return statusMap;
        }

    }

}



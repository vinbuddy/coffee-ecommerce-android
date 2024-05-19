package com.coffee.app.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    public int id;
    private int product_id;
    private String product_name;
    private double product_price;
    private int product_status;
    private String product_image;
    private int size_id;
    private String size_name;
    private double size_price;
    private int quantity;
    private ArrayList<CartTopping> toppings;
    private double order_item_price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return product_id;
    }

    public void setProductId(int product_id) {
        this.product_id = product_id;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public double getProductPrice() {
        return product_price;
    }

    public void setProductPrice(double product_price) {
        this.product_price = product_price;
    }

    public int getProductStatus() {
        return product_status;
    }

    public void setProductStatus(int product_status) {
        this.product_status = product_status;
    }

    public String getProductImage() {
        return product_image;
    }

    public void setProductImage(String product_image) {
        this.product_image = product_image;
    }

    public int getSizeId() {
        return size_id;
    }

    public void setSizeId(int size_id) {
        this.size_id = size_id;
    }

    public String getSizeName() {
        return size_name;
    }

    public void setSizeName(String size_name) {
        this.size_name = size_name;
    }

    public double getSizePrice() {
        return size_price;
    }

    public void setSizePrice(double size_price) {
        this.size_price = size_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ArrayList<CartTopping> getToppings() {
        return toppings;
    }

    public void setToppings(ArrayList<CartTopping> toppings) {
        this.toppings = toppings;
    }

    public double getOrderItemPrice() {
        return order_item_price;
    }

    public void setOrderItemPrice(double order_item_price) {
        this.order_item_price = order_item_price;
    }


    public static class CartTopping {
        private int topping_storage_id;
        private int topping_id;
        private String topping_name;
        private double topping_price;

        public int getToppingStorageId() {
            return topping_storage_id;
        }

        public void setToppingStorageId(int topping_storage_id) {
            this.topping_storage_id = topping_storage_id;
        }

        public int getToppingId() {
            return topping_id;
        }

        public void setToppingId(int topping_id) {
            this.topping_id = topping_id;
        }

        public String getToppingName() {
            return topping_name;
        }

        public void setToppingName(String topping_name) {
            this.topping_name = topping_name;
        }

        public double getToppingPrice() {
            return topping_price;
        }

        public void setToppingPrice(double topping_price) {
            this.topping_price = topping_price;
        }
    }

}


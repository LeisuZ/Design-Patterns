package com.ecommerce.cart.model;

import java.io.Serializable;

public class Coupon implements Serializable {
    private String id;
    private String name;
    private double value;
    private double minSpend;
    
    public Coupon(String id, String name, double value, double minSpend) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.minSpend = minSpend;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = value;
    }
    
    public double getMinSpend() {
        return minSpend;
    }
    
    public void setMinSpend(double minSpend) {
        this.minSpend = minSpend;
    }
    
    @Override
    public String toString() {
        return name + " (满" + minSpend + "减" + value + ")";
    }
}
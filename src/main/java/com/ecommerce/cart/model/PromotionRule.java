package com.ecommerce.cart.model;

public class PromotionRule implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String type;
    private double value;
    private double minSpend;
    private int priority;
    
    public PromotionRule(String id, String name, String type, double value, double minSpend, int priority) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
        this.minSpend = minSpend;
        this.priority = priority;
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
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
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
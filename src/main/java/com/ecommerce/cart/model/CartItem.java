package com.ecommerce.cart.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private ProductInterface product;
    private int quantity;
    
    public CartItem(ProductInterface product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    public ProductInterface getProduct() {
        return product;
    }
    
    public void setProduct(ProductInterface product) {
        this.product = product;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getSubtotal() {
        return product.getPrice() * quantity;
    }
}
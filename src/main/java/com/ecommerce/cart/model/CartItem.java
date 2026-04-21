package com.ecommerce.cart.model;

import java.io.Serializable;

public class CartItem implements Serializable, Cloneable {
    private static final long serialVersionUID = 6901365948722618489L;
    private ProductInterface product;
    private int quantity;
    private String decoratedName;
    private Double decoratedPrice;
    
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
        if (decoratedPrice != null) {
            return decoratedPrice * quantity;
        }
        return product.getPrice() * quantity;
    }
    
    public String getDisplayName() {
        if (decoratedName != null && !decoratedName.isEmpty()) {
            return decoratedName;
        }
        return product.getName();
    }
    
    public double getDisplayPrice() {
        if (decoratedPrice != null) {
            return decoratedPrice;
        }
        return product.getPrice();
    }
    
    public String getDecoratedName() {
        return decoratedName;
    }

    public void setDecoratedName(String decoratedName) {
        this.decoratedName = decoratedName;
    }
    
    public void setDecoratedPrice(Double decoratedPrice) {
        this.decoratedPrice = decoratedPrice;
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (decoratedPrice != null && decoratedPrice == 0.0 && decoratedName == null) {
            decoratedPrice = null;
        }
    }

    @Override
    public CartItem clone() {
        try {
            CartItem cloned = (CartItem) super.clone();
            if (product instanceof Product) {
                cloned.product = ((Product) product).clone();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
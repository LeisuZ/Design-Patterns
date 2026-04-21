package com.ecommerce.cart.pattern.decorator;

import com.ecommerce.cart.model.ProductInterface;

public class DiscountDecorator extends ProductDecorator {
    private double discountRate;
    
    public DiscountDecorator(ProductInterface decoratedProduct, double discountRate) {
        super(decoratedProduct);
        this.discountRate = discountRate;
    }
    
    @Override
    public double getPrice() {
        return decoratedProduct.getPrice() * (1 - discountRate);
    }
    
    @Override
    public String getName() {
        return decoratedProduct.getName() + " (折扣: " + (int)(discountRate * 100) + "%)";
    }
}
package com.ecommerce.cart.pattern.decorator;

import com.ecommerce.cart.model.ProductInterface;

public abstract class ProductDecorator implements ProductInterface {
    protected ProductInterface decoratedProduct;
    
    public ProductDecorator(ProductInterface decoratedProduct) {
        this.decoratedProduct = decoratedProduct;
    }
    
    @Override
    public String getId() {
        return decoratedProduct.getId();
    }
    
    @Override
    public String getName() {
        return decoratedProduct.getName();
    }
    
    @Override
    public double getPrice() {
        return decoratedProduct.getPrice();
    }
    
    @Override
    public String getCategory() {
        return decoratedProduct.getCategory();
    }
}
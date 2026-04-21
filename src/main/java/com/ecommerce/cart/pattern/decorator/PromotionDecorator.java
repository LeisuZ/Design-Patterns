package com.ecommerce.cart.pattern.decorator;

import com.ecommerce.cart.model.ProductInterface;

public class PromotionDecorator extends ProductDecorator {
    private String promotionName;
    private double promotionDiscount;
    
    public PromotionDecorator(ProductInterface decoratedProduct, String promotionName, double promotionDiscount) {
        super(decoratedProduct);
        this.promotionName = promotionName;
        this.promotionDiscount = promotionDiscount;
    }
    
    @Override
    public double getPrice() {
        return Math.max(0, decoratedProduct.getPrice() - promotionDiscount);
    }
    
    @Override
    public String getName() {
        return decoratedProduct.getName() + " (" + promotionName + ")";
    }
}
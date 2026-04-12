package com.ecommerce.cart.pattern.strategy;

import com.ecommerce.cart.model.Order;

/**
 * 策略模式-VIP折扣策略
 * 实现VIP折扣类型的促销逻辑
 */
public class VipDiscountStrategy implements PromotionStrategy {
    private double discountRate;
    
    public VipDiscountStrategy(double discountRate) {
        this.discountRate = discountRate;
    }
    
    @Override
    public double calculateDiscount(Order order) {
        double originalAmount = order.getOriginalAmount();
        return originalAmount * discountRate;
    }
}
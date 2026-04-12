package com.ecommerce.cart.pattern.strategy;

import com.ecommerce.cart.model.Order;

/**
 * 策略模式-满减策略
 * 实现满减类型的促销逻辑
 */
public class FullReductionStrategy implements PromotionStrategy {
    private double minSpend;
    private double reduction;
    
    public FullReductionStrategy(double minSpend, double reduction) {
        this.minSpend = minSpend;
        this.reduction = reduction;
    }
    
    @Override
    public double calculateDiscount(Order order) {
        double originalAmount = order.getOriginalAmount();
        if (originalAmount >= minSpend) {
            return reduction;
        }
        return 0;
    }
}
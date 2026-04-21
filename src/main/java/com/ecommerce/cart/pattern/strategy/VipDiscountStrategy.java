package com.ecommerce.cart.pattern.strategy;

import com.ecommerce.cart.model.Order;

/**
 * 策略模式-VIP折扣策略
 * 实现VIP折扣类型的促销逻辑
 */
public class VipDiscountStrategy implements PromotionStrategy {
    private double discountRate; // 折扣率
    
    /**
     * 构造VIP折扣策略
     * @param discountRate 折扣率（如0.1表示9折）
     */
    public VipDiscountStrategy(double discountRate) {
        this.discountRate = discountRate;
    }
    
    /**
     * 计算VIP折扣金额
     * @param order 订单对象
     * @return VIP折扣金额
     */
    @Override
    public double calculateDiscount(Order order) {
        if (discountRate <= 0) return 0;
        double originalAmount = order.getOriginalAmount();
        return originalAmount * (1 - discountRate);
    }
}
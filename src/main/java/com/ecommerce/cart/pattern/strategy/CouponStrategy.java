package com.ecommerce.cart.pattern.strategy;

import com.ecommerce.cart.model.Order;

/**
 * 策略模式-优惠券策略
 * 实现优惠券类型的促销逻辑
 */
public class CouponStrategy implements PromotionStrategy {
    @Override
    public double calculateDiscount(Order order) {
        if (order.getSelectedCoupon() == null) {
            return 0;
        }
        
        double originalAmount = order.getOriginalAmount();
        double minSpend = order.getSelectedCoupon().getMinSpend();
        double couponValue = order.getSelectedCoupon().getValue();
        
        if (originalAmount >= minSpend) {
            return couponValue;
        }
        return 0;
    }
}
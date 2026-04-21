package com.ecommerce.cart.pattern.strategy;

import com.ecommerce.cart.model.Order;

import java.time.LocalDateTime;

/**
 * 策略模式-限时折扣策略
 * 在特定时间段内提供折扣
 */
public class TimeLimitDiscountStrategy implements PromotionStrategy {
    private double discountRate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    public TimeLimitDiscountStrategy(double discountRate, LocalDateTime startTime, LocalDateTime endTime) {
        this.discountRate = discountRate;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    @Override
    public double calculateDiscount(Order order) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(startTime) && now.isBefore(endTime)) {
            return order.getOriginalAmount() * discountRate;
        }
        return 0;
    }
}
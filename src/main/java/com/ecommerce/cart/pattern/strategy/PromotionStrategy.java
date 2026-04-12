package com.ecommerce.cart.pattern.strategy;

import com.ecommerce.cart.model.Order;

/**
 * 策略模式-促销策略接口
 * 定义不同促销策略的统一方法
 */
public interface PromotionStrategy {
    /**
     * 策略模式-计算折扣方法
     * 不同策略实现不同的折扣计算逻辑
     * @param order 订单对象
     * @return 折扣金额
     */
    double calculateDiscount(Order order);
}
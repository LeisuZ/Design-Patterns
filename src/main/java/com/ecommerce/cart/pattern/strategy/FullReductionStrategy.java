package com.ecommerce.cart.pattern.strategy;

import com.ecommerce.cart.model.Order;

/**
 * 策略模式-满减策略
 * 实现满减类型的促销逻辑
 */
public class FullReductionStrategy implements PromotionStrategy {
    private double minSpend; // 最低消费金额
    private double reduction; // 减免金额
    
    /**
     * 构造满减策略
     * @param minSpend 最低消费金额
     * @param reduction 减免金额
     */
    public FullReductionStrategy(double minSpend, double reduction) {
        this.minSpend = minSpend;
        this.reduction = reduction;
    }
    
    /**
     * 计算满减折扣金额
     * @param order 订单对象
     * @return 满减折扣金额
     */
    @Override
    public double calculateDiscount(Order order) {
        double originalAmount = order.getOriginalAmount();
        if (originalAmount >= minSpend) {
            return reduction;
        }
        return 0;
    }
}
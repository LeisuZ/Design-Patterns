package com.ecommerce.cart.pattern.chain;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.strategy.PromotionStrategy;

/**
 * 职责链模式-VIP折扣处理器
 * 处理VIP折扣类型的促销逻辑
 */
public class VipHandler extends Handler {
    public VipHandler(PromotionStrategy strategy) {
        super(strategy);
    }
    
    @Override
    public void handle(Order order) {
        // 这里是策略调用 - 使用VIP折扣策略
        double discount = strategy.calculateDiscount(order);
        order.setVipDiscount(discount);
        
        // 传递给下一个处理器
        if (next != null) {
            next.handle(order);
        }
    }
}
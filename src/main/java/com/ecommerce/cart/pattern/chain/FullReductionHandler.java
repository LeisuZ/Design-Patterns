package com.ecommerce.cart.pattern.chain;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.strategy.PromotionStrategy;

/**
 * 职责链模式-满减处理器
 * 处理满减类型的促销逻辑
 */
public class FullReductionHandler extends Handler {
    public FullReductionHandler(PromotionStrategy strategy) {
        super(strategy);
    }
    
    @Override
    public void handle(Order order) {
        // 这里是策略调用 - 使用满减策略
        double discount = strategy.calculateDiscount(order);
        order.setFullReduction(discount);
        
        // 传递给下一个处理器
        if (next != null) {
            next.handle(order);
        }
    }
}
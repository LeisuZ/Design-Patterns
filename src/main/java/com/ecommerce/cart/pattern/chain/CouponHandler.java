package com.ecommerce.cart.pattern.chain;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.strategy.PromotionStrategy;

/**
 * 职责链模式-优惠券处理器
 * 处理优惠券类型的促销逻辑
 */
public class CouponHandler extends Handler {
    public CouponHandler(PromotionStrategy strategy) {
        super(strategy);
    }
    
    @Override
    public void handle(Order order) {
        // 这里是策略调用 - 使用优惠券策略
        double discount = strategy.calculateDiscount(order);
        order.setCouponDiscount(discount);
        
        // 传递给下一个处理器
        if (next != null) {
            next.handle(order);
        }
    }
}
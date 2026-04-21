package com.ecommerce.cart.pattern.chain;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.strategy.PromotionStrategy;
import com.ecommerce.cart.pattern.strategy.StrategyContext;

public class CouponHandler extends Handler {
    public CouponHandler(StrategyContext strategyContext) {
        super(strategyContext);
    }
    
    public CouponHandler(PromotionStrategy strategy) {
        super(strategy);
    }
    
    @Override
    public void handle(Order order) {
        double discount = strategyContext.executeStrategy(order);
        order.setCouponDiscount(discount);
        if (next != null) {
            next.handle(order);
        }
    }
}
package com.ecommerce.cart.pattern.chain;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.strategy.PromotionStrategy;
import com.ecommerce.cart.pattern.strategy.StrategyContext;

public class VipHandler extends Handler {
    public VipHandler(StrategyContext strategyContext) {
        super(strategyContext);
    }
    
    public VipHandler(PromotionStrategy strategy) {
        super(strategy);
    }
    
    @Override
    public void handle(Order order) {
        double discount = strategyContext.executeStrategy(order);
        order.setVipDiscount(discount);
        if (next != null) {
            next.handle(order);
        }
    }
}
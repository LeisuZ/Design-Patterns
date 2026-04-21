package com.ecommerce.cart.pattern.chain;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.strategy.PromotionStrategy;
import com.ecommerce.cart.pattern.strategy.StrategyContext;

public class FullReductionHandler extends Handler {
    public FullReductionHandler(StrategyContext strategyContext) {
        super(strategyContext);
    }
    
    public FullReductionHandler(PromotionStrategy strategy) {
        super(strategy);
    }
    
    @Override
    public void handle(Order order) {
        double discount = strategyContext.executeStrategy(order);
        order.setFullReduction(discount);
        if (next != null) {
            next.handle(order);
        }
    }
}
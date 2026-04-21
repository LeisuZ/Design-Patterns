package com.ecommerce.cart.pattern.chain;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.strategy.PromotionStrategy;
import com.ecommerce.cart.pattern.strategy.StrategyContext;

public class TimeLimitDiscountHandler extends Handler {
    public TimeLimitDiscountHandler(StrategyContext strategyContext) {
        super(strategyContext);
    }
    
    public TimeLimitDiscountHandler(PromotionStrategy strategy) {
        super(strategy);
    }
    
    @Override
    public void handle(Order order) {
        double discount = strategyContext.executeStrategy(order);
        order.setTimeLimitDiscount(discount);
        if (next != null) {
            next.handle(order);
        }
    }
}

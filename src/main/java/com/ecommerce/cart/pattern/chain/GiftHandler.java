package com.ecommerce.cart.pattern.chain;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.strategy.PromotionStrategy;
import com.ecommerce.cart.pattern.strategy.StrategyContext;

public class GiftHandler extends Handler {
    public GiftHandler(StrategyContext strategyContext) {
        super(strategyContext);
    }
    
    public GiftHandler(PromotionStrategy strategy) {
        super(strategy);
    }
    
    @Override
    public void handle(Order order) {
        double discount = strategyContext.executeStrategy(order);
        order.setGiftDiscount(discount);
        if (next != null) {
            next.handle(order);
        }
    }
}

package com.ecommerce.cart.pattern.strategy;

import com.ecommerce.cart.model.Order;

/**
 * 策略模式-满赠策略
 * 当订单金额达到一定阈值时赠送商品
 */
public class GiftStrategy implements PromotionStrategy {
    private double minSpend;
    private String giftName;
    private double giftPrice;
    
    public GiftStrategy(double minSpend, String giftName, double giftPrice) {
        this.minSpend = minSpend;
        this.giftName = giftName;
        this.giftPrice = giftPrice;
    }
    
    @Override
    public double calculateDiscount(Order order) {
        if (order.getOriginalAmount() >= minSpend) {
            // 满赠策略返回赠品的价值作为折扣
            return giftPrice;
        }
        return 0;
    }
    
    public String getGiftName() {
        return giftName;
    }
}
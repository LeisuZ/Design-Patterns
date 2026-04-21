package com.ecommerce.cart.pattern.strategy;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.factory.AbstractFactory;
import com.ecommerce.cart.pattern.factory.ECommerceFactory;

/**
 * 策略模式-策略上下文
 * 根据不同上下文选择合适的策略
 */
public class StrategyContext {
    private PromotionStrategy strategy;
    private AbstractFactory factory;
    
    /**
     * 构造方法，初始化工厂
     */
    public StrategyContext() {
        this.factory = new ECommerceFactory();
    }
    
    /**
     * 设置策略
     * @param strategy 促销策略
     */
    public void setStrategy(PromotionStrategy strategy) {
        this.strategy = strategy;
    }
    
    /**
     * 根据类型选择策略
     * @param type 策略类型
     * @param params 策略参数
     */
    public void selectStrategy(String type, Object... params) {
        this.strategy = factory.createStrategy(type, params);
    }
    
    /**
     * 执行策略
     * @param order 订单对象
     * @return 折扣金额
     */
    public double executeStrategy(Order order) {
        if (strategy != null) {
            return strategy.calculateDiscount(order);
        }
        return 0;
    }
}

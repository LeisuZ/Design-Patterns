package com.ecommerce.cart.pattern.chain;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.strategy.PromotionStrategy;

/**
 * 职责链模式抽象处理器
 * 用于处理订单的促销逻辑，通过setNext方法串联多个处理器
 */
public abstract class Handler {
    protected Handler next;
    protected PromotionStrategy strategy;
    
    public Handler(PromotionStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void setNext(Handler next) {
        this.next = next;
    }
    
    /**
     * 职责链节点处理方法
     * 处理订单的促销逻辑
     * @param order 订单对象
     */
    public abstract void handle(Order order);
}
package com.ecommerce.cart.pattern.chain;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.command.OrderCommand;
import com.ecommerce.cart.pattern.strategy.PromotionStrategy;
import com.ecommerce.cart.pattern.strategy.StrategyContext;

/**
 * 职责链模式抽象处理器
 * 用于处理订单的促销逻辑，通过setNext方法串联多个处理器
 */
public abstract class Handler {
    protected Handler next;
    protected StrategyContext strategyContext;
    
    /**
     * 构造处理器（使用StrategyContext）
     * @param strategyContext 策略上下文
     */
    public Handler(StrategyContext strategyContext) {
        this.strategyContext = strategyContext;
    }
    
    /**
     * 构造处理器（使用PromotionStrategy，向后兼容）
     * @param strategy 促销策略
     */
    public Handler(PromotionStrategy strategy) {
        this.strategyContext = new StrategyContext();
        this.strategyContext.setStrategy(strategy);
    }
    
    /**
     * 设置下一个处理器
     * @param next 下一个处理器
     */
    public void setNext(Handler next) {
        this.next = next;
    }
    
    /**
     * 职责链节点处理方法
     * 处理订单的促销逻辑
     * @param order 订单对象
     */
    public abstract void handle(Order order);
    
    /**
     * 处理命令对象
     * @param command 订单命令对象
     */
    public void handleCommand(OrderCommand command) {
        handle(command.getOrder());
    }
}
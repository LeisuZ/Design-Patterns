package com.ecommerce.cart.pattern.state;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.singleton.Logger;

/**
 * 状态模式-已完成状态
 * 实现已完成状态的行为
 */
public class CompletedState implements OrderState {
    private transient Logger logger;
    
    public CompletedState() {
        this.logger = Logger.INSTANCE;
    }
    
    @Override
    public void pay(Order order) {
        logger.log("订单已完成，无需支付: " + order.getOrderId());
    }
    
    @Override
    public void ship(Order order) {
        logger.log("订单已完成，无需发货: " + order.getOrderId());
    }
    
    @Override
    public void complete(Order order) {
        logger.log("订单已完成，无需重复操作: " + order.getOrderId());
    }
    
    @Override
    public void cancel(Order order) {
        logger.log("订单已完成，无法取消: " + order.getOrderId());
    }
    
    @Override
    public String getStateName() {
        return "已完成";
    }
    
    // 反序列化时初始化Logger
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.logger = Logger.INSTANCE;
    }
}
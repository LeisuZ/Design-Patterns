package com.ecommerce.cart.pattern.state;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.singleton.Logger;

/**
 * 状态模式-已发货状态
 * 实现已发货状态的行为
 */
public class ShippedState implements OrderState {
    private transient Logger logger;
    
    public ShippedState() {
        this.logger = Logger.INSTANCE;
    }
    
    @Override
    public void pay(Order order) {
        logger.log("订单已支付，无需重复支付: " + order.getOrderId());
    }
    
    @Override
    public void ship(Order order) {
        logger.log("订单已发货，无需重复发货: " + order.getOrderId());
    }
    
    @Override
    public void complete(Order order) {
        logger.log("订单完成: " + order.getOrderId());
        order.setState(OrderStateMachine.getInstance().getCompletedState());
    }
    
    @Override
    public void cancel(Order order) {
        logger.log("订单已发货，取消订单: " + order.getOrderId());
        order.setState(OrderStateMachine.getInstance().getCancelledState());
    }
    
    @Override
    public String getStateName() {
        return "已发货";
    }
    
    // 反序列化时初始化Logger
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.logger = Logger.INSTANCE;
    }
}
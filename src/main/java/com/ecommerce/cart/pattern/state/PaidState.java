package com.ecommerce.cart.pattern.state;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.singleton.Logger;

/**
 * 状态模式-已支付状态
 * 实现已支付状态的行为
 */
public class PaidState implements OrderState {
    private transient Logger logger;
    
    public PaidState() {
        this.logger = Logger.getInstance();
    }
    
    @Override
    public void pay(Order order) {
        logger.log("订单已支付，无需重复支付: " + order.getOrderId());
    }
    
    @Override
    public void ship(Order order) {
        logger.log("订单发货: " + order.getOrderId());
        order.setState(new ShippedState());
    }
    
    @Override
    public void complete(Order order) {
        logger.log("订单未发货，无法完成: " + order.getOrderId());
    }
    
    @Override
    public void cancel(Order order) {
        logger.log("订单已支付，取消订单: " + order.getOrderId());
        order.setState(new CancelledState());
    }
    
    @Override
    public String getStateName() {
        return "已支付";
    }
    
    // 反序列化时初始化Logger
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.logger = Logger.getInstance();
    }
}
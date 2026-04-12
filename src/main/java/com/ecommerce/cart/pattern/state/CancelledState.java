package com.ecommerce.cart.pattern.state;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.singleton.Logger;

/**
 * 状态模式-已取消状态
 * 实现已取消状态的行为
 */
public class CancelledState implements OrderState {
    private transient Logger logger;
    
    public CancelledState() {
        this.logger = Logger.getInstance();
    }
    
    @Override
    public void pay(Order order) {
        logger.log("订单已取消，无法支付: " + order.getOrderId());
    }
    
    @Override
    public void ship(Order order) {
        logger.log("订单已取消，无法发货: " + order.getOrderId());
    }
    
    @Override
    public void complete(Order order) {
        logger.log("订单已取消，无法完成: " + order.getOrderId());
    }
    
    @Override
    public void cancel(Order order) {
        logger.log("订单已取消，无需重复操作: " + order.getOrderId());
    }
    
    @Override
    public String getStateName() {
        return "已取消";
    }
    
    // 反序列化时初始化Logger
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.logger = Logger.getInstance();
    }
}
package com.ecommerce.cart.pattern.state;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.singleton.Logger;

/**
 * 状态模式-待支付状态
 * 实现待支付状态的行为
 */
public class PendingPaymentState implements OrderState {
    private transient Logger logger;
    
    public PendingPaymentState() {
        this.logger = Logger.INSTANCE;
    }
    
    @Override
    public void pay(Order order) {
        logger.log("订单支付成功: " + order.getOrderId());
        order.setState(OrderStateMachine.getInstance().getPaidState());
    }
    
    @Override
    public void ship(Order order) {
        logger.log("订单未支付，无法发货: " + order.getOrderId());
    }
    
    @Override
    public void complete(Order order) {
        logger.log("订单未支付，无法完成: " + order.getOrderId());
    }
    
    @Override
    public void cancel(Order order) {
        logger.log("订单取消: " + order.getOrderId());
        order.setState(OrderStateMachine.getInstance().getCancelledState());
    }
    
    @Override
    public String getStateName() {
        return "待支付";
    }
    
    // 反序列化时初始化Logger
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.logger = Logger.INSTANCE;
    }
}
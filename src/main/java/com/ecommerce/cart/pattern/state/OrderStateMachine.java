package com.ecommerce.cart.pattern.state;

import com.ecommerce.cart.model.Order;

/**
 * 状态模式-订单状态机
 * 用于管理订单状态转换
 */
public class OrderStateMachine {
    private static final OrderStateMachine instance = new OrderStateMachine(); // 单例模式
    
    // 各种状态实例
    private final OrderState pendingPaymentState = new PendingPaymentState();
    private final OrderState paidState = new PaidState();
    private final OrderState shippedState = new ShippedState();
    private final OrderState completedState = new CompletedState();
    private final OrderState cancelledState = new CancelledState();
    
    /**
     * 私有构造方法，防止外部实例化
     */
    private OrderStateMachine() {
    }
    
    /**
     * 获取状态机实例
     * @return 状态机实例
     */
    public static OrderStateMachine getInstance() {
        return instance;
    }
    
    /**
     * 初始化订单状态
     * @param order 订单对象
     */
    public void initializeState(Order order) {
        order.setState(pendingPaymentState);
    }
    
    /**
     * 处理支付
     * @param order 订单对象
     */
    public void processPayment(Order order) {
        order.getState().pay(order);
    }
    
    /**
     * 处理发货
     * @param order 订单对象
     */
    public void processShipment(Order order) {
        order.getState().ship(order);
    }
    
    /**
     * 处理完成
     * @param order 订单对象
     */
    public void processCompletion(Order order) {
        order.getState().complete(order);
    }
    
    /**
     * 处理取消
     * @param order 订单对象
     */
    public void processCancellation(Order order) {
        order.getState().cancel(order);
    }
    
    /**
     * 获取待支付状态
     * @return 待支付状态
     */
    public OrderState getPendingPaymentState() {
        return pendingPaymentState;
    }
    
    /**
     * 获取已支付状态
     * @return 已支付状态
     */
    public OrderState getPaidState() {
        return paidState;
    }
    
    /**
     * 获取已发货状态
     * @return 已发货状态
     */
    public OrderState getShippedState() {
        return shippedState;
    }
    
    /**
     * 获取已完成状态
     * @return 已完成状态
     */
    public OrderState getCompletedState() {
        return completedState;
    }
    
    /**
     * 获取已取消状态
     * @return 已取消状态
     */
    public OrderState getCancelledState() {
        return cancelledState;
    }
}

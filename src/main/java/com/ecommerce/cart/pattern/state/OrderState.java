package com.ecommerce.cart.pattern.state;

import com.ecommerce.cart.model.Order;

/**
 * 状态模式-订单状态接口
 * 定义订单状态的行为
 */
public interface OrderState extends java.io.Serializable {
    /**
     * 支付订单
     * @param order 订单对象
     */
    void pay(Order order);
    
    /**
     * 发货
     * @param order 订单对象
     */
    void ship(Order order);
    
    /**
     * 完成订单
     * @param order 订单对象
     */
    void complete(Order order);
    
    /**
     * 取消订单
     * @param order 订单对象
     */
    void cancel(Order order);
    
    /**
     * 获取状态名称
     * @return 状态名称
     */
    String getStateName();
}
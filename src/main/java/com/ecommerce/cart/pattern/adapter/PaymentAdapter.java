package com.ecommerce.cart.pattern.adapter;

import com.ecommerce.cart.model.Order;

/**
 * 适配器模式-支付适配器接口
 * 定义统一的支付接口
 */
public interface PaymentAdapter {
    /**
     * 支付方法
     * @param order 订单对象
     * @return 支付是否成功
     */
    boolean pay(Order order);
    
    /**
     * 获取支付方式名称
     * @return 支付方式名称
     */
    String getPaymentMethod();
    
    /**
     * 退款方法
     * @param order 订单对象
     * @return 退款是否成功
     */
    boolean refund(Order order);
}
package com.ecommerce.cart.pattern.template;

import com.ecommerce.cart.model.Order;

/**
 * 模板方法模式-订单处理模板
 * 定义订单处理的算法骨架，具体步骤由子类实现
 */
public abstract class OrderProcessTemplate {
    /**
     * 模板方法-订单处理流程
     * 定义订单处理的算法骨架
     */
    public final void processOrder(Order order) {
        validateOrder(order);
        calculatePrice(order);
        processPayment(order);
        updateInventory(order);
        generateOrder(order);
        sendNotification(order);
    }
    
    /**
     * 验证订单
     * @param order 订单对象
     */
    protected abstract void validateOrder(Order order);
    
    /**
     * 计算价格
     * @param order 订单对象
     */
    protected abstract void calculatePrice(Order order);
    
    /**
     * 处理支付
     * @param order 订单对象
     */
    protected abstract void processPayment(Order order);
    
    /**
     * 更新库存
     * @param order 订单对象
     */
    protected abstract void updateInventory(Order order);
    
    /**
     * 生成订单
     * @param order 订单对象
     */
    protected abstract void generateOrder(Order order);
    
    /**
     * 发送通知
     * @param order 订单对象
     */
    protected abstract void sendNotification(Order order);
}
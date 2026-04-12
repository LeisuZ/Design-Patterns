package com.ecommerce.cart.pattern.template;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.singleton.Logger;

/**
 * 模板方法模式-普通订单处理器
 * 实现普通订单的处理逻辑
 */
public class NormalOrderProcessor extends OrderProcessTemplate {
    private Logger logger;
    
    public NormalOrderProcessor() {
        this.logger = Logger.getInstance();
    }
    
    @Override
    protected void validateOrder(Order order) {
        logger.log("验证普通订单: " + order.getOrderId());
        // 模拟订单验证逻辑
        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("订单不能为空");
        }
    }
    
    @Override
    protected void calculatePrice(Order order) {
        logger.log("计算普通订单价格: " + order.getOrderId());
        // 价格计算已在订单创建时完成
    }
    
    @Override
    protected void processPayment(Order order) {
        logger.log("处理普通订单支付: " + order.getOrderId());
        // 模拟支付处理
    }
    
    @Override
    protected void updateInventory(Order order) {
        logger.log("更新普通订单库存: " + order.getOrderId());
        // 模拟库存更新
    }
    
    @Override
    protected void generateOrder(Order order) {
        logger.log("生成普通订单: " + order.getOrderId());
        // 订单已在OrderService中生成
    }
    
    @Override
    protected void sendNotification(Order order) {
        logger.log("发送普通订单通知: " + order.getOrderId());
        // 模拟通知发送
    }
}
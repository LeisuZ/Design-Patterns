package com.ecommerce.cart.pattern.template;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.singleton.Logger;

/**
 * 模板方法模式-VIP订单处理器
 * 实现VIP订单的处理逻辑
 */
public class VipOrderProcessor extends OrderProcessTemplate {
    private Logger logger;
    
    public VipOrderProcessor() {
        this.logger = Logger.getInstance();
    }
    
    @Override
    protected void validateOrder(Order order) {
        logger.log("验证VIP订单: " + order.getOrderId());
        // 模拟VIP订单验证逻辑
        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("VIP订单不能为空");
        }
    }
    
    @Override
    protected void calculatePrice(Order order) {
        logger.log("计算VIP订单价格: " + order.getOrderId());
        // VIP订单可能有额外的折扣
    }
    
    @Override
    protected void processPayment(Order order) {
        logger.log("处理VIP订单支付: " + order.getOrderId());
        // VIP可能有专属支付方式
    }
    
    @Override
    protected void updateInventory(Order order) {
        logger.log("更新VIP订单库存: " + order.getOrderId());
        // VIP订单可能有优先库存
    }
    
    @Override
    protected void generateOrder(Order order) {
        logger.log("生成VIP订单: " + order.getOrderId());
        // VIP订单可能有特殊标记
    }
    
    @Override
    protected void sendNotification(Order order) {
        logger.log("发送VIP订单通知: " + order.getOrderId());
        // VIP可能有专属通知渠道
    }
}
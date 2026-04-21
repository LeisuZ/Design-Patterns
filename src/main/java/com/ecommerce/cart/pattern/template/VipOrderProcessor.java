package com.ecommerce.cart.pattern.template;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.chain.Handler;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.pattern.singleton.Logger;
import com.ecommerce.cart.pattern.strategy.StrategyContext;

public class VipOrderProcessor extends OrderProcessTemplate {
    
    public VipOrderProcessor() {
        super();
    }
    
    @Override
    protected void validateOrder(Order order) {
        logger.log("验证VIP订单: " + order.getOrderId() + " - VIP专属快速验证通道");
        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("VIP订单不能为空");
        }
    }
    
    @Override
    protected void calculatePrice(Order order) {
        logger.log("计算VIP订单价格: " + order.getOrderId() + " - VIP专属折扣计算");
        Handler chain = buildDiscountChain();
        if (chain != null) {
            chain.handle(order);
        }
        double extraDiscount = order.getOriginalAmount() * 0.05;
        order.setVipDiscount(order.getVipDiscount() + extraDiscount);
        logger.log("VIP额外5%折扣: -¥" + String.format("%.2f", extraDiscount));
    }
    
    @Override
    protected void processPayment(Order order) {
        logger.log("处理VIP订单支付: " + order.getOrderId() + " - VIP免密支付");
    }
    
    @Override
    protected void updateInventory(Order order) {
        logger.log("更新VIP订单库存: " + order.getOrderId() + " - VIP优先发货");
    }
    
    @Override
    protected void generateOrder(Order order) {
        logger.log("生成VIP订单: " + order.getOrderId() + " - VIP专属订单标识");
    }
    
    @Override
    protected void sendNotification(Order order) {
        logger.log("发送VIP订单通知: " + order.getOrderId() + " - 短信+邮件双重通知");
    }
    
    @Override
    protected boolean needSendNotification() {
        return true;
    }
}

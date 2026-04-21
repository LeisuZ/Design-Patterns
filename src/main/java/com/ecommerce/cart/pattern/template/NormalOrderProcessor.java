package com.ecommerce.cart.pattern.template;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.chain.Handler;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.pattern.singleton.Logger;

public class NormalOrderProcessor extends OrderProcessTemplate {
    
    public NormalOrderProcessor() {
        super();
    }
    
    @Override
    protected void validateOrder(Order order) {
        logger.log("验证普通订单: " + order.getOrderId() + " - 标准验证流程");
        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("订单不能为空");
        }
    }
    
    @Override
    protected void calculatePrice(Order order) {
        logger.log("计算普通订单价格: " + order.getOrderId() + " - 标准折扣计算");
        Handler chain = buildDiscountChain();
        if (chain != null) {
            chain.handle(order);
        }
    }
    
    @Override
    protected void processPayment(Order order) {
        logger.log("处理普通订单支付: " + order.getOrderId() + " - 标准支付流程");
    }
    
    @Override
    protected void updateInventory(Order order) {
        logger.log("更新普通订单库存: " + order.getOrderId() + " - 标准库存扣减");
    }
    
    @Override
    protected void generateOrder(Order order) {
        logger.log("生成普通订单: " + order.getOrderId());
    }
    
    @Override
    protected void sendNotification(Order order) {
        logger.log("发送普通订单通知: " + order.getOrderId() + " - 邮件通知");
    }
    
    @Override
    protected boolean needSendNotification() {
        return false;
    }
}

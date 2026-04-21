package com.ecommerce.cart.pattern.adapter;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.singleton.Logger;

/**
 * 适配器模式-微信支付适配器
 * 适配微信支付方式
 */
public class WechatPayAdapter extends AbstractPaymentAdapter {
    private Logger logger;
    
    public WechatPayAdapter() {
        this.logger = Logger.INSTANCE;
    }
    
    @Override
    public boolean pay(Order order) {
        logger.log("使用微信支付订单: " + order.getOrderId() + "，金额: " + order.getFinalAmount());
        // 模拟微信支付流程
        try {
            // 这里可以添加实际的微信支付API调用
            Thread.sleep(800); // 模拟网络请求
            logger.log("微信支付成功: " + order.getOrderId());
            return true;
        } catch (Exception e) {
            logger.log("微信支付失败: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getPaymentMethod() {
        return "微信支付";
    }
    
    @Override
    public boolean refund(Order order) {
        logger.log("微信支付退款: " + order.getOrderId() + "，金额: " + order.getFinalAmount());
        // 模拟退款流程
        try {
            Thread.sleep(400);
            logger.log("微信支付退款成功: " + order.getOrderId());
            return true;
        } catch (Exception e) {
            logger.log("微信支付退款失败: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getPaymentDescription() {
        return "使用微信支付进行支付，支持微信余额、银行卡等多种支付方式";
    }
    
    @Override
    public double getDiscount() {
        // 微信支付无折扣
        return 0.0;
    }
}
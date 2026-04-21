package com.ecommerce.cart.pattern.adapter;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.singleton.Logger;

/**
 * 适配器模式-支付宝支付适配器
 * 适配支付宝支付方式
 */
public class AlipayAdapter extends AbstractPaymentAdapter {
    private Logger logger;
    
    public AlipayAdapter() {
        this.logger = Logger.INSTANCE;
    }
    
    @Override
    public boolean pay(Order order) {
        logger.log("使用支付宝支付订单: " + order.getOrderId() + "，金额: " + order.getFinalAmount());
        // 模拟支付宝支付流程
        try {
            // 这里可以添加实际的支付宝支付API调用
            Thread.sleep(1000); // 模拟网络请求
            logger.log("支付宝支付成功: " + order.getOrderId());
            return true;
        } catch (Exception e) {
            logger.log("支付宝支付失败: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getPaymentMethod() {
        return "支付宝";
    }
    
    @Override
    public boolean refund(Order order) {
        logger.log("支付宝退款: " + order.getOrderId() + "，金额: " + order.getFinalAmount());
        // 模拟退款流程
        try {
            Thread.sleep(500);
            logger.log("支付宝退款成功: " + order.getOrderId());
            return true;
        } catch (Exception e) {
            logger.log("支付宝退款失败: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getPaymentDescription() {
        return "使用支付宝进行支付，支持余额、银行卡、花呗等多种支付方式";
    }
    
    @Override
    public double getDiscount() {
        // 支付宝支付无折扣
        return 0.0;
    }
}
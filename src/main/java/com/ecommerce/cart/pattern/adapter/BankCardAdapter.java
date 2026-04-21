package com.ecommerce.cart.pattern.adapter;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.singleton.Logger;

public class BankCardAdapter extends AbstractPaymentAdapter {
    private Logger logger;
    
    public BankCardAdapter() {
        this.logger = Logger.INSTANCE;
    }
    
    @Override
    public boolean pay(Order order) {
        // 模拟银行卡支付过程
        logger.log("银行卡支付处理中...");
        logger.log("验证银行卡信息...");
        logger.log("处理支付请求...");
        logger.log("支付成功！");
        return true;
    }
    
    @Override
    public boolean refund(Order order) {
        // 模拟银行卡退款过程
        logger.log("银行卡退款处理中...");
        logger.log("处理退款请求...");
        logger.log("退款成功！");
        return true;
    }
    
    @Override
    public String getPaymentMethod() {
        return "银行卡支付";
    }
    
    @Override
    public String getPaymentDescription() {
        return "使用银行卡进行支付，支持所有主流银行的借记卡和信用卡";
    }
    
    @Override
    public double getDiscount() {
        // 银行卡支付无折扣
        return 0.0;
    }
}
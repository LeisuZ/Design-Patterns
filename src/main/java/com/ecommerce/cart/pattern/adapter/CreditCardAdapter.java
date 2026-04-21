package com.ecommerce.cart.pattern.adapter;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.singleton.Logger;

/**
 * 适配器模式-信用卡支付适配器
 * 适配信用卡支付方式
 */
public class CreditCardAdapter extends AbstractPaymentAdapter {
    private Logger logger;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    
    public CreditCardAdapter(String cardNumber, String expiryDate, String cvv) {
        this.logger = Logger.INSTANCE;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }
    
    @Override
    public boolean pay(Order order) {
        logger.log("使用信用卡支付订单: " + order.getOrderId() + "，金额: " + order.getFinalAmount());
        String maskedCardNumber = "****" + (cardNumber != null && cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : "****");
        logger.log("卡号: " + maskedCardNumber + "，有效期: " + expiryDate);
        // 模拟信用卡支付流程
        try {
            // 验证卡号、有效期和CVV
            if (cardNumber == null || cardNumber.isEmpty() || expiryDate == null || expiryDate.isEmpty() || cvv == null || cvv.isEmpty()) {
                throw new Exception("信用卡信息不完整");
            }
            // 这里可以添加实际的信用卡支付API调用
            Thread.sleep(1200); // 模拟网络请求
            logger.log("信用卡支付成功: " + order.getOrderId());
            return true;
        } catch (Exception e) {
            logger.log("信用卡支付失败: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getPaymentMethod() {
        return "信用卡";
    }
    
    @Override
    public boolean refund(Order order) {
        logger.log("信用卡退款: " + order.getOrderId() + "，金额: " + order.getFinalAmount());
        // 模拟退款流程
        try {
            Thread.sleep(600);
            logger.log("信用卡退款成功: " + order.getOrderId());
            return true;
        } catch (Exception e) {
            logger.log("信用卡退款失败: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getPaymentDescription() {
        return "使用信用卡进行支付，支持Visa、MasterCard、American Express等国际信用卡";
    }
    
    @Override
    public double getDiscount() {
        // 信用卡支付无折扣
        return 0.0;
    }
}
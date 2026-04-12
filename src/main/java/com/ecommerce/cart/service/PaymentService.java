package com.ecommerce.cart.service;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.adapter.AlipayAdapter;
import com.ecommerce.cart.pattern.adapter.PaymentAdapter;
import com.ecommerce.cart.pattern.adapter.WechatPayAdapter;
import com.ecommerce.cart.pattern.singleton.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付服务
 * 管理不同的支付方式
 */
public class PaymentService {
    private Map<String, PaymentAdapter> paymentAdapters;
    private Logger logger;
    
    public PaymentService() {
        this.logger = Logger.getInstance();
        this.paymentAdapters = new HashMap<>();
        initializePaymentAdapters();
    }
    
    private void initializePaymentAdapters() {
        // 初始化默认支付方式
        paymentAdapters.put("alipay", new AlipayAdapter());
        paymentAdapters.put("wechat", new WechatPayAdapter());
        // 信用卡支付需要卡号等信息，这里暂时不初始化
    }
    
    /**
     * 添加支付方式
     * @param type 支付方式类型
     * @param adapter 支付适配器
     */
    public void addPaymentAdapter(String type, PaymentAdapter adapter) {
        paymentAdapters.put(type, adapter);
        logger.log("添加支付方式: " + adapter.getPaymentMethod());
    }
    
    /**
     * 执行支付
     * @param order 订单对象
     * @param paymentType 支付方式类型
     * @return 支付是否成功
     */
    public boolean processPayment(Order order, String paymentType) {
        PaymentAdapter adapter = paymentAdapters.get(paymentType);
        if (adapter == null) {
            logger.log("不支持的支付方式: " + paymentType);
            return false;
        }
        
        logger.log("开始支付，订单: " + order.getOrderId() + "，支付方式: " + adapter.getPaymentMethod());
        boolean success = adapter.pay(order);
        
        if (success) {
            order.pay(); // 更新订单状态
            logger.log("支付成功，订单状态已更新");
        } else {
            logger.log("支付失败");
        }
        
        return success;
    }
    
    /**
     * 执行退款
     * @param order 订单对象
     * @param paymentType 支付方式类型
     * @return 退款是否成功
     */
    public boolean processRefund(Order order, String paymentType) {
        PaymentAdapter adapter = paymentAdapters.get(paymentType);
        if (adapter == null) {
            logger.log("不支持的支付方式: " + paymentType);
            return false;
        }
        
        logger.log("开始退款，订单: " + order.getOrderId() + "，支付方式: " + adapter.getPaymentMethod());
        boolean success = adapter.refund(order);
        
        if (success) {
            logger.log("退款成功");
        } else {
            logger.log("退款失败");
        }
        
        return success;
    }
    
    /**
     * 获取所有可用的支付方式
     * @return 支付方式名称列表
     */
    public String[] getAvailablePaymentMethods() {
        return paymentAdapters.values().stream()
                .map(PaymentAdapter::getPaymentMethod)
                .toArray(String[]::new);
    }
    
    /**
     * 获取支付方式类型
     * @param paymentMethod 支付方式名称
     * @return 支付方式类型
     */
    public String getPaymentType(String paymentMethod) {
        for (Map.Entry<String, PaymentAdapter> entry : paymentAdapters.entrySet()) {
            if (entry.getValue().getPaymentMethod().equals(paymentMethod)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
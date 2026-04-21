package com.ecommerce.cart.pattern.adapter;

import com.ecommerce.cart.model.Order;

/**
 * 适配器模式-默认支付适配器
 * 为PaymentAdapter接口提供默认实现，减少实现接口的工作量
 */
public abstract class AbstractPaymentAdapter implements PaymentAdapter {
    @Override
    public boolean pay(Order order) {
        // 默认实现，子类可以重写
        return false;
    }
    
    @Override
    public String getPaymentMethod() {
        // 默认实现，子类可以重写
        return "默认支付方式";
    }
    
    @Override
    public boolean refund(Order order) {
        // 默认实现，子类可以重写
        return false;
    }
    
    @Override
    public String getPaymentDescription() {
        // 默认实现，子类可以重写
        return "默认支付方式描述";
    }
    
    @Override
    public double getDiscount() {
        // 默认实现，子类可以重写
        return 0.0;
    }
}

package com.ecommerce.cart.model;

import com.ecommerce.cart.pattern.state.OrderState;
import com.ecommerce.cart.pattern.state.PendingPaymentState;
import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private List<CartItem> items;
    private double originalAmount;
    private double couponDiscount;
    private double fullReduction;
    private double vipDiscount;
    private double finalAmount;
    private Coupon selectedCoupon;
    private OrderState state;
    
    public Order(Cart cart) {
        this.items = cart.getItems();
        this.originalAmount = cart.getTotal();
        this.couponDiscount = 0;
        this.fullReduction = 0;
        this.vipDiscount = 0;
        this.finalAmount = originalAmount;
        this.state = new PendingPaymentState(); // 初始状态为待支付
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public double getOriginalAmount() {
        return originalAmount;
    }
    
    public double getCouponDiscount() {
        return couponDiscount;
    }
    
    public void setCouponDiscount(double couponDiscount) {
        this.couponDiscount = couponDiscount;
    }
    
    public double getFullReduction() {
        return fullReduction;
    }
    
    public void setFullReduction(double fullReduction) {
        this.fullReduction = fullReduction;
    }
    
    public double getVipDiscount() {
        return vipDiscount;
    }
    
    public void setVipDiscount(double vipDiscount) {
        this.vipDiscount = vipDiscount;
    }
    
    public Coupon getSelectedCoupon() {
        return selectedCoupon;
    }
    
    public void setSelectedCoupon(Coupon selectedCoupon) {
        this.selectedCoupon = selectedCoupon;
    }
    
    // 状态相关方法
    public OrderState getState() {
        return state;
    }
    
    public void setState(OrderState state) {
        this.state = state;
    }
    
    public String getStateName() {
        return state.getStateName();
    }
    
    // 委托给状态对象的方法
    public void pay() {
        state.pay(this);
    }
    
    public void ship() {
        state.ship(this);
    }
    
    public void complete() {
        state.complete(this);
    }
    
    public void cancel() {
        state.cancel(this);
    }
    
    public double getFinalAmount() {
        double totalDiscount = couponDiscount + fullReduction + vipDiscount;
        finalAmount = Math.max(0, originalAmount - totalDiscount);
        return finalAmount;
    }
}
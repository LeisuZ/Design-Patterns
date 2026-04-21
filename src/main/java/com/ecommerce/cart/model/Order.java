package com.ecommerce.cart.model;

import com.ecommerce.cart.pattern.observer.EventBus;
import com.ecommerce.cart.pattern.observer.Observer;
import com.ecommerce.cart.pattern.observer.Subject;
import com.ecommerce.cart.pattern.state.OrderState;
import com.ecommerce.cart.pattern.state.OrderStateMachine;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable, Subject {
    private static final long serialVersionUID = 1L;
    private String orderId;
    private List<CartItem> items;
    private double originalAmount;
    private double couponDiscount;
    private double fullReduction;
    private double vipDiscount;
    private double giftDiscount;
    private double timeLimitDiscount;
    private double finalAmount;
    private Coupon selectedCoupon;
    private OrderState state;
    private String memberId;
    private String memberName;
    private Address address;
    private String paymentType;
    private String previousStatus;
    private boolean priceCalculated;
    private java.util.Date createTime;
    
    public Order(Cart cart) {
        this.orderId = "ORD-" + new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        this.createTime = new java.util.Date();
        this.items = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            this.items.add(item.clone());
        }
        this.originalAmount = cart.getTotal();
        this.couponDiscount = 0;
        this.fullReduction = 0;
        this.vipDiscount = 0;
        this.giftDiscount = 0;
        this.timeLimitDiscount = 0;
        this.finalAmount = originalAmount;
        OrderStateMachine.getInstance().initializeState(this);
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
    
    public double getGiftDiscount() {
        return giftDiscount;
    }
    
    public void setGiftDiscount(double giftDiscount) {
        this.giftDiscount = giftDiscount;
    }
    
    public double getTimeLimitDiscount() {
        return timeLimitDiscount;
    }
    
    public void setTimeLimitDiscount(double timeLimitDiscount) {
        this.timeLimitDiscount = timeLimitDiscount;
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
        if (this.state != null) {
            this.previousStatus = this.state.getStateName();
        }
        this.state = state;
        notifyObservers("ORDER_STATUS_CHANGED", state.getStateName());
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
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public String getMemberName() {
        return memberName;
    }
    
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }
    
    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public boolean isPriceCalculated() {
        return priceCalculated;
    }

    public void setPriceCalculated(boolean priceCalculated) {
        this.priceCalculated = priceCalculated;
    }

    public double getFinalAmount() {
        double totalDiscount = couponDiscount + fullReduction + vipDiscount + giftDiscount + timeLimitDiscount;
        finalAmount = Math.max(0, originalAmount - totalDiscount);
        return finalAmount;
    }

    @Override
    public void registerObserver(Observer observer) {
        EventBus.getInstance().registerObserver("ORDER_STATUS_CHANGED", observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        EventBus.getInstance().removeObserver("ORDER_STATUS_CHANGED", observer);
    }

    @Override
    public void notifyObservers(String eventType, Object data) {
        EventBus.getInstance().publishEvent(eventType, data);
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
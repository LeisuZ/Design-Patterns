package com.ecommerce.cart.pattern.facade;

import com.ecommerce.cart.model.*;
import com.ecommerce.cart.service.*;
import com.ecommerce.cart.pattern.singleton.Logger;

/**
 * 外观模式-电商系统外观
 * 为客户端提供统一的系统服务入口
 */
public enum ECommerceFacade {
    INSTANCE; // 唯一实例
    
    private CartService cartService;
    private OrderService orderService;
    private PaymentService paymentService;
    private AddressService addressService;
    private PromotionService promotionService;
    private MemberService memberService;
    private LogisticsService logisticsService;
    private ReviewService reviewService;
    private ProductService productService;
    private Logger logger;
    
    /**
     * 枚举构造方法，防止外部实例化
     */
    private ECommerceFacade() {
        this.logger = Logger.INSTANCE;
        this.cartService = new CartService();
        this.orderService = new OrderService();
        this.paymentService = new PaymentService();
        this.addressService = new AddressService();
        this.promotionService = new PromotionService();
        this.memberService = new MemberService();
        this.logisticsService = new LogisticsService();
        this.reviewService = new ReviewService();
        this.productService = new ProductService();
        logger.log("初始化电商系统外观");
    }
    
    // 购物车相关方法
    public void addProduct(String type, String id, String name, double price, int quantity) {
        cartService.addProduct(type, id, name, price, quantity);
    }
    
    public void removeProduct(String productId) {
        cartService.removeProduct(productId);
    }
    
    public void updateQuantity(String productId, int quantity) {
        cartService.updateQuantity(productId, quantity);
    }
    
    public Cart getCart() {
        return cartService.getCart();
    }
    
    public double getCartTotal() {
        return cartService.getTotal();
    }
    
    // 订单相关方法
    public Order createOrder() {
        return orderService.createOrder(cartService.getCart());
    }
    
    public Order createOrder(Coupon coupon) {
        return orderService.createOrder(cartService.getCart(), coupon);
    }
    
    public void submitOrder(Order order) {
        orderService.submitOrder(order);
    }
    
    public Order getOrderById(String orderId) {
        return orderService.getOrderById(orderId);
    }
    
    public java.util.List<Order> getOrders() {
        return orderService.getOrders();
    }
    
    // 支付相关方法
    public boolean processPayment(Order order, String paymentType) {
        return paymentService.processPayment(order, paymentType);
    }
    
    public boolean processRefund(Order order, String paymentType) {
        return paymentService.processRefund(order, paymentType);
    }
    
    public String[] getAvailablePaymentMethods() {
        return paymentService.getAvailablePaymentMethods();
    }
    
    // 地址相关方法
    public void addAddress(String name, String phone, String province, String city, String district, String detailAddress, boolean isDefault) {
        addressService.addAddress(name, phone, province, city, district, detailAddress, isDefault);
    }
    
    public void updateAddress(String id, String name, String phone, String province, String city, String district, String detailAddress, boolean isDefault) {
        addressService.updateAddress(id, name, phone, province, city, district, detailAddress, isDefault);
    }
    
    public void deleteAddress(String id) {
        addressService.deleteAddress(id);
    }
    
    public java.util.List<Address> getAddresses() {
        return addressService.getAddresses();
    }
    
    public Address getDefaultAddress() {
        return addressService.getDefaultAddress();
    }
    
    // 促销相关方法
    public void addCoupon(String id, String name, double value, double minSpend) {
        promotionService.addCoupon(new com.ecommerce.cart.model.Coupon(id, name, value, minSpend));
    }
    
    public void addFullReductionRule(double minSpend, double reduction) {
        promotionService.addPromotionRule(new com.ecommerce.cart.model.PromotionRule(
            "R" + System.currentTimeMillis(), 
            "满" + minSpend + "减" + reduction, 
            "FULL_REDUCTION", 
            reduction, 
            minSpend, 
            1
        ));
    }
    
    public void setVipDiscountRate(double rate) {
        promotionService.addPromotionRule(new com.ecommerce.cart.model.PromotionRule(
            "R" + System.currentTimeMillis(), 
            "VIP " + (1 - rate) * 10 + "折", 
            "VIP", 
            rate, 
            0, 
            2
        ));
    }
    
    public java.util.List<Coupon> getCoupons() {
        return promotionService.getCoupons();
    }
    
    // 会员相关方法
    public Member addMember(String name, String phone, String email) {
        return memberService.addMember(name, phone, email);
    }
    
    public Member getMemberById(String id) {
        return memberService.getMemberById(id);
    }
    
    public Member getMemberByPhone(String phone) {
        return memberService.getMemberByPhone(phone);
    }
    
    public java.util.List<Member> getMembers() {
        return memberService.getMembers();
    }
    
    public void updateMember(Member member) {
        memberService.updateMember(member);
    }
    
    public boolean deleteMember(String id) {
        return memberService.deleteMember(id);
    }
    
    public Member spend(String memberId, double amount) {
        return memberService.spend(memberId, amount);
    }
    
    public boolean redeemPoints(String memberId, int points) {
        return memberService.redeemPoints(memberId, points);
    }
    
    // 物流相关方法
    public com.ecommerce.cart.model.Logistics createLogistics(String orderId, com.ecommerce.cart.model.Logistics.DeliveryMethod deliveryMethod) {
        return logisticsService.createLogistics(orderId, deliveryMethod);
    }
    
    public com.ecommerce.cart.model.Logistics getLogisticsByOrderId(String orderId) {
        return logisticsService.getLogisticsByOrderId(orderId);
    }
    
    public com.ecommerce.cart.model.Logistics getLogisticsById(String id) {
        return logisticsService.getLogisticsById(id);
    }
    
    public java.util.List<com.ecommerce.cart.model.Logistics> getLogisticsList() {
        return logisticsService.getLogisticsList();
    }
    
    public void updateLogisticsStatus(com.ecommerce.cart.model.Logistics logistics, com.ecommerce.cart.model.Logistics.LogisticsStatus status) {
        logisticsService.updateLogisticsStatus(logistics, status);
    }
    
    public boolean updateLogisticsStatus(String orderId, com.ecommerce.cart.model.Logistics.LogisticsStatus status) {
        return logisticsService.updateLogisticsStatus(orderId, status);
    }
    
    public boolean ship(String orderId, String trackingNumber, String courierCompany) {
        return logisticsService.ship(orderId, trackingNumber, courierCompany);
    }
    
    public boolean confirmDelivery(String orderId) {
        return logisticsService.confirmDelivery(orderId);
    }
    
    public com.ecommerce.cart.model.Logistics.DeliveryMethod[] getDeliveryMethods() {
        return logisticsService.getDeliveryMethods();
    }
    
    // 评价相关方法
    public com.ecommerce.cart.model.Review addReview(com.ecommerce.cart.model.Review review) {
        return reviewService.addReview(review);
    }
    
    public com.ecommerce.cart.model.Review addReview(String productId, String productName, String userId, String userName, int rating, String content, String orderId) {
        return reviewService.addReview(productId, productName, userId, userName, rating, content, orderId);
    }
    
    public java.util.List<com.ecommerce.cart.model.Review> getReviewsByProductId(String productId) {
        return reviewService.getReviewsByProductId(productId);
    }
    
    public java.util.List<com.ecommerce.cart.model.Review> getReviewsByUserId(String userId) {
        return reviewService.getReviewsByUserId(userId);
    }
    
    public java.util.List<com.ecommerce.cart.model.Review> getReviewsByOrderId(String orderId) {
        return reviewService.getReviewsByOrderId(orderId);
    }
    
    public com.ecommerce.cart.model.Review getReviewById(String id) {
        return reviewService.getReviewById(id);
    }
    
    public java.util.List<com.ecommerce.cart.model.Review> getReviews() {
        return reviewService.getReviews();
    }
    
    public boolean deleteReview(String id) {
        return reviewService.deleteReview(id);
    }
    
    public double getAverageRating(String productId) {
        return reviewService.getAverageRating(productId);
    }
    
    // 系统相关方法
    public void clearCart() {
        cartService.clearCart();
    }
    
    public void reset() {
        cartService.clearCart();
        logger.log("系统重置");
    }
    
    // 服务访问方法（供UI层通过Facade获取服务引用）
    public CartService getCartService() {
        return cartService;
    }
    
    public OrderService getOrderService() {
        return orderService;
    }
    
    public PaymentService getPaymentService() {
        return paymentService;
    }
    
    public AddressService getAddressService() {
        return addressService;
    }
    
    public PromotionService getPromotionService() {
        return promotionService;
    }
    
    public MemberService getMemberService() {
        return memberService;
    }
    
    public LogisticsService getLogisticsService() {
        return logisticsService;
    }
    
    public ReviewService getReviewService() {
        return reviewService;
    }
    
    public ProductService getProductService() {
        return productService;
    }
}
package com.ecommerce.cart.service;

import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.Logistics;
import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.model.OrderBuilder;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.pattern.observer.EventBus;
import com.ecommerce.cart.pattern.singleton.Logger;
import com.ecommerce.cart.pattern.state.OrderStateMachine;
import com.ecommerce.cart.pattern.template.NormalOrderProcessor;
import com.ecommerce.cart.pattern.template.OrderProcessTemplate;
import com.ecommerce.cart.pattern.template.VipOrderProcessor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private List<Order> orders;
    private Logger logger;
    private LogisticsService logisticsService;
    private OrderStateMachine stateMachine;
    private static final String ORDER_FILE = "orders.dat";
    
    public OrderService() {
        this.logger = Logger.INSTANCE;
        this.logisticsService = new LogisticsService();
        this.stateMachine = OrderStateMachine.getInstance();
        this.orders = loadOrders();
        if (orders == null) {
            this.orders = new ArrayList<>();
        }
    }
    
    public Order createOrder(Cart cart) {
        Order order = new OrderBuilder(cart).build();
        orders.add(order);
        saveOrders();
        logger.log("Created order: " + order.getOrderId());
        return order;
    }
    
    public Order createOrder(Cart cart, com.ecommerce.cart.model.Coupon coupon) {
        Order order = new OrderBuilder(cart).withCoupon(coupon).build();
        orders.add(order);
        saveOrders();
        logger.log("Created order: " + order.getOrderId());
        return order;
    }
    
    public Order createOrder(Cart cart, com.ecommerce.cart.model.Coupon coupon, com.ecommerce.cart.model.Member member) {
        Order order = new OrderBuilder(cart).withCoupon(coupon).withMember(member).build();
        orders.add(order);
        saveOrders();
        logger.log("Created order: " + order.getOrderId());
        return order;
    }
    
    public List<Order> getOrders() {
        return orders;
    }
    
    public Order getOrderById(String orderId) {
        for (Order order : orders) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }
    
    /**
     * 根据状态筛选订单
     * @param status 订单状态
     * @return 订单列表
     */
    public List<Order> getOrdersByStatus(String status) {
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.getStateName().equals(status)) {
                filteredOrders.add(order);
            }
        }
        return filteredOrders;
    }
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 是否取消成功
     */
    public boolean cancelOrder(String orderId) {
        Order order = getOrderById(orderId);
        if (order != null) {
            stateMachine.processCancellation(order);
            saveOrders();
            EventBus.getInstance().publishEvent("ORDER_STATUS_CHANGED", orderId);
            logger.log("Cancelled order: " + orderId);
            return true;
        }
        return false;
    }
    
    public boolean payOrder(String orderId, String paymentType) {
        Order order = getOrderById(orderId);
        if (order != null) {
            boolean paymentSuccess = ECommerceFacade.INSTANCE.getPaymentService().processPayment(order, paymentType);
            if (paymentSuccess) {
                order.setPaymentType(paymentType);
                order.pay();
                saveOrders();
                EventBus.getInstance().publishEvent("ORDER_STATUS_CHANGED", orderId);
                return true;
            }
        }
        return false;
    }
    
    /**
     * 发货
     * @param orderId 订单ID
     * @param trackingNumber 物流单号
     * @param courierCompany 快递公司
     * @return 是否发货成功
     */
    public boolean shipOrder(String orderId, String trackingNumber, String courierCompany) {
        Order order = getOrderById(orderId);
        if (order != null) {
            stateMachine.processShipment(order);
            logisticsService.ship(orderId, trackingNumber, courierCompany);
            saveOrders();
            EventBus.getInstance().publishEvent("ORDER_STATUS_CHANGED", orderId);
            logger.log("Shipped order: " + orderId);
            return true;
        }
        return false;
    }
    
    /**
     * 确认收货
     * @param orderId 订单ID
     * @return 是否确认成功
     */
    public boolean confirmDelivery(String orderId) {
        Order order = getOrderById(orderId);
        if (order != null) {
            stateMachine.processCompletion(order);
            logisticsService.confirmDelivery(orderId);
            saveOrders();
            EventBus.getInstance().publishEvent("ORDER_STATUS_CHANGED", orderId);
            logger.log("Confirmed delivery for order: " + orderId);
            return true;
        }
        return false;
    }
    
    public void submitOrder(Order order) {
        OrderProcessTemplate processor;
        
        if (order.getVipDiscount() > 0) {
            processor = new VipOrderProcessor();
        } else {
            processor = new NormalOrderProcessor();
        }
        
        processor.setPromotionService(ECommerceFacade.INSTANCE.getPromotionService());
        if (order.getMemberId() != null && !order.getMemberId().isEmpty()) {
            processor.setMember(ECommerceFacade.INSTANCE.getMemberService().getMemberById(order.getMemberId()));
        }
        
        processor.processOrder(order);
        
        if (getOrderById(order.getOrderId()) == null) {
            orders.add(order);
        }
        
        logisticsService.createLogistics(order.getOrderId(), Logistics.DeliveryMethod.STANDARD);
        logger.log("Created logistics for order: " + order.getOrderId());
        
        logger.log("Submitted order: " + order.getOrderId());
        saveOrders();
    }
    
    public void saveOrders() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ORDER_FILE))) {
            oos.writeObject(orders);
            logger.log("Orders saved to file");
        } catch (IOException e) {
            logger.log("Failed to save orders: " + e.getMessage());
        }
    }
    
    private List<Order> loadOrders() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ORDER_FILE))) {
            @SuppressWarnings("unchecked")
            List<Order> loadedOrders = (List<Order>) ois.readObject();
            logger.log("Orders loaded from file");
            return loadedOrders;
        } catch (FileNotFoundException e) {
            logger.log("Order file not found, creating new order list");
        } catch (IOException | ClassNotFoundException e) {
            logger.log("Failed to load orders: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}
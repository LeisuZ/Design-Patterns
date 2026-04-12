package com.ecommerce.cart.service;

import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.singleton.Logger;
import com.ecommerce.cart.pattern.template.NormalOrderProcessor;
import com.ecommerce.cart.pattern.template.OrderProcessTemplate;
import com.ecommerce.cart.pattern.template.VipOrderProcessor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderService {
    private List<Order> orders;
    private Logger logger;
    private static final String ORDER_FILE = "orders.dat";
    
    public OrderService() {
        this.logger = Logger.getInstance();
        this.orders = loadOrders();
        if (orders == null) {
            this.orders = new ArrayList<>();
        }
    }
    
    public Order createOrder(Cart cart) {
        Order order = new Order(cart);
        String orderId = UUID.randomUUID().toString();
        order.setOrderId(orderId);
        
        orders.add(order);
        saveOrders();
        logger.log("Created order: " + orderId);
        return order;
    }
    
    public Order createOrder(Cart cart, com.ecommerce.cart.model.Coupon coupon) {
        Order order = new Order(cart);
        String orderId = UUID.randomUUID().toString();
        order.setOrderId(orderId);
        order.setSelectedCoupon(coupon);
        
        orders.add(order);
        saveOrders();
        logger.log("Created order: " + orderId);
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
    
    public void submitOrder(Order order) {
        // 使用模板方法模式处理订单
        OrderProcessTemplate processor;
        
        // 根据订单类型选择不同的处理器
        // 这里简单判断，实际可以根据会员等级或其他条件
        if (order.getVipDiscount() > 0) {
            processor = new VipOrderProcessor();
        } else {
            processor = new NormalOrderProcessor();
        }
        
        // 执行订单处理流程
        processor.processOrder(order);
        
        logger.log("Submitted order: " + order.getOrderId());
        saveOrders();
    }
    
    private void saveOrders() {
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
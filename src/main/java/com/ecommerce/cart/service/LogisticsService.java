package com.ecommerce.cart.service;

import com.ecommerce.cart.model.Logistics;
import com.ecommerce.cart.pattern.singleton.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 物流服务
 * 管理物流信息
 */
public class LogisticsService {
    private static final String LOGISTICS_FILE = "logistics.dat";
    private List<Logistics> logisticsList;
    private Logger logger;
    
    public LogisticsService() {
        this.logger = Logger.getInstance();
        this.logisticsList = loadLogistics();
    }
    
    /**
     * 创建物流记录
     * @param orderId 订单ID
     * @param deliveryMethod 配送方式
     * @return 物流对象
     */
    public Logistics createLogistics(String orderId, Logistics.DeliveryMethod deliveryMethod) {
        Logistics logistics = new Logistics(orderId, deliveryMethod);
        logisticsList.add(logistics);
        saveLogistics();
        logger.log("Created logistics for order: " + orderId + ", delivery method: " + deliveryMethod.getName());
        return logistics;
    }
    
    /**
     * 根据订单ID获取物流记录
     * @param orderId 订单ID
     * @return 物流对象，如果不存在则返回null
     */
    public Logistics getLogisticsByOrderId(String orderId) {
        return logisticsList.stream()
                .filter(logistics -> logistics.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 根据物流ID获取物流记录
     * @param id 物流ID
     * @return 物流对象，如果不存在则返回null
     */
    public Logistics getLogisticsById(String id) {
        return logisticsList.stream()
                .filter(logistics -> logistics.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 获取所有物流记录
     * @return 物流列表
     */
    public List<Logistics> getLogisticsList() {
        return logisticsList;
    }
    
    /**
     * 更新物流状态
     * @param logistics 物流对象
     * @param status 新的物流状态
     */
    public void updateLogisticsStatus(Logistics logistics, Logistics.LogisticsStatus status) {
        logistics.updateStatus(status);
        saveLogistics();
        logger.log("Updated logistics status for order " + logistics.getOrderId() + " to " + status.getName());
    }
    
    /**
     * 更新物流状态
     * @param orderId 订单ID
     * @param status 新的物流状态
     * @return 是否更新成功
     */
    public boolean updateLogisticsStatus(String orderId, Logistics.LogisticsStatus status) {
        Logistics logistics = getLogisticsByOrderId(orderId);
        if (logistics != null) {
            updateLogisticsStatus(logistics, status);
            return true;
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
    public boolean ship(String orderId, String trackingNumber, String courierCompany) {
        Logistics logistics = getLogisticsByOrderId(orderId);
        if (logistics != null) {
            logistics.setTrackingNumber(trackingNumber);
            logistics.setCourierCompany(courierCompany);
            updateLogisticsStatus(logistics, Logistics.LogisticsStatus.SHIPPED);
            return true;
        }
        return false;
    }
    
    /**
     * 确认送达
     * @param orderId 订单ID
     * @return 是否确认成功
     */
    public boolean confirmDelivery(String orderId) {
        Logistics logistics = getLogisticsByOrderId(orderId);
        if (logistics != null) {
            updateLogisticsStatus(logistics, Logistics.LogisticsStatus.DELIVERED);
            return true;
        }
        return false;
    }
    
    /**
     * 获取配送方式列表
     * @return 配送方式数组
     */
    public Logistics.DeliveryMethod[] getDeliveryMethods() {
        return Logistics.DeliveryMethod.values();
    }
    
    // 保存物流信息到文件
    private void saveLogistics() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LOGISTICS_FILE))) {
            oos.writeObject(logisticsList);
            logger.log("Logistics saved to file");
        } catch (IOException e) {
            logger.log("Failed to save logistics: " + e.getMessage());
        }
    }
    
    // 从文件加载物流信息
    private List<Logistics> loadLogistics() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LOGISTICS_FILE))) {
            @SuppressWarnings("unchecked")
            List<Logistics> loadedLogistics = (List<Logistics>) ois.readObject();
            logger.log("Logistics loaded from file");
            return loadedLogistics;
        } catch (FileNotFoundException e) {
            logger.log("Logistics file not found, creating new list");
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            logger.log("Failed to load logistics: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
package com.ecommerce.cart.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 物流实体类
 * 包含物流状态和配送方式等信息
 */
public class Logistics implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String orderId;
    private LogisticsStatus status;
    private String trackingNumber;
    private String courierCompany;
    private DeliveryMethod deliveryMethod;
    private Date createTime;
    private Date updateTime;
    
    public Logistics(String orderId, DeliveryMethod deliveryMethod) {
        this.id = "L" + System.currentTimeMillis();
        this.orderId = orderId;
        this.status = LogisticsStatus.PENDING; // 初始状态为待处理
        this.deliveryMethod = deliveryMethod;
        this.createTime = new Date();
        this.updateTime = new Date();
    }
    
    // 物流状态枚举
    public enum LogisticsStatus {
        PENDING("待处理"),
        PROCESSING("处理中"),
        SHIPPED("已发货"),
        IN_TRANSIT("运输中"),
        DELIVERED("已送达"),
        FAILED("配送失败");
        
        private String name;
        
        LogisticsStatus(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    // 配送方式枚举
    public enum DeliveryMethod {
        STANDARD("标准配送", 0),
        EXPRESS("快递", 10),
        SAME_DAY("当日达", 20),
        NEXT_DAY("次日达", 15);
        
        private String name;
        private double fee;
        
        DeliveryMethod(String name, double fee) {
            this.name = name;
            this.fee = fee;
        }
        
        public String getName() {
            return name;
        }
        
        public double getFee() {
            return fee;
        }
    }
    
    // 更新物流状态
    public void updateStatus(LogisticsStatus status) {
        this.status = status;
        this.updateTime = new Date();
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public LogisticsStatus getStatus() {
        return status;
    }
    
    public void setStatus(LogisticsStatus status) {
        this.status = status;
        this.updateTime = new Date();
    }
    
    public String getTrackingNumber() {
        return trackingNumber;
    }
    
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    
    public String getCourierCompany() {
        return courierCompany;
    }
    
    public void setCourierCompany(String courierCompany) {
        this.courierCompany = courierCompany;
    }
    
    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }
    
    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    @Override
    public String toString() {
        return "Logistics{" +
                "id='" + id + '\'' +
                ", orderId='" + orderId + '\'' +
                ", status=" + status.getName() +
                ", deliveryMethod=" + deliveryMethod.getName() +
                ", fee=" + deliveryMethod.getFee() +
                '}';
    }
}
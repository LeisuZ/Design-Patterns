package com.ecommerce.cart.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 评价实体类
 * 包含评价内容、评分、用户信息等
 */
public class Review implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String productId;
    private String productName;
    private String userId;
    private String userName;
    private int rating;
    private String content;
    private Date createTime;
    private String orderId;
    
    public Review(String productId, String productName, String userId, String userName, int rating, String content, String orderId) {
        this.id = "R" + System.currentTimeMillis();
        this.productId = productId;
        this.productName = productName;
        this.userId = userId;
        this.userName = userName;
        this.rating = rating;
        this.content = content;
        this.createTime = new Date();
        this.orderId = orderId;
    }
    
    // 获取评分等级
    public String getRatingLevel() {
        if (rating == 5) {
            return "非常满意";
        } else if (rating == 4) {
            return "满意";
        } else if (rating == 3) {
            return "一般";
        } else if (rating == 2) {
            return "不满意";
        } else {
            return "非常不满意";
        }
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", userName='" + userName + '\'' +
                ", rating=" + rating +
                ", content='" + content + '\'' +
                '}';
    }
}
package com.ecommerce.cart.model;

import java.io.Serializable;

/**
 * 会员实体类
 * 包含会员等级、积分等信息
 */
public class Member implements Serializable {
    private String id;
    private String name;
    private String phone;
    private String email;
    private MemberLevel level;
    private int points;
    private double totalSpent;
    
    public Member(String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.level = MemberLevel.NORMAL; // 初始等级为普通会员
        this.points = 0;
        this.totalSpent = 0;
    }
    
    // 会员等级枚举
    public enum MemberLevel {
        NORMAL("普通会员", 1.0),
        SILVER("银卡会员", 0.95),
        GOLD("金卡会员", 0.9),
        PLATINUM("白金会员", 0.85),
        DIAMOND("钻石会员", 0.8);
        
        private String name;
        private double discountRate;
        
        MemberLevel(String name, double discountRate) {
            this.name = name;
            this.discountRate = discountRate;
        }
        
        public String getName() {
            return name;
        }
        
        public double getDiscountRate() {
            return discountRate;
        }
    }
    
    // 获取会员等级对应的折扣率
    public double getDiscountRate() {
        return level.getDiscountRate();
    }
    
    // 增加积分
    public void addPoints(int points) {
        this.points += points;
        updateLevel(); // 增加积分后可能需要更新等级
    }
    
    // 消费后更新总消费金额和积分
    public void spend(double amount) {
        this.totalSpent += amount;
        int earnedPoints = (int) (amount / 10); // 每消费10元获得1积分
        addPoints(earnedPoints);
    }
    
    // 更新会员等级
    private void updateLevel() {
        if (totalSpent >= 10000) {
            level = MemberLevel.DIAMOND;
        } else if (totalSpent >= 5000) {
            level = MemberLevel.PLATINUM;
        } else if (totalSpent >= 2000) {
            level = MemberLevel.GOLD;
        } else if (totalSpent >= 1000) {
            level = MemberLevel.SILVER;
        } else {
            level = MemberLevel.NORMAL;
        }
    }
    
    // 兑换积分
    public boolean redeemPoints(int points) {
        if (this.points >= points) {
            this.points -= points;
            return true;
        }
        return false;
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public MemberLevel getLevel() {
        return level;
    }
    
    public int getPoints() {
        return points;
    }
    
    public double getTotalSpent() {
        return totalSpent;
    }
}
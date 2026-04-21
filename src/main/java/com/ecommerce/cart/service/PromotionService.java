package com.ecommerce.cart.service;

import com.ecommerce.cart.model.Coupon;
import com.ecommerce.cart.model.PromotionRule;
import com.ecommerce.cart.pattern.singleton.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PromotionService {
    private List<Coupon> coupons;
    private List<PromotionRule> promotionRules;
    private Logger logger;
    private static final String PROMOTION_FILE = "promotions.dat";
    
    public PromotionService() {
        this.logger = Logger.INSTANCE;
        this.coupons = new ArrayList<>();
        this.promotionRules = new ArrayList<>();
        loadPromotions();
        if (coupons.isEmpty() && promotionRules.isEmpty()) {
            initDefaultPromotions();
        }
    }
    
    private void initDefaultPromotions() {
        // 添加默认优惠券
        coupons.add(new Coupon("C001", "新人优惠券", 20, 100));
        coupons.add(new Coupon("C002", "满减优惠券", 50, 200));
        coupons.add(new Coupon("C003", "节日优惠券", 30, 150));
        
        // 添加默认促销规则
        promotionRules.add(new PromotionRule("R001", "满100减20", "FULL_REDUCTION", 20, 100, 1));
        promotionRules.add(new PromotionRule("R002", "VIP 9折", "VIP", 0.1, 0, 2));
        
        logger.log("Initialized default promotions");
        savePromotions();
    }
    
    public List<Coupon> getCoupons() {
        return coupons;
    }
    
    public List<PromotionRule> getPromotionRules() {
        return promotionRules;
    }
    
    public void addPromotionRule(PromotionRule rule) {
        promotionRules.add(rule);
        logger.log("Added promotion rule: " + rule.getName());
        savePromotions();
    }
    
    public void updatePromotionRule(String ruleId, PromotionRule updatedRule) {
        for (int i = 0; i < promotionRules.size(); i++) {
            if (promotionRules.get(i).getId().equals(ruleId)) {
                promotionRules.set(i, updatedRule);
                logger.log("Updated promotion rule: " + ruleId);
                savePromotions();
                break;
            }
        }
    }
    
    public void removePromotionRule(String ruleId) {
        promotionRules.removeIf(rule -> rule.getId().equals(ruleId));
        logger.log("Removed promotion rule: " + ruleId);
        savePromotions();
    }
    
    public void addCoupon(Coupon coupon) {
        coupons.add(coupon);
        logger.log("Added coupon: " + coupon.getName());
        savePromotions();
    }
    
    private void savePromotions() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PROMOTION_FILE))) {
            oos.writeObject(coupons);
            oos.writeObject(promotionRules);
            logger.log("Promotions saved to file");
        } catch (IOException e) {
            logger.log("Failed to save promotions: " + e.getMessage());
        }
    }
    
    private void loadPromotions() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PROMOTION_FILE))) {
            @SuppressWarnings("unchecked")
            List<Coupon> loadedCoupons = (List<Coupon>) ois.readObject();
            @SuppressWarnings("unchecked")
            List<PromotionRule> loadedRules = (List<PromotionRule>) ois.readObject();
            this.coupons = loadedCoupons;
            this.promotionRules = loadedRules;
            logger.log("Promotions loaded from file");
        } catch (FileNotFoundException e) {
            logger.log("Promotion file not found, will use defaults");
        } catch (IOException | ClassNotFoundException e) {
            logger.log("Failed to load promotions: " + e.getMessage());
        }
    }
}
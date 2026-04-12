package com.ecommerce.cart.service;

import com.ecommerce.cart.model.Coupon;
import com.ecommerce.cart.model.PromotionRule;
import com.ecommerce.cart.pattern.singleton.Logger;

import java.util.ArrayList;
import java.util.List;

public class PromotionService {
    private List<Coupon> coupons;
    private List<PromotionRule> promotionRules;
    private Logger logger;
    
    public PromotionService() {
        this.coupons = new ArrayList<>();
        this.promotionRules = new ArrayList<>();
        this.logger = Logger.getInstance();
        initDefaultPromotions();
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
    }
    
    public List<Coupon> getCoupons() {
        return coupons;
    }
    
    public List<PromotionRule> getPromotionRules() {
        return promotionRules;
    }
    
    public void addCoupon(Coupon coupon) {
        coupons.add(coupon);
        logger.log("Added coupon: " + coupon.getName());
    }
    
    public void addPromotionRule(PromotionRule rule) {
        promotionRules.add(rule);
        logger.log("Added promotion rule: " + rule.getName());
    }
    
    public void updatePromotionRule(String ruleId, PromotionRule updatedRule) {
        for (int i = 0; i < promotionRules.size(); i++) {
            if (promotionRules.get(i).getId().equals(ruleId)) {
                promotionRules.set(i, updatedRule);
                logger.log("Updated promotion rule: " + ruleId);
                break;
            }
        }
    }
    
    public void removePromotionRule(String ruleId) {
        promotionRules.removeIf(rule -> rule.getId().equals(ruleId));
        logger.log("Removed promotion rule: " + ruleId);
    }
}
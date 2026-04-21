package com.ecommerce.cart.pattern.chain;

import com.ecommerce.cart.model.Member;
import com.ecommerce.cart.model.PromotionRule;
import com.ecommerce.cart.pattern.strategy.StrategyContext;
import com.ecommerce.cart.service.PromotionService;
import java.util.Comparator;
import java.util.List;

public class DiscountChainBuilder {
    private PromotionService promotionService;
    private Member member;

    public DiscountChainBuilder(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    public DiscountChainBuilder withMember(Member member) {
        this.member = member;
        return this;
    }

    public Handler build() {
        List<PromotionRule> rules = promotionService.getPromotionRules();
        rules.sort(Comparator.comparingInt(PromotionRule::getPriority));
        
        Handler head = null;
        Handler current = null;
        
        StrategyContext couponContext = new StrategyContext();
        couponContext.selectStrategy("COUPON");
        Handler couponHandler = new CouponHandler(couponContext);
        head = couponHandler;
        current = couponHandler;
        
        for (PromotionRule rule : rules) {
            Handler handler = createHandler(rule);
            if (handler != null) {
                if (current != null) {
                    current.setNext(handler);
                }
                current = handler;
            }
        }
        
        return head;
    }
    
    private Handler createHandler(PromotionRule rule) {
        StrategyContext context = new StrategyContext();
        switch (rule.getType()) {
            case "FULL_REDUCTION":
                context.selectStrategy("FULL_REDUCTION", rule.getMinSpend(), rule.getValue());
                return new FullReductionHandler(context);
            case "VIP":
                if (member == null) {
                    return null;
                }
                double vipDiscountRate = 1 - member.getLevel().getDiscountRate();
                context.selectStrategy("VIP", vipDiscountRate);
                return new VipHandler(context);
            case "GIFT":
                context.selectStrategy("GIFT", rule.getMinSpend(), "赠品", rule.getValue());
                return new GiftHandler(context);
            case "TIME_LIMIT":
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                context.selectStrategy("TIME_LIMIT", rule.getValue(), now, now.plusHours(24));
                return new TimeLimitDiscountHandler(context);
            default:
                return null;
        }
    }
}

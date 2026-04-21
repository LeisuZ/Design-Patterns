package com.ecommerce.cart.pattern.factory;

import com.ecommerce.cart.pattern.strategy.CouponStrategy;
import com.ecommerce.cart.pattern.strategy.FullReductionStrategy;
import com.ecommerce.cart.pattern.strategy.GiftStrategy;
import com.ecommerce.cart.pattern.strategy.PromotionStrategy;
import com.ecommerce.cart.pattern.strategy.TimeLimitDiscountStrategy;
import com.ecommerce.cart.pattern.strategy.VipDiscountStrategy;
import java.time.LocalDateTime;

/**
 * 工厂模式-策略工厂
 * 根据类型创建不同的促销策略实现类
 */
public class StrategyFactory {
    public static PromotionStrategy createStrategy(String type, Object... params) {
        switch (type) {
            case "COUPON":
                return new CouponStrategy();
            case "FULL_REDUCTION":
                if (params.length >= 2) {
                    return new FullReductionStrategy((double) params[0], (double) params[1]);
                } else {
                    // 默认满减规则：满100减20
                    return new FullReductionStrategy(100, 20);
                }
            case "VIP":
                if (params.length >= 1) {
                    return new VipDiscountStrategy((double) params[0]);
                } else {
                    // 默认VIP折扣：9折
                    return new VipDiscountStrategy(0.1);
                }
            case "TIME_LIMIT":
                if (params.length >= 3) {
                    return new TimeLimitDiscountStrategy(
                        (double) params[0],
                        (LocalDateTime) params[1],
                        (LocalDateTime) params[2]
                    );
                } else {
                    // 默认限时折扣：5折，持续1小时
                    LocalDateTime now = LocalDateTime.now();
                    return new TimeLimitDiscountStrategy(0.5, now, now.plusHours(1));
                }
            case "GIFT":
                if (params.length >= 3) {
                    return new GiftStrategy(
                        (double) params[0],
                        (String) params[1],
                        (double) params[2]
                    );
                } else {
                    // 默认满赠：满200送价值50的商品
                    return new GiftStrategy(200, "精美礼品", 50);
                }
            default:
                return null;
        }
    }
}
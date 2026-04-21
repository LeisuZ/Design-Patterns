package com.ecommerce.cart.util;

public class CategoryUtil {
    public static String toDisplayName(String category) {
        if (category == null) return "";
        switch (category) {
            case "electronics": return "电子产品";
            case "clothing": return "服装";
            case "food": return "食品";
            case "books": return "图书";
            default: return category;
        }
    }
    
    public static String toEnglishName(String displayName) {
        if (displayName == null) return "";
        switch (displayName) {
            case "电子产品": return "electronics";
            case "服装": return "clothing";
            case "食品": return "food";
            case "图书": return "books";
            default: return displayName;
        }
    }

    public static String getPromotionTypeDisplayName(String type) {
        switch (type) {
            case "FULL_REDUCTION": return "满减";
            case "VIP": return "VIP折扣";
            case "COUPON": return "优惠券";
            case "GIFT": return "满赠";
            case "TIME_LIMIT": return "限时折扣";
            default: return type;
        }
    }

    public static String getPromotionTypeEnglishName(String displayName) {
        switch (displayName) {
            case "满减": return "FULL_REDUCTION";
            case "VIP折扣": return "VIP";
            case "优惠券": return "COUPON";
            case "满赠": return "GIFT";
            case "限时折扣": return "TIME_LIMIT";
            default: return displayName;
        }
    }
}

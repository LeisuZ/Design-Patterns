package com.ecommerce.cart.pattern.factory;

import com.ecommerce.cart.model.Product;
import com.ecommerce.cart.pattern.strategy.PromotionStrategy;

/**
 * 抽象工厂模式-抽象工厂接口
 * 定义创建相关或依赖对象家族的接口
 */
public interface AbstractFactory {
    /**
     * 创建产品
     * @param type 产品类型
     * @param id 产品ID
     * @param name 产品名称
     * @param price 产品价格
     * @return 产品对象
     */
    Product createProduct(String type, String id, String name, double price);
    
    /**
     * 创建促销策略
     * @param type 策略类型
     * @param params 策略参数
     * @return 促销策略对象
     */
    PromotionStrategy createStrategy(String type, Object... params);
}

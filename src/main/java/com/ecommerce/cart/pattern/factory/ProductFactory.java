package com.ecommerce.cart.pattern.factory;

import com.ecommerce.cart.model.Product;

/**
 * 工厂模式-产品工厂
 * 根据类型创建不同类别的产品对象
 */
public class ProductFactory {
    public static Product createProduct(String type, String id, String name, double price) {
        switch (type) {
            case "electronics":
                return new Product(id, name, price, "电子产品");
            case "clothing":
                return new Product(id, name, price, "服装");
            case "food":
                return new Product(id, name, price, "食品");
            case "books":
                return new Product(id, name, price, "图书");
            default:
                return new Product(id, name, price, "其他");
        }
    }
}
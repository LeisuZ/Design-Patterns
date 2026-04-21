package com.ecommerce.cart.service;

import com.ecommerce.cart.model.Product;
import com.ecommerce.cart.pattern.singleton.DBConnection;
import com.ecommerce.cart.pattern.singleton.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductService {
    private static final String PRODUCT_FILE = "products.dat";
    private List<Product> products;
    private Logger logger;

    public ProductService() {
        this.logger = Logger.INSTANCE;
        this.products = loadProducts();
        if (products.isEmpty()) {
            initDefaultProducts();
        }
    }

    private void initDefaultProducts() {
        addProduct("electronics", "E001", "智能手机", 2999.00);
        addProduct("electronics", "E002", "笔记本电脑", 5999.00);
        addProduct("electronics", "E003", "蓝牙耳机", 299.00);
        addProduct("electronics", "E004", "平板电脑", 3299.00);
        addProduct("clothing", "C001", "纯棉T恤", 99.00);
        addProduct("clothing", "C002", "牛仔裤", 199.00);
        addProduct("clothing", "C003", "运动鞋", 499.00);
        addProduct("clothing", "C004", "羽绒服", 899.00);
        addProduct("food", "F001", "坚果礼盒", 69.00);
        addProduct("food", "F002", "有机牛奶", 49.00);
        addProduct("food", "F003", "进口巧克力", 39.00);
        addProduct("food", "F004", "龙井茶叶", 128.00);
        addProduct("books", "B001", "设计模式", 89.00);
        addProduct("books", "B002", "算法导论", 129.00);
        addProduct("books", "B003", "深入理解JVM", 99.00);
        addProduct("books", "B004", "Effective Java", 79.00);
        logger.log("Initialized default products");
    }

    public Product addProduct(String category, String id, String name, double price) {
        Product product = new Product(id, name, price, category);
        products.add(product);
        DBConnection.INSTANCE.executeUpdate("products", id, product);
        saveProducts();
        logger.log("Added product: " + name);
        return product;
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public Product getProductById(String id) {
        Object cached = DBConnection.INSTANCE.executeQuery("products", id);
        if (cached instanceof Product) {
            return (Product) cached;
        }
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (p.getCategory().equals(category)) {
                result.add(p);
            }
        }
        return result;
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                p.getId().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(p);
            }
        }
        return result;
    }

    public boolean updateProduct(String id, String name, double price, String category) {
        Product product = getProductById(id);
        if (product != null) {
            product.setName(name);
            product.setPrice(price);
            product.setCategory(category);
            saveProducts();
            logger.log("Updated product: " + name);
            return true;
        }
        return false;
    }

    public boolean deleteProduct(String id) {
        boolean removed = products.removeIf(p -> p.getId().equals(id));
        if (removed) {
            DBConnection.INSTANCE.executeDelete("products", id);
            saveProducts();
            logger.log("Deleted product: " + id);
        }
        return removed;
    }

    private void saveProducts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PRODUCT_FILE))) {
            oos.writeObject(products);
            logger.log("Products saved to file");
        } catch (IOException e) {
            logger.log("Failed to save products: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Product> loadProducts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PRODUCT_FILE))) {
            List<Product> loaded = (List<Product>) ois.readObject();
            logger.log("Products loaded from file");
            return loaded;
        } catch (FileNotFoundException e) {
            logger.log("Products file not found, creating new list");
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            logger.log("Failed to load products: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}

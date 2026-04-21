package com.ecommerce.cart.service;

import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.model.Product;
import com.ecommerce.cart.pattern.factory.ECommerceFactory;
import com.ecommerce.cart.pattern.singleton.Logger;

import java.io.*;
import java.util.List;

public class CartService {
    private Cart cart;
    private Logger logger;
    private ECommerceFactory factory;
    private static final String CART_FILE = "cart.dat";
    
    public CartService() {
        this.logger = Logger.INSTANCE;
        this.factory = new ECommerceFactory();
        this.cart = loadCart();
        if (cart == null) {
            this.cart = new Cart();
        }
    }
    
    public void addProduct(String type, String id, String name, double price, int quantity) {
        Product product = factory.createProduct(type, id, name, price);
        cart.addItem(product, quantity);
        logger.log("Added product: " + name + " x " + quantity);
        saveCart();
    }
    
    public void removeProduct(String productId) {
        cart.removeItem(productId);
        logger.log("Removed product with id: " + productId);
        saveCart();
    }
    
    public void updateQuantity(String productId, int quantity) {
        cart.updateQuantity(productId, quantity);
        logger.log("Updated product quantity: " + productId + " to " + quantity);
        saveCart();
    }
    
    public Cart getCart() {
        return cart;
    }
    
    public double getTotal() {
        return cart.getTotal();
    }
    
    public void clearCart() {
        cart.clear();
        logger.log("Cart cleared");
        saveCart();
    }
    
    private void saveCart() {
        try {
            // 数据验证：确保购物车不为空且商品数量有效
            if (cart.getItems() != null) {
                for (CartItem item : cart.getItems()) {
                    if (item.getProduct() == null || item.getQuantity() <= 0) {
                        logger.log("Invalid cart item detected, skipping save");
                        return;
                    }
                }
            }
            
            // 创建备份文件
            File cartFile = new File(CART_FILE);
            if (cartFile.exists()) {
                File backupFile = new File(CART_FILE + ".bak");
                if (backupFile.exists()) {
                    backupFile.delete();
                }
                cartFile.renameTo(backupFile);
                logger.log("Cart backup created");
            }
            
            // 保存购物车数据
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CART_FILE))) {
                oos.writeObject(cart.getItems());
                logger.log("Cart saved to file successfully");
            }
        } catch (IOException e) {
            logger.log("Failed to save cart: " + e.getMessage());
            // 尝试恢复备份
            try {
                File backupFile = new File(CART_FILE + ".bak");
                if (backupFile.exists()) {
                    File cartFile = new File(CART_FILE);
                    if (cartFile.exists()) {
                        cartFile.delete();
                    }
                    backupFile.renameTo(cartFile);
                    logger.log("Cart restored from backup");
                }
            } catch (Exception ex) {
                logger.log("Failed to restore cart from backup: " + ex.getMessage());
            }
        }
    }
    
    private Cart loadCart() {
        Cart cart = new Cart();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CART_FILE))) {
            @SuppressWarnings("unchecked")
            List<CartItem> items = (List<CartItem>) ois.readObject();
            
            // 数据验证：确保读取的数据有效
            if (items != null) {
                for (CartItem item : items) {
                    if (item != null && item.getProduct() != null && item.getQuantity() > 0) {
                        cart.addItem(item.getProduct(), item.getQuantity());
                    } else {
                        logger.log("Invalid cart item detected during loading, skipping");
                    }
                }
            }
            logger.log("Cart loaded from file successfully");
        } catch (FileNotFoundException e) {
            logger.log("Cart file not found, creating new cart");
        } catch (IOException | ClassNotFoundException e) {
            logger.log("Failed to load cart: " + e.getMessage());
            // 尝试从备份文件加载
            try {
                File backupFile = new File(CART_FILE + ".bak");
                if (backupFile.exists()) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backupFile))) {
                        @SuppressWarnings("unchecked")
                        List<CartItem> items = (List<CartItem>) ois.readObject();
                        if (items != null) {
                            for (CartItem item : items) {
                                if (item != null && item.getProduct() != null && item.getQuantity() > 0) {
                                    cart.addItem(item.getProduct(), item.getQuantity());
                                }
                            }
                        }
                        logger.log("Cart loaded from backup successfully");
                    }
                }
            } catch (Exception ex) {
                logger.log("Failed to load cart from backup: " + ex.getMessage());
            }
        }
        return cart;
    }
}
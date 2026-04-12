package com.ecommerce.cart.service;

import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.model.Product;
import com.ecommerce.cart.pattern.factory.ProductFactory;
import com.ecommerce.cart.pattern.singleton.Logger;

import java.io.*;
import java.util.List;

public class CartService {
    private Cart cart;
    private Logger logger;
    private static final String CART_FILE = "cart.dat";
    
    public CartService() {
        this.logger = Logger.getInstance();
        this.cart = loadCart();
        if (cart == null) {
            this.cart = new Cart();
        }
    }
    
    public void addProduct(String type, String id, String name, double price, int quantity) {
        Product product = ProductFactory.createProduct(type, id, name, price);
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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CART_FILE))) {
            oos.writeObject(cart.getItems());
            logger.log("Cart saved to file");
        } catch (IOException e) {
            logger.log("Failed to save cart: " + e.getMessage());
        }
    }
    
    private Cart loadCart() {
        Cart cart = new Cart();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CART_FILE))) {
            @SuppressWarnings("unchecked")
            List<CartItem> items = (List<CartItem>) ois.readObject();
            for (CartItem item : items) {
                cart.addItem(item.getProduct(), item.getQuantity());
            }
            logger.log("Cart loaded from file");
        } catch (FileNotFoundException e) {
            logger.log("Cart file not found, creating new cart");
        } catch (IOException | ClassNotFoundException e) {
            logger.log("Failed to load cart: " + e.getMessage());
        }
        return cart;
    }
}
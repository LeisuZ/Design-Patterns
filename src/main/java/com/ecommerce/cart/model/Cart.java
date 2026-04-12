package com.ecommerce.cart.model;

import com.ecommerce.cart.pattern.observer.Subject;
import com.ecommerce.cart.pattern.observer.Observer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable, Subject {
    private List<CartItem> items;
    private List<Observer> observers;
    
    public Cart() {
        items = new ArrayList<>();
        observers = new ArrayList<>();
    }
    
    public void addItem(ProductInterface product, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                notifyObservers();
                return;
            }
        }
        items.add(new CartItem(product, quantity));
        notifyObservers();
    }
    
    public void removeItem(String productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
        notifyObservers();
    }
    
    public void updateQuantity(String productId, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                notifyObservers();
                break;
            }
        }
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public double getTotal() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }
    
    public void clear() {
        items.clear();
        notifyObservers();
    }
    
    @Override
    public void registerObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
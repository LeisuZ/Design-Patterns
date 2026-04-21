package com.ecommerce.cart.model;

import com.ecommerce.cart.pattern.observer.EventBus;
import com.ecommerce.cart.pattern.observer.Subject;
import com.ecommerce.cart.pattern.observer.Observer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable, Subject {
    private static final long serialVersionUID = 1L;
    private List<CartItem> items;
    private transient EventBus eventBus;
    
    public Cart() {
        items = new ArrayList<>();
        eventBus = EventBus.getInstance();
    }
    
    public void addItem(ProductInterface product, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                notifyObservers("CART_CHANGED", "UPDATE:" + product.getId());
                return;
            }
        }
        items.add(new CartItem(product, quantity));
        notifyObservers("CART_CHANGED", "ADD:" + product.getId());
    }
    
    public void removeItem(String productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
        notifyObservers("CART_CHANGED", "REMOVE:" + productId);
    }
    
    public void updateQuantity(String productId, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                notifyObservers("CART_CHANGED", "UPDATE:" + productId);
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
        notifyObservers("CART_CHANGED", "CLEAR");
    }
    
    @Override
    public void registerObserver(Observer observer) {
        EventBus.getInstance().registerObserver("CART_CHANGED", observer);
    }
    
    @Override
    public void removeObserver(Observer observer) {
        EventBus.getInstance().removeObserver("CART_CHANGED", observer);
    }
    
    @Override
    public void notifyObservers(String eventType, Object data) {
        if (eventBus == null) {
            eventBus = EventBus.getInstance();
        }
        eventBus.publishEvent(eventType, data);
    }
}

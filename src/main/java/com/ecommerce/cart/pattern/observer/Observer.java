package com.ecommerce.cart.pattern.observer;

public interface Observer {
    void update(String eventType, Object data);
}
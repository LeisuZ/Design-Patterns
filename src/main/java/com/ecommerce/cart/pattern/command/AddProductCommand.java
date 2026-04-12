package com.ecommerce.cart.pattern.command;

import com.ecommerce.cart.service.CartService;

public class AddProductCommand implements Command {
    private CartService cartService;
    private String type;
    private String id;
    private String name;
    private double price;
    private int quantity;
    
    public AddProductCommand(CartService cartService, String type, String id, String name, double price, int quantity) {
        this.cartService = cartService;
        this.type = type;
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    
    @Override
    public void execute() {
        cartService.addProduct(type, id, name, price, quantity);
    }
    
    @Override
    public void undo() {
        cartService.removeProduct(id);
    }
}
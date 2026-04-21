package com.ecommerce.cart.pattern.command;

import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.service.CartService;

public class UpdateQuantityCommand implements Command {
    private CartService cartService;
    private String productId;
    private int oldQuantity;
    private int newQuantity;
    
    public UpdateQuantityCommand(CartService cartService, String productId, int newQuantity) {
        this.cartService = cartService;
        this.productId = productId;
        this.newQuantity = newQuantity;
        // 保存修改前的数量，用于撤销操作
        for (CartItem item : cartService.getCart().getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                this.oldQuantity = item.getQuantity();
                break;
            }
        }
    }
    
    @Override
    public void execute() {
        cartService.updateQuantity(productId, newQuantity);
    }
    
    @Override
    public void undo() {
        cartService.updateQuantity(productId, oldQuantity);
    }
}
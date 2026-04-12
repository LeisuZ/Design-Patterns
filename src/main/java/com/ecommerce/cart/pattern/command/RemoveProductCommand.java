package com.ecommerce.cart.pattern.command;

import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.service.CartService;

public class RemoveProductCommand implements Command {
    private CartService cartService;
    private String productId;
    private CartItem removedItem;
    
    public RemoveProductCommand(CartService cartService, String productId) {
        this.cartService = cartService;
        this.productId = productId;
        // 保存删除前的商品信息，用于撤销操作
        for (CartItem item : cartService.getCart().getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                this.removedItem = item;
                break;
            }
        }
    }
    
    @Override
    public void execute() {
        cartService.removeProduct(productId);
    }
    
    @Override
    public void undo() {
        if (removedItem != null) {
            cartService.addProduct(
                "", // 类型可能需要从商品信息中获取，这里简化处理
                removedItem.getProduct().getId(),
                removedItem.getProduct().getName(),
                removedItem.getProduct().getPrice(),
                removedItem.getQuantity()
            );
        }
    }
}
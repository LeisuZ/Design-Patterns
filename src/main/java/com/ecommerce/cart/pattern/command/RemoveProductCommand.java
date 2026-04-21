package com.ecommerce.cart.pattern.command;

import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.service.CartService;

public class RemoveProductCommand implements Command {
    private CartService cartService;
    private String productId;
    private CartItem removedItem;
    private String productCategory;
    
    public RemoveProductCommand(CartService cartService, String productId) {
        this.cartService = cartService;
        this.productId = productId;
        for (CartItem item : cartService.getCart().getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                this.removedItem = item;
                if (item.getProduct() instanceof com.ecommerce.cart.model.Product) {
                    this.productCategory = ((com.ecommerce.cart.model.Product) item.getProduct()).getCategory();
                }
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
                productCategory != null ? productCategory : "",
                removedItem.getProduct().getId(),
                removedItem.getProduct().getName(),
                removedItem.getProduct().getPrice(),
                removedItem.getQuantity()
            );
        }
    }
}

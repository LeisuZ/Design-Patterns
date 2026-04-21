package com.ecommerce.cart.pattern.command;

public interface Command {
    void execute();
    void undo();
}
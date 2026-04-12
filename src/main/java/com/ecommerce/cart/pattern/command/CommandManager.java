package com.ecommerce.cart.pattern.command;

import java.util.Stack;

public class CommandManager {
    private Stack<Command> commandStack;
    
    public CommandManager() {
        commandStack = new Stack<>();
    }
    
    public void executeCommand(Command command) {
        command.execute();
        commandStack.push(command);
    }
    
    public void undo() {
        if (!commandStack.isEmpty()) {
            Command command = commandStack.pop();
            command.undo();
        }
    }
    
    public boolean canUndo() {
        return !commandStack.isEmpty();
    }
}
package com.ecommerce.cart.pattern.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CommandManager {
    private Stack<Command> commandStack; // 已执行的命令栈，用于撤销
    private List<Command> commandQueue; // 命令队列，用于批处理
    
    public CommandManager() {
        commandStack = new Stack<>();
        commandQueue = new ArrayList<>();
    }
    
    /**
     * 执行单个命令
     * @param command 命令对象
     */
    public void executeCommand(Command command) {
        command.execute();
        commandStack.push(command);
    }
    
    /**
     * 添加命令到队列
     * @param command 命令对象
     */
    public void addCommand(Command command) {
        commandQueue.add(command);
    }
    
    /**
     * 批处理执行队列中的所有命令
     */
    public void executeBatch() {
        for (Command command : commandQueue) {
            command.execute();
            commandStack.push(command);
        }
        commandQueue.clear();
    }
    
    /**
     * 事务执行队列中的所有命令
     * 确保所有命令要么全部执行成功，要么全部不执行
     */
    public void executeTransaction() {
        List<Command> executedCommands = new ArrayList<>();
        try {
            for (Command command : commandQueue) {
                command.execute();
                executedCommands.add(command);
                commandStack.push(command);
            }
            commandQueue.clear();
        } catch (Exception e) {
            // 发生异常，撤销已执行的命令
            for (Command command : executedCommands) {
                command.undo();
                if (!commandStack.isEmpty()) {
                    commandStack.pop();
                }
            }
            e.printStackTrace();
        }
    }
    
    /**
     * 撤销命令
     */
    public void undo() {
        if (!commandStack.isEmpty()) {
            Command command = commandStack.pop();
            command.undo();
        }
    }
    
    /**
     * 检查是否可以撤销
     * @return 是否可以撤销
     */
    public boolean canUndo() {
        return !commandStack.isEmpty();
    }
    
    /**
     * 清空命令队列
     */
    public void clearQueue() {
        commandQueue.clear();
    }
    
    /**
     * 获取队列大小
     * @return 队列大小
     */
    public int getQueueSize() {
        return commandQueue.size();
    }
}
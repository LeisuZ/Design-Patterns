package com.ecommerce.cart.pattern.singleton;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 单例模式-日志记录器
 * 确保系统中只有一个日志记录器实例
 */
public enum Logger {
    INSTANCE; // 唯一实例
    
    private PrintWriter writer; // 日志写入器
    
    /**
     * 枚举构造方法，防止外部实例化
     */
    private Logger() {
        try {
            writer = new PrintWriter(new FileWriter("app.log", true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 记录日志
     * @param message 日志消息
     */
    public void log(String message) {
        String timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
        String logMessage = "[" + timestamp + "] " + message;
        System.out.println(logMessage);
        if (writer != null) {
            writer.println(logMessage);
            writer.flush();
        }
    }
    
    /**
     * 获取日志内容
     * @return 日志内容
     */
    public String getLogContent() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("app.log"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            content.append("日志文件不存在或无法读取: " + e.getMessage());
        }
        return content.toString();
    }
    
    /**
     * 关闭日志写入器
     */
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}
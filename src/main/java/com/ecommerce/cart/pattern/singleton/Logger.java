package com.ecommerce.cart.pattern.singleton;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final Logger instance = new Logger();
    private PrintWriter writer;
    
    private Logger() {
        try {
            writer = new PrintWriter(new FileWriter("app.log", true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Logger getInstance() {
        return instance;
    }
    
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
    
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}
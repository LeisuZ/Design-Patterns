package com.ecommerce.cart.ui;

import com.ecommerce.cart.pattern.singleton.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogViewerFrame extends JFrame {
    private JTextArea logTextArea;
    private JButton refreshButton;
    private JButton closeButton;
    private Logger logger;
    
    public LogViewerFrame() {
        logger = Logger.getInstance();
        initUI();
        loadLogContent();
    }
    
    private void initUI() {
        setTitle("日志查看器");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 日志文本区域
        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel();
        refreshButton = new JButton("刷新");
        closeButton = new JButton("关闭");
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 按钮监听器
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadLogContent();
            }
        });
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void loadLogContent() {
        String logContent = logger.getLogContent();
        logTextArea.setText(logContent);
        // 滚动到末尾
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }
}
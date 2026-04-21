package com.ecommerce.cart.ui;

import com.ecommerce.cart.pattern.singleton.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogViewerFrame extends JFrame {
    private JButton backButton;
    private JTextArea logTextArea;
    private JButton refreshButton;
    private JButton closeButton;
    private JButton filterButton;
    private JTextField filterTextField;
    private Logger logger;
    
    public LogViewerFrame() {
        logger = Logger.INSTANCE;
        initUI();
        loadLogContent();
    }
    
    private JTextField startDateField;  // 开始日期
    private JTextField endDateField;    // 结束日期
    private JButton clearFilterButton;   // 清除筛选按钮
    
    private void initUI() {
        setTitle("日志查看器");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 日志文本区域
        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 筛选面板
        JPanel filterPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 第一行：消息筛选
        JPanel messageFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        messageFilterPanel.add(new JLabel("消息包含:"));
        filterTextField = new JTextField(30);
        messageFilterPanel.add(filterTextField);
        
        // 第二行：日期筛选
        JPanel dateFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        dateFilterPanel.add(new JLabel("开始日期 (yyyy-MM-dd):"));
        startDateField = new JTextField(10);
        dateFilterPanel.add(startDateField);
        dateFilterPanel.add(new JLabel("结束日期 (yyyy-MM-dd):"));
        endDateField = new JTextField(10);
        dateFilterPanel.add(endDateField);
        
        filterButton = new JButton("筛选");
        dateFilterPanel.add(filterButton);
        clearFilterButton = new JButton("清除筛选");
        dateFilterPanel.add(clearFilterButton);
        
        JPanel patternFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        patternFilterPanel.setBorder(BorderFactory.createTitledBorder("模式筛选"));
        String[] patternNames = {"单例模式", "模板方法", "策略模式", "状态模式", "命令模式", "适配器模式", "装饰器模式"};
        for (String name : patternNames) {
            JButton btn = new JButton(name);
            btn.setFont(new Font("宋体", Font.PLAIN, 10));
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filterTextField.setText(name);
                    filterLogContent();
                }
            });
            patternFilterPanel.add(btn);
        }

        filterPanel.add(messageFilterPanel);
        filterPanel.add(dateFilterPanel);
        filterPanel.add(patternFilterPanel);
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
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
        
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterLogContent();
            }
        });
        
        clearFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterTextField.setText("");
                startDateField.setText("");
                endDateField.setText("");
                loadLogContent();
            }
        });
    }
    
    private void loadLogContent() {
        String logContent = logger.getLogContent();
        logTextArea.setText(logContent);
        // 滚动到末尾
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }
    
    private void filterLogContent() {
        String filterText = filterTextField.getText().trim();
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();
        
        if (filterText.isEmpty() && startDate.isEmpty() && endDate.isEmpty()) {
            loadLogContent();
            return;
        }
        
        String logContent = logger.getLogContent();
        StringBuilder filteredContent = new StringBuilder();
        String[] lines = logContent.split("\n");
        
        for (String line : lines) {
            boolean match = true;
            
            // 消息包含筛选
            if (!filterText.isEmpty() && !line.toLowerCase().contains(filterText.toLowerCase())) {
                match = false;
            }
            
            // 日期范围筛选
            if (match && (!startDate.isEmpty() || !endDate.isEmpty())) {
                // 提取日志中的日期部分 [2026-04-13 22:58:50]
                if (line.length() >= 19) {
                    String logDateStr = line.substring(1, 11); // 提取 yyyy-MM-dd 部分
                    
                    if (!startDate.isEmpty() && logDateStr.compareTo(startDate) < 0) {
                        match = false;
                    }
                    
                    if (!endDate.isEmpty() && logDateStr.compareTo(endDate) > 0) {
                        match = false;
                    }
                } else {
                    match = false;
                }
            }
            
            if (match) {
                filteredContent.append(line).append("\n");
            }
        }
        
        logTextArea.setText(filteredContent.toString());
        // 滚动到开头
        logTextArea.setCaretPosition(0);
    }
    
    public void refreshData() {
        loadLogContent();
    }

    public JButton getBackButton() {
        if (backButton == null) {
            backButton = new JButton("返回");
        }
        return backButton;
    }
}
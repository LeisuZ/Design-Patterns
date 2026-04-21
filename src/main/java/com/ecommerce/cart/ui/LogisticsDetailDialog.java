package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Logistics;
import com.ecommerce.cart.model.Order;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class LogisticsDetailDialog extends JDialog {
    public LogisticsDetailDialog(JFrame owner, Order order, Logistics logistics) {
        super(owner, "物流详情", true);
        setSize(500, 400);
        setLocationRelativeTo(owner);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel infoPanel = new JPanel(new GridLayout(8, 2, 10, 8));
        infoPanel.setBorder(BorderFactory.createTitledBorder("物流信息"));
        
        infoPanel.add(new JLabel("订单号:"));
        infoPanel.add(new JLabel(order.getOrderId()));
        
        infoPanel.add(new JLabel("配送方式:"));
        infoPanel.add(new JLabel(logistics != null ? logistics.getDeliveryMethod().getName() : "—"));
        
        infoPanel.add(new JLabel("快递公司:"));
        infoPanel.add(new JLabel(logistics != null && logistics.getCourierCompany() != null ? logistics.getCourierCompany() : "未分配"));
        
        infoPanel.add(new JLabel("物流单号:"));
        infoPanel.add(new JLabel(logistics != null && logistics.getTrackingNumber() != null ? logistics.getTrackingNumber() : "未分配"));
        
        infoPanel.add(new JLabel("物流状态:"));
        infoPanel.add(new JLabel(logistics != null ? logistics.getStatus().getName() : "未发货"));
        
        infoPanel.add(new JLabel("创建时间:"));
        infoPanel.add(new JLabel(logistics != null && logistics.getCreateTime() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(logistics.getCreateTime()) : "—"));
        
        infoPanel.add(new JLabel("更新时间:"));
        infoPanel.add(new JLabel(logistics != null && logistics.getUpdateTime() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(logistics.getUpdateTime()) : "—"));
        
        infoPanel.add(new JLabel("运费:"));
        infoPanel.add(new JLabel(logistics != null ? "¥" + String.format("%.2f", logistics.getDeliveryMethod().getFee()) : "¥0.00"));
        
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(ev -> dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
}

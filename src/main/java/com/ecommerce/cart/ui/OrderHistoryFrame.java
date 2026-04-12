package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.service.OrderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrderHistoryFrame extends JFrame {
    private OrderService orderService;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JButton viewButton;
    private JButton closeButton;
    
    public OrderHistoryFrame() {
        orderService = new OrderService();
        initUI();
        updateOrderTable();
    }
    
    private void initUI() {
        setTitle("订单历史记录");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 表格部分
        String[] columnNames = {"订单ID", "商品数量", "原始金额", "优惠金额", "实付金额"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel();
        viewButton = new JButton("查看详情");
        closeButton = new JButton("关闭");
        
        buttonPanel.add(viewButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 按钮监听器
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String orderId = (String) tableModel.getValueAt(selectedRow, 0);
                    Order order = orderService.getOrderById(orderId);
                    if (order != null) {
                        new OrderDetailFrame(order).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(OrderHistoryFrame.this, "请选择要查看的订单");
                }
            }
        });
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void updateOrderTable() {
        tableModel.setRowCount(0);
        for (Order order : orderService.getOrders()) {
            double totalDiscount = order.getCouponDiscount() + order.getFullReduction() + order.getVipDiscount();
            Object[] row = {
                order.getOrderId(),
                order.getItems().size(),
                order.getOriginalAmount(),
                totalDiscount,
                order.getFinalAmount()
            };
            tableModel.addRow(row);
        }
    }
}
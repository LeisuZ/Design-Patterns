package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.model.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrderDetailFrame extends JFrame {
    private Order order;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JLabel orderIdLabel;
    private JLabel originalAmountLabel;
    private JLabel couponDiscountLabel;
    private JLabel fullReductionLabel;
    private JLabel vipDiscountLabel;
    private JLabel finalAmountLabel;
    private JButton backButton;
    
    public OrderDetailFrame(Order order) {
        this.order = order;
        initUI();
    }
    
    private void initUI() {
        setTitle("订单详情");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 订单信息
        JPanel orderInfoPanel = new JPanel(new GridLayout(1, 1, 10, 10));
        orderInfoPanel.setBorder(BorderFactory.createTitledBorder("订单信息"));
        orderIdLabel = new JLabel("订单号: " + order.getOrderId());
        orderIdLabel.setFont(new Font("宋体", Font.BOLD, 14));
        orderInfoPanel.add(orderIdLabel);
        mainPanel.add(orderInfoPanel, BorderLayout.NORTH);
        
        // 商品列表
        String[] columnNames = {"商品ID", "商品名称", "单价", "数量", "小计"};
        tableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("商品列表"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 订单金额
        JPanel amountPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        amountPanel.setBorder(BorderFactory.createTitledBorder("订单金额"));
        
        amountPanel.add(new JLabel("原始金额:"));
        originalAmountLabel = new JLabel("¥" + String.format("%.2f", order.getOriginalAmount()));
        amountPanel.add(originalAmountLabel);
        
        amountPanel.add(new JLabel("优惠券:"));
        couponDiscountLabel = new JLabel("-¥" + String.format("%.2f", order.getCouponDiscount()));
        amountPanel.add(couponDiscountLabel);
        
        amountPanel.add(new JLabel("满减:"));
        fullReductionLabel = new JLabel("-¥" + String.format("%.2f", order.getFullReduction()));
        amountPanel.add(fullReductionLabel);
        
        amountPanel.add(new JLabel("VIP折扣:"));
        vipDiscountLabel = new JLabel("-¥" + String.format("%.2f", order.getVipDiscount()));
        amountPanel.add(vipDiscountLabel);
        
        amountPanel.add(new JLabel("实付金额:"));
        finalAmountLabel = new JLabel("¥" + String.format("%.2f", order.getFinalAmount()));
        finalAmountLabel.setFont(new Font("宋体", Font.BOLD, 16));
        amountPanel.add(finalAmountLabel);
        
        mainPanel.add(amountPanel, BorderLayout.SOUTH);
        
        // 返回按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("返回购物车");
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.WEST);
        
        add(mainPanel);
        
        // 填充表格数据
        populateTableData();
        
        // 按钮监听器
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CartFrame().setVisible(true);
                dispose();
            }
        });
    }
    
    private void populateTableData() {
        tableModel.setRowCount(0);
        for (CartItem item : order.getItems()) {
            Object[] row = {
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getSubtotal()
            };
            tableModel.addRow(row);
        }
    }
}
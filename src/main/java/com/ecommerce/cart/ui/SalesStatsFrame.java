package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.model.Product;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.service.OrderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesStatsFrame extends JFrame {
    private OrderService orderService;
    private JLabel totalOrdersLabel;
    private JLabel totalSalesLabel;
    private JLabel avgOrderLabel;
    private JLabel pendingLabel;
    private JLabel paidLabel;
    private JLabel shippedLabel;
    private JLabel completedLabel;
    private JLabel cancelledLabel;
    private JTable rankingTable;
    private DefaultTableModel rankingModel;
    private JTextField startDateField;
    private JTextField endDateField;
    private JButton backButton;

    public SalesStatsFrame() {
        orderService = ECommerceFacade.INSTANCE.getOrderService();
        initUI();
    }

    private void initUI() {
        setTitle("销售统计");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel statsPanel = new JPanel(new GridLayout(4, 2, 15, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("销售概览"));
        totalOrdersLabel = new JLabel("总订单数: 0");
        totalSalesLabel = new JLabel("总销售额: ¥0.00");
        avgOrderLabel = new JLabel("平均客单价: ¥0.00");
        pendingLabel = new JLabel("待支付: 0");
        paidLabel = new JLabel("已支付: 0");
        shippedLabel = new JLabel("已发货: 0");
        completedLabel = new JLabel("已完成: 0");
        cancelledLabel = new JLabel("已取消: 0");

        Font statsFont = new Font("宋体", Font.BOLD, 14);
        totalOrdersLabel.setFont(statsFont);
        totalSalesLabel.setFont(statsFont);
        totalSalesLabel.setForeground(new Color(200, 0, 0));
        avgOrderLabel.setFont(statsFont);

        statsPanel.add(totalOrdersLabel);
        statsPanel.add(totalSalesLabel);
        statsPanel.add(avgOrderLabel);
        statsPanel.add(cancelledLabel);
        statsPanel.add(pendingLabel);
        statsPanel.add(paidLabel);
        statsPanel.add(shippedLabel);
        statsPanel.add(completedLabel);
        mainPanel.add(statsPanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("开始日期(yyyy-MM-dd):"));
        startDateField = new JTextField(10);
        filterPanel.add(startDateField);
        filterPanel.add(new JLabel("结束日期(yyyy-MM-dd):"));
        endDateField = new JTextField(10);
        filterPanel.add(endDateField);
        JButton filterButton = new JButton("筛选");
        JButton resetButton = new JButton("重置");
        filterPanel.add(filterButton);
        filterPanel.add(resetButton);
        mainPanel.add(filterPanel, BorderLayout.CENTER);

        String[] columnNames = {"排名", "商品ID", "商品名称", "销量", "销售额"};
        rankingModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        rankingTable = new JTable(rankingModel);
        JScrollPane scrollPane = new JScrollPane(rankingTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("商品销量排行"));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButton = new JButton("返回");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStats();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startDateField.setText("");
                endDateField.setText("");
                updateStats();
            }
        });

        updateStats();
    }

    private void updateStats() {
        List<Order> orders = orderService.getOrders();
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();

        int totalCount = 0;
        double totalSales = 0;
        int pending = 0, paid = 0, shipped = 0, completed = 0, cancelled = 0;
        Map<String, int[]> productStats = new HashMap<>();

        for (Order order : orders) {
            if (!startDate.isEmpty() || !endDate.isEmpty()) {
                if (order.getCreateTime() == null) continue;
                String orderDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(order.getCreateTime());
                if (!startDate.isEmpty() && orderDateStr.compareTo(startDate) < 0) continue;
                if (!endDate.isEmpty() && orderDateStr.compareTo(endDate) > 0) continue;
            }
            
            totalCount++;
            String status = order.getStateName();
            switch (status) {
                case "待支付": pending++; break;
                case "已支付": paid++; break;
                case "已发货": shipped++; break;
                case "已完成": completed++; totalSales += order.getFinalAmount(); break;
                case "已取消": cancelled++; break;
            }

            for (CartItem item : order.getItems()) {
                String productId = item.getProduct().getId();
                String productName = item.getProduct().getName();
                int[] stats = productStats.getOrDefault(productId, new int[]{0, 0, 0});
                stats[0] += item.getQuantity();
                stats[1] = (int) ((stats[1]) + Math.round(item.getSubtotal()));
                stats[2] = productName.length();
                productStats.put(productId, new int[]{stats[0], stats[1], stats[2]});
            }
        }

        totalOrdersLabel.setText("总订单数: " + totalCount);
        totalSalesLabel.setText("总销售额: ¥" + String.format("%.2f", totalSales));
        avgOrderLabel.setText("平均客单价: ¥" + String.format("%.2f", completed > 0 ? totalSales / completed : 0));
        pendingLabel.setText("待支付: " + pending);
        paidLabel.setText("已支付: " + paid);
        shippedLabel.setText("已发货: " + shipped);
        completedLabel.setText("已完成: " + completed);
        cancelledLabel.setText("已取消: " + cancelled);

        rankingModel.setRowCount(0);
        productStats.entrySet().stream()
            .sorted((a, b) -> b.getValue()[0] - a.getValue()[0])
            .limit(20)
            .forEach(entry -> {
                String productId = entry.getKey();
                int quantity = entry.getValue()[0];
                int salesAmount = entry.getValue()[1];
                Product product = ECommerceFacade.INSTANCE.getProductService().getProductById(productId);
                String productName = product != null ? product.getName() : productId;
                Object[] row = {rankingModel.getRowCount() + 1, productId, productName, quantity, "¥" + String.format("%.2f", (double)salesAmount)};
                rankingModel.addRow(row);
            });
    }
    
    public void refreshData() {
        updateStats();
    }

    public JButton getBackButton() {
        return backButton;
    }
}

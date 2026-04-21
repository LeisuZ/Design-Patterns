package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Product;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MerchantFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private ProductService productService;
    private JTable productTable;
    private DefaultTableModel productTableModel;

    public MerchantFrame() {
        productService = ECommerceFacade.INSTANCE.getProductService();
        initUI();
    }

    private void initUI() {
        setTitle("商家管理系统");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("商品管理", createProductManagePanel());
        tabbedPane.addTab("销售统计", createSalesStatsPanel());
        tabbedPane.addTab("订单管理", createOrderManagePanel());
        tabbedPane.addTab("评价管理", createReviewManagePanel());

        add(tabbedPane);
    }

    private JPanel createProductManagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"商品ID", "商品名称", "分类", "价格"};
        productTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        for (Product p : productService.getAllProducts()) {
            Object[] row = {p.getId(), p.getName(), com.ecommerce.cart.util.CategoryUtil.toDisplayName(p.getCategory()), String.format("¥%.2f", p.getPrice())};
            productTableModel.addRow(row);
        }
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("新增商品");
        JButton editButton = new JButton("编辑商品");
        JButton deleteButton = new JButton("删除商品");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProductManageFrame pmf = new ProductManageFrame();
                pmf.showProductDialog(null);
                refreshProductTable();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String id = (String) productTableModel.getValueAt(selectedRow, 0);
                    Product product = productService.getProductById(id);
                    if (product != null) {
                        ProductManageFrame pmf = new ProductManageFrame();
                        pmf.showProductDialog(product);
                        refreshProductTable();
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "请选择要编辑的商品");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String id = (String) productTableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(panel, "确定要删除这个商品吗？");
                    if (confirm == JOptionPane.YES_OPTION) {
                        productService.deleteProduct(id);
                        ECommerceFacade.INSTANCE.getCartService().removeProduct(id);
                        refreshProductTable();
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "请选择要删除的商品");
                }
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshProductTable() {
        productTableModel.setRowCount(0);
        for (Product p : productService.getAllProducts()) {
            Object[] row = {p.getId(), p.getName(), com.ecommerce.cart.util.CategoryUtil.toDisplayName(p.getCategory()), String.format("¥%.2f", p.getPrice())};
            productTableModel.addRow(row);
        }
    }

    private JPanel createOrderManagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        com.ecommerce.cart.service.OrderService orderService = ECommerceFacade.INSTANCE.getOrderService();
        java.util.Set<String> merchantProductIds = new java.util.HashSet<>();
        for (Product p : productService.getAllProducts()) {
            merchantProductIds.add(p.getId());
        }

        String[] columnNames = {"订单ID", "下单时间", "商品数量", "原始金额", "优惠金额", "实付金额", "状态"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);

        Runnable refreshTable = new Runnable() {
            @Override
            public void run() {
                tableModel.setRowCount(0);
                for (com.ecommerce.cart.model.Order order : orderService.getOrders()) {
                    boolean hasMerchantProduct = false;
                    for (com.ecommerce.cart.model.CartItem item : order.getItems()) {
                        if (merchantProductIds.contains(item.getProduct().getId())) {
                            hasMerchantProduct = true;
                            break;
                        }
                    }
                    if (!hasMerchantProduct) continue;

                    double totalDiscount = order.getCouponDiscount() + order.getFullReduction() + order.getVipDiscount() + order.getGiftDiscount() + order.getTimeLimitDiscount();
                    String createTimeStr = order.getCreateTime() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(order.getCreateTime()) : "—";
                    Object[] row = {
                        order.getOrderId(),
                        createTimeStr,
                        order.getItems().size(),
                        order.getOriginalAmount(),
                        totalDiscount,
                        order.getFinalAmount(),
                        order.getStateName()
                    };
                    tableModel.addRow(row);
                }
            }
        };
        refreshTable.run();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshTable.run());
        buttonPanel.add(refreshButton);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createReviewManagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        com.ecommerce.cart.service.ReviewService reviewService = ECommerceFacade.INSTANCE.getReviewService();
        java.util.Set<String> merchantProductIds = new java.util.HashSet<>();
        for (Product p : productService.getAllProducts()) {
            merchantProductIds.add(p.getId());
        }

        String[] columnNames = {"评价ID", "商品ID", "商品名称", "用户", "评分", "评价内容", "订单ID"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);

        Runnable refreshTable = new Runnable() {
            @Override
            public void run() {
                tableModel.setRowCount(0);
                for (com.ecommerce.cart.model.Review review : reviewService.getReviews()) {
                    if (!merchantProductIds.contains(review.getProductId())) continue;
                    Object[] row = {
                        review.getId(),
                        review.getProductId(),
                        review.getProductName(),
                        review.getUserName(),
                        review.getRating() + "星",
                        review.getContent(),
                        review.getOrderId()
                    };
                    tableModel.addRow(row);
                }
            }
        };
        refreshTable.run();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshTable.run());
        buttonPanel.add(refreshButton);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSalesStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        com.ecommerce.cart.service.OrderService orderService = ECommerceFacade.INSTANCE.getOrderService();

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("开始日期(yyyy-MM-dd):"));
        JTextField startDateField = new JTextField(10);
        filterPanel.add(startDateField);
        filterPanel.add(new JLabel("结束日期(yyyy-MM-dd):"));
        JTextField endDateField = new JTextField(10);
        filterPanel.add(endDateField);
        JButton filterButton = new JButton("筛选");
        JButton resetButton = new JButton("重置");
        filterPanel.add(filterButton);
        filterPanel.add(resetButton);
        panel.add(filterPanel, BorderLayout.NORTH);

        JLabel totalOrdersLabel = new JLabel("总订单数: 0");
        JLabel totalSalesLabel = new JLabel("总销售额: ¥0.00");
        JLabel avgOrderLabel = new JLabel("平均客单价: ¥0.00");
        JLabel completedLabel = new JLabel("已完成: 0");

        Font statsFont = new Font("宋体", Font.BOLD, 14);
        totalOrdersLabel.setFont(statsFont);
        totalSalesLabel.setFont(statsFont);
        totalSalesLabel.setForeground(new Color(200, 0, 0));
        avgOrderLabel.setFont(statsFont);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("销售概览"));
        statsPanel.add(totalOrdersLabel);
        statsPanel.add(totalSalesLabel);
        statsPanel.add(avgOrderLabel);
        statsPanel.add(completedLabel);
        panel.add(statsPanel, BorderLayout.CENTER);

        String[] columnNames = {"排名", "商品ID", "商品名称", "销量", "销售额"};
        DefaultTableModel rankingModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable rankingTable = new JTable(rankingModel);
        JScrollPane scrollPane = new JScrollPane(rankingTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("商品销量排行"));
        panel.add(scrollPane, BorderLayout.SOUTH);

        Runnable updateStats = new Runnable() {
            @Override
            public void run() {
                java.util.List<com.ecommerce.cart.model.Order> orders = orderService.getOrders();
                String startDate = startDateField.getText().trim();
                String endDate = endDateField.getText().trim();

                int totalCount = 0;
                double totalSales = 0;
                int completed = 0;
                java.util.Map<String, int[]> productStats = new java.util.HashMap<>();

                for (com.ecommerce.cart.model.Order order : orders) {
                    if (!startDate.isEmpty() || !endDate.isEmpty()) {
                        if (order.getCreateTime() == null) continue;
                        String orderDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(order.getCreateTime());
                        if (!startDate.isEmpty() && orderDateStr.compareTo(startDate) < 0) continue;
                        if (!endDate.isEmpty() && orderDateStr.compareTo(endDate) > 0) continue;
                    }

                    totalCount++;
                    if ("已完成".equals(order.getStateName())) {
                        totalSales += order.getFinalAmount();
                        completed++;
                    }

                    for (com.ecommerce.cart.model.CartItem item : order.getItems()) {
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
                completedLabel.setText("已完成: " + completed);

                rankingModel.setRowCount(0);
                productStats.entrySet().stream()
                    .sorted((a, b) -> b.getValue()[0] - a.getValue()[0])
                    .limit(20)
                    .forEach(entry -> {
                        String productId = entry.getKey();
                        int quantity = entry.getValue()[0];
                        int salesAmount = entry.getValue()[1];
                        Product product = productService.getProductById(productId);
                        String productName = product != null ? product.getName() : productId;
                        Object[] row = {rankingModel.getRowCount() + 1, productId, productName, quantity, "¥" + String.format("%.2f", (double)salesAmount)};
                        rankingModel.addRow(row);
                    });
            }
        };

        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStats.run();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startDateField.setText("");
                endDateField.setText("");
                updateStats.run();
            }
        });

        updateStats.run();

        return panel;
    }
}

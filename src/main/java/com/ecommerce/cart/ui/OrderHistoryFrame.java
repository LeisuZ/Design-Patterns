package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.pattern.observer.EventBus;
import com.ecommerce.cart.pattern.observer.Observer;
import com.ecommerce.cart.service.OrderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class OrderHistoryFrame extends JFrame implements Observer {
    private OrderService orderService;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusComboBox;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton detailButton;
    private JButton backButton;
    private CartFrame parentCartFrame;
    
    public OrderHistoryFrame(CartFrame parentCartFrame) {
        this.parentCartFrame = parentCartFrame;
        this.orderService = ECommerceFacade.INSTANCE.getOrderService();
        initUI();
    }
    
    private void initUI() {
        setTitle("订单历史");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("筛选条件"));
        
        filterPanel.add(new JLabel("订单状态:"));
        statusComboBox = new JComboBox<>(new String[]{"全部", "待支付", "已支付", "已发货", "已完成", "已取消"});
        filterPanel.add(statusComboBox);
        
        filterPanel.add(new JLabel("开始日期:"));
        JTextField startDateField = new JTextField(10);
        filterPanel.add(startDateField);
        filterPanel.add(new JLabel("结束日期:"));
        JTextField endDateField = new JTextField(10);
        filterPanel.add(endDateField);
        
        filterPanel.add(new JLabel("关键词:"));
        JTextField keywordField = new JTextField(15);
        filterPanel.add(keywordField);
        
        searchButton = new JButton("搜索");
        refreshButton = new JButton("刷新");
        filterPanel.add(searchButton);
        filterPanel.add(refreshButton);
        
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
        // 订单表格
        String[] columnNames = {"订单号", "下单时间", "订单状态", "原始金额", "实付金额", "会员信息"};
        tableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("订单列表"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        detailButton = new JButton("查看详情");
        backButton = new JButton("返回购物车");
        buttonPanel.add(detailButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 填充表格数据
        populateTableData();
        EventBus.getInstance().registerObserver("ORDER_STATUS_CHANGED", this);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                EventBus.getInstance().removeObserver("ORDER_STATUS_CHANGED", OrderHistoryFrame.this);
            }
        });
        
        // 按钮监听器
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String status = (String) statusComboBox.getSelectedItem();
                String startDate = startDateField.getText().trim();
                String endDate = endDateField.getText().trim();
                String keyword = keywordField.getText().trim().toLowerCase();
                populateTableDataFiltered(status, startDate, endDate, keyword);
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                populateTableData();
            }
        });
        
        detailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String orderId = (String) tableModel.getValueAt(selectedRow, 0);
                    Order order = orderService.getOrderById(orderId);
                    if (order != null) {
                        new OrderDetailFrame(order, OrderHistoryFrame.this).setVisible(true);
                        setVisible(false);
                    }
                } else {
                    JOptionPane.showMessageDialog(OrderHistoryFrame.this, "请选择一个订单", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parentCartFrame != null) parentCartFrame.setVisible(true);
                dispose();
            }
        });
    }
    
    // 填充所有订单数据
    private void populateTableData() {
        tableModel.setRowCount(0);
        List<Order> orders = orderService.getOrders();
        for (Order order : orders) {
            String memberInfo = order.getMemberName() != null ? order.getMemberName() + " (" + order.getMemberId() + ")" : "非会员";
            String createTimeStr = order.getCreateTime() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(order.getCreateTime()) : "—";
            Object[] row = {
                order.getOrderId(),
                createTimeStr,
                order.getStateName(),
                "¥" + String.format("%.2f", order.getOriginalAmount()),
                "¥" + String.format("%.2f", order.getFinalAmount()),
                memberInfo
            };
            tableModel.addRow(row);
        }
    }
    
    private void populateTableDataByStatus(String status) {
        tableModel.setRowCount(0);
        List<Order> orders = orderService.getOrdersByStatus(status);
        for (Order order : orders) {
            String memberInfo = order.getMemberName() != null ? order.getMemberName() + " (" + order.getMemberId() + ")" : "非会员";
            String createTimeStr = order.getCreateTime() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(order.getCreateTime()) : "—";
            Object[] row = {
                order.getOrderId(),
                createTimeStr,
                order.getStateName(),
                "¥" + String.format("%.2f", order.getOriginalAmount()),
                "¥" + String.format("%.2f", order.getFinalAmount()),
                memberInfo
            };
            tableModel.addRow(row);
        }
    }
    
    private void populateTableDataFiltered(String status, String startDate, String endDate, String keyword) {
        tableModel.setRowCount(0);
        List<Order> orders = orderService.getOrders();
        for (Order order : orders) {
            if (!"全部".equals(status) && !order.getStateName().equals(status)) continue;
            
            if ((!startDate.isEmpty() || !endDate.isEmpty()) && order.getCreateTime() != null) {
                String orderDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(order.getCreateTime());
                if (!startDate.isEmpty() && orderDateStr.compareTo(startDate) < 0) continue;
                if (!endDate.isEmpty() && orderDateStr.compareTo(endDate) > 0) continue;
            }
            
            if (!keyword.isEmpty()) {
                boolean match = order.getOrderId().toLowerCase().contains(keyword);
                if (!match) {
                    for (com.ecommerce.cart.model.CartItem item : order.getItems()) {
                        if (item.getProduct().getName().toLowerCase().contains(keyword)) {
                            match = true;
                            break;
                        }
                    }
                }
                if (!match) continue;
            }
            
            String memberInfo = order.getMemberName() != null ? order.getMemberName() + " (" + order.getMemberId() + ")" : "非会员";
            String createTimeStr = order.getCreateTime() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(order.getCreateTime()) : "—";
            Object[] row = {
                order.getOrderId(),
                createTimeStr,
                order.getStateName(),
                "¥" + String.format("%.2f", order.getOriginalAmount()),
                "¥" + String.format("%.2f", order.getFinalAmount()),
                memberInfo
            };
            tableModel.addRow(row);
        }
    }

    @Override
    public void update(String eventType, Object data) {
        if ("ORDER_STATUS_CHANGED".equals(eventType)) {
            SwingUtilities.invokeLater(() -> populateTableData());
        }
    }
}
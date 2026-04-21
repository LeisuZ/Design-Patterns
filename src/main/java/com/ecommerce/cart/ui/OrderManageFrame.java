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

public class OrderManageFrame extends JFrame implements Observer {
    private OrderService orderService;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JButton viewButton;
    private JButton updateStatusButton;
    private JButton cancelButton;
    private JButton shipButton;
    private JButton confirmButton;
    private JButton backButton;
    private JComboBox<String> statusComboBox;
    
    public OrderManageFrame() {
        orderService = ECommerceFacade.INSTANCE.getOrderService();
        initUI();
    }
    
    private void initUI() {
        setTitle("订单管理");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 表格部分
        String[] columnNames = {"订单ID", "下单时间", "商品数量", "原始金额", "优惠金额", "实付金额", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("订单列表"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 操作面板
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(new JLabel("订单状态:"));
        statusComboBox = new JComboBox<>(new String[]{"全部", "待支付", "已支付", "已发货", "已完成", "已取消"});
        actionPanel.add(statusComboBox);
        statusComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedStatus = (String) statusComboBox.getSelectedItem();
                if ("全部".equals(selectedStatus)) {
                    populateTableData();
                } else {
                    populateTableDataByStatus(selectedStatus);
                }
            }
        });
        mainPanel.add(actionPanel, BorderLayout.NORTH);
        
        // 按钮组
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        viewButton = new JButton("查看详情");
        updateStatusButton = new JButton("更新状态");
        cancelButton = new JButton("取消订单");
        shipButton = new JButton("发货");
        confirmButton = new JButton("确认收货");
        backButton = new JButton("返回");
        
        buttonPanel.add(viewButton);
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(shipButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 填充表格数据
        populateTableData();
        EventBus.getInstance().registerObserver("ORDER_STATUS_CHANGED", this);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                EventBus.getInstance().removeObserver("ORDER_STATUS_CHANGED", OrderManageFrame.this);
            }
        });
        
        // 按钮监听器
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String orderId = (String) tableModel.getValueAt(selectedRow, 0);
                    Order order = orderService.getOrderById(orderId);
                    if (order != null) {
                        new OrderDetailFrame(order, OrderManageFrame.this).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(OrderManageFrame.this, "请选择要查看的订单");
                }
            }
        });
        
        updateStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String orderId = (String) tableModel.getValueAt(selectedRow, 0);
                    Order order = orderService.getOrderById(orderId);
                    if (order == null) return;
                    String[] operations = {"支付订单", "发货", "确认收货", "取消订单"};
                    String selectedOp = (String) JOptionPane.showInputDialog(
                        OrderManageFrame.this, "选择要执行的操作:", "更新订单状态",
                        JOptionPane.QUESTION_MESSAGE, null, operations, operations[0]);
                    if (selectedOp == null) return;
                    switch (selectedOp) {
                        case "支付订单":
                            String[] paymentMethods = ECommerceFacade.INSTANCE.getPaymentService().getAvailablePaymentMethods();
                            String payMethod = (String) JOptionPane.showInputDialog(
                                OrderManageFrame.this, "选择支付方式:", "支付",
                                JOptionPane.QUESTION_MESSAGE, null, paymentMethods, paymentMethods[0]);
                            if (payMethod != null) {
                                String paymentType = ECommerceFacade.INSTANCE.getPaymentService().getPaymentType(payMethod);
                                orderService.payOrder(orderId, paymentType);
                            }
                            break;
                        case "发货":
                            String trackingNumber = JOptionPane.showInputDialog(OrderManageFrame.this, "请输入物流单号:");
                            if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
                                String courierCompany = JOptionPane.showInputDialog(OrderManageFrame.this, "请输入快递公司:");
                                if (courierCompany != null && !courierCompany.trim().isEmpty()) {
                                    orderService.shipOrder(orderId, trackingNumber, courierCompany);
                                }
                            }
                            break;
                        case "确认收货":
                            orderService.confirmDelivery(orderId);
                            break;
                        case "取消订单":
                            orderService.cancelOrder(orderId);
                            break;
                    }
                    orderService.saveOrders();
                    populateTableData();
                } else {
                    JOptionPane.showMessageDialog(OrderManageFrame.this, "请选择要更新的订单");
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String orderId = (String) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(OrderManageFrame.this, "确定要取消这个订单吗？");
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = orderService.cancelOrder(orderId);
                        if (success) {
                            JOptionPane.showMessageDialog(OrderManageFrame.this, "订单取消成功");
                            populateTableData();
                        } else {
                            JOptionPane.showMessageDialog(OrderManageFrame.this, "订单取消失败");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(OrderManageFrame.this, "请选择要取消的订单");
                }
            }
        });
        
        shipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String orderId = (String) tableModel.getValueAt(selectedRow, 0);
                    JDialog dialog = new JDialog(OrderManageFrame.this, "发货", true);
                    dialog.setSize(400, 200);
                    dialog.setLocationRelativeTo(OrderManageFrame.this);
                    
                    JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
                    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    
                    JTextField trackingNumberField = new JTextField();
                    JTextField courierCompanyField = new JTextField();
                    
                    panel.add(new JLabel("物流单号:"));
                    panel.add(trackingNumberField);
                    panel.add(new JLabel("快递公司:"));
                    panel.add(courierCompanyField);
                    
                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    JButton confirmButton = new JButton("确认发货");
                    JButton cancelButton = new JButton("取消");
                    
                    buttonPanel.add(confirmButton);
                    buttonPanel.add(cancelButton);
                    
                    dialog.add(panel, BorderLayout.CENTER);
                    dialog.add(buttonPanel, BorderLayout.SOUTH);
                    
                    confirmButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String trackingNumber = trackingNumberField.getText();
                            String courierCompany = courierCompanyField.getText();
                            if (trackingNumber.isEmpty() || courierCompany.isEmpty()) {
                                JOptionPane.showMessageDialog(dialog, "请填写物流单号和快递公司");
                                return;
                            }
                            boolean success = orderService.shipOrder(orderId, trackingNumber, courierCompany);
                            if (success) {
                                JOptionPane.showMessageDialog(dialog, "发货成功");
                                dialog.dispose();
                                populateTableData();
                            } else {
                                JOptionPane.showMessageDialog(dialog, "发货失败");
                            }
                        }
                    });
                    
                    cancelButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dialog.dispose();
                        }
                    });
                    
                    dialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(OrderManageFrame.this, "请选择要发货的订单");
                }
            }
        });
        
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String orderId = (String) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(OrderManageFrame.this, "确定要确认收货吗？");
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = orderService.confirmDelivery(orderId);
                        if (success) {
                            JOptionPane.showMessageDialog(OrderManageFrame.this, "收货确认成功");
                            populateTableData();
                        } else {
                            JOptionPane.showMessageDialog(OrderManageFrame.this, "收货确认失败");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(OrderManageFrame.this, "请选择要确认收货的订单");
                }
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void populateTableData() {
        tableModel.setRowCount(0);
        for (Order order : orderService.getOrders()) {
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

    private void populateTableDataByStatus(String status) {
        tableModel.setRowCount(0);
        for (Order order : orderService.getOrders()) {
            if (!order.getStateName().equals(status)) continue;
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
    
    public void refreshData() {
        populateTableData();
    }

    public JButton getBackButton() {
        return backButton;
    }

    @Override
    public void update(String eventType, Object data) {
        if ("ORDER_STATUS_CHANGED".equals(eventType)) {
            SwingUtilities.invokeLater(() -> populateTableData());
        }
    }
}
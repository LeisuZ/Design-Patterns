package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Address;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.model.Member;
import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.pattern.observer.EventBus;
import com.ecommerce.cart.pattern.observer.Observer;
import com.ecommerce.cart.service.LogisticsService;
import com.ecommerce.cart.service.MemberService;
import com.ecommerce.cart.service.OrderService;
import com.ecommerce.cart.service.ReviewService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrderDetailFrame extends JFrame implements Observer {
    private Order order;
    private LogisticsService logisticsService;
    private MemberService memberService;
    private OrderService orderService;
    private ReviewService reviewService;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JLabel orderIdLabel;
    private JLabel originalAmountLabel;
    private JLabel couponDiscountLabel;
    private JLabel fullReductionLabel;
    private JLabel vipDiscountLabel;
    private JLabel finalAmountLabel;
    private JLabel logisticsStatusLabel;
    private JLabel orderStatusLabel;
    private JPanel buttonPanel;
    private JButton backButton;
    private JButton reviewButton;
    private JButton logisticsButton;
    private JButton payButton;
    private JButton shipButton;
    private JButton completeButton;
    private JButton cancelButton;
    private JButton undoPayButton;
    private JPanel timelinePanel;
    private JPanel centerPanel;
    private JLabel stateTransitionLabel;
    private String previousStatus;
    
    private JFrame parentFrame;
    
    public OrderDetailFrame(Order order, JFrame parent) {
        this.order = order;
        this.parentFrame = parent;
        ECommerceFacade facade = ECommerceFacade.INSTANCE;
        this.logisticsService = facade.getLogisticsService();
        this.memberService = facade.getMemberService();
        this.orderService = facade.getOrderService();
        this.reviewService = facade.getReviewService();
        initUI();
    }
    
    private void initUI() {
        setTitle("订单详情");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 订单信息
        JPanel orderInfoPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        orderInfoPanel.setBorder(BorderFactory.createTitledBorder("订单信息"));
        orderIdLabel = new JLabel("订单号: " + (order.getOrderId() != null ? order.getOrderId() : "未知"));
        orderIdLabel.setFont(new Font("宋体", Font.BOLD, 14));
        orderInfoPanel.add(orderIdLabel);
        
        orderStatusLabel = new JLabel("订单状态: " + order.getStateName());
        orderStatusLabel.setFont(new Font("宋体", Font.BOLD, 14));
        orderInfoPanel.add(orderStatusLabel);
        
        stateTransitionLabel = new JLabel("当前状态: " + order.getStateName() + " [状态模式]");
        stateTransitionLabel.setFont(new Font("宋体", Font.PLAIN, 12));
        stateTransitionLabel.setForeground(new Color(0, 0, 200));
        orderInfoPanel.add(stateTransitionLabel);
        previousStatus = order.getStateName();
        
        logisticsStatusLabel = new JLabel("物流状态: " + (order.getOrderId() != null ? logisticsService.getLogisticsStatus(order.getOrderId()) : "未发货"));
        orderInfoPanel.add(logisticsStatusLabel);
        
        Address address = order.getAddress();
        JLabel addressLabel = new JLabel("收货地址: " + (address != null ? address.getName() + " " + address.getPhone() + " " + address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress() : "未填写"));
        addressLabel.setFont(new Font("宋体", Font.PLAIN, 12));
        orderInfoPanel.add(addressLabel);
        mainPanel.add(orderInfoPanel, BorderLayout.NORTH);
        
        // 商品列表
        String[] columnNames = {"商品ID", "商品名称", "单价", "数量", "小计"};
        tableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("商品列表"));
        
        centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(scrollPane, BorderLayout.NORTH);
        
        refreshLogisticsTimeline();
        
        // 评价展示
        java.util.List<com.ecommerce.cart.model.Review> orderReviews = order.getOrderId() != null ? reviewService.getReviewsByOrderId(order.getOrderId()) : new java.util.ArrayList<>();
        if (!orderReviews.isEmpty()) {
            JPanel reviewPanel = new JPanel(new BorderLayout(5, 5));
            reviewPanel.setBorder(BorderFactory.createTitledBorder("订单评价"));
            StringBuilder reviewText = new StringBuilder();
            for (com.ecommerce.cart.model.Review r : orderReviews) {
                reviewText.append("商品: ").append(r.getProductName())
                    .append(" | 评分: ").append(r.getRating()).append("星")
                    .append(" | 评价: ").append(r.getContent()).append("\n");
            }
            JTextArea reviewArea = new JTextArea(reviewText.toString());
            reviewArea.setEditable(false);
            reviewArea.setLineWrap(true);
            reviewArea.setWrapStyleWord(true);
            reviewArea.setRows(3);
            reviewPanel.add(new JScrollPane(reviewArea), BorderLayout.CENTER);
            centerPanel.add(reviewPanel, BorderLayout.SOUTH);
        }
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // 订单金额
        JPanel amountPanel = new JPanel(new GridLayout(7, 2, 10, 10));
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
        
        amountPanel.add(new JLabel("满赠折扣:"));
        JLabel giftDiscountLabel = new JLabel("-¥" + String.format("%.2f", order.getGiftDiscount()));
        amountPanel.add(giftDiscountLabel);
        
        amountPanel.add(new JLabel("限时折扣:"));
        JLabel timeLimitDiscountLabel = new JLabel("-¥" + String.format("%.2f", order.getTimeLimitDiscount()));
        amountPanel.add(timeLimitDiscountLabel);
        
        amountPanel.add(new JLabel("实付金额:"));
        finalAmountLabel = new JLabel("¥" + String.format("%.2f", order.getFinalAmount()));
        finalAmountLabel.setFont(new Font("宋体", Font.BOLD, 16));
        amountPanel.add(finalAmountLabel);
        
        mainPanel.add(amountPanel, BorderLayout.SOUTH);
        
        // 按钮面板
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        backButton = new JButton("返回购物车");
        reviewButton = new JButton("评价商品");
        logisticsButton = new JButton("查看物流");
        payButton = new JButton("支付订单");
        shipButton = new JButton("发货");
        completeButton = new JButton("确认收货");
        cancelButton = new JButton("取消订单");
        undoPayButton = new JButton("撤销支付");
        
        // 根据订单状态显示不同的按钮
        String orderStatus = order.getStateName();
        switch (orderStatus) {
            case "待支付":
                buttonPanel.add(payButton);
                buttonPanel.add(cancelButton);
                break;
            case "已支付":
                buttonPanel.add(shipButton);
                buttonPanel.add(undoPayButton);
                buttonPanel.add(cancelButton);
                break;
            case "已发货":
                buttonPanel.add(completeButton);
                break;
            case "已完成":
                buttonPanel.add(reviewButton);
                break;
        }
        
        buttonPanel.add(logisticsButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 填充表格数据
        populateTableData();
        
        order.registerObserver(this);
        EventBus.getInstance().registerObserver("ORDER_STATUS_CHANGED", this);
        
        // 按钮监听器
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                }
                dispose();
            }
        });
        
        reviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 显示评价对话框
                showReviewDialog();
            }
        });
        
        logisticsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                com.ecommerce.cart.model.Logistics logistics = logisticsService.getLogisticsByOrderId(order.getOrderId());
                new LogisticsDetailDialog(OrderDetailFrame.this, order, logistics).setVisible(true);
            }
        });
        
        // 支付订单按钮监听器
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] paymentMethods = ECommerceFacade.INSTANCE.getPaymentService().getAvailablePaymentMethods();
                if (paymentMethods.length == 0) {
                    JOptionPane.showMessageDialog(OrderDetailFrame.this, "没有可用的支付方式");
                    return;
                }
                String selectedMethod = (String) JOptionPane.showInputDialog(
                    OrderDetailFrame.this, "选择支付方式:", "支付",
                    JOptionPane.QUESTION_MESSAGE, null, paymentMethods, paymentMethods[0]);
                if (selectedMethod != null) {
                    String paymentType = ECommerceFacade.INSTANCE.getPaymentService().getPaymentType(selectedMethod);
                    boolean paymentSuccess = ECommerceFacade.INSTANCE.getPaymentService().processPayment(order, paymentType);
                    if (paymentSuccess) {
                        order.setPaymentType(paymentType);
                        order.pay();
                        orderService.saveOrders();
                        refreshUI();
                        JOptionPane.showMessageDialog(OrderDetailFrame.this, "支付成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(OrderDetailFrame.this, "支付失败，请重试", "提示", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        // 发货按钮监听器
        shipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String trackingNumber = JOptionPane.showInputDialog(OrderDetailFrame.this, "请输入物流单号:");
                String courierCompany = JOptionPane.showInputDialog(OrderDetailFrame.this, "请输入快递公司:");
                if (trackingNumber != null && courierCompany != null) {
                    orderService.shipOrder(order.getOrderId(), trackingNumber, courierCompany);
                    refreshUI();
                    JOptionPane.showMessageDialog(OrderDetailFrame.this, "发货成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        // 确认收货按钮监听器
        completeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orderService.confirmDelivery(order.getOrderId());
                refreshUI();
                JOptionPane.showMessageDialog(OrderDetailFrame.this, "确认收货成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // 取消订单按钮监听器
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(OrderDetailFrame.this, "确定要取消订单吗？", "确认取消", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    orderService.cancelOrder(order.getOrderId());
                    JOptionPane.showMessageDialog(OrderDetailFrame.this, "订单已取消！", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        undoPayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(OrderDetailFrame.this, "确定要撤销支付吗？将执行退款并取消订单", "确认撤销", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    String refundType = order.getPaymentType() != null ? order.getPaymentType() : "alipay";
                    ECommerceFacade.INSTANCE.getPaymentService().processRefund(order, refundType);
                    order.cancel();
                    orderService.saveOrders();
                    refreshUI();
                    JOptionPane.showMessageDialog(OrderDetailFrame.this, "支付已撤销，订单已取消", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                try {
                    EventBus.getInstance().removeObserver("ORDER_STATUS_CHANGED", OrderDetailFrame.this);
                } catch (Exception ex) {
                    // ignore
                }
            }
        });
    }
    
    @Override
    public void update(String eventType, Object data) {
        if ("ORDER_STATUS_CHANGED".equals(eventType)) {
            SwingUtilities.invokeLater(() -> refreshUI());
        }
    }
    
    private void refreshUI() {
        orderStatusLabel.setText("订单状态: " + order.getStateName());
        logisticsStatusLabel.setText("物流状态: " + logisticsService.getLogisticsStatus(order.getOrderId()));
        String currentState = order.getStateName();
        stateTransitionLabel.setText("状态转换: " + previousStatus + " → " + currentState + " [状态模式]");
        previousStatus = currentState;
        
        buttonPanel.removeAll();
        String orderStatus = order.getStateName();
        switch (orderStatus) {
            case "待支付":
                buttonPanel.add(payButton);
                buttonPanel.add(cancelButton);
                break;
            case "已支付":
                buttonPanel.add(shipButton);
                buttonPanel.add(undoPayButton);
                buttonPanel.add(cancelButton);
                break;
            case "已发货":
                buttonPanel.add(completeButton);
                break;
            case "已完成":
                buttonPanel.add(reviewButton);
                break;
        }
        buttonPanel.add(logisticsButton);
        buttonPanel.add(backButton);
        buttonPanel.revalidate();
        buttonPanel.repaint();
        
        refreshLogisticsTimeline();
    }
    
    private void refreshLogisticsTimeline() {
        if (timelinePanel != null) {
            centerPanel.remove(timelinePanel);
        }
        com.ecommerce.cart.model.Logistics logistics = order.getOrderId() != null ? logisticsService.getLogisticsByOrderId(order.getOrderId()) : null;
        timelinePanel = new JPanel(new GridLayout(4, 2, 5, 5));
        timelinePanel.setBorder(BorderFactory.createTitledBorder("物流时间线"));
        timelinePanel.add(new JLabel("下单时间:"));
        timelinePanel.add(new JLabel(order.getCreateTime() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(order.getCreateTime()) : "—"));
        timelinePanel.add(new JLabel("支付时间:"));
        timelinePanel.add(new JLabel("—"));
        timelinePanel.add(new JLabel("发货时间:"));
        timelinePanel.add(new JLabel(logistics != null && logistics.getUpdateTime() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(logistics.getUpdateTime()) : "—"));
        timelinePanel.add(new JLabel("快递单号:"));
        timelinePanel.add(new JLabel(logistics != null && logistics.getTrackingNumber() != null ? logistics.getCourierCompany() + " " + logistics.getTrackingNumber() : "—"));
        centerPanel.add(timelinePanel, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
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
    
    private void showReviewDialog() {
        JDialog dialog = new JDialog(this, "评价商品", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 商品选择
        JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        productPanel.add(new JLabel("选择商品:"));
        JComboBox<String> productComboBox = new JComboBox<>();
        for (CartItem item : order.getItems()) {
            productComboBox.addItem(item.getProduct().getName() + " (" + item.getProduct().getId() + ")");
        }
        productPanel.add(productComboBox);
        mainPanel.add(productPanel, BorderLayout.NORTH);
        
        // 评分选择
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingPanel.add(new JLabel("评分:"));
        JComboBox<Integer> ratingComboBox = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
        ratingComboBox.setSelectedItem(5);
        ratingPanel.add(ratingComboBox);
        mainPanel.add(ratingPanel, BorderLayout.CENTER);
        
        // 评价内容
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(new JLabel("评价内容:"), BorderLayout.NORTH);
        JTextArea contentTextArea = new JTextArea(5, 30);
        contentTextArea.setLineWrap(true);
        contentTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(contentTextArea);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.SOUTH);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton confirmButton = new JButton("确认评价");
        JButton cancelButton = new JButton("取消");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedProduct = (String) productComboBox.getSelectedItem();
                String productId = selectedProduct.substring(selectedProduct.lastIndexOf("(") + 1, selectedProduct.lastIndexOf(")"));
                String productName = selectedProduct.substring(0, selectedProduct.lastIndexOf(" ("));
                int rating = (int) ratingComboBox.getSelectedItem();
                String content = contentTextArea.getText();
                
                // 使用订单中的会员信息
                String userId = order.getMemberId() != null ? order.getMemberId() : "M001";
                String userName = order.getMemberName() != null ? order.getMemberName() : "张三";
                
                // 添加评价
                reviewService.addReview(productId, productName, userId, userName, rating, content, order.getOrderId());
                
                JOptionPane.showMessageDialog(dialog, "评价成功！");
                dialog.dispose();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        dialog.setVisible(true);
    }
}
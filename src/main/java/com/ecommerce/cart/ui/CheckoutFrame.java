package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.Coupon;
import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.chain.CouponHandler;
import com.ecommerce.cart.pattern.chain.FullReductionHandler;
import com.ecommerce.cart.pattern.chain.Handler;
import com.ecommerce.cart.pattern.chain.VipHandler;
import com.ecommerce.cart.pattern.factory.StrategyFactory;
import com.ecommerce.cart.service.OrderService;
import com.ecommerce.cart.service.PaymentService;
import com.ecommerce.cart.service.PromotionService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckoutFrame extends JFrame {
    private Cart cart;
    private PromotionService promotionService;
    private OrderService orderService;
    private PaymentService paymentService;
    private Order order;
    
    private JComboBox<Coupon> couponComboBox;
    private JComboBox<String> paymentComboBox;
    private JButton calculateButton;
    private JButton submitButton;
    private JLabel originalAmountLabel;
    private JLabel couponDiscountLabel;
    private JLabel fullReductionLabel;
    private JLabel vipDiscountLabel;
    private JLabel totalLabel;
    
    public CheckoutFrame(Cart cart, PromotionService promotionService) {
        this.cart = cart;
        this.promotionService = promotionService;
        this.orderService = new OrderService();
        this.paymentService = new PaymentService();
        initUI();
    }
    
    private void initUI() {
        setTitle("结算页面");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 优惠券选择
        JPanel couponPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        couponPanel.add(new JLabel("选择优惠券:"));
        couponComboBox = new JComboBox<>();
        for (Coupon coupon : promotionService.getCoupons()) {
            couponComboBox.addItem(coupon);
        }
        couponPanel.add(couponComboBox);
        
        // 支付方式选择
        paymentComboBox = new JComboBox<>();
        for (String paymentMethod : paymentService.getAvailablePaymentMethods()) {
            paymentComboBox.addItem(paymentMethod);
        }
        couponPanel.add(new JLabel("  支付方式:"));
        couponPanel.add(paymentComboBox);
        
        mainPanel.add(couponPanel, BorderLayout.NORTH);
        
        // 明细展示
        JPanel detailPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        detailPanel.setBorder(BorderFactory.createTitledBorder("优惠明细"));
        
        detailPanel.add(new JLabel("原始金额:"));
        originalAmountLabel = new JLabel("¥" + String.format("%.2f", cart.getTotal()));
        detailPanel.add(originalAmountLabel);
        
        detailPanel.add(new JLabel("优惠券:"));
        couponDiscountLabel = new JLabel("计算中...");
        detailPanel.add(couponDiscountLabel);
        
        detailPanel.add(new JLabel("满减:"));
        fullReductionLabel = new JLabel("计算中...");
        detailPanel.add(fullReductionLabel);
        
        detailPanel.add(new JLabel("VIP折扣:"));
        vipDiscountLabel = new JLabel("计算中...");
        detailPanel.add(vipDiscountLabel);
        
        detailPanel.add(new JLabel("实付金额:"));
        totalLabel = new JLabel("计算中...");
        totalLabel.setFont(new Font("宋体", Font.BOLD, 16));
        detailPanel.add(totalLabel);
        
        mainPanel.add(detailPanel, BorderLayout.CENTER);
        
        // 按钮组
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        calculateButton = new JButton("计算优惠");
        submitButton = new JButton("提交订单");
        submitButton.setEnabled(false);
        
        buttonPanel.add(calculateButton);
        buttonPanel.add(submitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 按钮监听器
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateDiscounts();
            }
        });
        
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitOrder();
            }
        });
    }
    
    private void calculateDiscounts() {
        // 1. 创建订单对象
        Coupon selectedCoupon = (Coupon) couponComboBox.getSelectedItem();
        order = orderService.createOrder(cart, selectedCoupon);
        
        // 2. 重置UI显示
        couponDiscountLabel.setText("计算中...");
        fullReductionLabel.setText("计算中...");
        vipDiscountLabel.setText("计算中...");
        totalLabel.setText("计算中...");
        
        // 3. 使用SwingWorker执行职责链计算
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // 构建职责链
                // 这里是职责链节点 - 优惠券处理器
                Handler couponHandler = new CouponHandler(StrategyFactory.createStrategy("COUPON"));
                // 这里是职责链节点 - 满减处理器
                Handler fullReductionHandler = new FullReductionHandler(StrategyFactory.createStrategy("FULL_REDUCTION"));
                // 这里是职责链节点 - VIP处理器
                Handler vipHandler = new VipHandler(StrategyFactory.createStrategy("VIP"));
                
                // 设置链的顺序
                couponHandler.setNext(fullReductionHandler);
                fullReductionHandler.setNext(vipHandler);
                
                // 执行职责链计算
                couponHandler.handle(order);
                
                return null;
            }
            
            @Override
            protected void done() {
                // 4. 分阶段更新UI，演示职责链流转
                try {
                    // 显示优惠券折扣
                    SwingUtilities.invokeLater(() -> {
                        couponDiscountLabel.setText("-¥" + String.format("%.2f", order.getCouponDiscount()));
                    });
                    Thread.sleep(500); // 间隔0.5秒
                    
                    // 显示满减折扣
                    SwingUtilities.invokeLater(() -> {
                        fullReductionLabel.setText("-¥" + String.format("%.2f", order.getFullReduction()));
                    });
                    Thread.sleep(500); // 间隔0.5秒
                    
                    // 显示VIP折扣和最终金额
                    SwingUtilities.invokeLater(() -> {
                        vipDiscountLabel.setText("-¥" + String.format("%.2f", order.getVipDiscount()));
                        totalLabel.setText("¥" + String.format("%.2f", order.getFinalAmount()));
                        submitButton.setEnabled(true);
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
    
    private void submitOrder() {
        // 获取选择的支付方式
        String paymentMethod = (String) paymentComboBox.getSelectedItem();
        String paymentType = paymentService.getPaymentType(paymentMethod);
        
        // 处理支付
        boolean paymentSuccess = paymentService.processPayment(order, paymentType);
        
        if (paymentSuccess) {
            // 支付成功，提交订单
            orderService.submitOrder(order);
            JOptionPane.showMessageDialog(this, "订单提交成功！订单号: " + order.getOrderId());
            new OrderDetailFrame(order).setVisible(true);
            dispose();
        } else {
            // 支付失败
            JOptionPane.showMessageDialog(this, "支付失败，请重试");
        }
    }
    

}
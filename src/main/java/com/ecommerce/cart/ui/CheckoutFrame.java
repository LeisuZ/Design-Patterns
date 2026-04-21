package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.Coupon;
import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.chain.CouponHandler;
import com.ecommerce.cart.pattern.chain.FullReductionHandler;
import com.ecommerce.cart.pattern.chain.Handler;
import com.ecommerce.cart.pattern.chain.VipHandler;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.pattern.strategy.StrategyContext;
import com.ecommerce.cart.model.Address;
import com.ecommerce.cart.model.Member;
import com.ecommerce.cart.service.AddressService;
import com.ecommerce.cart.service.MemberService;
import com.ecommerce.cart.service.OrderService;
import com.ecommerce.cart.service.PaymentService;
import com.ecommerce.cart.service.PromotionService;
import com.ecommerce.cart.pattern.chain.DiscountChainBuilder;
import com.ecommerce.cart.model.OrderBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListCellRenderer;

public class CheckoutFrame extends JFrame {
    private Cart cart;
    private PromotionService promotionService;
    private OrderService orderService;
    private PaymentService paymentService;
    private AddressService addressService;
    private MemberService memberService;
    private Order order;
    private Member currentMember;
    private CartFrame parentCartFrame;
    
    private JComboBox<Coupon> couponComboBox;
    private JComboBox<String> paymentComboBox;
    private JComboBox<Address> addressComboBox;
    private JButton calculateButton;
    private JButton submitButton;
    private JButton addressManageButton;
    private JLabel originalAmountLabel;
    private JLabel couponDiscountLabel;
    private JLabel fullReductionLabel;
    private JLabel vipDiscountLabel;
    private JLabel totalLabel;
    private JLabel memberInfoLabel;
    private JTextArea discountDetailArea;
    private JLabel giftDiscountLabel;
    private JLabel timeLimitDiscountLabel;
    
    public CheckoutFrame(Cart cart, PromotionService promotionService, Member currentMember, CartFrame parentCartFrame) {
        this.cart = cart;
        this.promotionService = promotionService;
        this.currentMember = currentMember;
        this.parentCartFrame = parentCartFrame;
        // 通过外观模式获取服务引用
        ECommerceFacade facade = ECommerceFacade.INSTANCE;
        this.orderService = facade.getOrderService();
        this.paymentService = facade.getPaymentService();
        this.addressService = facade.getAddressService();
        this.memberService = facade.getMemberService();
        initUI();
    }
    
    // 结算步骤枚举
    private enum CheckoutStep {
        ADDRESS("选择地址"),
        COUPON("选择优惠"),
        PAYMENT("选择支付"),
        CONFIRM("确认订单");
        
        private String name;
        
        CheckoutStep(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    private CheckoutStep currentStep = CheckoutStep.ADDRESS;
    private JProgressBar progressBar;
    private JLabel stepLabel;
    private JButton prevButton;
    private JButton nextButton;
    private JPanel stepPanel;
    private JPanel addressStepPanel;
    private JPanel couponStepPanel;
    private JPanel paymentStepPanel;
    private JPanel confirmStepPanel;
    private JLabel paymentDiscountLabel;
    private JTextArea paymentDescriptionArea;
    
    private void initUI() {
        setTitle("结算页面");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (parentCartFrame != null) parentCartFrame.setVisible(true);
            }
        });
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel progressPanel = new JPanel(new BorderLayout(10, 5));
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 128, 0));
        stepLabel = new JLabel("当前步骤: " + currentStep.getName(), SwingConstants.CENTER);
        stepLabel.setFont(new Font("宋体", Font.BOLD, 14));
        progressPanel.add(progressBar, BorderLayout.NORTH);
        progressPanel.add(stepLabel, BorderLayout.SOUTH);
        mainPanel.add(progressPanel, BorderLayout.NORTH);
        
        createStepPanels();
        
        stepPanel = new JPanel(new CardLayout());
        stepPanel.add(addressStepPanel, "ADDRESS");
        stepPanel.add(couponStepPanel, "COUPON");
        stepPanel.add(paymentStepPanel, "PAYMENT");
        stepPanel.add(confirmStepPanel, "CONFIRM");
        mainPanel.add(stepPanel, BorderLayout.CENTER);
        
        JPanel navButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        prevButton = new JButton("上一步");
        nextButton = new JButton("下一步");
        calculateButton = new JButton("计算优惠");
        submitButton = new JButton("提交订单");
        submitButton.setEnabled(false);
        JButton exitButton = new JButton("退出");
        
        navButtonPanel.add(prevButton);
        navButtonPanel.add(nextButton);
        navButtonPanel.add(calculateButton);
        navButtonPanel.add(submitButton);
        navButtonPanel.add(exitButton);
        mainPanel.add(navButtonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        updateStep();
        
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (currentStep) {
                    case COUPON: currentStep = CheckoutStep.ADDRESS; break;
                    case PAYMENT: currentStep = CheckoutStep.COUPON; break;
                    case CONFIRM: currentStep = CheckoutStep.PAYMENT; submitButton.setEnabled(false); break;
                }
                updateStep();
            }
        });
        
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (currentStep) {
                    case ADDRESS:
                        if (addressComboBox.getSelectedItem() == null) {
                            JOptionPane.showMessageDialog(CheckoutFrame.this, "请先添加收货地址！\n点击\"管理地址\"按钮添加。", "提示", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        currentStep = CheckoutStep.COUPON;
                        break;
                    case COUPON: currentStep = CheckoutStep.PAYMENT; break;
                    case PAYMENT: currentStep = CheckoutStep.CONFIRM; break;
                }
                updateStep();
            }
        });
        
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
        
        addressManageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddressFrame af = new AddressFrame(addressService);
                af.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent evt) {
                        Address selected = (Address) addressComboBox.getSelectedItem();
                        addressComboBox.removeAllItems();
                        for (Address addr : addressService.getAddresses()) {
                            addressComboBox.addItem(addr);
                        }
                        if (selected != null) {
                            addressComboBox.setSelectedItem(selected);
                        }
                        Address defaultAddr = addressService.getDefaultAddress();
                        if (defaultAddr != null && addressComboBox.getSelectedItem() == null) {
                            addressComboBox.setSelectedItem(defaultAddr);
                        }
                    }
                });
                af.setVisible(true);
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parentCartFrame != null) parentCartFrame.setVisible(true);
                dispose();
            }
        });
    }
    
    private void createStepPanels() {
        createAddressStepPanel();
        createCouponStepPanel();
        createPaymentStepPanel();
        createConfirmStepPanel();
    }
    
    private void createAddressStepPanel() {
        addressStepPanel = new JPanel(new BorderLayout(20, 20));
        
        JPanel memberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if (currentMember != null) {
            memberInfoLabel = new JLabel("会员: " + currentMember.getName() + " | 等级: " + currentMember.getLevel().getName() + " | 积分: " + currentMember.getPoints());
        } else {
            memberInfoLabel = new JLabel("会员: 未登录");
        }
        memberInfoLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        memberPanel.add(memberInfoLabel);
        addressStepPanel.add(memberPanel, BorderLayout.NORTH);
        
        JPanel addressPanel = new JPanel(new BorderLayout(10, 10));
        addressPanel.setBorder(BorderFactory.createTitledBorder("选择收货地址"));
        
        JPanel addressSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addressSelectPanel.add(new JLabel("收货地址:"));
        
        addressComboBox = new JComboBox<>();
        addressComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Address) {
                    Address address = (Address) value;
                    setText(address.getName() + " " + address.getPhone() + " | " + address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
                }
                return this;
            }
        });
        for (Address address : addressService.getAddresses()) {
            addressComboBox.addItem(address);
        }
        Address defaultAddress = addressService.getDefaultAddress();
        if (defaultAddress != null) addressComboBox.setSelectedItem(defaultAddress);
        addressComboBox.setPreferredSize(new Dimension(400, 25));
        addressSelectPanel.add(addressComboBox);
        
        addressManageButton = new JButton("管理地址");
        addressSelectPanel.add(addressManageButton);
        addressPanel.add(addressSelectPanel, BorderLayout.CENTER);
        
        addressStepPanel.add(addressPanel, BorderLayout.CENTER);
    }
    
    private void createCouponStepPanel() {
        couponStepPanel = new JPanel(new BorderLayout(20, 20));
        
        JPanel couponPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        couponPanel.setBorder(BorderFactory.createTitledBorder("优惠券选择 [策略模式]"));
        couponPanel.add(new JLabel("选择优惠券:"));
        
        couponComboBox = new JComboBox<>();
        couponComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Coupon) {
                    Coupon coupon = (Coupon) value;
                    setText(coupon.getName() + " (满" + coupon.getMinSpend() + "减" + coupon.getValue() + ")");
                }
                return this;
            }
        });
        for (Coupon coupon : promotionService.getCoupons()) {
            couponComboBox.addItem(coupon);
        }
        couponComboBox.setPreferredSize(new Dimension(250, 25));
        couponPanel.add(couponComboBox);
        
        JButton couponDetailButton = new JButton("查看详情");
        couponDetailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Coupon selectedCoupon = (Coupon) couponComboBox.getSelectedItem();
                if (selectedCoupon != null) showCouponDetail(selectedCoupon);
            }
        });
        couponPanel.add(couponDetailButton);
        couponStepPanel.add(couponPanel, BorderLayout.NORTH);
        
        JPanel promotionPanel = new JPanel();
        promotionPanel.setBorder(BorderFactory.createTitledBorder("适用促销规则"));
        promotionPanel.setLayout(new BoxLayout(promotionPanel, BoxLayout.Y_AXIS));
        
        JTextArea promotionTextArea = new JTextArea(8, 50);
        promotionTextArea.setEditable(false);
        promotionTextArea.setLineWrap(true);
        promotionTextArea.setWrapStyleWord(true);
        
        StringBuilder promotionDesc = new StringBuilder();
        promotionDesc.append("当前适用的促销规则：\n\n");
        java.util.List<com.ecommerce.cart.model.PromotionRule> rules = promotionService.getPromotionRules();
        if (rules.isEmpty()) {
            promotionDesc.append("暂无促销规则\n");
        } else {
            for (com.ecommerce.cart.model.PromotionRule rule : rules) {
                promotionDesc.append("- ").append(rule.getName()).append(" (类型: ").append(com.ecommerce.cart.util.CategoryUtil.getPromotionTypeDisplayName(rule.getType())).append(", 值: ").append(rule.getValue()).append(", 最低消费: ¥").append(String.format("%.0f", rule.getMinSpend())).append(")\n");
            }
        }
        promotionDesc.append("\n优惠规则说明：\n");
        promotionDesc.append("- 满减优惠自动计算，无需手动选择\n");
        promotionDesc.append("- VIP折扣根据当前会员等级自动应用\n");
        promotionDesc.append("- 优惠券可在支付时选择使用\n");
        promotionTextArea.setText(promotionDesc.toString());
        promotionPanel.add(new JScrollPane(promotionTextArea));
        couponStepPanel.add(promotionPanel, BorderLayout.CENTER);
    }
    
    private void createPaymentStepPanel() {
        paymentStepPanel = new JPanel(new BorderLayout(20, 20));
        
        JPanel paymentPanel = new JPanel(new BorderLayout(10, 10));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("支付方式选择"));
        
        JPanel paymentSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paymentSelectPanel.add(new JLabel("选择支付方式:"));
        paymentComboBox = new JComboBox<>();
        for (String paymentMethod : paymentService.getAvailablePaymentMethods()) {
            paymentComboBox.addItem(paymentMethod);
        }
        paymentComboBox.setPreferredSize(new Dimension(200, 25));
        paymentSelectPanel.add(paymentComboBox);
        paymentPanel.add(paymentSelectPanel, BorderLayout.NORTH);
        
        JPanel paymentDetailPanel = new JPanel();
        paymentDetailPanel.setLayout(new BoxLayout(paymentDetailPanel, BoxLayout.Y_AXIS));
        paymentDescriptionArea = new JTextArea(3, 50);
        paymentDescriptionArea.setEditable(false);
        paymentDescriptionArea.setLineWrap(true);
        paymentDescriptionArea.setWrapStyleWord(true);
        paymentDiscountLabel = new JLabel("支付优惠: ¥0.00");
        paymentDiscountLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        paymentDiscountLabel.setForeground(new Color(0, 128, 0));
        paymentDetailPanel.add(new JScrollPane(paymentDescriptionArea));
        paymentDetailPanel.add(paymentDiscountLabel);
        paymentPanel.add(paymentDetailPanel, BorderLayout.CENTER);
        
        paymentComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPayment = (String) paymentComboBox.getSelectedItem();
                if (selectedPayment != null) {
                    String paymentType = paymentService.getPaymentType(selectedPayment);
                    com.ecommerce.cart.pattern.adapter.PaymentAdapter adapter = paymentService.getPaymentAdapter(paymentType);
                    if (adapter != null) {
                        paymentDescriptionArea.setText("[适配器模式] 当前适配器: " + selectedPayment + "\n" + adapter.getPaymentDescription());
                        paymentDiscountLabel.setText("支付优惠: ¥" + String.format("%.2f", adapter.getDiscount()));
                    }
                }
            }
        });
        
        if (paymentComboBox.getItemCount() > 0) {
            String defaultPayment = (String) paymentComboBox.getItemAt(0);
            String defaultPaymentType = paymentService.getPaymentType(defaultPayment);
            com.ecommerce.cart.pattern.adapter.PaymentAdapter defaultAdapter = paymentService.getPaymentAdapter(defaultPaymentType);
            if (defaultAdapter != null) paymentDescriptionArea.setText("[适配器模式] 当前适配器: " + defaultPayment + "\n" + defaultAdapter.getPaymentDescription());
        }
        
        paymentStepPanel.add(paymentPanel, BorderLayout.CENTER);
    }
    
    private void createConfirmStepPanel() {
        confirmStepPanel = new JPanel(new BorderLayout(20, 20));
        
        JPanel amountPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        amountPanel.setBorder(BorderFactory.createTitledBorder("订单金额"));
        
        amountPanel.add(new JLabel("原始金额:"));
        originalAmountLabel = new JLabel("¥" + String.format("%.2f", cart.getTotal()));
        amountPanel.add(originalAmountLabel);
        
        amountPanel.add(new JLabel("优惠券:"));
        couponDiscountLabel = new JLabel("点击\"计算优惠\"查看");
        amountPanel.add(couponDiscountLabel);
        
        amountPanel.add(new JLabel("满减:"));
        fullReductionLabel = new JLabel("点击\"计算优惠\"查看");
        amountPanel.add(fullReductionLabel);
        
        amountPanel.add(new JLabel("VIP折扣:"));
        vipDiscountLabel = new JLabel("点击\"计算优惠\"查看");
        amountPanel.add(vipDiscountLabel);
        
        amountPanel.add(new JLabel("满赠折扣:"));
        giftDiscountLabel = new JLabel("点击\"计算优惠\"查看");
        amountPanel.add(giftDiscountLabel);
        
        amountPanel.add(new JLabel("限时折扣:"));
        timeLimitDiscountLabel = new JLabel("点击\"计算优惠\"查看");
        amountPanel.add(timeLimitDiscountLabel);
        
        amountPanel.add(new JLabel("实付金额:"));
        totalLabel = new JLabel("点击\"计算优惠\"查看");
        totalLabel.setFont(new Font("宋体", Font.BOLD, 16));
        amountPanel.add(totalLabel);
        
        confirmStepPanel.add(amountPanel, BorderLayout.NORTH);
        
        discountDetailArea = new JTextArea(6, 50);
        discountDetailArea.setEditable(false);
        discountDetailArea.setLineWrap(true);
        discountDetailArea.setWrapStyleWord(true);
        discountDetailArea.setText("优惠计算明细将在点击\"计算优惠\"后显示");
        JScrollPane detailScrollPane = new JScrollPane(discountDetailArea);
        detailScrollPane.setBorder(BorderFactory.createTitledBorder("优惠计算明细"));
        confirmStepPanel.add(detailScrollPane, BorderLayout.CENTER);
        
        JLabel hintLabel = new JLabel("请先点击\"计算优惠\"按钮，确认金额后再提交订单", SwingConstants.CENTER);
        hintLabel.setForeground(new Color(200, 0, 0));
        confirmStepPanel.add(hintLabel, BorderLayout.SOUTH);
    }
    
    private void updateStep() {
        stepLabel.setText("当前步骤: " + currentStep.getName());
        int progress = 0;
        switch (currentStep) {
            case ADDRESS: progress = 25; break;
            case COUPON: progress = 50; break;
            case PAYMENT: progress = 75; break;
            case CONFIRM: progress = 100; break;
        }
        progressBar.setValue(progress);
        progressBar.setString(progress + "%");
        
        prevButton.setEnabled(currentStep != CheckoutStep.ADDRESS);
        nextButton.setEnabled(currentStep != CheckoutStep.CONFIRM);
        calculateButton.setEnabled(currentStep == CheckoutStep.CONFIRM);
        submitButton.setEnabled(false);
        
        CardLayout cl = (CardLayout) stepPanel.getLayout();
        switch (currentStep) {
            case ADDRESS: cl.show(stepPanel, "ADDRESS"); break;
            case COUPON: cl.show(stepPanel, "COUPON"); break;
            case PAYMENT: cl.show(stepPanel, "PAYMENT"); break;
            case CONFIRM: cl.show(stepPanel, "CONFIRM"); break;
        }
    }
    
    private void calculateDiscounts() {
        // 1. 创建订单对象
        Coupon selectedCoupon = (Coupon) couponComboBox.getSelectedItem();
        Address selectedAddress = (Address) addressComboBox.getSelectedItem();
        order = new OrderBuilder(cart)
            .withCoupon(selectedCoupon)
            .withMember(currentMember)
            .withAddress(selectedAddress)
            .build();
        
        // 2. 重置UI显示
        couponDiscountLabel.setText("计算中...");
        fullReductionLabel.setText("计算中...");
        vipDiscountLabel.setText("计算中...");
        giftDiscountLabel.setText("计算中...");
        timeLimitDiscountLabel.setText("计算中...");
        totalLabel.setText("计算中...");
        
        // 3. 使用SwingWorker执行职责链计算
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Handler chain = new DiscountChainBuilder(promotionService)
                    .withMember(currentMember)
                    .build();
                chain.handle(order);
                order.setPriceCalculated(true);

                for (com.ecommerce.cart.model.CartItem item : order.getItems()) {
                    StringBuilder nameBuilder = new StringBuilder(item.getProduct().getName());
                    if (order.getCouponDiscount() > 0) {
                        nameBuilder.append(" (优惠券已抵扣)");
                    }
                    if (order.getFullReduction() > 0) {
                        nameBuilder.append(" (满减活动)");
                    }
                    if (order.getVipDiscount() > 0) {
                        nameBuilder.append(" (VIP折扣)");
                    }
                    item.setDecoratedName(nameBuilder.toString());
                }
                
                return null;
            }
            
            @Override
            protected void done() {
                try { get(); } catch (Exception ex) { ex.printStackTrace(); return; }
                couponDiscountLabel.setText("-¥" + String.format("%.2f", order.getCouponDiscount()));
                Timer t1 = new Timer(500, e1 -> {
                    fullReductionLabel.setText("-¥" + String.format("%.2f", order.getFullReduction()));
                    Timer t2 = new Timer(500, e2 -> {
                        vipDiscountLabel.setText("-¥" + String.format("%.2f", order.getVipDiscount()));
                        giftDiscountLabel.setText("-¥" + String.format("%.2f", order.getGiftDiscount()));
                        timeLimitDiscountLabel.setText("-¥" + String.format("%.2f", order.getTimeLimitDiscount()));
                        totalLabel.setText("¥" + String.format("%.2f", order.getFinalAmount()));
                        submitButton.setEnabled(true);
                        StringBuilder detail = new StringBuilder();
                        detail.append("===== 优惠计算明细 [策略模式+职责链模式] =====\n\n");
                        if (order.getCouponDiscount() > 0) {
                            Coupon coupon = (Coupon) couponComboBox.getSelectedItem();
                            String couponName = coupon != null ? coupon.getName() : "优惠券";
                            detail.append("【优惠券】").append(couponName).append("(满").append(String.format("%.0f", coupon != null ? coupon.getMinSpend() : 0)).append("减").append(String.format("%.0f", order.getCouponDiscount())).append("): -¥").append(String.format("%.2f", order.getCouponDiscount())).append("\n");
                        }
                        if (order.getFullReduction() > 0) {
                            for (com.ecommerce.cart.model.PromotionRule rule : promotionService.getPromotionRules()) {
                                if ("FULL_REDUCTION".equals(rule.getType()) && cart.getTotal() >= rule.getMinSpend()) {
                                    detail.append("【满减】").append(rule.getName()).append("(满").append(String.format("%.0f", rule.getMinSpend())).append("减").append(String.format("%.0f", rule.getValue())).append("): -¥").append(String.format("%.2f", order.getFullReduction())).append("\n");
                                    break;
                                }
                            }
                        }
                        if (order.getVipDiscount() > 0 && currentMember != null) {
                            detail.append("【VIP折扣】").append(currentMember.getLevel().getName()).append("(").append(String.format("%.0f", currentMember.getLevel().getDiscountRate() * 100)).append("%折扣): -¥").append(String.format("%.2f", order.getVipDiscount())).append("\n");
                        }
                        if (order.getGiftDiscount() > 0) {
                            detail.append("【满赠】满赠活动: -¥").append(String.format("%.2f", order.getGiftDiscount())).append("\n");
                        }
                        if (order.getTimeLimitDiscount() > 0) {
                            detail.append("【限时折扣】限时折扣活动: -¥").append(String.format("%.2f", order.getTimeLimitDiscount())).append("\n");
                        }
                        detail.append("\n合计优惠: -¥").append(String.format("%.2f", order.getCouponDiscount() + order.getFullReduction() + order.getVipDiscount() + order.getGiftDiscount() + order.getTimeLimitDiscount()));
                        discountDetailArea.setText(detail.toString());
                    });
                    t2.setRepeats(false);
                    t2.start();
                });
                t1.setRepeats(false);
                t1.start();
            }
        }.execute();
    }
    
    private void submitOrder() {
        String paymentMethod = (String) paymentComboBox.getSelectedItem();
        String paymentType = paymentService.getPaymentType(paymentMethod);
        
        com.ecommerce.cart.pattern.command.CommandManager commandManager = 
            new com.ecommerce.cart.pattern.command.CommandManager();
        com.ecommerce.cart.pattern.command.OrderCommand orderCommand = 
            new com.ecommerce.cart.pattern.command.OrderCommand(order, commandManager, paymentType);
        orderCommand.execute();
        
        if (orderCommand.isExecuted()) {
            ECommerceFacade.INSTANCE.getCartService().clearCart();
            if (currentMember != null) {
                Member updatedMember = memberService.getMemberById(currentMember.getId());
                if (updatedMember != null) {
                    memberInfoLabel.setText("会员: " + updatedMember.getName() + " | 等级: " + updatedMember.getLevel().getName() + " | 积分: " + updatedMember.getPoints());
                }
            }
            
            String processorType = order.getVipDiscount() > 0 ? "VIP订单处理器(VIP免密支付+短信邮件通知+额外5%折扣)" : "普通订单处理器(标准支付+邮件通知)";
            JOptionPane.showMessageDialog(this, "订单提交成功！\n订单号: " + order.getOrderId() + "\n[模板方法模式] 使用: " + processorType + "\n处理步骤: 验证→计价→支付→库存→生成订单→通知");
            new OrderDetailFrame(order, parentCartFrame).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "支付失败，请重试");
        }
    }
    
    // 搜索地址
    private void searchAddresses(String keyword) {
        // 清空当前地址列表
        addressComboBox.removeAllItems();
        
        // 添加匹配的地址
        for (Address address : addressService.getAddresses()) {
            if (keyword.isEmpty() || 
                address.getName().toLowerCase().contains(keyword.toLowerCase()) || 
                address.getPhone().contains(keyword) || 
                address.getProvince().toLowerCase().contains(keyword.toLowerCase()) || 
                address.getCity().toLowerCase().contains(keyword.toLowerCase()) || 
                address.getDistrict().toLowerCase().contains(keyword.toLowerCase()) || 
                address.getDetailAddress().toLowerCase().contains(keyword.toLowerCase())) {
                addressComboBox.addItem(address);
            }
        }
        
        // 如果没有匹配的地址，显示提示
        if (addressComboBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "没有找到匹配的地址");
            // 重新添加所有地址
            for (Address address : addressService.getAddresses()) {
                addressComboBox.addItem(address);
            }
        }
    }
    
    // 显示优惠券详情
    private void showCouponDetail(Coupon coupon) {
        JDialog dialog = new JDialog(this, "优惠券详情", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 优惠券信息
        JPanel couponInfoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        couponInfoPanel.add(new JLabel("优惠券名称:"));
        couponInfoPanel.add(new JLabel(coupon.getName()));
        couponInfoPanel.add(new JLabel("优惠券ID:"));
        couponInfoPanel.add(new JLabel(coupon.getId()));
        couponInfoPanel.add(new JLabel("优惠金额:"));
        couponInfoPanel.add(new JLabel("¥" + coupon.getValue()));
        couponInfoPanel.add(new JLabel("使用条件:"));
        couponInfoPanel.add(new JLabel("满" + coupon.getMinSpend() + "元可用"));
        
        // 优惠券使用说明
        JPanel couponDescPanel = new JPanel(new BorderLayout());
        couponDescPanel.setBorder(BorderFactory.createTitledBorder("使用说明"));
        JTextArea descTextArea = new JTextArea();
        descTextArea.setEditable(false);
        descTextArea.setLineWrap(true);
        descTextArea.setWrapStyleWord(true);
        descTextArea.setText("1. 本优惠券可与其他优惠叠加使用\n" +
                           "2. 每个订单只能使用一张优惠券\n" +
                           "3. 优惠券不找零，不兑换现金\n" +
                           "4. 优惠券有效期至2026年12月31日\n" +
                           "5. 最终解释权归本商城所有");
        couponDescPanel.add(new JScrollPane(descTextArea), BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        buttonPanel.add(closeButton);
        
        mainPanel.add(couponInfoPanel, BorderLayout.NORTH);
        mainPanel.add(couponDescPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}
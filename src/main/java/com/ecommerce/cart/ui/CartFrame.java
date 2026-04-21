package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.model.Member;
import com.ecommerce.cart.pattern.command.CommandManager;
import com.ecommerce.cart.pattern.command.AddProductCommand;
import com.ecommerce.cart.pattern.command.RemoveProductCommand;
import com.ecommerce.cart.pattern.command.UpdateQuantityCommand;
import com.ecommerce.cart.pattern.observer.EventBus;
import com.ecommerce.cart.pattern.observer.Observer;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.service.CartService;
import com.ecommerce.cart.service.MemberService;
import com.ecommerce.cart.service.PromotionService;
import com.ecommerce.cart.service.ReviewService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class CartFrame extends JFrame implements Observer {
    private CartService cartService;
    private PromotionService promotionService;
    private MemberService memberService;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton removeButton;
    private JButton checkoutButton;
    private JButton undoButton;
    private JLabel totalLabel;
    private JLabel memberInfoLabel;
    private JLabel vipDiscountLabel;
    private CommandManager commandManager;
    private Member currentMember;
    
    public CartFrame() {
        // 通过外观模式获取服务引用
        ECommerceFacade facade = ECommerceFacade.INSTANCE;
        cartService = facade.getCartService();
        promotionService = facade.getPromotionService();
        memberService = facade.getMemberService();
        commandManager = new CommandManager();
        EventBus.getInstance().registerObserver("CART_CHANGED", this);
        initUI();
        populateTestData();
    }
    
    private void initUI() {
        setTitle("购物车");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(CartFrame.this, "确定要退出系统吗？", "退出确认", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    EventBus.getInstance().removeObserver("CART_CHANGED", CartFrame.this);
                    dispose();
                    System.exit(0);
                }
            }
        });
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 表格部分
        String[] columnNames = {"商品ID", "商品名称", "装饰名称", "分类", "单价", "数量", "小计"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 只有数量列可编辑，其他列不可编辑
                return column == 5;
            }
        };
        cartTable = new JTable(tableModel);
        // 设置数量列的单元格渲染器，显示加减按钮
        cartTable.getColumnModel().getColumn(5).setCellRenderer(new QuantityCellRenderer());
        // 设置数量列的单元格编辑器，处理按钮点击事件
        cartTable.getColumnModel().getColumn(5).setCellEditor(new QuantityCellEditor());
        // 设置表格列宽
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        // 设置表头样式
        cartTable.getTableHeader().setFont(new Font("宋体", Font.BOLD, 12));
        cartTable.getTableHeader().setBackground(new Color(240, 240, 240));
        
        // 设置表格行高
        cartTable.setRowHeight(30);
        
        // 添加鼠标监听器，双击查看商品详情
        cartTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = cartTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        String productId = (String) tableModel.getValueAt(selectedRow, 0);
                        com.ecommerce.cart.service.ProductService prodService = ECommerceFacade.INSTANCE.getProductService();
                        com.ecommerce.cart.model.Product product = prodService.getProductById(productId);
                        if (product != null) {
                            new ProductDetailDialog(CartFrame.this, product, ECommerceFacade.INSTANCE.getReviewService()).setVisible(true);
                        }
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 底部操作栏
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        
        // 第一行：会员信息和筛选功能
        JPanel topInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        // 获取默认会员（这里使用第一个会员作为示例）
        Member member = memberService.getMembers().isEmpty() ? null : memberService.getMembers().get(0);
        if (member != null) {
            memberInfoLabel = new JLabel("会员: " + member.getName() + " | 等级: " + member.getLevel().getName() + " | 积分: " + member.getPoints() + " [观察者模式]");
        } else {
            memberInfoLabel = new JLabel("会员: 未登录 [观察者模式]");
        }
        memberInfoLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        topInfoPanel.add(memberInfoLabel);
        
        // 会员选择组合框
        JLabel memberSelectLabel = new JLabel("选择会员: ");
        JComboBox<String> memberComboBox = new JComboBox<>();
        memberComboBox.addItem("无会员");
        // 添加所有会员到组合框
        for (Member m : memberService.getMembers()) {
            memberComboBox.addItem(m.getId() + " - " + m.getName());
        }
        if (!memberService.getMembers().isEmpty()) {
            memberComboBox.setSelectedIndex(1);
        }
        
        // 会员选择监听器
        memberComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) memberComboBox.getSelectedItem();
                if ("无会员".equals(selected)) {
                    currentMember = null;
                    memberInfoLabel.setText("会员: 未登录 [观察者模式]");
                    vipDiscountLabel.setText("会员专属优惠: ¥0.00");
                } else {
                    // 从选择的字符串中提取会员ID
                    String memberId = selected.split(" - ")[0];
                    currentMember = memberService.getMemberById(memberId);
                    if (currentMember != null) {
                        memberInfoLabel.setText("会员: " + currentMember.getName() + " | 等级: " + currentMember.getLevel().getName() + " | 积分: " + currentMember.getPoints() + " [观察者模式]");
                        // 计算会员专属优惠
                        double total = cartService.getCart().getTotal();
                        double vipDiscount = total * (1 - currentMember.getLevel().getDiscountRate());
                        vipDiscountLabel.setText("会员专属优惠: ¥" + String.format("%.2f", vipDiscount));
                    }
                }
            }
        });
        
        topInfoPanel.add(memberSelectLabel);
        topInfoPanel.add(memberComboBox);
        
        // 商品分类筛选
        JLabel categoryLabel = new JLabel("分类筛选: ");
        JComboBox<String> categoryComboBox = new JComboBox<>();
        categoryComboBox.addItem("全部");
        categoryComboBox.addItem("电子产品");
        categoryComboBox.addItem("服装");
        categoryComboBox.addItem("食品");
        categoryComboBox.addItem("图书");
        
        // 分类筛选监听器
        categoryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                filterCartByCategory(selectedCategory);
            }
        });
        
        topInfoPanel.add(categoryLabel);
        topInfoPanel.add(categoryComboBox);
        
        // 商品搜索功能
        JLabel searchLabel = new JLabel("搜索: ");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("搜索");
        
        // 搜索按钮监听器
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText().trim();
                searchCart(keyword);
            }
        });
        
        // 搜索输入框回车监听器
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText().trim();
                searchCart(keyword);
            }
        });
        
        topInfoPanel.add(searchLabel);
        topInfoPanel.add(searchField);
        topInfoPanel.add(searchButton);
        
        bottomPanel.add(topInfoPanel);
        
        // 第二行：优惠信息和总价
        JPanel middleInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        // 会员专属优惠信息
        vipDiscountLabel = new JLabel("会员专属优惠: ¥0.00");
        vipDiscountLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        vipDiscountLabel.setForeground(new Color(0, 128, 0));
        middleInfoPanel.add(vipDiscountLabel);
        
        totalLabel = new JLabel("总价: ¥0.00");
        totalLabel.setFont(new Font("宋体", Font.BOLD, 16));
        totalLabel.setForeground(new Color(255, 0, 0));
        middleInfoPanel.add(totalLabel);
        
        bottomPanel.add(middleInfoPanel);
        
        // 第三行：按钮
        addButton = new JButton("添加商品");
        removeButton = new JButton("删除商品");
        checkoutButton = new JButton("结算");
        checkoutButton.setForeground(new Color(255, 255, 255));
        checkoutButton.setBackground(new Color(0, 128, 0));
        JButton browseButton = new JButton("浏览商品");
        JButton orderHistoryButton = new JButton("订单历史");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"购物者", "管理员", "商家"});
        undoButton = new JButton("撤销 [命令模式]");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(browseButton);
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(orderHistoryButton);
        buttonPanel.add(new JLabel("角色:"));
        buttonPanel.add(roleComboBox);
        buttonPanel.add(undoButton);
        
        bottomPanel.add(buttonPanel);

        JPanel patternNavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        patternNavPanel.setBorder(BorderFactory.createTitledBorder("设计模式导航"));
        String[][] patterns = {
            {"单例模式", "Logger/DBConnection"}, {"工厂模式", "ECommerceFactory"}, {"抽象工厂", "AbstractFactory"},
            {"策略模式", "结算优惠计算"}, {"观察者模式", "EventBus"}, {"命令模式", "购物车操作"},
            {"模板方法", "订单处理流程"}, {"状态模式", "订单状态转换"}, {"职责链", "优惠计算链"},
            {"装饰器", "商品浏览装饰"}, {"适配器", "支付方式"}, {"外观模式", "ECommerceFacade"},
            {"建造者", "OrderBuilder"}
        };
        for (String[] p : patterns) {
            JLabel label = new JLabel(p[0] + "(" + p[1] + ")");
            label.setFont(new Font("宋体", Font.PLAIN, 11));
            label.setForeground(new Color(0, 0, 180));
            patternNavPanel.add(label);
        }
        bottomPanel.add(patternNavPanel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 按钮监听器
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddProductDialog();
            }
        });
        
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = cartTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String productId = (String) tableModel.getValueAt(selectedRow, 0);
                    RemoveProductCommand command = new RemoveProductCommand(cartService, productId);
                    commandManager.executeCommand(command);
                } else {
                    JOptionPane.showMessageDialog(CartFrame.this, "请选择要删除的商品");
                }
            }
        });
        
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cartService.getCart().getItems().isEmpty()) {
                    JOptionPane.showMessageDialog(CartFrame.this, "购物车为空");
                    return;
                }
                new CheckoutFrame(cartService.getCart(), promotionService, currentMember, CartFrame.this).setVisible(true);
                setVisible(false);
            }
        });
        
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProductBrowseFrame(CartFrame.this).setVisible(true);
            }
        });
        
        orderHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OrderHistoryFrame(CartFrame.this).setVisible(true);
            }
        });
        
        roleComboBox.setSelectedItem("购物者");
        roleComboBox.addItemListener(new java.awt.event.ItemListener() {
            @Override
            public void itemStateChanged(java.awt.event.ItemEvent e) {
                if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                    String role = (String) roleComboBox.getSelectedItem();
                    if ("管理员".equals(role)) {
                        setVisible(false);
                        AdminFrame adminFrame = new AdminFrame();
                        adminFrame.setVisible(true);
                        adminFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                            @Override
                            public void windowClosed(java.awt.event.WindowEvent evt) {
                                setVisible(true);
                                updateCartTable();
                                updateMemberInfo();
                                roleComboBox.setSelectedItem("购物者");
                            }
                        });
                    } else if ("商家".equals(role)) {
                        setVisible(false);
                        MerchantFrame merchantFrame = new MerchantFrame();
                        merchantFrame.setVisible(true);
                        merchantFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                            @Override
                            public void windowClosed(java.awt.event.WindowEvent evt) {
                                setVisible(true);
                                updateCartTable();
                                updateMemberInfo();
                                roleComboBox.setSelectedItem("购物者");
                            }
                        });
                    }
                }
            }
        });
        
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (commandManager.canUndo()) {
                    commandManager.undo();
                } else {
                    JOptionPane.showMessageDialog(CartFrame.this, "没有可撤销的操作");
                }
            }
        });
    }
    
    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "添加商品", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        
        com.ecommerce.cart.service.ProductService prodService = ECommerceFacade.INSTANCE.getProductService();
        java.util.List<com.ecommerce.cart.model.Product> products = prodService.getAllProducts();
        String[] productNames = new String[products.size()];
        for (int i = 0; i < products.size(); i++) {
            com.ecommerce.cart.model.Product p = products.get(i);
            productNames[i] = p.getName() + " (" + p.getId() + ") - ¥" + String.format("%.2f", p.getPrice());
        }
        JComboBox<String> productComboBox = new JComboBox<>(productNames);
        productComboBox.setFont(new Font("宋体", Font.PLAIN, 14));
        
        JTextField quantityField = new JTextField("1");
        quantityField.setFont(new Font("宋体", Font.PLAIN, 14));
        
        JLabel productLabel = new JLabel("选择商品:");
        productLabel.setFont(new Font("宋体", Font.BOLD, 14));
        
        JLabel quantityLabel = new JLabel("数量:");
        quantityLabel.setFont(new Font("宋体", Font.BOLD, 14));
        
        panel.add(productLabel);
        panel.add(productComboBox);
        panel.add(quantityLabel);
        panel.add(quantityField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton confirmButton = new JButton("确认");
        confirmButton.setFont(new Font("宋体", Font.BOLD, 14));
        confirmButton.setForeground(new Color(255, 255, 255));
        confirmButton.setBackground(new Color(0, 128, 0));
        
        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("宋体", Font.BOLD, 14));
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedIndex = productComboBox.getSelectedIndex();
                    if (selectedIndex < 0) {
                        JOptionPane.showMessageDialog(dialog, "请选择商品");
                        return;
                    }
                    com.ecommerce.cart.model.Product selectedProduct = products.get(selectedIndex);
                    int quantity = Integer.parseInt(quantityField.getText());
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(dialog, "数量必须大于0");
                        return;
                    }
                    
                    AddProductCommand command = new AddProductCommand(cartService, selectedProduct.getCategory(), selectedProduct.getId(), selectedProduct.getName(), selectedProduct.getPrice(), quantity);
                    commandManager.executeCommand(command);
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "请输入正确的数量");
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
    }
    
    public void updateCartTable() {
        tableModel.setRowCount(0);
        for (CartItem item : cartService.getCart().getItems()) {
            String displayCategory = com.ecommerce.cart.util.CategoryUtil.toDisplayName(item.getProduct().getCategory());
            String decoratedName = item.getDecoratedName() != null ? item.getDecoratedName() : "—";
            Object[] row = {
                item.getProduct().getId(),
                item.getProduct().getName(),
                decoratedName,
                displayCategory,
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getSubtotal()
            };
            tableModel.addRow(row);
        }
        totalLabel.setText("总价: ¥" + String.format("%.2f", cartService.getTotal()));
    }
    
    private void populateTestData() {
        if (cartService.getCart().getItems().isEmpty()) {
            cartService.addProduct("electronics", "E001", "手机", 1999.99, 1);
            cartService.addProduct("clothing", "C001", "T恤", 99.99, 2);
            cartService.addProduct("food", "F001", "零食", 49.99, 3);
        }
        if (currentMember == null && !memberService.getMembers().isEmpty()) {
            currentMember = memberService.getMembers().get(0);
            memberInfoLabel.setText("会员: " + currentMember.getName() + " | 等级: " + currentMember.getLevel().getName() + " | 积分: " + currentMember.getPoints() + " [观察者模式]");
            double total = cartService.getCart().getTotal();
            double vipDiscount = total * (1 - currentMember.getLevel().getDiscountRate());
            vipDiscountLabel.setText("会员专属优惠: ¥" + String.format("%.2f", vipDiscount));
        }
        updateCartTable();
    }
    
    @Override
    public void update(String eventType, Object data) {
        updateCartTable();
        updateMemberInfo();
        if (currentMember != null) {
            double total = cartService.getCart().getTotal();
            double vipDiscount = total * (1 - currentMember.getLevel().getDiscountRate());
            vipDiscountLabel.setText("会员专属优惠: ¥" + String.format("%.2f", vipDiscount));
        }
    }
    
    private void updateMemberInfo() {
        if (currentMember != null) {
            Member freshMember = memberService.getMemberById(currentMember.getId());
            if (freshMember != null) {
                currentMember = freshMember;
            }
            memberInfoLabel.setText("会员: " + currentMember.getName() + " | 等级: " + currentMember.getLevel().getName() + " | 积分: " + currentMember.getPoints() + " [观察者模式]");
            double total = cartService.getCart().getTotal();
            double vipDiscount = total * (1 - currentMember.getLevel().getDiscountRate());
            vipDiscountLabel.setText("会员专属优惠: ¥" + String.format("%.2f", vipDiscount));
        } else {
            memberInfoLabel.setText("会员: 未登录 [观察者模式]");
            vipDiscountLabel.setText("会员专属优惠: ¥0.00");
        }
    }
    
    private void filterCartByCategory(String category) {
        tableModel.setRowCount(0);
        double filteredTotal = 0;
        
        for (CartItem item : cartService.getCart().getItems()) {
            String displayCategory = com.ecommerce.cart.util.CategoryUtil.toDisplayName(item.getProduct().getCategory());
            
            if ("全部".equals(category) || displayCategory.equals(category)) {
                String decoratedName = item.getDecoratedName() != null ? item.getDecoratedName() : "—";
                Object[] row = {
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    decoratedName,
                    displayCategory,
                    item.getProduct().getPrice(),
                    item.getQuantity(),
                    item.getSubtotal()
                };
                tableModel.addRow(row);
                filteredTotal += item.getSubtotal();
            }
        }
        
        totalLabel.setText("总价: ¥" + String.format("%.2f", filteredTotal));
    }
    
    private void searchCart(String keyword) {
        tableModel.setRowCount(0);
        double searchTotal = 0;
        
        for (CartItem item : cartService.getCart().getItems()) {
            String displayCategory = com.ecommerce.cart.util.CategoryUtil.toDisplayName(item.getProduct().getCategory());
            
            if (keyword.isEmpty() || 
                item.getProduct().getId().toLowerCase().contains(keyword.toLowerCase()) || 
                item.getProduct().getName().toLowerCase().contains(keyword.toLowerCase())) {
                String decoratedName = item.getDecoratedName() != null ? item.getDecoratedName() : "—";
                Object[] row = {
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    decoratedName,
                    displayCategory,
                    item.getProduct().getPrice(),
                    item.getQuantity(),
                    item.getSubtotal()
                };
                tableModel.addRow(row);
                searchTotal += item.getSubtotal();
            }
        }
        
        totalLabel.setText("总价: ¥" + String.format("%.2f", searchTotal));
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CartFrame().setVisible(true);
            }
        });
    }
    
    // 数量单元格渲染器，显示加减按钮
    private class QuantityCellRenderer implements TableCellRenderer {
        private JPanel panel;
        private JButton minusButton;
        private JTextField quantityField;
        private JButton plusButton;
        
        public QuantityCellRenderer() {
            panel = new JPanel(new BorderLayout());
            minusButton = new JButton("-");
            quantityField = new JTextField();
            quantityField.setEditable(false);
            quantityField.setHorizontalAlignment(JTextField.CENTER);
            plusButton = new JButton("+");
            
            panel.add(minusButton, BorderLayout.WEST);
            panel.add(quantityField, BorderLayout.CENTER);
            panel.add(plusButton, BorderLayout.EAST);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value != null) {
                quantityField.setText(value.toString());
            }
            return panel;
        }
    }
    
    // 数量单元格编辑器，处理按钮点击事件
    private class QuantityCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton minusButton;
        private JTextField quantityField;
        private JButton plusButton;
        private int currentValue;
        private int row;
        
        public QuantityCellEditor() {
            panel = new JPanel(new BorderLayout());
            minusButton = new JButton("-");
            quantityField = new JTextField();
            quantityField.setEditable(false);
            quantityField.setHorizontalAlignment(JTextField.CENTER);
            plusButton = new JButton("+");
            
            panel.add(minusButton, BorderLayout.WEST);
            panel.add(quantityField, BorderLayout.CENTER);
            panel.add(plusButton, BorderLayout.EAST);
            
            // 减号按钮监听器
            minusButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentValue > 1) {
                        currentValue--;
                        quantityField.setText(String.valueOf(currentValue));
                        updateQuantity();
                    }
                }
            });
            
            // 加号按钮监听器
            plusButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentValue++;
                    quantityField.setText(String.valueOf(currentValue));
                    updateQuantity();
                }
            });
        }
        
        private void updateQuantity() {
            String productId = (String) tableModel.getValueAt(row, 0);
            UpdateQuantityCommand command = new UpdateQuantityCommand(cartService, productId, currentValue);
            commandManager.executeCommand(command);
        }
        
        @Override
        public Object getCellEditorValue() {
            return currentValue;
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value != null) {
                try { currentValue = Integer.parseInt(value.toString()); } catch (NumberFormatException ex) { currentValue = 1; }
                quantityField.setText(value.toString());
            }
            this.row = row;
            return panel;
        }
    }
}
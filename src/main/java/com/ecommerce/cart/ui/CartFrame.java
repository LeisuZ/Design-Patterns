package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.pattern.command.CommandManager;
import com.ecommerce.cart.pattern.command.AddProductCommand;
import com.ecommerce.cart.pattern.command.RemoveProductCommand;
import com.ecommerce.cart.pattern.command.UpdateQuantityCommand;
import com.ecommerce.cart.pattern.observer.Observer;
import com.ecommerce.cart.service.CartService;
import com.ecommerce.cart.service.PromotionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CartFrame extends JFrame implements Observer {
    private CartService cartService;
    private PromotionService promotionService;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton removeButton;
    private JButton checkoutButton;
    private JButton undoButton;
    private JLabel totalLabel;
    private CommandManager commandManager;
    
    public CartFrame() {
        cartService = new CartService();
        promotionService = new PromotionService();
        commandManager = new CommandManager();
        // 注册为购物车的观察者
        cartService.getCart().registerObserver(this);
        initUI();
        populateTestData();
    }
    
    private void initUI() {
        setTitle("购物车");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 表格部分
        String[] columnNames = {"商品ID", "商品名称", "分类", "单价", "数量", "小计"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 只有数量列可编辑
                return column == 4;
            }
        };
        cartTable = new JTable(tableModel);
        // 添加单元格编辑监听器
        cartTable.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column == 4) { // 数量列
                String productId = (String) tableModel.getValueAt(row, 0);
                Object value = tableModel.getValueAt(row, column);
                try {
                    int quantity = Integer.parseInt(value.toString());
                    if (quantity > 0) {
                        UpdateQuantityCommand command = new UpdateQuantityCommand(cartService, productId, quantity);
                        commandManager.executeCommand(command);
                    } else {
                        JOptionPane.showMessageDialog(CartFrame.this, "数量必须大于0");
                        // 恢复原来的值
                        updateCartTable();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CartFrame.this, "请输入正确的数字");
                    // 恢复原来的值
                    updateCartTable();
                }
            }
        });
        // 设置表格列宽
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(80); // 商品ID
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(150); // 商品名称
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(100); // 分类
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(80); // 单价
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(80); // 数量
        cartTable.getColumnModel().getColumn(5).setPreferredWidth(100); // 小计
        
        // 设置表头样式
        cartTable.getTableHeader().setFont(new Font("宋体", Font.BOLD, 12));
        cartTable.getTableHeader().setBackground(new Color(240, 240, 240));
        
        // 设置表格行高
        cartTable.setRowHeight(30);
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 底部操作栏
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // 总价显示
        JPanel totalPanel = new JPanel();
        totalLabel = new JLabel("总价: ¥0.00");
        totalLabel.setFont(new Font("宋体", Font.BOLD, 16));
        totalLabel.setForeground(new Color(255, 0, 0));
        totalPanel.add(totalLabel);
        bottomPanel.add(totalPanel, BorderLayout.WEST);
        
        // 按钮组 - 使用FlowLayout并设置水平间距
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        addButton = new JButton("添加商品");
        removeButton = new JButton("删除商品");
        checkoutButton = new JButton("结算");
        checkoutButton.setForeground(new Color(255, 255, 255));
        checkoutButton.setBackground(new Color(0, 128, 0));
        JButton logButton = new JButton("查看日志");
        JButton addressButton = new JButton("地址管理");
        JButton orderHistoryButton = new JButton("订单历史");
        undoButton = new JButton("撤销");
        
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(logButton);
        buttonPanel.add(addressButton);
        buttonPanel.add(orderHistoryButton);
        buttonPanel.add(undoButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
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
                new CheckoutFrame(cartService.getCart(), promotionService).setVisible(true);
                dispose();
            }
        });
        
        logButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LogViewerFrame().setVisible(true);
            }
        });
        
        addressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddressFrame().setVisible(true);
            }
        });
        
        orderHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OrderHistoryFrame().setVisible(true);
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
        
        // 使用带边距的面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 15));
        
        // 改进商品类型选项，使用更友好的名称
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"电子产品", "服装", "食品", "图书"});
        typeComboBox.setFont(new Font("宋体", Font.PLAIN, 14));
        
        JTextField idField = new JTextField();
        idField.setFont(new Font("宋体", Font.PLAIN, 14));
        
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("宋体", Font.PLAIN, 14));
        
        JTextField priceField = new JTextField();
        priceField.setFont(new Font("宋体", Font.PLAIN, 14));
        
        JTextField quantityField = new JTextField();
        quantityField.setFont(new Font("宋体", Font.PLAIN, 14));
        
        JLabel typeLabel = new JLabel("商品类型:");
        typeLabel.setFont(new Font("宋体", Font.BOLD, 14));
        
        JLabel idLabel = new JLabel("商品ID:");
        idLabel.setFont(new Font("宋体", Font.BOLD, 14));
        
        JLabel nameLabel = new JLabel("商品名称:");
        nameLabel.setFont(new Font("宋体", Font.BOLD, 14));
        
        JLabel priceLabel = new JLabel("单价:");
        priceLabel.setFont(new Font("宋体", Font.BOLD, 14));
        
        JLabel quantityLabel = new JLabel("数量:");
        quantityLabel.setFont(new Font("宋体", Font.BOLD, 14));
        
        panel.add(typeLabel);
        panel.add(typeComboBox);
        panel.add(idLabel);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(priceLabel);
        panel.add(priceField);
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
                    String type = (String) typeComboBox.getSelectedItem();
                    // 将中文类型转换为英文类型
                    String englishType = type;
                    switch (type) {
                        case "电子产品":
                            englishType = "electronics";
                            break;
                        case "服装":
                            englishType = "clothing";
                            break;
                        case "食品":
                            englishType = "food";
                            break;
                        case "图书":
                            englishType = "books";
                            break;
                    }
                    String id = idField.getText();
                    String name = nameField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());
                    
                    AddProductCommand command = new AddProductCommand(cartService, englishType, id, name, price, quantity);
                    commandManager.executeCommand(command);
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "请输入正确的数字");
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
    
    private void updateCartTable() {
        tableModel.setRowCount(0);
        for (CartItem item : cartService.getCart().getItems()) {
            Object[] row = {
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getCategory(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getSubtotal()
            };
            tableModel.addRow(row);
        }
        totalLabel.setText("总价: ¥" + String.format("%.2f", cartService.getTotal()));
    }
    
    private void populateTestData() {
        // 只有当购物车为空时才添加测试数据
        if (cartService.getCart().getItems().isEmpty()) {
            cartService.addProduct("electronics", "E001", "手机", 1999.99, 1);
            cartService.addProduct("clothing", "C001", "T恤", 99.99, 2);
            cartService.addProduct("food", "F001", "零食", 49.99, 3);
        }
        updateCartTable();
    }
    
    @Override
    public void update() {
        // 当购物车发生变化时，更新UI
        updateCartTable();
    }
    

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CartFrame().setVisible(true);
            }
        });
    }
}
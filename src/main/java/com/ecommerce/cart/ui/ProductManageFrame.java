package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Product;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProductManageFrame extends JFrame {
    private ProductService productService;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton backButton;
    
    public ProductManageFrame() {
        productService = ECommerceFacade.INSTANCE.getProductService();
        initUI();
    }

    private void initUI() {
        setTitle("商品管理");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columnNames = {"商品ID", "商品名称", "分类", "价格"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("商品列表"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("新增商品");
        JButton editButton = new JButton("编辑商品");
        JButton deleteButton = new JButton("删除商品");
        backButton = new JButton("返回");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showProductDialog(null);
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String id = (String) tableModel.getValueAt(selectedRow, 0);
                    Product product = productService.getProductById(id);
                    if (product != null) {
                        showProductDialog(product);
                    }
                } else {
                    JOptionPane.showMessageDialog(ProductManageFrame.this, "请选择要编辑的商品");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String id = (String) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(ProductManageFrame.this, "确定要删除这个商品吗？");
                    if (confirm == JOptionPane.YES_OPTION) {
                        productService.deleteProduct(id);
                        ECommerceFacade.INSTANCE.getCartService().removeProduct(id);
                        populateTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(ProductManageFrame.this, "请选择要删除的商品");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        populateTableData();
    }

    public void refreshData() {
        populateTableData();
    }

    public JButton getBackButton() {
        return backButton;
    }
    
    public void showProductDialog(Product product) {
        JDialog dialog = new JDialog(this, product == null ? "新增商品" : "编辑商品", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField idField = new JTextField(product != null ? product.getId() : "");
        if (product != null) idField.setEditable(false);
        JTextField nameField = new JTextField(product != null ? product.getName() : "");
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"电子产品", "服装", "食品", "图书"});
        if (product != null) categoryCombo.setSelectedItem(com.ecommerce.cart.util.CategoryUtil.toDisplayName(product.getCategory()));
        JTextField priceField = new JTextField(product != null ? String.valueOf(product.getPrice()) : "");

        panel.add(new JLabel("商品ID:"));
        panel.add(idField);
        panel.add(new JLabel("商品名称:"));
        panel.add(nameField);
        panel.add(new JLabel("分类:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("价格:"));
        panel.add(priceField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton confirmButton = new JButton("确认");
        JButton cancelButton = new JButton("取消");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String id = idField.getText().trim();
                    String name = nameField.getText().trim();
                    String category = com.ecommerce.cart.util.CategoryUtil.toEnglishName((String) categoryCombo.getSelectedItem());
                    double price = Double.parseDouble(priceField.getText().trim());

                    if (id.isEmpty() || name.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "请填写所有必填字段");
                        return;
                    }

                    if (product == null) {
                        productService.addProduct(category, id, name, price);
                    } else {
                        productService.updateProduct(id, name, price, category);
                    }
                    populateTableData();
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "请输入正确的价格");
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

    private void populateTableData() {
        tableModel.setRowCount(0);
        for (Product p : productService.getAllProducts()) {
            Object[] row = {p.getId(), p.getName(), com.ecommerce.cart.util.CategoryUtil.toDisplayName(p.getCategory()), String.format("¥%.2f", p.getPrice())};
            tableModel.addRow(row);
        }
    }
}

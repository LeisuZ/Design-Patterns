package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Product;
import com.ecommerce.cart.model.Review;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.pattern.observer.EventBus;
import com.ecommerce.cart.service.CartService;
import com.ecommerce.cart.service.ProductService;
import com.ecommerce.cart.service.ReviewService;
import com.ecommerce.cart.pattern.decorator.DiscountDecorator;
import com.ecommerce.cart.pattern.decorator.PromotionDecorator;
import com.ecommerce.cart.pattern.decorator.ProductDecorator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ProductBrowseFrame extends JFrame {
    private ProductService productService;
    private CartService cartService;
    private ReviewService reviewService;
    private CartFrame parentCartFrame;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryComboBox;
    private JTextField searchField;
    private java.util.Map<String, ProductDecorator> decoratedProducts = new java.util.HashMap<>();

    public ProductBrowseFrame(CartFrame parentCartFrame) {
        ECommerceFacade facade = ECommerceFacade.INSTANCE;
        this.productService = facade.getProductService();
        this.cartService = facade.getCartService();
        this.reviewService = facade.getReviewService();
        this.parentCartFrame = parentCartFrame;
        initUI();
    }

    public ProductBrowseFrame() {
        this(null);
    }

    private void initUI() {
        setTitle("商品浏览");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("分类:"));
        categoryComboBox = new JComboBox<>(new String[]{"全部", "电子产品", "服装", "食品", "图书"});
        categoryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterProducts();
            }
        });
        filterPanel.add(categoryComboBox);

        filterPanel.add(new JLabel("搜索:"));
        searchField = new JTextField(20);
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterProducts();
            }
        });
        filterPanel.add(searchField);

        JButton searchButton = new JButton("搜索");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterProducts();
            }
        });
        filterPanel.add(searchButton);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        String[] columnNames = {"商品ID", "商品名称", "分类", "价格", "平均评分"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showProductDetail();
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("商品列表"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addToCartButton = new JButton("加入购物车");
        JButton detailButton = new JButton("查看详情");
        JButton discountDecoratorButton = new JButton("应用折扣装饰 [装饰器模式]");
        JButton promotionDecoratorButton = new JButton("应用促销装饰 [装饰器模式]");
        JButton clearDecoratorButton = new JButton("清除装饰");
        JButton backButton = new JButton("返回购物车");

        discountDecoratorButton.setForeground(new Color(0, 0, 200));
        promotionDecoratorButton.setForeground(new Color(0, 0, 200));

        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String productId = (String) tableModel.getValueAt(selectedRow, 0);
                    Product product = productService.getProductById(productId);
                    if (product != null) {
                        String quantityStr = JOptionPane.showInputDialog(ProductBrowseFrame.this, "请输入数量:", "1");
                        if (quantityStr != null) {
                            try {
                                int quantity = Integer.parseInt(quantityStr);
                                if (quantity > 0) {
                                    com.ecommerce.cart.model.ProductInterface productToAdd = decoratedProducts.containsKey(productId) ? decoratedProducts.get(productId) : product;
                                    cartService.addProduct(product.getCategory(), product.getId(), productToAdd.getName(), productToAdd.getPrice(), quantity);
                                    if (decoratedProducts.containsKey(productId)) {
                                        com.ecommerce.cart.model.CartItem lastItem = cartService.getCart().getItems().get(cartService.getCart().getItems().size() - 1);
                                        lastItem.setDecoratedName(productToAdd.getName());
                                        lastItem.setDecoratedPrice(productToAdd.getPrice());
                                    }
                                    EventBus.getInstance().publishEvent("CART_CHANGED", product.getId());
                                    JOptionPane.showMessageDialog(ProductBrowseFrame.this, "已添加到购物车: " + productToAdd.getName() + " x" + quantity + "\n[装饰器模式] 装饰效果已传递到购物车");
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(ProductBrowseFrame.this, "请输入有效数量");
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(ProductBrowseFrame.this, "请选择商品");
                }
            }
        });

        detailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showProductDetail();
            }
        });

        discountDecoratorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String productId = (String) tableModel.getValueAt(selectedRow, 0);
                    Product product = productService.getProductById(productId);
                    if (product != null) {
                        String rateStr = JOptionPane.showInputDialog(ProductBrowseFrame.this, "请输入折扣率(如0.1表示9折):", "0.1");
                        if (rateStr != null) {
                            try {
                                double rate = Double.parseDouble(rateStr);
                                if (rate > 0 && rate < 1) {
                                    com.ecommerce.cart.model.ProductInterface base = decoratedProducts.containsKey(productId) ? decoratedProducts.get(productId) : product;
                                    DiscountDecorator decorator = new DiscountDecorator(base, rate);
                                    decoratedProducts.put(productId, decorator);
                                    refreshTableWithDecorators();
                                    JOptionPane.showMessageDialog(ProductBrowseFrame.this, "[装饰器模式] 已应用折扣装饰: " + decorator.getName() + "\n装饰后价格: ¥" + String.format("%.2f", decorator.getPrice()));
                                } else {
                                    JOptionPane.showMessageDialog(ProductBrowseFrame.this, "折扣率必须在0-1之间");
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(ProductBrowseFrame.this, "请输入有效数字");
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(ProductBrowseFrame.this, "请选择商品");
                }
            }
        });

        promotionDecoratorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String productId = (String) tableModel.getValueAt(selectedRow, 0);
                    Product product = productService.getProductById(productId);
                    if (product != null) {
                        String promoName = JOptionPane.showInputDialog(ProductBrowseFrame.this, "请输入促销名称:", "限时特惠");
                        String discountStr = JOptionPane.showInputDialog(ProductBrowseFrame.this, "请输入优惠金额:", "50");
                        if (promoName != null && discountStr != null) {
                            try {
                                double discount = Double.parseDouble(discountStr);
                                if (discount > 0) {
                                    com.ecommerce.cart.model.ProductInterface base = decoratedProducts.containsKey(productId) ? decoratedProducts.get(productId) : product;
                                    PromotionDecorator decorator = new PromotionDecorator(base, promoName, discount);
                                    decoratedProducts.put(productId, decorator);
                                    refreshTableWithDecorators();
                                    JOptionPane.showMessageDialog(ProductBrowseFrame.this, "[装饰器模式] 已应用促销装饰: " + decorator.getName() + "\n装饰后价格: ¥" + String.format("%.2f", decorator.getPrice()));
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(ProductBrowseFrame.this, "请输入有效金额");
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(ProductBrowseFrame.this, "请选择商品");
                }
            }
        });

        clearDecoratorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String productId = (String) tableModel.getValueAt(selectedRow, 0);
                    if (decoratedProducts.containsKey(productId)) {
                        decoratedProducts.remove(productId);
                        refreshTableWithDecorators();
                        JOptionPane.showMessageDialog(ProductBrowseFrame.this, "已清除装饰，恢复原始价格");
                    }
                } else {
                    JOptionPane.showMessageDialog(ProductBrowseFrame.this, "请选择商品");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(addToCartButton);
        buttonPanel.add(detailButton);
        buttonPanel.add(discountDecoratorButton);
        buttonPanel.add(promotionDecoratorButton);
        buttonPanel.add(clearDecoratorButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        populateTableData();
    }

    private void populateTableData() {
        tableModel.setRowCount(0);
        for (Product product : productService.getAllProducts()) {
            com.ecommerce.cart.model.ProductInterface displayProduct = decoratedProducts.containsKey(product.getId()) ? decoratedProducts.get(product.getId()) : product;
            double avgRating = reviewService.getAverageRating(product.getId());
            Object[] row = {
                product.getId(),
                displayProduct.getName(),
                com.ecommerce.cart.util.CategoryUtil.toDisplayName(product.getCategory()),
                String.format("¥%.2f", displayProduct.getPrice()),
                String.format("%.1f", avgRating)
            };
            tableModel.addRow(row);
        }
    }

    private void filterProducts() {
        String category = (String) categoryComboBox.getSelectedItem();
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0);

        List<Product> filtered;
        if (!"全部".equals(category)) {
            String categoryEn = com.ecommerce.cart.util.CategoryUtil.toEnglishName(category);
            filtered = productService.getProductsByCategory(categoryEn);
        } else {
            filtered = productService.getAllProducts();
        }

        for (Product product : filtered) {
            if (keyword.isEmpty() ||
                product.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                product.getId().toLowerCase().contains(keyword.toLowerCase())) {
                com.ecommerce.cart.model.ProductInterface displayProduct = decoratedProducts.containsKey(product.getId()) ? decoratedProducts.get(product.getId()) : product;
                double avgRating = reviewService.getAverageRating(product.getId());
                Object[] row = {
                    product.getId(),
                    displayProduct.getName(),
                    com.ecommerce.cart.util.CategoryUtil.toDisplayName(product.getCategory()),
                    String.format("¥%.2f", displayProduct.getPrice()),
                    String.format("%.1f", avgRating)
                };
                tableModel.addRow(row);
            }
        }
    }

    private void refreshTableWithDecorators() {
        String category = (String) categoryComboBox.getSelectedItem();
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0);

        List<Product> filtered;
        if (!"全部".equals(category)) {
            String categoryEn = com.ecommerce.cart.util.CategoryUtil.toEnglishName(category);
            filtered = productService.getProductsByCategory(categoryEn);
        } else {
            filtered = productService.getAllProducts();
        }

        for (Product product : filtered) {
            if (keyword.isEmpty() ||
                product.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                product.getId().toLowerCase().contains(keyword.toLowerCase())) {
                com.ecommerce.cart.model.ProductInterface displayProduct = decoratedProducts.containsKey(product.getId()) ? decoratedProducts.get(product.getId()) : product;
                double avgRating = reviewService.getAverageRating(product.getId());
                Object[] row = {
                    product.getId(),
                    displayProduct.getName(),
                    com.ecommerce.cart.util.CategoryUtil.toDisplayName(product.getCategory()),
                    String.format("¥%.2f", displayProduct.getPrice()),
                    String.format("%.1f", avgRating)
                };
                tableModel.addRow(row);
            }
        }
    }

    private void showProductDetail() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请选择商品");
            return;
        }
        String productId = (String) tableModel.getValueAt(selectedRow, 0);
        Product product = productService.getProductById(productId);
        if (product == null) return;
        new ProductDetailDialog(this, product, reviewService).setVisible(true);
    }

}

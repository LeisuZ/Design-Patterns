package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Product;
import com.ecommerce.cart.model.Review;
import com.ecommerce.cart.service.ReviewService;
import com.ecommerce.cart.util.CategoryUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductDetailDialog extends JDialog {
    public ProductDetailDialog(JFrame owner, Product product, ReviewService reviewService) {
        super(owner, "商品详情", true);
        setSize(600, 500);
        setLocationRelativeTo(owner);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("商品信息"));
        infoPanel.add(new JLabel("商品ID:"));
        infoPanel.add(new JLabel(product.getId()));
        infoPanel.add(new JLabel("商品名称:"));
        infoPanel.add(new JLabel(product.getName()));
        infoPanel.add(new JLabel("分类:"));
        infoPanel.add(new JLabel(CategoryUtil.toDisplayName(product.getCategory())));
        infoPanel.add(new JLabel("价格:"));
        infoPanel.add(new JLabel(String.format("¥%.2f", product.getPrice())));
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        JPanel reviewPanel = new JPanel(new BorderLayout(10, 10));
        reviewPanel.setBorder(BorderFactory.createTitledBorder("商品评价"));
        List<Review> reviews = reviewService.getReviewsByProductId(product.getId());
        if (reviews.isEmpty()) {
            reviewPanel.add(new JLabel("暂无评价"), BorderLayout.CENTER);
        } else {
            double avgRating = reviewService.getAverageRating(product.getId());
            reviewPanel.add(new JLabel("平均评分: " + String.format("%.1f", avgRating) + " 星"), BorderLayout.NORTH);
            JTextArea reviewArea = new JTextArea();
            reviewArea.setEditable(false);
            reviewArea.setLineWrap(true);
            reviewArea.setWrapStyleWord(true);
            StringBuilder sb = new StringBuilder();
            for (Review r : reviews) {
                sb.append("用户: ").append(r.getUserName()).append("\n");
                sb.append("评分: ").append(r.getRating()).append(" 星\n");
                sb.append("评价: ").append(r.getContent()).append("\n");
                sb.append("时间: ").append(r.getCreateTime()).append("\n");
                sb.append("-----------------------------------\n");
            }
            reviewArea.setText(sb.toString());
            reviewPanel.add(new JScrollPane(reviewArea), BorderLayout.CENTER);
        }
        mainPanel.add(reviewPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(ev -> dispose());
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}

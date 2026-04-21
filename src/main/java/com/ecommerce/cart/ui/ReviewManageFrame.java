package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Review;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.service.ReviewService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ReviewManageFrame extends JFrame {
    private ReviewService reviewService;
    private JTable reviewTable;
    private DefaultTableModel tableModel;
    private JTextField filterField;
    private JButton backButton;

    public ReviewManageFrame() {
        reviewService = ECommerceFacade.INSTANCE.getReviewService();
        initUI();
    }

    private void initUI() {
        setTitle("评价管理");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("按商品ID/名称筛选:"));
        filterField = new JTextField(20);
        filterPanel.add(filterField);
        JButton filterButton = new JButton("筛选");
        JButton resetButton = new JButton("重置");
        filterPanel.add(filterButton);
        filterPanel.add(resetButton);
        mainPanel.add(filterPanel, BorderLayout.NORTH);

        String[] columnNames = {"评价ID", "商品名称", "用户", "评分", "内容", "时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reviewTable = new JTable(tableModel);
        reviewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(reviewTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("评价列表"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteButton = new JButton("删除评价");
        backButton = new JButton("返回");

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = reviewTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String reviewId = (String) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(ReviewManageFrame.this, "确定要删除这条评价吗？");
                    if (confirm == JOptionPane.YES_OPTION) {
                        reviewService.deleteReview(reviewId);
                        populateTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(ReviewManageFrame.this, "请选择要删除的评价");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                populateTableData();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterField.setText("");
                populateTableData();
            }
        });

        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        populateTableData();
    }

    private void populateTableData() {
        tableModel.setRowCount(0);
        String keyword = filterField.getText().trim().toLowerCase();
        List<Review> reviews = reviewService.getReviews();

        for (Review r : reviews) {
            if (keyword.isEmpty() ||
                r.getProductId().toLowerCase().contains(keyword) ||
                r.getProductName().toLowerCase().contains(keyword)) {
                String content = r.getContent();
                if (content.length() > 30) {
                    content = content.substring(0, 30) + "...";
                }
                Object[] row = {r.getId(), r.getProductName(), r.getUserName(), r.getRating() + "星", content, r.getCreateTime()};
                tableModel.addRow(row);
            }
        }
    }
    
    public void refreshData() {
        populateTableData();
    }

    public JButton getBackButton() {
        return backButton;
    }
}

package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.PromotionRule;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.service.PromotionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PromotionManageFrame extends JFrame {
    private CartFrame parentCartFrame;
    private PromotionService promotionService;
    private JTable promotionTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton backButton;
    
    public PromotionManageFrame(CartFrame parentCartFrame) {
        this.parentCartFrame = parentCartFrame;
        promotionService = ECommerceFacade.INSTANCE.getPromotionService();
        initUI();
    }

    public PromotionManageFrame() {
        this(null);
    }
    
    private void initUI() {
        setTitle("促销管理");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 表格部分
        String[] columnNames = {"规则ID", "规则名称", "类型", "值", "最低消费", "优先级"};
        tableModel = new DefaultTableModel(columnNames, 0);
        promotionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(promotionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("促销规则列表"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮组
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("新增规则");
        editButton = new JButton("修改规则");
        deleteButton = new JButton("删除规则");
        backButton = new JButton("返回购物车");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 填充表格数据
        populateTableData();
        
        // 按钮监听器
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPromotionDialog(null);
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = promotionTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String ruleId = (String) tableModel.getValueAt(selectedRow, 0);
                    for (PromotionRule rule : promotionService.getPromotionRules()) {
                        if (rule.getId().equals(ruleId)) {
                            showPromotionDialog(rule);
                            break;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(PromotionManageFrame.this, "请选择要修改的规则");
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = promotionTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String ruleId = (String) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(PromotionManageFrame.this, "确定要删除这条规则吗？");
                    if (confirm == JOptionPane.YES_OPTION) {
                        promotionService.removePromotionRule(ruleId);
                        populateTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(PromotionManageFrame.this, "请选择要删除的规则");
                }
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parentCartFrame != null) {
                    parentCartFrame.setVisible(true);
                } else {
                    new CartFrame().setVisible(true);
                }
                dispose();
            }
        });
    }
    
    private void showPromotionDialog(PromotionRule rule) {
        JDialog dialog = new JDialog(this, rule == null ? "新增规则" : "修改规则", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"满减", "VIP折扣", "优惠券", "满赠", "限时折扣"});
        JTextField valueField = new JTextField();
        JTextField minSpendField = new JTextField();
        JTextField priorityField = new JTextField();
        
        if (rule != null) {
            idField.setText(rule.getId());
            idField.setEditable(false);
            nameField.setText(rule.getName());
            typeComboBox.setSelectedItem(com.ecommerce.cart.util.CategoryUtil.getPromotionTypeDisplayName(rule.getType()));
            valueField.setText(String.valueOf(rule.getValue()));
            minSpendField.setText(String.valueOf(rule.getMinSpend()));
            priorityField.setText(String.valueOf(rule.getPriority()));
        }
        
        panel.add(new JLabel("规则ID:"));
        panel.add(idField);
        panel.add(new JLabel("规则名称:"));
        panel.add(nameField);
        panel.add(new JLabel("规则类型:"));
        panel.add(typeComboBox);
        panel.add(new JLabel("值:"));
        panel.add(valueField);
        panel.add(new JLabel("最低消费:"));
        panel.add(minSpendField);
        panel.add(new JLabel("优先级:"));
        panel.add(priorityField);
        
        JPanel buttonPanel = new JPanel();
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
                    if (id.isEmpty() || name.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "请填写规则ID和名称");
                        return;
                    }
                    String type = com.ecommerce.cart.util.CategoryUtil.getPromotionTypeEnglishName((String) typeComboBox.getSelectedItem());
                    double value = Double.parseDouble(valueField.getText());
                    double minSpend = Double.parseDouble(minSpendField.getText());
                    int priority = Integer.parseInt(priorityField.getText());
                    
                    PromotionRule newRule = new PromotionRule(id, name, type, value, minSpend, priority);
                    if (rule == null) {
                        promotionService.addPromotionRule(newRule);
                    } else {
                        promotionService.updatePromotionRule(rule.getId(), newRule);
                    }
                    populateTableData();
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
    
    private void populateTableData() {
        tableModel.setRowCount(0);
        for (PromotionRule rule : promotionService.getPromotionRules()) {
            Object[] row = {
                rule.getId(),
                rule.getName(),
                com.ecommerce.cart.util.CategoryUtil.getPromotionTypeDisplayName(rule.getType()),
                rule.getValue(),
                rule.getMinSpend(),
                rule.getPriority()
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
}
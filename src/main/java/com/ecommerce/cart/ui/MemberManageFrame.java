package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Member;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.service.MemberService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MemberManageFrame extends JFrame {
    private MemberService memberService;
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JButton viewButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton backButton;
    
    public MemberManageFrame() {
        memberService = ECommerceFacade.INSTANCE.getMemberService();
        initUI();
    }
    
    private void initUI() {
        setTitle("会员管理");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 表格部分
        String[] columnNames = {"会员ID", "姓名", "电话", "邮箱", "等级", "积分", "总消费"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("会员列表"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮组
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("新增会员");
        viewButton = new JButton("查看详情");
        editButton = new JButton("编辑会员");
        deleteButton = new JButton("删除会员");
        backButton = new JButton("返回");
        
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 填充表格数据
        populateTableData();
        
        // 按钮监听器
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = memberTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String memberId = (String) tableModel.getValueAt(selectedRow, 0);
                    Member member = memberService.getMemberById(memberId);
                    if (member != null) {
                        showMemberDetailDialog(member);
                    }
                } else {
                    JOptionPane.showMessageDialog(MemberManageFrame.this, "请选择要查看的会员");
                }
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = memberTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String memberId = (String) tableModel.getValueAt(selectedRow, 0);
                    Member member = memberService.getMemberById(memberId);
                    if (member != null) {
                        showMemberEditDialog(member);
                    }
                } else {
                    JOptionPane.showMessageDialog(MemberManageFrame.this, "请选择要编辑的会员");
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = memberTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String memberId = (String) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(MemberManageFrame.this, "确定要删除这个会员吗？");
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = memberService.deleteMember(memberId);
                        if (success) {
                            JOptionPane.showMessageDialog(MemberManageFrame.this, "会员删除成功");
                            populateTableData();
                        } else {
                            JOptionPane.showMessageDialog(MemberManageFrame.this, "会员删除失败");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(MemberManageFrame.this, "请选择要删除的会员");
                }
            }
        });
        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMemberAddDialog();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void showMemberDetailDialog(Member member) {
        JDialog dialog = new JDialog(this, "会员详情", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("会员ID:"));
        panel.add(new JLabel(member.getId()));
        panel.add(new JLabel("姓名:"));
        panel.add(new JLabel(member.getName()));
        panel.add(new JLabel("电话:"));
        panel.add(new JLabel(member.getPhone()));
        panel.add(new JLabel("邮箱:"));
        panel.add(new JLabel(member.getEmail()));
        panel.add(new JLabel("等级:"));
        panel.add(new JLabel(member.getLevel().getName()));
        panel.add(new JLabel("积分:"));
        panel.add(new JLabel(String.valueOf(member.getPoints())));
        panel.add(new JLabel("总消费:"));
        panel.add(new JLabel(String.format("%.2f", member.getTotalSpent())));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("关闭");
        buttonPanel.add(closeButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        dialog.setVisible(true);
    }
    
    private void showMemberEditDialog(Member member) {
        JDialog dialog = new JDialog(this, "编辑会员", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField idField = new JTextField(member.getId());
        idField.setEditable(false);
        JTextField nameField = new JTextField(member.getName());
        JTextField phoneField = new JTextField(member.getPhone());
        JTextField emailField = new JTextField(member.getEmail());
        JTextField pointsField = new JTextField(String.valueOf(member.getPoints()));
        JTextField totalSpentField = new JTextField(String.format("%.2f", member.getTotalSpent()));
        
        panel.add(new JLabel("会员ID:"));
        panel.add(idField);
        panel.add(new JLabel("姓名:"));
        panel.add(nameField);
        panel.add(new JLabel("电话:"));
        panel.add(phoneField);
        panel.add(new JLabel("邮箱:"));
        panel.add(emailField);
        panel.add(new JLabel("积分:"));
        panel.add(pointsField);
        panel.add(new JLabel("总消费:"));
        panel.add(totalSpentField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton confirmButton = new JButton("确认修改");
        JButton cancelButton = new JButton("取消");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    member.setName(nameField.getText());
                    member.setPhone(phoneField.getText());
                    member.setEmail(emailField.getText());
                    int points = Integer.parseInt(pointsField.getText());
                    double totalSpent = Double.parseDouble(totalSpentField.getText());
                    
                    member.setPoints(points);
                    member.setTotalSpent(totalSpent);
                    
                    memberService.updateMember(member);
                    JOptionPane.showMessageDialog(dialog, "会员信息更新成功");
                    dialog.dispose();
                    populateTableData();
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
    
    private void showMemberAddDialog() {
        JDialog dialog = new JDialog(this, "新增会员", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        
        panel.add(new JLabel("姓名:"));
        panel.add(nameField);
        panel.add(new JLabel("电话:"));
        panel.add(phoneField);
        panel.add(new JLabel("邮箱:"));
        panel.add(emailField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton confirmButton = new JButton("确认添加");
        JButton cancelButton = new JButton("取消");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    String phone = phoneField.getText();
                    String email = emailField.getText();
                    
                    if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "请填写所有必填字段");
                        return;
                    }
                    
                    Member member = memberService.addMember(name, phone, email);
                    if (member != null) {
                        JOptionPane.showMessageDialog(dialog, "会员添加成功，会员ID: " + member.getId());
                        dialog.dispose();
                        populateTableData();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "会员添加失败");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "添加会员时发生错误: " + ex.getMessage());
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
        for (Member member : memberService.getMembers()) {
            Object[] row = {
                member.getId(),
                member.getName(),
                member.getPhone(),
                member.getEmail(),
                member.getLevel().getName(),
                member.getPoints(),
                member.getTotalSpent()
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
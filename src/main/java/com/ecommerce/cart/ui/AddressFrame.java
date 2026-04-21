package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Address;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.service.AddressService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddressFrame extends JFrame {
    private AddressService addressService;
    private JTable addressTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton setDefaultButton;
    private JButton closeButton;
    
    public AddressFrame() {
        addressService = ECommerceFacade.INSTANCE.getAddressService();
        initUI();
        updateAddressTable();
    }
    
    public AddressFrame(AddressService addressService) {
        this.addressService = addressService;
        initUI();
        updateAddressTable();
    }
    
    private void initUI() {
        setTitle("地址管理");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 表格部分
        String[] columnNames = {"ID", "姓名", "电话", "省份", "城市", "区县", "详细地址", "默认"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        addressTable = new JTable(tableModel);
        addressTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(addressTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("添加地址");
        editButton = new JButton("编辑地址");
        deleteButton = new JButton("删除地址");
        setDefaultButton = new JButton("设为默认");
        closeButton = new JButton("关闭");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(setDefaultButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 按钮监听器
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddressDialog(null);
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = addressTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String id = (String) tableModel.getValueAt(selectedRow, 0);
                    for (Address address : addressService.getAddresses()) {
                        if (address.getId().equals(id)) {
                            showAddressDialog(address);
                            break;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(AddressFrame.this, "请选择要编辑的地址");
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = addressTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String id = (String) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(AddressFrame.this, "确定要删除这个地址吗？");
                    if (confirm == JOptionPane.YES_OPTION) {
                        addressService.deleteAddress(id);
                        updateAddressTable();
                    }
                } else {
                    JOptionPane.showMessageDialog(AddressFrame.this, "请选择要删除的地址");
                }
            }
        });
        
        setDefaultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = addressTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String id = (String) tableModel.getValueAt(selectedRow, 0);
                    for (Address address : addressService.getAddresses()) {
                        if (address.getId().equals(id)) {
                            addressService.updateAddress(
                                address.getId(),
                                address.getName(),
                                address.getPhone(),
                                address.getProvince(),
                                address.getCity(),
                                address.getDistrict(),
                                address.getDetailAddress(),
                                true
                            );
                            updateAddressTable();
                            break;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(AddressFrame.this, "请选择要设为默认的地址");
                }
            }
        });
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void showAddressDialog(Address address) {
        JDialog dialog = new JDialog(this, address == null ? "添加地址" : "编辑地址", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        
        JTextField nameField = new JTextField(address != null ? address.getName() : "");
        JTextField phoneField = new JTextField(address != null ? address.getPhone() : "");
        JTextField provinceField = new JTextField(address != null ? address.getProvince() : "");
        JTextField cityField = new JTextField(address != null ? address.getCity() : "");
        JTextField districtField = new JTextField(address != null ? address.getDistrict() : "");
        JTextField detailAddressField = new JTextField(address != null ? address.getDetailAddress() : "");
        JCheckBox defaultCheckBox = new JCheckBox("设为默认地址");
        if (address != null) {
            defaultCheckBox.setSelected(address.isDefault());
        }
        
        panel.add(new JLabel("姓名:"));
        panel.add(nameField);
        panel.add(new JLabel("电话:"));
        panel.add(phoneField);
        panel.add(new JLabel("省份:"));
        panel.add(provinceField);
        panel.add(new JLabel("城市:"));
        panel.add(cityField);
        panel.add(new JLabel("区县:"));
        panel.add(districtField);
        panel.add(new JLabel("详细地址:"));
        panel.add(detailAddressField);
        panel.add(new JLabel(""));
        panel.add(defaultCheckBox);
        
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
                String name = nameField.getText();
                String phone = phoneField.getText();
                String province = provinceField.getText();
                String city = cityField.getText();
                String district = districtField.getText();
                String detailAddress = detailAddressField.getText();
                boolean isDefault = defaultCheckBox.isSelected();
                
                if (name.isEmpty() || phone.isEmpty() || province.isEmpty() || city.isEmpty() || district.isEmpty() || detailAddress.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请填写所有必填字段");
                    return;
                }
                
                if (address == null) {
                    addressService.addAddress(name, phone, province, city, district, detailAddress, isDefault);
                } else {
                    addressService.updateAddress(address.getId(), name, phone, province, city, district, detailAddress, isDefault);
                }
                
                updateAddressTable();
                dialog.dispose();
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
    
    private void updateAddressTable() {
        tableModel.setRowCount(0);
        for (Address address : addressService.getAddresses()) {
            Object[] row = {
                address.getId(),
                address.getName(),
                address.getPhone(),
                address.getProvince(),
                address.getCity(),
                address.getDistrict(),
                address.getDetailAddress(),
                address.isDefault() ? "是" : "否"
            };
            tableModel.addRow(row);
        }
    }
}
package com.ecommerce.cart.ui;

import com.ecommerce.cart.model.Member;
import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.model.Product;
import com.ecommerce.cart.model.Review;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.service.MemberService;
import com.ecommerce.cart.service.OrderService;
import com.ecommerce.cart.service.ProductService;
import com.ecommerce.cart.service.PromotionService;
import com.ecommerce.cart.service.ReviewService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdminFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private MemberManageFrame memberManageFrame;
    private OrderManageFrame orderManageFrame;
    private PromotionManageFrame promotionManageFrame;
    private ProductManageFrame productManageFrame;
    private SalesStatsFrame salesStatsFrame;
    private ReviewManageFrame reviewManageFrame;
    private LogViewerFrame logViewerFrame;
    
    public AdminFrame() {
        initUI();
    }
    
    private void initUI() {
        setTitle("管理员管理系统");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("会员管理", createMemberManagePanel());
        tabbedPane.addTab("订单管理", createOrderManagePanel());
        tabbedPane.addTab("促销管理", createPromotionManagePanel());
        tabbedPane.addTab("商品管理", createProductManagePanel());
        tabbedPane.addTab("销售统计", createSalesStatsPanel());
        tabbedPane.addTab("评价管理", createReviewManagePanel());
        tabbedPane.addTab("系统日志", createLogViewerPanel());
        
        add(tabbedPane);

        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                switch (selectedIndex) {
                    case 0: if (memberManageFrame != null) memberManageFrame.refreshData(); break;
                    case 1: if (orderManageFrame != null) orderManageFrame.refreshData(); break;
                    case 2: if (promotionManageFrame != null) promotionManageFrame.refreshData(); break;
                    case 3: if (productManageFrame != null) productManageFrame.refreshData(); break;
                    case 4: if (salesStatsFrame != null) salesStatsFrame.refreshData(); break;
                    case 5: if (reviewManageFrame != null) reviewManageFrame.refreshData(); break;
                    case 6: if (logViewerFrame != null) logViewerFrame.refreshData(); break;
                }
            }
        });
    }
    
    private JPanel createMemberManagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        MemberManageFrame frame = memberManageFrame = new MemberManageFrame();
        frame.getBackButton().setVisible(false);
        panel.add(frame.getContentPane(), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createOrderManagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        OrderManageFrame frame = orderManageFrame = new OrderManageFrame();
        frame.getBackButton().setVisible(false);
        panel.add(frame.getContentPane(), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createPromotionManagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        PromotionManageFrame frame = promotionManageFrame = new PromotionManageFrame(null);
        frame.getBackButton().setVisible(false);
        panel.add(frame.getContentPane(), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createProductManagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ProductManageFrame frame = productManageFrame = new ProductManageFrame();
        frame.getBackButton().setVisible(false);
        panel.add(frame.getContentPane(), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createSalesStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        SalesStatsFrame frame = salesStatsFrame = new SalesStatsFrame();
        frame.getBackButton().setVisible(false);
        panel.add(frame.getContentPane(), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createReviewManagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ReviewManageFrame frame = reviewManageFrame = new ReviewManageFrame();
        frame.getBackButton().setVisible(false);
        panel.add(frame.getContentPane(), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createLogViewerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        LogViewerFrame frame = logViewerFrame = new LogViewerFrame();
        frame.getBackButton().setVisible(false);
        panel.add(frame.getContentPane(), BorderLayout.CENTER);
        return panel;
    }
}

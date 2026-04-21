package com.ecommerce.cart.pattern.command;

import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.pattern.singleton.Logger;
import com.ecommerce.cart.service.MemberService;
import com.ecommerce.cart.service.OrderService;
import com.ecommerce.cart.service.PaymentService;

public class OrderCommand implements Command {
    private Order order;
    private CommandManager commandManager;
    private String paymentType;
    private double originalCouponDiscount;
    private double originalFullReduction;
    private double originalVipDiscount;
    private double originalGiftDiscount;
    private double originalTimeLimitDiscount;
    private boolean executed = false;
    private Logger logger;

    public OrderCommand(Order order, CommandManager commandManager, String paymentType) {
        this.order = order;
        this.commandManager = commandManager;
        this.paymentType = paymentType;
        this.originalCouponDiscount = order.getCouponDiscount();
        this.originalFullReduction = order.getFullReduction();
        this.originalVipDiscount = order.getVipDiscount();
        this.originalGiftDiscount = order.getGiftDiscount();
        this.originalTimeLimitDiscount = order.getTimeLimitDiscount();
        this.logger = Logger.INSTANCE;
    }

    @Override
    public void execute() {
        if (executed) {
            return;
        }
        ECommerceFacade facade = ECommerceFacade.INSTANCE;
        PaymentService paymentService = facade.getPaymentService();
        OrderService orderService = facade.getOrderService();
        
        boolean paymentSuccess = paymentService.processPayment(order, paymentType);
        if (paymentSuccess) {
            order.setPaymentType(paymentType);
            order.pay();
            orderService.submitOrder(order);
            MemberService memberService = facade.getMemberService();
            if (order.getMemberId() != null && !order.getMemberId().isEmpty()) {
                memberService.spend(order.getMemberId(), order.getFinalAmount());
            }
            commandManager.addCommand(this);
            executed = true;
        }
    }

    @Override
    public void undo() {
        if (!executed) {
            return;
        }
        ECommerceFacade facade = ECommerceFacade.INSTANCE;
        
        PaymentService paymentService = facade.getPaymentService();
        String refundPaymentType = order.getPaymentType() != null ? order.getPaymentType() : paymentType;
        boolean refundSuccess = paymentService.processRefund(order, refundPaymentType);
        if (refundSuccess) {
            logger.log("订单撤销-退款成功: " + order.getOrderId());
        }
        
        order.cancel();
        logger.log("订单撤销-取消订单: " + order.getOrderId());
        
        if (order.getMemberId() != null && !order.getMemberId().isEmpty()) {
            MemberService memberService = facade.getMemberService();
            com.ecommerce.cart.model.Member member = memberService.getMemberById(order.getMemberId());
            if (member != null) {
                int pointsToDeduct = (int) (order.getFinalAmount() / 10);
                member.setPoints(Math.max(0, member.getPoints() - pointsToDeduct));
                memberService.updateMember(member);
                logger.log("订单撤销-回退积分: " + pointsToDeduct);
            }
        }
        
        order.setCouponDiscount(originalCouponDiscount);
        order.setFullReduction(originalFullReduction);
        order.setVipDiscount(originalVipDiscount);
        order.setGiftDiscount(originalGiftDiscount);
        order.setTimeLimitDiscount(originalTimeLimitDiscount);
        executed = false;
    }

    public Order getOrder() {
        return order;
    }
    
    public boolean isExecuted() {
        return executed;
    }
}

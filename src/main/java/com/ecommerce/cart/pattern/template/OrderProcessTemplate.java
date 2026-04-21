package com.ecommerce.cart.pattern.template;

import com.ecommerce.cart.model.Member;
import com.ecommerce.cart.model.Order;
import com.ecommerce.cart.pattern.chain.DiscountChainBuilder;
import com.ecommerce.cart.pattern.chain.Handler;
import com.ecommerce.cart.pattern.facade.ECommerceFacade;
import com.ecommerce.cart.pattern.singleton.Logger;
import com.ecommerce.cart.service.PromotionService;

public abstract class OrderProcessTemplate {
    protected Logger logger;
    protected PromotionService promotionService;
    protected Member member;
    
    public OrderProcessTemplate() {
        this.logger = Logger.INSTANCE;
    }
    
    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }
    
    public void setMember(Member member) {
        this.member = member;
    }
    
    public final void processOrder(Order order) {
        logger.log("[模板方法] 开始处理订单: " + order.getOrderId());
        logger.log("[模板方法] 步骤1: 验证订单");
        validateOrder(order);
        if (order.isPriceCalculated()) {
            logger.log("[模板方法] 步骤2: 计算价格(已计算，跳过)");
        } else {
            logger.log("[模板方法] 步骤2: 计算价格");
            calculatePrice(order);
        }
        if (needProcessPayment()) {
            logger.log("[模板方法] 步骤3: 处理支付");
            processPayment(order);
        }
        logger.log("[模板方法] 步骤4: 更新库存");
        updateInventory(order);
        logger.log("[模板方法] 步骤5: 生成订单");
        generateOrder(order);
        if (needSendNotification()) {
            logger.log("[模板方法] 步骤6: 发送通知");
            sendNotification(order);
        }
        logger.log("[模板方法] 订单处理完成: " + order.getOrderId());
    }
    
    protected abstract void validateOrder(Order order);
    
    protected abstract void calculatePrice(Order order);
    
    protected abstract void processPayment(Order order);
    
    protected abstract void updateInventory(Order order);
    
    protected abstract void generateOrder(Order order);
    
    protected abstract void sendNotification(Order order);
    
    protected boolean needProcessPayment() {
        return true;
    }
    
    protected boolean needSendNotification() {
        return true;
    }
    
    protected Handler buildDiscountChain() {
        if (promotionService != null) {
            return new DiscountChainBuilder(promotionService)
                .withMember(member)
                .build();
        }
        return null;
    }
}

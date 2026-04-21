# 电商购物车结算模块

> 本科设计模式课程项目 - 展示11种设计模式在电商场景中的实际应用

## 项目概述

本项目是一个基于 Java Swing 的电商购物车结算系统，以电商促销场景为载体，深度应用 **11种经典设计模式**，实现了购物车管理、促销计算、订单处理、会员系统、物流跟踪、商品评价等完整的电商核心功能。

### 核心特点

- **设计模式深度应用** - 每种模式都真正融入业务流程，展示模式的实际价值
- **完整的业务流程** - 覆盖购物车、结算、订单、会员、促销、物流、评价全流程
- **模式协作展示** - 多种设计模式协同工作，展示模式的组合使用
- **可视化演示** - 结算页面逐步展示职责链优惠计算过程

## 功能模块

| 模块    | 功能描述                          |
| ----- | ----------------------------- |
| 购物车管理 | 添加/删除/修改商品、分类筛选、商品搜索、操作撤销     |
| 促销管理  | 优惠券、满减、VIP折扣、限时折扣、满赠等多种促销类型   |
| 结算流程  | 分步骤引导（地址→优惠→支付→确认）、优惠可视化计算    |
| 订单管理  | 订单状态流转、历史查询、订单详情              |
| 会员系统  | 会员等级、积分累积、专属折扣                |
| 物流系统  | 物流跟踪、发货确认                     |
| 评价系统  | 商品评价、评分统计                     |
| 管理后台  | 会员管理、订单管理、促销管理、商品管理、销售统计、日志查看 |

## 技术栈

| 技术         | 说明           |
| ---------- | ------------ |
| Java 8+    | 开发语言         |
| Java Swing | UI框架         |
| 对象序列化      | 数据持久化        |
| 自定义Logger  | 日志系统（单例模式实现） |

## 快速开始

### 环境要求

- JDK 8 或更高版本

### 编译运行

**Windows PowerShell:**

```powershell
cd "vibecoding 设计模式"
$javaFiles = Get-ChildItem -Recurse -Filter *.java -Path src | Select-Object -ExpandProperty FullName
javac -d bin $javaFiles
java -cp bin com.ecommerce.cart.Main
```

**Linux/Mac:**

```bash
cd "vibecoding 设计模式"
find src -name "*.java" > sources.txt
javac -d bin @sources.txt
java -cp bin com.ecommerce.cart.Main
```

### 演示流程

1. **购物车页面** - 添加/删除/修改商品、选择会员、分类筛选、搜索商品
2. **结算页面** - 选择地址、优惠券、支付方式，观察优惠计算过程
3. **订单详情** - 查看订单信息、物流跟踪、商品评价
4. **管理员入口** - 切换角色至"管理员"，管理订单、会员、促销、商品

## 项目结构

```
com.ecommerce.cart
├── model/              # 实体类
│   ├── Product.java         # 商品
│   ├── Cart.java            # 购物车（Subject + EventBus发布者）
│   ├── Order.java           # 订单（状态模式）
│   ├── Member.java          # 会员
│   ├── Coupon.java          # 优惠券
│   ├── PromotionRule.java   # 促销规则
│   ├── Address.java         # 地址
│   ├── Logistics.java       # 物流
│   └── Review.java          # 评价
│
├── service/            # 业务逻辑层
│   ├── CartService.java     # 购物车服务
│   ├── OrderService.java    # 订单服务
│   ├── PromotionService.java # 促销服务
│   ├── PaymentService.java  # 支付服务
│   ├── MemberService.java   # 会员服务
│   ├── AddressService.java  # 地址服务
│   ├── LogisticsService.java # 物流服务
│   └── ReviewService.java   # 评价服务
│
├── ui/                 # 界面层
│   ├── CartFrame.java       # 购物车页面
│   ├── CheckoutFrame.java   # 结算页面
│   ├── ProductBrowseFrame.java # 商品浏览页面
│   ├── OrderHistoryFrame.java # 订单历史页面
│   ├── OrderDetailFrame.java # 订单详情页面
│   ├── AddressFrame.java    # 地址管理页面
│   ├── AdminFrame.java      # 管理员入口
│   └── ...                  # 其他管理页面
│
├── pattern/            # 设计模式实现
│   ├── singleton/      # 单例模式
│   ├── factory/        # 工厂模式 + 抽象工厂模式
│   ├── strategy/       # 策略模式
│   ├── chain/          # 职责链模式
│   ├── observer/       # 观察者模式
│   ├── command/        # 命令模式
│   ├── template/       # 模板方法模式
│   ├── state/          # 状态模式
│   ├── adapter/        # 适配器模式
│   ├── decorator/      # 装饰器模式
│   └── facade/         # 外观模式
│
├── util/               # 工具类
└── Main.java           # 程序入口
```

## 设计模式应用

### 模式总览

| 类型  | 设计模式   | 应用场景           | 核心类                                                  |
| --- | ------ | -------------- | ---------------------------------------------------- |
| 创建型 | 单例模式   | 日志、外观、事件总线、状态机 | Logger, ECommerceFacade, EventBus, OrderStateMachine |
| 创建型 | 工厂模式   | 商品创建、策略创建      | ECommerceFactory                                     |
| 创建型 | 抽象工厂模式 | 统一创建产品和策略      | AbstractFactory, ECommerceFactory                    |
| 结构型 | 适配器模式  | 多种支付方式适配       | PaymentAdapter, AlipayAdapter等                       |
| 结构型 | 装饰器模式  | 商品价格动态装饰       | DiscountDecorator, PromotionDecorator                |
| 结构型 | 外观模式   | 系统统一入口         | ECommerceFacade                                      |
| 行为型 | 策略模式   | 促销策略计算         | PromotionStrategy, StrategyContext                   |
| 行为型 | 职责链模式  | 折扣计算流程         | Handler, CouponHandler, FullReductionHandler         |
| 行为型 | 观察者模式  | 购物车状态通知        | Observer, Subject, EventBus                          |
| 行为型 | 命令模式   | 操作撤销、批处理       | Command, CommandManager                              |
| 行为型 | 模板方法模式 | 订单处理流程         | OrderProcessTemplate                                 |
| 行为型 | 状态模式   | 订单状态管理         | OrderState, OrderStateMachine                        |

### 核心模式详解

#### 1. 单例模式（Singleton）

**应用场景**：需要全局唯一实例的场景

```java
public enum Logger {
    INSTANCE;
    public void log(String message) { /* 记录日志 */ }
}

// 使用
Logger.INSTANCE.log("操作日志");
```

#### 2. 策略模式（Strategy）

**应用场景**：不同促销类型的折扣计算

```java
// 策略接口
public interface PromotionStrategy {
    double calculateDiscount(Order order);
}

// 策略上下文
StrategyContext context = new StrategyContext();
context.selectStrategy("COUPON");
double discount = context.executeStrategy(order);
```

#### 3. 职责链模式（Chain of Responsibility）

**应用场景**：购物车结算时的促销规则抵扣流程

```
优惠券处理 → 满减处理 → VIP折扣处理 → 最终金额
```

```java
Handler chain = new DiscountChainBuilder(promotionService)
    .withMember(currentMember)
    .build();
chain.handle(order);
```

#### 4. 命令模式（Command）

**应用场景**：购物车操作的撤销功能

```java
CommandManager manager = new CommandManager();
manager.executeCommand(new AddProductCommand(...));
manager.undo(); // 撤销操作
```

#### 5. 状态模式（State）

**应用场景**：订单状态管理

```
待支付 → 已支付 → 已发货 → 已完成
   ↓
 已取消
```

```java
OrderStateMachine.getInstance().initializeState(order);
OrderStateMachine.getInstance().processPayment(order);
```

### 模式协作关系

```
┌─────────────────────────────────────────────────────────────┐
│                    设计模式协作架构                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │ 外观模式     │───→│ 单例模式     │    │ 工厂模式     │     │
│  │ (统一入口)   │    │ (唯一实例)   │    │ (对象创建)   │     │
│  └─────────────┘    └─────────────┘    └─────────────┘     │
│         │                                      │            │
│         ↓                                      ↓            │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │ 观察者模式   │←───│ 职责链模式   │←───│ 策略模式     │     │
│  │ (状态通知)   │    │ (链式处理)   │    │ (算法封装)   │     │
│  └─────────────┘    └─────────────┘    └─────────────┘     │
│         │                  │                  │            │
│         ↓                  ↓                  ↓            │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │ 命令模式     │    │ 状态模式     │    │ 装饰器模式   │     │
│  │ (操作封装)   │    │ (状态管理)   │    │ (动态装饰)   │     │
│  └─────────────┘    └─────────────┘    └─────────────┘     │
│         │                  │                               │
│         ↓                  ↓                               │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │ 模板方法模式 │    │ 适配器模式   │    │ 业务流程     │     │
│  │ (流程骨架)   │    │ (接口适配)   │    │             │     │
│  └─────────────┘    └─────────────┘    └─────────────┘     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 业务流程

### 购物流程

```
浏览商品 → 加入购物车 → 管理购物车 → 结算下单 → 支付确认 → 订单跟踪
```

### 结算流程（分步骤引导）

```
步骤1: 选择收货地址 (25%)
    ↓
步骤2: 选择优惠券 (50%)
    ↓
步骤3: 选择支付方式 (75%)
    ↓
步骤4: 确认订单 (100%)
```

### 优惠计算流程（职责链）

```
原始金额
    ↓
┌─────────────────┐
│ CouponHandler   │ → 优惠券抵扣: -¥XX
└────────┬────────┘
         ↓
┌─────────────────┐
│ FullReduction   │ → 满减抵扣: -¥XX
│ Handler         │
└────────┬────────┘
         ↓
┌─────────────────┐
│ VipHandler      │ → VIP折扣: -¥XX
└────────┬────────┘
         ↓
    最终支付金额
```

## 数据持久化

系统使用对象序列化实现数据持久化：

| 数据类型 | 文件名            |
| ---- | -------------- |
| 购物车  | cart.dat       |
| 订单   | orders.dat     |
| 会员   | members.dat    |
| 地址   | addresses.dat  |
| 促销规则 | promotions.dat |
| 优惠券  | coupons.dat    |
| 商品   | products.dat   |
| 物流   | logistics.dat  |
| 评价   | reviews.dat    |
| 日志   | app.log        |

## 扩展指南

### 新增促销类型

1. 创建新的策略类实现 `PromotionStrategy` 接口
2. 在 `ECommerceFactory` 中添加创建逻辑
3. 创建新的 `Handler` 类（可选）

```java
// 1. 新增策略类
public class NewPromotionStrategy implements PromotionStrategy {
    @Override
    public double calculateDiscount(Order order) {
        // 实现计算逻辑
    }
}

// 2. 工厂中添加创建逻辑
case "NEW_PROMOTION":
    return new NewPromotionStrategy();
```

### 新增支付方式

1. 创建新的适配器类继承 `AbstractPaymentAdapter`
2. 在 `PaymentService` 中注册

```java
public class NewPaymentAdapter extends AbstractPaymentAdapter {
    @Override
    public boolean pay(Order order) {
        // 实现支付逻辑
    }
    
    @Override
    public String getPaymentMethod() {
        return "新支付方式";
    }
}
```

## 相关文档

- [UX设计文档](./电商购物车结算模块UX设计文档.md) - 用户界面设计说明
- [PRD产品需求文档](./电商购物车结算模块PRD产品需求文档.md) - 产品需求详细说明
- [设计模式应用报告](./设计模式应用报告.md) - 设计模式应用分析

## 项目亮点

1. **设计模式深度应用** - 每种模式都真正融入业务流程，而非仅仅是示例
2. **模式协作展示** - 多种设计模式协同工作，展示模式的组合使用
3. **可视化职责链** - 结算页面逐步展示优惠计算过程，直观展示职责链模式
4. **操作撤销** - 命令模式支持撤销、批处理和事务
5. **事件驱动** - EventBus实现发布-订阅模式，解耦组件通信
6. **状态机管理** - 集中管理订单状态转换，状态行为封装清晰
7. **良好的代码结构** - 分层架构，模块化设计，易于理解和扩展

## 许可证

本项目为本科设计模式课程项目，仅供学习交流使用。

***

**作者**：设计模式课程项目组\
**更新日期**：2026年4月

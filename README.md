# 电商购物车结算模块

## 项目简介

这是一个基于Java Swing的电商购物车结算模块，应用了多种设计模式，实现了购物车管理、促销规则管理、结算功能、地址管理、订单历史记录等完整的电商购物车系统。

## 功能特点

- **购物车管理**：添加、删除、修改商品数量、商品分类展示
- **购物车持久化**：自动保存购物车内容，下次启动时恢复
- **地址管理**：添加、编辑、删除、管理收货地址
- **订单历史记录**：查看历史订单和订单详情
- **促销规则管理**：新增、修改、删除促销规则
- **结算管理**：优惠券 → 满减 → VIP折扣 依次抵扣
- **可视化职责链**：结算页面逐步展示优惠计算过程
- **操作撤销**：支持添加、删除、修改数量的撤销操作
- **日志查看器**：查看系统操作日志，验证单例模式
- **设计模式应用**：清晰展示多种设计模式的实际应用

## 技术栈

- **语言**：Java 8+
- **UI框架**：Java Swing
- **构建工具**：手动编译（可扩展为Maven）
- **日志**：自定义Logger（单例模式）
- **数据持久化**：文件序列化

## 项目结构

```
com.ecommerce.cart
├── model/              # 实体类
│   ├── Product.java    # 商品实体
│   ├── Cart.java       # 购物车实体
│   ├── CartItem.java   # 购物车项
│   ├── Order.java      # 订单实体
│   ├── Coupon.java     # 优惠券实体
│   ├── PromotionRule.java  # 促销规则实体
│   ├── Address.java    # 地址实体
│   ├── Member.java     # 会员实体
│   ├── Logistics.java  # 物流实体
│   ├── Review.java     # 评价实体
│   └── ProductInterface.java # 商品接口（装饰器模式）
├── service/            # 业务逻辑
│   ├── CartService.java      # 购物车服务
│   ├── OrderService.java     # 订单服务
│   ├── PromotionService.java # 促销服务
│   ├── AddressService.java   # 地址服务
│   └── PaymentService.java   # 支付服务
├── ui/                 # 界面
│   ├── CartFrame.java        # 购物车页面
│   ├── CheckoutFrame.java    # 结算页面
│   ├── PromotionManageFrame.java # 促销管理页面
│   ├── OrderDetailFrame.java # 订单详情页面
│   ├── LogViewerFrame.java   # 日志查看器
│   ├── AddressFrame.java     # 地址管理页面
│   └── OrderHistoryFrame.java # 订单历史页面
├── pattern/            # 设计模式实现
│   ├── factory/        # 工厂模式
│   │   ├── ProductFactory.java    # 商品工厂
│   │   └── StrategyFactory.java   # 策略工厂
│   ├── strategy/       # 策略模式
│   │   ├── PromotionStrategy.java # 促销策略接口
│   │   ├── CouponStrategy.java    # 优惠券策略
│   │   ├── FullReductionStrategy.java # 满减策略
│   │   ├── VipDiscountStrategy.java   # VIP折扣策略
│   │   ├── TimeLimitDiscountStrategy.java # 限时折扣策略
│   │   └── GiftStrategy.java      # 满赠策略
│   ├── chain/          # 职责链模式
│   │   ├── Handler.java         # 处理器抽象类
│   │   ├── CouponHandler.java   # 优惠券处理器
│   │   ├── FullReductionHandler.java # 满减处理器
│   │   └── VipHandler.java      # VIP处理器
│   ├── singleton/      # 单例模式
│   │   ├── DBConnection.java    # 数据库连接池
│   │   └── Logger.java           # 日志记录器
│   ├── observer/       # 观察者模式
│   │   ├── Observer.java         # 观察者接口
│   │   └── Subject.java          # 被观察者接口
│   ├── decorator/      # 装饰器模式
│   │   ├── ProductDecorator.java # 商品装饰器抽象类
│   │   ├── DiscountDecorator.java # 折扣装饰器
│   │   └── PromotionDecorator.java # 促销装饰器
│   └── command/        # 命令模式
│       ├── Command.java          # 命令接口
│       ├── CommandManager.java   # 命令管理器
│       ├── AddProductCommand.java # 添加商品命令
│       ├── RemoveProductCommand.java # 删除商品命令
│       └── UpdateQuantityCommand.java # 修改数量命令
├── Main.java           # 主类
└── README.md           # 项目说明
```

## 设计模式应用

### 1. 工厂模式
- **ProductFactory**：根据商品类型创建不同商品实例
- **StrategyFactory**：根据促销类型创建不同策略实例

### 2. 策略模式
- **PromotionStrategy**：定义促销策略接口
- **CouponStrategy**：实现优惠券折扣计算
- **FullReductionStrategy**：实现满减优惠计算
- **VipDiscountStrategy**：实现VIP折扣计算
- **TimeLimitDiscountStrategy**：实现限时折扣计算
- **GiftStrategy**：实现满赠优惠计算

### 3. 职责链模式
- **Handler**：定义处理器抽象类
- **CouponHandler**：处理优惠券抵扣
- **FullReductionHandler**：处理满减抵扣
- **VipHandler**：处理VIP折扣
- **流转顺序**：优惠券 → 满减 → VIP折扣

### 4. 单例模式
- **DBConnection**：懒汉式实现，确保数据库连接池唯一
- **Logger**：饿汉式实现，确保日志记录器唯一

### 5. 观察者模式
- **Observer**：定义观察者接口
- **Subject**：定义被观察者接口
- **应用场景**：购物车变化时自动更新UI

### 6. 装饰器模式
- **ProductDecorator**：商品装饰器抽象类
- **DiscountDecorator**：实现商品折扣装饰
- **PromotionDecorator**：实现商品促销装饰
- **应用场景**：动态调整商品价格

### 7. 命令模式
- **Command**：定义命令接口
- **CommandManager**：管理命令执行和撤销
- **AddProductCommand**：添加商品命令
- **RemoveProductCommand**：删除商品命令
- **UpdateQuantityCommand**：修改数量命令
- **应用场景**：支持操作撤销功能

## 运行说明

### 编译项目
1. 进入项目目录
2. 执行以下命令编译所有Java文件：
   ```bash
   javac -d bin src\main\java\com\ecommerce\cart\**\*.java
   ```

### 运行项目
1. 执行以下命令运行主类：
   ```bash
   java -cp bin com.ecommerce.cart.Main
   ```

### 演示流程
1. **购物车页面**：
   - 添加商品（测试数据已默认添加）
   - 删除商品
   - 修改商品数量
   - 查看商品分类
   - 点击"地址管理"按钮管理收货地址
   - 点击"订单历史"按钮查看历史订单
   - 点击"查看日志"按钮查看操作日志
   - 点击"撤销"按钮撤销上一步操作
   - 点击"结算"按钮进入结算页面

2. **结算页面**：
   - 选择优惠券
   - 点击"计算优惠"按钮，观察优惠金额的逐步计算过程
   - 点击"提交订单"按钮完成订单

3. **订单详情页面**：
   - 查看订单信息和商品列表
   - 点击"返回购物车"按钮返回

4. **促销管理页面**：
   - 新增、修改、删除促销规则
   - 点击"返回购物车"按钮返回

5. **地址管理页面**：
   - 添加新地址
   - 编辑现有地址
   - 删除地址
   - 设置默认地址

6. **订单历史页面**：
   - 查看历史订单列表
   - 点击"查看详情"按钮查看订单详情

7. **日志查看器**：
   - 查看系统操作日志
   - 刷新日志内容

## 注意事项

- 本项目为演示设计模式的教学项目，数据库连接为模拟实现
- 结算页面的优惠计算过程有0.5秒的间隔，用于演示职责链的流转
- 默认添加了一些测试数据，便于直接查看效果
- 购物车内容会自动保存到文件，下次启动时恢复
- 地址和订单信息也会持久化保存

## 项目亮点

1. **丰富的设计模式应用**：七种设计模式的实际应用示例
2. **完整的电商购物车系统**：从购物车到订单的完整流程
3. **数据持久化**：自动保存购物车、地址和订单信息
4. **操作撤销功能**：支持添加、删除、修改数量的撤销操作
5. **直观的职责链演示**：结算页面的逐步计算过程
6. **美观的用户界面**：优化的按钮、表格和对话框样式
7. **良好的代码结构**：模块化设计，易于理解和扩展
8. **完整的功能集**：购物车管理、地址管理、订单历史等

## 作者

本项目为本科设计模式课程项目，旨在展示设计模式在实际项目中的应用，以及如何构建一个完整的电商购物车系统。
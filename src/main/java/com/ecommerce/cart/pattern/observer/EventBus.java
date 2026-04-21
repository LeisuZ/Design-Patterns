package com.ecommerce.cart.pattern.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 观察者模式-事件总线
 * 用于更加灵活地管理观察者，将事件的发布和订阅解耦
 */
public class EventBus {
    private static final EventBus instance = new EventBus(); // 单例模式
    private Map<String, List<Observer>> eventObservers; // 事件类型到观察者的映射
    
    /**
     * 私有构造方法，防止外部实例化
     */
    private EventBus() {
        eventObservers = new HashMap<>();
    }
    
    /**
     * 获取事件总线实例
     * @return 事件总线实例
     */
    public static EventBus getInstance() {
        return instance;
    }
    
    /**
     * 注册观察者到指定事件
     * @param eventType 事件类型
     * @param observer 观察者
     */
    public void registerObserver(String eventType, Observer observer) {
        if (!eventObservers.containsKey(eventType)) {
            eventObservers.put(eventType, new ArrayList<>());
        }
        eventObservers.get(eventType).add(observer);
    }
    
    /**
     * 从指定事件中移除观察者
     * @param eventType 事件类型
     * @param observer 观察者
     */
    public void removeObserver(String eventType, Observer observer) {
        if (eventObservers.containsKey(eventType)) {
            eventObservers.get(eventType).remove(observer);
        }
    }
    
    /**
     * 发布事件，通知所有观察者
     * @param eventType 事件类型
     */
    public void publishEvent(String eventType, Object data) {
        if (eventObservers.containsKey(eventType)) {
            for (Observer observer : eventObservers.get(eventType)) {
                observer.update(eventType, data);
            }
        }
    }
}

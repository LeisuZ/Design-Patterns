package com.ecommerce.cart.pattern.singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public enum DBConnection {
    INSTANCE;

    private Map<String, Map<String, Object>> tables;

    private DBConnection() {
        tables = new HashMap<>();
        tables.put("products", new HashMap<>());
        tables.put("orders", new HashMap<>());
        tables.put("members", new HashMap<>());
        Logger.INSTANCE.log("[单例模式] DBConnection 数据库连接初始化完成");
    }

    public void executeUpdate(String table, String key, Object data) {
        Map<String, Object> tableData = tables.get(table);
        if (tableData != null) {
            tableData.put(key, data);
            Logger.INSTANCE.log("[单例模式] DBConnection 执行更新: " + table + "." + key);
        }
    }

    public Object executeQuery(String table, String key) {
        Map<String, Object> tableData = tables.get(table);
        if (tableData != null) {
            Logger.INSTANCE.log("[单例模式] DBConnection 执行查询: " + table + "." + key);
            return tableData.get(key);
        }
        return null;
    }

    public List<Object> executeQueryAll(String table) {
        Map<String, Object> tableData = tables.get(table);
        List<Object> results = new ArrayList<>();
        if (tableData != null) {
            results.addAll(tableData.values());
            Logger.INSTANCE.log("[单例模式] DBConnection 执行全表查询: " + table + " (共" + results.size() + "条记录)");
        }
        return results;
    }

    public boolean executeDelete(String table, String key) {
        Map<String, Object> tableData = tables.get(table);
        if (tableData != null && tableData.remove(key) != null) {
            Logger.INSTANCE.log("[单例模式] DBConnection 执行删除: " + table + "." + key);
            return true;
        }
        return false;
    }
}

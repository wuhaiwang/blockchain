package com.seasun.management.config;

import java.util.ArrayList;
import java.util.List;

public class DataSourceContextHolder {

    private static final ThreadLocal<String> local = new ThreadLocal<String>();
    private static final List<String> dataSourceIds = new ArrayList<>();

    public static ThreadLocal<String> getLocal() {
        return local;
    }

    public static void setType(String type) {
        local.set(type);
    }

    public static void usePrimary() {
        local.set(DataSourceType.primary.getType());
    }

    public static void useSecond() {
        local.set(DataSourceType.second.getType());
    }

    public static void useThird() {
        local.set(DataSourceType.third.getType());
    }

    public static void useFourth() {
        local.set(DataSourceType.fourth.getType());
    }

    public static String getJdbcType() {
        return local.get();
    }

    public static void clearDBType() {
        local.remove();
    }

    public static boolean containsDataSource(String dataSourceId) {
        return dataSourceIds.contains(dataSourceId);
    }

    public static void addDataSourceIds(String dataSourceId) {
        dataSourceIds.add(dataSourceId);
    }
}

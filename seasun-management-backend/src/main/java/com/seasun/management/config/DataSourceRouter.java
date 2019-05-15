package com.seasun.management.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


public class DataSourceRouter extends AbstractRoutingDataSource {

    protected Object determineCurrentLookupKey() {
//        System.out.println("current type is:"+DataSourceContextHolder.getJdbcType());
        return DataSourceContextHolder.getJdbcType();
    }
}

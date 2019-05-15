package com.seasun.management.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    private Class<? extends DataSource> datasourceType = org.apache.tomcat.jdbc.pool.DataSource.class;

    @Bean(name = "springDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource writeDataSource() {
        return DataSourceBuilder.create().type(datasourceType).build();
    }

    @Bean(name = "secondDataSource")
    @ConfigurationProperties(prefix = "second.datasource")
    public DataSource readDataSource1() {
        return DataSourceBuilder.create().type(datasourceType).build();
    }

    @Bean(name = "thirdDataSource")
    @ConfigurationProperties(prefix = "third.datasource")
    public DataSource thirdDataSource() {
        return DataSourceBuilder.create().type(datasourceType).build();
    }

    @Bean(name = "fourthDataSource")
    @ConfigurationProperties(prefix = "fourth.datasource")
    public DataSource fourthDataSource() {
        return DataSourceBuilder.create().type(datasourceType).build();
    }

    @Primary
    @Bean(name = "dynamicDataSource")
    @DependsOn({"springDataSource", "secondDataSource","thirdDataSource","fourthDataSource"})
    public AbstractRoutingDataSource dynamicDataSource(@Qualifier("springDataSource") DataSource springDatasource,
                                                       @Qualifier("secondDataSource") DataSource secondDatasource,
                                                       @Qualifier("thirdDataSource") DataSource thirdDataSource,
                                                       @Qualifier("fourthDataSource") DataSource fourthDataSource) {
        DataSourceRouter proxy = new DataSourceRouter();
        DataSourceContextHolder.addDataSourceIds("springDataSource");
        DataSourceContextHolder.addDataSourceIds("secondDataSource");
        DataSourceContextHolder.addDataSourceIds("thirdDataSource");
        DataSourceContextHolder.addDataSourceIds("fourthDataSource");
        Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
        targetDataSources.put("springDataSource", springDatasource);
        targetDataSources.put("secondDataSource", secondDatasource);
        targetDataSources.put("thirdDataSource", thirdDataSource);
        targetDataSources.put("fourthDataSource", fourthDataSource);
        proxy.setDefaultTargetDataSource(springDatasource);
        proxy.setTargetDataSources(targetDataSources);
        return proxy;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean(AbstractRoutingDataSource dynamicDataSource) throws Throwable {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactoryBean.setDataSource(dynamicDataSource);
        sessionFactoryBean.setMapperLocations(resolver
                .getResources("classpath:/mapper/**/*.xml"));
        SqlSessionFactory sqlSessionFactory = sessionFactoryBean.getObject();
        sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);
        sqlSessionFactory.getConfiguration().getTypeAliasRegistry().registerAliases("com.seasun.management.model");
        return sessionFactoryBean.getObject();
    }
}

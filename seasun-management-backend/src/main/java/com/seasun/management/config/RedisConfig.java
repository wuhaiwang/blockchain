package com.seasun.management.config;

import com.seasun.management.constant.RedisConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableAutoConfiguration
public class RedisConfig {

    private static Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Autowired
    Environment env;

    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisPoolConfig getRedisConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        return config;
    }

    @Bean
    public JedisConnectionFactory getConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        if ("true".equals(env.getProperty("redis.enable"))) {
            Set<String> nodeSet = new HashSet<>();
            // 启用了sentinel
            if ("true".equals(env.getProperty("sentinel.enable"))) {
                String nodes = env.getProperty("spring.redis.sentinel.nodes");
                for (String s : nodes.split(",")) {
                    nodeSet.add(s);
                }
                String master = env.getProperty("spring.redis.sentinel.master");
                RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration(master, nodeSet);
                factory = new JedisConnectionFactory(sentinelConfig);
            }
            // 未启用sentinel
            else {
                factory.setHostName(env.getProperty("spring.redis.hostName"));
                factory.setPort(Integer.valueOf(env.getProperty("spring.redis.port")));
            }
        }

        JedisPoolConfig config = getRedisConfig();
        factory.setPoolConfig(config);
        logger.info("JedisConnectionFactory bean init success.");
        return factory;
    }


    @Bean
    public RedisTemplate<?, ?> getRedisTemplate() {
        RedisTemplate<?, ?> template = new StringRedisTemplate(getConnectionFactory());
        return template;
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        JedisConnectionFactory jedisConnectionFactory = getConnectionFactory();
        if ("true".equals(env.getProperty("redis.enable"))) {
            jedisConnectionFactory.getConnection().setClientName(RedisConstant.redis_client_name.getBytes());
        }
        container.setConnectionFactory(jedisConnectionFactory);

        return container;
    }


}

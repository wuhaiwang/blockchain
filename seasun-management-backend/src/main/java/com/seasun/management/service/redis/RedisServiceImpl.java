package com.seasun.management.service.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.RedisConstant;
import com.seasun.management.util.MyHttpClientUtils;
import com.seasun.management.vo.UserMiniVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class RedisServiceImpl implements RedisService {

    private static Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);

    @Autowired
    private RedisTemplate<String, ?> redisTemplate;

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;


    @Override
    public boolean set(final String key, final String value) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.set(serializer.serialize(key), serializer.serialize(value));
                return true;
            }
        });
        return result;
    }

    public String get(final String key) {
        String result = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] value = connection.get(serializer.serialize(key));
                return serializer.deserialize(value);
            }
        });
        return result;
    }

    @Override
    public boolean expire(final String key, long expire) {
        return redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    @Override
    public boolean setObject(String key, Object object) {
        String value = JSONObject.toJSONString(object);
        return set(key, value);
    }

    @Override
    public <T> T getObject(String key, T object) {
        String json = get(key);
        if (json != null) {
            T ret = (T) JSON.parseObject(json);
            return ret;
        }
        return null;
    }

    @Override
    public long lPush(final String key, Object obj) {
        final String value = JSONObject.toJSONString(obj);
        long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                long count = connection.lPush(serializer.serialize(key), serializer.serialize(value));
                return count;
            }
        });
        return result;
    }

    @Override
    public long rPush(final String key, Object obj) {
        final String value = JSONObject.toJSONString(obj);
        long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                long count = connection.rPush(serializer.serialize(key), serializer.serialize(value));
                return count;
            }
        });
        return result;
    }


    @Override
    public String lPop(final String key) {
        String result = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] res = connection.lPop(serializer.serialize(key));
                return serializer.deserialize(res);
            }
        });
        return result;
    }

    @Override
    public void publish(String channel, String content) {
        redisTemplate.convertAndSend(channel, content);
    }

    @Override
    public List<String> getPushServers() {
        List<RedisClientInfo> clientInfos = redisTemplate.getClientList();
        List<String> pushServers = new ArrayList<>();
        for (RedisClientInfo clientInfo : clientInfos) {
            String host = clientInfo.getAddressPort().split(":")[0];
            if (RedisConstant.push_server_client_name.equals(clientInfo.getName())
                    && !pushServers.contains(host)) {
                pushServers.add(host);
            }
        }
        return pushServers;
    }

    @Override
    public List<UserMiniVo> getOnlineUserFromPushServer(String host) {
        String url = "http://" + host + ":" + RedisConstant.push_server_port + RedisConstant.push_session_url;
        Map<String, String> header = new HashMap<>();
        header.put(RedisConstant.push_server_header, RedisConstant.push_server_token);
        String resultStr = MyHttpClientUtils.doGet(url, header);
        return JSON.parseArray(resultStr, UserMiniVo.class);
    }

    /*
    * key hashKey
    * requestId hashValue
    * expireTime 过期时间 单位 ms
    * */
    private boolean getlock(String key, String requestId, int expireTime) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = getJedis();
            result = jedis.set(key, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        } catch (Exception e) {
            logger.error("redis挂了...");
            logger.error(e.getMessage());

            // 让线程睡一会，防止后续同一时刻操作mysql
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 4; j++) {
                sb.append(random.nextInt(10));
            }
            int sleepTime = 100 * Integer.parseInt(sb.toString());
            logger.info("让这个线程睡眠" + sleepTime / 1000 + "秒");
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e1) {
                logger.error("获取redisLock时，redis连接失败后让线程sleep的地方出问题了....");
                logger.error(e1.getMessage());
            }
            return true;
        }

        if (jedis != null) {
            jedis.close();
        }

        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }

        return false;
    }

    /*
    * key hashKey
    * requestId hashValue
    * expireTime 过期时间 单位 ms
    * 当 key，requestId与redis中的key-value相同时，才会设置上过期时间
    * */

    private boolean unlock(String key, String requestId, int expireTime) {
        Jedis jedis = null;
        Object eval = null;
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        try {
            jedis = getJedis();
            eval = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(requestId));
        } catch (Exception e) {
            logger.error("redis挂了...");
            logger.error(e.getMessage());
        }

        if (jedis != null) {
            jedis.close();
        }

        if (RELEASE_SUCCESS.equals(eval)) {
            return true;
        }
        return false;
    }

    private Jedis getJedis() {
        return null;
      //  return jedisPool.getResource();
    }

}

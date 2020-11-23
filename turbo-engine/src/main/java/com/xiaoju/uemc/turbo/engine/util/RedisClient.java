package com.xiaoju.uemc.turbo.engine.util;

import com.didiglobal.reportlogger.LoggerFactory;
import com.didiglobal.reportlogger.ReportLogger;
import com.xiaoju.uemc.turbo.engine.config.RedisProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：turbo
 * 类 名 称：RedisClient
 * 类 描 述：
 * 创建时间：2020/11/12 8:42 PM
 * 创 建 人：didiwangxing
 */
@Component
public class RedisClient {

    protected static final ReportLogger LOGGER = LoggerFactory.getLogger(RedisClient.class);

    @Resource
    private RedisProperties redisProperties;

    private static final String DEFAULT_REDIS_SEPARATOR = ";";
    private static final String HOST_PORT_SEPARATOR = ":";
    public static final int REDIS_DEFAULT_TIMEOUT = 2000;
    public static final int JEDIS_POOL_DEFAULT_MAX_TOTAL = 8;
    public static final int JEDIS_POOL_DEFAULT_MAX_IDLE = 8;
    public static final int JEDIS_POOL_DEFAULT_MIN_IDLE = 3;
    public static final long JEDIS_POOL_DEFAULT_MAX_WAIT = 60000;
    public static final boolean JEDIS_POOL_DEFAULT_TEST_ON_BORROW = true;

    private ShardedJedisPool shardedJedisPool = null;

    @PostConstruct
    public void initialClient() {
        initialShardedPool();
    }

    private void initialShardedPool() {

        int timeout = redisProperties.getTimeout() == null ? REDIS_DEFAULT_TIMEOUT : redisProperties.getTimeout();
        int maxTotal = redisProperties.getJedisPoolMaxTotal() == null ? JEDIS_POOL_DEFAULT_MAX_TOTAL : redisProperties.getJedisPoolMaxTotal();
        int maxIdle = redisProperties.getJedisPoolMaxIdle() == null ? JEDIS_POOL_DEFAULT_MAX_IDLE : redisProperties.getJedisPoolMaxIdle();
        int minIdle = redisProperties.getJedisPoolMinIdle() == null ? JEDIS_POOL_DEFAULT_MIN_IDLE : redisProperties.getJedisPoolMinIdle();
        long maxWaitMillis = redisProperties.getJedisPoolMaxWaitMillis() == null ? JEDIS_POOL_DEFAULT_MAX_WAIT : redisProperties.getJedisPoolMaxWaitMillis();
        boolean testOnBorrow = redisProperties.getJedisPoolTestOnBorrow() == null ? JEDIS_POOL_DEFAULT_TEST_ON_BORROW : redisProperties.getJedisPoolTestOnBorrow();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setTestOnBorrow(testOnBorrow);

        List<JedisShardInfo> shardedPoolList = new ArrayList<JedisShardInfo>();
        for (String redisUrl : redisProperties.getUrl().split(DEFAULT_REDIS_SEPARATOR)) {
            String[] redisUrlInfo = redisUrl.split(HOST_PORT_SEPARATOR);
            JedisShardInfo Jedisinfo = new JedisShardInfo(redisUrlInfo[0], Integer.parseInt(redisUrlInfo[1]), timeout);
            shardedPoolList.add(Jedisinfo);
        }

        this.shardedJedisPool = new ShardedJedisPool(poolConfig, shardedPoolList, Hashing.MURMUR_HASH);
    }

    public interface JedisAction<T> {
        T action(ShardedJedis jedis);
    }

    private  <T> T execute(JedisAction<T> jedisAction) {
        ShardedJedis jedis = shardedJedisPool.getResource();
        T result = null;
        try {
            result = jedisAction.action(jedis);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }


    public String get(final String key) {
        return execute(new JedisAction<String>() {
            @Override
            public String action(ShardedJedis jedis) {
                return jedis.get(key);
            }
        });
    }

    public String setex(final String key, final int seconds, final String value) {
        return execute(new JedisAction<String>() {
            @Override
            public String action(ShardedJedis jedis) {
                return jedis.setex(key, seconds, value);
            }
        });
    }

    public Long del(final String key) {
        return execute(new JedisAction<Long>() {
            @Override
            public Long action(ShardedJedis jedis) {
                return jedis.del(key);
            }
        });
    }
}

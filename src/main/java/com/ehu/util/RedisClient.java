package com.ehu.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class RedisClient {
    private static JedisPool pool;

    private static final String PROPERTIES_FILE = "redis.properties";

    private static final String REDIS_HOSTNAME = "app.datasource.redis.hostName";

    private static final String REDIS_PORT = "app.datasource.redis.port";

    private static final String TIME_OUT = "app.datasource.redis.timeout";

    private static final String REDIS_PASSWORD = "app.datasource.redis.password";

    public static JedisPool getPool() {
        if (pool == null) {
            Properties properties = new Properties();
            try {
                properties.load(RedisClient.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
            } catch (IOException e) {
                e.printStackTrace();
            }
            JedisPoolConfig config = new JedisPoolConfig();
            // 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
            // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
            // config.setMaxActive(MAXACTIVE);
            // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
            config.setMaxIdle(100);
            // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
            config.setMaxWaitMillis(10000);
            // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);
            config.setMaxTotal(1000);
            config.setMinIdle(50);
            pool = new JedisPool(config, properties.getProperty(REDIS_HOSTNAME), Integer.valueOf(properties.getProperty(REDIS_PORT)),
                    Integer.valueOf(properties.getProperty(TIME_OUT)), properties.getProperty(REDIS_PASSWORD));
        }
        return pool;
    }

    public static boolean set(String key, String value) {

        JedisPool pool = null;
        Jedis jedis = null;
        boolean result = false;

        try {
            pool = getPool();
            jedis = pool.getResource();
            result = "OK".equals(jedis.set(key, value));
        } catch (Exception e) {
            log.error("RedisClient set error", e);
        } finally {
            // 返还到连接池
            returnResource(pool, jedis);
        }

        return result;
    }

    /**
     * 存储对象
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static boolean set(String key, Object value) {
        String jsonString = JSON.toJSONString(value);
        return set(key, jsonString);
    }

    public static boolean setex(String key, Object value, int seconds) {

        JedisPool pool = null;
        Jedis jedis = null;
        boolean result = false;

        try {
            pool = getPool();
            jedis = pool.getResource();
            result = "OK".equals(jedis.setex(key, seconds, value.toString()));

        } catch (Exception e) {
            // 释放redis对象
            if (null != pool && null != jedis) {
                jedis.close();
            }
        } finally {
            // 返还到连接池
            returnResource(pool, jedis);
        }

        return result;
    }

    /**
     * 获取锁
     *
     * @param key：锁的唯一标识
     * @param expire：锁自动释放时间 单位：秒
     * @param timeout：超时等待时间 单位：毫秒
     * @return true：获得锁；false:锁已被占用 单位：毫秒
     */
    public static Boolean getLock(String key, Integer expire, int sleepTime, Integer timeout) {
        if (expire < 0) {
            return false;
        }
        Long beginTime = System.currentTimeMillis();
        JedisPool pool = getPool();
        Jedis jedis = pool.getResource();
        try {
            do {
                //防止程序出错设置键值不失效
                if (jedis.ttl(key) == -1) {
                    jedis.expire(key, expire);
                }

                Long res = jedis.incr(key);

                if (res == 1) {
                    jedis.expire(key, expire);

//                    try {
//                        jedis.close();
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }

                    return true;
                }

//                if (jedis != null) {
//                    try {
//                        jedis.close();
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//                }

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while ((System.currentTimeMillis() - beginTime) < timeout);

        } finally {
            returnResource(pool, jedis);
        }

        return false;
    }

    public static Long ttl(String key) {

        JedisPool pool = null;
        Jedis jedis = null;
        Long result = 0L;

        try {
            pool = getPool();
            jedis = pool.getResource();
            result = jedis.ttl(key);

        } catch (Exception e) {
            // 释放redis对象
            if (null != pool && null != jedis) {
                jedis.close();
            }
        } finally {
            // 返还到连接池
            returnResource(pool, jedis);
        }

        return result;
    }

    public static String get(String key) {

        JedisPool pool = null;
        Jedis jedis = null;
        String result = null;

        try {
            pool = getPool();
            jedis = pool.getResource();
            result = jedis.get(key);
        } catch (Exception e) {
            // 释放redis对象
            if (null != pool && null != jedis) {
                jedis.close();
            }
        } finally {
            // 返还到连接池
            returnResource(pool, jedis);
        }

        return result;
    }

    public static boolean remove(String key) {
        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = getPool();
            jedis = pool.getResource();
            return jedis.del(key) > 0;

        } catch (Exception e) {
            // 释放redis对象
            if (null != pool && null != jedis) {
                jedis.close();
            }

            return false;
        } finally {
            // 返还到连接池
            returnResource(pool, jedis);
        }
    }

    public static void returnResource(JedisPool pool, Jedis redis) {
        if (redis != null) {
            redis.close();
        }
    }

    public static String clear() {
        JedisPool pool = null;
        Jedis jedis = null;
        String result = null;

        try {
            pool = getPool();
            jedis = pool.getResource();
            result = jedis.flushAll();
        } catch (Exception e) {
            // 释放redis对象
            if (null != pool && null != jedis) {
                jedis.close();
            }
        } finally {
            // 返还到连接池
            returnResource(pool, jedis);
        }

        return result;
    }

    public static Long incr(String key) {
        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = getPool();
            jedis = pool.getResource();
            return jedis.incr(key);

        } catch (Exception e) {
            // 释放redis对象
            if (null != pool && null != jedis) {
                jedis.close();
            }

            return null;
        } finally {
            // 返还到连接池
            returnResource(pool, jedis);
        }
    }

    public static Long incr(String key, Long aLong) {
        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = getPool();
            jedis = pool.getResource();
            if (aLong != null) {
                return jedis.incrBy(key, aLong);
            } else {
                return jedis.incr(key);
            }

        } catch (Exception e) {
            // 释放redis对象
            if (null != pool && null != jedis) {
                jedis.close();
            }

            return 0L;
        } finally {
            // 返还到连接池
            returnResource(pool, jedis);
        }
    }

    public static Long decr(String key, Long integer) {
        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = getPool();
            jedis = pool.getResource();
            if (integer != null) {
                return jedis.decrBy(key, integer);
            } else {
                return jedis.decr(key);
            }

        } catch (Exception e) {
            // 释放redis对象
            if (null != pool && null != jedis) {
                jedis.close();
            }

            return 0L;
        } finally {
            // 返还到连接池
            returnResource(pool, jedis);
        }
    }


    /**
     * 推送redis队列消息
     *
     * @param key
     * @param value
     * @return
     */
    public static Long lpush(String key, String value) {
        JedisPool pool = null;
        Jedis jedis = null;
        Long push = null;

        try {
            pool = getPool();
            jedis = pool.getResource();
            push = jedis.lpush(key, value);

        } catch (Exception e) {
            if (null != pool && null != jedis) {
                jedis.close();
            }
        } finally {
            returnResource(pool, jedis);
        }
        return push;
    }

    /**
     * 获取redis队列消息
     *
     * @param key
     * @return
     */
    public static String lpop(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        String val = null;

        try {
            pool = RedisClient.getPool();
            jedis = pool.getResource();

            val = jedis.lpop(key);
        } catch (Exception e) {
            if (null != pool && null != jedis) {
                jedis.close();
            }
        } finally {
            RedisClient.returnResource(pool, jedis);
        }
        return val;
    }

    /**
     * @param key
     * @param offset
     * @return
     */
    public static boolean getBit(String key, long offset) {
        JedisPool pool = null;
        Jedis jedis = null;
        Boolean flag = false;

        try {
            pool = RedisClient.getPool();
            jedis = pool.getResource();

            flag = jedis.getbit(key, offset);
        } catch (Exception e) {
            if (null != pool && null != jedis) {
                jedis.close();
            }
        } finally {
            RedisClient.returnResource(pool, jedis);
        }
        return flag;
    }

    /**
     * @param key
     * @param offset
     * @return
     */
    public static boolean setBit(String key, long offset) {
        JedisPool pool = null;
        Jedis jedis = null;
        Boolean flag = false;

        try {
            pool = RedisClient.getPool();
            jedis = pool.getResource();

            flag = jedis.setbit(key, offset, true);
        } catch (Exception e) {
            if (null != pool && null != jedis) {
                jedis.close();
            }
        } finally {
            RedisClient.returnResource(pool, jedis);
        }
        return flag;
    }

    /**
     * @param key
     * @param offset
     * @return
     */
    public static boolean setBit(String key, long offset, String value) {
        JedisPool pool = null;
        Jedis jedis = null;
        Boolean flag = false;

        try {
            pool = RedisClient.getPool();
            jedis = pool.getResource();

            flag = jedis.setbit(key, offset, value);
        } catch (Exception e) {
            if (null != pool && null != jedis) {
                jedis.close();
            }
        } finally {
            RedisClient.returnResource(pool, jedis);
        }
        return flag;
    }
}

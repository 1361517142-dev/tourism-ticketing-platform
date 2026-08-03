package com.qinghuan.redis;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.qinghuan.common.RedisData;
import com.qinghuan.common.constant.RedisConstants;
import org.flywaydb.core.internal.util.JsonUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class CacheClient {
    private final StringRedisTemplate stringRedisTemplate;
    // 线程池
    ThreadPoolExecutor pool = new ThreadPoolExecutor(
            2,
            4,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "    return redis.call('del', KEYS[1]) " +
                    "else " +
                    "    return 0 " +
                    "end";

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /*
     * 设置键值对
     */
    public void set(String key, Object value, Long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), timeout, timeUnit);
    }

    /*
     * 设计键值对+逻辑过期
     */

    public <T> void setWtihLogicalExpire(String key, T value, Class<T> type, Long timeout, TimeUnit timeUnit) {
        RedisData<T> redisData = new RedisData<>();
        redisData.setData(value);
        redisData.setExpireAt(LocalDateTime.now().plusSeconds(timeUnit.toSeconds(timeout)));
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /*
     * 查询（防缓存穿透）
     */
    public <R, ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback,
                                          Long timeout, TimeUnit timeUnit) {
        String key = keyPrefix + id;
        // 1.查询缓存
        String valueJson = stringRedisTemplate.opsForValue().get(key);

        // 2.命中，且不为空串，直接返回
        if (StrUtil.isNotBlank(valueJson)) {
            return JSONUtil.toBean(valueJson, type);
        }

        // 3.为空串，说明数据库不存在该key，返回null
        if (valueJson != null) {
            return null;
        }

        // 4.未命中，查询数据库
        R r = dbFallback.apply(id);

        // 5.数据库不存在，新增空串缓存，返回null
        if (r == null) {
            stringRedisTemplate.opsForValue().set(key, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.SECONDS);
            return null;
        }

        // 6.数据库存在，新增缓存，返回数据
        this.set(key, r, timeout, timeUnit);
        return r;
    }

    /*
     * 逻辑过期查询（防缓存击穿）
     */
    public <R, ID> R queryWithLogicalExpire(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long timeout, TimeUnit timeUnit) {
        String key = keyPrefix + id;
        // 1.查询缓存
        String valueJson = stringRedisTemplate.opsForValue().get(key);

        // 2.缓存未命中，返回null
        if (StrUtil.isBlank(valueJson)) {
            return null;
        }

        // 3.缓存命中，判断逻辑过期时间
        RedisData<R> redisData = JSONUtil.toBean(valueJson, RedisData.class);

        // 4.逻辑未过期，返回缓存数据
        if (redisData.getExpireAt().isAfter(LocalDateTime.now())) {
            return redisData.getData();
        }

        // 5.逻辑过期，缓存重建
        String lockKey = "lock:" + key;
        String lockId = UUID.randomUUID().toString();

        // 5.1 尝试获取锁
        if (tryLock(lockKey, lockId)) {
            // Double Check
            String newValueJson = stringRedisTemplate.opsForValue().get(key);
            RedisData<R> newRedisData = JSONUtil.toBean(valueJson, RedisData.class);
            if (newRedisData.getExpireAt().isAfter(LocalDateTime.now())) {
                return newRedisData.getData();
            }

            // 5.2 重建缓存，然后返回旧数据
            pool.submit(() -> {
                try {
                    R r = dbFallback.apply(id);
                    this.setWtihLogicalExpire(key, r, type, timeout, timeUnit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    unlock(lockKey, lockId);
                }
            });

            return redisData.getData();
        }

        // 5.3 获取锁失败，返回旧数据
        return redisData.getData();
    }

    /*
     * 尝试获取锁
     */
    private boolean tryLock(String key, String lockId) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, lockId);
        return flag.equals(true);
    }

    /*
     * 释放锁
     */
    private void unlock(String key, String lockId) {
        // 使用 execute 方法执行 Lua 脚本
        // Collections.singletonList(key) 对应 KEYS[1]
        // lockId 对应 ARGV[1]
        Long result = stringRedisTemplate.execute(
                new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class),
                Collections.singletonList(key),
                lockId
        );

        if (result != null && result > 0) {
            // 锁已释放
        } else {
            // 锁已过期或不属于当前线程
        }
    }

    /*
     * 删除缓存
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }




}

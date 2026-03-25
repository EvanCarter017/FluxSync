package com.esther.fluxsync.service;

import com.esther.fluxsync.utils.LogConverter;
import com.esther.fluxsync.websocket.handler.MyWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisService.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 保存数据
    public void save(String key, Object value) { redisTemplate.opsForValue().set(key, value); }

    public void save(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    // 原子写
    public void savenx(String key, Object value) { redisTemplate.opsForValue().setIfAbsent(key, value); }

    // 原子自增
    public long incr(String key) {
        Long value = redisTemplate.opsForValue().increment(key);

        if (value == null) {
            throw new IllegalStateException("Redis INCR failed for key: " + key);
        }

        return value;
    }

    // 获取数据
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 删除数据
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // 判断 key 是否存在
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    // 重新设置 key 的 EX
    public boolean expire(String key, Duration ttl) { return Boolean.TRUE.equals(redisTemplate.expire(key, ttl)); }

    // 获取所有 keys
    public Set<String> getAllKeys() {
        return redisTemplate.keys("*");
    }

    // 清空所有 keys
    public void clear() { redisTemplate.execute((RedisCallback<Void>) connection -> {
        connection.serverCommands().flushDb();
        return null;
    }); }

    // 通配符模式移除键
    public void dimDelete(String pattern) {

        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(1)
                .build();

        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            if (cursor.hasNext()) {
                redisTemplate.delete(cursor.next());
            }
        } catch (Exception e) {
            log.debug(LogConverter.debug("(@Redis) 资源访问异常"));
        }

    }

}

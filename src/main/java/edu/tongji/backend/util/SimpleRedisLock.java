package edu.tongji.backend.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock{
    private String name;

    public SimpleRedisLock(String name, RedisTemplate RedisTemplate) {
        this.name = name;
        this.redisTemplate = RedisTemplate;
    }

    private RedisTemplate redisTemplate;
    private static final String KEY_PREFIX="lock:";

    @Override
    public boolean tryLock(long timeoutSec) {
        //Get the Id of currentthread
        Long threadId = Thread.currentThread().getId();
        Boolean success=redisTemplate.opsForValue().
                setIfAbsent(KEY_PREFIX+name,threadId.toString(),timeoutSec,
                TimeUnit.SECONDS);//setnx
        System.out.println("Success:"+KEY_PREFIX+name);
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void unlock() {
        //Release
        redisTemplate.delete(KEY_PREFIX+name);
    }
}

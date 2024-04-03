package edu.tongji.backend.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock{
    private String name;

    public SimpleRedisLock(String name, RedisTemplate RedisTemplate) {
        this.name = name;
        this.redisTemplate = RedisTemplate;
    }

    private RedisTemplate redisTemplate;
    private static final String KEY_PREFIX="lock:";
    private static final String ID_PREFIX=UUID.randomUUID()+"-";
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    static {
        UNLOCK_SCRIPT=new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }
    @Override
    public boolean tryLock(long timeoutSec) {
        //Get the Id of currentthread
        String threadId = ID_PREFIX+Thread.currentThread().getId();
        Boolean success=redisTemplate.opsForValue().
                setIfAbsent(KEY_PREFIX+name,threadId.toString(),timeoutSec,
                TimeUnit.SECONDS);//setnx
        System.out.println("Success:"+KEY_PREFIX+name);
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void unlock() {

        redisTemplate.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(KEY_PREFIX+name),
                ID_PREFIX+Thread.currentThread().getId()
        );
    }
}

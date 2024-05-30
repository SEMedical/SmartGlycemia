package edu.tongji.backend;

import edu.tongji.backend.util.RedidIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@SpringBootTest
@Slf4j
public class RedisUtilTest {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedidIdWorker redidIdWorker=new RedidIdWorker(stringRedisTemplate);
    private ExecutorService es= Executors.newFixedThreadPool(500);
    @Test
    void testIdWorker() throws InterruptedException {
        CountDownLatch latch=new CountDownLatch(300);
        Runnable task=()->{
            for(int i=0;i<100;i++){
                long id=redidIdWorker.nextId("exercise");
                log.info("id ="+id);
            }
            latch.countDown();
        };
        long begin=System.currentTimeMillis();
        for(int i=0;i<300;i++){
            es.submit(task);
        }
        latch.await();
        long end=System.currentTimeMillis();
        log.info("time ="+(end-begin));
    }
}

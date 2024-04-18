package edu.tongji.backend;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@Slf4j
@SpringBootTest
public class RabbitMQTest {
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//    @Test
//    public void testSimpleQueue(){
//        String queueName="simple.queue";
//        String message="hello,spring amqb!";
//        rabbitTemplate.convertAndSend(queueName,message);
//    }
    /*@Test
    @RabbitListener(queues="simple.queue")
    public void listenSimpleQueueMsg(String msg){
        log.info("Spring consumber accepted the msg:["+msg+"]");
        log.info("Done");
    }*/

}

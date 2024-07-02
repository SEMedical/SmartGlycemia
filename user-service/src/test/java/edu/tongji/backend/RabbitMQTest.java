package edu.tongji.backend;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */




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

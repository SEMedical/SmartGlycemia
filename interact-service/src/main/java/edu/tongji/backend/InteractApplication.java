package edu.tongji.backend;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableFeignClients
@SpringBootApplication
@MapperScan("edu.tongji.backend.mapper")
public class InteractApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(InteractApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

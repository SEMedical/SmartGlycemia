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
public class ExerciseApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(ExerciseApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

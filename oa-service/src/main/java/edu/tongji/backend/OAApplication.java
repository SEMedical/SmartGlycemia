package edu.tongji.backend;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@MapperScan("edu.tongji.backend.mapper")
@Slf4j
public class OAApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(OAApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

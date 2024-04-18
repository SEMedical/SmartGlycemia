package edu.tongji.backend;
import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.config.annotation.NacosProperty;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
@EnableFeignClients
@SpringBootApplication
@MapperScan("edu.tongji.backend.mapper")
//@NacosPropertySource(dataId = "security.yml")
public class UserApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(UserApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

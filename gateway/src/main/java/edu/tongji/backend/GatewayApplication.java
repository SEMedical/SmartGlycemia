package edu.tongji.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(GatewayApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

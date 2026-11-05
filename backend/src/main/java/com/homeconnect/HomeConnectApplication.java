package com.homeconnect;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.homeconnect.mapper")
public class HomeConnectApplication {
    public static void main(String[] args) {
        SpringApplication.run(HomeConnectApplication.class, args);
    }
}

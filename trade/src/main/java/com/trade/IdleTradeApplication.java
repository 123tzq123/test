package com.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.trade.mapper")
@EnableScheduling
public class IdleTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdleTradeApplication.class, args);
    }
}
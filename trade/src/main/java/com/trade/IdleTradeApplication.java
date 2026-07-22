package com.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
@MapperScan("com.trade.mapper")
@EnableScheduling
public class IdleTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdleTradeApplication.class, args);
    }

    // 新增：DeepSeek接口调用专用RestTemplate，设置超时10秒
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // 超时10秒，防止AI接口卡死
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }
}
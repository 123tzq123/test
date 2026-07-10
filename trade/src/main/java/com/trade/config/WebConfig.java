package com.trade.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import javax.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private JwtConfig jwtConfig;

    //注册JWT拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtConfig)
                .addPathPatterns("/**") //拦截所有请求
                .excludePathPatterns("/user/login","/user/register"); //放行登录注册
    }
}
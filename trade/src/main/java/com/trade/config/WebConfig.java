package com.trade.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import javax.annotation.Resource;
import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private JwtConfig jwtConfig;

    //注册JWT拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtConfig)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/category/list",
                        "/goods/list",
                        "/goods/detail",
                        "/message/list/**",
                        "/upload/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问路径 /upload/** 映射到项目根目录upload文件夹
        String projectPath = System.getProperty("user.dir") + File.separator + "upload" + File.separator;
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + projectPath);
    }
}
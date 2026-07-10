//package com.trade.config;
//
//import com.trade.util.JwtUtil;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import javax.annotation.Resource;
//
//@Configuration
//public class JwtConfig implements WebMvcConfigurer {
//    @Resource
//    private JwtUtil jwtUtil;
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(jwtUtil)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/user/login","/user/register","/goods/list");
//    }
//}
package com.trade.config;

import com.trade.constant.AttributeConst;
import com.trade.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtConfig implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1. 获取请求头里面token
        String token = request.getHeader("token");
        //放行登录、注册接口
        String path = request.getRequestURI();
        if(path.contains("/user/login") || path.contains("/user/register")){
            return true;
        }
        if(token == null || token.isEmpty()){
            response.setStatus(401);
            return false;
        }
        try {
            //====核心改动：只调用工具类的parseToken，不用自己写jjwt底层解析代码====
            Long userId = jwtUtil.parseToken(token);
            //把userId放到请求域，Controller里面可以获取登录用户id
            request.setAttribute(AttributeConst.LOGIN_USER_ID,userId);
        }catch (Exception e){
            //token过期或者token错误，返回401未登录
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
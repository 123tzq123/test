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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.constant.AttributeConst;
import com.trade.util.JwtUtil;
import com.trade.vo.ResultVO;
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
        //放行OPTIONS预检请求
        if("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return true;
        }
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }
        try {
            Long userId = jwtUtil.parseToken(token);
            request.setAttribute(AttributeConst.LOGIN_USER_ID, userId);
        } catch (Exception e) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(401);
            ResultVO<Void> result = ResultVO.fail(401,"登录失效，请重新登录");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), result);
            return false;
        }
        return true;
    }
}
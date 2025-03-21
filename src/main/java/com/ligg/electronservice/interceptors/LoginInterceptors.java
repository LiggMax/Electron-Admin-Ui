package com.ligg.electronservice.interceptors;

import com.ligg.electronservice.utils.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * 登录拦截器
 */
@Slf4j
@Component
public class LoginInterceptors implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //获取请求头中的token
        String Token = request.getHeader("Token");

        try {
            //解析请求头token获取用户信息
            Map<String, Object> stringObjectMap = JWTUtil.parseTokenWithValidation(Token);
            String userId = (String) stringObjectMap.get("userId");
            //从Redis中获取用户token
            String redisUserToken = redisTemplate.opsForValue().get("Token:" + userId);
            if (redisUserToken == null){
                throw new RuntimeException();
            }
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
}

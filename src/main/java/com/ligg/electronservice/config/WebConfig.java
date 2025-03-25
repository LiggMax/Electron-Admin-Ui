package com.ligg.electronservice.config;

import com.ligg.electronservice.interceptors.LoginInterceptors;
import com.ligg.electronservice.interceptors.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptors loginInterceptors;
    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录拦截器
        registry.addInterceptor(loginInterceptors)
                .order(1)
                .excludePathPatterns("/api/account/**")//放行路径
                .addPathPatterns("/api/**");//拦截路径
        //请求拦截器
        registry.addInterceptor(rateLimitInterceptor)
                .order(2)
                .addPathPatterns("/**");
    }
}

package com.ligg.electronservice;

import com.ligg.electronservice.utils.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

@SpringBootTest
class
ElectronServiceApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    public void getToken() {
        Map<String, Object> stringObjectMap = JWTUtil.parseTokenWithValidation
                ("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGFpbXMiOnsidXNlcklkIjoiMSIsInVzZXJuYW1lIjoibGlnZyJ9LCJleHAiOjE3NDI1NTUyMjV9.hgf3r4dDbRtJs1F-brDiGaNRfp6aYz3WuypBC2W75UM");
        String userId = (String) stringObjectMap.get("userId");
        String username = (String) stringObjectMap.get("username");
        System.out.println(userId);
        System.out.println(username);
        String RedisToken = redisTemplate.opsForValue().get("Token:" + userId);
        System.out.println(RedisToken);
    }

}

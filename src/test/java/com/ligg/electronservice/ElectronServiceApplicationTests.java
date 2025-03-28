package com.ligg.electronservice;

import com.ligg.electronservice.utils.JWTUtil;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

@Slf4j
@SpringBootTest
class
ElectronServiceApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
    }

//    @Test
//    public void getToken() {
//        Map<String, Object> stringObjectMap = JWTUtil.parseTokenWithValidation
//                ("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGFpbXMiOnsidXNlcklkIjoiMTIzIiwidXNlcm5hbWUiOiJsaWdnIn0sImV4cCI6MTc0MjkwNjY2NX0.4yfPVZ_gpWY1OahrZ-nGuzgi1JqJQHimdvhjhLatnoU");
//        String userId = (String) stringObjectMap.get("userId");
//        String username = (String) stringObjectMap.get("username");
//        System.out.println(userId);
//        System.out.println(username);
//        String RedisToken = redisTemplate.opsForValue().get("Token:" + userId);
//        log.info("Token:{}" ,RedisToken);
//    }

}

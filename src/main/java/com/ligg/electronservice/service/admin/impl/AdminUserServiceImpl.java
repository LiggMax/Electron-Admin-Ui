package com.ligg.electronservice.service.admin.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ligg.electronservice.mapper.admin.AdminUserMapper;
import com.ligg.electronservice.pojo.admin.AdminUser;
import com.ligg.electronservice.service.admin.AdminUserService;
import com.ligg.electronservice.utils.JWTUtil;
import com.ligg.electronservice.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AdminUserMapper userMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JWTUtil jwtUtil;
    /**
     * 查询用户信息
     *
     * @param account  账号
     * @param password 密码
     * @return 用户信息
     */
    @Override
    public AdminUser findByUser(String account, String password) {
        return userMapper.findByUser(account, password);
    }

    /**
     * 获取token
     *
     * @return token
     */
    @Override
    public String getToken(AdminUser user) {
        //创建载荷储存用户信息
        Map<String, Object> claims = new HashMap<>();//存储用户信息
        claims.put("userId", user.getUserId());
        claims.put("username", user.getAccount());
        String token = jwtUtil.createToken(claims);
        //将token存储到redis中,过期时间6小时
        redisTemplate.opsForValue()
                .set("Token:" + user.getUserId(), token,
                        6, TimeUnit.HOURS);
        return token;
    }

    /**
     * 清除token
     */
    @Override
    public void clearToken() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        redisTemplate.delete("Token:" + userId);
    }

    @Override
    public AdminUser findByUserInfo(String userId) throws JsonProcessingException {
        String RedisUserInfo = redisTemplate.opsForValue().get("userInfo:" + userId);
        if (RedisUserInfo == null) {
            AdminUser userInfo = userMapper.findByUserInfo(userId);
            if (userInfo != null) {
                redisTemplate.opsForValue().set("userInfo:" + userId, objectMapper.writeValueAsString(userInfo), 6, TimeUnit.HOURS);
                return userInfo;
            }
        }
        return objectMapper.readValue(RedisUserInfo, AdminUser.class);
    }
}

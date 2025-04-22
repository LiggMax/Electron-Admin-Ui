package com.ligg.electronservice.service.user.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ligg.electronservice.mapper.user.UserMapper;
import com.ligg.electronservice.pojo.user.User;
import com.ligg.electronservice.service.user.UserService;
import com.ligg.electronservice.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JWTUtil jwtUtil;

    /**
     * 登录
     */
    @Override
    public User findByAccount(String account, String password) {

        User user = userMapper.findByAccount(account);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    /**
     * 创建token
     */
    @Override
    public String createUserToken(Long userId, String account) {

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("userId", userId);
        userMap.put("account", account);
        String userToken = jwtUtil.createToken(userMap);

        redisTemplate.opsForValue().set("userToken:" + account, userToken,
                6, TimeUnit.HOURS);
        return userToken;
    }

    /**
     * 根据id获取用户信息·
     */
    @Override
    public User findByUserInfo(Integer userId) {
        String userInfo = null;

        try {
            userInfo = redisTemplate.opsForValue().get("userInfo:" + userId);
        } catch (Exception e) {
            log.error("Redis获取用户信息失败，将从数据库获取", e);
        }

        if (userInfo != null) {
            try {
                return objectMapper.readValue(userInfo, User.class);
            } catch (Exception e) {
                log.error("用户信息序列化异常", e);
            }
        }

        // 根据id查询数据库获取用户信息
        User byUserInfo = userMapper.findByUserInfo(userId);
        if (byUserInfo != null) {
            try {
                String userInfoJson = objectMapper.writeValueAsString(byUserInfo);
                redisTemplate.opsForValue().set("userInfo:" + userId, userInfoJson, 6, TimeUnit.HOURS);
            } catch (Exception e) {
                log.error("Redis存储用户信息失败", e);
            }
        }
        return byUserInfo;
    }
}

package com.ligg.electronservice.service.impl;

import com.ligg.electronservice.mapper.UserMapper;
import com.ligg.electronservice.pojo.User;
import com.ligg.electronservice.service.UserService;
import com.ligg.electronservice.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserMapper userMapper;

    /**
     * 查询用户信息
     *
     * @param account  账号
     * @param password 密码
     * @return 用户信息
     */
    @Override
    public User findByUser(String account, String password) {
        return userMapper.findByUser(account, password);
    }

    /**
     * 获取token
     * @return token
     */
    @Override
    public String getToken(User user) {
        //创建载荷储存用户信息
        Map<String, Object> claims = new HashMap<>();//存储用户信息
        claims.put("userId", user.getUserId());
        claims.put("username", user.getAccount());
        String token = JWTUtil.getToken(claims);
        //将token存储到redis中,过期时间6小时
        redisTemplate.opsForValue()
                .set("Token:" + user.getUserId(), token,
                        6, TimeUnit.HOURS);
        return token;
    }

    @Override
    public User findByUserInfo() {


        return null;
    }
}

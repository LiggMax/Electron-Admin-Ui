package com.ligg.electronservice.controller;

import com.ligg.electronservice.pojo.Result;
import com.ligg.electronservice.pojo.User;
import com.ligg.electronservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RequestMapping
@RestController
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 登录
     * return: token
     */
    @CrossOrigin
    @PostMapping("/api/login")
    public Result<?> login(@RequestParam String account,
                           @RequestParam String password){
       User userInfo = userService.findByUser(account,password);
       if (userInfo == null){
           return Result.error(400,"账号或密码错误");
       }
//       //判断用户是否登录
//        String userToken = redisTemplate.opsForValue().get("user:" + userInfo.getUserId() + "Token");
//        if (userToken != null){
//            return Result.success(200,userToken);
//        }
       //生成Token
        String token = userService.getToken(userInfo);
        return Result.success(200,token);
    }
}

package com.ligg.electronservice.controller;

import com.ligg.electronservice.pojo.Result;
import com.ligg.electronservice.pojo.User;
import com.ligg.electronservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class LoginController {

    @Autowired
    private UserService userService;


    /**
     * 登录
     * return: token
     */
    @PostMapping("/login")
    public Result<?> login(@RequestParam String account,
                                @RequestParam String password){
       User userInfo = userService.findByUser(account,password);
       if (userInfo == null){
           return Result.error(400,"账号或密码错误");
       }
       //生成Token
        String token = userService.getToken(userInfo);
        return Result.success(200,token);
    }
}

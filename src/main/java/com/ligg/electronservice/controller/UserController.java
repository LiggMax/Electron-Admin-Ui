package com.ligg.electronservice.controller;


import com.ligg.electronservice.pojo.Result;
import com.ligg.electronservice.pojo.User;
import com.ligg.electronservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<User> getUserInfo() {
        User userInfo = userService.findByUserInfo();
        return Result.success();
    }
}

package com.ligg.electronservice.controller;


import com.ligg.electronservice.pojo.Result;
import com.ligg.electronservice.pojo.User;
import com.ligg.electronservice.service.UserService;
import com.ligg.electronservice.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     */
    @GetMapping("/userInfo")
    public Result<User> getUserInfo() {
        Map<String,Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        User userInfo = userService.findByUserInfo(userId);
        return Result.success(200, userInfo);
    }
    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public Result<String> logout() {
        userService.clearToken();
        return Result.success(200, "退出成功");
    }
}

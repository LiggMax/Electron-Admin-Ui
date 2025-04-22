package com.ligg.electronservice.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ligg.electronservice.utils.Result;
import com.ligg.electronservice.pojo.admin.AdminUser;
import com.ligg.electronservice.service.admin.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/account")
public class AdminAccountController {

    @Autowired
    private AdminUserService userService;

    /**
     * 登录
     * return: token
     */
    @PostMapping("/login")
    public Result<?> login(@RequestParam String account,
                           @RequestParam String password) throws JsonProcessingException {
       AdminUser userInfo = userService.findByUser(account,password);
       if (userInfo == null){
           return Result.error(400,"账号或密码错误");
       }
//       //判断用户Token是否存在
//        String userToken = redisTemplate.opsForValue().get("user:" + userInfo.getUserId() + "Token");
//        if (userToken != null){
//            return Result.success(200,userToken);
//        }
       //生成Token
        String token = userService.getToken(userInfo);
        return Result.success(200,token);
    }
}

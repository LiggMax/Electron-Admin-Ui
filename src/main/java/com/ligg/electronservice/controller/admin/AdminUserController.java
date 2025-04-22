package com.ligg.electronservice.controller.admin;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.ligg.electronservice.utils.Result;
import com.ligg.electronservice.pojo.admin.AdminUser;
import com.ligg.electronservice.service.admin.AdminUserService;
import com.ligg.electronservice.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    @Autowired
    private AdminUserService userService;

    /**
     * 获取用户信息
     */
    @GetMapping("/userInfo")
    public Result<AdminUser> getUserInfo() throws JsonProcessingException {
        Map<String,Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("userId");
        AdminUser userInfo = userService.findByUserInfo(userId);
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

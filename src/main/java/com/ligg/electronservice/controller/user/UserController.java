package com.ligg.electronservice.controller.user;


import com.ligg.electronservice.pojo.user.User;
import com.ligg.electronservice.service.user.UserService;
import com.ligg.electronservice.utils.JWTUtil;
import com.ligg.electronservice.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JWTUtil jwtUtil;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody Map<String,Object> user){

        if (! (user.get("account") instanceof String account && user.get("password") instanceof String password)){
            return Result.error(400,"参数错误");
        }

        User byAccount = userService.findByAccount(account, password);
        if (byAccount != null){
            String userToken = userService.createUserToken(byAccount.getUserId(), byAccount.getAccount());
            return Result.success(200,userToken);
        }
        return Result.error(400,"账号或密码错误");
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/userInfo")
    public Result<User> userInfo(){
        String userToken = request.getHeader("Token");
        Map<String, Object> userMap = jwtUtil.parseToken(userToken);
        Integer userId = (Integer) userMap.get("userId");

        User byUserInfo = userService.findByUserInfo(userId);
        return Result.success(200,byUserInfo);
    }
}

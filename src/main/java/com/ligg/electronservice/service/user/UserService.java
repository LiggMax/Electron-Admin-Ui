package com.ligg.electronservice.service.user;


import com.ligg.electronservice.pojo.user.User;

public interface UserService {

    /**
     * 根据账号和密码查询用户
     */
    User findByAccount(String account, String password);

    /**
     * 生成Token
     */
    String createUserToken(Long userId, String account);

    User findByUserInfo(Integer userId);
}

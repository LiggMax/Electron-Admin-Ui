package com.ligg.electronservice.service;

import com.ligg.electronservice.pojo.User;

public interface UserService {

    /**
     * 登录
     */
    User findByUser(String account, String password);

    /**
     * 获取token
     */
    String getToken(User user);

    /**
     * 清除缓存中的token
     */
    void clearToken();
    /**
     * 获取用户信息
     */
    User findByUserInfo(String userId);
}

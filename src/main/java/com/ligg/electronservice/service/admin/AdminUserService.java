package com.ligg.electronservice.service.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ligg.electronservice.pojo.admin.AdminUser;

public interface AdminUserService {

    /**
     * 登录
     */
    AdminUser findByUser(String account, String password) throws JsonProcessingException;

    /**
     * 获取token
     */
    String getToken(AdminUser user);

    /**
     * 清除缓存中的token
     */
    void clearToken();
    /**
     * 获取用户信息
     */
    AdminUser findByUserInfo(String userId) throws JsonProcessingException;
}

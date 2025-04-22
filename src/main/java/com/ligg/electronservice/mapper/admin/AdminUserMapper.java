package com.ligg.electronservice.mapper.admin;

import com.ligg.electronservice.pojo.admin.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminUserMapper {

    @Select("select user_id, nick_name, email, account " +
            "from admin_users where account = #{account} and password = #{password}")
    AdminUser findByUser(String account, String password);

    @Select("select user_id, nick_name, email, account,created_at, updated_at, user_avatar from admin_users where user_id = #{userId} ")
    AdminUser findByUserInfo(String userId);
}

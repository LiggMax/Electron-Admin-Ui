package com.ligg.electronservice.mapper;

import com.ligg.electronservice.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select user_id, nick_name, email, account " +
            "from users where account = #{account} and password = #{password}")
    User findByUser(String account, String password);

    @Select("select user_id, nick_name, email, account,created_at, updated_at, user_avatar from users where user_id = #{userId} ")
    User findByUserInfo(String userId);
}

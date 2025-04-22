package com.ligg.electronservice.mapper.user;

import com.ligg.electronservice.pojo.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据账号查询用户信息
     */
    @Select("select user_id,account,password" +
            " from users where account = #{account}")
    User findByAccount(String account);

    /**
     * 根据用户id查询详细信息
     */
    @Select("select * from users where user_id = #{userId}")
    User findByUserInfo(Integer userId);
}

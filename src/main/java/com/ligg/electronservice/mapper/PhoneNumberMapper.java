package com.ligg.electronservice.mapper;

import com.ligg.electronservice.pojo.Phone;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PhoneNumberMapper {

    /**
     * 条件查询卡号数据
     */
    List<Phone> phoneList(String countryCode, Integer status);
}

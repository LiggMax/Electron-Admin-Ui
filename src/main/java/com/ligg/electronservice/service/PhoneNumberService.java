package com.ligg.electronservice.service;

import com.ligg.electronservice.pojo.PageBean;
import com.ligg.electronservice.pojo.Phone;

public interface PhoneNumberService {

    /**
     * 分页查询卡号集合
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @param countryCode 号码归属地
     * @param usageStatus 号码状态
     * @return 分页对象
     */
    PageBean<Phone> phoneList(Integer pageNum, Integer pageSize, String countryCode, Integer usageStatus);
}

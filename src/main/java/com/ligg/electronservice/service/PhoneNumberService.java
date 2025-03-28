package com.ligg.electronservice.service;

import com.ligg.electronservice.pojo.PageBean;
import com.ligg.electronservice.pojo.Phone;

import java.util.List;
import java.util.Map;

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
    
    /**
     * 批量添加手机号
     * @param phoneNumbers 手机号列表
     * @param country 国家
     * @param project 项目
     * @return 成功添加的数量
     */
    int batchAddPhoneNumbers(List<String> phoneNumbers, String country, String project);

    /**
     * 根据手机号ID查询手机号详情
     *
     * @param phoneId 手机号ID
     * @return 手机号详情（包含基本信息和项目列表）
     */
    Map<String, Object> phoneDetail(Integer phoneId);
}

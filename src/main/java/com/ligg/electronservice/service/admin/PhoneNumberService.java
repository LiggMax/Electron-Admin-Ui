package com.ligg.electronservice.service.admin;

import com.ligg.electronservice.pojo.admin.Phone;

import java.util.List;
import java.util.Map;

public interface PhoneNumberService {

    /**
     * 分页查询卡号列表
     * @param countryCode 号码归属地
     * @param usageStatus 号码状态
     * @return 分页对象
     */
    List<Phone> phoneList( String countryCode, Integer usageStatus,String keyword);
    
    /**
     * 批量添加手机号
     * @param phoneNumbers 手机号列表
     * @param country 国家
     * @param projects 项目列表
     * @return 成功添加的数量
     */
    int batchAddPhoneNumbers(List<String> phoneNumbers, String country, List<String> projects);

    /**
     * 根据手机号ID查询手机号详情
     *
     * @param phoneId 手机号ID
     * @return 手机号详情（包含基本信息和项目列表）
     */
    Map<String, Object> phoneDetail(Integer phoneId);
}

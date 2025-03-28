package com.ligg.electronservice.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ligg.electronservice.mapper.PhoneNumberMapper;
import com.ligg.electronservice.pojo.PageBean;
import com.ligg.electronservice.pojo.Phone;
import com.ligg.electronservice.service.PhoneNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhoneNumberServiceImpl implements PhoneNumberService {

    @Autowired
    private PhoneNumberMapper phoneNumberMapper;

    /**
     * 分页查询卡号列表
     * 1.创建PageBean对象，设置总记录数和当前页数据
     * 2.开启分页
     * 3.调用mapper方法查询数据
     */
    @Override
    public PageBean<Phone> phoneList(Integer pageNum, Integer pageSize, String countryCode, Integer usageStatus) {
        PageBean<Phone> pageBean = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        List<Phone> phoneList = phoneNumberMapper.phoneList(countryCode, usageStatus);
        //Page中提供了方法，可获得PageHelper分页查询后,获得的总记录数和当前页数据.
        Page<Phone> page = (Page<Phone>) phoneList;

        pageBean.setTotal(page.getTotal());
        pageBean.setItems(page.getResult());
        return pageBean;
    }
    
    /**
     * 批量添加手机号
     * 
     * @param phoneNumbers 手机号列表
     * @param country 国家
     * @param project 项目
     * @return 成功添加的数量
     */
    @Override
    @Transactional
    public int batchAddPhoneNumbers(List<String> phoneNumbers, String country, String project) {
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            return 0;
        }
        // 创建Phone对象列表
        List<Phone> phones = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // 转换手机号并创建Phone对象
        for (String phoneStr : phoneNumbers) {
            try {
                // 将字符串转换为Long类型的手机号
                Long phoneNumber = Long.parseLong(phoneStr);
                
                // 创建新的Phone对象
                Phone phone = new Phone();
                phone.setPhoneNumber(phoneNumber);
                phone.setCountryCode(country);      // 设置国家
                phone.setLineStatus(1);             // 默认在线状态
                phone.setUsageStatus(1);            // 默认使用状态为正常
                phone.setRegistrationTime(now);     // 设置注册时间

                phones.add (phone);
            } catch (NumberFormatException e) {
                // 忽略无法转换为数字的手机号
                continue;
            }
        }
        
        // 如果没有有效的手机号需要添加，则直接返回0
        if (phones.isEmpty()) {
            return 0;
        }
        // 批量插入手机号，使用INSERT IGNORE语法处理唯一键冲突
        int result = phoneNumberMapper.batchInsertPhones(phones);
        
        // 插入项目关联信息
        for (Phone phone : phones) {
            phoneNumberMapper.insertPhoneProject(phone.getPhoneNumber(), project);
        }
        return result;
    }
}

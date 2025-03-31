package com.ligg.electronservice.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ligg.electronservice.mapper.PhoneNumberMapper;
import com.ligg.electronservice.pojo.PageBean;
import com.ligg.electronservice.pojo.Phone;
import com.ligg.electronservice.service.PhoneNumberService;
import com.ligg.electronservice.dto.PhoneDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据手机号ID查询手机号详情
     *
     * @param phoneId 手机号ID
     * @return 手机号详情（包含基本信息和项目列表）
     */
    @Override
    public Map<String, Object> phoneDetail(Integer phoneId) {
        // 获取手机号的详细信息
        List<PhoneDetailDTO> phoneDetails = phoneNumberMapper.queryByIdPhoneDetail(phoneId);
        
        // 构建结果
        Map<String, Object> resultMap = new HashMap<>();
        
        if (phoneDetails != null && !phoneDetails.isEmpty()) {
            // 获取第一条记录的基本信息
            PhoneDetailDTO baseInfo = phoneDetails.get(0);

            // 构建基本信息
            Map<String, Object> phoneInfo = new HashMap<>();
            phoneInfo.put("phoneId", baseInfo.getPhoneId());
            phoneInfo.put("phoneNumber", baseInfo.getPhoneNumber());
            phoneInfo.put("countryCode", baseInfo.getCountryCode());
            phoneInfo.put("lineStatus", baseInfo.getLineStatus());
            phoneInfo.put("usageStatus", baseInfo.getUsageStatus());
            phoneInfo.put("registrationTime", baseInfo.getRegistrationTime());

            // 添加基本信息到结果
            resultMap.put("basicInfo", phoneInfo);

            // 构建项目列表
            List<Map<String, Object>> projectList = new ArrayList<>();
            for (PhoneDetailDTO detail : phoneDetails) {
                if (detail.getProjectName() != null) {
                    Map<String, Object> projectMap = new HashMap<>();
                    projectMap.put("projectName", detail.getProjectName());
                    projectMap.put("timeOfUse", detail.getTimeOfUse());
                    projectList.add(projectMap);
                }
            }
            
            // 添加项目列表到结果
            resultMap.put("projects", projectList);
        }
        
        return resultMap;
    }
}

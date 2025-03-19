package com.ligg.electronservice.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ligg.electronservice.mapper.PhoneNumberMapper;
import com.ligg.electronservice.pojo.PageBean;
import com.ligg.electronservice.pojo.Phone;
import com.ligg.electronservice.service.PhoneNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public PageBean<Phone> phoneList(Integer pageNum, Integer pageSize, String countryCode, Integer status) {
        PageBean<Phone> pageBean = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        List<Phone> phoneList = phoneNumberMapper.phoneList(countryCode, status);
        //Page中提供了方法，可获得PageHelper分页查询后,获得的总记录数和当前页数据.
        Page<Phone> page = (Page<Phone>) phoneList;

        pageBean.setTotal(page.getTotal());
        pageBean.setItems(page.getResult());
        return pageBean;
    }
}

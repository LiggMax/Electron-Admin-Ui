package com.ligg.electronservice.controller;

import com.ligg.electronservice.pojo.PageBean;
import com.ligg.electronservice.pojo.Phone;
import com.ligg.electronservice.pojo.Result;
import com.ligg.electronservice.service.PhoneNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/phone")
public class PhoneNumberController {

    @Autowired
    private PhoneNumberService phoneNumberService;

    /**
     * 分页查询卡号数据
     */
    @GetMapping("/phoneList")
    public Result<PageBean<Phone>> phoneList(
            Integer pageNum,//当前页码
            Integer pageSize,//每页显示条数
            @RequestParam(required = false) String countryCode,//号码归属地
            @RequestParam(required = false) Integer status //号码状态
    ) {
        PageBean<Phone> pageBean = phoneNumberService.phoneList(pageNum, pageSize, countryCode, status);
        return Result.success(200, pageBean);
    }

}

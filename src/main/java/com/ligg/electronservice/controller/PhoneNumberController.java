package com.ligg.electronservice.controller;

import com.ligg.electronservice.anno.Status;
import com.ligg.electronservice.pojo.PageBean;
import com.ligg.electronservice.pojo.Phone;
import com.ligg.electronservice.pojo.Result;
import com.ligg.electronservice.service.PhoneNumberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/phone")
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
            @RequestParam(required = false) Integer usageStatus //号码状态
    ) {
        PageBean<Phone> pageBean = phoneNumberService.phoneList(pageNum, pageSize, countryCode, usageStatus);
        return Result.success(200, pageBean);
    }

    /**
     * 批量上传手机号
     */
    @PostMapping("/upload")
    public Result<?> uploadPhoneNumbers(@RequestBody Map<String, Object> uploadData) {
        try {
            // 从请求体中获取国家和项目信息
            String country = (String) uploadData.get("country");
            String project = (String) uploadData.get("project");
            
            // 获取文件列表
            List<Map<String, Object>> files = (List<Map<String, Object>>) uploadData.get("files");
            
            // 用于记录处理结果
            int totalProcessed = 0; // 总处理数量
            int totalAdded = 0;     // 成功添加数量
            int totalDuplicate = 0; // 重复数量
            int totalInvalid = 0;   // 无效数量
            
            // 处理每个文件中的手机号
            for (Map<String, Object> fileData : files) {
                // 获取手机号列表
                List<String> phoneNumbers = (List<String>) fileData.get("phoneNumbers");
                
                if (phoneNumbers != null) {
                    totalProcessed += phoneNumbers.size();
                      // 批量添加手机号到数据库
                    int added = phoneNumberService.batchAddPhoneNumbers(phoneNumbers, country, project);
                    totalAdded += added;
                    
                    // 计算重复和无效数量
                    totalDuplicate += phoneNumbers.size() - added;
                }
            }
            
            // 构建响应结果
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("total", totalProcessed);
            resultData.put("added", totalAdded);
            resultData.put("duplicate", totalDuplicate);
            resultData.put("invalid", totalInvalid);
            resultData.put("message", "导入成功：成功添加" + totalAdded + "个手机号，" + 
                          (totalDuplicate > 0 ? totalDuplicate + "个重复号码已跳过" : ""));
            
            // 返回成功结果和详细信息
            return Result.success(200, resultData);
        } catch (Exception e) {
            // 异常处理
            log.error("上传失败: " + e.getMessage());
            return Result.error(500, "上传失败: " + e.getMessage());
        }
    }
}

package com.ligg.electronservice.mapper.admin;

import com.ligg.electronservice.dto.PhoneDetailDTO;
import com.ligg.electronservice.pojo.admin.Phone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PhoneNumberMapper {

    /**
     * 条件查询卡号数据
     */
    List<Phone> phoneList(String countryCode, Integer usageStatus, String keyword);

    /**
     * 批量插入手机号
     * @param phones 手机号列表
     * @return 插入成功的条数
     */
    int batchInsertPhones(@Param("phones") List<Phone> phones);

    /**
     * 插入手机号项目
     */
    int insertPhoneProject(@Param("phoneNumber") Long phoneNumber, @Param("projectName") String projectName);

    /**
     * 检查手机号是否已存在
     * @param phoneNumber 手机号
     * @return 存在的数量
     */
    int checkPhoneExists(@Param("phoneNumber") Long phoneNumber);

    /**
     * 根据手机号id查询详情
     */
    List<PhoneDetailDTO> queryByIdPhoneDetail(Integer phoneId);
}

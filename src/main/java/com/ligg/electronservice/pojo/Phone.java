package com.ligg.electronservice.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {
    private Integer phoneId;//id
    private Integer phoneNumber;//手机号
    private Integer lineStatus;//线路状态 1.在线、2.离线
    private String countryCode;//号码归属地
    private LocalDateTime registrationTime;//注册时间
    private Integer usageStatus;//状态 1.正常、2.停用
}

package com.ligg.electronservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class phoneDetailVo {
    private Integer phoneId;
    private Long phoneNumber;
}

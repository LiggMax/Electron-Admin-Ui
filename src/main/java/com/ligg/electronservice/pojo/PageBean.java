package com.ligg.electronservice.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageBean<T> {
    private Long total;//总记录数
    private List<T> items;//当前页数据
}

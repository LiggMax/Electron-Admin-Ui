package com.ligg.electronservice.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;
    private String account;//账号
    private String nickName;
    @JsonIgnore
    private String password;
    private String email;
    private LocalDateTime createdAt;//创建时间
    private LocalDateTime updatedAt;//更新时间
}

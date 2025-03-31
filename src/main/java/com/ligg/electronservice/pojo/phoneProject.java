package com.ligg.electronservice.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class phoneProject {
    private Integer phoneProjectId;
    private String projectName;
    private Long phoneNumber;
    private LocalDateTime timeOfUse;
}

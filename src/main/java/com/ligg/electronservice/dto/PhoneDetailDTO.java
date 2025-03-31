package com.ligg.electronservice.dto;

import java.time.LocalDateTime;

public class PhoneDetailDTO {
    private Integer phoneId;
    private Long phoneNumber;
    private String countryCode;
    private Integer lineStatus;
    private Integer usageStatus;
    private LocalDateTime registrationTime;
    // 项目相关字段
    private String projectName;
    private LocalDateTime timeOfUse;

    // Getters and Setters
    public Integer getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(Integer phoneId) {
        this.phoneId = phoneId;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getLineStatus() {
        return lineStatus;
    }

    public void setLineStatus(Integer lineStatus) {
        this.lineStatus = lineStatus;
    }

    public Integer getUsageStatus() {
        return usageStatus;
    }

    public void setUsageStatus(Integer usageStatus) {
        this.usageStatus = usageStatus;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public LocalDateTime getTimeOfUse() {
        return timeOfUse;
    }

    public void setTimeOfUse(LocalDateTime timeOfUse) {
        this.timeOfUse = timeOfUse;
    }
} 
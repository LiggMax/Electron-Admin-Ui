package com.ligg.electronservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ligg.electronservice.service.BangumiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class BangumiServiceImpl implements BangumiService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CALENDAR_URL = "https://api.bgm.tv/calendar";

    @Override
    public JsonNode getCalendar() throws JsonProcessingException {

        // 1. 创建RestTemplate对象
        RestTemplate restTemplate = new RestTemplate();
        // 2. 发送请求
        if (Boolean.TRUE.equals(redisTemplate.hasKey("BangumiCalendar"))){
            return objectMapper.readTree(redisTemplate.opsForValue().get("BangumiCalendar"));
        }
        ResponseEntity<String> response = restTemplate.getForEntity(CALENDAR_URL, String.class);

        log.info("响应头：");
        response.getHeaders().forEach((key, values) -> {
            log.info("  {} : {}", key, String.join(", ", values));
        });
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        redisTemplate.opsForValue().set("BangumiCalendar", String.valueOf(jsonNode),1, TimeUnit.DAYS);
        return jsonNode;
    }
}

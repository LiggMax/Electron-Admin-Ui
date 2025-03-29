package com.ligg.electronservice.controller.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ligg.electronservice.pojo.Result;
import com.ligg.electronservice.service.BangumiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin/bangumi")
public class BangumiController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BangumiService bangumiService;

    @GetMapping("/calendar")
    public Result<JsonNode> getCalendar() throws JsonProcessingException {
        JsonNode calendar = bangumiService.getCalendar();
        return Result.success(200, calendar);
    }
}

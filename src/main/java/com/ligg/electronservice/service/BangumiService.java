package com.ligg.electronservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface BangumiService {

    JsonNode getCalendar() throws JsonProcessingException;
}

package com.ligg.electronservice.controller.oauth;

import com.ligg.electronservice.pojo.Result;
import com.ligg.electronservice.service.anime.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import javax.swing.text.Document;
import java.util.Map;

@RestController
@RequestMapping("/api/anime")
public class AnimeController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AnimeService animeService;


    @GetMapping("/Search")
    public Result<Map<String, Object>> getSearchList(String keyword) {
        Map<String, Object> stringObjectMap = animeService.searchAnime(keyword);
        return Result.success(200, stringObjectMap);
    }

    @GetMapping("/PlayVideo")
    public Result<String> getPlayVideo(String url) {
        String playVideoUrl = animeService.getPlayVideoUrl(url);
        return Result.success(200, playVideoUrl);
    }
}

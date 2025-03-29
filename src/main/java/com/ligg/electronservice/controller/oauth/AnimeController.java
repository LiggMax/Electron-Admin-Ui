package com.ligg.electronservice.controller.oauth;

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

@RestController
@RequestMapping("/api/anime")
public class AnimeController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String XFDM_SEARCH_URL = "https://dm1.xfdm.pro/search.html?wd=";


    @GetMapping("/Search")
    public ResponseEntity<String> Search(String keyword) {
        ResponseEntity<String> forEntity = restTemplate.getForEntity(XFDM_SEARCH_URL + keyword, String.class);

        return forEntity;
    }

    public static void main(String[] args) {
        String keyword = "败犬女主";
        String url = "https://dm1.xfdm.pro/search.html?wd=" + keyword;

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);

        System.out.println(forEntity);
    }
}

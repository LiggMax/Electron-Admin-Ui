package com.ligg.electronservice;

import com.ligg.electronservice.service.anime.AnimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class AnimeTest {

    @Autowired
    private AnimeService animeService;


    @Test
    public void searchAnime() {
        String keyword = "咒术回战";
        Map<String, Object> searchAnime = animeService.searchAnime(keyword);
        System.out.println(searchAnime);
    }

    @Test
    public void getPlayVideo() {
        String url = "https://www.bilibili.com/bangumi/play/ep10901";
        String playVideoUrl = animeService.getPlayVideoUrl(url);
        System.out.println(playVideoUrl);
    }
}

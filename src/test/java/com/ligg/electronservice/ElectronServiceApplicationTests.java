package com.ligg.electronservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URL;

@Slf4j
@SpringBootTest
class ElectronServiceApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
    }

    //    @Test
//    public void getToken() {
//        Map<String, Object> stringObjectMap = JWTUtil.parseTokenWithValidation
//                ("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGFpbXMiOnsidXNlcklkIjoiMTIzIiwidXNlcm5hbWUiOiJsaWdnIn0sImV4cCI6MTc0MjkwNjY2NX0.4yfPVZ_gpWY1OahrZ-nGuzgi1JqJQHimdvhjhLatnoU");
//        String userId = (String) stringObjectMap.get("userId");
//        String username = (String) stringObjectMap.get("username");
//        System.out.println(userId);
//        System.out.println(username);
//        String RedisToken = redisTemplate.opsForValue().get("Token:" + userId);
//        log.info("Token:{}" ,RedisToken);
//    }
    @Test
    public void request() {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(
                "https://api.bgm.tv/calendar",
                String.class
        );
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
        } catch (JsonProcessingException e) {
            log.error("序列化{}", e.getMessage());
        }
    }

    /**
     * 1.获取搜索内容页面
     * 2.获取详细内容页面
     * 3.解析详细内容页面获取每条线路和每条线路的聚集
     */
    @Test
    public void searchAnime() {
        //1.获取搜索内容页面

        // 构建请求URL和参数
        String xfdmUrl = "https://dm1.xfdm.pro";
        String baseUrl = "https://dm1.xfdm.pro/search.html?wd=";
        String searchTerm = "败犬女主"; // 这里可以替换为实际的搜索关键词
        
        // 设置HTTP请求参数
        int maxRetries = 3;                // 最大重试次数
        int connectionTimeout = 10000;     // 连接超时时间(毫秒)
        int readTimeout = 30000;           // 读取超时时间(毫秒)
        
        // 可选：设置代理 (如果需要)
        // System.setProperty("http.proxyHost", "127.0.0.1");
        // System.setProperty("http.proxyPort", "7890");
        // System.setProperty("https.proxyHost", "127.0.0.1");
        // System.setProperty("https.proxyPort", "7890");

        try {
            System.out.println("开始搜索动漫: " + searchTerm);
            
            // 使用Jsoup的connect方法代替parse(URL)，提供更多配置选项
            Document searchPage = null;
            Exception lastException = null;
            
            // 实现重试逻辑
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    System.out.println("尝试第" + attempt + "次请求搜索页面...");
                    searchPage = Jsoup.connect(baseUrl + searchTerm)
                            .timeout(connectionTimeout)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                            .header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                            .followRedirects(true)
                            .get();
                    
                    // 成功获取页面，跳出重试循环
                    lastException = null;
                    break;
                } catch (Exception e) {
                    lastException = e;
                    System.out.println("第" + attempt + "次请求失败: " + e.getMessage());
                    
                    if (attempt < maxRetries) {
                        // 重试前等待，时间随重试次数增加
                        int waitTime = 2000 * attempt;
                        System.out.println("等待" + waitTime + "毫秒后重试...");
                        Thread.sleep(waitTime);
                    }
                }
            }
            
            // 检查是否成功获取页面
            if (searchPage == null) {
                if (lastException != null) {
                    throw lastException;
                } else {
                    throw new RuntimeException("无法获取搜索页面，所有重试均失败");
                }
            }

            System.out.println("成功获取搜索页面，开始解析结果");
            Elements playButtons = searchPage.select("a.button.cr3");
            if (playButtons.isEmpty()) {
                System.out.println("没有找到播放按钮，可能搜索结果为空或页面结构已变化");
                return;
            }
            
            // 查找"播放正片"链接并获取详情页
            for (Element element : playButtons) {
                String href = element.attr("href");
                String text = element.text();
                
                // 筛选播放正片文本的链接
                if (text.contains("正片")) {
                    System.out.println("找到播放链接: " + href);
                    String detailUrl = xfdmUrl + href;
                    
                    // 获取并解析详情页面
                    Document detailPage = null;
                    
                    // 对详情页面的请求也实现重试
                    for (int attempt = 1; attempt <= maxRetries; attempt++) {
                        try {
                            System.out.println("尝试第" + attempt + "次请求详情页面: " + detailUrl);
                            detailPage = Jsoup.connect(detailUrl)
                                    .timeout(readTimeout)  // 详情页可能较大，使用更长的超时时间
                                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                                    .header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                                    .header("Referer", baseUrl + searchTerm) // 添加Referer头，模拟正常浏览行为
                                    .followRedirects(true)
                                    .get();
                            
                            // 成功获取详情页面
                            break;
                        } catch (Exception e) {
                            System.out.println("第" + attempt + "次获取详情页失败: " + e.getMessage());
                            
                            if (attempt < maxRetries) {
                                int waitTime = 3000 * attempt;
                                System.out.println("等待" + waitTime + "毫秒后重试...");
                                Thread.sleep(waitTime);
                            } else {
                                System.out.println("获取详情页面失败，已达最大重试次数");
                                throw e;
                            }
                        }
                    }
                    
                    if (detailPage == null) {
                        System.out.println("无法获取详情页面，跳过解析");
                        continue;
                    }
                    
                    // 解析详情页内容
                    Element body = detailPage.body();
                    
                    // 1. 提取基本信息
                    String title = body.select(".slide-info-title").text();
                    String summary = body.select("#height_limit").text();
                    
                    System.out.println("\n========== 动漫详情 ==========");
                    System.out.println("动漫标题: " + title);
                    System.out.println("简介: " + summary);
                    System.out.println("==============================\n");
                    
                    // 2. 获取所有线路和剧集信息
                    Elements routeTabs = body.select(".anthology-tab .swiper-wrapper a.swiper-slide");
                    Elements episodeLists = body.select(".anthology-list-box");
                    
                    System.out.println("找到" + routeTabs.size() + "条线路");
                    
                    // 遍历所有线路及其剧集
                    for (int i = 0; i < routeTabs.size(); i++) {
                        Element routeTab = routeTabs.get(i);
                        String routeName = routeTab.ownText().trim();
                        String episodeCount = routeTab.select(".badge").text();
                        
                        System.out.println("\n线路" + (i+1) + ": " + routeName + " (共" + episodeCount + "集)");
                        
                        // 获取对应线路的剧集列表
                        if (i < episodeLists.size()) {
                            Element episodeList = episodeLists.get(i);
                            Elements episodes = episodeList.select("ul.anthology-list-play li a");
                            
                            System.out.println("该线路包含" + episodes.size() + "个剧集");
                            
                            // 只输出前5个剧集避免日志过长，实际应用中可以处理所有剧集
                            int displayLimit = Math.min(5, episodes.size());
                            for (int j = 0; j < displayLimit; j++) {
                                Element episode = episodes.get(j);
                                String episodeName = episode.text();
                                String episodeUrl = episode.attr("href");
                                
                                // 确保URL是完整的
                                if (!episodeUrl.startsWith("http")) {
                                    episodeUrl = xfdmUrl + episodeUrl;
                                }
                                
                                System.out.println("  - " + episodeName + ": " + episodeUrl);
                            }
                            
                            if (episodes.size() > displayLimit) {
                                System.out.println("  ... 还有" + (episodes.size() - displayLimit) + "个剧集 ...");
                            }
                        }
                    }
                    
                    // 已找到并处理完一个结果，结束搜索
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("\n搜索过程发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

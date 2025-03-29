package com.ligg.electronservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ligg.electronservice.service.anime.AnimeService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
class ElectronServiceApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AnimeService animeService;

    @Test
    void search() {
        // 测试搜索功能，获取动漫信息和所有剧集
        String keyword = "败犬女主";
        Map<String, Object> animeInfo = animeService.searchAnime(keyword);
        
        // 返回结果已经包含了所有信息，并在控制台输出
        // 打印一下返回数据结构，不打印详细内容
        if (!animeInfo.isEmpty()) {
            System.out.println("\n========== 返回数据结构 ==========");
            animeInfo.keySet().forEach(key -> {
                Object value = animeInfo.get(key);
                if (value instanceof List) {
                    System.out.println(key + ": List[" + ((List<?>) value).size() + " 项]");
                } else if (value instanceof Map) {
                    System.out.println(key + ": Map[" + ((Map<?, ?>) value).size() + " 项]");
                } else if (value instanceof String) {
                    // 对于文本内容，只显示前20个字符，避免输出过长
                    String text = (String) value;
                    if (text.length() > 20) {
                        System.out.println(key + ": \"" + text.substring(0, 20) + "...\"");
                    } else {
                        System.out.println(key + ": \"" + text + "\"");
                    }
                } else {
                    System.out.println(key + ": " + value);
                }
            });
        }
    }


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

        // 构建请求URL和参数
        String xfdmUrl = "https://dm1.xfdm.pro";
        String baseUrl = "https://dm1.xfdm.pro/search.html?wd=";
        String searchTerm = "败犬女主"; // 这里可以替换为实际的搜索关键词
        
        // 设置HTTP请求参数
        int maxRetries = 3;                // 最大重试次数
        int connectionTimeout = 100000;    // 连接超时时间(毫秒)
        int readTimeout = 300000;          // 读取超时时间(毫秒)
        
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
                            
                            // 输出所有剧集
                            for (int j = 0; j < episodes.size(); j++) {
                                Element episode = episodes.get(j);
                                String episodeName = episode.text();
                                String episodeUrl = episode.attr("href");
                                
                                // 确保URL是完整的
                                if (!episodeUrl.startsWith("http")) {
                                    episodeUrl = xfdmUrl + episodeUrl;
                                }
                                
                                System.out.println("  - " + episodeName + ": " + episodeUrl);
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

    @Test
    public void getPlayVideo() {
        String url = "https://dm1.xfdm.pro/watch/2660/1/1.html";
        try {
            // 设置连接选项
            Connection connect = Jsoup.connect(url)
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                    .followRedirects(true);

            // 发送请求并获取页面
            Document document = connect.get();
            
            // 查找所有script标签
            Elements scriptElements = document.select("script");
            String videoUrl = null;
            
            System.out.println("开始解析视频播放地址...");
            
            // 遍历所有script标签，查找包含播放信息的脚本
            for (Element script : scriptElements) {
                String scriptContent = script.html();
                // 查找播放器配置的脚本，包含var player_aaaa的脚本
                if (scriptContent.contains("var player_aaaa")) {
                    System.out.println("找到播放器配置脚本");
                    
                    // 提取播放地址 - 方法1：使用正则表达式
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"url\":\"(.*?)\"");
                    java.util.regex.Matcher matcher = pattern.matcher(scriptContent);
                    if (matcher.find()) {
                        videoUrl = matcher.group(1);
                        // 处理转义字符
                        videoUrl = videoUrl.replace("\\/", "/");
                        System.out.println("通过正则表达式提取到的视频地址: " + videoUrl);
                    }
                    
                    // 方法2：提取JSON对象 (备用方法)
                    int startIndex = scriptContent.indexOf("var player_aaaa=") + "var player_aaaa=".length();
                    int endIndex = scriptContent.indexOf("</script>", startIndex);
                    if (endIndex == -1) {
                        endIndex = scriptContent.length();
                    }
                    
                    String jsonStr = scriptContent.substring(startIndex, endIndex).trim();
                    // 打印JSON字符串用于调试
                    System.out.println("提取的JSON字符串: " + jsonStr);
                    
                    try {
                        // 尝试解析JSON以获取更多信息
                        // 需要处理JSON字符串，移除可能的尾部分号
                        if (jsonStr.endsWith(";")) {
                            jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
                        }
                        
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(jsonStr);
                        
                        // 获取视频URL
                        String urlFromJson = rootNode.path("url").asText();
                        System.out.println("通过JSON解析提取到的视频地址: " + urlFromJson);
                        
                        // 获取下一集URL
                        String nextUrl = rootNode.path("url_next").asText();
                        System.out.println("下一集视频地址: " + nextUrl);
                        
                        // 获取视频信息
                        JsonNode vodData = rootNode.path("vod_data");
                        if (!vodData.isMissingNode()) {
                            String vodName = vodData.path("vod_name").asText();
                            System.out.println("视频名称: " + vodName);
                        }
                        
                        // 如果通过正则表达式没有获取到URL，则使用JSON解析的结果
                        if (videoUrl == null || videoUrl.isEmpty()) {
                            videoUrl = urlFromJson;
                        }
                    } catch (Exception e) {
                        System.out.println("JSON解析失败: " + e.getMessage());
                    }
                    
                    // 找到后退出循环
                    break;
                }
                
                // 还可以检查其他可能包含视频链接的脚本
                if (scriptContent.contains("iframeObj.contentWindow.postMessage")) {
                    System.out.println("找到iframe消息脚本");
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"next\":\"(.*?)\"");
                    java.util.regex.Matcher matcher = pattern.matcher(scriptContent);
                    if (matcher.find()) {
                        String nextUrlFromIframe = matcher.group(1);
                        nextUrlFromIframe = nextUrlFromIframe.replace("\\/", "/");
                        System.out.println("iframe中的下一集视频地址: " + nextUrlFromIframe);
                    }
                }
            }
            
            // 输出最终结果
            if (videoUrl != null && !videoUrl.isEmpty()) {
                System.out.println("\n========== 提取结果 ==========");
                System.out.println("最终提取到的视频播放地址: " + videoUrl);
                System.out.println("=============================");
                
                // 测试访问视频URL是否有效
                try {
                    Connection videoConnection = Jsoup.connect(videoUrl)
                            .timeout(5000)
                            .method(Connection.Method.HEAD)
                            .ignoreContentType(true);
                    Connection.Response videoResponse = videoConnection.execute();
                    int statusCode = videoResponse.statusCode();
                    System.out.println("视频URL状态码: " + statusCode);
                    if (statusCode == 200) {
                        System.out.println("视频链接有效，可以播放");
                    } else {
                        System.out.println("视频链接可能无效，状态码: " + statusCode);
                    }
                } catch (Exception e) {
                    System.out.println("测试视频链接时出错: " + e.getMessage());
                }
            } else {
                System.out.println("未能提取到视频播放地址");
            }

        } catch (Exception e) {
            log.error("获取播放地址发生错误: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testGetPlayVideo() {
        // 先获取一个动漫的信息
        Map<String, Object> animeInfo = animeService.searchAnime("斗罗大陆");
        
        // 检查是否有结果
        if (animeInfo.isEmpty()) {
            System.out.println("无法找到动漫信息，无法进行视频播放测试");
            return;
        }
        
        // 获取episodes信息
        Map<String, List<Map<String, String>>> allRoutes = (Map<String, List<Map<String, String>>>) animeInfo.get("episodes");
        if (allRoutes == null || allRoutes.isEmpty()) {
            System.out.println("没有找到剧集信息");
            return;
        }
        
        // 获取第一个路线的第一集
        String firstRouteName = allRoutes.keySet().iterator().next();
        List<Map<String, String>> episodes = allRoutes.get(firstRouteName);
        
        if (episodes == null || episodes.isEmpty()) {
            System.out.println("所选路线没有剧集");
            return;
        }
        
        Map<String, String> firstEpisode = episodes.get(0);
        String playUrl = firstEpisode.get("url");
        
        System.out.println("测试播放地址: " + playUrl);
        System.out.println("集数标题: " + firstEpisode.get("title"));
        
        // 获取视频播放链接
        String videoUrl = animeService.getPlayVideoUrl(playUrl);
        
        if (videoUrl != null && !videoUrl.isEmpty()) {
            System.out.println("成功获取视频地址: " + videoUrl);
            // 测试视频URL的格式是否符合预期
            if (videoUrl.startsWith("http") && (videoUrl.endsWith(".m3u8") || videoUrl.contains(".mp4"))) {
                System.out.println("视频地址格式正确，可以播放");
            } else {
                System.out.println("视频地址格式可能不正确，请检查: " + videoUrl);
            }
        } else {
            System.out.println("无法获取视频播放地址");
        }
    }
}

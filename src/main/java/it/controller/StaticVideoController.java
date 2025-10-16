package it.controller;

import it.pojo.StaticVideo;
import it.service.StaticVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/static")
@CrossOrigin(origins = "http://localhost:5173")
public class StaticVideoController {

    @Autowired
    private StaticVideoService staticVideoService;

    @GetMapping("/list")
    public List<Map<String, Object>> getAllStaticVideos() {
        List<StaticVideo> list = staticVideoService.getAllStaticVideos();
        List<Map<String, Object>> resp = new ArrayList<>();
        for (StaticVideo video : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", video.getId());
            map.put("title", video.getTitle());

            // 把本地路径转换成浏览器可访问的url
            String localPath = video.getCoverUrl();
            String fileName = localPath.substring(localPath.lastIndexOf("\\") + 1);
            String coverUrl = "http://localhost:8080/photo/" + fileName;

            map.put("coverUrl", coverUrl);
            map.put("duration", video.getDuration());
            resp.add(map);
        }
        return resp;
    }
}

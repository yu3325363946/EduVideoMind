package it.controller;

import it.pojo.Video;
import it.service.BilibiliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bilibili")
public class BilibiliController {

    @Autowired
    private BilibiliService bilibiliService;


    @PostMapping("/analyze")
    public Video analyze(@RequestParam("bvid") String bvid) throws Exception {
        System.out.println(">>> 接收到 BV 号: " + bvid);
        Video video = bilibiliService.analyzeFromBvid(bvid);
        System.out.println(">>> 分析完成，视频ID: " + video.getId());
        return video;
    }
}


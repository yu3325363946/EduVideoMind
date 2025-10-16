package it.controller;

import it.pojo.VideoSummary;
import it.service.VideoSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
public class VideoSummaryController {

    @Autowired
    private VideoSummaryService videoSummaryService;

    @PostMapping("/summary")
    public List<Map<String, Object>> generateSummary(@RequestParam Long videoId) {
        System.out.println(">>> POST 请求，生成视频章节总结，videoId=" + videoId);
        List<VideoSummary> summaries = videoSummaryService.generateAndGetSummary(videoId);
        System.out.println(">>> 生成完成，返回 " + summaries.size() + " 条章节总结");

        return summaries.stream().map(s -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("chapterTitle", s.getChapterTitle());
            map.put("summary", s.getSummary());
            return map;
        }).collect(Collectors.toList());
    }
}
package it.controller;

import it.pojo.Chapter;
import it.pojo.VideoSummary;
import it.service.ChapterService;
import it.service.VideoSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/chapter")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;
    private final VideoSummaryService videoSummaryService;

    /**
     * 保持原有的章节生成接口，不改逻辑，只是章节生成完后
     * 增加总结生成调用
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateChapters(
            @RequestParam("videoId") Long videoId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 章节生成
            List<Chapter> chapters = chapterService.generateChapters(videoId);

            // 组装章节返回给前端
            List<Map<String, Object>> processedChapters = new ArrayList<>();
            for (Chapter ch : chapters) {
                String summary = Optional.ofNullable(ch.getSummary())
                        .map(s -> s.replaceFirst("^章节标题[:：]\\s*", "")
                                .replaceAll("\\s*\\n\\s*", " ")
                                .trim())
                        .orElse("");
                String titleSummary = ch.getTitle().trim() + "  " + summary;

                Map<String, Object> m = new HashMap<>();
                m.put("titleSummary", titleSummary);
                m.put("startTime", ch.getStartTime());
                m.put("endTime", ch.getEndTime());
                m.put("displayTime", ch.getDisplayTime());
                processedChapters.add(m);
            }

            resp.put("code", 200);
            resp.put("chapters", processedChapters);
            try {
                List<VideoSummary> summaries = videoSummaryService.generateAndGetSummary(videoId);
                System.out.println(">>> 视频总结生成成功，条数：" + summaries.size());
            } catch (Exception e) {
                System.err.println(">>> 视频总结生成失败：" + e.getMessage());
            }
        } catch (Exception e) {
            resp.put("code", 500);
            resp.put("msg", "章节生成失败");
            resp.put("error", e.getMessage());
        }
        return ResponseEntity.ok(resp);
    }

    //获取视频总结的接口
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(@RequestParam("videoId") Long videoId) {
        // 用 LinkedHashMap 保证输出顺序
        Map<String, Object> resp = new LinkedHashMap<>();
        try {
            List<VideoSummary> list = videoSummaryService.getVideoSummary(videoId);

            // 构造内部的 List<Map>
            List<Map<String, Object>> processedSummaries = new ArrayList<>();
            for (VideoSummary s : list) {
                Map<String, Object> m = new HashMap<>();
                m.put("chapterTitle", s.getChapterTitle());
                m.put("summary",      s.getSummary());
                processedSummaries.add(m);
            }

            resp.put("code",    200);
            resp.put("summary", processedSummaries);
        } catch (Exception e) {
            resp.put("code",  500);
            resp.put("msg",   "查询摘要失败");
            resp.put("error", e.getMessage());
        }
        return ResponseEntity.ok(resp);
    }

}

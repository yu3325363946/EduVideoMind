package it.controller;

import it.mapper.VideoSummaryMapper;
import it.pojo.OutlineItem;
import it.pojo.VideoSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class OutlineController {

    private final VideoSummaryMapper videoSummaryMapper;

    @GetMapping("/outline")
    public Map<String, Object> getOutline(@RequestParam("videoId") Long videoId) {
        Map<String, Object> response = new HashMap<>();
        List<VideoSummary> summaries = videoSummaryMapper.getVideoSummary(videoId);

        if (summaries == null || summaries.isEmpty()) {
            response.put("code", 404);
            response.put("msg", "无可用摘要");
            return response;
        }

        // 构造大纲（一级标题）
        List<OutlineItem> outlineList = new ArrayList<>();
        for (VideoSummary s : summaries) {
            outlineList.add(new OutlineItem(1, s.getChapterTitle()));
        }

        // 构造表格
        List<Map<String, Object>> records = new ArrayList<>();
        for (int i = 0; i < summaries.size(); i++) {
            VideoSummary s = summaries.get(i);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("条目", "第" + (i + 1) + "条");
            row.put("标题", s.getChapterTitle());
            row.put("摘要内容", s.getSummary());
            records.add(row);
        }

        List<Map<String, Object>> columns = List.of(
                Map.of("field", "条目", "title", "条目", "width", "auto"),
                Map.of("field", "标题", "title", "标题"),
                Map.of("field", "摘要内容", "title", "摘要内容")
        );

        Map<String, Object> chartOption = new HashMap<>();
        chartOption.put("columns", columns);
        chartOption.put("records", records);
        chartOption.put("defaultColWidth", 160);
        chartOption.put("defaultHeaderColWidth", 120);
        chartOption.put("transpose", false);

        response.put("code", 200);
        response.put("outline", outlineList);
        response.put("chartOption", chartOption);

        return response;
    }


}

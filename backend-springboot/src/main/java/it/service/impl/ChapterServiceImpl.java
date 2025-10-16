package it.service.impl;

import com.alibaba.fastjson.JSONObject;
import it.mapper.ChapterMapper;
import it.mapper.SubtitleSegmentMapper;
import it.pojo.Chapter;
import it.pojo.SubtitleSegment;
import it.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final SubtitleSegmentMapper subtitleMapper;
    private final ChapterMapper chapterMapper;

    private static final String API_KEY = "sk-af18c52322c04684912d86db0fcb2fe2";
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    @Override
    public List<Chapter> generateChapters(Long videoId) throws Exception {
        System.out.println(">>> 开始生成章节，视频ID = " + videoId);

        List<SubtitleSegment> segments = subtitleMapper.selectByVideoId(videoId);
        if (segments == null || segments.isEmpty()) {
            System.out.println(">>> 无字幕段可供处理");
            return Collections.emptyList();
        }

        System.out.println(">>> 共获取到字幕段：" + segments.size() + " 个");

        // 整合所有字幕内容
        StringBuilder fullText = new StringBuilder();
        for (SubtitleSegment s : segments) {
            fullText.append(s.getContent()).append(" ");
        }

        Double totalDuration = segments.get(segments.size() - 1).getEndTime();
        int maxChapters = 6;
        double chapterDuration = Math.max(15.0, totalDuration / maxChapters);

        List<Chapter> chapters = new ArrayList<>();
        int chapterIndex = 1;
        double curStart = 0;

        while (curStart < totalDuration) {
            double curEnd = Math.min(curStart + chapterDuration, totalDuration);
            StringBuilder chapterText = new StringBuilder();
            for (SubtitleSegment s : segments) {
                if (s.getStartTime() >= curStart && s.getEndTime() <= curEnd) {
                    chapterText.append(s.getContent()).append(" ");
                }
            }

            String prompt = """
                你是一位专业的教学教研人员，请根据以下字幕内容划分出【合理的章节】。
                要求如下：
                1. 每章包括：章节标题（5~8字）+ 简洁专业的简介（8~10字）
                2. 标题必须使用准确、专业的教学术语
                3. 简介说明该部分主要内容
                字幕如下：
                %s
                请按照如下格式输出：
                章节标题：xxx
                简介：xxx
                """.formatted(chapterText.toString());

            String result = callAI(prompt);

            System.out.printf(">>> 正在处理第 %d 组字幕，时间段 %s - %s%n",
                    chapterIndex,
                    formatTime(curStart),
                    formatTime(curEnd));
            System.out.println(">>> AI 返回：" + result);

            String title = "未命名章节";
            String summary = "暂无";

            try {
                String[] parts = result.split("[\\n：:]");
                if (parts.length > 1) title = parts[1].trim();
                if (parts.length > 3) summary = parts[3].trim();
            } catch (Exception ignored) {
            }

            Chapter chapter = new Chapter();
            chapter.setVideoId(videoId);
            chapter.setChapterIndex(chapterIndex);
            chapter.setTitle(chapterIndex + ". " + title);
            chapter.setSummary(summary);
            chapter.setStartTime(curStart);
            chapter.setEndTime(curEnd);
            chapter.setDisplayTime(formatTime(curStart) + " - " + formatTime(curEnd));

            chapters.add(chapter);

            chapterIndex++;
            curStart = curEnd;
        }

        System.out.println(">>> 成功生成章节数：" + chapters.size());
        chapterMapper.insertBatch(chapters);
        System.out.println(">>> 章节入库完成");

        return chapters;
    }

    private String callAI(String prompt) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "qwen-turbo");
        body.put("input", Map.of("prompt", prompt));
        body.put("parameters", Map.of("result_format", "message"));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, request, String.class);

        JSONObject json = JSONObject.parseObject(response.getBody());
        JSONObject output = json.getJSONObject("output");

        if (output != null && output.containsKey("choices")) {
            JSONObject message = output.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message");
            return message.getString("content");
        } else {
            return "章节标题：未命名章节\n简介：暂无";
        }
    }

    private String formatTime(double seconds) {
        int min = (int) (seconds / 60);
        int sec = (int) (seconds % 60);
        return String.format("%d:%02d", min, sec);
    }
}

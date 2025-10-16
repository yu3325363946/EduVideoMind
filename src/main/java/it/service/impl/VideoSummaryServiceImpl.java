package it.service.impl;

import com.alibaba.fastjson.JSONObject;
import it.mapper.VideoSummaryMapper;
import it.pojo.Chapter;
import it.pojo.SubtitleSegment;
import it.pojo.VideoSummary;
import it.service.VideoSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VideoSummaryServiceImpl implements VideoSummaryService {

    private final VideoSummaryMapper videoSummaryMapper;

    private static final String API_KEY = "sk-af18c52322c04684912d86db0fcb2fe2";
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";


    @Override
    public List<VideoSummary> generateAndGetSummary(Long videoId) {
        System.out.println(">>> 开始生成视频总结，videoId = " + videoId);

        // 1. 读取已生成的章节和字幕数据
        List<Chapter> chapters = videoSummaryMapper.getChapters(videoId);
        List<SubtitleSegment> subtitles = videoSummaryMapper.getSubtitles(videoId);

        if (chapters == null || chapters.isEmpty()) {
            System.out.println(">>> 无章节数据，无法生成总结");
            return Collections.emptyList();
        }
        if (subtitles == null || subtitles.isEmpty()) {
            System.out.println(">>> 无字幕数据，无法生成总结");
            return Collections.emptyList();
        }

        // 2. 清空旧总结记录
        videoSummaryMapper.deleteSummaryByVideoId(videoId);

        List<VideoSummary> resultList = new ArrayList<>();

        // 3. 针对每个章节调用 AI 接口生成摘要
        for (Chapter chapter : chapters) {
            StringBuilder textBuilder = new StringBuilder();
            for (SubtitleSegment s : subtitles) {
                if (s.getStartTime() >= chapter.getStartTime()
                 && s.getEndTime()   <= chapter.getEndTime()) {
                    textBuilder.append(s.getContent()).append(" ");
                }
            }
            String prompt = buildPrompt(textBuilder.toString());

            String aiSummary;
            try {
                aiSummary = callModelApi(prompt);
                System.out.println(">>> AI 返回章节摘要：" + aiSummary);
            } catch (Exception e) {
                aiSummary = "本章节内容生成失败";
                System.err.println(">>> AI 调用失败：" + e.getMessage());
            }

            // 4. 构建实体并入库
            VideoSummary summary = new VideoSummary();
            summary.setVideoId(videoId);
            summary.setChapterIndex(chapter.getChapterIndex());
            summary.setChapterTitle(chapter.getTitle());
            summary.setSummary(aiSummary);
            summary.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            videoSummaryMapper.insertSummary(summary);
            resultList.add(summary);
        }

        return resultList;
    }

    @Override
    public List<VideoSummary> getVideoSummary(Long videoId) {
        return videoSummaryMapper.getVideoSummary(videoId);
    }

    private String buildPrompt(String subtitleText) {
        return """
你是一位专业的教学内容总结专家，负责为教学视频章节生成高质量、连贯且专业的章节摘要。

请根据以下字幕内容，生成摘要，要求如下：
- 不要输出“章节标题”、“本章讲了”这类引导语；
- 语言连贯专业、准确真实，不进行推测；
- 避免项目编号、标点结构语；
- 不要输出章节编号，只输出摘要内容；
- 控制在150字以内。

字幕内容如下：
%s
""".formatted(subtitleText == null ? "" : subtitleText.trim());
    }

    private String callModelApi(String prompt) throws Exception {
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
            return "本章节暂无有效内容";
        }
    }
}
